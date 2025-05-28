package es.etg.smr.carreracamellos.servidor.mvc.modelo;

import java.net.Socket;

import es.etg.smr.carreracamellos.servidor.mvc.utilidades.Util;

public class Jugador implements Jugable {
    

    private String nombre;
    private int puntos = 0;
    private  Socket conexion;

    public Jugador(String nombre, Socket conexion) {
        this.nombre = nombre;
        this.conexion = conexion;
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
        if (conexion != null && !conexion.isClosed()) {
            try {
                conexion.close();
            } catch (Exception e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage()); ////////
            }
        }
    }
    public Socket getConexion() {
        return conexion;
    }
    public void setConexion(Socket conexion) {
        this.conexion = conexion;
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
