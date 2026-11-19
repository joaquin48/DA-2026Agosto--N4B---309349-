package uy.edu.ort.battleshipdemo.modelo.excepciones;

public final class AdyacenciaBarcosException extends ReglaJuegoException {
    public AdyacenciaBarcosException() { super("Los barcos no pueden tocarse"); }
}
