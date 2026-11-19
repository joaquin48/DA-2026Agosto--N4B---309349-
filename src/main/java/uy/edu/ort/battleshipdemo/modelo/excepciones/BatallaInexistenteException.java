package uy.edu.ort.battleshipdemo.modelo.excepciones;

public final class BatallaInexistenteException extends ReglaJuegoException {
    public BatallaInexistenteException(int id) { super("La batalla '" + id + "' no existe"); }
}
