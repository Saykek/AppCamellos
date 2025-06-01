package es.etg.smr.carreracamellos.servidor.mvc.documentos;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

import es.etg.smr.carreracamellos.servidor.mvc.modelo.Resultado;

public class GuardarHistorialTest {

    @Test
    public void testGenerarHistorial() {
        GuardarHistorial guardador = new GuardarHistorial();

        Resultado resultado = new Resultado(
                "Jugador1", 12,
                "Jugador2", 8
               
        );

        try {
            guardador.generar(resultado);
        } catch (IOException e) {
            fail("No debería lanzarse una excepción: " + e.getMessage());
        }

        File archivo = new File("partidas.txt");
        assertTrue(archivo.exists(), "El archivo partidas.txt debería existir");
        assertTrue(archivo.length() > 0, "El archivo debería contener texto");
    }
}