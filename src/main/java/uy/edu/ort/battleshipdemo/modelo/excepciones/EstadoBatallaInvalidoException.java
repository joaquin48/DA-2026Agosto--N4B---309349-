package uy.edu.ort.battleshipdemo.modelo.excepciones;

import uy.edu.ort.battleshipdemo.modelo.valores.EstadoBatalla;

public final class EstadoBatallaInvalidoException extends ReglaJuegoException {
    public EstadoBatallaInvalidoException(EstadoBatalla esperado, EstadoBatalla actual) { super("Se esperaba el estado " + esperado + " y la batalla está en " + actual); }
}
