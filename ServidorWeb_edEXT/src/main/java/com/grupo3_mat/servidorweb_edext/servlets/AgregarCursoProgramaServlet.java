package com.grupo3_mat.servidorweb_edext.servlets;

import com.grupo3_mat.edEXT.Logica.Fabrica;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCategoria;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorInstituto;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorProgramaFormacion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;

@WebServlet("/agregar-curso-programa")
public class AgregarCursoProgramaServlet extends HttpServlet {

    private final IControladorProgramaFormacion controladorPrograma = Fabrica.getInstance().getIControladorProgramaFormacion();
    private final IControladorCurso controladorCurso = Fabrica.getInstance().getIControladorCurso();
    private final IControladorInstituto controladorInstituto = Fabrica.getInstance().getIControladorInstituto();
    private final IControladorCategoria controladorCategoria = Fabrica.getInstance().getIControladorCategoria();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String nickname = session == null ? null : (String) session.getAttribute("usuarioNickname");
        Boolean esDocente = session == null ? null : (Boolean) session.getAttribute("usuarioDocente");
        if (nickname == null) {
            response.sendRedirect(request.getContextPath() + "/login?motivo=docente");
            return;
        }
        if (!Boolean.TRUE.equals(esDocente)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Solo un docente puede agregar cursos a un programa.");
            return;
        }
        try {
            var programasDocente = controladorPrograma.listarProgramas().stream()
                    .filter(programa -> nickname.equals(programa.getNicknameDocente()))
                    .toList();
            request.setAttribute("programas", programasDocente);
            request.setAttribute("cursos", controladorCurso.listarCursos());
            String programaSeleccionado = request.getParameter("programa");
            if (programaSeleccionado != null && !programaSeleccionado.isBlank()
                    && programasDocente.stream().noneMatch(programa -> programaSeleccionado.equals(programa.getNombre()))) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Solo podés agregar cursos a programas que creaste.");
                return;
            }
            request.setAttribute("programaSeleccionado", programaSeleccionado);
            cargarSidebar(request);
            request.getRequestDispatcher("/WEB-INF/views/agregar-curso-programa.jsp").forward(request, response);
        } catch (Exception e) {
            getServletContext().log("No se pudieron cargar las opciones para agregar un curso al programa.", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No se pudieron cargar programas y cursos.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        String nickname = (session != null) ? (String) session.getAttribute("usuarioNickname") : null;
        Boolean esDocente = (session != null) ? (Boolean) session.getAttribute("usuarioDocente") : null;

        if (nickname == null || esDocente == null || !esDocente) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado: Se requieren permisos de Docente.");
            return;
        }

        String nombrePrograma = request.getParameter("nombrePrograma");
        String nombreCurso = request.getParameter("nombreCurso");

        if (nombrePrograma == null || nombrePrograma.trim().isEmpty() ||
            nombreCurso == null || nombreCurso.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Se requieren el programa y el curso.");
            return;
        }

        try {
            // ORDEN CORREGIDO: primer parámetro = nombrePrograma, segundo parámetro = nombreCurso
            controladorPrograma.agregarCursoAPrograma(nickname, nombrePrograma.trim(), nombreCurso.trim());

            response.sendRedirect(request.getContextPath() + "/consulta-programa?nombre=" 
                    + URLEncoder.encode(nombrePrograma.trim(), "UTF-8") + "&agregado=1");

        } catch (Exception e) {
            getServletContext().log("Error al agregar curso " + nombreCurso + " al programa " + nombrePrograma, e);
            if (e instanceof SecurityException) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
                return;
            }
            String errorMsg = (e.getMessage() != null) ? e.getMessage() : "No se pudo agregar el curso al programa.";
            response.sendRedirect(request.getContextPath() + "/consulta-programa?nombre=" 
                    + URLEncoder.encode(nombrePrograma.trim(), "UTF-8") + "&error=" 
                    + URLEncoder.encode(errorMsg, "UTF-8"));
        }
    }

    private void cargarSidebar(HttpServletRequest request) throws Exception {
        request.setAttribute("institutos", controladorInstituto.listarInstitutos());
        request.setAttribute("categorias", controladorCategoria.listarCategorias());
        request.setAttribute("filtroInstituto", "");
        request.setAttribute("filtroCategoria", "");
    }
}