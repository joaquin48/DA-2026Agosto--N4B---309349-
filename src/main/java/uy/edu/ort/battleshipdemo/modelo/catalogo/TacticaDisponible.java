package uy.edu.ort.battleshipdemo.modelo.catalogo;

public class TacticaDisponible {
    private final String id;
    private final String nombre;

    public TacticaDisponible(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
}
