package uy.edu.ort.battleshipdemo.modelo.excepciones;

public final class DistribucionFlotaException extends ReglaJuegoException {
    public DistribucionFlotaException() {
        super("No fue posible distribuir la flota enemiga");
    }
}
