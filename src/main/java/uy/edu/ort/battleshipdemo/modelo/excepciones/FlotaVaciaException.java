package uy.edu.ort.battleshipdemo.modelo.excepciones;

public final class FlotaVaciaException extends ReglaJuegoException {
    public FlotaVaciaException() { super("La flota no puede estar vacía"); }
}
