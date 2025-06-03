package es.etg.smr.carreracamellos.servidor.mvc.documentos;

import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

import es.etg.smr.carreracamellos.servidor.mvc.modelo.Resultado;
import es.etg.smr.carreracamellos.servidor.mvc.utilidades.LogCamellos;

public class GuardarHistorial implements GeneradorDocumentos {

    private static final String ARCHIVO = "partidas.txt";
    private static final String FORMATO_HISTORIAL = "Fecha: %s |  Ganador: %s (%d pts)  |  Perdedor: %s (%d pts) ";
    private static final String FORMATO_FECHA = "dd/MM/yyyy HH:mm";
    private static final String FORMATO_GUARDADO = "Historial guardado: %s";
    private static final String FORMATO_ERROR = "Error al guardar el historial: ";

    @Override
    public void generar(Resultado resultadoPartida) throws IOException {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern(FORMATO_FECHA);
        String linea = String.format(FORMATO_HISTORIAL,
                resultadoPartida.getFecha().format(formato),
                resultadoPartida.getGanador(), resultadoPartida.getPuntosGanador(),
                resultadoPartida.getPerdedor(), resultadoPartida.getPuntosPerdedor());

        try (FileWriter writer = new FileWriter(ARCHIVO, true)) {
            writer.write(linea + "\n");
            LogCamellos.info(String.format(FORMATO_GUARDADO, linea));
        } catch (IOException e) {
            LogCamellos.error(FORMATO_ERROR, e);
            throw e;
        }

    }
}
