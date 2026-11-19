package uy.edu.ort.battleshipdemo.modelo.batalla;
import uy.edu.ort.battleshipdemo.modelo.catalogo.TipoBarco; import uy.edu.ort.battleshipdemo.modelo.oceano.Coordenada; import uy.edu.ort.battleshipdemo.modelo.valores.Bando; import uy.edu.ort.battleshipdemo.modelo.valores.ResultadoDisparo;
public class Disparo {
    private final Bando tirador; private final Coordenada destino; private final ResultadoDisparo resultado; private final TipoBarco tipoHundido;
    public Disparo(Bando tirador,Coordenada destino,ResultadoDisparo resultado,TipoBarco tipoHundido){this.tirador=tirador;this.destino=destino;this.resultado=resultado;this.tipoHundido=tipoHundido;}
    public Bando getTirador(){return tirador;} public Coordenada getDestino(){return destino;} public ResultadoDisparo getResultado(){return resultado;} public TipoBarco getTipoHundido(){return tipoHundido;}
}
