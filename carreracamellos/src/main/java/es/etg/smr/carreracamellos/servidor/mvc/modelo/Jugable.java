package es.etg.smr.carreracamellos.servidor.mvc.modelo;

public interface Jugable {
    public boolean esGanador();
    public void incrementarPuntos(int puntos);
    public String getNombre();
    public void terminarConexion();
    public int getPuntos();
}
