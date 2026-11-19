package uy.edu.ort.battlefield;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import uy.edu.ort.battleshipdemo.aplicacion.Fachada;
import uy.edu.ort.battleshipdemo.aplicacion.datos.BatallaDTO;
import uy.edu.ort.battleshipdemo.aplicacion.datos.ResumenBatallaDTO;
import uy.edu.ort.battleshipdemo.aplicacion.datos.SalaDTO;
import uy.edu.ort.battleshipdemo.infraestructura.inicio.PrecargaDatos;
import uy.edu.ort.battleshipdemo.modelo.valores.EstadoBatalla;

class PrecargaDatosTests {
    @Test
    void creaUnaBatallaValidaEnCadaEstado() {
        Fachada fachada = Fachada.getInstancia();
        new PrecargaDatos().cargar(fachada);

        SalaDTO sala = fachada.obtenerSala();
        ResumenBatallaDTO preparacionResumen = null;
        ResumenBatallaDTO combateResumen = null;
        ResumenBatallaDTO ganadaResumen = null;
        ResumenBatallaDTO perdidaResumen = null;

        for (ResumenBatallaDTO batalla : sala.getBatallas()) {
            if (batalla.getLugar().equals("Mar de Cristal")) preparacionResumen = batalla;
            if (batalla.getLugar().equals("Islas Lejanas")) combateResumen = batalla;
            if (batalla.getLugar().equals("Bahía del Trueno")) ganadaResumen = batalla;
            if (batalla.getLugar().equals("Paso de las Sombras")) perdidaResumen = batalla;
        }

        assertNotNull(preparacionResumen);
        assertNotNull(combateResumen);
        assertNotNull(ganadaResumen);
        assertNotNull(perdidaResumen);
        assertEquals(EstadoBatalla.EN_PREPARACION, preparacionResumen.getEstado());
        assertEquals(EstadoBatalla.EN_COMBATE, combateResumen.getEstado());
        assertEquals(EstadoBatalla.GANADA, ganadaResumen.getEstado());
        assertEquals(EstadoBatalla.PERDIDA, perdidaResumen.getEstado());

        BatallaDTO preparacion = fachada.obtenerBatalla(preparacionResumen.getId());
        BatallaDTO combate = fachada.obtenerBatalla(combateResumen.getId());
        assertEquals(2, preparacion.getBarcosPendientes().size());
        assertEquals(2, combate.getNumeroRonda());
        assertTrue(combate.getBarcosPendientes().isEmpty());
    }
}
