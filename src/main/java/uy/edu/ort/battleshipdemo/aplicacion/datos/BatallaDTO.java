package uy.edu.ort.battleshipdemo.aplicacion.datos;
import java.util.List;
import uy.edu.ort.battleshipdemo.modelo.valores.EstadoBatalla;
import java.util.ArrayList;
import uy.edu.ort.battleshipdemo.modelo.batalla.Batalla;
import uy.edu.ort.battleshipdemo.modelo.flota.Barco;
import uy.edu.ort.battleshipdemo.modelo.oceano.Celda;
import uy.edu.ort.battleshipdemo.modelo.oceano.Oceano;
public class BatallaDTO {
    private final int id; private final String lugar; private final EstadoBatalla estado; private final long version; private final int numeroRonda; private final int filas; private final int columnas; private final List<String> bajasEnemigas; private final List<CeldaDTO> propias; private final List<CeldaDTO> enemigas; private final List<BarcoPendienteDTO> barcosPendientes;
    public BatallaDTO(Batalla batalla) {
        id = batalla.getId(); lugar = batalla.getLugar(); estado = batalla.getEstado();
        version = batalla.getVersion(); numeroRonda = batalla.numeroRonda();
        filas = batalla.getConfiguracion().getFilas(); columnas = batalla.getConfiguracion().getColumnas();
        bajasEnemigas = new ArrayList<>(batalla.bajasEnemigas());
        propias = proyectarOceano(batalla.getHumano().getOceano(), true);
        enemigas = proyectarOceano(batalla.getMaquina().getOceano(), estado == EstadoBatalla.PERDIDA);
        barcosPendientes = proyectarPendientes(batalla.barcosPendientes());
    }

    private static List<CeldaDTO> proyectarOceano(Oceano oceano, boolean revelar) {
        List<CeldaDTO> resultado = new ArrayList<>();
        for (Celda celda : oceano.getCeldas()) {
            String valor = "";
            if (celda.estaDisparada()) valor = celda.estaOcupada() ? "impacto" : "agua";
            else if (revelar && celda.estaOcupada()) valor = "barco";
            resultado.add(new CeldaDTO(celda.getPosicion().getFila(), celda.getPosicion().getColumna(), valor));
        }
        return resultado;
    }

    private static List<BarcoPendienteDTO> proyectarPendientes(List<Barco> barcos) {
        List<BarcoPendienteDTO> resultado = new ArrayList<>();
        for (Barco barco : barcos) {
            resultado.add(new BarcoPendienteDTO(barco.getId(), barco.getTipo().getNombre(),
                    barco.getTipo().getTamano()));
        }
        return resultado;
    }
    public int getId(){return id;} public String getLugar(){return lugar;} public EstadoBatalla getEstado(){return estado;} public long getVersion(){return version;} public int getNumeroRonda(){return numeroRonda;} public int getFilas(){return filas;} public int getColumnas(){return columnas;} public List<String> getBajasEnemigas(){return bajasEnemigas;} public List<CeldaDTO> getPropias(){return propias;} public List<CeldaDTO> getEnemigas(){return enemigas;} public List<BarcoPendienteDTO> getBarcosPendientes(){return barcosPendientes;}
}
