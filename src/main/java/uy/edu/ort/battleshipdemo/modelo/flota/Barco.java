package uy.edu.ort.battleshipdemo.modelo.flota;

import java.util.ArrayList;
import java.util.List;
import uy.edu.ort.battleshipdemo.modelo.catalogo.TipoBarco;
import uy.edu.ort.battleshipdemo.modelo.oceano.Celda;

public class Barco {
    private final int id;
    private final TipoBarco tipo;
    private List<Celda> celdas = new ArrayList<>();
    public Barco(int id, TipoBarco tipo) { this.id = id; this.tipo = tipo; }
    public int getId() { return id; }
    public TipoBarco getTipo() { return tipo; }
    public boolean estaPosicionado() { return celdas.size() == tipo.getTamano(); }
    public void asignarCeldas(List<Celda> celdas) { this.celdas = new ArrayList<>(celdas); }
    public int partesSanas() {
        if (!estaPosicionado()) return tipo.getTamano();
        int sanas = 0;
        for (Celda celda : celdas) if (!celda.estaDisparada()) sanas++;
        return sanas;
    }
    public boolean estaHundido() { return estaPosicionado() && partesSanas() == 0; }
    public boolean seHundiriaCon(Celda celda) { return celdas.contains(celda) && partesSanas() == 1 && !celda.estaDisparada(); }
    public List<Celda> getCeldas() { return new ArrayList<>(celdas); }
}
