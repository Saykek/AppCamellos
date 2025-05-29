package es.etg.smr.carreracamellos.cliente.mvc.vista;


import java.io.IOException;

import es.etg.smr.carreracamellos.cliente.mvc.controlador.ControladorCliente;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class ControladorVista {
    
    @FXML
    private TextField txtNombreCliente;

    private String nombre;
    
    @FXML
    private TextField txtNombreCamello1; //icono
    
    @FXML
    private TextField txtNombreCamello2; //icono

    @FXML
    private TextArea taCamello1;

    @FXML
    private TextArea taCamello2;

    @FXML
    private TextArea taMensajes;

    @FXML
    private ImageView imgCamello1;

    @FXML
    private ImageView imgCamello2;

    @FXML
    private Button btnConectar;
    
    private ControladorCliente controladorCliente;

    public void setControladorCliente(ControladorCliente controladorCliente) {
        this.controladorCliente = controladorCliente;
    }
  
    public void setNombreJugadores(String nombre1, String nombre2) {
        Platform.runLater(() -> {
            txtNombreCamello1.setText("Camello de " + nombre1);
            txtNombreCamello2.setText("Camello de " + nombre2);
        });
       
    }
    @FXML
public void mostrarMensaje(String mensaje) {
    Platform.runLater(() -> {
        taMensajes.appendText(mensaje + "\n");
        System.out.println("Servidor: " + mensaje);
    });
}
    

    @FXML
    public void iniciarPartida(ActionEvent event)throws IOException{
  System.out.println("BOTON CONECTAR correctamente.");
  System.out.println("[DEBUG] controladorCliente = " + controladorCliente);
        nombre = txtNombreCliente.getText().trim();
        controladorCliente.conectarConServidor(nombre);

    }
    @FXML
public void initialize() {
    if (txtNombreCliente != null) {
        System.out.println("TextField inicializado correctamente.");
    } else {
        System.out.println("Error al inicializar el TextField.");
    }
}
}
