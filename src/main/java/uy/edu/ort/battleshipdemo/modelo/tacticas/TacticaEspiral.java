package uy.edu.ort.battleshipdemo.modelo.tacticas;

import java.util.ArrayList;
import java.util.List;
import uy.edu.ort.battleshipdemo.modelo.batalla.Disparo;
import uy.edu.ort.battleshipdemo.modelo.oceano.Coordenada;
public class TacticaEspiral extends Tactica {
    private List<Coordenada> recorrido = new ArrayList<>();
    private int filasRecorrido;
    private int columnasRecorrido;
    public TacticaEspiral() { super("Espiral"); }
    public Coordenada elegirDisparo(ContextoTactico contexto) {
        asegurarRecorrido(contexto.getFilas(), contexto.getColumnas());
        for (Coordenada coordenada : recorrido) {
            if (!contexto.fueAtacada(coordenada)) return coordenada;
        }
        throw new IllegalStateException("No quedan celdas disponibles para disparar");
    }
    public void registrarResultado(Disparo disparo) { }
    public Tactica copiar() { return new TacticaEspiral(); }
    protected List<Coordenada> generarRecorrido(int filas, int columnas) {
        int totalCeldas = filas * columnas;
        if (totalCeldas <= 0) return new ArrayList<>();

        List<Coordenada> resultado = new ArrayList<>(totalCeldas);
        Coordenada posicion = new Coordenada((filas - 1) / 2, (columnas - 1) / 2);
        resultado.add(posicion);

        int[][] direcciones = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        int longitudTramo = 1;
        int direccion = 0;

        while (resultado.size() < totalCeldas) {
            for (int tramo = 0; tramo < 2 && resultado.size() < totalCeldas; tramo++) {
                for (int paso = 0; paso < longitudTramo; paso++) {
                    posicion = posicion.desplazar(direcciones[direccion][0], direcciones[direccion][1]);
                    if (estaDentro(posicion, filas, columnas)) resultado.add(posicion);
                }
                direccion = (direccion + 1) % direcciones.length;
            }
            longitudTramo++;
        }
        return resultado;
    }

    private void asegurarRecorrido(int filas, int columnas) {
        if (filas != filasRecorrido || columnas != columnasRecorrido) {
            recorrido = generarRecorrido(filas, columnas);
            filasRecorrido = filas;
            columnasRecorrido = columnas;
        }
    }

    private boolean estaDentro(Coordenada coordenada, int filas, int columnas) {
        return coordenada.getFila() >= 0 && coordenada.getFila() < filas
                && coordenada.getColumna() >= 0 && coordenada.getColumna() < columnas;
    }
}
