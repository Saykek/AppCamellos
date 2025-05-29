package es.etg.smr.carreracamellos.cliente.mvc.vista;



import java.io.IOException;

import es.etg.smr.carreracamellos.cliente.mvc.controlador.ControladorCliente;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class ControladorVista {

    private String nombreJugador1;
    private String nombreJugador2;
    
    @FXML
    private TextField txtNombreCliente;

    
    
    @FXML
    private TextField txtNombreCamello1; //icono
    
    @FXML
    private TextField txtNombreCamello2; //icono

    @FXML
    private Label lblProgresoCamello1;

    @FXML
    private Label lblProgresoCamello2;
    
    @FXML
    private ProgressBar pbCamello1;

    @FXML
    private ProgressBar pbCamello2;

    private final int PUNTOS_MAXIMOS = 100; // Cambia esto si tu carrera tiene más o menos

@FXML
public void actualizarProgresoTotal(String nombre, int puntos) {
    double progreso = Math.min((double) puntos / PUNTOS_MAXIMOS, 1.0);

    Platform.runLater(() -> {
        String nombreNormalizado = nombre.trim().toLowerCase();
        String jugador1Normalizado = nombreJugador1.trim().toLowerCase();
        String jugador2Normalizado = nombreJugador2.trim().toLowerCase();


        if (nombreNormalizado.equals(jugador1Normalizado)) {
            System.out.println("entando en equals " + nombreJugador1 + " con " + puntos + " puntos.");
            lblProgresoCamello1.setText(puntos + " puntos");
            pbCamello1.setProgress(progreso);
            
        } else if (nombreNormalizado.equals(jugador2Normalizado)) {
            System.out.println("entando en  egundo equals " + nombreJugador1 + " con " + puntos + " puntos.");
            lblProgresoCamello2.setText(puntos + " puntos");
            pbCamello2.setProgress(progreso);
         
        } else {
            System.out.println("Nombre de camello no reconocido: " + nombre);
            System.out.println("Jugadores actuales: " + nombreJugador1 + ", " + nombreJugador2);
        }

        taMensajes.appendText(nombre + " avanza a " + puntos + " puntos.\n");
    });
}
    @FXML
public void actualizarProgresoCamello(String nombre, int puntos) {
    Platform.runLater(() -> {
        if (txtNombreCamello1.getText().equals(nombre)) {
            lblProgresoCamello1.setText(puntos + " puntos");
            pbCamello1.setProgress(puntos / 100.0);  
        } else if (txtNombreCamello2.getText().equals(nombre)) {
            lblProgresoCamello2.setText(puntos + " puntos");
            pbCamello2.setProgress(puntos / 100.0);
        }
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
        this.nombreJugador1 = nombre1;
        this.nombreJugador2 = nombre2;
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

        nombreJugador1 = txtNombreCliente.getText().trim();
        controladorCliente.conectarConServidor(nombreJugador1);

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
