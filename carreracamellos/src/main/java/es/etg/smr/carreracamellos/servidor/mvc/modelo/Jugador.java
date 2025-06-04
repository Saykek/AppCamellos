package es.etg.smr.carreracamellos.servidor.mvc.modelo;

import java.net.Socket;

import es.etg.smr.carreracamellos.servidor.mvc.utilidades.ConexionCliente;
import es.etg.smr.carreracamellos.servidor.mvc.utilidades.LogCamellos;

public class Jugador implements Jugable {

    private static final int PUNTOS_GANADOR = 100;
    private static final String ERROR_CONEXION = "Error al cerrar la conexión:";

    private String nombre;
    private int puntos = 0;
    private Socket socket;
    private ConexionCliente conexion;

    public Jugador(String nombre, Socket socket) throws Exception  {
        this.nombre = nombre;
        this.socket = socket;
        this.conexion = new ConexionCliente(socket);
        
    }
    @Override
    public ConexionCliente getConexion() {
        return conexion;
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
                LogCamellos.error(ERROR_CONEXION, e);
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
