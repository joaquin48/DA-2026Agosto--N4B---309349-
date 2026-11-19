package uy.edu.ort.battleshipdemo.modelo.excepciones;

public final class DimensionInvalidaException extends ReglaJuegoException {
    public DimensionInvalidaException() { super("Las dimensiones deben ser números enteros entre 6 y 12"); }
}
