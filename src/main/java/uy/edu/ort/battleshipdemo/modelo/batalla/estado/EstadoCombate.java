package uy.edu.ort.battleshipdemo.modelo.batalla.estado;

import uy.edu.ort.battleshipdemo.modelo.valores.EstadoBatalla;

public class EstadoCombate extends EstadoBatallaBase {
    @Override
    public EstadoBatalla getNombre() { return EstadoBatalla.EN_COMBATE; }

    @Override
    public void validarJugarRonda() { }
}
