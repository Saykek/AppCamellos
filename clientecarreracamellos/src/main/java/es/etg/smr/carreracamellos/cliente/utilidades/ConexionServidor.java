package es.etg.smr.carreracamellos.cliente.utilidades;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ConexionServidor {
    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;
    private DataInputStream entradaDatos; // Para recibir PDF

   /*/ public ConexionServidor(String host, int puerto) throws IOException {
        this.socket = new Socket(host, puerto);
        this.entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.salida = new PrintWriter(socket.getOutputStream(), true);
        this.entradaDatos = new DataInputStream(socket.getInputStream());
    }

    public BufferedReader getEntrada() {
        return entrada;
    }

    public PrintWriter getSalida() {
        return salida;
    }

    public DataInputStream getEntradaDatos() {
        return entradaDatos;
    } */

    public void cerrar() {
        try {
            if (entradaDatos != null) entradaDatos.close();
            if (entrada != null) entrada.close();
            if (salida != null) salida.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
