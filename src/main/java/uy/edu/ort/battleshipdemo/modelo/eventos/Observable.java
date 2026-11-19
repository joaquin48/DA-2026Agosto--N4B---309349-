package uy.edu.ort.battleshipdemo.modelo.eventos;

import java.util.ArrayList;
import java.util.List;

public abstract class Observable {
    private final List<Observador> observadores = new ArrayList<>();
    public void agregarObservador(Observador observador) {
        if (!observadores.contains(observador)) observadores.add(observador);
    }
    public void quitarObservador(Observador observador) { observadores.remove(observador); }
    protected void notificar(Object evento) {
        List<Observador> copia = new ArrayList<>(observadores);
        for (Observador observador : copia) {
            try {
                observador.actualizar(evento, this);
            } catch (Exception excepcion) {
                System.err.println("Error al notificar evento: " + excepcion.getMessage());
            }
        }
    }
}
