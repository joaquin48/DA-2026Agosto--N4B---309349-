package uy.edu.ort.battleshipdemo.modelo.batalla.estado;

import uy.edu.ort.battleshipdemo.modelo.valores.EstadoBatalla;

public class EstadoGanada extends EstadoBatallaBase {
    @Override
    public EstadoBatalla getNombre() { return EstadoBatalla.GANADA; }
}
