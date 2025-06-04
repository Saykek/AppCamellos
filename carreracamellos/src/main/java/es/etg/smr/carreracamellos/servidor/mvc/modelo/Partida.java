package es.etg.smr.carreracamellos.servidor.mvc.modelo;

import java.util.Random;

import es.etg.smr.carreracamellos.servidor.mvc.utilidades.LogCamellos;

public class Partida implements Runnable {

    private Jugable[] jugadores;

    private static final int MAX_POINTS = 10;
    private static final int MIN_POINTS = 0;
    private static final int INDICE_JUG1 = 0;
    private static final int INDICE_JUG2 = 1;
    private static final int TIEMPO_ESPERA = 300;
    private static final int PUNTOS_GANADOR = 100;

    private static final String MJ_ERROR_ENVIO_RESULTADO = "Error al enviar el resultado de la partida: ";
    private static final String MJ_ERROR_GUARDAR_ENVIAR = "Error al guardar o enviar el resultado de la partida para: ";
    private static final String MJ_ERROR_PAUSAR = "Error al pausar la partida";
    private static final String MJ_ERROR_ENVIO_PROGRESO = "Error al enviar el progreso al jugador ";
    private static final String FORMATO_RESULTADO_GANADOR = "RESULTADO: El jugador %s ha ganado la partida con %d puntos.";
    private static final String FORMATO_INICIO_PARTIDA = "Iniciando partida con los jugadores: %s%s";
    private static final String FORMATO_LOG_PUNTOS = "%s;%d;%d";
    private static final String FORMATO_PROGRESO = "PROGRESO: %s;%d";

    public boolean partidaTerminada = false;

    public Jugable[] getJugadores() {
        return jugadores;
    }

    public Partida(int MAX_JUGADORES) {
        jugadores = new Jugador[MAX_JUGADORES];
    }

    public void agregar(Jugador jugador, int indice) {
        jugadores[indice] = jugador;
    }

    @Override
    public void run() {

        Random random = new Random();

        LogCamellos.info(String.format(FORMATO_INICIO_PARTIDA, jugadores[INDICE_JUG1].getNombre(),
                jugadores[INDICE_JUG2].getNombre()));

        while (!partidaTerminada) {

            for (Jugable jugador : jugadores) {
                int puntosCamello = random.nextInt(MAX_POINTS) + 1;
                int puntosActuales = jugador.getPuntos();

                if (puntosActuales + puntosCamello > PUNTOS_GANADOR) { // PARA LOS 100 PUNTOS MÁXIMOS
                    puntosCamello = PUNTOS_GANADOR - puntosActuales;
                }
                if (puntosCamello <= MIN_POINTS) {
                    jugador.setPuntos(puntosActuales + puntosCamello);
                    partidaTerminada = true;
                    break;
                }

                jugador.incrementarPuntos(puntosCamello);

                LogCamellos.debug(String.format(FORMATO_LOG_PUNTOS,
                        jugador.getNombre(), puntosCamello, jugador.getPuntos()));

                for (Jugable receptor : jugadores) { // ENVIO PROGRESO A TODOS LOS JUGADORES
                    try {

                        receptor.getConexion().enviar(String.format(FORMATO_PROGRESO,
                                jugador.getNombre(), jugador.getPuntos()));

                    } catch (Exception e) {
                        LogCamellos.error(MJ_ERROR_ENVIO_PROGRESO + receptor.getNombre(), e);
                    }
                }

                if (jugador.esGanador()) {
                    partidaTerminada = true;
                    for (Jugable receptor : jugadores) {
                        try {

                            receptor.getConexion().enviar(String.format(FORMATO_RESULTADO_GANADOR,
                                    jugador.getNombre(), jugador.getPuntos()));

                        } catch (Exception e) {
                            LogCamellos.error(MJ_ERROR_ENVIO_RESULTADO + receptor.getNombre(), e);
                        }
                    }
                    try {
                        GestorPartida gestor = new GestorPartida();
                        gestor.procesarResultado(jugadores);
                        break;
                    } catch (Exception e) {
                        LogCamellos.error(MJ_ERROR_GUARDAR_ENVIAR + e.getMessage(), e);
                    }
                }
                try {
                    Thread.sleep(TIEMPO_ESPERA); // Espera 3 segundos antes de continuar
                } catch (InterruptedException e) {
                    LogCamellos.error(MJ_ERROR_PAUSAR, e);
                }

            }

        }
    }
}
