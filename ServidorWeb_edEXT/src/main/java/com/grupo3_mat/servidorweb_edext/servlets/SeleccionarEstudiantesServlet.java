package com.grupo3_mat.servidorweb_edext.servlets;

import com.grupo3_mat.servidorweb_edext.soap.ControladoresRemotos;
import com.grupo3_mat.servidorweb_edext.soap.generated.CursoSoap;
import com.grupo3_mat.servidorweb_edext.soap.generated.EdicionSoap;
import com.grupo3_mat.servidorweb_edext.soap.generated.CategoriaSoap;
import com.grupo3_mat.servidorweb_edext.soap.generated.UsuarioSoap;
import com.grupo3_mat.servidorweb_edext.soap.generated.DtEdicionCurso;
import com.grupo3_mat.servidorweb_edext.soap.generated.DtInscripcionEdicion;
import com.grupo3_mat.servidorweb_edext.soap.generated.CriterioOrdenInscripciones;
import com.grupo3_mat.servidorweb_edext.soap.generated.EstadoInscripcion;
import com.grupo3_mat.servidorweb_edext.soap.generated.DtUsuario;
import com.grupo3_mat.servidorweb_edext.soap.generated.DtDocente;

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

@WebServlet("/seleccion-estudiantes")
public class SeleccionarEstudiantesServlet extends HttpServlet {

    private CursoSoap controladorCurso;
    private EdicionSoap controladorEdicion;
    private CategoriaSoap controladorCategoria;
    private UsuarioSoap controladorUsuario;

    @Override
    public void init() throws ServletException {

        this.controladorCurso = ControladoresRemotos.getCursoSoap();
        this.controladorEdicion = ControladoresRemotos.getEdicionSoap();
        this.controladorCategoria = ControladoresRemotos.getCategoriaSoap();
        this.controladorUsuario = ControladoresRemotos.getUsuarioSoap();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String usuarioNickname = (session != null) ? (String) session.getAttribute("usuarioNickname") : null;

        // Control de acceso: Exclusivo para Docentes autenticados
        if (usuarioNickname == null) {
            response.sendRedirect(request.getContextPath() + "/login?motivo=docente");
            return;
        }
        DtUsuario usuario = controladorUsuario.obtenerInfoUsuario(usuarioNickname);
        if (!(usuario instanceof DtDocente docente)) {
            response.sendRedirect(request.getContextPath() + "/inicio?acceso=docente");
            return;
        }

        String instituto = limpiar(request.getParameter("instituto"));
        String categoria = limpiar(request.getParameter("categoria"));
        String nombreCurso = limpiar(request.getParameter("curso"));
        String nombreEdicion = limpiar(request.getParameter("edicion"));
        String criterioParam = limpiar(request.getParameter("criterio"));

        try {
            // 1. Obtener y filtrar los institutos a los que pertenece el docente
            List<String> institutosDocente = docente.getInstitutos() == null
                    ? new ArrayList<>()
                    : docente.getInstitutos();
            request.setAttribute("institutos", institutosDocente);
            request.setAttribute("categorias", controladorCategoria.listarCategorias());
            request.setAttribute("filtroCategoria", categoria);

            // Si el instituto seleccionado no pertenece al docente, limpiarlo
            if (!instituto.isEmpty() && !institutosDocente.contains(instituto)) {
                instituto = "";
            }

            // 2. Cursos por Instituto permitido
            if (!instituto.isEmpty()) {
                request.setAttribute("filtroInstituto", instituto);
                List<String> cursos = controladorCurso.listarCursosPorInstituto(instituto);
                request.setAttribute("cursos", cursos != null ? cursos : new ArrayList<>());
            }

            // 3. Edición y validación de participación del docente
            if (!nombreCurso.isEmpty()) {
                request.setAttribute("cursoSeleccionado", nombreCurso);
                List<String> edicionesDocente = controladorEdicion.listarEdicionesDeDocente(usuarioNickname);
                List<String> edicionesDelCurso = new ArrayList<>();
                if (edicionesDocente != null) {
                    for (String edicionDocente : edicionesDocente) {
                        if (nombreCurso.equals(controladorEdicion.obtenerCursoDeEdicion(edicionDocente))) {
                            edicionesDelCurso.add(edicionDocente);
                        }
                    }
                }
                request.setAttribute("edicionesDelCurso", edicionesDelCurso);

                String nombreEdicionGestion = nombreEdicion;
                if (!nombreEdicionGestion.isEmpty()) {
                    String cursoDeEdicion = controladorEdicion.obtenerCursoDeEdicion(nombreEdicionGestion);
                    if (!nombreCurso.equals(cursoDeEdicion)) {
                        throw new IllegalArgumentException("La edición seleccionada no pertenece al curso indicado.");
                    }
                } else {
                    nombreEdicionGestion = controladorCurso.obtenerEdicionVigente(
                            nombreCurso, LocalDate.now().toString());
                    if ((nombreEdicionGestion == null || nombreEdicionGestion.isBlank())
                            && !edicionesDelCurso.isEmpty()) {
                        nombreEdicionGestion = seleccionarEdicionParaGestion(edicionesDelCurso);
                    }
                }

                if (nombreEdicionGestion != null && !nombreEdicionGestion.trim().isEmpty()) {
                    if (edicionesDocente != null && edicionesDocente.contains(nombreEdicionGestion)) {
                        DtEdicionCurso edicionVigente = controladorEdicion.mostrarDetalleEdicion(nombreEdicionGestion);

                        if (edicionVigente != null) {
                            request.setAttribute("edicionVigente", edicionVigente);

                            CriterioOrdenInscripciones criterio = CriterioOrdenInscripciones.FECHA_INSCRIPCION;
                            if ("prioridad".equalsIgnoreCase(criterioParam)) {
                                criterio = CriterioOrdenInscripciones.PRIORIDAD;
                                request.setAttribute("criterioSeleccionado", "prioridad");
                            } else {
                                request.setAttribute("criterioSeleccionado", "fecha");
                            }

                            List<DtInscripcionEdicion> inscripciones = edicionVigente.isCerrada()
                                    ? controladorEdicion.consultarEvaluacionesEdicion(usuarioNickname, edicionVigente.getNombre())
                                    : controladorEdicion.listarInscripcionesAEdicion(
                                            usuarioNickname, edicionVigente.getNombre(), criterio);

                            request.setAttribute("inscripciones", inscripciones != null ? inscripciones : new ArrayList<>());
                        } else {
                            request.setAttribute("mensajeInfo", "No se pudieron recuperar los detalles de la edición.");
                        }
                    } else {
                        request.setAttribute("mensajeInfo", "No participás como docente en la edición seleccionada.");
                    }
                } else {
                    request.setAttribute("mensajeInfo", edicionesDelCurso.isEmpty()
                            ? "No participás en ediciones de este curso."
                            : "El curso no tiene una edición vigente; podés consultar sus ediciones anteriores.");
                }
            }

            request.getRequestDispatcher("/seleccion-estudiantes.jsp").forward(request, response);

        } catch (Exception e) {
            getServletContext().log("Error en SeleccionEstudiantesServlet (GET): " + e.getMessage(), e);
            request.setAttribute("error", "Error al procesar la consulta: " + e.getMessage());
            request.getRequestDispatcher("/seleccion-estudiantes.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String usuarioNickname = (session != null) ? (String) session.getAttribute("usuarioNickname") : null;

        if (usuarioNickname == null) {
            response.sendRedirect(request.getContextPath() + "/login?motivo=docente");
            return;
        }
        if (!(controladorUsuario.obtenerInfoUsuario(usuarioNickname) instanceof DtDocente)) {
            response.sendRedirect(request.getContextPath() + "/inicio?acceso=docente");
            return;
        }

        String nombreEdicion = limpiar(request.getParameter("edicion"));
        String estudianteNickname = limpiar(request.getParameter("estudianteNickname"));
        String nuevoEstadoStr = limpiar(request.getParameter("nuevoEstado"));
        String accion = limpiar(request.getParameter("accion"));

        try {
            if (nombreEdicion.isEmpty()) {
                throw new IllegalArgumentException("Falta indicar la edición.");
            }
            if ("evaluar".equals(accion)) {
                if (estudianteNickname.isEmpty()) {
                    throw new IllegalArgumentException("Falta indicar el estudiante.");
                }
                int evaluacion;
                try {
                    evaluacion = Integer.parseInt(limpiar(request.getParameter("evaluacion")));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("La evaluación debe ser un número entero entre 1 y 12.", e);
                }
                controladorEdicion.evaluarInscripcion(
                        usuarioNickname, nombreEdicion, estudianteNickname, evaluacion);
                if (esSolicitudAjax(request)) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    return;
                }
                request.setAttribute("mensajeExito", "Se guardó la evaluación del estudiante " + estudianteNickname + ".");
            } else if ("cerrar".equals(accion)) {
                controladorEdicion.cerrarEdicion(usuarioNickname, nombreEdicion);
                request.setAttribute("mensajeExito", "La edición se cerró y sus evaluaciones ya están disponibles.");
            } else {
                if (estudianteNickname.isEmpty() || nuevoEstadoStr.isEmpty()) {
                    throw new IllegalArgumentException("Faltan datos para actualizar la inscripción.");
                }
                EstadoInscripcion nuevoEstado = EstadoInscripcion.valueOf(nuevoEstadoStr);
                if (nuevoEstado != EstadoInscripcion.ACEPTADA && nuevoEstado != EstadoInscripcion.RECHAZADA) {
                    throw new IllegalArgumentException("El estado seleccionado no es válido.");
                }
                DtEdicionCurso edicionAntes = controladorEdicion.mostrarDetalleEdicion(nombreEdicion);
                boolean seCompletaCupo = nuevoEstado == EstadoInscripcion.ACEPTADA
                        && edicionAntes != null
                        && edicionAntes.getCupo() > 0
                        && edicionAntes.getCupoDisponible() == 1;
                controladorEdicion.cambiarEstadoInscripcion(
                        usuarioNickname, nombreEdicion, estudianteNickname, nuevoEstado);
                String mensajeExito = "Se actualizó el estado de la inscripción del estudiante "
                        + estudianteNickname + ".";
                if (seCompletaCupo) {
                    mensajeExito += " Se completó el cupo y las solicitudes pendientes restantes quedaron rechazadas.";
                }
                request.setAttribute("mensajeExito", mensajeExito);
            }

            doGet(request, response);

        } catch (Exception e) {
            getServletContext().log("Error en SeleccionEstudiantesServlet (POST): " + e.getMessage(), e);
            if (esSolicitudAjax(request) && "evaluar".equals(accion)) {
                response.setStatus(e instanceof IllegalArgumentException || e instanceof IllegalStateException
                        ? HttpServletResponse.SC_BAD_REQUEST
                        : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("No se pudo guardar la evaluación. " + e.getMessage());
                return;
            }
            request.setAttribute("error", "No se pudo actualizar el estado: " + e.getMessage());
            response.setStatus(e instanceof IllegalArgumentException || e instanceof IllegalStateException
                    ? HttpServletResponse.SC_BAD_REQUEST
                    : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            doGet(request, response);
        }
    }

    private static boolean esSolicitudAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))
                || (request.getHeader("Accept") != null
                && request.getHeader("Accept").contains("application/json"));
    }

    private static String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private String seleccionarEdicionParaGestion(List<String> ediciones) throws Exception {
        DtEdicionCurso ultimaEdicion = null;
        DtEdicionCurso ultimaEdicionAbierta = null;
        for (String nombreEdicion : ediciones) {
            DtEdicionCurso edicion = controladorEdicion.mostrarDetalleEdicion(nombreEdicion);
            if (edicion == null) {
                continue;
            }
            if (esMasReciente(edicion, ultimaEdicion)) {
                ultimaEdicion = edicion;
            }
            if (!edicion.isCerrada() && esMasReciente(edicion, ultimaEdicionAbierta)) {
                ultimaEdicionAbierta = edicion;
            }
        }
        if (ultimaEdicionAbierta != null) {
            return ultimaEdicionAbierta.getNombre();
        }
        if (ultimaEdicion != null) {
            return ultimaEdicion.getNombre();
        }
        return ediciones.get(0);
    }

    private static boolean esMasReciente(DtEdicionCurso candidata, DtEdicionCurso actual) {
        return actual == null
                || (candidata.getFechaFin() != null
                && (actual.getFechaFin() == null || LocalDate.parse(candidata.getFechaFin())
                        .isAfter(LocalDate.parse(actual.getFechaFin()))));
    }
}