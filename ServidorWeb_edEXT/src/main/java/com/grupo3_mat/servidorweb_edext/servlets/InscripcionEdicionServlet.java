package com.grupo3_mat.servidorweb_edext.servlets;

import com.grupo3_mat.edEXT.Logica.Fabrica;
import com.grupo3_mat.edEXT.Logica.Clases.EstadoInscripcion;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTInscripcionEdicion;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtEstudiante;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtUsuario;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorEdicion;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorInstituto;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCategoria;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorUsuario;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTEdicionCurso;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/inscripcion-edicion")
public class InscripcionEdicionServlet extends HttpServlet {

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
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        String usuarioNickname = estudianteActual(request, response);
        if (usuarioNickname == null) {
            return;
        }
        mostrarInscripcion(request, response, usuarioNickname);
    }

    private void mostrarInscripcion(HttpServletRequest request, HttpServletResponse response,
            String usuarioNickname) throws ServletException, IOException {
        String instituto = limpiar(request.getParameter("instituto"));
        String categoria = limpiar(request.getParameter("categoria"));
        String nombreCurso = limpiar(request.getParameter("curso"));

        try {
            request.setAttribute("institutos", controladorInstituto.listarInstitutos());
            request.setAttribute("categorias", controladorCategoria.listarCategorias());
            List<String> todosLosCursos = controladorCurso.listarCursos();
            List<String> cursosCandidatos = !instituto.isEmpty()
                    ? controladorCurso.listarCursosPorInstituto(instituto)
                    : !categoria.isEmpty()
                            ? controladorCurso.listarCursosPorCategoria(categoria)
                            : todosLosCursos;
            List<String> cursosDisponibles = new ArrayList<>();
            for (String candidato : cursosCandidatos) {
                DtCurso curso = controladorCurso.consultarCurso(candidato);
                if (curso != null
                        && (instituto.isEmpty() || instituto.equals(curso.getNomInstituto()))
                        && (categoria.isEmpty() || curso.getCategorias().contains(categoria))) {
                    cursosDisponibles.add(candidato);
                }
            }
            request.setAttribute("cursos", cursosDisponibles);
            request.setAttribute("filtroInstituto", instituto);
            request.setAttribute("filtroCategoria", categoria);
                request.setAttribute("mensajeExito", null);

            if (!nombreCurso.isEmpty()
                    && !todosLosCursos.contains(nombreCurso)) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                request.setAttribute("error", "No se encontró el curso seleccionado.");
            } else if (!nombreCurso.isEmpty()) {
                request.setAttribute("cursoSeleccionado", nombreCurso);
                String nombreEdicionVigente = controladorCurso.obtenerEdicionVigente(nombreCurso, LocalDate.now());
                if (nombreEdicionVigente != null && !nombreEdicionVigente.isBlank()) {
                    DTEdicionCurso edicionVigente = controladorEdicion.mostrarDetalleEdicion(nombreEdicionVigente);
                    if (edicionVigente == null) {
                        request.setAttribute("mensajeInfo", "No se encontraron los datos de la edición vigente.");
                    } else {
                        request.setAttribute("edicionVigente", edicionVigente);
                        EstadoInscripcion estadoInscripcion = estadoInscripcion(usuarioNickname, nombreEdicionVigente);
                        request.setAttribute("estadoInscripcion",
                            estadoInscripcion == null ? null : estadoInscripcion.name());
                        request.setAttribute("yaInscripto", estadoInscripcion != null);
                        if (estadoInscripcion != null && "1".equals(request.getParameter("inscripto"))) {
                            request.setAttribute("mensajeExito", mensajeInscripcion(estadoInscripcion));
                        }
                    }
                } else {
                    request.setAttribute("mensajeInfo",
                            "Este curso no tiene una edición abierta para inscripción en este momento.");
                }
            }
        } catch (Exception e) {
            getServletContext().log("No se pudieron cargar los datos para la inscripción.", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("error", "No se pudieron consultar las inscripciones. Intentá nuevamente.");
        }
        request.getRequestDispatcher("/inscripcion-edicion.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String usuarioNickname = estudianteActual(request, response);
        if (usuarioNickname == null) {
            return;
        }

        String nombreCurso = limpiar(request.getParameter("curso"));
        String nombreEdicion = limpiar(request.getParameter("edicion"));

        try {
            if (nombreCurso.isEmpty() || nombreEdicion.isEmpty()) {
                throw new IllegalArgumentException("Seleccioná un curso y una edición vigente.");
            }
            if (!controladorCurso.listarCursos().contains(nombreCurso)) {
                throw new IllegalArgumentException("No se encontró el curso seleccionado.");
            }
            String edicionVigente = controladorCurso.obtenerEdicionVigente(nombreCurso, LocalDate.now());
            if (!nombreEdicion.equals(edicionVigente)) {
                throw new IllegalArgumentException("La edición ya no está abierta para inscripción.");
            }
            List<String> edicionesEstudiante = controladorEdicion.listarEdicionesDeEstudiante(usuarioNickname);
            if (edicionesEstudiante != null && edicionesEstudiante.contains(nombreEdicion)) {
                throw new IllegalArgumentException("Ya estás inscripto a esta edición.");
            }
            DTEdicionCurso edicion = controladorEdicion.mostrarDetalleEdicion(nombreEdicion);
            if (edicion == null) {
                throw new IllegalArgumentException("No se encontró la edición seleccionada.");
            }
            if (edicion.getCupo() > 0 && edicion.getCupoDisponible() <= 0) {
                throw new IllegalArgumentException("La edición ya no tiene cupos disponibles.");
            }
            controladorEdicion.inscribirEstudianteAEdicion(usuarioNickname, nombreEdicion, LocalDate.now());
            StringBuilder redirectUrl = new StringBuilder(request.getContextPath())
                    .append("/inscripcion-edicion?curso=")
                    .append(URLEncoder.encode(nombreCurso, StandardCharsets.UTF_8))
                    .append("&inscripto=1");
            String instituto = limpiar(request.getParameter("instituto"));
            String categoria = limpiar(request.getParameter("categoria"));
            if (!instituto.isEmpty()) {
                redirectUrl.append("&instituto=").append(URLEncoder.encode(instituto, StandardCharsets.UTF_8));
            }
            if (!categoria.isEmpty()) {
                redirectUrl.append("&categoria=").append(URLEncoder.encode(categoria, StandardCharsets.UTF_8));
            }
            response.sendRedirect(redirectUrl.toString());

        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                request.setAttribute("error", e.getMessage());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else {
                getServletContext().log("No se pudo procesar la inscripción a la edición.", e);
                request.setAttribute("error", "No se pudo completar la inscripción. Intentá nuevamente.");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            mostrarInscripcion(request, response, usuarioNickname);
        }
    }

    private String estudianteActual(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        String nickname = session == null ? null : (String) session.getAttribute("usuarioNickname");
        if (nickname == null) {
            response.sendRedirect(request.getContextPath() + "/login?motivo=inscripcion");
            return null;
        }
        DtUsuario usuario = controladorUsuario.obtenerInfoUsuario(nickname);
        if (usuario instanceof DtEstudiante) {
            return nickname;
        }
        if (usuario == null && session != null) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login?motivo=inscripcion");
            return null;
        }
        response.sendRedirect(request.getContextPath() + "/inicio?acceso=estudiante");
        return null;
    }

    private EstadoInscripcion estadoInscripcion(String nickname, String nombreEdicion) throws Exception {
        DtUsuario usuario = controladorUsuario.obtenerInfoUsuario(nickname);
        if (usuario instanceof DtEstudiante estudiante) {
            for (DTInscripcionEdicion inscripcion : estudiante.getInscripcionesEdicion()) {
                if (nombreEdicion.equals(inscripcion.getNombreEdicion())) {
                    return inscripcion.getEstado();
                }
            }
        }
        return null;
    }

    private static String mensajeInscripcion(EstadoInscripcion estado) {
        return switch (estado) {
            case INSCRIPTO -> "Ya te inscribiste a esta edición. Tu inscripción está pendiente de ser aceptada.";
            case ACEPTADA -> "Ya estás inscripto a esta edición y tu inscripción fue aceptada.";
            case RECHAZADA -> "Ya estás inscripto a esta edición, pero tu inscripción fue rechazada.";
        };
    }

    private static String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}