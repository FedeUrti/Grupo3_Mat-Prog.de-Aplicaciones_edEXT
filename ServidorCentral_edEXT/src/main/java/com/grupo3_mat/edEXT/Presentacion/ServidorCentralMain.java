package com.grupo3_mat.edEXT.Presentacion;

import com.grupo3_mat.edEXT.Configuracion.ConfiguracionServidor;
import com.grupo3_mat.edEXT.Logica.Fabrica;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCategoria;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorEdicion;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorInstituto;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorProgramaFormacion;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorUsuario;
import com.grupo3_mat.edEXT.Persistencia.Conexion;
import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.transport.http.server.EndpointImpl;
import jakarta.xml.ws.Endpoint;
import java.net.BindException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Arranque headless del proceso central: reutiliza los controladores de dominio y publica
 * un servicio SOAP por contrato. En contenedores se configura la dirección de escucha
 * como 0.0.0.0; al ejecutar localmente, el valor predeterminado solo escucha en loopback.
 */
public final class ServidorCentralMain {
    private static final List<Endpoint> ENDPOINTS = new ArrayList<>();

    private ServidorCentralMain() {
    }

    public static void main(String[] args) throws InterruptedException {
        String base = quitarBarraFinal(ConfiguracionServidor.get().getSoapBindUrl());

        CountDownLatch cierre = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Cierre limpio del servidor central SOAP.");
            detenerServicios();
            cierre.countDown();
        }, "cierre-servidor-central"));

        try {
            Fabrica fabrica = Fabrica.getInstance();
            publicar(base, "/categoria", fabrica.getIControladorCategoria());
            publicar(base, "/curso", fabrica.getIControladorCurso());
            publicar(base, "/edicion", fabrica.getIControladorEdicion());
            publicar(base, "/instituto", fabrica.getIControladorInstituto());
            publicar(base, "/programa", fabrica.getIControladorProgramaFormacion());
            publicar(base, "/usuario", fabrica.getIControladorUsuario());
            System.out.println("Servidor central SOAP disponible en " + base + " (WSDL por servicio: ?wsdl).");
            cierre.await();
        } catch (RuntimeException error) {
            detenerServicios();
            if (esPuertoOcupado(error)) {
                System.err.println("No se pudo publicar el servicio SOAP en " + base
                        + ": el puerto está ocupado. Cierre el otro proceso o cambie soap.bind.url.");
                System.exit(1);
            }
            throw error;
        }
    }

    private static void publicar(String base, String ruta, Object controlador) {
        String direccion = base + ruta;
        Endpoint endpoint = new EndpointImpl(
                BindingID.SOAP11_HTTP,
                controlador.getClass(),
                new MapeadorExcepcionesSoap(controlador));
        endpoint.publish(direccion);
        ENDPOINTS.add(endpoint);
        System.out.println("Servicio SOAP publicado: " + direccion + "?wsdl");
    }

    private static boolean esPuertoOcupado(Throwable error) {
        for (Throwable causa = error; causa != null; causa = causa.getCause()) {
            if (causa instanceof BindException
                    || (causa.getMessage() != null
                    && causa.getMessage().toLowerCase(java.util.Locale.ROOT).contains("address already in use"))) {
                return true;
            }
        }
        return false;
    }

    private static String quitarBarraFinal(String direccion) {
        int fin = direccion.length();
        while (fin > 0 && direccion.charAt(fin - 1) == '/') {
            fin--;
        }
        return direccion.substring(0, fin);
    }

    private static void detenerServicios() {
        for (int indice = ENDPOINTS.size() - 1; indice >= 0; indice--) {
            ENDPOINTS.get(indice).stop();
        }
        ENDPOINTS.clear();
        Conexion.cerrar();
    }
}
