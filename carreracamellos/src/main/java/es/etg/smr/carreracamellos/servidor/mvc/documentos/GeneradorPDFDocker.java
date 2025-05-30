package es.etg.smr.carreracamellos.servidor.mvc.documentos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import es.etg.smr.carreracamellos.servidor.mvc.modelo.Resultado;

public class GeneradorPDFDocker implements GeneradorDocumentos {

    @Override
    public void generar(Resultado resultado) throws IOException {
        String nombreArchivoMd = resultado.getGanador();
        String archivo = " Certificado de ganador:\n\n"+ nombreArchivoMd;

        String [] comando = {
            "docker", "run", "--rm",
            "-v", System.getProperty("user.dir") + ":/workdir",
            "plass/mdtopdf", nombreArchivoMd
            
        };
    
        Process proceso = Runtime.getRuntime().exec(comando);


        try (BufferedReader salida = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
                 BufferedReader error = new BufferedReader(new InputStreamReader(proceso.getErrorStream()))) {
            String linea;
            while ((linea = salida.readLine()) != null) {
                System.out.println(linea);
            }
            while ((linea = error.readLine()) != null) {
                System.err.println(linea);
            }
        } catch (IOException e) {
            System.err.println("❌ Error al leer la salida del proceso: " + e.getMessage());
        }


        try {
            int codigoSalida = proceso.waitFor();
            if (codigoSalida == 0) {
                System.out.println("✅ PDF generado correctamente.");
            } else {
                System.err.println("❌ Error al generar el PDF. Código de salida: " + codigoSalida);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("❌ Proceso interrumpido: " + e.getMessage());
        }
    }
    
}