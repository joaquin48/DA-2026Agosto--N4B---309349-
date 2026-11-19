package uy.edu.ort.battleshipdemo.modelo.tacticas;

import java.util.ArrayList;
import java.util.List;
import uy.edu.ort.battleshipdemo.modelo.batalla.Disparo;
import uy.edu.ort.battleshipdemo.modelo.oceano.Coordenada;

public class ContextoTactico {
    private final int filas;
    private final int columnas;
    private final List<Disparo> disparosPrevios;

    public ContextoTactico(int filas, int columnas, List<Disparo> disparosPrevios) {
        this.filas = filas;
        this.columnas = columnas;
        this.disparosPrevios = new ArrayList<>(disparosPrevios);
    }

    public boolean estaDentro(Coordenada coordenada) {
        return coordenada.getFila() >= 0
                && coordenada.getFila() < filas
                && coordenada.getColumna() >= 0
                && coordenada.getColumna() < columnas;
    }

    public boolean fueAtacada(Coordenada coordenada) {
        for (Disparo disparo : disparosPrevios) {
            if (disparo.getDestino().equals(coordenada)) return true;
        }
        return false;
    }

    public List<Coordenada> celdasNoAtacadas() {
        List<Coordenada> libres = new ArrayList<>();
        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
                Coordenada coordenada = new Coordenada(fila, columna);
                if (!fueAtacada(coordenada)) libres.add(coordenada);
            }
        }
        return libres;
    }

    public List<Coordenada> adyacentesOrtogonales(Coordenada coordenada) {
        List<Coordenada> adyacentes = new ArrayList<>();
        Coordenada[] candidatas = {
                coordenada.desplazar(1, 0),
                coordenada.desplazar(-1, 0),
                coordenada.desplazar(0, 1),
                coordenada.desplazar(0, -1)
        };

        for (Coordenada candidata : candidatas) {
            if (estaDentro(candidata)) adyacentes.add(candidata);
        }
        return adyacentes;
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public List<Disparo> getDisparosPrevios() {
        return disparosPrevios;
    }
}
