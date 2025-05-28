package es.etg.smr.carreracamellos.cliente.mvc.modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {
    private static final String HOST_POR_DEFECTO = "localhost";
    private static final int PUERTO_POR_DEFECTO = 3009;

    private String host;
    private int puerto;

    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;

    public Cliente() throws IOException {
        this.host = HOST_POR_DEFECTO;
        this.puerto = PUERTO_POR_DEFECTO;
    }
    public Cliente(String host, int puerto) {
        this.host = host;
        this.puerto = puerto;
    }
    public void conectar() throws IOException {
        socket = new Socket(host, puerto);
        entrada = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
        salida = new PrintWriter(socket.getOutputStream(), true);
    }

    public void enviarNombre(String nombre) {
        salida.println(nombre);
    }

    public String recibirMensaje() throws IOException {
        return entrada.readLine();
    }

    public void cerrar() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}