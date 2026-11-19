package uy.edu.ort.battleshipdemo.modelo.excepciones;

import uy.edu.ort.battleshipdemo.modelo.oceano.Coordenada;

public final class CeldaYaDisparadaException extends ReglaJuegoException {
    public CeldaYaDisparadaException(Coordenada coordenada) { super("La celda " + coordenada + " ya fue atacada"); }
}
