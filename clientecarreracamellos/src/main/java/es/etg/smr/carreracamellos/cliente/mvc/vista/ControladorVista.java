package es.etg.smr.carreracamellos.cliente.mvc.vista;



import java.io.IOException;

import es.etg.smr.carreracamellos.cliente.mvc.controlador.ControladorCliente;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    private Label lblProgresoCamello1;

    @FXML
private Label lblProgresoJugador1;
@FXML
private Label lblProgresoJugador2;

@FXML// Este método lo llama Cliente cuando recibe "PROGRESO"
public void actualizarProgreso(String nombre, int puntos) {
    String bloques = "🟩".repeat(Math.max(0, puntos));  // Por defecto 🟩
    
    if (nombre.equals(txtNombreCamello1.getText())) {
        lblProgresoJugador1.setText(bloques + " (" + puntos + ")");
    } else if (nombre.equals(txtNombreCamello2.getText())) {
        bloques = "🟦".repeat(Math.max(0, puntos));  // Jugador 2 con 🟦
        lblProgresoJugador2.setText(bloques + " (" + puntos + ")");
    }
}
    
    @FXML
    private Label lblProgresoCamello2;
    @FXML  
    public void actualizarProgresoCamello(String nombre, int puntos) {
        if (nombre.equals("Camello 1")) {
            lblProgresoCamello1.setText(puntos + " puntos"); // debajo podria meter el progreso de la barra /100
        } else if (nombre.equals("Camello 2")) {
            lblProgresoCamello2.setText(puntos + " puntos"); // debajo podria meter el progreso de la barra /100 con setProgress
        }
        Platform.runLater(() -> {
            taMensajes.setText("Progreso Camello 1: " + puntos);
        });
    }
    public void actualizarProgresoCamello2(int puntos){
        Platform.runLater(() -> {
            taCamello2.setText("Progreso Camello 2: " + puntos);
        });
    }

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
            txtNombreCamello1.setText(nombre1); // ASI NO ME COGE EL EQUALS txtNombreCamello1.setText("Camello de " + nombre1);
            txtNombreCamello2.setText(nombre2);
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
