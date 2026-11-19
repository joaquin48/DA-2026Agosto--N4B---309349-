package uy.edu.ort.battleshipdemo.modelo.excepciones;

public final class TacticaInexistenteException extends ReglaJuegoException {
    public TacticaInexistenteException(String id) { super("La táctica '" + id + "' no existe"); }
}
