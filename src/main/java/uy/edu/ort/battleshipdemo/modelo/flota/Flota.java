package uy.edu.ort.battleshipdemo.modelo.flota;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import uy.edu.ort.battleshipdemo.modelo.batalla.ItemFlota;
import uy.edu.ort.battleshipdemo.modelo.batalla.ConfiguracionBatalla;
import uy.edu.ort.battleshipdemo.modelo.excepciones.BarcoInexistenteException;

public class Flota {
    private final List<Barco> barcos;
    public Flota(List<Barco> barcos) { this.barcos = new ArrayList<>(barcos); }
    public static Flota crearDesde(ConfiguracionBatalla configuracion) {
        List<Barco> barcos = new ArrayList<>();
        int siguienteId = 1;
        for (ItemFlota item : configuracion.getItems()) {
            for (int cantidad = 0; cantidad < item.getCantidad(); cantidad++) {
                barcos.add(new Barco(siguienteId++, item.getTipo()));
            }
        }
        return new Flota(barcos);
    }
    public Barco buscarBarco(int id) {
        for (Barco barco : barcos) if (barco.getId() == id) return barco;
        throw new BarcoInexistenteException(id);
    }
    public List<Barco> pendientesPorTamano() {
        List<Barco> pendientes = new ArrayList<>();
        for (Barco barco : barcos) if (!barco.estaPosicionado()) pendientes.add(barco);
        pendientes.sort(new Comparator<Barco>() {
            public int compare(Barco primero, Barco segundo) {
                return segundo.getTipo().getTamano() - primero.getTipo().getTamano();
            }
        });
        return pendientes;
    }
    public boolean estaPosicionada() {
        for (Barco barco : barcos) if (!barco.estaPosicionado()) return false;
        return true;
    }
    public int partesSanas() {
        int total = 0;
        for (Barco barco : barcos) total += barco.partesSanas();
        return total;
    }
    public int tamanoTotal() {
        int total = 0;
        for (Barco barco : barcos) total += barco.getTipo().getTamano();
        return total;
    }
    public boolean estaHundida() {
        if (!estaPosicionada()) return false;
        for (Barco barco : barcos) if (!barco.estaHundido()) return false;
        return true;
    }
}
