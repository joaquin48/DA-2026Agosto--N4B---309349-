package uy.edu.ort.battleshipdemo.modelo.batalla.estado;

import uy.edu.ort.battleshipdemo.modelo.valores.EstadoBatalla;

public class EstadoPreparacion extends EstadoBatallaBase {
    @Override
    public EstadoBatalla getNombre() { return EstadoBatalla.EN_PREPARACION; }

    @Override
    public void validarUbicarBarco() { }

    @Override
    public void validarIniciarCombate() { }
}
