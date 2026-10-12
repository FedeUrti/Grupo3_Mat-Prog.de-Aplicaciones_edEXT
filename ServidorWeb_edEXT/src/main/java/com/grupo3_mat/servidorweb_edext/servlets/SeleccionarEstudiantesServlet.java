package com.grupo3_mat.servidorweb_edext.servlets;

import com.grupo3_mat.edEXT.Logica.Fabrica;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorEdicion;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorInstituto;
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
    private IControladorInstituto controladorInstituto;
    private IControladorUsuario controladorUsuario;

    @Override
    public void init() throws ServletException {
        Fabrica fabrica = Fabrica.getInstance();
        this.controladorCurso = fabrica.getIControladorCurso();
        this.controladorEdicion = fabrica.getIControladorEdicion();
        this.controladorInstituto = fabrica.getIControladorInstituto();
        this.controladorUsuario = fabrica.getIControladorUsuario();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String usuarioNickname = (session != null) ? (String) session.getAttribute("usuarioNickname") : null;
        Boolean esDocente = (session != null) ? (Boolean) session.getAttribute("usuarioDocente") : null;

        // Control de acceso: Exclusivo para Docentes autenticados
        if (usuarioNickname == null || !Boolean.TRUE.equals(esDocente)) {
            response.sendRedirect("login");
            return;
        }

        String instituto = limpiar(request.getParameter("instituto"));
        String nombreCurso = limpiar(request.getParameter("curso"));
        String criterioParam = limpiar(request.getParameter("criterio"));

        try {
            // 1. Obtener y filtrar los institutos a los que pertenece el docente
            List<String> institutosDocente = obtenerInstitutosDelDocente(usuarioNickname);
            request.setAttribute("institutos", institutosDocente);

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

                String nombreEdicionVigente = null;
                try {
                    nombreEdicionVigente = controladorCurso.obtenerEdicionVigente(nombreCurso, LocalDate.now());
                } catch (Exception e) {
                    getServletContext().log("El curso " + nombreCurso + " no posee edición vigente: " + e.getMessage());
                }

                if (nombreEdicionVigente != null && !nombreEdicionVigente.trim().isEmpty()) {
                    // Obtener las ediciones en las que participa el docente para validar
                    List<String> edicionesDocente = controladorEdicion.listarEdicionesDeDocente(usuarioNickname);

                    if (edicionesDocente != null && edicionesDocente.contains(nombreEdicionVigente)) {
                        DTEdicionCurso edicionVigente = controladorEdicion.mostrarDetalleEdicion(nombreEdicionVigente);

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
                        request.setAttribute("mensajeInfo", "No participás como docente en la edición vigente de este curso.");
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
        Boolean esDocente = (session != null) ? (Boolean) session.getAttribute("usuarioDocente") : null;

        if (usuarioNickname == null || !Boolean.TRUE.equals(esDocente)) {
            response.sendRedirect("login");
            return;
        }

        String nombreEdicion = limpiar(request.getParameter("edicion"));
        String estudianteNickname = limpiar(request.getParameter("estudianteNickname"));
        String nuevoEstadoStr = limpiar(request.getParameter("nuevoEstado"));

        try {
            if (!nombreEdicion.isEmpty() && !estudianteNickname.isEmpty() && !nuevoEstadoStr.isEmpty()) {
                EstadoInscripcion nuevoEstado = EstadoInscripcion.valueOf(nuevoEstadoStr);
                
                controladorEdicion.cambiarEstadoInscripcion(usuarioNickname, nombreEdicion, estudianteNickname, nuevoEstado);
                request.setAttribute("mensajeExito", "Se actualizó el estado de la inscripción del estudiante " + estudianteNickname + ".");
            }

            doGet(request, response);

        } catch (Exception e) {
            getServletContext().log("Error en SeleccionEstudiantesServlet (POST): " + e.getMessage(), e);
            request.setAttribute("error", "No se pudo actualizar el estado: " + e.getMessage());
            doGet(request, response);
        }
    }

    private List<String> obtenerInstitutosDelDocente(String nickname) {
        List<String> institutos = new ArrayList<>();
        try {
            DtUsuario dtUsuario = controladorUsuario.obtenerInfoUsuario(nickname);
            if (dtUsuario instanceof DtDocente) {
                List<String> insts = ((DtDocente) dtUsuario).getInstitutos();
                if (insts != null) {
                    institutos = insts;
                }
            }
        } catch (Exception e) {
            getServletContext().log("Error al obtener los institutos del docente " + nickname + ": " + e.getMessage());
        }
        return institutos;
    }

    private static String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}