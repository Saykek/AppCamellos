package es.etg.smr.carreracamellos.servidor.mvc.modelo;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import es.etg.smr.carreracamellos.servidor.mvc.documentos.GeneradorCertificadoMd;
import es.etg.smr.carreracamellos.servidor.mvc.documentos.GeneradorDocumentos;
import es.etg.smr.carreracamellos.servidor.mvc.documentos.GeneradorPDFDocker;
import es.etg.smr.carreracamellos.servidor.mvc.documentos.GuardarHistorial;
import es.etg.smr.carreracamellos.servidor.mvc.utilidades.LogCamellos;

public class Partida implements Runnable {

    private  Jugable[] jugadores;

    private static final int MIN_POINTS = 0;
    private static final int MAX_POINTS = 10;
    private static final int TIEMPO_ESPERA = 300;

    private int puntosCamello = 0;
    public boolean partidaTerminada = false; // Variable para indicar si la partida ha terminado

    public Jugable[] getJugadores() {
        return jugadores;
    }

    public Partida(int MAX_JUGADORES) {
        jugadores = new Jugador[MAX_JUGADORES];
    }

    public void agregarJugador(Jugador jugador, int indice) {
        jugadores[indice] = jugador; // Asigna el jugador al índice correspondiente
    }

    private void guardarResultado() throws IOException {
        Jugable ganador = jugadores[0].getPuntos() >= jugadores[1].getPuntos() ? jugadores[0] : jugadores[1];
        Jugable perdedor = (ganador == jugadores[0]) ? jugadores[1] : jugadores[0];

        Resultado resultado = new Resultado(
                ganador.getNombre(), ganador.getPuntos(),
                perdedor.getNombre(), perdedor.getPuntos()
        );

        try {
            // Genero el historial de la partida
            GeneradorDocumentos historial = new GuardarHistorial();
            historial.generar(resultado);
    
            // Genero el PDF de la partida
            GeneradorDocumentos certificado = new GeneradorCertificadoMd();
            certificado.generar(resultado);
            GeneradorDocumentos pdf = new GeneradorPDFDocker();
            pdf.generar(resultado);
           

        } catch (IOException e) {
            System.out.println("Error al guardar el resultado de la partida: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        
        Random random = new Random();
        // imprimimos un mensaje indicando que la partida ha comenzado.
        System.out.println("Iniciando partida con los jugadores: " + jugadores[0].getNombre() + " y " + jugadores[1].getNombre());

        while (!partidaTerminada) {

            for (Jugable jugador : jugadores) {
                puntosCamello = random.nextInt(MAX_POINTS) + 1;
                jugador.incrementarPuntos(puntosCamello);
                LogCamellos.log(jugador.getNombre() + " avanza " + puntosCamello +
                        " puntos. Total acumulado: " + jugador.getPuntos());
            for (Jugable receptor : jugadores) {   // ENVIO PROGRESO A TODOS LOS JUGADORES
                try {        
                PrintWriter salida = new PrintWriter(receptor.getSocket().getOutputStream(), true);
                salida.println("PROGRESO: " + jugador.getNombre() + ";" + jugador.getPuntos());
                
                } catch (IOException e) {
                    System.out.println("Error al enviar el progreso al jugador " + jugador.getNombre() + ": " + e.getMessage());
                  }
                }
  
                if (jugador.esGanador()) {
                        partidaTerminada = true;
                        for (Jugable receptor : jugadores) {
                            try {
                             PrintWriter salida = new PrintWriter(receptor.getSocket().getOutputStream(), true);
                             salida.print("RESULTADO: El jugador " + jugador.getNombre() + " ha ganado la partida con " + jugador.getPuntos() + " puntos.");
                
                            } catch (Exception e) {
                             System.out.println("Error al enviar el progreso al jugador " + jugador.getNombre() + ": " + e.getMessage());
                            }
                        }
                try{        
                    guardarResultado(); // Me da error IO
                             break;
                } catch (IOException e) {
                    System.out.println("Error al guardar el resultado de la partida: " + e.getMessage());
                }             
                }
                try {
                    Thread.sleep(TIEMPO_ESPERA); // Espera 3 segundos antes de continuar
                } catch (InterruptedException e) {
                    System.out.println("Error al pausar la partida: " + e.getMessage());
                }

            }

            /*for (Jugar jugador : jugadores) {
                
                    jugador.terminarConexion();
            
            }*/
        }
    }
}             

