package uy.edu.ort.battleshipdemo.modelo.excepciones;

public final class PlanRondaIncompletoException extends BatallaNavalException {
    public PlanRondaIncompletoException() { super("El plan de ronda está incompleto"); }
}
