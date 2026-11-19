package uy.edu.ort.battleshipdemo.modelo.batalla;

import java.util.List;
import uy.edu.ort.battleshipdemo.modelo.flota.Barco;
import uy.edu.ort.battleshipdemo.modelo.flota.Flota;
import uy.edu.ort.battleshipdemo.modelo.oceano.DistribuidorAleatorio;
import uy.edu.ort.battleshipdemo.modelo.oceano.Oceano;
import uy.edu.ort.battleshipdemo.modelo.oceano.UbicacionBarco;
import uy.edu.ort.battleshipdemo.modelo.valores.Bando;

public class Participante {
    private final Bando bando;
    private final Flota flota;
    private final Oceano oceano;
    public Participante(Bando bando, Flota flota, Oceano oceano) { this.bando = bando; this.flota = flota; this.oceano = oceano; }
    public Bando getBando() { return bando; }
    public Oceano getOceano() { return oceano; }
    public Barco buscarBarco(int id) { return flota.buscarBarco(id); }
    public List<Barco> barcosPendientes() { return flota.pendientesPorTamano(); }
    public boolean flotaCompletaPosicionada() { return flota.estaPosicionada(); }
    public void distribuirFlota(DistribuidorAleatorio distribuidor) {
        List<UbicacionBarco> plan = distribuidor.planificar(oceano, flota);
        oceano.aplicarDistribucion(plan, flota);
    }
    public int integridad() { return flota.partesSanas(); }
    public boolean estaDerrotado() { return flota.estaHundida(); }
}
