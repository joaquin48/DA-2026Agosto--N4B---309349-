package uy.edu.ort.battlefield;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import uy.edu.ort.battleshipdemo.modelo.batalla.Disparo;
import uy.edu.ort.battleshipdemo.modelo.oceano.Coordenada;
import uy.edu.ort.battleshipdemo.modelo.tacticas.ContextoTactico;
import uy.edu.ort.battleshipdemo.modelo.tacticas.Tactica;
import uy.edu.ort.battleshipdemo.modelo.tacticas.TacticaEspiral;
import uy.edu.ort.battleshipdemo.modelo.valores.Bando;
import uy.edu.ort.battleshipdemo.modelo.valores.ResultadoDisparo;

class TacticaEspiralTests {
    private final TacticaEspiral tactica = new TacticaEspiral();

    @Test void comienzaEnElCentroInferiorIzquierdo() {
        assertEquals(new Coordenada(2, 2), tactica.elegirDisparo(contexto(6, 6, List.of())));
    }

    @Test void sigueLaEspiralDerechaArribaIzquierdaAbajo() {
        List<Coordenada> esperadas = List.of(
                new Coordenada(2, 2), new Coordenada(2, 3), new Coordenada(3, 3),
                new Coordenada(3, 2), new Coordenada(3, 1), new Coordenada(2, 1),
                new Coordenada(1, 1), new Coordenada(1, 2), new Coordenada(1, 3));
        List<Disparo> disparos = new ArrayList<>();

        for (Coordenada esperada : esperadas) {
            Coordenada elegida = tactica.elegirDisparo(contexto(5, 5, disparos));
            assertEquals(esperada, elegida);
            disparos.add(disparo(elegida));
        }
    }

    @Test void cubreTablerosRectangularesSinRepetirCeldas() {
        verificarCoberturaCompleta(3, 7);
        verificarCoberturaCompleta(7, 3);
    }

    @Test void omiteCeldasDelRecorridoQueYaFueronAtacadas() {
        List<Disparo> disparos = List.of(
                disparo(new Coordenada(2, 2)), disparo(new Coordenada(2, 3)));

        assertEquals(new Coordenada(3, 3), tactica.elegirDisparo(contexto(5, 5, disparos)));
    }

    @Test void recalculaElRecorridoSiCambianLasDimensiones() {
        assertEquals(new Coordenada(1, 3), tactica.elegirDisparo(contexto(3, 7, List.of())));
        assertEquals(new Coordenada(3, 1), tactica.elegirDisparo(contexto(7, 3, List.of())));
    }

    @Test void fallaExplicitamenteCuandoElTableroEstaAgotado() {
        List<Disparo> disparos = List.of(disparo(new Coordenada(0, 0)));

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> tactica.elegirDisparo(contexto(1, 1, disparos)));

        assertEquals("No quedan celdas disponibles para disparar", error.getMessage());
    }

    @Test void copiarDevuelveUnaInstanciaIndependienteSinEstado() {
        tactica.elegirDisparo(contexto(5, 5, List.of()));
        Tactica copia = tactica.copiar();

        assertNotSame(tactica, copia);
        assertEquals(new Coordenada(2, 2), copia.elegirDisparo(contexto(5, 5, List.of())));
    }

    private void verificarCoberturaCompleta(int filas, int columnas) {
        TacticaEspiral espiral = new TacticaEspiral();
        List<Disparo> disparos = new ArrayList<>();

        for (int i = 0; i < filas * columnas; i++) {
            Coordenada elegida = espiral.elegirDisparo(contexto(filas, columnas, disparos));
            disparos.add(disparo(elegida));
        }

        assertEquals(filas * columnas, disparos.stream().map(Disparo::getDestino).distinct().count());
    }

    private ContextoTactico contexto(int filas, int columnas, List<Disparo> disparos) {
        return new ContextoTactico(filas, columnas, disparos);
    }

    private Disparo disparo(Coordenada destino) {
        return new Disparo(Bando.MAQUINA, destino, ResultadoDisparo.AGUA, null);
    }
}
