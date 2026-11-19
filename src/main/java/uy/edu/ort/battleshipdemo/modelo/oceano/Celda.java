package uy.edu.ort.battleshipdemo.modelo.oceano;

import uy.edu.ort.battleshipdemo.modelo.flota.Barco;
import uy.edu.ort.battleshipdemo.modelo.catalogo.TipoBarco;
import uy.edu.ort.battleshipdemo.modelo.valores.ResultadoDisparo;

public class Celda {
    private final Coordenada posicion;
    private boolean disparada;
    private Barco barco;
    public Celda(Coordenada posicion) { this.posicion = posicion; }
    public Coordenada getPosicion() { return posicion; }
    public boolean estaOcupada() { return barco != null; }
    public boolean estaDisparada() { return disparada; }
    public TipoBarco getTipoBarco() { return barco == null ? null : barco.getTipo(); }
    public void asignarBarco(Barco barco) { this.barco = barco; }
    public ResultadoDisparo preverResultado() {
        if (barco == null) return ResultadoDisparo.AGUA;
        return barco.seHundiriaCon(this) ? ResultadoDisparo.HUNDIDO : ResultadoDisparo.IMPACTO;
    }
    public ResultadoDisparo recibirDisparo() { ResultadoDisparo resultado = preverResultado(); disparada = true; return resultado; }
}
