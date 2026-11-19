package uy.edu.ort.battleshipdemo.aplicacion.datos;
public class CeldaDTO {
    private final int fila; private final int columna; private final String valor;
    public CeldaDTO(int fila,int columna,String valor){this.fila=fila;this.columna=columna;this.valor=valor;}
    public int getFila(){return fila;} public int getColumna(){return columna;} public String getValor(){return valor;}
}
