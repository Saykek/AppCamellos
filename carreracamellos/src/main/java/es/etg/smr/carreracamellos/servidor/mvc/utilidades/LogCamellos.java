package es.etg.smr.carreracamellos.servidor.mvc.utilidades;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogCamellos {

    public static final String FICHERO_LOG = "fichero.log";
    public static final String LOGGER = "CarreraCamellosLogger";
    public static final Logger logger = Logger.getLogger(LOGGER);  
    public static boolean inicializado = false;

    static {    // para iniciarlo solo una vez
        try {          
            FileHandler fh = new FileHandler(FICHERO_LOG, true);
            SimpleFormatter formatter = new SimpleFormatter();
            logger.addHandler(fh);
            fh.setFormatter(formatter);
            inicializado = true;
        } catch (IOException e) {
            e.printStackTrace(); //// esto no se puede poner
        }
    }

    public static void log(String mensaje) {
        if (inicializado) {   
            logger.log(Level.INFO, mensaje);
        }  
    }
}

