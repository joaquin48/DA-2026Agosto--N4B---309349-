package uy.edu.ort.battlefield;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import uy.edu.ort.battleshipdemo.modelo.batalla.Disparo;
import uy.edu.ort.battleshipdemo.modelo.oceano.Coordenada;
import uy.edu.ort.battleshipdemo.modelo.tacticas.ContextoTactico;
import uy.edu.ort.battleshipdemo.modelo.tacticas.Tactica;
import uy.edu.ort.battleshipdemo.modelo.tacticas.TacticaCazaAtaque;
import uy.edu.ort.battleshipdemo.modelo.valores.Bando;
import uy.edu.ort.battleshipdemo.modelo.valores.ResultadoDisparo;

class TacticaCazaAtaqueTests {
    private final TacticaCazaAtaque tactica = new TacticaCazaAtaque();

    @Test void cazaEligeUnaCeldaDisponible() {
        List<Disparo> previos = List.of(disparo(0, 0, ResultadoDisparo.AGUA));

        Coordenada elegida = tactica.elegirDisparo(contexto(2, 2, previos));

        assertTrue(List.of(new Coordenada(0, 1), new Coordenada(1, 0), new Coordenada(1, 1)).contains(elegida));
    }

    @Test void cazaEligeLaUltimaCeldaLibre() {
        List<Disparo> previos = List.of(disparo(0, 0, ResultadoDisparo.AGUA),
                disparo(0, 1, ResultadoDisparo.AGUA), disparo(1, 0, ResultadoDisparo.AGUA));

        assertEquals(new Coordenada(1, 1), tactica.elegirDisparo(contexto(2, 2, previos)));
    }

    @Test void primerImpactoIniciaAtaqueConOrdenFijo() {
        Disparo impacto = disparo(2, 2, ResultadoDisparo.IMPACTO);
        tactica.registrarResultado(impacto);

        assertEquals(new Coordenada(2, 3), tactica.elegirDisparo(contexto(5, 5, List.of(impacto))));
    }

    @Test void ataqueOmiteVecinosFueraDelTableroOAtacados() {
        Disparo impacto = disparo(2, 4, ResultadoDisparo.IMPACTO);
        Disparo aguaArriba = disparo(3, 4, ResultadoDisparo.AGUA);
        tactica.registrarResultado(impacto);
        tactica.registrarResultado(aguaArriba);

        assertEquals(new Coordenada(2, 3),
                tactica.elegirDisparo(contexto(5, 5, List.of(impacto, aguaArriba))));
    }

    @Test void deduceOrientacionHorizontalYPruebaAmbosExtremos() {
        Disparo primerImpacto = disparo(2, 2, ResultadoDisparo.IMPACTO);
        Disparo segundoImpacto = disparo(2, 3, ResultadoDisparo.IMPACTO);
        tactica.registrarResultado(primerImpacto);
        tactica.registrarResultado(segundoImpacto);

        assertEquals(new Coordenada(2, 4),
                tactica.elegirDisparo(contexto(5, 5, List.of(primerImpacto, segundoImpacto))));

        Disparo aguaDerecha = disparo(2, 4, ResultadoDisparo.AGUA);
        tactica.registrarResultado(aguaDerecha);
        assertEquals(new Coordenada(2, 1), tactica.elegirDisparo(
                contexto(5, 5, List.of(primerImpacto, segundoImpacto, aguaDerecha))));
    }

    @Test void deduceOrientacionVerticalYPruebaAmbosExtremos() {
        Disparo primerImpacto = disparo(2, 2, ResultadoDisparo.IMPACTO);
        Disparo segundoImpacto = disparo(3, 2, ResultadoDisparo.IMPACTO);
        tactica.registrarResultado(primerImpacto);
        tactica.registrarResultado(segundoImpacto);

        assertEquals(new Coordenada(4, 2),
                tactica.elegirDisparo(contexto(5, 5, List.of(primerImpacto, segundoImpacto))));

        Disparo aguaArriba = disparo(4, 2, ResultadoDisparo.AGUA);
        tactica.registrarResultado(aguaArriba);
        assertEquals(new Coordenada(1, 2), tactica.elegirDisparo(
                contexto(5, 5, List.of(primerImpacto, segundoImpacto, aguaArriba))));
    }

    @Test void hundimientoLimpiaElObjetivoYVuelveACaza() {
        Disparo impacto = disparo(0, 0, ResultadoDisparo.IMPACTO);
        Disparo hundido = disparo(0, 1, ResultadoDisparo.HUNDIDO);
        tactica.registrarResultado(impacto);
        tactica.registrarResultado(hundido);
        List<Disparo> previos = List.of(impacto, hundido,
                disparo(1, 0, ResultadoDisparo.AGUA));

        assertEquals(new Coordenada(1, 1), tactica.elegirDisparo(contexto(2, 2, previos)));
    }

    @Test void hundimientoDeBarcoDeUnaCeldaPermaneceEnCaza() {
        Disparo hundido = disparo(0, 0, ResultadoDisparo.HUNDIDO);
        tactica.registrarResultado(hundido);
        List<Disparo> previos = List.of(hundido, disparo(0, 1, ResultadoDisparo.AGUA),
                disparo(1, 0, ResultadoDisparo.AGUA));

        assertEquals(new Coordenada(1, 1), tactica.elegirDisparo(contexto(2, 2, previos)));
    }

    @Test void copiarConservaElAtaqueSinCompartirElEstado() {
        Disparo impacto = disparo(2, 2, ResultadoDisparo.IMPACTO);
        tactica.registrarResultado(impacto);
        Tactica copia = tactica.copiar();

        assertNotSame(tactica, copia);
        assertEquals(new Coordenada(2, 3), copia.elegirDisparo(contexto(5, 5, List.of(impacto))));

        copia.registrarResultado(disparo(2, 3, ResultadoDisparo.HUNDIDO));
        assertEquals(new Coordenada(2, 3), tactica.elegirDisparo(contexto(5, 5, List.of(impacto))));
    }

    @Test void cazaFallaCuandoElTableroEstaAgotado() {
        List<Disparo> previos = List.of(disparo(0, 0, ResultadoDisparo.AGUA));

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> tactica.elegirDisparo(contexto(1, 1, previos)));

        assertEquals("No quedan celdas disponibles para disparar", error.getMessage());
    }

    @Test void ataqueFallaSiNoTieneDestinosValidos() {
        Disparo impacto = disparo(0, 0, ResultadoDisparo.IMPACTO);
        tactica.registrarResultado(impacto);
        List<Disparo> previos = List.of(impacto, disparo(0, 1, ResultadoDisparo.AGUA),
                disparo(1, 0, ResultadoDisparo.AGUA));

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> tactica.elegirDisparo(contexto(2, 2, previos)));

        assertEquals("El ataque no tiene destinos válidos", error.getMessage());
    }

    private ContextoTactico contexto(int filas, int columnas, List<Disparo> disparos) {
        return new ContextoTactico(filas, columnas, disparos);
    }

    private Disparo disparo(int fila, int columna, ResultadoDisparo resultado) {
        return new Disparo(Bando.MAQUINA, new Coordenada(fila, columna), resultado, null);
    }
}
