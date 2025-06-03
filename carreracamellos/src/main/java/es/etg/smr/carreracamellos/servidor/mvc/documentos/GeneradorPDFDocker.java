package es.etg.smr.carreracamellos.servidor.mvc.documentos;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import es.etg.smr.carreracamellos.servidor.mvc.modelo.Resultado;
import es.etg.smr.carreracamellos.servidor.mvc.utilidades.LogCamellos;

public class GeneradorPDFDocker implements GeneradorDocumentos {

    private static final String IMAGEN_DOCKER = "pandoc/latex";
    private static final String DIRECTORIO = ":/data";
    private static final String EXTENSION_MD = ".md";
    private static final String EXTENSION_PDF = ".pdf";
    private static final String RUTA_DOCUMENTOS = System.getProperty("user.dir")
            + "/src/main/java/es/etg/smr/carreracamellos/servidor/mvc/documentos/envios/";

    private static final String DOCKER_RUN = "run";
    private static final String DOCKER_REMOVE = "--rm";
    private static final String COMANDO_DOCKER = "docker";
    private static final String PLATAFORMA = "--platform=linux/amd64";
    private static final String VOL = "-v";
    private static final String FLAG_SALIDA = "-o";

    private static final String MJ_NO_EXISTE_MD = "El archivo %s no existe. Primero genera el archivo .md.";
    private static final String FORMATO_COMANDO = "Ejecutando comando Docker: %s";
    private static final String FORMATO_SALIDA_OK = "PDF generado correctamente para %s.";
    private static final String FORMATO_SALIDA_ERROR = "Error al generar el PDF . Código de salida: %d";
    private static final String MJ_ERROR_LECTURA = "Error al leer la salida del proceso Docker";
    private static final String FORMATO_INTERRUPCION = "El proceso fue interrumpido mientras se generaba el PDF para %s.";
    private static final String FORMATO_DOCKER_SALIDA = "[DOCKER-STDOUT] %s";
    private static final String FORMATO_DOCKER_ERROR = "[DOCKER-STDERR] %s";

    @Override
    public void generar(Resultado resultado) throws IOException {

        String nombreGanador = resultado.getGanador();
        String nombreArchivoMD = nombreGanador + EXTENSION_MD;

        File archivoMd = new File(RUTA_DOCUMENTOS + nombreArchivoMD);

        if (!archivoMd.exists()) {
            LogCamellos.info(String.format(MJ_NO_EXISTE_MD + nombreArchivoMD));
            return;
        }

        String[] comando = {
                COMANDO_DOCKER, DOCKER_RUN, DOCKER_REMOVE,
                PLATAFORMA,
                VOL, RUTA_DOCUMENTOS + DIRECTORIO,
                IMAGEN_DOCKER,
                nombreArchivoMD,
                FLAG_SALIDA,
                nombreGanador + EXTENSION_PDF

        };

        LogCamellos.debug(String.format(FORMATO_COMANDO, String.join(" ", comando)));

        Process proceso = Runtime.getRuntime().exec(comando);

        try (BufferedReader salida = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
                BufferedReader error = new BufferedReader(new InputStreamReader(proceso.getErrorStream()))) {
            String linea;
            while ((linea = salida.readLine()) != null) {
                LogCamellos.debug(String.format(FORMATO_DOCKER_SALIDA, linea));
            }
            while ((linea = error.readLine()) != null) {
                LogCamellos.info(String.format(FORMATO_DOCKER_ERROR, linea));
            }
        } catch (IOException e) {
            LogCamellos.error(MJ_ERROR_LECTURA, e);
        }

        try {
            int codigoSalida = proceso.waitFor();
            if (codigoSalida == 0) {
                LogCamellos.info(FORMATO_SALIDA_OK);
            } else {
                LogCamellos.error(String.format(FORMATO_SALIDA_ERROR, codigoSalida), null);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LogCamellos.error(String.format(FORMATO_INTERRUPCION, e.getMessage()), e);
        }
    }

}