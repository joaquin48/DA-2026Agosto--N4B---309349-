package uy.edu.ort.battleshipdemo.modelo.tacticas;

import uy.edu.ort.battleshipdemo.modelo.excepciones.TacticaInexistenteException;

public class FabricaTacticas {
    public static Tactica crear(String id) {
        if ("espiral".equalsIgnoreCase(id)) return new TacticaEspiral();
        if ("caza-ataque".equalsIgnoreCase(id)) return new TacticaCazaAtaque();
        if ("secuencial".equalsIgnoreCase(id)) return new TacticaSecuencial();
        throw new TacticaInexistenteException(id);
    }

    private FabricaTacticas() { }
}
