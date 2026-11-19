package uy.edu.ort.battleshipdemo.modelo.eventos;

public interface Observador {
    void actualizar(Object evento, Observable origen);
}
