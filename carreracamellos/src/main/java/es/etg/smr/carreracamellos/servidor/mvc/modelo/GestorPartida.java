package es.etg.smr.carreracamellos.servidor.mvc.modelo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import es.etg.smr.carreracamellos.servidor.mvc.documentos.GeneradorCertificadoMd;
import es.etg.smr.carreracamellos.servidor.mvc.documentos.GeneradorDocumentos;
import es.etg.smr.carreracamellos.servidor.mvc.documentos.GeneradorPDFDocker;
import es.etg.smr.carreracamellos.servidor.mvc.documentos.GuardarHistorial;
import es.etg.smr.carreracamellos.servidor.mvc.utilidades.ConexionCliente;
import es.etg.smr.carreracamellos.servidor.mvc.utilidades.LogCamellos;

public class GestorPartida {

    private static final int INTENTOS_MAX = 10;
    private static final int ESPERA_MS = 100;
    private static final int INDICE_JUG1 = 0;
    private static final int INDICE_JUG2 = 1;
    private static final String RUTA_DOCUMENTOS = System.getProperty("user.dir") + "/documentos_generados/";
    private static final String EXT_PDF = ".pdf";

    private static final String MJ_PDF_NO_LISTO = "El PDF no está listo después de varios intentos.";
    private static final String FORMATO_GANADOR = "GANADOR: %s%s";
    private static final String FORMATO_ESPERA_PDF = "Esperando a que se cree el archivo PDF: %s (%d)";
    private static final String FORMATO_DOC_ENVIADO_OK = "PDF enviado correctamente a: %s (%d)";
    private static final String TIPO_DOC = "PDF";
    private static final String FORMATO_ERROR = "Error procesando el resultado para %s";

    public void procesarResultado(Jugable[] jugadores) throws Exception {
        Jugable jugadorGanador = jugadores[INDICE_JUG1].getPuntos() >= jugadores[INDICE_JUG2].getPuntos()
                ? jugadores[INDICE_JUG1]
                : jugadores[INDICE_JUG2];
        Jugable jugadorPerdedor = jugadorGanador == jugadores[INDICE_JUG1] ? jugadores[INDICE_JUG2]
                : jugadores[INDICE_JUG1];

        String nombreGanador = jugadorGanador.getNombre();
        String nombrePerdedor = jugadorPerdedor.getNombre();

        Resultado resultado = new Resultado(
                nombreGanador, jugadorGanador.getPuntos(),
                nombrePerdedor, jugadorPerdedor.getPuntos());

        guardar(resultado);

        if (!archivoListo(nombreGanador)) {
            LogCamellos.info(MJ_PDF_NO_LISTO);
            return;
        }

        enviarPDF(jugadorGanador, resultado);
    }

    private void guardar(Resultado resultado) {
        try {
            GeneradorDocumentos historial = new GuardarHistorial();
            historial.generar(resultado);

            GeneradorDocumentos certificado = new GeneradorCertificadoMd();
            certificado.generar(resultado);

            GeneradorDocumentos pdfDocker = new GeneradorPDFDocker();
            pdfDocker.generar(resultado);

        } catch (Exception e) {
            LogCamellos.error(String.format(FORMATO_ERROR, resultado.getGanador()), e);
        }
    }

    private boolean archivoListo(String nombreGanador) {
        Path ruta = Paths.get(RUTA_DOCUMENTOS, nombreGanador + EXT_PDF);
        int intentos = 0;

        while (!Files.exists(ruta) && intentos < INTENTOS_MAX) {
            try {
                LogCamellos.debug(String.format(FORMATO_ESPERA_PDF, ruta.toAbsolutePath(), intentos + 1));
                Thread.sleep(ESPERA_MS);
                intentos++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        return Files.exists(ruta);
    }

    private void enviarPDF(Jugable jugadorGanador, Resultado resultado) {
        try {
            String nombrePdf = resultado.getGanador() + EXT_PDF;
            Path rutaPdf = Paths.get(RUTA_DOCUMENTOS, nombrePdf);

            ConexionCliente conexion = jugadorGanador.getConexion();

            conexion.enviar(String.format(FORMATO_GANADOR, resultado.getGanador(), resultado.getPuntosGanador()));
            conexion.enviar(TIPO_DOC);
            byte[] contenidoPdf = Files.readAllBytes(rutaPdf);// 3. Enviar el contenido del PDF
            conexion.enviarDatos(contenidoPdf);

            LogCamellos.info(String.format(FORMATO_DOC_ENVIADO_OK, resultado.getGanador(), contenidoPdf.length));
        } catch (IOException e) {
            LogCamellos.error(String.format(FORMATO_ERROR, resultado.getGanador()), e);
        }
    }
}
