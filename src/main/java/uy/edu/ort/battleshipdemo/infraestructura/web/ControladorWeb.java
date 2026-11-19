package uy.edu.ort.battleshipdemo.infraestructura.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControladorWeb {
    @GetMapping("/")
    public String inicio() {
        return "redirect:/sala.html";
    }
}
