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
        try {
            // La portada obtiene los filtros y cursos del backend; no carga datos de ejemplo al iniciar.
            request.setAttribute("institutos", institutos.listarInstitutos());
            request.setAttribute("categorias", categorias.listarCategorias());

            List<DtCurso> cursosPortada = new ArrayList<>();
            for (String nombre : cursos.listarCursos()) {
                cursosPortada.add(cursos.consultarCurso(nombre));
            }
            request.setAttribute("cursos", cursosPortada);
        } catch (Exception e) {
            getServletContext().log("No se pudo cargar la página principal.", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("error", "No se pudo cargar el contenido. Verificá la conexión con la base de datos.");
        }

        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
