package uy.edu.ort.battleshipdemo.modelo.excepciones;

public final class FlotaIncompletaException extends ReglaJuegoException {
    public FlotaIncompletaException() { super("Debe posicionar todos sus barcos primero"); }
}
