package uy.edu.ort.battleshipdemo;

public class ValorDto {
    private int coordenadaVertical;
    private int coordenadaHorizontal;
    private String valor;

    public ValorDto(int coordenadaVertical, int coordenadaHorizontal, String valor) {
        this.coordenadaHorizontal = coordenadaHorizontal;
        this.coordenadaVertical = coordenadaVertical;
        this.valor = valor;
    }

    public int getCoordenadaVertical() { return coordenadaVertical; }
    public int getCoordenadaHorizontal() { return coordenadaHorizontal; }
    public String getValor() { return valor; }
}
