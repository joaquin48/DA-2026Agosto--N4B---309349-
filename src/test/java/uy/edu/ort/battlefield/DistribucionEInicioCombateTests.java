package uy.edu.ort.battlefield;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import uy.edu.ort.battleshipdemo.aplicacion.datos.BatallaDTO;
import uy.edu.ort.battleshipdemo.modelo.batalla.Batalla;
import uy.edu.ort.battleshipdemo.modelo.batalla.SolicitudNuevaBatalla;
import uy.edu.ort.battleshipdemo.modelo.campana.SistemaBatallas;
import uy.edu.ort.battleshipdemo.modelo.catalogo.SistemaConfiguracion;
import uy.edu.ort.battleshipdemo.modelo.catalogo.TipoBarco;
import uy.edu.ort.battleshipdemo.modelo.excepciones.DistribucionFlotaException;
import uy.edu.ort.battleshipdemo.modelo.excepciones.FlotaIncompletaException;
import uy.edu.ort.battleshipdemo.modelo.excepciones.SolicitudDesactualizadaException;
import uy.edu.ort.battleshipdemo.modelo.flota.Barco;
import uy.edu.ort.battleshipdemo.modelo.flota.Flota;
import uy.edu.ort.battleshipdemo.modelo.oceano.Coordenada;
import uy.edu.ort.battleshipdemo.modelo.oceano.DistribuidorAleatorio;
import uy.edu.ort.battleshipdemo.modelo.oceano.Oceano;
import uy.edu.ort.battleshipdemo.modelo.oceano.UbicacionBarco;
import uy.edu.ort.battleshipdemo.modelo.tacticas.TacticaSecuencial;
import uy.edu.ort.battleshipdemo.modelo.valores.EstadoBatalla;
import uy.edu.ort.battleshipdemo.modelo.valores.Orientacion;
import uy.edu.ort.battleshipdemo.modelo.valores.TipoEvento;

class DistribucionEInicioCombateTests {
    @Test
    void distribuyeTodaLaFlotaSinSuperposicionNiAdyacencias() {
        List<Barco> barcos = List.of(
                barco(1, "crucero", 3), barco(2, "submarino", 2), barco(3, "destructor", 1));
        Flota flota = new Flota(barcos);
        Oceano oceano = new Oceano(6, 8);

        List<UbicacionBarco> plan = new DistribuidorAleatorio(17).planificar(oceano, flota);
        assertFalse(flota.estaPosicionada());
        assertTrue(oceano.getCeldas().stream().noneMatch(c -> c.estaOcupada()));
        oceano.aplicarDistribucion(plan, flota);

        assertTrue(flota.estaPosicionada());
        assertEquals(6, oceano.getCeldas().stream().filter(c -> c.estaOcupada()).count());
        for (int i = 0; i < barcos.size(); i++) {
            for (int j = i + 1; j < barcos.size(); j++) {
                Barco primero = barcos.get(i);
                Barco segundo = barcos.get(j);
                assertTrue(primero.getCeldas().stream().noneMatch(celdaPrimero ->
                        segundo.getCeldas().stream().anyMatch(celdaSegundo ->
                                celdaPrimero.getPosicion().equals(celdaSegundo.getPosicion())
                                || celdaPrimero.getPosicion().esAdyacenteA(celdaSegundo.getPosicion()))));
            }
        }
    }

    @Test
    void rechazaUnPlanInvalidoSinUbicarNingunBarco() {
        Barco crucero = barco(11, "crucero", 3);
        Barco destructor = barco(12, "destructor", 1);
        Flota flota = new Flota(List.of(crucero, destructor));
        Oceano oceano = new Oceano(6, 6);
        List<UbicacionBarco> plan = List.of(
                new UbicacionBarco(crucero.getId(), new Coordenada(0, 0), Orientacion.HORIZONTAL),
                new UbicacionBarco(destructor.getId(), new Coordenada(0, 3), Orientacion.HORIZONTAL));

        assertThrows(DistribucionFlotaException.class, () -> oceano.aplicarDistribucion(plan, flota));
        assertFalse(crucero.estaPosicionado());
        assertFalse(destructor.estaPosicionado());
        assertTrue(oceano.getCeldas().stream().noneMatch(c -> c.estaOcupada()));
    }

    @Test
    void exigeLaFlotaHumanaCompletaAntesDeIniciar() {
        SistemaBatallas campana = new SistemaBatallas();
        Batalla batalla = crearBatalla(campana);

        FlotaIncompletaException error = assertThrows(FlotaIncompletaException.class,
                () -> batalla.iniciarCombate(new DistribuidorAleatorio(5), 0));

        assertEquals("Debe posicionar todos sus barcos primero", error.getMessage());
        assertEquals(EstadoBatalla.EN_PREPARACION, batalla.getEstado());
        assertEquals(0, batalla.getVersion());
        assertFalse(batalla.getMaquina().flotaCompletaPosicionada());
    }

    @Test
    void iniciaUnaSolaVezDistribuyeAlEnemigoYNotificaALaCampana() {
        SistemaBatallas campana = new SistemaBatallas();
        Batalla batalla = crearBatalla(campana);
        List<Barco> pendientes = batalla.barcosPendientes();
        batalla.ubicarBarco(pendientes.get(0).getId(), new Coordenada(0, 0), Orientacion.HORIZONTAL, 0);
        batalla.ubicarBarco(pendientes.get(1).getId(), new Coordenada(3, 3), Orientacion.HORIZONTAL, 1);
        List<Object> eventosCampana = new ArrayList<>();
        campana.agregarObservador((evento, origen) -> eventosCampana.add(evento));

        batalla.iniciarCombate(new DistribuidorAleatorio(9), 2);

        assertEquals(EstadoBatalla.EN_COMBATE, batalla.getEstado());
        assertEquals(3, batalla.getVersion());
        assertTrue(batalla.getMaquina().flotaCompletaPosicionada());
        assertEquals(1, eventosCampana.size());
        assertEquals(TipoEvento.CAMPANA_MODIFICADA, eventosCampana.get(0));
        BatallaDTO dto = new BatallaDTO(batalla);
        assertEquals(1, dto.getNumeroRonda());
        assertEquals(3, dto.getPropias().stream().filter(c -> c.getValor().equals("barco")).count());
        assertTrue(dto.getEnemigas().stream().allMatch(c -> c.getValor().isEmpty()));
        assertThrows(SolicitudDesactualizadaException.class,
                () -> batalla.iniciarCombate(new DistribuidorAleatorio(9), 2));
    }

    private Batalla crearBatalla(SistemaBatallas campana) {
        SistemaConfiguracion catalogo = new SistemaConfiguracion();
        catalogo.registrarTipo(new TipoBarco("submarino", "Submarino", 2));
        catalogo.registrarTipo(new TipoBarco("destructor", "Destructor", 1));
        catalogo.registrarTactica("secuencial", "Secuencial");
        return campana.crearBatalla(new SolicitudNuevaBatalla(
                "Prueba", 6, 6, "secuencial", Map.of("submarino", 1, "destructor", 1)), catalogo);
    }

    private Barco barco(int id, String nombre, int tamano) {
        return new Barco(id, new TipoBarco(nombre, nombre, tamano));
    }
}
