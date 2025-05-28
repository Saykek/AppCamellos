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

    public static void main(String[] args) throws IOException {
        try (ServerSocket server = new ServerSocket(PUERTO)) {
            LogCamellos.log("Servidor iniciado en el puerto " + PUERTO);

            while (true) {
                // Crear la partida
                Partida partida = new Partida(MAX_CAMELLOS);

                for (int i = 0; i < MAX_CAMELLOS; i++) {
                    Socket socket = server.accept();
                    System.out.println("Cliente conectado desde: " + socket.getInetAddress().getHostAddress()); ////

                    BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);

                    String nombreJugador = entrada.readLine();

                    LogCamellos.log("Jugador conectado: " + nombreJugador);
                    System.out.println("Mensaje recibido del cliente: " + nombreJugador);

                    Jugador jugador = new Jugador(nombreJugador, socket); // creo un camello con el nombre del jugador y el socket
                    partida.agregarJugador(jugador, i); // agrego el jugador a la partida

                    // Enviamos un mensaje al cliente
                    salida.println("Bienvenido " + nombreJugador + ". Esperando a que se unan más jugadores..."); // envío un mensaje de bienvenida al jugador
                }

                // Crear un hilo para ejecutar la partida
                Thread hiloPartida = new Thread(partida);
                hiloPartida.start(); // Inicia la partida en un hilo separado
            }
        }
    }
}
