package com.grupo3_mat.servidorweb_edext.servlets;

import com.grupo3_mat.edEXT.Logica.DataTypes.DtCurso;
import com.grupo3_mat.edEXT.Logica.Fabrica;
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

@WebServlet("/inicio")
public class InicioServlet extends HttpServlet {

    private final IControladorCurso cursos = Fabrica.getInstance().getIControladorCurso();
    private final IControladorInstituto institutos = Fabrica.getInstance().getIControladorInstituto();
    private final IControladorCategoria categorias = Fabrica.getInstance().getIControladorCategoria();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String institutoSeleccionado = valor(request.getParameter("instituto"));
        String categoriaSeleccionada = valor(request.getParameter("categoria"));

        try {
            // La portada obtiene los filtros y cursos del backend; no carga datos de ejemplo al iniciar.
            request.setAttribute("institutos", institutos.listarInstitutos());
            request.setAttribute("categorias", categorias.listarCategorias());

            // Se consultan candidatos por el filtro más específico y luego se aplican los restantes.
            List<String> nombres;
            if (!institutoSeleccionado.isEmpty()) {
                nombres = cursos.listarCursosPorInstituto(institutoSeleccionado);
            } else if (!categoriaSeleccionada.isEmpty()) {
                nombres = cursos.listarCursosPorCategoria(categoriaSeleccionada);
            } else {
                nombres = cursos.listarCursos();
            }

            List<DtCurso> cursosPortada = new ArrayList<>();
            for (String nombre : nombres) {
                DtCurso curso = cursos.consultarCurso(nombre);
                boolean coincideInstituto = institutoSeleccionado.isEmpty()
                        || institutoSeleccionado.equals(curso.getNomInstituto());
                boolean coincideCategoria = categoriaSeleccionada.isEmpty()
                        || curso.getCategorias().contains(categoriaSeleccionada);
                if (coincideInstituto && coincideCategoria) {
                    cursosPortada.add(curso);
                }
            }
            request.setAttribute("cursos", cursosPortada);
            request.setAttribute("filtroInstituto", institutoSeleccionado);
            request.setAttribute("filtroCategoria", categoriaSeleccionada);
        } catch (Exception e) {
            getServletContext().log("No se pudo cargar la página principal.", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("error", "No se pudo cargar el contenido. Verificá la conexión con la base de datos.");
        }

        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    private static String valor(String value) {
        return value == null ? "" : value.trim();
    }
}
