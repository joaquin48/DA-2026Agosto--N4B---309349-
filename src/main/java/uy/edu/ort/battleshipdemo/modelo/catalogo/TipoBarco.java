package uy.edu.ort.battleshipdemo.modelo.catalogo;
import uy.edu.ort.battleshipdemo.modelo.excepciones.TipoBarcoInvalidoException;
public class TipoBarco {
    private final String id; private final String nombre; private final int tamano;
    public TipoBarco(String id,String nombre,int tamano){this.id=id;this.nombre=nombre;this.tamano=tamano;validar();}
    public void validar(){if(id==null||id.isBlank()||nombre==null||nombre.isBlank()||tamano<=0)throw new TipoBarcoInvalidoException();}
    public String getId(){return id;} public String getNombre(){return nombre;} public int getTamano(){return tamano;}
}
