package uy.edu.ort.battleshipdemo.modelo.campana;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import uy.edu.ort.battleshipdemo.modelo.batalla.Batalla;
import uy.edu.ort.battleshipdemo.modelo.batalla.ConfiguracionBatalla;
import uy.edu.ort.battleshipdemo.modelo.batalla.ItemFlota;
import uy.edu.ort.battleshipdemo.modelo.batalla.Participante;
import uy.edu.ort.battleshipdemo.modelo.batalla.SolicitudNuevaBatalla;
import uy.edu.ort.battleshipdemo.modelo.catalogo.SistemaConfiguracion;
import uy.edu.ort.battleshipdemo.modelo.eventos.Observable;
import uy.edu.ort.battleshipdemo.modelo.eventos.Observador;
import uy.edu.ort.battleshipdemo.modelo.excepciones.BatallaInexistenteException;
import uy.edu.ort.battleshipdemo.modelo.excepciones.LugarDuplicadoException;
import uy.edu.ort.battleshipdemo.modelo.flota.Flota;
import uy.edu.ort.battleshipdemo.modelo.oceano.Oceano;
import uy.edu.ort.battleshipdemo.modelo.tacticas.Tactica;
import uy.edu.ort.battleshipdemo.modelo.valores.Bando;
import uy.edu.ort.battleshipdemo.modelo.valores.EstadoBatalla;
import uy.edu.ort.battleshipdemo.modelo.valores.TipoEvento;

public class SistemaBatallas extends Observable implements Observador {
    private final List<Batalla> batallas = new ArrayList<>();
    private final Random random = new Random();
    private int siguienteId = 1;

    public synchronized Batalla crearBatalla(SolicitudNuevaBatalla solicitud,
            SistemaConfiguracion configuracion) {
        String lugar = resolverLugar(solicitud.getLugar());
        List<ItemFlota> items = crearItems(solicitud, configuracion);
        ConfiguracionBatalla configuracionBatalla = ConfiguracionBatalla.crear(
                solicitud.getFilas(), solicitud.getColumnas(), items);
        Tactica tactica = configuracion.crearTactica(solicitud.getTacticaId());

        Participante humano = crearParticipante(Bando.HUMANO, configuracionBatalla);
        Participante maquina = crearParticipante(Bando.MAQUINA, configuracionBatalla);
        Batalla creada = new Batalla(siguienteId++, lugar, configuracionBatalla,
                humano, maquina, tactica);
        creada.agregarObservador(this);
        batallas.add(creada);
        notificar(TipoEvento.BATALLA_CREADA);
        return creada;
    }

    public synchronized Batalla buscarBatalla(int id) {
        for (Batalla batalla : batallas) if (batalla.getId() == id) return batalla;
        throw new BatallaInexistenteException(id);
    }

    public synchronized List<Batalla> listarBatallas() {
        return new ArrayList<>(batallas);
    }

    public synchronized int contarGanadas() { return contarPorEstado(EstadoBatalla.GANADA); }
    public synchronized int contarPerdidas() { return contarPorEstado(EstadoBatalla.PERDIDA); }

    public synchronized double calcularExito() {
        return batallas.isEmpty() ? 0 : 100.0 * contarPorEstado(EstadoBatalla.GANADA) / batallas.size();
    }

    public void actualizar(Object evento, Observable origen) {
        notificar(TipoEvento.CAMPANA_MODIFICADA);
    }

    public synchronized String resolverLugar(String nombre) {
        if (nombre != null && !nombre.isBlank()) {
            validarLugarUnico(nombre);
            return nombre;
        }
        String generado;
        do {
            generado = generarLugar();
        } while (existeLugar(generado));
        return generado;
    }

    public synchronized void validarLugarUnico(String nombre) {
        if (existeLugar(nombre)) throw new LugarDuplicadoException(nombre);
    }

    private String generarLugar() {
        String lugar = "";
        for (int i = 0; i < 6; i++) lugar += (char) ('A' + random.nextInt(26));
        return lugar;
    }

    private boolean existeLugar(String nombre) {
        for (Batalla batalla : batallas) {
            if (batalla.getLugar().equalsIgnoreCase(nombre)) return true;
        }
        return false;
    }

    private List<ItemFlota> crearItems(SolicitudNuevaBatalla solicitud,
            SistemaConfiguracion configuracion) {
        List<ItemFlota> items = new ArrayList<>();
        for (Map.Entry<String, Integer> cantidad : solicitud.getCantidadesPorTipo().entrySet()) {
            items.add(new ItemFlota(configuracion.buscarTipo(cantidad.getKey()), cantidad.getValue()));
        }
        return items;
    }

    private Participante crearParticipante(Bando bando, ConfiguracionBatalla configuracion) {
        return new Participante(bando, Flota.crearDesde(configuracion),
                new Oceano(configuracion.getFilas(), configuracion.getColumnas()));
    }

    private int contarPorEstado(EstadoBatalla estado) {
        int cantidad = 0;
        for (Batalla batalla : batallas) if (batalla.getEstado() == estado) cantidad++;
        return cantidad;
    }
}
