package uy.edu.ort.battleshipdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uy.edu.ort.battleshipdemo.aplicacion.Fachada;

@Configuration
public class ConfigAppBatallaNaval {
    @Bean
    public Fachada fachada() {
        return Fachada.getInstancia();
    }
}
