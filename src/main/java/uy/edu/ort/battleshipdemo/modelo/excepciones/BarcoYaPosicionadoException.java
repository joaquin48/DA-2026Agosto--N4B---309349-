package uy.edu.ort.battleshipdemo.modelo.excepciones;

public final class BarcoYaPosicionadoException extends ReglaJuegoException {
    public BarcoYaPosicionadoException() { super("El barco ya está posicionado"); }
}
