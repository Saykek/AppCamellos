package es.etg.smr.carreracamellos.servidor.mvc.modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class ServidorTest {

    @Test
    public void testServidorAceptaClientesYLanzaPartida() throws Exception {
        // Iniciar el servidor en un hilo separado
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Servidor.main(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Espera breve para que el servidor arranque
        Thread.sleep(500);

        // Conectamos los dos clientes
        Socket cliente1 = new Socket("localhost", 3009);
        PrintWriter out1 = new PrintWriter(cliente1.getOutputStream(), true);
        BufferedReader in1 = new BufferedReader(new InputStreamReader(cliente1.getInputStream()));
        out1.println("Jugador1");

        Socket cliente2 = new Socket("localhost", 3009);
        PrintWriter out2 = new PrintWriter(cliente2.getOutputStream(), true);
        BufferedReader in2 = new BufferedReader(new InputStreamReader(cliente2.getInputStream()));
        out2.println("Jugador2");

        // Esperamos a que el servidor envíe los mensajes
        boolean mensajeRecibido1 = contieneMensajeEsperado(in1, "Bienvenido", 3000);
        boolean mensajeRecibido2 = contieneMensajeEsperado(in2, "Bienvenido", 3000);

        assertTrue(mensajeRecibido1, "El cliente 1 no recibió el mensaje de bienvenida");
        assertTrue(mensajeRecibido2, "El cliente 2 no recibió el mensaje de bienvenida");

        // Cerramos sockets
        cliente1.close();
        cliente2.close();
    }

    // Método auxiliar que espera hasta que recibe el mensaje esperado
    private boolean contieneMensajeEsperado(BufferedReader in, String esperado, int timeoutMillis) throws IOException {
        long fin = System.currentTimeMillis() + timeoutMillis;
        String linea;

        while (System.currentTimeMillis() < fin) {
            if (in.ready()) {
                linea = in.readLine();
                if (linea != null && linea.contains(esperado)) {
                    return true;
                }
            } else {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        return false;
    }
}
