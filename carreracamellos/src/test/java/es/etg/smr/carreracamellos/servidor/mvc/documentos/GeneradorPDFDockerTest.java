package es.etg.smr.carreracamellos.servidor.mvc.documentos;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

import es.etg.smr.carreracamellos.servidor.mvc.modelo.Resultado;

public class GeneradorPDFDockerTest {

    @Test
    public void testGenerarPDF() {
        String nombreGanador = "TestJugador";
        String nombreArchivoMd = nombreGanador + ".md";
        String nombreArchivoPdf = nombreGanador + ".pdf";

        String rutaBase = System.getProperty("user.dir");
        String carpeta = rutaBase + "/documentos_generados/";
        String rutaMd = carpeta + nombreArchivoMd;

        // Crear archivo .md si no existe
        File archivoMd = new File(rutaMd);
        archivoMd.getParentFile().mkdirs(); // crea carpeta si no existe

        try {
            FileWriter writer = new FileWriter(archivoMd);
            writer.write("# Certificado de Victoria\n\n¡Felicidades " + nombreGanador + " por ganar!");
            writer.close();
        } catch (IOException e) {
            fail("No se pudo crear el archivo .md: " + e.getMessage());
        }

        Resultado resultado = new Resultado(
                nombreGanador, 10,
                "OtroJugador", 5);

        GeneradorPDFDocker generador = new GeneradorPDFDocker();

        try {
            generador.generar(resultado);
        } catch (IOException e) {
            fail("Excepción al generar el PDF: " + e.getMessage());
        }

        // Verificar que el PDF se ha generado
        File archivoPdf = new File(carpeta + nombreArchivoPdf);
        assertTrue(archivoPdf.exists(), "El archivo PDF debería haberse generado.");
        assertTrue(archivoPdf.length() > 0, "El archivo PDF no debería estar vacío.");

        // Limpieza opcional
        archivoMd.delete();
        archivoPdf.delete();
    }
}