package es.etg.smr.carreracamellos.servidor.mvc.modelo;

import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class PartidaTest {

    @Test
    void testPartidaYGeneraResultado() throws Exception {
        final int PUERTO = 3009;

        // Arrancamos un servidor para simular conexiones de clientes
        ServerSocket serverSocket = new ServerSocket(PUERTO);

        // Simulamos dos clientes que se conectan
        Socket cliente1 = new Socket("localhost", PUERTO);
        Socket servidor1 = serverSocket.accept();

        Socket cliente2 = new Socket("localhost", PUERTO);
        Socket servidor2 = serverSocket.accept();

        // Creamos jugadores falsos conectados por sockets
        Jugador jugador1 = new Jugador("Luis", servidor1);
        Jugador jugador2 = new Jugador("Sandra", servidor2);

        Partida partida = new Partida(2);
        partida.agregar(jugador1, 0);
        partida.agregar(jugador2, 1);

        Thread hiloPartida = new Thread(partida);
        hiloPartida.start();

        // Esperamos a que termine (máx 10 segundos)
        hiloPartida.join(20000);

        assertTrue(partida.partidaTerminada, "La partida debería haber terminado.");
        assertTrue(jugador1.getPuntos() > 0 || jugador2.getPuntos() > 0, "Algún jugador debería tener puntos.");

        // Cerramos recursos
        cliente1.close();
        cliente2.close();
        servidor1.close();
        servidor2.close();
        serverSocket.close();
    }
}
