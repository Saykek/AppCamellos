package es.etg.smr.carreracamellos.servidor.mvc.documentos;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import es.etg.smr.carreracamellos.servidor.mvc.modelo.Resultado;
import es.etg.smr.carreracamellos.servidor.mvc.utilidades.LogCamellos;

public class GeneradorCertificadoMd implements DocumentoGenerable {

    private static final String RUTA_DOCUMENTOS = System.getProperty("user.dir") + "/documentos_generados/";
    private static final String FORMATO_CERTIFICADO_OK = "Certificado generado correctamente para: %s";
    private static final String FORMATO_CERTIFICADO_ERROR = "Error al generar el certificado para %s: %s";
    private static final String EXTENSION = ".md";
    private static final String TITULO = "      CERTIFICADO DE GANADOR\n\n     ";
    private static final String MENSAJE = "¡Felicidades %s!\n\n" +
            "Has ganado la partida de la Carrera de Camellos.\n\n" +
            "Gracias por jugar y esperamos verte en la próxima carrera.\n";

    @Override
    public void generar(Resultado resultado) throws IOException {
        String nombreGanador = resultado.getGanador();
        String nombreArchivo = RUTA_DOCUMENTOS + nombreGanador + EXTENSION;
        String contenidoMd = TITULO + MENSAJE.formatted(nombreGanador);

        File carpeta = new File(RUTA_DOCUMENTOS);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        try (FileWriter writer = new FileWriter(nombreArchivo)) {
            writer.write(contenidoMd);

            LogCamellos.info(String.format(FORMATO_CERTIFICADO_OK, nombreGanador));
        } catch (IOException e) {
            LogCamellos.error(String.format(FORMATO_CERTIFICADO_ERROR, nombreGanador, e.getMessage()), e);
            throw e;
        }

    }
}
