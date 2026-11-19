package uy.edu.ort.battleshipdemo.presentacion.servidor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.SessionScope;
import uy.edu.ort.battleshipdemo.Command;
import uy.edu.ort.battleshipdemo.Commands;
import uy.edu.ort.battleshipdemo.aplicacion.Fachada;
import uy.edu.ort.battleshipdemo.infraestructura.web.ConexionNavegador;
import uy.edu.ort.battleshipdemo.modelo.eventos.Observador;
import uy.edu.ort.battleshipdemo.modelo.eventos.Observable;
import uy.edu.ort.battleshipdemo.modelo.valores.EstadoBatalla;

@RestController
@SessionScope
@RequestMapping("/sala")
public class PresentadorSala implements Observador {
    private final Fachada fachada;
    private final ConexionNavegador conexionNavegador;
    private boolean observando;

    public PresentadorSala(Fachada fachada, ConexionNavegador conexionNavegador) {
        this.fachada = fachada;
        this.conexionNavegador = conexionNavegador;
    }

    @PostMapping("/iniciar")
    public Commands iniciar() {
        finalizar();
        fachada.observarSala(this);
        observando = true;
        return sala();
    }

    @PostMapping("/finalizar")
    public void finalizar() {
        if (observando) {
            fachada.dejarDeObservarSala(this);
            observando = false;
        }
    }

    @PostMapping("/nuevaBatalla")
    public Commands nuevaBatalla() {
        return navegar("/nueva-batalla.html");
    }

    @PostMapping("/entrar")
    public Commands entrar(@RequestParam int batallaId) {
        EstadoBatalla estado = fachada.obtenerBatalla(batallaId).getEstado();
        String pagina = estado == EstadoBatalla.EN_PREPARACION ? "/preparacion.html" : "/batalla.html";
        return navegar(pagina + "?batallaId=" + batallaId);
    }

    @Override
    public void actualizar(Object evento, Observable origen) {
        conexionNavegador.enviar(sala());
    }

    private Commands sala() {
        return Commands.create(new Command("mostrarSala", fachada.obtenerSala()));
    }

    private Commands navegar(String destino) {
        return Commands.create(new Command("navegar", destino));
    }
}
