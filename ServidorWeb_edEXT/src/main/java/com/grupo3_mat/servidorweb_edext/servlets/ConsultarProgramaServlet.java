package com.grupo3_mat.servidorweb_edext.servlets;

import com.grupo3_mat.servidorweb_edext.soap.generated.DtCurso;
import com.grupo3_mat.servidorweb_edext.soap.generated.DtDocente;
import com.grupo3_mat.servidorweb_edext.soap.generated.DtProgramaFormacion;
import com.grupo3_mat.servidorweb_edext.soap.ControladoresRemotos;
import com.grupo3_mat.servidorweb_edext.soap.SoapValues;
import com.grupo3_mat.servidorweb_edext.soap.generated.CategoriaSoap;
import com.grupo3_mat.servidorweb_edext.soap.generated.CursoSoap;
import com.grupo3_mat.servidorweb_edext.soap.generated.InstitutoSoap;
import com.grupo3_mat.servidorweb_edext.soap.generated.ProgramaFormacionSoap;
import com.grupo3_mat.servidorweb_edext.soap.generated.UsuarioSoap;
import com.grupo3_mat.servidorweb_edext.soap.generated.DtEstudiante;
import com.grupo3_mat.servidorweb_edext.soap.generated.DtUsuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

@WebServlet("/consulta-programa")
public class ConsultarProgramaServlet extends HttpServlet {

    private final ProgramaFormacionSoap controladorPrograma = ControladoresRemotos.getProgramaFormacionSoap();
    private final CursoSoap controladorCurso = ControladoresRemotos.getCursoSoap();
    private final InstitutoSoap controladorInstituto = ControladoresRemotos.getInstitutoSoap();
    private final CategoriaSoap controladorCategoria = ControladoresRemotos.getCategoriaSoap();
    private final UsuarioSoap controladorUsuario = ControladoresRemotos.getUsuarioSoap();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. DESACTIVAR CACHÉ (Fuerza la lectura limpia de datos tras agregar un curso)
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies

        String nombrePrograma = request.getParameter("nombre");

        if (nombrePrograma == null || nombrePrograma.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/programas");
            return;
        }

        try {
            cargarSidebar(request);
            // 2. Obtener la información del Programa de Formación
            DtProgramaFormacion dtPrograma = controladorPrograma.consultarPrograma(nombrePrograma.trim());

            if (dtPrograma == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                request.setAttribute("error", "No se encontró el programa de formación especificado.");
            } else {
                request.setAttribute("programa", dtPrograma);
                boolean estudianteInscripto = false;
                jakarta.servlet.http.HttpSession sesion = request.getSession(false);
                String nickname = sesion == null ? null : (String) sesion.getAttribute("usuarioNickname");
                if (nickname != null) {
                    DtUsuario usuario = controladorUsuario.obtenerInfoUsuario(nickname);
                    estudianteInscripto = usuario instanceof DtEstudiante estudiante
                            && estudiante.getProgramasInscripto().contains(dtPrograma.getNombre());
                }
                request.setAttribute("estudianteInscripto", estudianteInscripto);

                // 3. Fijar el orden de las categorías para evitar que cambien de lugar al dar F5
                if (dtPrograma.getCategorias() != null) {
                    List<String> categoriasOrdenadas = new ArrayList<>(dtPrograma.getCategorias());
                    Collections.sort(categoriasOrdenadas);
                    request.setAttribute("categoriasOrdenadas", categoriasOrdenadas);
                }

                // 4. Cargar el detalle de los cursos pertenecientes a este programa
                List<DtCurso> cursosDetalle = new ArrayList<>();
                if (dtPrograma.getCursos() != null) {
                    for (DtCurso curso : SoapValues.consultarCursos(
                            controladorCurso, dtPrograma.getCursos())) {
                        if (curso != null) {
                            cursosDetalle.add(curso);
                        }
                    }
                }
                request.setAttribute("cursosDetalle", cursosDetalle);

                // El modal solo ofrece cursos de los institutos del docente responsable.
                List<String> cursosDocente = List.of();
                if (nickname != null && nickname.equals(dtPrograma.getNicknameDocente())) {
                    DtUsuario usuario = controladorUsuario.obtenerInfoUsuario(nickname);
                    if (usuario instanceof DtDocente docente) {
                        cursosDocente = listarCursosDeInstitutos(docente);
                    }
                }
                request.setAttribute("todosLosCursos", cursosDocente);
            }

        } catch (Exception e) {
            getServletContext().log("Error al consultar el programa " + nombrePrograma, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("error", "Ocurrió un error al cargar la información del programa.");
        }

        request.getRequestDispatcher("/WEB-INF/views/consulta-programa.jsp").forward(request, response);
    }

    private void cargarSidebar(HttpServletRequest request) throws Exception {
        request.setAttribute("institutos", controladorInstituto.listarInstitutos());
        request.setAttribute("categorias", controladorCategoria.listarCategorias());
        request.setAttribute("filtroInstituto", "");
        request.setAttribute("filtroCategoria", "");
    }

    private List<String> listarCursosDeInstitutos(DtDocente docente) {
        LinkedHashSet<String> cursos = new LinkedHashSet<>();
        if (docente.getInstitutos() != null) {
            for (String instituto : docente.getInstitutos()) {
                List<String> cursosInstituto = controladorCurso.listarCursosPorInstituto(instituto);
                if (cursosInstituto != null) {
                    cursos.addAll(cursosInstituto);
                }
            }
        }
        return new ArrayList<>(cursos);
    }
}