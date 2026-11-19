package uy.edu.ort.battleshipdemo.modelo.eventos;

import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Observable {
    private final CopyOnWriteArrayList<Observador> observadores = new CopyOnWriteArrayList<>();
    public void agregarObservador(Observador observador) {
        observadores.addIfAbsent(observador);
    }
    public void quitarObservador(Observador observador) { observadores.remove(observador); }
    protected void notificar(Object evento) {
        for (Observador observador : observadores) {
            try {
                observador.actualizar(evento, this);
            } catch (Exception excepcion) {
                System.err.println("Error al notificar evento: " + excepcion.getMessage());
            }
        }
    }
}
