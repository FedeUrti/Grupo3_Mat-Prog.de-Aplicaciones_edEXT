package com.grupo3_mat.servidorweb_edext.servlets;

import com.grupo3_mat.edEXT.Logica.Fabrica;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorProgramaFormacion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate; // <-- IMPORTANTE: Importar LocalDate

@WebServlet("/inscribir-programa")
public class InscribirProgramaServlet extends HttpServlet {

    private final IControladorProgramaFormacion icpf = Fabrica.getInstance().getIControladorProgramaFormacion();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        // 1. Validar que la sesión exista
        String nickname = (session != null) ? (String) session.getAttribute("usuarioNickname") : null;
        Boolean esDocente = (session != null) ? (Boolean) session.getAttribute("usuarioDocente") : null;

        // 2. Control de Acceso: Solo para Estudiantes (Logueado y NO Docente)
        if (nickname == null || !Boolean.FALSE.equals(esDocente)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Solo los estudiantes pueden inscribirse a programas de formación.");
            return;
        }

        String nombrePrograma = request.getParameter("nombrePrograma");

        if (nombrePrograma == null || nombrePrograma.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el nombre del programa.");
            return;
        }

        try {
            // 3. Invocar el controlador pasando la fecha actual (LocalDate.now())
            icpf.inscribirEstudianteAPrograma(nickname, nombrePrograma.trim(), LocalDate.now());

            // Redirigir con mensaje de éxito
            response.sendRedirect(request.getContextPath() + "/consulta-programa?nombre=" 
                    + java.net.URLEncoder.encode(nombrePrograma, "UTF-8") + "&inscripto=1");

        } catch (Exception e) {
            getServletContext().log("Error al inscribir estudiante " + nickname + " al programa " + nombrePrograma, e);
            
            // Si ya está inscripto u ocurre un error de negocio:
            String errorMsg = e.getMessage() != null ? e.getMessage() : "Ocurrió un error al procesar la inscripción.";
            response.sendRedirect(request.getContextPath() + "/consulta-programa?nombre=" 
                    + java.net.URLEncoder.encode(nombrePrograma, "UTF-8") + "&errorInscripcion=" 
                    + java.net.URLEncoder.encode(errorMsg, "UTF-8"));
        }
    }
}