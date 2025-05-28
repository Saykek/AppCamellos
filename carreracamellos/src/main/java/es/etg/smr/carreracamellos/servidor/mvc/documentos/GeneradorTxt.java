package es.etg.smr.carreracamellos.servidor.mvc.documentos;

import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

import es.etg.smr.carreracamellos.servidor.mvc.modelo.Resultado;

public class GeneradorTxt implements GeneradorDocumentos {


   private static final String ARCHIVO = "demo/partidas.txt";

    @Override
    public void guardar(Resultado resultadoPartida) throws IOException {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String linea = String.format("Fecha: %s | Ganador: %s (%d pts) | Perdedor: %s (%d pts)",
                resultadoPartida.getFecha().format(formato),
                resultadoPartida.getGanador(), resultadoPartida.getPuntosGanador(),
                resultadoPartida.getPerdedor(), resultadoPartida.getPuntosPerdedor());

        FileWriter writer = new FileWriter(ARCHIVO, true);
        writer.write(linea + "\n");
        writer.close();
      
    }
}

