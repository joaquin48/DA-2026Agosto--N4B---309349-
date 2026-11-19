package uy.edu.ort.battleshipdemo.modelo.batalla.estado;

import uy.edu.ort.battleshipdemo.modelo.excepciones.EstadoBatallaInvalidoException;
import uy.edu.ort.battleshipdemo.modelo.valores.EstadoBatalla;

public abstract class EstadoBatallaBase {
    public abstract EstadoBatalla getNombre();

    public void validarUbicarBarco() {
        operacionInvalida(EstadoBatalla.EN_PREPARACION);
    }

    public void validarIniciarCombate() {
        operacionInvalida(EstadoBatalla.EN_PREPARACION);
    }

    public void validarJugarRonda() {
        operacionInvalida(EstadoBatalla.EN_COMBATE);
    }

    protected void operacionInvalida(EstadoBatalla esperado) {
        throw new EstadoBatallaInvalidoException(esperado, getNombre());
    }
}
