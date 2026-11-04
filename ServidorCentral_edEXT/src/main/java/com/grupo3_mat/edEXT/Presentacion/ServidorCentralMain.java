package com.grupo3_mat.edEXT.Presentacion;

import com.grupo3_mat.edEXT.Logica.Fabrica;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCategoria;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorEdicion;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorInstituto;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorProgramaFormacion;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorUsuario;
import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.transport.http.server.EndpointImpl;
import jakarta.xml.ws.Endpoint;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Arranque headless del proceso central: reutiliza los controladores de dominio y publica
 * un servicio SOAP por contrato. En contenedores se configura la dirección de escucha
 * como 0.0.0.0; al ejecutar localmente, el valor predeterminado solo escucha en loopback.
 */
public final class ServidorCentralMain {
    private static final String DIRECCION_PREDETERMINADA = "http://127.0.0.1:9128/edext/soap";
    private static final List<Endpoint> ENDPOINTS = new ArrayList<>();

    private ServidorCentralMain() {
    }

    public static void main(String[] args) throws InterruptedException {
        String base = System.getenv("EDEXT_SOAP_BIND_ADDRESS");
        if (base == null || base.isBlank()) {
            base = DIRECCION_PREDETERMINADA;
        }
        base = quitarBarraFinal(base.trim());

        CountDownLatch cierre = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
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
    }
}
