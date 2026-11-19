package uy.edu.ort.battleshipdemo.modelo.batalla;
import java.util.Map;
import java.util.LinkedHashMap;
public class SolicitudNuevaBatalla {
    private final String lugar; private final int filas; private final int columnas; private final String tacticaId; private final Map<String,Integer> cantidadesPorTipo;
    public SolicitudNuevaBatalla(String lugar,int filas,int columnas,String tacticaId,Map<String,Integer> cantidadesPorTipo){this.lugar=lugar;this.filas=filas;this.columnas=columnas;this.tacticaId=tacticaId;this.cantidadesPorTipo=new LinkedHashMap<>(cantidadesPorTipo);}
    public String getLugar(){return lugar;} public int getFilas(){return filas;} public int getColumnas(){return columnas;} public String getTacticaId(){return tacticaId;} public Map<String,Integer> getCantidadesPorTipo(){return cantidadesPorTipo;}
}
