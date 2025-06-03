package es.etg.smr.carreracamellos.servidor.mvc.modelo;

import java.net.Socket;

public interface Jugable {
    public boolean esGanador();
    public void incrementarPuntos(int puntos);
    public String getNombre();
    public void terminarConexion();
    public int getPuntos();
    public void setPuntos(int puntos);
    public Socket getSocket(); 
}
