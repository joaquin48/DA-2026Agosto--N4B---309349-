package uy.edu.ort.battleshipdemo.modelo.batalla;
import uy.edu.ort.battleshipdemo.modelo.catalogo.TipoBarco; import uy.edu.ort.battleshipdemo.modelo.excepciones.CantidadInvalidaException;
public class ItemFlota {
    private final TipoBarco tipo; private final int cantidad;
    public ItemFlota(TipoBarco tipo,int cantidad){this.tipo=tipo;this.cantidad=cantidad;validarCantidad();}
    public void validarCantidad(){if(cantidad<0||cantidad>6)throw new CantidadInvalidaException();}
    public int celdasOcupadas(){return cantidad*tipo.getTamano();}
    public TipoBarco getTipo(){return tipo;} public int getCantidad(){return cantidad;}
}
