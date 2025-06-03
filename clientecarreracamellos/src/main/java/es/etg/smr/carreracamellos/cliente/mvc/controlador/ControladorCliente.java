package es.etg.smr.carreracamellos.cliente.mvc.controlador;

import java.io.IOException;

import es.etg.smr.carreracamellos.Main;
import es.etg.smr.carreracamellos.cliente.mvc.modelo.Cliente;
import es.etg.smr.carreracamellos.cliente.mvc.vista.ControladorVista;
import es.etg.smr.carreracamellos.cliente.utilidades.LogCamellos;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ControladorCliente extends Application {

    private final static String VISTA = "/es/etg/smr/carreracamellos/cliente/vista/pantallaPrincipal.fxml";

    private Scene scene;
    private final Cliente cliente;
    private ControladorVista controladorVista;

    @Override
    public void start(Stage stage) throws IOException {

        LogCamellos.info("[CLIENTE] Iniciando interfaz de usuario...");
        // Cargo la vista principal
        stage.setScene(cargarVista(VISTA));
        stage.show();

    }

    private Scene cargarVista(String ficheroView) throws IOException {
        LogCamellos.info("[CLIENTE] Cargando vista: " + ficheroView);
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(ficheroView));
        Parent root = (Parent) fxmlLoader.load();

        // Obtengo el controlador de la vista para pasarle una referencia al controlador
        controladorVista = fxmlLoader.<ControladorVista>getController();
        controladorVista.setControladorCliente(this);
        // this.controladorVista = controladorVista;
        cliente.setControladorVista(controladorVista);
        scene = new Scene(root);

        return scene;
    }

    // Constructor con valores por defecto (localhost:3009)
    public ControladorCliente() throws IOException {
        cliente = new Cliente();
    }

    // Constructor personalizado (para usar otro puerto)
    public ControladorCliente(String host, int puerto) {
        cliente = new Cliente(host, puerto);
    }

    // conecto con el servidor y enviar el nombre del jugador
    public String conectarConServidor(String nombreJugador) {
        try {
            LogCamellos.info("[CLIENTE] Conectando al servidor como: " + nombreJugador);
            cliente.conectar(nombreJugador);
            cliente.enviarNombre(nombreJugador);
            String respuesta = cliente.recibirMensaje();
            LogCamellos.info("[CLIENTE] Mensaje recibido tras conexión: " + respuesta);
            return respuesta;
        } catch (IOException e) {
            LogCamellos.error("[CLIENTE] Error al conectar con el servidor: " + e.getMessage(), e);
            return "Error de conexión: " + e.getMessage();
        }
    }

}
