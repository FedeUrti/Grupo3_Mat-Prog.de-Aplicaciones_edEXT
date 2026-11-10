package com.grupo3_mat.edEXT.Presentacion;

import com.grupo3_mat.edEXT.Presentacion.Pantallas.Principal;
import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ServidorEdExtMain {
    private static final Logger LOGGER = Logger.getLogger(ServidorEdExtMain.class.getName());

    private ServidorEdExtMain() {
    }

    public static void main(String[] args) {
        Thread servidor = new Thread(() -> {
            try {
                ServidorCentralMain.main(args);
            } catch (InterruptedException error) {
                Thread.currentThread().interrupt();
                LOGGER.log(Level.SEVERE, "Se interrumpió el servidor central SOAP.", error);
            }
        }, "edext-servidor-central");
        servidor.setUncaughtExceptionHandler((thread, error) ->
                LOGGER.log(Level.SEVERE, "No se pudo iniciar el servidor central SOAP.", error));
        servidor.start();

        if (GraphicsEnvironment.isHeadless()) {
            LOGGER.warning("No hay una pantalla disponible; la Estación de Trabajo no puede abrirse.");
            return;
        }
        EventQueue.invokeLater(() -> new Principal().setVisible(true));
    }
}
