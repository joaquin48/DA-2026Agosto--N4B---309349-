package uy.edu.ort.battleshipdemo.modelo.excepciones;

public final class LugarDuplicadoException extends ReglaJuegoException {
    public LugarDuplicadoException(String lugar) {
        super("No se puede librar más de una batalla en el mismo lugar, cambie de nombre");
    }
}
