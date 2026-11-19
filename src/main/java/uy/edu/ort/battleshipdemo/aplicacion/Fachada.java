package uy.edu.ort.battleshipdemo.aplicacion;

import uy.edu.ort.battleshipdemo.aplicacion.datos.*;
import uy.edu.ort.battleshipdemo.modelo.batalla.SolicitudNuevaBatalla;
import uy.edu.ort.battleshipdemo.modelo.batalla.Batalla;
import uy.edu.ort.battleshipdemo.modelo.campana.SistemaBatallas;
import uy.edu.ort.battleshipdemo.modelo.catalogo.SistemaConfiguracion;
import uy.edu.ort.battleshipdemo.modelo.catalogo.TipoBarco;
import uy.edu.ort.battleshipdemo.modelo.eventos.Observador;
import uy.edu.ort.battleshipdemo.modelo.oceano.Coordenada;
import uy.edu.ort.battleshipdemo.modelo.oceano.DistribuidorAleatorio;
import uy.edu.ort.battleshipdemo.modelo.valores.Orientacion;
import uy.edu.ort.battleshipdemo.modelo.tacticas.Tactica;

public final class Fachada {
    private static final Fachada INSTANCIA = new Fachada();
    private final SistemaBatallas campana = new SistemaBatallas();
    private final SistemaConfiguracion catalogo = new SistemaConfiguracion();
    private Fachada() { }
    public static Fachada getInstancia() { return INSTANCIA; }
    public SalaDTO obtenerSala() { return new SalaDTO(campana); }
    public OpcionesDTO obtenerOpciones() { return new OpcionesDTO(catalogo); }
    public int prepararBatalla(SolicitudNuevaBatalla solicitud) { return campana.crearBatalla(solicitud, catalogo).getId(); }
    public BatallaDTO obtenerBatalla(int id) {
        Batalla batalla = campana.buscarBatalla(id);
        synchronized (batalla) { return new BatallaDTO(batalla); }
    }
    public void ubicarBarco(int id, int barcoId, Coordenada proa, Orientacion o, long v) { campana.buscarBatalla(id).ubicarBarco(barcoId, proa, o, v); }
    public void iniciarCombate(int id, long v) { campana.buscarBatalla(id).iniciarCombate(new DistribuidorAleatorio(), v); }
    public void jugarRonda(int id, Coordenada destino, int ronda, long v) { campana.buscarBatalla(id).jugarRonda(destino, ronda, v); }
    public void observarSala(Observador o) { campana.agregarObservador(o); }
    public void dejarDeObservarSala(Observador o) { campana.quitarObservador(o); }
    public void observarBatalla(int id, Observador o) { campana.buscarBatalla(id).agregarObservador(o); }
    public void dejarDeObservarBatalla(int id, Observador o) { campana.buscarBatalla(id).quitarObservador(o); }
    public void registrarTipoBarco(TipoBarco tipo) { catalogo.registrarTipo(tipo); }
    public void registrarTactica(String id, String nombre) { catalogo.registrarTactica(id, nombre); }
}
