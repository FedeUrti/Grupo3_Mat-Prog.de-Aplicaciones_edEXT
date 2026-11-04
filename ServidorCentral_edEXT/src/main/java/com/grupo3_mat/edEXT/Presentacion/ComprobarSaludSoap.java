package com.grupo3_mat.edEXT.Presentacion;

import java.net.HttpURLConnection;
import java.net.URI;

/**
 * Comprobación mínima utilizada por Docker Compose para esperar a que el WSDL central
 * esté disponible antes de iniciar Tomcat; no crea sesión ni accede a datos de negocio.
 */
public final class ComprobarSaludSoap {
    private ComprobarSaludSoap() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.exit(2);
        }
        HttpURLConnection conexion = (HttpURLConnection) URI.create(args[0]).toURL().openConnection();
        conexion.setConnectTimeout(2000);
        conexion.setReadTimeout(2000);
        conexion.setRequestMethod("GET");
        int codigo = conexion.getResponseCode();
        conexion.disconnect();
        if (codigo < 200 || codigo >= 300) {
            System.exit(1);
        }
    }
}
