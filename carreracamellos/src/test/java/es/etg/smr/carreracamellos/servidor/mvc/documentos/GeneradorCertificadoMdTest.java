package es.etg.smr.carreracamellos.servidor.mvc.documentos;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.etg.smr.carreracamellos.servidor.mvc.modelo.Resultado;

class GeneradorCertificadoMdTest {

    private static final String RUTA_BASE = System.getProperty("user.dir") + "/documentos_generados/";
    private static final String EXTENSION = ".md";

    private GeneradorCertificadoMd generador;

    @BeforeEach
    void setUp() {
        generador = new GeneradorCertificadoMd();
    }

    @Test
    void testGeneraCertificadoCorrectamente() throws IOException {
        String ganador = "Sergio";
        Resultado resultado = new Resultado(ganador, 10, "Pedro", 7);

        generador.generar(resultado);

        String rutaArchivo = RUTA_BASE + ganador + EXTENSION;
        File archivo = new File(rutaArchivo);

        assertTrue(archivo.exists(), "El archivo debe existir después de generarlo");

        String contenido = Files.readString(Paths.get(rutaArchivo));
        assertTrue(contenido.contains("CERTIFICADO DE GANADOR"), "Debe contener el título");
        assertTrue(contenido.contains("¡Felicidades Sergio!"), "Debe contener el nombre del ganador");
        assertTrue(contenido.contains("Has ganado la partida"), "Debe contener el mensaje de felicitación");

        // Limpieza
        archivo.delete();
    }

    @Test
    void testCreaCarpetaSiNoExiste() throws IOException {
        String ganador = "Carla";
        Resultado resultado = new Resultado(ganador, 10, "Rita", 9);

        // Borramos carpeta si existe
        File carpeta = new File(RUTA_BASE);
        if (carpeta.exists()) {
            for (File f : carpeta.listFiles()) f.delete();
            carpeta.delete();
        }

        generador.generar(resultado);

        File archivo = new File(RUTA_BASE + ganador + EXTENSION);
        assertTrue(archivo.exists(), "El archivo debe crearse incluso si la carpeta no existía");

        // Limpieza
        archivo.delete();
        carpeta.delete();
    }
}
