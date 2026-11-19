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
import uy.edu.ort.battleshipdemo.modelo.tacticas.TacticaSecuencial;
import uy.edu.ort.battleshipdemo.modelo.valores.Bando;
import uy.edu.ort.battleshipdemo.modelo.valores.ResultadoDisparo;

class TacticaSecuencialTests {
    private final TacticaSecuencial tactica = new TacticaSecuencial();

    @Test void comienzaEnLaEsquinaInferiorIzquierda() {
        assertEquals(new Coordenada(0, 0), tactica.elegirDisparo(contexto(6, 8, List.of())));
    }

    @Test void recorreColumnasYLuegoAvanzaDeFila() {
        List<Disparo> disparos = List.of(
                disparo(0, 0), disparo(0, 1), disparo(0, 2), disparo(0, 3));

        assertEquals(new Coordenada(1, 0), tactica.elegirDisparo(contexto(3, 4, disparos)));
    }

    @Test void omiteCeldasAtacadasAunqueNoSeanConsecutivas() {
        List<Disparo> disparos = List.of(disparo(0, 0), disparo(0, 2));

        assertEquals(new Coordenada(0, 1), tactica.elegirDisparo(contexto(2, 3, disparos)));
    }

    @Test void cubreUnTableroRectangularSinRepetirCeldas() {
        int filas = 3;
        int columnas = 5;
        List<Disparo> disparos = new ArrayList<>();

        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
                Coordenada elegida = tactica.elegirDisparo(contexto(filas, columnas, disparos));
                assertEquals(new Coordenada(fila, columna), elegida);
                disparos.add(disparo(elegida.getFila(), elegida.getColumna()));
            }
        }

        assertEquals(filas * columnas, disparos.stream().map(Disparo::getDestino).distinct().count());
    }

    @Test void fallaExplicitamenteCuandoElTableroEstaAgotado() {
        List<Disparo> disparos = List.of(disparo(0, 0), disparo(0, 1));

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> tactica.elegirDisparo(contexto(1, 2, disparos)));

        assertEquals("No quedan celdas disponibles para disparar", error.getMessage());
    }

    @Test void copiarDevuelveUnaInstanciaIndependienteSinEstado() {
        Tactica copia = tactica.copiar();

        assertNotSame(tactica, copia);
        assertEquals(new Coordenada(0, 0), copia.elegirDisparo(contexto(2, 2, List.of())));
    }

    private ContextoTactico contexto(int filas, int columnas, List<Disparo> disparos) {
        return new ContextoTactico(filas, columnas, disparos);
    }

    private Disparo disparo(int fila, int columna) {
        return new Disparo(Bando.MAQUINA, new Coordenada(fila, columna), ResultadoDisparo.AGUA, null);
    }
}
