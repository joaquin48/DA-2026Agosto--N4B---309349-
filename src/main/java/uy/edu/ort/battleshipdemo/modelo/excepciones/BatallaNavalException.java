package uy.edu.ort.battleshipdemo.modelo.excepciones;

public abstract class BatallaNavalException extends RuntimeException {
    protected BatallaNavalException(String mensaje) {
        super(mensaje);
    }
}
