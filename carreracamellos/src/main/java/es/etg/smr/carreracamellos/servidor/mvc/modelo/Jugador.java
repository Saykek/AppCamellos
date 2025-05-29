package es.etg.smr.carreracamellos.servidor.mvc.modelo;

import java.net.Socket;

import es.etg.smr.carreracamellos.servidor.mvc.utilidades.Util;

public class Jugador implements Jugable {
    

    private String nombre;
    private int puntos = 0;
    private  Socket socket;

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
                System.out.println("Error al cerrar la conexión: " + e.getMessage()); ////////
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
        return puntos >= Util.PUNTOS_GANADOR;
    }

    @Override
    public void incrementarPuntos(int puntos) {
        this.puntos += puntos;
    }
}
