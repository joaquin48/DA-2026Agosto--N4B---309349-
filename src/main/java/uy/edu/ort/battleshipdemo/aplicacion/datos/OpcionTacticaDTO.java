package uy.edu.ort.battleshipdemo.aplicacion.datos;
public class OpcionTacticaDTO {
    private final String id; private final String nombre;
    public OpcionTacticaDTO(String id,String nombre){this.id=id;this.nombre=nombre;}
    public String getId(){return id;} public String getNombre(){return nombre;}
}
