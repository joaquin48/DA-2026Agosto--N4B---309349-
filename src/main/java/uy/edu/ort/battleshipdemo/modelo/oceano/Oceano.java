package uy.edu.ort.battleshipdemo.modelo.oceano;

import java.util.ArrayList;
import java.util.List;
import uy.edu.ort.battleshipdemo.modelo.catalogo.TipoBarco;
import uy.edu.ort.battleshipdemo.modelo.excepciones.AdyacenciaBarcosException;
import uy.edu.ort.battleshipdemo.modelo.excepciones.BarcoYaPosicionadoException;
import uy.edu.ort.battleshipdemo.modelo.excepciones.CeldaYaDisparadaException;
import uy.edu.ort.battleshipdemo.modelo.excepciones.CoordenadaFueraDeLimitesException;
import uy.edu.ort.battleshipdemo.modelo.excepciones.DistribucionFlotaException;
import uy.edu.ort.battleshipdemo.modelo.excepciones.ReglaJuegoException;
import uy.edu.ort.battleshipdemo.modelo.excepciones.SuperposicionBarcosException;
import uy.edu.ort.battleshipdemo.modelo.flota.Barco;
import uy.edu.ort.battleshipdemo.modelo.flota.Flota;
import uy.edu.ort.battleshipdemo.modelo.valores.Orientacion;
import uy.edu.ort.battleshipdemo.modelo.valores.ResultadoDisparo;

public class Oceano {
    private final int filas;
    private final int columnas;
    private final Celda[][] celdas;

    public Oceano(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        celdas = new Celda[filas][columnas];
        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
                celdas[fila][columna] = new Celda(new Coordenada(fila, columna));
            }
        }
    }

    public int getFilas() { return filas; }
    public int getColumnas() { return columnas; }

    public boolean contiene(Coordenada posicion) {
        return posicion != null && posicion.getFila() >= 0 && posicion.getFila() < filas
                && posicion.getColumna() >= 0 && posicion.getColumna() < columnas;
    }

    public Celda celda(Coordenada posicion) {
        if (!contiene(posicion)) throw new CoordenadaFueraDeLimitesException(posicion);
        return celdas[posicion.getFila()][posicion.getColumna()];
    }

    public void ubicarBarco(Barco barco, Coordenada proa, Orientacion orientacion) {
        List<Celda> destino = resolverDestino(barco, proa, orientacion);
        validarUbicacion(barco, destino);
        for (Celda celda : destino) celda.asignarBarco(barco);
        barco.asignarCeldas(destino);
    }

    public void aplicarDistribucion(List<UbicacionBarco> plan, Flota flota) {
        validarPlanCompleto(plan, flota);
        Oceano validacion = new Oceano(filas, columnas);
        try {
            for (UbicacionBarco ubicacion : plan) {
                Barco original = flota.buscarBarco(ubicacion.getBarcoId());
                Barco copia = new Barco(original.getId(), original.getTipo());
                validacion.ubicarBarco(copia, ubicacion.getProa(), ubicacion.getOrientacion());
            }
        } catch (ReglaJuegoException excepcion) {
            throw new DistribucionFlotaException();
        }
        for (UbicacionBarco ubicacion : plan) {
            ubicarBarco(flota.buscarBarco(ubicacion.getBarcoId()),
                    ubicacion.getProa(), ubicacion.getOrientacion());
        }
    }

    public List<UbicacionBarco> posicionesValidas(Barco barco) {
        List<UbicacionBarco> posiciones = new ArrayList<>();
        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
                Coordenada proa = new Coordenada(fila, columna);
                agregarSiEsValida(posiciones, barco, proa, Orientacion.HORIZONTAL);
                if (barco.getTipo().getTamano() > 1) {
                    agregarSiEsValida(posiciones, barco, proa, Orientacion.VERTICAL);
                }
            }
        }
        return posiciones;
    }

    public ResultadoDisparo preverDisparo(Coordenada posicion) {
        Celda celda = celda(posicion);
        if (celda.estaDisparada()) throw new CeldaYaDisparadaException(posicion);
        return celda.preverResultado();
    }

    public TipoBarco tipoBarcoEn(Coordenada posicion) { return celda(posicion).getTipoBarco(); }

    public ResultadoDisparo recibirDisparo(Coordenada posicion) {
        Celda celda = celda(posicion);
        if (celda.estaDisparada()) throw new CeldaYaDisparadaException(posicion);
        return celda.recibirDisparo();
    }

    public void validarUbicacion(Barco barco, List<Celda> destino) {
        if (barco.estaPosicionado()) throw new BarcoYaPosicionadoException();
        for (Celda celda : destino) if (celda.estaOcupada()) throw new SuperposicionBarcosException();
        if (tieneVecinosOcupados(destino)) throw new AdyacenciaBarcosException();
    }

    public boolean tieneVecinosOcupados(List<Celda> destino) {
        for (Celda celdaDestino : destino) {
            for (Celda celdaOceano : getCeldas()) {
                if (celdaOceano.getPosicion().esAdyacenteA(celdaDestino.getPosicion())
                        && celdaOceano.estaOcupada() && !destino.contains(celdaOceano)) return true;
            }
        }
        return false;
    }

    public List<Celda> getCeldas() {
        List<Celda> resultado = new ArrayList<>();
        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) resultado.add(celdas[fila][columna]);
        }
        return resultado;
    }

    private List<Celda> resolverDestino(Barco barco, Coordenada proa, Orientacion orientacion) {
        List<Celda> destino = new ArrayList<>();
        for (int i = 0; i < barco.getTipo().getTamano(); i++) {
            int desplazamientoFila = orientacion == Orientacion.VERTICAL ? i : 0;
            int desplazamientoColumna = orientacion == Orientacion.HORIZONTAL ? i : 0;
            destino.add(celda(proa.desplazar(desplazamientoFila, desplazamientoColumna)));
        }
        return destino;
    }

    private void agregarSiEsValida(List<UbicacionBarco> posiciones, Barco barco,
            Coordenada proa, Orientacion orientacion) {
        try {
            validarUbicacion(barco, resolverDestino(barco, proa, orientacion));
            posiciones.add(new UbicacionBarco(barco.getId(), proa, orientacion));
        } catch (CoordenadaFueraDeLimitesException | SuperposicionBarcosException
                | AdyacenciaBarcosException excepcion) {
            // Se prueba la siguiente posición.
        }
    }

    private void validarPlanCompleto(List<UbicacionBarco> plan, Flota flota) {
        if (plan == null) throw new DistribucionFlotaException();
        for (Celda celda : getCeldas()) if (celda.estaOcupada()) throw new DistribucionFlotaException();
        List<Barco> pendientes = flota.pendientesPorTamano();
        if (plan.size() != pendientes.size()) throw new DistribucionFlotaException();
        for (Barco barco : pendientes) {
            int apariciones = 0;
            for (UbicacionBarco ubicacion : plan) {
                if (ubicacion.getBarcoId() == barco.getId()) apariciones++;
            }
            if (apariciones != 1) throw new DistribucionFlotaException();
        }
    }
}
