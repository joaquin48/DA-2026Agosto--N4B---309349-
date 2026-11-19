package uy.edu.ort.battleshipdemo.modelo.tacticas;

import uy.edu.ort.battleshipdemo.modelo.batalla.Disparo;
import uy.edu.ort.battleshipdemo.modelo.oceano.Coordenada;
public abstract class Tactica {
    private final String nombre;
    protected Tactica(String nombre) { this.nombre = nombre; }
    public String getNombre() { return nombre; }
    public abstract Coordenada elegirDisparo(ContextoTactico contexto);
    public void registrarResultado(Disparo disparo) { }
    public abstract Tactica copiar();
}
