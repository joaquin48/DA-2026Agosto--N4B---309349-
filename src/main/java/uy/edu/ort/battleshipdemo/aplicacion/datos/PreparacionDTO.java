package uy.edu.ort.battleshipdemo.aplicacion.datos;
import uy.edu.ort.battleshipdemo.modelo.valores.Orientacion;
public class PreparacionDTO {
    private final BatallaDTO batalla; private final int barcoSeleccionado; private final Orientacion orientacion;
    public PreparacionDTO(BatallaDTO batalla,int barcoSeleccionado,Orientacion orientacion){this.batalla=batalla;this.barcoSeleccionado=barcoSeleccionado;this.orientacion=orientacion;}
    public BatallaDTO getBatalla(){return batalla;} public int getBarcoSeleccionado(){return barcoSeleccionado;} public Orientacion getOrientacion(){return orientacion;}
}
