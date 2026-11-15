package com.grupo3_mat.edEXT.Presentacion;

import com.grupo3_mat.edEXT.Presentacion.Pantallas.Principal;
import java.awt.GraphicsEnvironment;

public final class ServidorEdExtMain {
    private ServidorEdExtMain() {
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            iniciarServicio();
            return;
        }

        if (args.length == 1 && "--estacion".equals(args[0])) {
            iniciarEstacion();
            return;
        }

        System.err.println("Argumentos no reconocidos. Uso: java -jar servidor.jar [--estacion]");
    }

    private static void iniciarServicio() {
        try {
            ServidorCentralMain.main(new String[0]);
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Se interrumpió el servidor central SOAP.", error);
        }
    }

    private static void iniciarEstacion() {
        if (GraphicsEnvironment.isHeadless()) {
            System.err.println("No se puede abrir la Estación de Trabajo: no hay una pantalla disponible.");
            return;
        }
        Principal.main(new String[0]);
    }
}
