package uy.edu.ort.battleshipdemo.modelo.catalogo;

import java.util.ArrayList;
import java.util.List;
import uy.edu.ort.battleshipdemo.modelo.excepciones.TacticaInexistenteException;
import uy.edu.ort.battleshipdemo.modelo.excepciones.TipoBarcoInexistenteException;
import uy.edu.ort.battleshipdemo.modelo.tacticas.Tactica;
import uy.edu.ort.battleshipdemo.modelo.tacticas.FabricaTacticas;

public class SistemaConfiguracion {
    private final List<TipoBarco> tipos = new ArrayList<>();
    private final List<TacticaDisponible> tacticas = new ArrayList<>();

    public void registrarTipo(TipoBarco tipo) {
        tipo.validar();
        for (int posicion = 0; posicion < tipos.size(); posicion++) {
            TipoBarco registrado = tipos.get(posicion);
            if (registrado.getId().equalsIgnoreCase(tipo.getId())) {
                tipos.set(posicion, tipo);
                return;
            }
        }
        tipos.add(tipo);
    }

    public void registrarTactica(String id, String nombre) {
        FabricaTacticas.crear(id);
        TacticaDisponible nueva = new TacticaDisponible(id, nombre);
        for (int posicion = 0; posicion < tacticas.size(); posicion++) {
            TacticaDisponible registrada = tacticas.get(posicion);
            if (registrada.getId().equalsIgnoreCase(id)) {
                tacticas.set(posicion, nueva);
                return;
            }
        }
        tacticas.add(nueva);
    }

    public TipoBarco buscarTipo(String id) {
        for (TipoBarco tipo : tipos) {
            if (tipo.getId().equalsIgnoreCase(id)) return tipo;
        }
        throw new TipoBarcoInexistenteException(id);
    }

    public List<TipoBarco> obtenerTipos() {
        return new ArrayList<>(tipos);
    }

    public List<TacticaDisponible> opcionesTacticas() {
        return new ArrayList<>(tacticas);
    }

    public Tactica crearTactica(String id) {
        for (TacticaDisponible tactica : tacticas) {
            if (tactica.getId().equalsIgnoreCase(id)) return FabricaTacticas.crear(id);
        }
        throw new TacticaInexistenteException(id);
    }
}
