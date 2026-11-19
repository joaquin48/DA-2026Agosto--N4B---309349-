package uy.edu.ort.battleshipdemo.modelo.oceano;
import java.util.Objects;
import uy.edu.ort.battleshipdemo.modelo.valores.Orientacion;
public class UbicacionBarco {
    private final int barcoId; private final Coordenada proa; private final Orientacion orientacion;
    public UbicacionBarco(int barcoId,Coordenada proa,Orientacion orientacion){this.barcoId=barcoId;this.proa=Objects.requireNonNull(proa);this.orientacion=Objects.requireNonNull(orientacion);}
    public int getBarcoId(){return barcoId;} public Coordenada getProa(){return proa;} public Orientacion getOrientacion(){return orientacion;}
}
