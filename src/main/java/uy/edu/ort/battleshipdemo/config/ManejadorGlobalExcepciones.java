package uy.edu.ort.battleshipdemo.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uy.edu.ort.battleshipdemo.modelo.excepciones.ReglaJuegoException;

@RestControllerAdvice
public class ManejadorGlobalExcepciones {
    @ExceptionHandler(ReglaJuegoException.class)
    public ResponseEntity<String> manejarReglaJuego(ReglaJuegoException excepcion) {
        return ResponseEntity.status(299).body(excepcion.getMessage());
    }
}
