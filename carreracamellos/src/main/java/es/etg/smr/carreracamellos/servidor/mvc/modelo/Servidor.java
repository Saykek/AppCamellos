package es.etg.smr.carreracamellos.servidor.mvc.modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import es.etg.smr.carreracamellos.servidor.mvc.utilidades.LogCamellos;

public class Servidor {
    private static final int PUERTO = 3009;
    private static final int MAX_CAMELLOS = 2;
    private static final int INDICE_JUG1 = 0;
    private static final int INDICE_JUG2 = 1;

    private static final String MJ_ESPERA = ". Esperando a que se unan más jugadores...";
    private static final String FORMATO_SERVIDOR = "Servidor iniciado en el puerto  %s";
    private static final String FORMATO_BIENVENIDA = "Bienvenido %s%s";
    private static final String FORMATO_CONEXION = "Cliente conectado desde: %s%s ";
    private static final String FORMATO_JUGADORES = "JUGADORES: %s;%s";

    public static void main(String[] args) throws IOException {
        try (ServerSocket server = new ServerSocket(PUERTO)) {
            LogCamellos.info(String.format(FORMATO_SERVIDOR, PUERTO));

            while (true) {
                // Crear la partida
                Partida partida = new Partida(MAX_CAMELLOS);

                for (int i = 0; i < MAX_CAMELLOS; i++) {
                    Socket socket = server.accept();

                    BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);

                    String nombreJugador = entrada.readLine();

                    LogCamellos.info(
                            String.format(FORMATO_CONEXION, nombreJugador, socket.getInetAddress().getHostAddress()));

                    Jugador jugador = new Jugador(nombreJugador, socket); // creo un camello con el nombre del jugador y
                                                                          // el socket
                    partida.agregar(jugador, i); // agrego el jugador a la partida

                    // Envío un mensaje al cliente
                    salida.println(String.format(FORMATO_BIENVENIDA, nombreJugador, MJ_ESPERA)); // envío un mensaje de
                                                                                                 // bienvenida al
                    // jugador
                    salida.println(nombreJugador);
                }

                Jugador jugador1 = (Jugador) partida.getJugadores()[INDICE_JUG1];
                Jugador jugador2 = (Jugador) partida.getJugadores()[INDICE_JUG2];

                // Envío a cada cliente los nombres de ambos jugadores
                PrintWriter salida1 = new PrintWriter(jugador1.getSocket().getOutputStream(), true);
                PrintWriter salida2 = new PrintWriter(jugador2.getSocket().getOutputStream(), true);

                salida1.println(String.format(FORMATO_JUGADORES, jugador1.getNombre(), jugador2.getNombre()));
                salida2.println(String.format(FORMATO_JUGADORES, jugador2.getNombre(), jugador1.getNombre()));

                // Crear un hilo para ejecutar la partida
                Thread hiloPartida = new Thread(partida);
                hiloPartida.start();
            }
        }
    }
}
