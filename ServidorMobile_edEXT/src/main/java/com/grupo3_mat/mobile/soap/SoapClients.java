package com.grupo3_mat.mobile.soap;

import com.grupo3_mat.mobile.soap.generated.CursoSoap;
import com.grupo3_mat.mobile.soap.generated.CursoSoapService;
import com.grupo3_mat.mobile.soap.generated.EdicionSoap;
import com.grupo3_mat.mobile.soap.generated.EdicionSoapService;
import com.grupo3_mat.mobile.soap.generated.UsuarioSoap;
import com.grupo3_mat.mobile.soap.generated.UsuarioSoapService;
import jakarta.xml.ws.BindingProvider;
import java.net.URL;
import javax.xml.namespace.QName;

public final class SoapClients {
    private static final String DEFAULT_BASE_URL = "http://central:9128/edext/soap";
    private static final String NAMESPACE = "http://soap.edext.grupo3_mat.com/";

    private static final UsuarioSoap USUARIOS = createUsuarios();
    private static final CursoSoap CURSOS = createCursos();
    private static final EdicionSoap EDICIONES = createEdiciones();

    private SoapClients() {
    }

    public static UsuarioSoap usuarios() {
        return USUARIOS;
    }

    public static CursoSoap cursos() {
        return CURSOS;
    }

    public static EdicionSoap ediciones() {
        return EDICIONES;
    }

    private static UsuarioSoap createUsuarios() {
        URL wsdl = wsdl("usuario");
        UsuarioSoap service = new UsuarioSoapService(
                wsdl, new QName(NAMESPACE, "UsuarioSoapService")).getUsuarioSoapPort();
        return configure(service, "usuario");
    }

    private static CursoSoap createCursos() {
        URL wsdl = wsdl("curso");
        CursoSoap service = new CursoSoapService(
                wsdl, new QName(NAMESPACE, "CursoSoapService")).getCursoSoapPort();
        return configure(service, "curso");
    }

    private static EdicionSoap createEdiciones() {
        URL wsdl = wsdl("edicion");
        EdicionSoap service = new EdicionSoapService(
                wsdl, new QName(NAMESPACE, "EdicionSoapService")).getEdicionSoapPort();
        return configure(service, "edicion");
    }

    private static URL wsdl(String service) {
        URL wsdl = SoapClients.class.getClassLoader().getResource("wsdl/" + service + ".wsdl");
        if (wsdl == null) {
            throw new IllegalStateException("No está empaquetado el WSDL de " + service + ".");
        }
        return wsdl;
    }

    private static <T> T configure(T port, String service) {
        String baseUrl = System.getenv("EDEXT_SOAP_BASE_URL");
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = DEFAULT_BASE_URL;
        }
        String endpoint = baseUrl.trim().replaceAll("/+$", "") + "/" + service;
        BindingProvider binding = (BindingProvider) port;
        binding.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
        binding.getRequestContext().put("com.sun.xml.ws.connect.timeout", 5000);
        binding.getRequestContext().put("com.sun.xml.ws.request.timeout", 15000);
        return port;
    }
}
