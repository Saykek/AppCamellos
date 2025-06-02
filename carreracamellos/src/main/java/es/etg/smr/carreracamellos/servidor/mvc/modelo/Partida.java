package es.etg.smr.carreracamellos.servidor.mvc.modelo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import es.etg.smr.carreracamellos.servidor.mvc.documentos.GeneradorCertificadoMd;
import es.etg.smr.carreracamellos.servidor.mvc.documentos.GeneradorDocumentos;
import es.etg.smr.carreracamellos.servidor.mvc.documentos.GeneradorPDFDocker;
import es.etg.smr.carreracamellos.servidor.mvc.documentos.GuardarHistorial;
import es.etg.smr.carreracamellos.servidor.mvc.utilidades.LogCamellos;

public class Partida implements Runnable {

    private Jugable[] jugadores;

    private static final int MAX_POINTS = 10;
    private static final int TIEMPO_ESPERA = 300;
    private static final int INTENTOS_MAX = 10;
    private static final int ESPERA_MS = 100;
    private static final String RUTA_DOCUMENTOS = System.getProperty("user.dir")
            + "/src/main/java/es/etg/smr/carreracamellos/servidor/mvc/documentos/envios/";

    private int puntosCamello = 0;
    public boolean partidaTerminada = false;

    public Jugable[] getJugadores() {
        return jugadores;
    }

    public Partida(int MAX_JUGADORES) {
        jugadores = new Jugador[MAX_JUGADORES];
    }

    public void agregarJugador(Jugador jugador, int indice) {
        jugadores[indice] = jugador; // Asigno el jugador al índice correspondiente
    }

    private boolean arhivoListo(Path ruta, int intentosMax, int esperaMs) {
        int intentos = 0;
        while (!Files.exists(ruta) && intentos < intentosMax) {
            try {
                LogCamellos.debug("Esperando a que se cree el archivo PDF: " + RUTA_DOCUMENTOS + " (intento "
                        + (intentos + 1) + ")");
                Thread.sleep(esperaMs);
                intentos++;
            } catch (InterruptedException e) {
                LogCamellos.error("Error mientras se esperaba el archivo PDF: " + ruta, e);
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return Files.exists(ruta);
    }

    private void guardarResultado() throws IOException {
        Jugable ganador = jugadores[0].getPuntos() >= jugadores[1].getPuntos() ? jugadores[0] : jugadores[1];
        String nombreGanador = ganador.getNombre();
        Socket socketGanador = ganador.getSocket();
        Jugable perdedor = (ganador == jugadores[0]) ? jugadores[1] : jugadores[0];
        String nombrePerdedor = perdedor.getNombre();

        String nombrePdf = nombreGanador + ".pdf";
        Path rutaPdf = Paths.get(RUTA_DOCUMENTOS, nombrePdf);

        Resultado resultado = new Resultado(
                nombreGanador, ganador.getPuntos(),
                nombrePerdedor, perdedor.getPuntos());

        try {
            // Genero el historial de la partida
            GeneradorDocumentos historial = new GuardarHistorial();
            historial.generar(resultado);

            // Genero el PDF de la partida
            GeneradorDocumentos certificado = new GeneradorCertificadoMd();
            certificado.generar(resultado);
            GeneradorDocumentos pdf = new GeneradorPDFDocker();
            pdf.generar(resultado);

            LogCamellos.debug("Buscando PDF en: " + rutaPdf.toAbsolutePath());

            boolean pdfListo = arhivoListo(rutaPdf, INTENTOS_MAX, ESPERA_MS); // Espera hasta que el PDF esté listo
            if (!pdfListo) {
                LogCamellos.info("El PDF no está listo después de varios intentos.");
                return;
            }

            OutputStream outGanador = socketGanador.getOutputStream();
            DataOutputStream dataOut = new DataOutputStream(outGanador);

            PrintWriter salidaGanador = new PrintWriter(outGanador, true);
            salidaGanador.println(
                    "GANADOR: " + nombreGanador + " ha ganado la partida con " + ganador.getPuntos() + " puntos.");
            LogCamellos.info("Enviado mensaje de ganador a: " + nombreGanador);

            salidaGanador.println("PDF"); // Envío el tipo de documento
            LogCamellos.debug("Preparando envío del PDF a: " + nombreGanador);

            byte[] bytesPdf = Files.readAllBytes(rutaPdf);
            dataOut.writeInt(bytesPdf.length);// Envío la longitud del PDF
            dataOut.write(bytesPdf); // Envío el contenido del PDF
            dataOut.flush(); // Aseguro que se envíe todo

            LogCamellos.info("PDF enviado correctamente a: " + nombreGanador + " (" + bytesPdf.length + " bytes)");

        } catch (IOException e) {
            LogCamellos.error("Error al guardar o enviar el resultado de la partida para: " + nombreGanador, e);
        }
    }

    @Override
    public void run() {

        Random random = new Random();

        LogCamellos.info(
                "Iniciando partida con los jugadores: " + jugadores[0].getNombre() + " y " + jugadores[1].getNombre());

        while (!partidaTerminada) {

            for (Jugable jugador : jugadores) {
                puntosCamello = random.nextInt(MAX_POINTS) + 1;
                jugador.incrementarPuntos(puntosCamello);

                LogCamellos.debug(jugador.getNombre() + " avanza " + puntosCamello +
                        " puntos. Total acumulado: " + jugador.getPuntos());
                for (Jugable receptor : jugadores) { // ENVIO PROGRESO A TODOS LOS JUGADORES
                    try {
                        PrintWriter salida = new PrintWriter(receptor.getSocket().getOutputStream(), true);
                        salida.println("PROGRESO: " + jugador.getNombre() + ";" + jugador.getPuntos());

                        LogCamellos.debug("Enviado progreso de " + jugador.getNombre() + " a " + receptor.getNombre());

                    } catch (IOException e) {
                        LogCamellos.error("Error al enviar el progreso al jugador " + receptor.getNombre(), e);
                    }
                }

                if (jugador.esGanador()) {
                    partidaTerminada = true;
                    for (Jugable receptor : jugadores) {
                        try {
                            PrintWriter salida = new PrintWriter(receptor.getSocket().getOutputStream(), true);
                            salida.println("RESULTADO: El jugador " + jugador.getNombre() + " ha ganado la partida con "
                                    + jugador.getPuntos() + " puntos.");

                            LogCamellos.debug("Resultado enviado a " + receptor.getNombre());

                        } catch (Exception e) {
                            LogCamellos.error("Error al enviar el resultado al jugador " + receptor.getNombre(), e);
                        }
                    }
                    try {
                        guardarResultado();
                        break;
                    } catch (IOException e) {
                        System.out.println("Error al guardar el resultado de la partida: " + e.getMessage());
                    }
                }
                try {
                    Thread.sleep(TIEMPO_ESPERA); // Espera 3 segundos antes de continuar
                } catch (InterruptedException e) {
                    LogCamellos.error("Error al pausar la partida", e);
                }

            }

            /*
             * for (Jugar jugador : jugadores) {
             * 
             * jugador.terminarConexion();
             * 
             * }
             */
        }
    }
}
