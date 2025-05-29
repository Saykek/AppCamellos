package es.etg.smr.carreracamellos.servidor.mvc.modelo;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import es.etg.smr.carreracamellos.servidor.mvc.documentos.GeneradorDocumentos;
import es.etg.smr.carreracamellos.servidor.mvc.documentos.GeneradorTxt;
import es.etg.smr.carreracamellos.servidor.mvc.utilidades.LogCamellos;

public class Partida implements Runnable {

    private  Jugable[] jugadores;

    private static final int MIN_POINTS = 0;
    private static final int MAX_POINTS = 10;
    private static final int TIEMPO_ESPERA = 300;

    private int puntosCamello = 0;
    public boolean partidaTerminada = false; // Variable para indicar si la partida ha terminado

    public Jugable[] getJugadores() {
        return jugadores;
    }

    public Partida(int MAX_JUGADORES) {
        jugadores = new Jugador[MAX_JUGADORES];
    }

    public void agregarJugador(Jugador jugador, int indice) {
        jugadores[indice] = jugador; // Asigna el jugador al índice correspondiente
    }

    private void guardarResultadoPartida() throws IOException {
        Jugable ganador = jugadores[0].getPuntos() >= jugadores[1].getPuntos() ? jugadores[0] : jugadores[1];
        Jugable perdedor = (ganador == jugadores[0]) ? jugadores[1] : jugadores[0];

        Resultado resultado = new Resultado(
                ganador.getNombre(), ganador.getPuntos(),
                perdedor.getNombre(), perdedor.getPuntos()
        );

        GeneradorDocumentos generar = new GeneradorTxt();

        generar.guardar(resultado);
    }

    @Override
    public void run() {
        
        Random random = new Random();
        // imprimimos un mensaje indicando que la partida ha comenzado.
        System.out.println("Iniciando partida con los jugadores: " + jugadores[0].getNombre() + " y " + jugadores[1].getNombre());

        while (!partidaTerminada) {

            for (Jugable jugador : jugadores) {
                puntosCamello = random.nextInt(MAX_POINTS) + 1;
                jugador.incrementarPuntos(puntosCamello);
                LogCamellos.log(jugador.getNombre() + " avanza " + puntosCamello +
                        " puntos. Total acumulado: " + jugador.getPuntos());

                try {        
                PrintWriter salida = new PrintWriter(jugador.getSocket().getOutputStream(), true);
                salida.println("PROGRESO: " + jugador.getNombre() + ";" + jugador.getPuntos());
                salida.println(); // VER COMO COMO PUEDO DEJAR UN SALTO DE
                
                } catch (IOException e) {
                    System.out.println("Error al enviar el progreso al jugador " + jugador.getNombre() + ": " + e.getMessage());
                }
                try {
                    LogCamellos.log("Enviando progreso al jugador " + jugador.getNombre() + ": " + jugador.getPuntos());
                
                    if (jugador.esGanador()) {
                        PrintWriter salida = new PrintWriter(jugador.getSocket().getOutputStream(), true);
                        salida.print("El jugador " + jugador.getNombre() + " ha ganado la partida con " + jugador.getPuntos() + " puntos.");
                        partidaTerminada = true;
                        //guardarResultadoPartida(); // Me da error IO
                        break;
                }
                } catch (Exception e) {
                    System.out.println("Error al enviar el progreso al jugador " + jugador.getNombre() + ": " + e.getMessage());
                }

                try {
                    Thread.sleep(TIEMPO_ESPERA); // Espera 3 segundos antes de continuar
                } catch (InterruptedException e) {
                    System.out.println("Error al pausar la partida: " + e.getMessage());
                }

            }

            /*for (Jugar jugador : jugadores) {
                
                    jugador.terminarConexion();
            
            }*/
        }
    }
}

