package com.grupo3_mat.servidorweb_edext.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/cursos")
public class CursosServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        StringBuilder destino = new StringBuilder(request.getContextPath()).append("/inicio");
        String separador = "?";
        for (String parametro : new String[]{"instituto", "categoria", "query", "tipo", "acceso"}) {
            String valor = request.getParameter(parametro);
            if (valor != null && !valor.isBlank()) {
                destino.append(separador)
                        .append(parametro)
                        .append("=")
                        .append(URLEncoder.encode(valor, StandardCharsets.UTF_8));
                separador = "&";
            }
        }
        response.sendRedirect(destino.toString());
    }
}
