package com.grupo3_mat.servidorweb_edext.soap;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Convierte los fallos de transporte SOAP que no manejó una operación concreta en un
 * HTTP 503 claro. Los SOAP Fault de negocio conservan su propio mensaje y no se marcan
 * erróneamente como indisponibilidad del proceso central.
 */
@WebFilter("/*")
public class ErroresSoapFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (ServicioCentralNoDisponibleException error) {
            HttpServletResponse http = (HttpServletResponse) response;
            if (!http.isCommitted()) {
                http.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                        "El servicio central no está disponible. Revise central.soap.url en "
                                + "~/.edEXT/web.properties y verifique que el central esté activo.");
            } else {
                throw new ServletException(error);
            }
        }
    }
}
