package uy.edu.ort.battleshipdemo.modelo.batalla;

import uy.edu.ort.battleshipdemo.modelo.excepciones.PlanRondaIncompletoException;
import uy.edu.ort.battleshipdemo.modelo.tacticas.Tactica;
import uy.edu.ort.battleshipdemo.modelo.valores.EstadoBatalla;
import uy.edu.ort.battleshipdemo.modelo.valores.ResultadoDisparo;

public class PlanRonda {
    private final long versionBase;
    private final Disparo disparoHumano;
    private final Disparo disparoMaquina;
    private final EstadoBatalla estadoFinal;
    private final Tactica tacticaSiguiente;

    public PlanRonda(long versionBase, Disparo disparoHumano, Disparo disparoMaquina,
            EstadoBatalla estadoFinal, Tactica tacticaSiguiente) {
        this.versionBase = versionBase;
        this.disparoHumano = disparoHumano;
        this.disparoMaquina = disparoMaquina;
        this.estadoFinal = estadoFinal;
        this.tacticaSiguiente = tacticaSiguiente;
    }

    public void validarCompleto() {
        if (disparoHumano == null || estadoFinal == null || tacticaSiguiente == null) {
            throw new PlanRondaIncompletoException();
        }

        boolean esVictoria = estadoFinal == EstadoBatalla.GANADA
                && disparoMaquina == null
                && disparoHumano.getResultado() == ResultadoDisparo.HUNDIDO;

        boolean hayRespuestaDeLaMaquina = disparoMaquina != null
                && (estadoFinal == EstadoBatalla.EN_COMBATE
                        || estadoFinal == EstadoBatalla.PERDIDA);

        if (!esVictoria && !hayRespuestaDeLaMaquina) {
            throw new PlanRondaIncompletoException();
        }
    }

    public long getVersionBase() {
        return versionBase;
    }

    public Disparo getDisparoHumano() {
        return disparoHumano;
    }

    public Disparo getDisparoMaquina() {
        return disparoMaquina;
    }

    public EstadoBatalla getEstadoFinal() {
        return estadoFinal;
    }

    public Tactica getTacticaSiguiente() {
        return tacticaSiguiente;
    }
}
