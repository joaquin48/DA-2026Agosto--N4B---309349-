package uy.edu.ort.battleshipdemo.modelo.excepciones;

public abstract class ReglaJuegoException extends BatallaNavalException {
    protected ReglaJuegoException(String mensaje) { super(mensaje); }
}
