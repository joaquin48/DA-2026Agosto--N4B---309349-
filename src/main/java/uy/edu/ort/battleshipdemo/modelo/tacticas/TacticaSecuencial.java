package uy.edu.ort.battleshipdemo.modelo.tacticas;

import uy.edu.ort.battleshipdemo.modelo.batalla.Disparo;
import uy.edu.ort.battleshipdemo.modelo.oceano.Coordenada;
public class TacticaSecuencial extends Tactica {
    public TacticaSecuencial() { super("Secuencial"); }
    public Coordenada elegirDisparo(ContextoTactico contexto) {
        for (int fila = 0; fila < contexto.getFilas(); fila++) {
            for (int columna = 0; columna < contexto.getColumnas(); columna++) {
                Coordenada destino = new Coordenada(fila, columna);
                if (!contexto.fueAtacada(destino)) return destino;
            }
        }
        throw new IllegalStateException("No quedan celdas disponibles para disparar");
    }
    public void registrarResultado(Disparo disparo) { }
    public Tactica copiar() { return new TacticaSecuencial(); }
}
