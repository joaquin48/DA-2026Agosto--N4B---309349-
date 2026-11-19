package uy.edu.ort.battleshipdemo.modelo.excepciones;

public final class CantidadInvalidaException extends ReglaJuegoException {
    public CantidadInvalidaException() { super("La cantidad debe ser un número entero entre 0 y 6"); }
}
