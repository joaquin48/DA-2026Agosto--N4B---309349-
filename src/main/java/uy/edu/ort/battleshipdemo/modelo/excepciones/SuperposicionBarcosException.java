package uy.edu.ort.battleshipdemo.modelo.excepciones;

public final class SuperposicionBarcosException extends ReglaJuegoException {
    public SuperposicionBarcosException() { super("Los barcos no pueden superponerse"); }
}
