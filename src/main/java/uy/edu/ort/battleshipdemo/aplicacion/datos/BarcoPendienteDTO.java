package uy.edu.ort.battleshipdemo.aplicacion.datos;
public class BarcoPendienteDTO {
    private final int id; private final String nombre; private final int tamano;
    public BarcoPendienteDTO(int id, String nombre, int tamano) { this.id=id; this.nombre=nombre; this.tamano=tamano; }
    public int getId(){return id;} public String getNombre(){return nombre;} public int getTamano(){return tamano;}
}
