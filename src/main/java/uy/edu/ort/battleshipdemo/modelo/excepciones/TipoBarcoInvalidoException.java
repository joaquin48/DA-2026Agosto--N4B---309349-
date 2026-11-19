package uy.edu.ort.battleshipdemo.modelo.excepciones;

public final class TipoBarcoInvalidoException extends ReglaJuegoException {
    public TipoBarcoInvalidoException() { super("El tipo de barco es inválido"); }
}
