package uy.edu.ort.battlefield;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import uy.edu.ort.battleshipdemo.aplicacion.datos.BatallaDTO;
import uy.edu.ort.battleshipdemo.modelo.batalla.Batalla;
import uy.edu.ort.battleshipdemo.modelo.batalla.ConfiguracionBatalla;
import uy.edu.ort.battleshipdemo.modelo.batalla.Disparo;
import uy.edu.ort.battleshipdemo.modelo.batalla.ItemFlota;
import uy.edu.ort.battleshipdemo.modelo.batalla.Participante;
import uy.edu.ort.battleshipdemo.modelo.catalogo.TipoBarco;
import uy.edu.ort.battleshipdemo.modelo.excepciones.CeldaYaDisparadaException;
import uy.edu.ort.battleshipdemo.modelo.excepciones.SolicitudDesactualizadaException;
import uy.edu.ort.battleshipdemo.modelo.flota.Barco;
import uy.edu.ort.battleshipdemo.modelo.flota.Flota;
import uy.edu.ort.battleshipdemo.modelo.oceano.Coordenada;
import uy.edu.ort.battleshipdemo.modelo.oceano.DistribuidorAleatorio;
import uy.edu.ort.battleshipdemo.modelo.oceano.Oceano;
import uy.edu.ort.battleshipdemo.modelo.tacticas.ContextoTactico;
import uy.edu.ort.battleshipdemo.modelo.tacticas.Tactica;
import uy.edu.ort.battleshipdemo.modelo.valores.Bando;
import uy.edu.ort.battleshipdemo.modelo.valores.EstadoBatalla;
import uy.edu.ort.battleshipdemo.modelo.valores.Orientacion;

class RondasTests {
    @Test
    void completaLosDosDisparosYAvanzaLaRonda() {
        TipoBarco submarino = new TipoBarco("submarino", "Submarino", 2);
        Partida partida = preparar(List.of(submarino), new TacticaFija(new Coordenada(0, 0)));
        Coordenada aguaEnemiga = buscarAgua(partida.maquina(), partida.configuracion());

        partida.batalla().jugarRonda(aguaEnemiga, 1, partida.batalla().getVersion());

        BatallaDTO dto = new BatallaDTO(partida.batalla());
        assertEquals(EstadoBatalla.EN_COMBATE, dto.getEstado());
        assertEquals(2, dto.getNumeroRonda());
        assertEquals("agua", valor(dto.getEnemigas(), aguaEnemiga));
        assertEquals("impacto", valor(dto.getPropias(), new Coordenada(0, 0)));
        assertEquals(1, partida.batalla().getHumano().integridad());
    }

    @Test
    void registraUnaBajaEnemigaYPermiteLaRespuesta() {
        TipoBarco submarino = new TipoBarco("submarino", "Submarino", 2);
        TipoBarco destructor = new TipoBarco("destructor", "Destructor", 1);
        Partida partida = preparar(List.of(submarino, destructor),
                new TacticaFija(new Coordenada(0, 0)));
        Coordenada destructorEnemigo = partida.maquina().get(1).getCeldas().get(0).getPosicion();

        partida.batalla().jugarRonda(destructorEnemigo, 1, partida.batalla().getVersion());

        assertEquals(EstadoBatalla.EN_COMBATE, partida.batalla().getEstado());
        assertEquals(List.of("{1}"), partida.batalla().bajasEnemigas());
        assertTrue(partida.batalla().getHumano().getOceano().celda(new Coordenada(0, 0)).estaDisparada());
    }

    @Test
    void laVictoriaHumanaOmiteElDisparoDeLaMaquina() {
        TipoBarco destructor = new TipoBarco("destructor", "Destructor", 1);
        AtomicInteger elecciones = new AtomicInteger();
        Partida partida = preparar(List.of(destructor),
                new TacticaFija(new Coordenada(0, 0), elecciones));
        Coordenada objetivo = partida.maquina().get(0).getCeldas().get(0).getPosicion();

        partida.batalla().jugarRonda(objetivo, 1, partida.batalla().getVersion());

        assertEquals(EstadoBatalla.GANADA, partida.batalla().getEstado());
        assertEquals(0, elecciones.get());
        assertEquals(List.of("{1}"), partida.batalla().bajasEnemigas());
        assertEquals(1, partida.batalla().numeroRonda());
        assertTrue(partida.batalla().getHumano().getOceano().getCeldas().stream()
                .noneMatch(celda -> celda.estaDisparada()));
    }

    @Test
    void elDisparoDeLaMaquinaPuedeFinalizarLaBatallaComoPerdida() {
        TipoBarco destructor = new TipoBarco("destructor", "Destructor", 1);
        Partida partida = preparar(List.of(destructor), new TacticaFija(new Coordenada(0, 0)));
        Coordenada aguaEnemiga = buscarAgua(partida.maquina(), partida.configuracion());

        partida.batalla().jugarRonda(aguaEnemiga, 1, partida.batalla().getVersion());

        BatallaDTO dto = new BatallaDTO(partida.batalla());
        assertEquals(EstadoBatalla.PERDIDA, dto.getEstado());
        assertEquals("impacto", valor(dto.getPropias(), new Coordenada(0, 0)));
        assertTrue(dto.getEnemigas().stream().anyMatch(celda -> celda.getValor().equals("barco")));
    }

    @Test
    void unaCeldaRepetidaOUnaRondaAtrasadaNoProducenEfectosParciales() {
        TipoBarco submarino = new TipoBarco("submarino", "Submarino", 2);
        Partida partida = preparar(List.of(submarino), new TacticaFija(new Coordenada(0, 0)));
        Coordenada aguaEnemiga = buscarAgua(partida.maquina(), partida.configuracion());
        List<Object> eventos = new ArrayList<>();
        partida.batalla().agregarObservador((evento, origen) -> eventos.add(evento));
        partida.batalla().jugarRonda(aguaEnemiga, 1, partida.batalla().getVersion());
        long versionConfirmada = partida.batalla().getVersion();
        int integridadConfirmada = partida.batalla().getHumano().integridad();

        assertThrows(CeldaYaDisparadaException.class,
                () -> partida.batalla().jugarRonda(aguaEnemiga, 2, versionConfirmada));
        assertThrows(SolicitudDesactualizadaException.class,
                () -> partida.batalla().jugarRonda(new Coordenada(5, 5), 1, versionConfirmada));
        assertEquals(versionConfirmada, partida.batalla().getVersion());
        assertEquals(integridadConfirmada, partida.batalla().getHumano().integridad());
        assertEquals(1, eventos.size());
    }

    @Test
    void elPrimerDisparoRegistradoTienePrecedenciaEnLaRonda() throws Exception {
        CountDownLatch primeroRegistrado = new CountDownLatch(1);
        CountDownLatch completarPrimero = new CountDownLatch(1);
        TipoBarco submarino = new TipoBarco("submarino", "Submarino", 2);
        Partida partida = preparar(List.of(submarino),
                new TacticaBloqueante(primeroRegistrado, completarPrimero));
        Coordenada primerDestino = buscarAgua(partida.maquina(), partida.configuracion());
        Coordenada segundoDestino = new Coordenada(5, 5);
        long version = partida.batalla().getVersion();
        ExecutorService ejecutor = Executors.newFixedThreadPool(2);
        try {
            Future<?> primero = ejecutor.submit(() ->
                    partida.batalla().jugarRonda(primerDestino, 1, version));
            assertTrue(primeroRegistrado.await(5, TimeUnit.SECONDS));
            Future<?> segundo = ejecutor.submit(() ->
                    partida.batalla().jugarRonda(segundoDestino, 1, version));
            completarPrimero.countDown();

            primero.get(5, TimeUnit.SECONDS);
            ExecutionException error = assertThrows(ExecutionException.class,
                    () -> segundo.get(5, TimeUnit.SECONDS));
            assertInstanceOf(SolicitudDesactualizadaException.class, error.getCause());
            assertTrue(partida.batalla().getMaquina().getOceano().celda(primerDestino).estaDisparada());
            if (!primerDestino.equals(segundoDestino)) {
                assertFalse(partida.batalla().getMaquina().getOceano().celda(segundoDestino).estaDisparada());
            }
        } finally {
            completarPrimero.countDown();
            ejecutor.shutdownNow();
        }
    }

    private Partida preparar(List<TipoBarco> tipos, Tactica tactica) {
        ConfiguracionBatalla configuracion = ConfiguracionBatalla.crear(6, 6,
                tipos.stream().map(tipo -> new ItemFlota(tipo, 1)).toList());
        List<Barco> barcosHumanos = new ArrayList<>();
        List<Barco> barcosMaquina = new ArrayList<>();
        int siguienteId = 1;
        for (TipoBarco tipo : tipos) {
            barcosHumanos.add(new Barco(siguienteId, tipo));
            barcosMaquina.add(new Barco(siguienteId, tipo));
            siguienteId++;
        }
        Participante humano = new Participante(Bando.HUMANO, new Flota(barcosHumanos), new Oceano(6, 6));
        Participante maquina = new Participante(Bando.MAQUINA, new Flota(barcosMaquina), new Oceano(6, 6));
        Batalla batalla = new Batalla(1, "Rondas", configuracion, humano, maquina, tactica);
        for (int i = 0; i < barcosHumanos.size(); i++) {
            Coordenada proa = i == 0 ? new Coordenada(0, 0) : new Coordenada(3, 3);
            batalla.ubicarBarco(barcosHumanos.get(i).getId(), proa, Orientacion.HORIZONTAL,
                    batalla.getVersion());
        }
        batalla.iniciarCombate(new DistribuidorAleatorio(21), batalla.getVersion());
        return new Partida(batalla, barcosMaquina, configuracion);
    }

    private Coordenada buscarAgua(List<Barco> barcos, ConfiguracionBatalla configuracion) {
        for (int fila = 0; fila < configuracion.getFilas(); fila++) {
            for (int columna = 0; columna < configuracion.getColumnas(); columna++) {
                Coordenada candidata = new Coordenada(fila, columna);
                boolean ocupada = barcos.stream().flatMap(barco -> barco.getCeldas().stream())
                        .anyMatch(celda -> celda.getPosicion().equals(candidata));
                if (!ocupada) return candidata;
            }
        }
        throw new IllegalStateException("No se encontró una celda de agua");
    }

    private String valor(List<uy.edu.ort.battleshipdemo.aplicacion.datos.CeldaDTO> celdas,
            Coordenada coordenada) {
        return celdas.stream().filter(celda -> celda.getFila() == coordenada.getFila()
                && celda.getColumna() == coordenada.getColumna()).findFirst().orElseThrow().getValor();
    }

    private static class Partida {
        private final Batalla batalla;
        private final List<Barco> maquina;
        private final ConfiguracionBatalla configuracion;
        Partida(Batalla batalla, List<Barco> maquina, ConfiguracionBatalla configuracion) {
            this.batalla = batalla; this.maquina = maquina; this.configuracion = configuracion;
        }
        Batalla batalla() { return batalla; }
        List<Barco> maquina() { return maquina; }
        ConfiguracionBatalla configuracion() { return configuracion; }
    }

    private static class TacticaFija extends Tactica {
        private final Coordenada destino;
        private final AtomicInteger elecciones;
        TacticaFija(Coordenada destino) { this(destino, new AtomicInteger()); }
        TacticaFija(Coordenada destino, AtomicInteger elecciones) {
            super("Fija");
            this.destino = destino;
            this.elecciones = elecciones;
        }
        public Coordenada elegirDisparo(ContextoTactico contexto) {
            elecciones.incrementAndGet();
            return destino;
        }
        public void registrarResultado(Disparo disparo) { }
        public Tactica copiar() { return new TacticaFija(destino, elecciones); }
    }

    private static class TacticaBloqueante extends Tactica {
        private final CountDownLatch registrado;
        private final CountDownLatch continuar;
        TacticaBloqueante(CountDownLatch registrado, CountDownLatch continuar) {
            super("Bloqueante");
            this.registrado = registrado;
            this.continuar = continuar;
        }
        public Coordenada elegirDisparo(ContextoTactico contexto) {
            registrado.countDown();
            try {
                if (!continuar.await(5, TimeUnit.SECONDS))
                    throw new IllegalStateException("El primer disparo no continuó");
            } catch (InterruptedException excepcion) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("El primer disparo fue interrumpido", excepcion);
            }
            return new Coordenada(5, 5);
        }
        public void registrarResultado(Disparo disparo) { }
        public Tactica copiar() { return new TacticaBloqueante(registrado, continuar); }
    }
}
