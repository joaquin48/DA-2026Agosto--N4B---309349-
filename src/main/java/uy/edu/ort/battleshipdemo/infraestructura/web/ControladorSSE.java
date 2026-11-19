package uy.edu.ort.battleshipdemo.infraestructura.web;

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
    public SseEmitter registrarSSE() {
        SseEmitter emisor = new SseEmitter(30L * 60L * 1000L);
        conexionNavegador.conectar(emisor);
        return emisor;
    }
}
