package uy.edu.ort.battleshipdemo.modelo.tacticas;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import uy.edu.ort.battleshipdemo.modelo.batalla.Disparo;
import uy.edu.ort.battleshipdemo.modelo.oceano.Coordenada;
import uy.edu.ort.battleshipdemo.modelo.valores.ModoCazaAtaque;
import uy.edu.ort.battleshipdemo.modelo.valores.ResultadoDisparo;
public class TacticaCazaAtaque extends Tactica {
    private ModoCazaAtaque modo = ModoCazaAtaque.CAZA;
    private final List<Coordenada> impactosActivos = new ArrayList<>();
    private final Random random = new Random();
    public TacticaCazaAtaque() { super("Caza y ataque"); }
    private TacticaCazaAtaque(ModoCazaAtaque modo, List<Coordenada> impactosActivos) {
        this();
        this.modo = modo;
        this.impactosActivos.addAll(impactosActivos);
    }
    public Coordenada elegirDisparo(ContextoTactico contexto) { return modo == ModoCazaAtaque.CAZA ? elegirEnCaza(contexto) : elegirEnAtaque(contexto); }
    public void registrarResultado(Disparo disparo) {
        if (disparo.getResultado() == ResultadoDisparo.HUNDIDO) {
            reiniciarCaza();
        } else if (disparo.getResultado() == ResultadoDisparo.IMPACTO) {
            if (!impactosActivos.contains(disparo.getDestino())) impactosActivos.add(disparo.getDestino());
            modo = ModoCazaAtaque.ATAQUE;
        }
    }
    public Tactica copiar() { return new TacticaCazaAtaque(modo, impactosActivos); }
    protected Coordenada elegirEnCaza(ContextoTactico contexto) {
        List<Coordenada> disponibles = contexto.celdasNoAtacadas();
        if (disponibles.isEmpty()) throw new IllegalStateException("No quedan celdas disponibles para disparar");
        return disponibles.get(random.nextInt(disponibles.size()));
    }
    protected Coordenada elegirEnAtaque(ContextoTactico contexto) {
        for (Coordenada coordenada : candidatosDeAtaque()) {
            if (contexto.estaDentro(coordenada) && !contexto.fueAtacada(coordenada)) return coordenada;
        }
        throw new IllegalStateException("El ataque no tiene destinos válidos");
    }
    protected void reiniciarCaza() { modo = ModoCazaAtaque.CAZA; impactosActivos.clear(); }

    private List<Coordenada> candidatosDeAtaque() {
        if (impactosActivos.size() == 1) return vecinosEnOrden(impactosActivos.get(0));
        if (impactosActivos.size() < 2) throw new IllegalStateException("El ataque no tiene impactos activos");

        Coordenada primero = impactosActivos.get(0);
        boolean mismaFila = true;
        boolean mismaColumna = true;
        for (Coordenada impacto : impactosActivos) {
            if (impacto.getFila() != primero.getFila()) mismaFila = false;
            if (impacto.getColumna() != primero.getColumna()) mismaColumna = false;
        }
        if (mismaFila) return extremosHorizontales();
        if (mismaColumna) return extremosVerticales();
        throw new IllegalStateException("Los impactos activos no tienen una orientación válida");
    }

    private List<Coordenada> vecinosEnOrden(Coordenada impacto) {
        List<Coordenada> vecinos = new ArrayList<>();
        vecinos.add(impacto.desplazar(0, 1)); vecinos.add(impacto.desplazar(1, 0));
        vecinos.add(impacto.desplazar(0, -1)); vecinos.add(impacto.desplazar(-1, 0));
        return vecinos;
    }

    private List<Coordenada> extremosHorizontales() {
        int fila = impactosActivos.get(0).getFila();
        int menorColumna = impactosActivos.get(0).getColumna();
        int mayorColumna = menorColumna;
        for (Coordenada impacto : impactosActivos) {
            if (impacto.getColumna() < menorColumna) menorColumna = impacto.getColumna();
            if (impacto.getColumna() > mayorColumna) mayorColumna = impacto.getColumna();
        }
        List<Coordenada> extremos = new ArrayList<>();
        extremos.add(new Coordenada(fila, mayorColumna + 1));
        extremos.add(new Coordenada(fila, menorColumna - 1));
        return extremos;
    }

    private List<Coordenada> extremosVerticales() {
        int columna = impactosActivos.get(0).getColumna();
        int menorFila = impactosActivos.get(0).getFila();
        int mayorFila = menorFila;
        for (Coordenada impacto : impactosActivos) {
            if (impacto.getFila() < menorFila) menorFila = impacto.getFila();
            if (impacto.getFila() > mayorFila) mayorFila = impacto.getFila();
        }
        List<Coordenada> extremos = new ArrayList<>();
        extremos.add(new Coordenada(mayorFila + 1, columna));
        extremos.add(new Coordenada(menorFila - 1, columna));
        return extremos;
    }
}
