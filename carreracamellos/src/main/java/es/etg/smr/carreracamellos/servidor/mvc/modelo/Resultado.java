package es.etg.smr.carreracamellos.servidor.mvc.modelo;

import java.time.LocalDateTime;

public class Resultado {
    private String ganador;
    private int puntosGanador;
    private String perdedor;
    private int puntosPerdedor;
    private LocalDateTime fecha;

    public Resultado(String ganador, int puntosGanador, String perdedor, int puntosPerdedor) {
        this.ganador = ganador;
        this.puntosGanador = puntosGanador;
        this.perdedor = perdedor;
        this.puntosPerdedor = puntosPerdedor;
        this.fecha = LocalDateTime.now();
    }

    public String getGanador() {
        return ganador;
    }

    public int getPuntosGanador() {
        return puntosGanador;
    }

    public String getPerdedor() {
        return perdedor;
    }

    public int getPuntosPerdedor() {
        return puntosPerdedor;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}

