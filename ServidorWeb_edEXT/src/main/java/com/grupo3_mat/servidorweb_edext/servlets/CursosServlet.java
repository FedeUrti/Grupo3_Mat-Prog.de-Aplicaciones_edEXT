package com.grupo3_mat.servidorweb_edext.servlets;

import com.grupo3_mat.edEXT.Logica.Fabrica;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCategoria;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorInstituto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@WebServlet("/cursos")
public class CursosServlet extends HttpServlet {

    // El controlador conserva las reglas y la persistencia del sistema central.
    private final IControladorCurso cursos = Fabrica.getInstance().getIControladorCurso();
    private final IControladorInstituto institutos = Fabrica.getInstance().getIControladorInstituto();
    private final IControladorCategoria categorias = Fabrica.getInstance().getIControladorCategoria();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1. Leer los filtros enviados desde el catálogo.
        String instituto = valor(request.getParameter("instituto"));
        String categoria = valor(request.getParameter("categoria"));
        String query = valor(request.getParameter("query"));

        try {
            // 2. Cargar las opciones de filtros desde los catálogos persistidos.
            request.setAttribute("institutos", institutos.listarInstitutos());
            request.setAttribute("categorias", categorias.listarCategorias());

            // 3. Recuperar por instituto/categoría cuando sea posible; sin filtros, leer todos los cursos.
            List<String> nombres;
            if (!instituto.isEmpty()) {
                nombres = cursos.listarCursosPorInstituto(instituto);
            } else if (!categoria.isEmpty()) {
                nombres = cursos.listarCursosPorCategoria(categoria);
            } else {
                nombres = cursos.listarCursos();
            }

            List<DtCurso> resultados = new ArrayList<>();
            for (String nombre : nombres) {
                DtCurso curso = cursos.consultarCurso(nombre);
                boolean coincideCategoria = categoria.isEmpty() || curso.getCategorias().contains(categoria);
                String descripcion = curso.getDescripcion() == null ? "" : curso.getDescripcion();
                boolean coincideBusqueda = query.isEmpty()
                        || curso.getNombre().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))
                        || descripcion.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT));
                if (coincideCategoria && coincideBusqueda) {
                    resultados.add(curso);
                }
            }
            request.setAttribute("cursos", resultados);
            request.setAttribute("filtroInstituto", instituto);
            request.setAttribute("filtroCategoria", categoria);
            request.setAttribute("query", query);
        } catch (Exception e) {
            // 4. Registrar el detalle técnico en el servidor y mostrar un mensaje seguro en la vista.
            getServletContext().log("No se pudo cargar el catálogo de cursos.", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("error", "No se pudo cargar el catálogo. Verificá la conexión con la base de datos.");
        }

        // 5. Enviar los resultados a la JSP; el servlet conserva el acceso a la base.
        request.getRequestDispatcher("/cursos.jsp").forward(request, response);
    }

    private static String valor(String value) {
        return value == null ? "" : value.trim();
    }
}
