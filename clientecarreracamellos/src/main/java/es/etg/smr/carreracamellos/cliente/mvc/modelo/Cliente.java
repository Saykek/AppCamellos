package es.etg.smr.carreracamellos.cliente.mvc.modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import es.etg.smr.carreracamellos.cliente.mvc.vista.ControladorVista;
import javafx.application.Platform;

public class Cliente {
    private static final String HOST_POR_DEFECTO = "localhost";
    private static final int PUERTO_POR_DEFECTO = 3009;

    private String host;
    private int puerto;

    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;
    private ControladorVista controladorVista;

    public Cliente() throws IOException {
        this.host = HOST_POR_DEFECTO;
        this.puerto = PUERTO_POR_DEFECTO;
    }
    public Cliente(String host, int puerto) {
        this.host = host;
        this.puerto = puerto;
    }
    public void setControladorVista(ControladorVista controladorVista) {
        this.controladorVista = controladorVista;
    }
    public void conectar(String nombreJugador) throws IOException {
    socket = new Socket(host, puerto);
    entrada = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
    salida = new PrintWriter(socket.getOutputStream(), true);

    // Envio nombre del jugador justo después de conectarse
    salida.println(nombreJugador);

    // Lanzo un hilo para escuchar respuestas del servidor
    new Thread(() -> {
        try {
            // Leo bienvenida
            String bienvenida = recibirMensaje();
            System.out.println("Servidor: " + bienvenida);
            Platform.runLater(() -> controladorVista.mostrarMensaje(bienvenida));

            // Leo nombres de jugadores
            String nombreJugadores = recibirMensaje();
            System.out.println("Servidor: " + nombreJugadores);
            Platform.runLater(() -> controladorVista.mostrarMensaje("Jugadores conectados: " + nombreJugadores));

            if (nombreJugadores != null && nombreJugadores.contains(";")) {
                String[] nombres = nombreJugadores.split(";");
                Platform.runLater(() -> controladorVista.setNombreJugadores(nombres[0], nombres[1]));
            }
            
            // Leo otros mensajes 
            String mensaje;
            while ((mensaje = recibirMensaje()) != null) {
                final String finalMensaje = mensaje;
             
            if(mensaje.startsWith("PROGRESO: ")) {
                String [] partes = mensaje.substring(9).split(";");
                String nombre = partes[0];
                int puntos = Integer.parseInt(partes[1]);

                Platform.runLater(() -> 
                    controladorVista.actualizarProgresoCamello(nombre, puntos));
                    controladorVista.actualizarProgreso(nombre, puntos);
                   
            }else {
                Platform.runLater(() -> controladorVista.mostrarMensaje(finalMensaje));
            }
            }
            

        } catch (IOException e) {
            Platform.runLater(() -> controladorVista.mostrarMensaje("Error al recibir datos del servidor."));
            e.printStackTrace();
        }
    }).start();

    }    
    public void enviarNombre(String nombre) {
        salida.println(nombre);
    }

    public String recibirMensaje() throws IOException {
        return entrada.readLine();
    }

    public void cerrar() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
