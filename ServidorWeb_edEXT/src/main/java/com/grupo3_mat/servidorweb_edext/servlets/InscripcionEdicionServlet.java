package com.grupo3_mat.servidorweb_edext.servlets;

import com.grupo3_mat.edEXT.Logica.Fabrica;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorEdicion;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorInstituto;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCategoria;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTEdicionCurso;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/inscripcion-edicion")
public class InscripcionEdicionServlet extends HttpServlet {

    private IControladorCurso controladorCurso;
    private IControladorEdicion controladorEdicion;
    private IControladorInstituto controladorInstituto;
    private IControladorCategoria controladorCategoria;

    @Override
    public void init() throws ServletException {
        Fabrica fabrica = Fabrica.getInstance();
        this.controladorCurso = fabrica.getIControladorCurso();
        this.controladorEdicion = fabrica.getIControladorEdicion();
        this.controladorInstituto = fabrica.getIControladorInstituto();
        this.controladorCategoria = fabrica.getIControladorCategoria();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Forzar al navegador a no almacenar en caché la página de inscripción
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        HttpSession session = request.getSession(false);
        String usuarioNickname = (session != null) ? (String) session.getAttribute("usuarioNickname") : null;
        Boolean esDocente = (session != null) ? (Boolean) session.getAttribute("usuarioDocente") : null;

        // Control de acceso: Solo estudiantes autenticados
        if (usuarioNickname == null || Boolean.TRUE.equals(esDocente)) {
            response.sendRedirect("login");
            return;
        }

        String instituto = limpiar(request.getParameter("instituto"));
        String categoria = limpiar(request.getParameter("categoria"));
        String nombreCurso = limpiar(request.getParameter("curso"));

        try {
            cargarCatalogos(request);

            // 1. Filtrado de cursos por Instituto o Categoría
            if (!instituto.isEmpty()) {
                request.setAttribute("filtroInstituto", instituto);
                List<String> cursos = controladorCurso.listarCursosPorInstituto(instituto);
                request.setAttribute("cursos", cursos != null ? cursos : new ArrayList<>());
            } else if (!categoria.isEmpty()) {
                request.setAttribute("filtroCategoria", categoria);
                List<String> cursos = controladorCurso.listarCursosPorCategoria(categoria);
                request.setAttribute("cursos", cursos != null ? cursos : new ArrayList<>());
            }

            // 2. Consulta de Edición Vigente
            if (!nombreCurso.isEmpty()) {
                request.setAttribute("cursoSeleccionado", nombreCurso);

                String nombreEdicionVigente = null;
                try {
                    nombreEdicionVigente = controladorCurso.obtenerEdicionVigente(nombreCurso, LocalDate.now());
                } catch (Exception e) {
                    getServletContext().log("El curso " + nombreCurso + " no posee edición vigente: " + e.getMessage());
                }

                if (nombreEdicionVigente != null && !nombreEdicionVigente.trim().isEmpty()) {
                    DTEdicionCurso edicionVigente = controladorEdicion.mostrarDetalleEdicion(nombreEdicionVigente);

                    if (edicionVigente != null) {
                        request.setAttribute("edicionVigente", edicionVigente);

                        // 3. Verificación de inscripción existente mediante listarEdicionesDeEstudiante
                        List<String> edicionesEstudiante = controladorEdicion.listarEdicionesDeEstudiante(usuarioNickname);
                        boolean yaInscripto = edicionesEstudiante != null && edicionesEstudiante.contains(edicionVigente.getNombre());
                        
                        request.setAttribute("yaInscripto", yaInscripto);
                    } else {
                        request.setAttribute("mensajeInfo", "No se pudieron recuperar los detalles de la edición vigente.");
                    }
                } else {
                    request.setAttribute("mensajeInfo", "El curso seleccionado no posee una edición vigente actualmente.");
                }
            }

            request.getRequestDispatcher("/inscripcion-edicion.jsp").forward(request, response);

        } catch (Exception e) {
            getServletContext().log("Error en InscripcionEdicionServlet (GET): " + e.getMessage(), e);
            request.setAttribute("error", "Error al consultar los datos: " + e.getMessage());
            request.getRequestDispatcher("/inscripcion-edicion.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String usuarioNickname = (session != null) ? (String) session.getAttribute("usuarioNickname") : null;
        Boolean esDocente = (session != null) ? (Boolean) session.getAttribute("usuarioDocente") : null;

        if (usuarioNickname == null || Boolean.TRUE.equals(esDocente)) {
            response.sendRedirect("login");
            return;
        }

        String nombreEdicion = limpiar(request.getParameter("edicion"));

        try {
            // Inscribir pasando nickname, nombre de edición y fecha actual
            controladorEdicion.inscribirEstudianteAEdicion(usuarioNickname, nombreEdicion, LocalDate.now());
            request.setAttribute("mensajeExito", "¡Te has inscripto correctamente a la edición " + nombreEdicion + "!");

            doGet(request, response);

        } catch (Exception e) {
            getServletContext().log("Error en InscripcionEdicionServlet (POST): " + e.getMessage(), e);
            request.setAttribute("error", "No se pudo procesar la inscripción: " + e.getMessage());
            doGet(request, response);
        }
    }

    private void cargarCatalogos(HttpServletRequest request) {
        List<String> institutos = new ArrayList<>();
        List<String> categorias = new ArrayList<>();
        try {
            List<String> instRes = controladorInstituto.listarInstitutos();
            if (instRes != null) institutos = instRes;
        } catch (Exception ignored) {}

        try {
            List<String> catRes = controladorCategoria.listarCategorias();
            if (catRes != null) categorias = catRes;
        } catch (Exception ignored) {}

        request.setAttribute("institutos", institutos);
        request.setAttribute("categorias", categorias);
    }

    private static String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}