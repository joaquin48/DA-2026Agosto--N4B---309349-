package uy.edu.ort.battleshipdemo.presentacion.servidor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.SessionScope;
import uy.edu.ort.battleshipdemo.Command;
import uy.edu.ort.battleshipdemo.Commands;
import uy.edu.ort.battleshipdemo.InicializarDto;
import uy.edu.ort.battleshipdemo.ValorDto;
import uy.edu.ort.battleshipdemo.aplicacion.Fachada;
import uy.edu.ort.battleshipdemo.aplicacion.datos.BatallaDTO;
import uy.edu.ort.battleshipdemo.aplicacion.datos.CeldaDTO;
import uy.edu.ort.battleshipdemo.infraestructura.web.ConexionNavegador;
import uy.edu.ort.battleshipdemo.modelo.eventos.Observador;
import uy.edu.ort.battleshipdemo.modelo.eventos.Observable;
import uy.edu.ort.battleshipdemo.modelo.oceano.Coordenada;

@RestController
@SessionScope
@RequestMapping("/batalla")
public class PresentadorBatalla implements Observador {
    private final Fachada fachada;
    private final ConexionNavegador conexionNavegador;
    private int batallaId;
    private boolean observando;

    public PresentadorBatalla(Fachada fachada, ConexionNavegador conexionNavegador) {
        this.fachada = fachada;
        this.conexionNavegador = conexionNavegador;
    }

    @PostMapping("/iniciar")
    public Commands iniciar(@RequestParam int batallaId) {
        finalizar();
        this.batallaId = batallaId;
        fachada.observarBatalla(batallaId, this);
        observando = true;
        return refrescar();
    }

    @PostMapping("/finalizar")
    public void finalizar() {
        if (observando) {
            fachada.dejarDeObservarBatalla(batallaId, this);
            observando = false;
        }
    }

    @PostMapping("/clickPropias")
    public Commands clickPropias() {
        return Commands.create(new Command("mostrarError", "No se puede disparar en aguas propias"));
    }

    @PostMapping("/clickEnemigas")
    public Commands clickEnemigas(@RequestParam int coordenadaVertical,
            @RequestParam int coordenadaHorizontal, @RequestParam int ronda,
            @RequestParam long version) {
        fachada.jugarRonda(batallaId, new Coordenada(coordenadaVertical, coordenadaHorizontal),
                ronda, version);
        return refrescar();
    }

    @PostMapping("/volver")
    public Commands volver() {
        return Commands.create(new Command("navegar", "/sala.html"));
    }

    @Override
    public void actualizar(Object evento, Observable origen) {
        conexionNavegador.enviar(refrescar());
    }

    private Commands refrescar() {
        BatallaDTO batalla = fachada.obtenerBatalla(batallaId);
        Commands comandos = Commands.create(new Command("mostrarBatalla", batalla));
        String enemigas = "/batalla/clickEnemigas?version=" + batalla.getVersion()
                + "&ronda=" + batalla.getNumeroRonda();
        comandos.add(new Command("inicializar", new InicializarDto(batalla.getFilas(),
                batalla.getColumnas(), "/batalla/clickPropias", enemigas)));
        for (CeldaDTO celda : batalla.getPropias()) {
            comandos.add(new Command("mostrarAguasPropias", new ValorDto(celda.getFila(),
                    celda.getColumna(), celda.getValor())));
        }
        for (CeldaDTO celda : batalla.getEnemigas()) {
            comandos.add(new Command("mostrarAguasEnemigas", new ValorDto(celda.getFila(),
                    celda.getColumna(), celda.getValor())));
        }
        String bajas = batalla.getBajasEnemigas().isEmpty()
                ? "Ninguna" : String.join(" ", batalla.getBajasEnemigas());
        comandos.add(new Command("bajasEnemigas", bajas));
        return comandos;
    }
}
