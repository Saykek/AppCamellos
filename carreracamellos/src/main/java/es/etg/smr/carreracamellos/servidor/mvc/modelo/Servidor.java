package es.etg.smr.carreracamellos.servidor.mvc.modelo;

import java.io.IOException;

import es.etg.smr.carreracamellos.servidor.mvc.controlador.ControladorServidor;

public class Servidor {

    public static void main(String[] args) throws IOException {
        ControladorServidor.iniciarServidor();
    }
}