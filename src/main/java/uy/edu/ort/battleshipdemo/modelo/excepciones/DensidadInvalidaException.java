package uy.edu.ort.battleshipdemo.modelo.excepciones;

public final class DensidadInvalidaException extends ReglaJuegoException {
    public DensidadInvalidaException() { super("La flota es demasiado densa"); }
}
