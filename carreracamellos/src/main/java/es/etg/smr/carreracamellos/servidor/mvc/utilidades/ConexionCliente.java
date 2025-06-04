package es.etg.smr.carreracamellos.servidor.mvc.utilidades;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ConexionCliente {
    private final Socket socket;
    private final BufferedReader entrada;
    private final PrintWriter salida;

    public ConexionCliente(Socket socket) throws IOException {
        this.socket = socket;
        this.entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.salida = new PrintWriter(socket.getOutputStream(), true);
    }

    public String leer() throws IOException {
        return entrada.readLine();
    }

    public void enviar(String mensaje) {
        salida.println(mensaje);
    }

    public Socket getSocket() {
        return socket;
    }

    public void cerrar() throws IOException {
        socket.close();
    }
}
    

