package uy.edu.ort.battlefield;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import uy.edu.ort.battleshipdemo.aplicacion.Fachada;
import uy.edu.ort.battleshipdemo.modelo.batalla.ConfiguracionBatalla;
import uy.edu.ort.battleshipdemo.modelo.batalla.ItemFlota;
import uy.edu.ort.battleshipdemo.modelo.catalogo.TipoBarco;
import uy.edu.ort.battleshipdemo.modelo.eventos.Observable;
import uy.edu.ort.battleshipdemo.modelo.eventos.Observador;
import uy.edu.ort.battleshipdemo.modelo.excepciones.CeldaYaDisparadaException;
import uy.edu.ort.battleshipdemo.modelo.excepciones.DimensionInvalidaException;
import uy.edu.ort.battleshipdemo.modelo.excepciones.FlotaVaciaException;
import uy.edu.ort.battleshipdemo.modelo.oceano.Coordenada;
import uy.edu.ort.battleshipdemo.modelo.oceano.Oceano;
import uy.edu.ort.battleshipdemo.modelo.valores.TipoEvento;

class EstructuraDiagramaTests {
    @Test void fachadaEsSingleton() { assertSame(Fachada.getInstancia(), Fachada.getInstancia()); }
    @Test void coordenadaEsValorYConsideraDiagonales() {
        Coordenada origen = new Coordenada(2, 3);
        assertEquals(new Coordenada(3, 2), origen.desplazar(1, -1));
        assertTrue(origen.esAdyacenteA(new Coordenada(3, 4)));
        assertFalse(origen.esAdyacenteA(origen));
    }
    @Test void configuracionCalculaDensidad() {
        ConfiguracionBatalla c = ConfiguracionBatalla.crear(10, 10, List.of(new ItemFlota(new TipoBarco("d", "Destructor", 1), 2)));
        assertEquals(2, c.totalCeldasFlota()); assertEquals(0.02, c.densidad());
    }
    @Test void configuracionIdentificaElProblemaConcreto() {
        assertThrows(DimensionInvalidaException.class,
                () -> ConfiguracionBatalla.crear(5, 10, List.of()));
        assertThrows(FlotaVaciaException.class,
                () -> ConfiguracionBatalla.crear(10, 10, List.of()));
    }
    @Test void disparoRepetidoUsaSuExcepcionPropia() {
        Oceano oceano = new Oceano(6, 6);
        Coordenada destino = new Coordenada(2, 2);
        oceano.recibirDisparo(destino);
        assertThrows(CeldaYaDisparadaException.class, () -> oceano.recibirDisparo(destino));
    }
    @Test void suscripcionSeCancelaUnaVez() {
        class Fuente extends Observable { void emitir() { notificar(TipoEvento.CAMPANA_MODIFICADA); } }
        Fuente fuente = new Fuente(); int[] recibidos = {0};
        Observador observador = (evento, origen) -> recibidos[0]++;
        fuente.agregarObservador(observador); fuente.emitir();
        fuente.quitarObservador(observador); fuente.quitarObservador(observador); fuente.emitir();
        assertEquals(1, recibidos[0]);
    }
}
