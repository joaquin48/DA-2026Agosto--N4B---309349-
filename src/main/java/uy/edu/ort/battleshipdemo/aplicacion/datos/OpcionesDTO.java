package uy.edu.ort.battleshipdemo.aplicacion.datos;
import java.util.List;
import uy.edu.ort.battleshipdemo.modelo.catalogo.TipoBarco;
import uy.edu.ort.battleshipdemo.modelo.catalogo.SistemaConfiguracion;
import uy.edu.ort.battleshipdemo.modelo.catalogo.TacticaDisponible;
import java.util.ArrayList;
public class OpcionesDTO {
    private final List<TipoBarco> tipos; private final List<OpcionTacticaDTO> tacticas;
    public OpcionesDTO(SistemaConfiguracion configuracion) {
        this.tipos = configuracion.obtenerTipos();
        List<OpcionTacticaDTO> opciones = new ArrayList<>();
        for (TacticaDisponible tactica : configuracion.opcionesTacticas()) {
            opciones.add(new OpcionTacticaDTO(tactica.getId(), tactica.getNombre()));
        }
        this.tacticas = opciones;
    }
    public List<TipoBarco> getTipos(){return tipos;} public List<OpcionTacticaDTO> getTacticas(){return tacticas;}
}
