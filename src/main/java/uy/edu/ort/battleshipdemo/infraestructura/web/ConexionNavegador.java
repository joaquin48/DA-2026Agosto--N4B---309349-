package uy.edu.ort.battleshipdemo.infraestructura.web;

import java.io.IOException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uy.edu.ort.battleshipdemo.Commands;

@Component
@Scope("session")
public class ConexionNavegador {
    private SseEmitter emisor;

    public ConexionNavegador() { }

    public ConexionNavegador(SseEmitter emisor) {
        conectar(emisor);
    }

    public synchronized SseEmitter getEmisor() { return emisor; }
    public synchronized void conectar(SseEmitter nuevoEmisor) {
        if (emisor != null && emisor != nuevoEmisor) emisor.complete();
        emisor = nuevoEmisor;
    }
    public synchronized void enviar(Commands commands) {
        if (emisor == null) return;
        try {
            emisor.send(commands);
        } catch (IOException | IllegalStateException e) {
            emisor = null;
        }
    }
    public synchronized void cerrar() {
        if (emisor != null) emisor.complete();
        emisor = null;
    }
}
