package uy.edu.ort.battlefield;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uy.edu.ort.battleshipdemo.aplicacion.Fachada;
import uy.edu.ort.battleshipdemo.config.ManejadorGlobalExcepciones;
import uy.edu.ort.battleshipdemo.infraestructura.inicio.PrecargaDatos;
import uy.edu.ort.battleshipdemo.infraestructura.web.ConexionNavegador;
import uy.edu.ort.battleshipdemo.infraestructura.web.ControladorWeb;
import uy.edu.ort.battleshipdemo.presentacion.servidor.PresentadorBatalla;
import uy.edu.ort.battleshipdemo.presentacion.servidor.PresentadorCrearBatalla;
import uy.edu.ort.battleshipdemo.presentacion.servidor.PresentadorPreparacion;
import uy.edu.ort.battleshipdemo.presentacion.servidor.PresentadorSala;

class ControladorWebTests {
    private static int siguienteLugar = 1;
    private MockMvc mvc;

    @BeforeEach
    void preparar() {
        Fachada fachada = Fachada.getInstancia();
        new PrecargaDatos().cargarCatalogo(fachada);
        ConexionNavegador conexion = new ConexionNavegador();
        mvc = MockMvcBuilders.standaloneSetup(
                new PresentadorSala(fachada, conexion),
                new PresentadorCrearBatalla(fachada),
                new PresentadorPreparacion(fachada, conexion),
                new PresentadorBatalla(fachada, conexion),
                new ControladorWeb())
                .setControllerAdvice(new ManejadorGlobalExcepciones())
                .build();
    }

    @Test
    void laSalaUsaUnPresentadorAdministradoPorSesion() throws Exception {
        mvc.perform(post("/sala/iniciar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commands[0].id").value("mostrarSala"));
        mvc.perform(post("/sala/iniciar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commands[0].id").value("mostrarSala"));
        mvc.perform(post("/sala/finalizar")).andExpect(status().isOk());
    }

    @Test
    void creaUnaBatallaYOrdenaNavegarALaPreparacion() throws Exception {
        mvc.perform(post("/nueva-batalla/iniciar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commands[0].id").value("mostrarOpciones"));

        mvc.perform(post("/nueva-batalla/crear")
                .param("lugar", lugar("Web"))
                .param("filas", "6").param("columnas", "6")
                .param("tacticaId", "secuencial").param("cantidad.destructor", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commands[0].id").value("navegar"))
                .andExpect(jsonPath("$.commands[0].parametro").value(
                        org.hamcrest.Matchers.startsWith("/preparacion.html?batallaId=")));
    }

    @Test
    void traduceLasReglasDeNegocioAlContrato299() throws Exception {
        mvc.perform(post("/nueva-batalla/crear")
                .param("lugar", lugar("Invalida"))
                .param("filas", "4").param("columnas", "4")
                .param("tacticaId", "secuencial").param("cantidad.destructor", "1"))
                .andExpect(status().is(299));
    }

    @Test
    void rechazaDimensionesVaciasONoNumericasSinErrorDelServidor() throws Exception {
        for (String filas : new String[] { "", "abc" }) {
            mvc.perform(post("/nueva-batalla/crear")
                    .param("lugar", lugar("Dimension"))
                    .param("filas", filas).param("columnas", "6")
                    .param("tacticaId", "secuencial").param("cantidad.destructor", "1"))
                    .andExpect(status().is(299))
                    .andExpect(content().string("Las dimensiones deben ser números enteros entre 6 y 12"));
        }
    }

    @Test
    void rechazaCantidadesVaciasONoNumericasSinErrorDelServidor() throws Exception {
        for (String cantidad : new String[] { "", "abc" }) {
            mvc.perform(post("/nueva-batalla/crear")
                    .param("lugar", lugar("Cantidad"))
                    .param("filas", "6").param("columnas", "6")
                    .param("tacticaId", "secuencial").param("cantidad.destructor", cantidad))
                    .andExpect(status().is(299))
                    .andExpect(content().string("La cantidad debe ser un número entero entre 0 y 6"));
        }
    }

    @Test
    void publicaLasPaginasSeparadasYLasBibliotecasProvistas() throws Exception {
        ClassLoader recursos = getClass().getClassLoader();
        assertNotNull(recursos.getResource("static/sala.html"));
        assertNotNull(recursos.getResource("static/nueva-batalla.html"));
        assertNotNull(recursos.getResource("static/preparacion.html"));
        assertNotNull(recursos.getResource("static/batalla.html"));
        assertNotNull(recursos.getResource("static/vendor/CommandDispatcher.js"));
        assertNotNull(recursos.getResource("static/vendor/BattleShipBoard.js"));
        mvc.perform(get("/")).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sala.html"));
    }

    private synchronized String lugar(String prefijo) {
        return prefijo + "-" + siguienteLugar++;
    }
}
