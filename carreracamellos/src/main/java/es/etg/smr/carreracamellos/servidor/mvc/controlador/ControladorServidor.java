package es.etg.smr.carreracamellos.servidor.mvc.controlador;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import es.etg.smr.carreracamellos.servidor.mvc.modelo.Jugador;
import es.etg.smr.carreracamellos.servidor.mvc.modelo.Partida;
import es.etg.smr.carreracamellos.servidor.mvc.utilidades.ConexionCliente;
import es.etg.smr.carreracamellos.servidor.mvc.utilidades.LogCamellos;

public class ControladorServidor {
    private static final int PUERTO = 3009;
    private static final int MAX_CAMELLOS = 2;
    private static final int INDICE_JUG1 = 0;
    private static final int INDICE_JUG2 = 1;

    private static final String MJ_ESPERA = ". Esperando a que se unan más jugadores...";
    private static final String FORMATO_SERVIDOR = "Servidor iniciado en el puerto  %s";
    private static final String FORMATO_BIENVENIDA = "Bienvenido %s%s";
    private static final String FORMATO_CONEXION = "Cliente conectado desde: %s%s ";
    private static final String FORMATO_JUGADORES = "JUGADORES: %s;%s";
    private static final String FORMATO_ERROR_JUGADOR = "Error al crear el jugador:  %s";

    public static void iniciarServidor() throws IOException {

        try (ServerSocket server = new ServerSocket(PUERTO)) {
            LogCamellos.info(String.format(FORMATO_SERVIDOR, PUERTO));

            while (true) {
                // Crear la partida
                Partida partida = new Partida(MAX_CAMELLOS);

                for (int i = 0; i < MAX_CAMELLOS; i++) {
                    Socket socket = server.accept();
                    ConexionCliente conexion = new ConexionCliente(socket);
                    
                    String nombreJugador = conexion.leer();

                    LogCamellos.info(
                            String.format(FORMATO_CONEXION, nombreJugador, socket.getInetAddress().getHostAddress()));

                    try {
                        Jugador jugador = new Jugador(nombreJugador, socket);

                        partida.agregar(jugador, i);

                        conexion.enviar(String.format(FORMATO_BIENVENIDA, nombreJugador, MJ_ESPERA));
                        conexion.enviar(nombreJugador);
                    } catch (Exception e) {
                        LogCamellos.error(FORMATO_ERROR_JUGADOR + nombreJugador, e);
                        
                    }
                }

                Jugador jugador1 = (Jugador) partida.getJugadores()[INDICE_JUG1];
                Jugador jugador2 = (Jugador) partida.getJugadores()[INDICE_JUG2];

                ConexionCliente conexion1 = new ConexionCliente(jugador1.getSocket());// Envío a cada cliente los
                                                                                      // nombres de ambos jugadores
                ConexionCliente conexion2 = new ConexionCliente(jugador2.getSocket());

                conexion1.enviar(String.format(FORMATO_JUGADORES, jugador1.getNombre(), jugador2.getNombre()));
                conexion2.enviar(String.format(FORMATO_JUGADORES, jugador2.getNombre(), jugador1.getNombre()));

                Thread hiloPartida = new Thread(partida);
                hiloPartida.start();
            }
        }
    }
}
