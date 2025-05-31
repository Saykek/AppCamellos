package es.etg.smr.carreracamellos.cliente.mvc.modelo;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
    private DataInputStream entradaDatos;
    private PrintWriter salida;
    private ControladorVista controladorVista;
    private String nombreJugadorLocal;

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
    this.nombreJugadorLocal = nombreJugador;
    socket = new Socket(host, puerto);
    entradaDatos = new DataInputStream(socket.getInputStream());
    entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    salida = new PrintWriter(socket.getOutputStream(), true);

    // Enviar nombre al servidor
    salida.println(nombreJugador);

    // Hilo para escuchar mensajes del servidor
    new Thread(() -> {
        try {
            String mensaje;
            while ((mensaje = entrada.readLine()) != null) {
                System.out.println("[DEBUG CLIENTE] Mensaje recibido del servidor: " + mensaje);
               
                final String finalMensaje = mensaje;

                if (mensaje.startsWith("PROGRESO:")) {
                    String[] partes = mensaje.substring(9).split(";");
                    String nombre = partes[0];
                    int puntos = Integer.parseInt(partes[1]);

                    Platform.runLater(() -> {
                        System.out.println("Actualizando progreso de " + nombre + " con " + puntos + " puntos.");
                        controladorVista.actualizarProgresoCamello(nombre, puntos);
                        controladorVista.actualizarProgresoTotal(nombre, puntos);
                        
                    });

                } else if (mensaje.startsWith("JUGADORES:")) {
                    String[] jugadores = mensaje.substring(10).split(";");
                    Platform.runLater(() -> controladorVista.setNombreJugadores(jugadores[0], jugadores[1]));

                } else if (mensaje.startsWith("GANADOR: ")) {
                    controladorVista.mostrarBotonCertificado(true);
                    System.out.println("[DEBUG CLIENTE] Mensaje de ganador recibido: " + mensaje);

                } else if (mensaje.startsWith("RESULTADO:")) {
                    // Extraer nombre del ganador
                    String[] partes = mensaje.split(" ");
                    String nombreGanador = partes[3]; // "RESULTADO: El jugador NOMBRE ha..."
                    System.out.println("[DEBUG CLIENTE] Mensaje de resultado recibido: " + mensaje);

                    System.out.println("[DEBUG CLIENTE] Ganador es: " + nombreGanador);
                    System.out.println("[DEBUG CLIENTE] Este cliente es: " + nombreJugador);

                    if (nombreGanador.equalsIgnoreCase(nombreJugadorLocal)) {
                        controladorVista.mostrarMensaje("¡Felicidades " + nombreGanador + "! Has ganado la carrera.");
                        controladorVista.mostrarBotonCertificado(true);
                    } else {
                        controladorVista.mostrarMensaje("El ganador es: " + nombreGanador);
                        controladorVista.mostrarBotonCertificado(false);
                    }                  
                                          

                } else if (mensaje.equals("PDF")) {
                    recibirCertificado();                   

                } else {
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
    public void recibirCertificado() throws IOException {
        System.out.println("[DEBUG CLIENTE] Recibiendo certificado PDF del servidor...");
        int longitud = entradaDatos.readInt(); //LEO EL TAMAÑO
        System.out.println("[DEBUG CLIENTE] Longitud del certificado PDF: " + longitud);
        byte[] datosPdf = new byte[longitud]; // leo los bytes
        entradaDatos.readFully(datosPdf); // leo los bytes

        System.out.println("[DEBUG CLIENTE] Certificado PDF recibido correctamente. Guardando en disco...");

        File carpeta = new File("certificados_recibidos");
        if (!carpeta.exists()) {
            carpeta.mkdir(); // Creo la carpeta si no existe
        }
        File archivoPdf = new File(carpeta, "certificado.pdf"); // Nombre del archivo PDF

        try (FileOutputStream flujo = new FileOutputStream(archivoPdf)) {
            flujo.write(datosPdf); // Escribo los bytes en el archivo
            System.out.println("[DEBUG CLIENTE] Certificado PDF guardado en: " + archivoPdf.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            
            System.out.println("[DEBUG CLIENTE] Error al guardar el certificado PDF: " + e.getMessage());
        }
    }
}
