package uy.edu.ort.battleshipdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uy.edu.ort.battleshipdemo.aplicacion.Fachada;
import uy.edu.ort.battleshipdemo.infraestructura.inicio.PrecargaDatos;

@SpringBootApplication
public class BattleShipApplication {

	public static void main(String[] args) {
		new PrecargaDatos().cargar(Fachada.getInstancia());
		SpringApplication.run(BattleShipApplication.class, args);
	}
}
