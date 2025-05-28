package es.etg.smr.carreracamellos.cliente.mvc.vista;


import java.io.IOException;

import es.etg.smr.carreracamellos.cliente.mvc.controlador.ControladorCliente;
import es.etg.smr.carreracamellos.cliente.mvc.modelo.Cliente;
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
    private ImageView imgCamello1;

    @FXML
    private ImageView imgCamello2;

    @FXML
    private Button btnConectar;
    
    private ControladorCliente controladorCliente;

    public void setControladorCliente(ControladorCliente controladorCliente) {
        this.controladorCliente = controladorCliente;
    }

    @FXML
    public void iniciarPartida(ActionEvent event)throws IOException{
  
        nombre = txtNombreCliente.getText().trim();

       Cliente cliente = new Cliente();
       cliente.conectar();
       cliente.enviarNombre(nombre);

       String respuesta = cliente.recibirMensaje(); // Esperamos la respuesta del servidor
      
            
        txtNombreCamello1.setText(nombre + "'s Camello 1");
        txtNombreCamello2.setText(nombre + "'s Camello 2");

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
