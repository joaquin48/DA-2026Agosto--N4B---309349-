package uy.edu.ort.battleshipdemo.modelo.excepciones;

public class SolicitudDesactualizadaException extends ReglaJuegoException {
    public SolicitudDesactualizadaException() {
        super("La solicitud está desactualizada");
    }
}
