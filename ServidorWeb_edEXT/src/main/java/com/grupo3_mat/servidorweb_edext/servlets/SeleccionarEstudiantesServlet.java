package com.grupo3_mat.servidorweb_edext.servlets;

import com.grupo3_mat.edEXT.Logica.Fabrica;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorEdicion;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCategoria;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorUsuario;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTEdicionCurso;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTInscripcionEdicion;
import com.grupo3_mat.edEXT.Logica.DataTypes.CriterioOrdenInscripciones;
import com.grupo3_mat.edEXT.Logica.Clases.EstadoInscripcion;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtUsuario;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtDocente;

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

    private IControladorCurso controladorCurso;
    private IControladorEdicion controladorEdicion;
    private IControladorCategoria controladorCategoria;
    private IControladorUsuario controladorUsuario;

    @Override
    public void init() throws ServletException {
        Fabrica fabrica = Fabrica.getInstance();
        this.controladorCurso = fabrica.getIControladorCurso();
        this.controladorEdicion = fabrica.getIControladorEdicion();
        this.controladorCategoria = fabrica.getIControladorCategoria();
        this.controladorUsuario = fabrica.getIControladorUsuario();
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

            // 3. Edición vigente y validación de participación del docente
            if (!nombreCurso.isEmpty()) {
                request.setAttribute("cursoSeleccionado", nombreCurso);
                String nombreEdicionGestion = nombreEdicion;
                if (!nombreEdicionGestion.isEmpty()) {
                    String cursoDeEdicion = controladorEdicion.obtenerCursoDeEdicion(nombreEdicionGestion);
                    if (!nombreCurso.equals(cursoDeEdicion)) {
                        throw new IllegalArgumentException("La edición seleccionada no pertenece al curso indicado.");
                    }
                } else {
                    nombreEdicionGestion = controladorCurso.obtenerEdicionVigente(nombreCurso, LocalDate.now());
                }

                if (nombreEdicionGestion != null && !nombreEdicionGestion.trim().isEmpty()) {
                    // Obtener las ediciones en las que participa el docente para validar
                    List<String> edicionesDocente = controladorEdicion.listarEdicionesDeDocente(usuarioNickname);

                    if (edicionesDocente != null && edicionesDocente.contains(nombreEdicionGestion)) {
                        DTEdicionCurso edicionVigente = controladorEdicion.mostrarDetalleEdicion(nombreEdicionGestion);

                        if (edicionVigente != null) {
                            request.setAttribute("edicionVigente", edicionVigente);

                            CriterioOrdenInscripciones criterio = CriterioOrdenInscripciones.FECHA_INSCRIPCION;
                            if ("prioridad".equalsIgnoreCase(criterioParam)) {
                                criterio = CriterioOrdenInscripciones.PRIORIDAD;
                                request.setAttribute("criterioSeleccionado", "prioridad");
                            } else {
                                request.setAttribute("criterioSeleccionado", "fecha");
                            }

                            List<DTInscripcionEdicion> inscripciones =
                                controladorEdicion.listarInscripcionesAEdicion(usuarioNickname, edicionVigente.getNombre(), criterio);

                            request.setAttribute("inscripciones", inscripciones != null ? inscripciones : new ArrayList<>());
                        } else {
                            request.setAttribute("mensajeInfo", "No se pudieron recuperar los detalles de la edición vigente.");
                        }
                    } else {
                        request.setAttribute("mensajeInfo", "No participás como docente en la edición seleccionada.");
                    }
                } else {
                    request.setAttribute("mensajeInfo", "El curso seleccionado no posee una edición vigente actualmente.");
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

        try {
            if (nombreEdicion.isEmpty() || estudianteNickname.isEmpty() || nuevoEstadoStr.isEmpty()) {
                throw new IllegalArgumentException("Faltan datos para actualizar la inscripción.");
            }
            EstadoInscripcion nuevoEstado = EstadoInscripcion.valueOf(nuevoEstadoStr);
            if (nuevoEstado != EstadoInscripcion.ACEPTADA && nuevoEstado != EstadoInscripcion.RECHAZADA) {
                throw new IllegalArgumentException("El estado seleccionado no es válido.");
            }
            DTEdicionCurso edicionAntes = controladorEdicion.mostrarDetalleEdicion(nombreEdicion);
            boolean seCompletaCupo = nuevoEstado == EstadoInscripcion.ACEPTADA
                    && edicionAntes != null
                    && edicionAntes.getCupo() > 0
                    && edicionAntes.getCupoDisponible() == 1;
            controladorEdicion.cambiarEstadoInscripcion(usuarioNickname, nombreEdicion, estudianteNickname, nuevoEstado);
            String mensajeExito = "Se actualizó el estado de la inscripción del estudiante " + estudianteNickname + ".";
            if (seCompletaCupo) {
                mensajeExito += " Se completó el cupo y las solicitudes pendientes restantes quedaron rechazadas.";
            }
            request.setAttribute("mensajeExito", mensajeExito);

            doGet(request, response);

        } catch (Exception e) {
            getServletContext().log("Error en SeleccionEstudiantesServlet (POST): " + e.getMessage(), e);
            request.setAttribute("error", "No se pudo actualizar el estado: " + e.getMessage());
            response.setStatus(e instanceof IllegalArgumentException
                    ? HttpServletResponse.SC_BAD_REQUEST
                    : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            doGet(request, response);
        }
    }

    private static String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}