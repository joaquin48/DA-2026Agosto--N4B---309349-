package uy.edu.ort.battleshipdemo.modelo.excepciones;

public final class TipoBarcoInexistenteException extends ReglaJuegoException {
    public TipoBarcoInexistenteException(String id) { super("El tipo de barco '" + id + "' no existe"); }
}
