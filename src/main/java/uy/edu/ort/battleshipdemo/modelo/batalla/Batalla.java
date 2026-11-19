package uy.edu.ort.battleshipdemo.modelo.batalla;

import java.util.ArrayList;
import java.util.List;
import uy.edu.ort.battleshipdemo.modelo.batalla.estado.EstadoBatallaBase;
import uy.edu.ort.battleshipdemo.modelo.batalla.estado.EstadoCombate;
import uy.edu.ort.battleshipdemo.modelo.batalla.estado.EstadoGanada;
import uy.edu.ort.battleshipdemo.modelo.batalla.estado.EstadoPerdida;
import uy.edu.ort.battleshipdemo.modelo.batalla.estado.EstadoPreparacion;
import uy.edu.ort.battleshipdemo.modelo.catalogo.TipoBarco;
import uy.edu.ort.battleshipdemo.modelo.eventos.Observable;
import uy.edu.ort.battleshipdemo.modelo.excepciones.EstadoBatallaInvalidoException;
import uy.edu.ort.battleshipdemo.modelo.excepciones.FlotaIncompletaException;
import uy.edu.ort.battleshipdemo.modelo.excepciones.PlanRondaIncompletoException;
import uy.edu.ort.battleshipdemo.modelo.excepciones.SolicitudDesactualizadaException;
import uy.edu.ort.battleshipdemo.modelo.flota.Barco;
import uy.edu.ort.battleshipdemo.modelo.oceano.Coordenada;
import uy.edu.ort.battleshipdemo.modelo.oceano.DistribuidorAleatorio;
import uy.edu.ort.battleshipdemo.modelo.tacticas.ContextoTactico;
import uy.edu.ort.battleshipdemo.modelo.tacticas.Tactica;
import uy.edu.ort.battleshipdemo.modelo.valores.Bando;
import uy.edu.ort.battleshipdemo.modelo.valores.EstadoBatalla;
import uy.edu.ort.battleshipdemo.modelo.valores.Orientacion;
import uy.edu.ort.battleshipdemo.modelo.valores.ResultadoDisparo;
import uy.edu.ort.battleshipdemo.modelo.valores.TipoEvento;

public class Batalla extends Observable {
    private final int id;
    private final String lugar;
    private EstadoBatallaBase estado = new EstadoPreparacion();
    private long version;
    private final ConfiguracionBatalla configuracion;
    private final Participante humano;
    private final Participante maquina;
    private Tactica tacticaMaquina;
    private final List<Ronda> historial = new ArrayList<>();

    public Batalla(int id, String lugar, ConfiguracionBatalla configuracion,
            Participante humano, Participante maquina, Tactica tacticaMaquina) {
        this.id = id;
        this.lugar = lugar;
        this.configuracion = configuracion;
        this.humano = humano;
        this.maquina = maquina;
        this.tacticaMaquina = tacticaMaquina;
    }

    public int getId() { return id; }
    public String getLugar() { return lugar; }
    public EstadoBatalla getEstado() { return estado.getNombre(); }
    public long getVersion() { return version; }
    public ConfiguracionBatalla getConfiguracion() { return configuracion; }
    public Participante getHumano() { return humano; }
    public Participante getMaquina() { return maquina; }
    public String getNombreTactica() { return tacticaMaquina.getNombre(); }

    public synchronized void ubicarBarco(int barcoId, Coordenada proa,
            Orientacion orientacion, long versionEsperada) {
        validarVersion(versionEsperada);
        estado.validarUbicarBarco();
        Barco barco = humano.buscarBarco(barcoId);
        humano.getOceano().ubicarBarco(barco, proa, orientacion);
        version++;
        notificar(TipoEvento.BATALLA_MODIFICADA);
    }

    public synchronized void iniciarCombate(DistribuidorAleatorio distribuidor,
            long versionEsperada) {
        validarVersion(versionEsperada);
        estado.validarIniciarCombate();
        if (!humano.flotaCompletaPosicionada()) throw new FlotaIncompletaException();
        maquina.distribuirFlota(distribuidor);
        estado = new EstadoCombate();
        version++;
        notificar(TipoEvento.BATALLA_MODIFICADA);
    }

    public synchronized void jugarRonda(Coordenada destino, int ronda, long versionEsperada) {
        validarVersion(versionEsperada);
        estado.validarJugarRonda();
        if (ronda != historial.size() + 1) throw new SolicitudDesactualizadaException();
        confirmarRonda(planificarRonda(destino));
        notificar(TipoEvento.BATALLA_MODIFICADA);
    }

    public int numeroRonda() {
        return getEstado() == EstadoBatalla.EN_COMBATE
                ? historial.size() + 1 : Math.max(1, historial.size());
    }

    public List<Barco> barcosPendientes() { return humano.barcosPendientes(); }

    public List<String> bajasEnemigas() {
        List<String> bajas = new ArrayList<>();
        for (Ronda ronda : historial) {
            TipoBarco tipo = ronda.getDisparoHumano().getTipoHundido();
            if (tipo != null) bajas.add("{" + tipo.getTamano() + "}");
        }
        return bajas;
    }

    public void validarVersion(long versionEsperada) {
        if (version != versionEsperada) throw new SolicitudDesactualizadaException();
    }

    public void validarEstado(EstadoBatalla esperado) {
        if (getEstado() != esperado) throw new EstadoBatallaInvalidoException(esperado, getEstado());
    }

    protected PlanRonda planificarRonda(Coordenada destino) {
        Disparo disparoHumano = preverDisparo(Bando.HUMANO, maquina, destino);
        Tactica tacticaSiguiente = tacticaMaquina.copiar();
        if (provocaDerrota(disparoHumano, maquina)) {
            return new PlanRonda(version, disparoHumano, null,
                    EstadoBatalla.GANADA, tacticaSiguiente);
        }

        List<Disparo> disparosMaquina = new ArrayList<>();
        for (Ronda ronda : historial) {
            if (ronda.getDisparoMaquina() != null) disparosMaquina.add(ronda.getDisparoMaquina());
        }
        ContextoTactico contexto = new ContextoTactico(configuracion.getFilas(),
                configuracion.getColumnas(), disparosMaquina);
        Coordenada destinoMaquina = tacticaSiguiente.elegirDisparo(contexto);
        Disparo disparoMaquina = preverDisparo(Bando.MAQUINA, humano, destinoMaquina);
        tacticaSiguiente.registrarResultado(disparoMaquina);
        EstadoBatalla estadoFinal = provocaDerrota(disparoMaquina, humano)
                ? EstadoBatalla.PERDIDA : EstadoBatalla.EN_COMBATE;
        return new PlanRonda(version, disparoHumano, disparoMaquina,
                estadoFinal, tacticaSiguiente);
    }

    protected void confirmarRonda(PlanRonda plan) {
        plan.validarCompleto();
        if (plan.getVersionBase() != version) throw new SolicitudDesactualizadaException();
        Ronda ronda = new Ronda(historial.size() + 1,
                plan.getDisparoHumano(), plan.getDisparoMaquina());
        if (!ronda.estaCompletaPara(plan.getEstadoFinal())) throw new PlanRondaIncompletoException();
        maquina.getOceano().recibirDisparo(plan.getDisparoHumano().getDestino());
        if (plan.getDisparoMaquina() != null) {
            humano.getOceano().recibirDisparo(plan.getDisparoMaquina().getDestino());
        }
        historial.add(ronda);
        tacticaMaquina = plan.getTacticaSiguiente();
        estado = crearEstado(plan.getEstadoFinal());
        version++;
    }

    private EstadoBatallaBase crearEstado(EstadoBatalla nombre) {
        if (nombre == EstadoBatalla.EN_PREPARACION) return new EstadoPreparacion();
        if (nombre == EstadoBatalla.EN_COMBATE) return new EstadoCombate();
        if (nombre == EstadoBatalla.GANADA) return new EstadoGanada();
        return new EstadoPerdida();
    }

    private Disparo preverDisparo(Bando tirador, Participante objetivo, Coordenada destino) {
        ResultadoDisparo resultado = objetivo.getOceano().preverDisparo(destino);
        TipoBarco tipoHundido = resultado == ResultadoDisparo.HUNDIDO
                ? objetivo.getOceano().tipoBarcoEn(destino) : null;
        return new Disparo(tirador, destino, resultado, tipoHundido);
    }

    private boolean provocaDerrota(Disparo disparo, Participante objetivo) {
        return disparo.getResultado() == ResultadoDisparo.HUNDIDO && objetivo.integridad() == 1;
    }
}
