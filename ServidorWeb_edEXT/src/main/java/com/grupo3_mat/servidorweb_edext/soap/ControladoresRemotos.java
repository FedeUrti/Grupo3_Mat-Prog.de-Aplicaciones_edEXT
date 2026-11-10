package com.grupo3_mat.servidorweb_edext.soap;

import com.grupo3_mat.servidorweb_edext.configuracion.ConfiguracionWeb;
import com.grupo3_mat.servidorweb_edext.soap.generated.CategoriaSoap;
import com.grupo3_mat.servidorweb_edext.soap.generated.CursoSoap;
import com.grupo3_mat.servidorweb_edext.soap.generated.EdicionSoap;
import com.grupo3_mat.servidorweb_edext.soap.generated.InstitutoSoap;
import com.grupo3_mat.servidorweb_edext.soap.generated.ProgramaFormacionSoap;
import com.grupo3_mat.servidorweb_edext.soap.generated.UsuarioSoap;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.soap.SOAPFaultException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Construye los seis clientes a partir de las interfaces Java generadas por wsimport.
 * La construcción de puertos usa solo los QName y la URL configurada; así Tomcat no
 * descarga WSDL al arrancar ni necesita el código central ni el módulo de contratos.
 */
public final class ControladoresRemotos {
    private static final String NAMESPACE = "http://soap.edext.grupo3_mat.com/";
    private static final CategoriaSoap CATEGORIA =
            clienteSeguro(CategoriaSoap.class, "CategoriaSoapService", "CategoriaSoapPort", "categoria");
    private static final CursoSoap CURSO =
            clienteSeguro(CursoSoap.class, "CursoSoapService", "CursoSoapPort", "curso");
    private static final EdicionSoap EDICION =
            clienteSeguro(EdicionSoap.class, "EdicionSoapService", "EdicionSoapPort", "edicion");
    private static final InstitutoSoap INSTITUTO =
            clienteSeguro(InstitutoSoap.class, "InstitutoSoapService", "InstitutoSoapPort", "instituto");
    private static final ProgramaFormacionSoap PROGRAMA =
            clienteSeguro(ProgramaFormacionSoap.class, "ProgramaFormacionSoapService", "ProgramaFormacionSoapPort", "programa");
    private static final UsuarioSoap USUARIO =
            clienteSeguro(UsuarioSoap.class, "UsuarioSoapService", "UsuarioSoapPort", "usuario");

    private ControladoresRemotos() {
    }

    public static CategoriaSoap getCategoriaSoap() {
        return CATEGORIA;
    }

    public static CursoSoap getCursoSoap() {
        return CURSO;
    }

    public static EdicionSoap getEdicionSoap() {
        return EDICION;
    }

    public static InstitutoSoap getInstitutoSoap() {
        return INSTITUTO;
    }

    public static ProgramaFormacionSoap getProgramaFormacionSoap() {
        return PROGRAMA;
    }

    public static UsuarioSoap getUsuarioSoap() {
        return USUARIO;
    }

    private static <T> T clienteSeguro(Class<T> contrato, String servicio, String puerto, String ruta) {
        try {
            return cliente(contrato, servicio, puerto, ruta);
        } catch (RuntimeException error) {
            return clienteNoDisponible(contrato, servicio, error);
        }
    }

    private static <T> T cliente(Class<T> contrato, String servicio, String puerto, String ruta) {
        ConfiguracionWeb configuracion = ConfiguracionWeb.get();
        String base = configuracion.getCentralSoapUrl().trim();
        while (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        String direccion = base + "/" + ruta;
        URI endpoint = URI.create(direccion);
        if (!("http".equalsIgnoreCase(endpoint.getScheme()) || "https".equalsIgnoreCase(endpoint.getScheme()))
                || endpoint.getHost() == null) {
            throw new IllegalArgumentException("La URL del servicio central no es válida: " + base);
        }
        URL wsdl = ControladoresRemotos.class.getClassLoader().getResource("wsdl/" + ruta + ".wsdl");
        if (wsdl == null) {
            throw new IllegalStateException("No se encontró el WSDL local del servicio '" + servicio + "'.");
        }
        QName servicioQName = new QName(NAMESPACE, servicio);
        QName puertoQName = new QName(NAMESPACE, puerto);

        Service service = Service.create(wsdl, servicioQName);
        T remoto = service.getPort(puertoQName, contrato);
        var contexto = ((BindingProvider) remoto).getRequestContext();
        contexto.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, direccion);
        contexto.put("com.sun.xml.ws.connect.timeout", configuracion.getSoapConnectTimeoutMillis());
        contexto.put("com.sun.xml.ws.request.timeout", configuracion.getSoapRequestTimeoutMillis());

        AtomicReference<T> port = new AtomicReference<>(remoto);
        return contrato.cast(Proxy.newProxyInstance(
                contrato.getClassLoader(),
                new Class<?>[]{contrato},
                (proxy, metodo, argumentos) -> {
                    if (metodo.getDeclaringClass() == Object.class) {
                        return switch (metodo.getName()) {
                            case "toString" -> "Proxy SOAP para " + direccion;
                            case "hashCode" -> System.identityHashCode(proxy);
                            case "equals" -> proxy == argumentos[0];
                            default -> throw new UnsupportedOperationException(metodo.getName());
                        };
                    }
                    try {
                        return metodo.invoke(port.get(), argumentos);
                    } catch (InvocationTargetException error) {
                        Throwable causa = error.getCause();
                        if (causa instanceof SOAPFaultException fallo) {
                            RuntimeException negocio = excepcionDeNegocio(fallo);
                            if (negocio != null) {
                                throw negocio;
                            }
                            throw new FalloOperacionCentralException(
                                    "El servidor central rechazó la operación '" + metodo.getName()
                                            + "': " + fallo.getFault().getFaultString(),
                                    fallo);
                        }
                        if (causa instanceof WebServiceException) {
                            throw new ServicioCentralNoDisponibleException(
                                    "No se pudo completar la operación SOAP '" + metodo.getName()
                                            + "' contra " + direccion
                                            + ". Verifique que el servidor central esté activo.",
                                    causa);
                        }
                        throw causa;
                    }
                }));
    }

    private static <T> T clienteNoDisponible(Class<T> contrato, String servicio, RuntimeException causa) {
        String mensaje = "No se pudo inicializar el cliente SOAP de " + servicio
                + ". Revise central.soap.url en ~/.edEXT/web.properties.";
        return contrato.cast(Proxy.newProxyInstance(
                contrato.getClassLoader(),
                new Class<?>[]{contrato},
                (proxy, metodo, argumentos) -> {
                    if (metodo.getDeclaringClass() == Object.class) {
                        return switch (metodo.getName()) {
                            case "toString" -> mensaje;
                            case "hashCode" -> System.identityHashCode(proxy);
                            case "equals" -> proxy == argumentos[0];
                            default -> throw new UnsupportedOperationException(metodo.getName());
                        };
                    }
                    throw new ServicioCentralNoDisponibleException(mensaje, causa);
                }));
    }

    private static RuntimeException excepcionDeNegocio(SOAPFaultException fallo) {
        if (fallo.getFault().getDetail() == null) {
            return null;
        }
        NodeList detalles = fallo.getFault().getDetail()
                .getElementsByTagNameNS("http://soap.edext.grupo3_mat.com/faults", "BusinessFault");
        if (detalles.getLength() == 0 || !(detalles.item(0) instanceof Element businessFault)) {
            return null;
        }
        String tipo = texto(businessFault, "type");
        String mensaje = texto(businessFault, "message");
        return switch (tipo) {
            case "IllegalArgumentException" -> new IllegalArgumentException(mensaje);
            case "IllegalStateException" -> new IllegalStateException(mensaje);
            case "SecurityException" -> new SecurityException(mensaje);
            default -> null;
        };
    }

    private static String texto(Element elemento, String nombre) {
        NodeList valores = elemento.getElementsByTagNameNS(
                "http://soap.edext.grupo3_mat.com/faults", nombre);
        return valores.getLength() == 0 ? "" : valores.item(0).getTextContent();
    }
}
