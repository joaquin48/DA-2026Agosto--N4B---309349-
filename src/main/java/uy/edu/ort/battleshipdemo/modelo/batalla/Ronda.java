package uy.edu.ort.battleshipdemo.modelo.batalla;
import uy.edu.ort.battleshipdemo.modelo.valores.EstadoBatalla;
public class Ronda {
    private final int numero; private final Disparo disparoHumano; private final Disparo disparoMaquina;
    public Ronda(int numero,Disparo disparoHumano,Disparo disparoMaquina){this.numero=numero;this.disparoHumano=disparoHumano;this.disparoMaquina=disparoMaquina;}
    public boolean estaCompleta(){return disparoHumano!=null&&disparoMaquina!=null;}
    public boolean estaCompletaPara(EstadoBatalla estadoFinal){return estaCompleta()||disparoHumano!=null&&disparoMaquina==null&&estadoFinal==EstadoBatalla.GANADA;}
    public int getNumero(){return numero;} public Disparo getDisparoHumano(){return disparoHumano;} public Disparo getDisparoMaquina(){return disparoMaquina;}
}
