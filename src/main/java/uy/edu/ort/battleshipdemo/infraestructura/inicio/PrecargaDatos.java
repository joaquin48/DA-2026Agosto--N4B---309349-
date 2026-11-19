package uy.edu.ort.battleshipdemo.infraestructura.inicio;

import java.util.Map;
import java.util.LinkedHashMap;
import uy.edu.ort.battleshipdemo.aplicacion.Fachada;
import uy.edu.ort.battleshipdemo.aplicacion.datos.BarcoPendienteDTO;
import uy.edu.ort.battleshipdemo.aplicacion.datos.BatallaDTO;
import uy.edu.ort.battleshipdemo.modelo.batalla.SolicitudNuevaBatalla;
import uy.edu.ort.battleshipdemo.modelo.catalogo.TipoBarco;
import uy.edu.ort.battleshipdemo.modelo.oceano.Coordenada;
import uy.edu.ort.battleshipdemo.modelo.tacticas.*;
import uy.edu.ort.battleshipdemo.modelo.valores.EstadoBatalla;
import uy.edu.ort.battleshipdemo.modelo.valores.Orientacion;

public class PrecargaDatos {
    private static final int TAMANO_OCEANO = 6;

    public void cargar(Fachada fachada) { cargarCatalogo(fachada); crearEscenariosPorOperacionesValidas(fachada); }

    public void cargarCatalogo(Fachada fachada) {
        fachada.registrarTipoBarco(new TipoBarco("portaaviones", "Portaaviones", 5));
        fachada.registrarTipoBarco(new TipoBarco("acorazado", "Acorazado", 4));
        fachada.registrarTipoBarco(new TipoBarco("crucero", "Crucero", 3));
        fachada.registrarTipoBarco(new TipoBarco("submarino", "Submarino", 2));
        fachada.registrarTipoBarco(new TipoBarco("destructor", "Destructor", 1));
        fachada.registrarTactica("espiral", "Espiral");
        fachada.registrarTactica("caza-ataque", "Caza y ataque");
        fachada.registrarTactica("secuencial", "Secuencial");
    }

    public void cargarCatalogo() { cargarCatalogo(Fachada.getInstancia()); }

    public void crearEscenariosPorOperacionesValidas(Fachada fachada) {
        preparar(fachada, "Mar de Cristal", "caza-ataque");
        crearEnCombate(fachada);
        crearGanada(fachada);
        crearPerdida(fachada);
    }

    public void crearEscenariosPorOperacionesValidas() { crearEscenariosPorOperacionesValidas(Fachada.getInstancia()); }

    private void crearEnCombate(Fachada fachada) {
        int batallaId = preparar(fachada, "Islas Lejanas", "espiral");
        desplegarAlFinal(fachada, batallaId);
        iniciarCombate(fachada, batallaId);
        jugar(fachada, batallaId, new Coordenada(0, 0));
        verificarEstado(fachada, batallaId, EstadoBatalla.EN_COMBATE);
    }

    private void crearGanada(Fachada fachada) {
        int batallaId = preparar(fachada, "Bahía del Trueno", "secuencial");
        desplegarAlFinal(fachada, batallaId);
        iniciarCombate(fachada, batallaId);

        for (int fila = 0; fila < TAMANO_OCEANO; fila++) {
            for (int columna = 0; columna < TAMANO_OCEANO; columna++) {
                if (fachada.obtenerBatalla(batallaId).getEstado() != EstadoBatalla.EN_COMBATE) break;
                jugar(fachada, batallaId, new Coordenada(fila, columna));
            }
        }
        verificarEstado(fachada, batallaId, EstadoBatalla.GANADA);
    }

    private void crearPerdida(Fachada fachada) {
        int batallaId = preparar(fachada, "Paso de las Sombras", "secuencial");
        ubicar(fachada, batallaId, 2, new Coordenada(0, 0), Orientacion.HORIZONTAL);
        ubicar(fachada, batallaId, 1, new Coordenada(0, 3), Orientacion.HORIZONTAL);
        iniciarCombate(fachada, batallaId);

        jugar(fachada, batallaId, new Coordenada(0, 0));
        jugar(fachada, batallaId, new Coordenada(0, 5));
        jugar(fachada, batallaId, new Coordenada(5, 0));
        jugar(fachada, batallaId, new Coordenada(5, 5));
        verificarEstado(fachada, batallaId, EstadoBatalla.PERDIDA);
    }

    private int preparar(Fachada fachada, String lugar, String tacticaId) {
        return fachada.prepararBatalla(new SolicitudNuevaBatalla(
                lugar, TAMANO_OCEANO, TAMANO_OCEANO, tacticaId, flotaInicial()));
    }

    private Map<String, Integer> flotaInicial() {
        Map<String, Integer> flota = new LinkedHashMap<>();
        flota.put("submarino", 1);
        flota.put("destructor", 1);
        return flota;
    }

    private void desplegarAlFinal(Fachada fachada, int batallaId) {
        ubicar(fachada, batallaId, 2, new Coordenada(5, 2), Orientacion.HORIZONTAL);
        ubicar(fachada, batallaId, 1, new Coordenada(5, 5), Orientacion.HORIZONTAL);
    }

    private void ubicar(Fachada fachada, int batallaId, int tamano,
            Coordenada proa, Orientacion orientacion) {
        BatallaDTO batalla = fachada.obtenerBatalla(batallaId);
        BarcoPendienteDTO barco = null;
        for (BarcoPendienteDTO pendiente : batalla.getBarcosPendientes()) {
            if (pendiente.getTamano() == tamano) barco = pendiente;
        }
        if (barco == null) throw new IllegalStateException("No hay un barco pendiente de tamaño " + tamano);
        fachada.ubicarBarco(batallaId, barco.getId(), proa, orientacion, batalla.getVersion());
    }

    private void iniciarCombate(Fachada fachada, int batallaId) {
        BatallaDTO batalla = fachada.obtenerBatalla(batallaId);
        fachada.iniciarCombate(batallaId, batalla.getVersion());
    }

    private void jugar(Fachada fachada, int batallaId, Coordenada destino) {
        BatallaDTO batalla = fachada.obtenerBatalla(batallaId);
        fachada.jugarRonda(batallaId, destino, batalla.getNumeroRonda(), batalla.getVersion());
    }

    private void verificarEstado(Fachada fachada, int batallaId, EstadoBatalla esperado) {
        EstadoBatalla actual = fachada.obtenerBatalla(batallaId).getEstado();
        if (actual != esperado)
            throw new IllegalStateException("La batalla precargada quedó " + actual + " y se esperaba " + esperado);
    }
}
