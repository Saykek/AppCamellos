package es.etg.smr.carreracamellos.cliente.utilidades;

import java.io.File;
import java.io.IOException;

public class GestorCertificado {

    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();
    private static final String OS_MAC = "mac";
    private static final String OS_LINUX_1 = "nix";
    private static final String OS_LINUX_2 = "nux";
    private static final String OS_WINDOWS = "win";
    private static final String COMANDO_MAC = "open";
    private static final String COMANDO_LINUX = "xdg-open";
    private static final String COMANDO_WINDOWS = "cmd";
    private static final String CMD_FLAG_EJECUTAR = "/c";
    private static final String CMD_COMANDO_START = "start";
    private static final String CMD_VENTANA_TITULO = "";

    private static final String LOG_CERTIFICADO_NO_EXISTE = "El archivo no existe: %s";

    public static void abrirCertificado(String ruta) throws IOException {
        
        File pdfFile = new File(ruta);

        if (!pdfFile.exists()) {
            LogCamellos.info(LOG_CERTIFICADO_NO_EXISTE + pdfFile.getAbsolutePath());
            return;
        }
        // Para macOS
            if (OS_NAME.contains(OS_MAC)) {
                new ProcessBuilder(COMANDO_MAC, pdfFile.getAbsolutePath()).start();

                // Para Linux
            } else if (OS_NAME.contains(OS_LINUX_1) ||
                    OS_NAME.contains(OS_LINUX_2)) {
                new ProcessBuilder(COMANDO_LINUX, pdfFile.getAbsolutePath()).start();

                // Para Windows
            } else if (OS_NAME.contains(OS_WINDOWS)) {
                new ProcessBuilder(COMANDO_WINDOWS, CMD_FLAG_EJECUTAR, CMD_COMANDO_START, CMD_VENTANA_TITULO, pdfFile.getAbsolutePath()).start();
            }
    }
}
