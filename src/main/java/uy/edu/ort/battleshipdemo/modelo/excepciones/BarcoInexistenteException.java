package uy.edu.ort.battleshipdemo.modelo.excepciones;

public final class BarcoInexistenteException extends ReglaJuegoException {
    public BarcoInexistenteException(int id) { super("El barco '" + id + "' no existe"); }
}
