package uy.edu.ort.battleshipdemo.presentacion.servidor;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.SessionScope;
import uy.edu.ort.battleshipdemo.Command;
import uy.edu.ort.battleshipdemo.Commands;
import uy.edu.ort.battleshipdemo.aplicacion.Fachada;
import uy.edu.ort.battleshipdemo.modelo.batalla.SolicitudNuevaBatalla;
import uy.edu.ort.battleshipdemo.modelo.excepciones.CantidadInvalidaException;
import uy.edu.ort.battleshipdemo.modelo.excepciones.DimensionInvalidaException;

@RestController
@SessionScope
@RequestMapping("/nueva-batalla")
public class PresentadorCrearBatalla {
    private final Fachada fachada;

    public PresentadorCrearBatalla(Fachada fachada) {
        this.fachada = fachada;
    }

    @PostMapping("/iniciar")
    public Commands iniciar() {
        return Commands.create(new Command("mostrarOpciones", fachada.obtenerOpciones()));
    }

    @PostMapping("/crear")
    public Commands crear(@RequestParam Map<String, String> datos) {
        Map<String, Integer> cantidades = new LinkedHashMap<>();
        for (Map.Entry<String, String> dato : datos.entrySet()) {
            if (dato.getKey().startsWith("cantidad.")) {
                cantidades.put(dato.getKey().substring("cantidad.".length()),
                        leerCantidad(dato.getValue()));
            }
        }
        SolicitudNuevaBatalla solicitud = new SolicitudNuevaBatalla(datos.get("lugar"),
                leerDimension(datos.get("filas")), leerDimension(datos.get("columnas")),
                datos.get("tacticaId"), cantidades);
        int batallaId = fachada.prepararBatalla(solicitud);
        return Commands.create(new Command("navegar", "/preparacion.html?batallaId=" + batallaId));
    }

    @PostMapping("/volver")
    public Commands volver() {
        return Commands.create(new Command("navegar", "/sala.html"));
    }

    private int leerDimension(String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException excepcion) {
            throw new DimensionInvalidaException();
        }
    }

    private int leerCantidad(String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException excepcion) {
            throw new CantidadInvalidaException();
        }
    }
}
