package uy.edu.ort.battleshipdemo.modelo.excepciones;

import uy.edu.ort.battleshipdemo.modelo.oceano.Coordenada;

public final class CoordenadaFueraDeLimitesException extends ReglaJuegoException {
    public CoordenadaFueraDeLimitesException(Coordenada coordenada) { super("La coordenada " + coordenada + " está fuera de límites"); }
}
