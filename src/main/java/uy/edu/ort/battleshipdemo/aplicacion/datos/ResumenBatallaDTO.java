package uy.edu.ort.battleshipdemo.aplicacion.datos;
import uy.edu.ort.battleshipdemo.modelo.valores.EstadoBatalla;
import uy.edu.ort.battleshipdemo.modelo.batalla.Batalla;
public class ResumenBatallaDTO {
    private final int id; private final String lugar; private final int filas; private final int columnas; private final EstadoBatalla estado; private final int sanasPropias; private final int totalPropias; private final int sanasEnemigas; private final int totalEnemigas;
    public ResumenBatallaDTO(Batalla batalla) {
        id = batalla.getId(); lugar = batalla.getLugar();
        filas = batalla.getConfiguracion().getFilas(); columnas = batalla.getConfiguracion().getColumnas();
        estado = batalla.getEstado(); sanasPropias = batalla.getHumano().integridad();
        totalPropias = batalla.getConfiguracion().totalCeldasFlota();
        sanasEnemigas = batalla.getMaquina().integridad(); totalEnemigas = totalPropias;
    }
    public int getId(){return id;} public String getLugar(){return lugar;} public int getFilas(){return filas;} public int getColumnas(){return columnas;} public EstadoBatalla getEstado(){return estado;} public int getSanasPropias(){return sanasPropias;} public int getTotalPropias(){return totalPropias;} public int getSanasEnemigas(){return sanasEnemigas;} public int getTotalEnemigas(){return totalEnemigas;}
}
