package uy.edu.ort.battleshipdemo.modelo.oceano;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import uy.edu.ort.battleshipdemo.modelo.excepciones.DistribucionFlotaException;
import uy.edu.ort.battleshipdemo.modelo.flota.Barco;
import uy.edu.ort.battleshipdemo.modelo.flota.Flota;
public class DistribuidorAleatorio {
    private final Random random;
    public DistribuidorAleatorio() { this(new Random()); }
    public DistribuidorAleatorio(long semilla) { this(new Random(semilla)); }
    private DistribuidorAleatorio(Random random) { this.random = random; }

    public List<UbicacionBarco> planificar(Oceano oceano, Flota flota) {
        List<UbicacionBarco> plan = new ArrayList<>();
        if (!buscarConRetroceso(oceano, flota, plan)) throw new DistribucionFlotaException();
        return new ArrayList<>(plan);
    }

    protected boolean buscarConRetroceso(Oceano oceano, Flota flota, List<UbicacionBarco> plan) {
        List<Barco> barcos = flota.pendientesPorTamano();
        if (plan.size() == barcos.size()) return true;

        Oceano temporal = recrearOceano(oceano, flota, plan);
        Barco original = barcos.get(plan.size());
        Barco candidato = new Barco(original.getId(), original.getTipo());
        List<UbicacionBarco> opciones = new ArrayList<>(temporal.posicionesValidas(candidato));
        Collections.shuffle(opciones, random);

        for (UbicacionBarco opcion : opciones) {
            plan.add(opcion);
            if (buscarConRetroceso(oceano, flota, plan)) return true;
            plan.remove(plan.size() - 1);
        }
        return false;
    }

    private Oceano recrearOceano(Oceano oceano, Flota flota, List<UbicacionBarco> plan) {
        Oceano temporal = new Oceano(oceano.getFilas(), oceano.getColumnas());
        for (UbicacionBarco ubicacion : plan) {
            Barco original = flota.buscarBarco(ubicacion.getBarcoId());
            Barco copia = new Barco(original.getId(), original.getTipo());
            temporal.ubicarBarco(copia, ubicacion.getProa(), ubicacion.getOrientacion());
        }
        return temporal;
    }
}
