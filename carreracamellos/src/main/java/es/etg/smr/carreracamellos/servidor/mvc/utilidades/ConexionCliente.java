package es.etg.smr.carreracamellos.servidor.mvc.utilidades;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConexionCliente {
    private final Socket socket;
    private final BufferedReader entrada;
    private final PrintWriter salida;
    private DataOutputStream dataOut;

    public ConexionCliente(Socket socket) throws IOException {
        this.socket = socket;
        this.entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.salida = new PrintWriter(socket.getOutputStream(), true);
        this.dataOut = new DataOutputStream(socket.getOutputStream());
    }

    public String leer() throws IOException {
        return entrada.readLine();
    }

    public void enviar(String mensaje) {
        salida.println(mensaje);
    }

    public void enviarDatos(byte[] datos) throws IOException {
        dataOut.writeInt(datos.length);
        dataOut.write(datos);
        dataOut.flush();
    }

    public Socket getSocket() {
        return socket;
    }

    public void cerrar() throws IOException {
        entrada.close();
        salida.close();
        dataOut.close();
        socket.close();
    }
}
