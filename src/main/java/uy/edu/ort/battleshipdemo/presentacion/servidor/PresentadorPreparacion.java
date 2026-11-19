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
import uy.edu.ort.battleshipdemo.aplicacion.datos.PreparacionDTO;
import uy.edu.ort.battleshipdemo.aplicacion.datos.CeldaDTO;
import uy.edu.ort.battleshipdemo.aplicacion.datos.BarcoPendienteDTO;
import uy.edu.ort.battleshipdemo.infraestructura.web.ConexionNavegador;
import uy.edu.ort.battleshipdemo.modelo.eventos.Observador;
import uy.edu.ort.battleshipdemo.modelo.eventos.Observable;
import uy.edu.ort.battleshipdemo.modelo.oceano.Coordenada;
import uy.edu.ort.battleshipdemo.modelo.valores.EstadoBatalla;
import uy.edu.ort.battleshipdemo.modelo.valores.Orientacion;

@RestController
@SessionScope
@RequestMapping("/preparacion")
public class PresentadorPreparacion implements Observador {
    private final Fachada fachada;
    private final ConexionNavegador conexionNavegador;
    private int batallaId;
    private int barcoSeleccionado;
    private Orientacion orientacion = Orientacion.HORIZONTAL;
    private boolean observando;

    public PresentadorPreparacion(Fachada fachada, ConexionNavegador conexionNavegador) {
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

    @PostMapping("/seleccionarBarco")
    public Commands seleccionarBarco(@RequestParam int barcoId) {
        barcoSeleccionado = barcoId;
        return refrescar();
    }

    @PostMapping("/elegirOrientacion")
    public Commands elegirOrientacion(@RequestParam Orientacion orientacion) {
        this.orientacion = orientacion;
        return refrescar();
    }

    @PostMapping("/clickPropias")
    public Commands clickPropias(@RequestParam int coordenadaVertical,
            @RequestParam int coordenadaHorizontal, @RequestParam long version) {
        fachada.ubicarBarco(batallaId, barcoSeleccionado,
                new Coordenada(coordenadaVertical, coordenadaHorizontal), orientacion, version);
        return refrescar();
    }

    @PostMapping("/clickEnemigas")
    public Commands clickEnemigas() {
        return Commands.create(new Command("mostrarError", "No puede ubicar barcos en aguas enemigas"));
    }

    @PostMapping("/librarBatalla")
    public Commands librarBatalla(@RequestParam long version) {
        fachada.iniciarCombate(batallaId, version);
        return Commands.create(new Command("navegar", "/batalla.html?batallaId=" + batallaId));
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
        if (batalla.getEstado() != EstadoBatalla.EN_PREPARACION) {
            return Commands.create(new Command("navegar", "/batalla.html?batallaId=" + batallaId));
        }
        boolean seleccionPendiente = false;
        for (BarcoPendienteDTO barco : batalla.getBarcosPendientes()) {
            if (barco.getId() == barcoSeleccionado) seleccionPendiente = true;
        }
        if (!seleccionPendiente) {
            barcoSeleccionado = batalla.getBarcosPendientes().isEmpty()
                    ? 0 : batalla.getBarcosPendientes().get(0).getId();
        }
        PreparacionDTO preparacion = new PreparacionDTO(batalla, barcoSeleccionado, orientacion);
        Commands comandos = Commands.create(new Command("mostrarPreparacion", preparacion));
        agregarComandosTablero(comandos, batalla);
        return comandos;
    }

    private void agregarComandosTablero(Commands comandos, BatallaDTO batalla) {
        String propias = "/preparacion/clickPropias?version=" + batalla.getVersion();
        comandos.add(new Command("inicializar", new InicializarDto(batalla.getFilas(),
                batalla.getColumnas(), propias, "/preparacion/clickEnemigas")));
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
    }
}
