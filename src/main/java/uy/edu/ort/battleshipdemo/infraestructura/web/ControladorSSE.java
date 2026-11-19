package uy.edu.ort.battleshipdemo.infraestructura.web;

import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@SessionScope
@RequestMapping("/sse")
public class ControladorSSE {
    private final ConexionNavegador conexionNavegador;

    public ControladorSSE(ConexionNavegador conexionNavegador) {
        this.conexionNavegador = conexionNavegador;
    }

    @GetMapping(value = "/registrarSSE", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter registrarSSE() throws IOException {
        SseEmitter emisor = new SseEmitter(30L * 60L * 1000L);
        conexionNavegador.conectar(emisor);
        // Fuerza la apertura de la conexión antes de que la vista pida su estado inicial.
        emisor.send(SseEmitter.event().comment("conectado"));
        return emisor;
    }
}
