package uy.edu.ort.battleshipdemo;

/**
 * Representa un comando individual que se envía al frontend.
 * Cada comando tiene un identificador (id) y un parámetro (dato asociado).
 */
public class Command {
    private String id;
    private Object parametro;

    public Command(String id, Object parametro) {
        this.id = id;
        this.parametro = parametro;
    }

    public Command() {
    }

    public String getId() {
        return id;
    }

    public Object getParametro() {
        return parametro;
    }
}
