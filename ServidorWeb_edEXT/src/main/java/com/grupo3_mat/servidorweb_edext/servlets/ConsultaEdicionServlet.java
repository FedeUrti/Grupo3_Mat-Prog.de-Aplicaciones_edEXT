package com.grupo3_mat.servidorweb_edext.servlets;

import com.grupo3_mat.edEXT.Logica.Fabrica;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorEdicion;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorInstituto;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCategoria;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTEdicionCurso;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtCurso;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/consulta-edicion-curso")
public class ConsultaEdicionServlet extends HttpServlet {

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

            // 2. Paso 3 & 4: Filtrado de cursos por Instituto o Categoría
            if (!instituto.isEmpty()) {
                request.setAttribute("filtroInstituto", instituto);
                List<String> cursosInstituto = controladorCurso.listarCursosPorInstituto(instituto);
                request.setAttribute("cursos", cursosInstituto);
            } else if (!categoria.isEmpty()) {
                request.setAttribute("filtroCategoria", categoria);
                List<String> cursosCategoria = controladorCurso.listarCursosPorCategoria(categoria);
                request.setAttribute("cursos", cursosCategoria);
            }

            // 3. Paso 5 & 6: Si eligió un curso, listar sus ediciones asociadas
            if (!nombreCurso.isEmpty()) {
                request.setAttribute("cursoSeleccionado", nombreCurso);
                
                // Información básica del curso para contexto visual
                DtCurso dtCurso = controladorCurso.consultarCurso(nombreCurso);
                request.setAttribute("dtCurso", dtCurso);

                // Listar nombres de ediciones asociadas al curso
                List<String> ediciones = controladorEdicion.listarEdicionesPorCurso(nombreCurso);
                request.setAttribute("ediciones", ediciones);
            }

            // 4. Paso 7 & 8: Si eligió una edición, obtener todos los datos detallados
            if (!nombreEdicion.isEmpty()) {
                request.setAttribute("edicionSeleccionada", nombreEdicion);

                DTEdicionCurso dtEdicion = controladorEdicion.mostrarDetalleEdicion(nombreEdicion);
                
                if (dtEdicion != null) {
                    request.setAttribute("edicion", dtEdicion);
                } else {
                    request.setAttribute("error", "No se encontró la edición especificada.");
                }
            }

            // Renderizar la vista JSP de consulta
            request.getRequestDispatcher("/consulta-edicion-curso.jsp").forward(request, response);

        } catch (Exception e) {
            getServletContext().log("Error en ConsultaEdicionServlet: " + e.getMessage(), e);
            request.setAttribute("error", "Ocurrió un error al procesar la consulta: " + e.getMessage());
            request.getRequestDispatcher("/consulta-edicion-curso.jsp").forward(request, response);
        }
    }

    private void cargarCatalogos(HttpServletRequest request) {
        try {
            List<String> institutos = controladorInstituto.listarInstitutos();
            List<String> categorias = controladorCategoria.listarCategorias();
            
            request.setAttribute("institutos", institutos != null ? institutos : new ArrayList<>());
            request.setAttribute("categorias", categorias != null ? categorias : new ArrayList<>());
        } catch (Exception e) {
            getServletContext().log("No se pudieron cargar los institutos/categorías.", e);
        }
    }

    private static String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}