package es.etg.smr.carreracamellos.servidor.mvc.modelo;

import java.net.Socket;

import es.etg.smr.carreracamellos.servidor.mvc.utilidades.LogCamellos;

public class Jugador implements Jugable {

    private static final int PUNTOS_GANADOR = 100;

    private String nombre;
    private int puntos = 0;
    private Socket socket;

    public Jugador(String nombre, Socket socket) {
        this.nombre = nombre;
        this.socket = socket;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    @Override
    public void terminarConexion() {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (Exception e) {
                LogCamellos.error("Error al cerrar la conexión: ", e);
            }
        }
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public boolean esGanador() {
        return puntos >= PUNTOS_GANADOR;
    }

    @Override
    public void incrementarPuntos(int puntos) {
        this.puntos += puntos;
    }
}
