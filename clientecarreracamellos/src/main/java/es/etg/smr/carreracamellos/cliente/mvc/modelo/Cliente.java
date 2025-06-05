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
import es.etg.smr.carreracamellos.cliente.utilidades.LogCamellos;
import javafx.application.Platform;

public class Cliente {
    private static final String HOST_POR_DEFECTO = "localhost";
    private static final int PUERTO_POR_DEFECTO = 3009;
    private static final int VALOR_SUBSTRING = 10;

    private static final String MSG_PROGRESO = "PROGRESO:";
    private static final String MSG_JUGADORES = "JUGADORES:";
    private static final String MSG_GANADOR = "GANADOR:";
    private static final String MSG_RESULTADO = "RESULTADO:";
    private static final String MSG_PDF = "PDF";
    private static final String MSG_PRE_FELICIDADES = "¡Felicidades ";
    private static final String MSG_POST_FELICIDADES = "! Has ganado la carrera.";
    private static final String MSG_PRE_GANADOR = "El ganador es: ";

    private static final String CARPETA_CERTIFICADOS = "certificados_recibidos";
    private static final String NOMBRE_CERTIFICADO = "certificado.pdf";
   

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
                    LogCamellos.info("[CLIENTE] Mensaje recibido del servidor: " + mensaje);

                    final String finalMensaje = mensaje;

                    if (mensaje.startsWith(MSG_PROGRESO)) {
                        String[] partes = mensaje.substring(9).split(";");
                        String nombre = partes[0];
                        int puntos = Integer.parseInt(partes[1]);

                        Platform.runLater(() -> {
                            LogCamellos.info("Actualizando progreso de " + nombre + " con " + puntos + " puntos.");
                            controladorVista.actualizarProgresoCamello(nombre, puntos);
                            controladorVista.actualizarProgresoTotal(nombre, puntos);

                        });

                    } else if (mensaje.startsWith(MSG_JUGADORES)) {
                        String[] jugadores = mensaje.substring(VALOR_SUBSTRING).split(";");
                        Platform.runLater(() -> controladorVista.setNombreJugadores(jugadores[0], jugadores[1]));

                    } else if (mensaje.startsWith(MSG_GANADOR)) {
                        controladorVista.mostrarBotonCertificado(true);
                        LogCamellos.info("[CLIENTE] Mensaje de ganador recibido: " + mensaje);

                    } else if (mensaje.startsWith(MSG_RESULTADO)) {
                        // Extraigo nombre del ganador
                        String[] partes = mensaje.split(" ");
                        String nombreGanador = partes[3];
                        LogCamellos.info("[CLIENTE] Mensaje de resultado recibido: " + mensaje);

                        LogCamellos.info("[CLIENTE] Ganador es: " + nombreGanador);
                        LogCamellos.info("[CLIENTE] Este cliente es: " + nombreJugador);

                        if (nombreGanador.equalsIgnoreCase(nombreJugadorLocal)) {
                            controladorVista.mostrarMensaje(MSG_PRE_FELICIDADES + nombreGanador + MSG_POST_FELICIDADES);
                            controladorVista.mostrarBotonCertificado(true);
                        } else {
                            controladorVista.mostrarMensaje(MSG_PRE_GANADOR + nombreGanador);
                            controladorVista.mostrarBotonCertificado(false);
                        }

                    } else if (mensaje.equals(MSG_PDF)) {
                        recibirCertificado();

                    } else {
                        Platform.runLater(() -> controladorVista.mostrarMensaje(finalMensaje));
                    }
                }

            } catch (IOException e) {
                Platform.runLater(() -> controladorVista.mostrarMensaje("Error al recibir datos del servidor."));
                LogCamellos.error("Error al recibir datos del servidor: " + e.getMessage(), e);

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
        LogCamellos.info("[CLIENTE] Recibiendo certificado PDF del servidor...");
        int longitud = entradaDatos.readInt(); // LEO EL TAMAÑO
        LogCamellos.info("[CLIENTE] Longitud del certificado PDF: " + longitud);

        byte[] datosPdf = new byte[longitud]; // leo los bytes
        entradaDatos.readFully(datosPdf); // leo los bytes

        LogCamellos.info("[CLIENTE] Certificado PDF recibido correctamente. Guardando en disco...");

        String rutaBase = System.getProperty("user.dir")+ File.separator + CARPETA_CERTIFICADOS; // Obtengo la ruta base del proyecto
        File carpeta = new File(rutaBase);
        if (!carpeta.exists()) {
            carpeta.mkdir(); // Creo la carpeta si no existe
        }
        File archivoPdf = new File(carpeta, NOMBRE_CERTIFICADO); // Nombre del archivo PDF

        try (FileOutputStream flujo = new FileOutputStream(archivoPdf)) {
            flujo.write(datosPdf); // Escribo los bytes en el archivo
            LogCamellos.info("[CLIENTE] Certificado PDF guardado en: " + archivoPdf.getAbsolutePath());
        } catch (IOException e) {
            LogCamellos.error("[CLIENTE] Error al guardar el certificado PDF: " + e.getMessage(), e);

        }
    }
}
