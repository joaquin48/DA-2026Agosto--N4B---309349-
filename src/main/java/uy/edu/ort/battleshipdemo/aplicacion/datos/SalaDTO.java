package uy.edu.ort.battleshipdemo.aplicacion.datos;
import java.util.List;
import java.util.ArrayList;
import uy.edu.ort.battleshipdemo.modelo.batalla.Batalla;
import uy.edu.ort.battleshipdemo.modelo.campana.SistemaBatallas;
public class SalaDTO {
    private final int ganadas; private final int perdidas; private final double exito; private final List<ResumenBatallaDTO> batallas;
    public SalaDTO(SistemaBatallas sistema) {
        this.ganadas = sistema.contarGanadas();
        this.perdidas = sistema.contarPerdidas();
        this.exito = sistema.calcularExito();
        List<ResumenBatallaDTO> resumenes = new ArrayList<>();
        for (Batalla batalla : sistema.listarBatallas()) {
            synchronized (batalla) { resumenes.add(new ResumenBatallaDTO(batalla)); }
        }
        this.batallas = resumenes;
    }
    public int getGanadas(){return ganadas;} public int getPerdidas(){return perdidas;} public double getExito(){return exito;} public List<ResumenBatallaDTO> getBatallas(){return batallas;}
}
