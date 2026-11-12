package com.grupo3_mat.servidorweb_edext.servlets;

import com.grupo3_mat.servidorweb_edext.soap.generated.DtCurso;
import com.grupo3_mat.servidorweb_edext.soap.ControladoresRemotos;
import com.grupo3_mat.servidorweb_edext.soap.ServicioCentralNoDisponibleException;
import com.grupo3_mat.servidorweb_edext.soap.SoapValues;
import com.grupo3_mat.servidorweb_edext.soap.generated.CategoriaSoap;
import com.grupo3_mat.servidorweb_edext.soap.generated.CursoSoap;
import com.grupo3_mat.servidorweb_edext.soap.generated.InstitutoSoap;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/inicio")
public class InicioServlet extends HttpServlet {

    private final CursoSoap cursos = ControladoresRemotos.getCursoSoap();
    private final InstitutoSoap institutos = ControladoresRemotos.getInstitutoSoap();
    private final CategoriaSoap categorias = ControladoresRemotos.getCategoriaSoap();

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

            List<DtCurso> cursosCandidatos = SoapValues.consultarCursos(cursos, nombres);
            List<DtCurso> cursosPortada = new ArrayList<>();
            for (DtCurso curso : cursosCandidatos) {
                boolean coincideInstituto = institutoSeleccionado.isEmpty()
                        || institutoSeleccionado.equals(curso.getNomInstituto());
                boolean coincideCategoria = categoriaSeleccionada.isEmpty()
                        || curso.getCategorias().contains(categoriaSeleccionada);
                if (coincideInstituto && coincideCategoria) {
                    cursosPortada.add(curso);
                }
            }
            request.setAttribute("cursos", cursosPortada);
            Map<String, BigDecimal> promediosValoracion = new HashMap<>();
            Map<String, Integer> cantidadesValoracion = new HashMap<>();
            Map<String, Double> estrellasValoracion = new HashMap<>();
            for (DtCurso curso : cursosPortada) {
                BigDecimal promedio = curso.getPromedioValoracion();
                int cantidad = curso.getCantidadValoraciones();
                promediosValoracion.put(curso.getNombre(), promedio);
                cantidadesValoracion.put(curso.getNombre(), cantidad);
                estrellasValoracion.put(curso.getNombre(),
                        Math.round(promedio.doubleValue() * 2.0) / 2.0);
            }
            request.setAttribute("promediosValoracion", promediosValoracion);
            request.setAttribute("cantidadesValoracion", cantidadesValoracion);
            request.setAttribute("estrellasValoracion", estrellasValoracion);
            request.setAttribute("filtroInstituto", institutoSeleccionado);
            request.setAttribute("filtroCategoria", categoriaSeleccionada);
        } catch (Exception e) {
            if (e instanceof ServicioCentralNoDisponibleException error) {
                throw error;
            }
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
