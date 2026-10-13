package com.grupo3_mat.servidorweb_edext.servlets;

import com.grupo3_mat.edEXT.Logica.Fabrica;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorEdicion;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorInstituto;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCategoria;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTEdicionCurso;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtCurso;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtDocente;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtUsuario;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorUsuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/consulta-edicion-curso")
public class ConsultaEdicionServlet extends HttpServlet {

    private IControladorCurso controladorCurso;
    private IControladorEdicion controladorEdicion;
    private IControladorInstituto controladorInstituto;
    private IControladorCategoria controladorCategoria;
    private IControladorUsuario controladorUsuario;

    @Override
    public void init() throws ServletException {
        Fabrica fabrica = Fabrica.getInstance();
        this.controladorCurso = fabrica.getIControladorCurso();
        this.controladorEdicion = fabrica.getIControladorEdicion();
        this.controladorInstituto = fabrica.getIControladorInstituto();
        this.controladorCategoria = fabrica.getIControladorCategoria();
        this.controladorUsuario = fabrica.getIControladorUsuario();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Forzar al navegador a no almacenar en caché la página de consulta
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        String instituto = limpiar(request.getParameter("instituto"));
        String categoria = limpiar(request.getParameter("categoria"));
        String nombreCurso = limpiar(request.getParameter("curso"));
        String nombreEdicion = limpiar(request.getParameter("edicion"));

        try {
            // 1. Cargar siempre los catálogos para los filtros de búsqueda inicial
            cargarCatalogos(request);
            request.setAttribute("filtroInstituto", instituto);
            request.setAttribute("filtroCategoria", categoria);
            List<String> cursosDisponibles = null;

            // 2. Paso 3 & 4: Filtrado de cursos por Instituto o Categoría
            if (!instituto.isEmpty() || !categoria.isEmpty()) {
                List<String> candidatos = !instituto.isEmpty()
                        ? controladorCurso.listarCursosPorInstituto(instituto)
                        : controladorCurso.listarCursosPorCategoria(categoria);
                cursosDisponibles = new ArrayList<>();
                for (String candidato : candidatos) {
                    DtCurso curso = controladorCurso.consultarCurso(candidato);
                    if (curso != null && (categoria.isEmpty() || curso.getCategorias().contains(categoria))) {
                        cursosDisponibles.add(candidato);
                    }
                }
            } else if (!nombreCurso.isEmpty()) {
                cursosDisponibles = controladorCurso.listarCursos();
            }
            if (cursosDisponibles != null) {
                request.setAttribute("cursos", cursosDisponibles);
                if (!nombreCurso.isEmpty() && !cursosDisponibles.contains(nombreCurso)) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "No se encontró el curso solicitado.");
                    return;
                }
            }

            // 3. Paso 5 & 6: Si eligió un curso, listar sus ediciones asociadas
            if (!nombreCurso.isEmpty()) {
                request.setAttribute("cursoSeleccionado", nombreCurso);

                // Listar nombres de ediciones asociadas al curso
                List<String> ediciones = controladorEdicion.listarEdicionesPorCurso(nombreCurso);
                request.setAttribute("ediciones", ediciones);
            }

            // 4. Paso 7 & 8: Si eligió una edición, obtener todos los datos detallados
            if (!nombreEdicion.isEmpty()) {
                String cursoDeEdicion = controladorEdicion.obtenerCursoDeEdicion(nombreEdicion);
                if (cursoDeEdicion == null || cursoDeEdicion.isBlank()
                        || (!nombreCurso.isEmpty() && !nombreCurso.equals(cursoDeEdicion))) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "No se encontró la edición solicitada.");
                    return;
                }
                if (nombreCurso.isEmpty()) {
                    nombreCurso = cursoDeEdicion;
                    request.setAttribute("cursoSeleccionado", nombreCurso);
                    request.setAttribute("ediciones", controladorEdicion.listarEdicionesPorCurso(nombreCurso));
                }
                request.setAttribute("edicionSeleccionada", nombreEdicion);

                DTEdicionCurso dtEdicion = controladorEdicion.mostrarDetalleEdicion(nombreEdicion);

                if (dtEdicion != null) {
                    request.setAttribute("edicion", dtEdicion);
                    Map<String, DtDocente> docentesEdicion = obtenerDocentes(dtEdicion);
                    request.setAttribute("docentesEdicion", new ArrayList<>(docentesEdicion.values()));
                    Map<String, String> imagenesDocentesEdicion = new LinkedHashMap<>();
                    for (Map.Entry<String, DtDocente> docente : docentesEdicion.entrySet()) {
                        imagenesDocentesEdicion.put(docente.getKey(), UsuarioServlet.imagenPerfil(docente.getValue()));
                    }
                    request.setAttribute("imagenesDocentesEdicion", imagenesDocentesEdicion);
                } else {
                    request.setAttribute("error", "No se encontró la edición especificada.");
                }
            }

            // Renderizar la vista JSP de consulta
            request.getRequestDispatcher("/consulta-edicion-curso.jsp").forward(request, response);

        } catch (Exception e) {
            getServletContext().log("No se pudo consultar la edición del curso.", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("error", "No se pudo consultar la edición. Intentá nuevamente.");
            request.getRequestDispatcher("/consulta-edicion-curso.jsp").forward(request, response);
        }
    }

    private void cargarCatalogos(HttpServletRequest request) throws Exception {
        request.setAttribute("institutos", controladorInstituto.listarInstitutos());
        request.setAttribute("categorias", controladorCategoria.listarCategorias());
    }

    private Map<String, DtDocente> obtenerDocentes(DTEdicionCurso edicion) {
        Map<String, DtDocente> docentes = new LinkedHashMap<>();
        for (String nickname : edicion.getDocentes()) {
            DtUsuario usuario = controladorUsuario.obtenerInfoUsuario(nickname);
            if (usuario instanceof DtDocente docente) {
                docentes.put(nickname, docente);
            } else {
                getServletContext().log("No se encontró el perfil del docente asignado " + nickname
                        + " en la edición " + edicion.getNombre() + ".");
            }
        }
        return docentes;
    }

    private static String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}