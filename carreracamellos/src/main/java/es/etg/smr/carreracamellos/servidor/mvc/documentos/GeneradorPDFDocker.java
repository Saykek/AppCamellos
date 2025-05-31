package es.etg.smr.carreracamellos.servidor.mvc.documentos;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import es.etg.smr.carreracamellos.servidor.mvc.modelo.Resultado;

public class GeneradorPDFDocker implements GeneradorDocumentos {

    private static final String IMAGEN_DOCKER = "pandoc/latex";
    private static final String DIRECTORIO = " /data/";
    private static final String RUTA_DOCUMENTOS = System.getProperty("user.dir") + "/src/main/java/es/etg/smr/carreracamellos/servidor/mvc/documentos/envios/"; 

    @Override
    public void generar(Resultado resultado) throws IOException {
       
        String nombreGanador = resultado.getGanador();
        String archivoMdRuta = "documentos/envios/" + nombreGanador + ".md";
        String archivoPdfRuta = "documentos/envios/" + nombreGanador + ".pdf";
        String nombreArchivoMD = nombreGanador + ".md";
        

        //String rutaActual = new File(".").getAbsolutePath();

        File archivoMd = new File(RUTA_DOCUMENTOS + nombreArchivoMD);

        if (!archivoMd.exists()) {
            System.err.println("❌ El archivo " + nombreArchivoMD + " no existe. Primero genera el .md.");
            return;
        }

        String [] comando = {
            "docker", "run", "--rm",
            "--platform=linux/amd64",
            "-v", RUTA_DOCUMENTOS + ":/data",
            IMAGEN_DOCKER,
            nombreArchivoMD,
            "-o",
            nombreGanador + ".pdf"
            
        };

        System.out.println("COMANDO DOCKER: " + String.join(" ", comando));
    
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