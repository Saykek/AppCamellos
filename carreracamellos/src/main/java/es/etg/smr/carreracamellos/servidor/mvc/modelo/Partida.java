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
    private static final int INDICE_JUG1 = 0;
    private static final int INDICE_JUG2 = 1;
    private static final int TIEMPO_ESPERA = 300;
    private static final int INTENTOS_MAX = 10;
    private static final int ESPERA_MS = 100;
    private static final int PUNTOS_GANADOR = 100;
    private static final String RUTA_DOCUMENTOS = System.getProperty("user.dir") + "/documentos_generados/";

    private static final String MJ_ERROR_PDF = "Error mientras se esperaba el archivo PDF: ";
    private static final String MJ_ERROR_ENVIO_RESULTADO = "Error al enviar el resultado de la partida: ";
    private static final String MJ_ERROR_GUARDAR_ENVIAR = "Error al guardar o enviar el resultado de la partida para: ";
    private static final String MJ_ERROR_PAUSAR = "Error al pausar la partida";
    private static final String MJ_ERROR_ENVIO_PROGRESO = "Error al enviar el progreso al jugador ";
    private static final String FORMATO_BUSCANDO = "Buscando PDF en: %s";
    private static final String MJ_PDF_NO_LISTO = "El PDF no está listo después de varios intentos.";
    private static final String MJ_ENVIO_GANADOR = "Enviado mensaje de ganador a: ";
    private static final String FORMATO_GANADOR = "GANADOR: %s%s%d";
    private static final String MJ_GANADOR_PUNTOS = " ha ganado la partida con estos puntos:  ";
    private static final String FORMATO_ESPERA_PDF = "Esperando a que se cree el archivo PDF: %s (%d)";
    private static final String FORMATO_RESULTADO_GANADOR = "RESULTADO: El jugador %s ha ganado la partida con %d puntos.";
    private static final String FORMATO_INICIO_PARTIDA = "Iniciando partida con los jugadores: %s%s";
    private static final String FORMATO_LOG_PUNTOS = "%s;%d;%d";
    private static final String FORMATO_PROGRESO = "PROGRESO: %s;%d";
    private static final String FORMATO_DOC_ENVIADO_OK = "PDF enviado correctamente a: %s (%d)";
    private static final String EXT_PDF = ".pdf";
    private static final String TIPO_DOC = "PDF";

    // private int puntosCamello = 0;
    public boolean partidaTerminada = false;

    public Jugable[] getJugadores() {
        return jugadores;
    }

    public Partida(int MAX_JUGADORES) {
        jugadores = new Jugador[MAX_JUGADORES];
    }

    public void agregar(Jugador jugador, int indice) {
        jugadores[indice] = jugador; // Asigno el jugador al índice correspondiente
    }

    private boolean arhivoListo(Path ruta, int intentosMax, int esperaMs) {
        int intentos = 0;
        while (!Files.exists(ruta) && intentos < intentosMax) {
            try {
                LogCamellos.debug(String.format(FORMATO_ESPERA_PDF, RUTA_DOCUMENTOS, intentos + 1));

                Thread.sleep(esperaMs);
                intentos++;
            } catch (InterruptedException e) {
                LogCamellos.error(MJ_ERROR_PDF + ruta, e);
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return Files.exists(ruta);
    }

    private void guardar() throws IOException {
        Jugable ganador = jugadores[INDICE_JUG1].getPuntos() >= jugadores[INDICE_JUG2].getPuntos()
                ? jugadores[INDICE_JUG1]
                : jugadores[INDICE_JUG2];
        String nombreGanador = ganador.getNombre();
        Socket socketGanador = ganador.getSocket();
        Jugable perdedor = (ganador == jugadores[INDICE_JUG1]) ? jugadores[INDICE_JUG2] : jugadores[INDICE_JUG1];
        String nombrePerdedor = perdedor.getNombre();

        String nombrePdf = nombreGanador + EXT_PDF;
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

            LogCamellos.debug(String.format(FORMATO_BUSCANDO, rutaPdf.toAbsolutePath()));

            boolean pdfListo = arhivoListo(rutaPdf, INTENTOS_MAX, ESPERA_MS); // Espera hasta que el PDF esté listo
            if (!pdfListo) {
                LogCamellos.info(MJ_PDF_NO_LISTO);
                return;
            }

            OutputStream outGanador = socketGanador.getOutputStream();
            DataOutputStream dataOut = new DataOutputStream(outGanador);

            PrintWriter salidaGanador = new PrintWriter(outGanador, true);

            salidaGanador
                    .println(String.format(FORMATO_GANADOR, nombreGanador, MJ_GANADOR_PUNTOS, ganador.getPuntos()));

            LogCamellos.info(MJ_ENVIO_GANADOR + nombreGanador);

            salidaGanador.println(TIPO_DOC); // Envío el tipo de documento

            byte[] bytesPdf = Files.readAllBytes(rutaPdf);
            dataOut.writeInt(bytesPdf.length);// Envío la longitud del PDF
            dataOut.write(bytesPdf); // Envío el contenido del PDF
            dataOut.flush(); // Aseguro que se envíe todo

            LogCamellos.info(String.format(FORMATO_DOC_ENVIADO_OK, nombreGanador, bytesPdf.length));

        } catch (IOException e) {
            LogCamellos.error(MJ_ERROR_GUARDAR_ENVIAR + nombreGanador, e);
        }
    }

    @Override
    public void run() {

        Random random = new Random();

        LogCamellos.info(String.format(FORMATO_INICIO_PARTIDA, jugadores[INDICE_JUG1].getNombre(),
                jugadores[INDICE_JUG2].getNombre()));

        while (!partidaTerminada) {

            for (Jugable jugador : jugadores) {
                int puntosCamello = random.nextInt(MAX_POINTS) + 1;
                int puntosActuales = jugador.getPuntos();
                if (puntosActuales + puntosCamello > PUNTOS_GANADOR) { // PARA LOS 100 PUNTOS MÁXIMOS
                    puntosCamello = PUNTOS_GANADOR - puntosActuales;
                }
                if (puntosCamello <= 0) {
                    jugador.setPuntos(puntosActuales + puntosCamello);
                    partidaTerminada = true;
                    break;
                }

                jugador.incrementarPuntos(puntosCamello);

                LogCamellos.debug(String.format(FORMATO_LOG_PUNTOS,
                        jugador.getNombre(), puntosCamello, jugador.getPuntos()));

                for (Jugable receptor : jugadores) { // ENVIO PROGRESO A TODOS LOS JUGADORES
                    try {
                        PrintWriter salida = new PrintWriter(receptor.getSocket().getOutputStream(), true);

                        salida.println(String.format(FORMATO_PROGRESO,
                                jugador.getNombre(), jugador.getPuntos()));

                    } catch (IOException e) {
                        LogCamellos.error(MJ_ERROR_ENVIO_PROGRESO + receptor.getNombre(), e);
                    }
                }

                if (jugador.esGanador()) {
                    partidaTerminada = true;
                    for (Jugable receptor : jugadores) {
                        try {
                            PrintWriter salida = new PrintWriter(receptor.getSocket().getOutputStream(), true);

                            salida.println(String.format(FORMATO_RESULTADO_GANADOR,
                                    jugador.getNombre(), jugador.getPuntos()));

                        } catch (Exception e) {
                            LogCamellos.error(MJ_ERROR_ENVIO_RESULTADO + receptor.getNombre(), e);
                        }
                    }
                    try {
                        guardar();
                        break;
                    } catch (IOException e) {
                        LogCamellos.error(MJ_ERROR_GUARDAR_ENVIAR + e.getMessage(), e);
                    }
                }
                try {
                    Thread.sleep(TIEMPO_ESPERA); // Espera 3 segundos antes de continuar
                } catch (InterruptedException e) {
                    LogCamellos.error(MJ_ERROR_PAUSAR, e);
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
