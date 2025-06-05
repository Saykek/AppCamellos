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
    private static final int INDICE_JUG1 = 0;
    private static final int INDICE_JUG2 = 1;
    private static final int PARTES_MJ_RESULTADO = 3;
    private static final int PARTES_MJ_PROGRESO = 0;
    private static final int PARTES_PUNTOS = 1;
    private static final int PARTES_MJ_PROGRESO_SUBSTRING = 9;
    private static final String DIRECTORIO_ACTUAL = System.getProperty("user.dir");

    private static final String MJ_PROGRESO = "PROGRESO:";
    private static final String MJ_JUGADORES = "JUGADORES:";
    private static final String MJ_GANADOR = "GANADOR:";
    private static final String MJ_RESULTADO = "RESULTADO:";
    private static final String MJ_PDF = "PDF";
    private static final String MJ_PRE_GANADOR = "El ganador es: ";
    private static final String MJ_ERROR_DATOS = "Error al recibir datos del servidor.";
    private static final String MJ_RECEPCION_CERTIFICADO = "Recibiendo certificado PDF del servidor...";
    private static final String MJ_CERTIFICADO_OK = "Certificado PDF recibido correctamente. Guardando en disco...";

    private static final String FORMATO_MJ_RECIBIDO = "Mensaje recibido del servidor: %s";
    private static final String FORMATO_ACTUALIZAR_PROGRESO = "Actualizando progreso de %s con %d puntos.";
    private static final String FORMATO_MJ_GANADOR_RECIBIDO = "Mensaje de ganador recibido: %s";
    private static final String FORMATO_MJ_RESULTADO_RECIBIDO = "Mensaje de resultado recibido: %s";
    private static final String FORMATO_GANADOR = "Ganador es: %s";
    private static final String FORMATO_TAMANIO_CERTIFICADO = "Longitud del certificado PDF: %s";
    private static final String FORMATO_MJ_GANADOR = "¡Felicidades %s! Has ganado la carrera.";
    private static final String FORMATO_UBICACION_CERTIFICADO = "Certificado PDF guardado en:  %s";
    private static final String FORMATO_ERROR_GUARDAR_CERTIFICADO = "Error al guardar el certificado PDF: %s";

    private static final String CARPETA_CERTIFICADOS = "certificados_recibidos";
    private static final String NOMBRE_CERTIFICADO = "certificado.pdf";
    private static final String PUNTO_COMA = ";";
    private static final String VACIO = " ";

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

        Platform.runLater(() -> {
            controladorVista.deshabilitarConectar(); 
        });

        // Hilo para escuchar mensajes del servidor
        new Thread(() -> {
            try {
                String mensaje;
                boolean pdfRecibido = false;
                while ((mensaje = entrada.readLine()) != null) {
                    LogCamellos.info(String.format(FORMATO_MJ_RECIBIDO, mensaje));

                    final String finalMensaje = mensaje;

                    if (mensaje.startsWith(MJ_PROGRESO)) {
                        String[] partes = mensaje.substring(PARTES_MJ_PROGRESO_SUBSTRING).split(PUNTO_COMA);
                        String nombre = partes[PARTES_MJ_PROGRESO];
                        int puntos = Integer.parseInt(partes[PARTES_PUNTOS]);

                        Platform.runLater(() -> {
                            LogCamellos.info(String.format(FORMATO_ACTUALIZAR_PROGRESO, nombre, puntos));
                            controladorVista.actualizarProgresoCamello(nombre, puntos);
                            controladorVista.actualizarProgresoTotal(nombre, puntos);

                        });

                    } else if (mensaje.startsWith(MJ_JUGADORES)) {
                        String[] jugadores = mensaje.substring(VALOR_SUBSTRING).split(PUNTO_COMA);
                        Platform.runLater(() -> controladorVista.setNombreJugadores(jugadores[INDICE_JUG1],
                                jugadores[INDICE_JUG2]));

                    } else if (mensaje.startsWith(MJ_GANADOR)) {
                        controladorVista.mostrarBotonCertificado(true);
                        LogCamellos.info(String.format(FORMATO_MJ_GANADOR_RECIBIDO, mensaje));

                    } else if (mensaje.startsWith(MJ_RESULTADO)) {
                        // Extraigo nombre del ganador
                        String[] partes = mensaje.split(VACIO);
                        String nombreGanador = partes[PARTES_MJ_RESULTADO];
                        LogCamellos.info(String.format(FORMATO_MJ_RESULTADO_RECIBIDO, mensaje));

                        LogCamellos.info(String.format(FORMATO_GANADOR, nombreGanador));

                        if (nombreGanador.equalsIgnoreCase(nombreJugadorLocal)) {
                            controladorVista.mostrarMensaje(String.format(FORMATO_MJ_GANADOR, nombreGanador));
                            controladorVista.mostrarBotonCertificado(true);
                        } else {
                            controladorVista.mostrarMensaje(MJ_PRE_GANADOR + nombreGanador);
                            controladorVista.mostrarBotonCertificado(false);
                        }
                        Platform.runLater(() -> controladorVista.habilitarConectar());

                    } else if (mensaje.equals(MJ_PDF)) {
                        recibirCertificado();
                        pdfRecibido = true;

                        try {
                            cerrar();

                        } catch (IOException e) {
                        }

                    } else {
                        Platform.runLater(() -> controladorVista.mostrarMensaje(finalMensaje));
                        
                    }
                }

            } catch (IOException e) {
                if (!socket.isClosed()) {
                    Platform.runLater(() -> controladorVista.mostrarMensaje(MJ_ERROR_DATOS));
                    LogCamellos.error(MJ_ERROR_DATOS + e.getMessage(), e);
                }
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
        LogCamellos.info(MJ_RECEPCION_CERTIFICADO);
        int longitud = entradaDatos.readInt(); // LEO EL TAMAÑO
        LogCamellos.info(String.format(FORMATO_TAMANIO_CERTIFICADO, longitud));

        byte[] datosPdf = new byte[longitud]; // leo los bytes
        entradaDatos.readFully(datosPdf); // leo los bytes

        LogCamellos.info(MJ_CERTIFICADO_OK);

        String rutaBase = DIRECTORIO_ACTUAL + File.separator + CARPETA_CERTIFICADOS; // Obtengo la ruta
                                                                                     // base del proyecto
        File carpeta = new File(rutaBase);
        if (!carpeta.exists()) {
            carpeta.mkdir(); // Creo la carpeta si no existe
        }
        File archivoPdf = new File(carpeta, NOMBRE_CERTIFICADO); // Nombre del archivo PDF

        try (FileOutputStream flujo = new FileOutputStream(archivoPdf)) {
            flujo.write(datosPdf); // Escribo los bytes en el archivo
            LogCamellos.info(String.format(FORMATO_UBICACION_CERTIFICADO, archivoPdf.getAbsolutePath()));
        } catch (IOException e) {
            LogCamellos.error(String.format(FORMATO_ERROR_GUARDAR_CERTIFICADO + e.getMessage()), e);

        }
    }
}
