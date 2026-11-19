package uy.edu.ort.battleshipdemo.modelo.batalla;

import java.util.List;
import java.util.ArrayList;
import uy.edu.ort.battleshipdemo.modelo.excepciones.DensidadInvalidaException;
import uy.edu.ort.battleshipdemo.modelo.excepciones.DimensionInvalidaException;
import uy.edu.ort.battleshipdemo.modelo.excepciones.FlotaVaciaException;

public class ConfiguracionBatalla {
    private final int filas;
    private final int columnas;
    private final List<ItemFlota> items;

    private ConfiguracionBatalla(int filas, int columnas, List<ItemFlota> items) {
        this.filas = filas;
        this.columnas = columnas;
        this.items = new ArrayList<>(items);
        validar();
    }
    public static ConfiguracionBatalla crear(int filas, int columnas, List<ItemFlota> items) {
        return new ConfiguracionBatalla(filas, columnas, items);
    }
    public void validar() {
        if (filas < 6 || filas > 12 || columnas < 6 || columnas > 12) throw new DimensionInvalidaException();
        if (totalCeldasFlota() == 0) throw new FlotaVaciaException();
        if (4 * totalCeldasFlota() >= filas * columnas) throw new DensidadInvalidaException();
    }
    public int totalCeldasFlota() {
        int total = 0;
        for (ItemFlota item : items) total += item.celdasOcupadas();
        return total;
    }
    public double densidad() { return (double) totalCeldasFlota() / (filas * columnas); }
    public int getFilas(){return filas;} public int getColumnas(){return columnas;} public List<ItemFlota> getItems(){return items;}
}
