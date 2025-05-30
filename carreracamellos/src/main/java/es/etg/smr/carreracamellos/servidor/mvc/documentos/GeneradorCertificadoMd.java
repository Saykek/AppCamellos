package es.etg.smr.carreracamellos.servidor.mvc.documentos;

import java.io.FileWriter;
import java.io.IOException;

import es.etg.smr.carreracamellos.servidor.mvc.modelo.Resultado;

public class GeneradorCertificadoMd implements GeneradorDocumentos {
    
    private static final String PREFIJO = " Certificado de ganador:\n\n";
    private static final String EXTENSION = ".md";
    private static final String TITULO = "  🏆    Certificado de ganador\n\n    🏆 "  ;
    private static final String MENSAJE = "¡Felicidades %s!\n\n" + 
            "Has ganado la partida de la Carrera de Camellos.\n\n" +
            "Gracias por jugar y esperamos verte en la próxima carrera.\n";
    @Override
    public void generar(Resultado resultado) throws IOException {
        String nombreGanador = resultado.getGanador();
        String nombreArchivo = PREFIJO + nombreGanador + EXTENSION;
        String contenidoMd = TITULO  + MENSAJE.formatted(nombreGanador);

        try (FileWriter writer = new FileWriter(nombreArchivo)) {
            writer.write(contenidoMd);
            writer.close();
        } 
        
        
    }
}
