package uy.edu.ort.battlefield;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import uy.edu.ort.battleshipdemo.aplicacion.Fachada;
import uy.edu.ort.battleshipdemo.aplicacion.datos.BatallaDTO;
import uy.edu.ort.battleshipdemo.infraestructura.inicio.PrecargaDatos;
import uy.edu.ort.battleshipdemo.modelo.batalla.Batalla;
import uy.edu.ort.battleshipdemo.modelo.batalla.SolicitudNuevaBatalla;
import uy.edu.ort.battleshipdemo.modelo.campana.SistemaBatallas;
import uy.edu.ort.battleshipdemo.modelo.catalogo.SistemaConfiguracion;
import uy.edu.ort.battleshipdemo.modelo.catalogo.TipoBarco;
import uy.edu.ort.battleshipdemo.modelo.excepciones.DensidadInvalidaException;
import uy.edu.ort.battleshipdemo.modelo.excepciones.LugarDuplicadoException;
import uy.edu.ort.battleshipdemo.modelo.tacticas.TacticaSecuencial;
import uy.edu.ort.battleshipdemo.modelo.valores.EstadoBatalla;
import uy.edu.ort.battleshipdemo.modelo.valores.TipoEvento;

class CreacionBatallaTests {
    @Test
    void laPrecargaRegistraLosCincoBarcosYLasTresTacticasMedianteLaFachada() {
        Fachada fachada = Fachada.getInstancia();
        new PrecargaDatos().cargarCatalogo(fachada);

        assertEquals(5, fachada.obtenerOpciones().getTipos().size());
        assertEquals(3, fachada.obtenerOpciones().getTacticas().size());
    }

    @Test
    void creaLaBatallaCompletaYLaDejaDisponibleParaPreparacion() {
        SistemaBatallas campana = new SistemaBatallas();
        SistemaConfiguracion catalogo = catalogo();
        List<Object> eventos = new ArrayList<>();
        campana.agregarObservador((evento, origen) -> eventos.add(evento));

        Batalla batalla = campana.crearBatalla(
                new SolicitudNuevaBatalla("Atlántico", 6, 8, "secuencial",
                        Map.of("acorazado", 1, "destructor", 2)),
                catalogo);

        assertEquals(EstadoBatalla.EN_PREPARACION, batalla.getEstado());
        assertEquals("Secuencial", batalla.getNombreTactica());
        assertSame(batalla, campana.buscarBatalla(batalla.getId()));
        assertNotSame(batalla.getHumano().getOceano(), batalla.getMaquina().getOceano());
        assertEquals(48, batalla.getHumano().getOceano().getCeldas().size());
        assertEquals(3, batalla.getHumano().barcosPendientes().size());
        assertEquals(3, batalla.getMaquina().barcosPendientes().size());
        assertNotSame(batalla.getHumano().barcosPendientes().get(0),
                batalla.getMaquina().barcosPendientes().get(0));
        assertEquals(1, eventos.size());
        assertEquals(TipoEvento.BATALLA_CREADA, eventos.get(0));

        BatallaDTO dto = new BatallaDTO(batalla);
        assertEquals(48, dto.getPropias().size());
        assertEquals(48, dto.getEnemigas().size());
        assertEquals(List.of(4, 1, 1), dto.getBarcosPendientes().stream().map(b -> b.getTamano()).toList());
        assertTrue(dto.getPropias().stream().allMatch(c -> c.getValor().isEmpty()));
        assertTrue(dto.getEnemigas().stream().allMatch(c -> c.getValor().isEmpty()));
    }

    @Test
    void generaUnLugarDeSeisMayusculasCuandoNoSeIndicaUno() {
        SistemaBatallas campana = new SistemaBatallas();
        Batalla batalla = campana.crearBatalla(
                new SolicitudNuevaBatalla("  ", 6, 6, "secuencial", Map.of("destructor", 1)),
                catalogo());

        assertTrue(batalla.getLugar().matches("[A-Z]{6}"));
    }

    @Test
    void unaCreacionInvalidaNoDejaEfectosParciales() {
        SistemaBatallas campana = new SistemaBatallas();
        List<Object> eventos = new ArrayList<>();
        campana.agregarObservador((evento, origen) -> eventos.add(evento));

        assertThrows(DensidadInvalidaException.class, () -> campana.crearBatalla(
                new SolicitudNuevaBatalla("Inválida", 6, 6, "secuencial", Map.of("acorazado", 3)),
                catalogo()));

        assertTrue(campana.listarBatallas().isEmpty());
        assertTrue(eventos.isEmpty());
    }

    @Test
    void rechazaElLugarDuplicadoSinAlterarLaCampana() {
        SistemaBatallas campana = new SistemaBatallas();
        SistemaConfiguracion catalogo = catalogo();
        SolicitudNuevaBatalla primera = new SolicitudNuevaBatalla(
                "Pacífico", 6, 6, "secuencial", Map.of("destructor", 1));
        campana.crearBatalla(primera, catalogo);

        LugarDuplicadoException error = assertThrows(LugarDuplicadoException.class, () -> campana.crearBatalla(
                new SolicitudNuevaBatalla("PACÍFICO", 8, 8, "secuencial", Map.of("destructor", 1)),
                catalogo));
        assertEquals("No se puede librar más de una batalla en el mismo lugar, cambie de nombre",
                error.getMessage());
        assertEquals(1, campana.listarBatallas().size());
    }

    private SistemaConfiguracion catalogo() {
        SistemaConfiguracion catalogo = new SistemaConfiguracion();
        catalogo.registrarTipo(new TipoBarco("acorazado", "Acorazado", 4));
        catalogo.registrarTipo(new TipoBarco("destructor", "Destructor", 1));
        catalogo.registrarTactica("secuencial", "Secuencial");
        return catalogo;
    }
}
