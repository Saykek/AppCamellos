package es.etg.smr.carreracamellos.servidor.mvc.documentos;

import java.io.IOException;

import es.etg.smr.carreracamellos.servidor.mvc.modelo.Resultado;



public interface GeneradorDocumentos {
    public void generar(Resultado resultado)throws IOException;

}
