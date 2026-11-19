package uy.edu.ort.battleshipdemo.modelo.oceano;

import java.util.Objects;

public class Coordenada {
    private final int fila;
    private final int columna;

    public Coordenada(int fila, int columna) { this.fila = fila; this.columna = columna; }
    public int getFila() { return fila; }
    public int getColumna() { return columna; }
    public Coordenada desplazar(int df, int dc) { return new Coordenada(fila + df, columna + dc); }
    public boolean esAdyacenteA(Coordenada otra) {
        if (otra == null || equals(otra)) return false;
        return Math.max(Math.abs(fila - otra.fila), Math.abs(columna - otra.columna)) == 1;
    }
    @Override public boolean equals(Object objeto) {
        if (this == objeto) return true;
        if (!(objeto instanceof Coordenada)) return false;
        Coordenada otra = (Coordenada) objeto;
        return fila == otra.fila && columna == otra.columna;
    }
    @Override public int hashCode() { return Objects.hash(fila, columna); }
    @Override public String toString() { return "(" + fila + ", " + columna + ")"; }
}
