package com.grupo3_mat.mobile.web;

import com.grupo3_mat.mobile.soap.SoapClients;
import com.grupo3_mat.mobile.soap.generated.DtCurso;
import com.grupo3_mat.mobile.soap.generated.DtEdicionCurso;
import com.grupo3_mat.mobile.soap.generated.DtEstudiante;
import com.grupo3_mat.mobile.soap.generated.DtUsuario;
import com.grupo3_mat.mobile.soap.generated.Exception_Exception;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.xml.ws.WebServiceException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"", "/app", "/login", "/logout", "/curso", "/edicion"})
public class MobileServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(MobileServlet.class.getName());
    private static final String SESSION_NICKNAME = "mobile.nickname";
    private static final String SESSION_NAME = "mobile.nombre";
    private static final String IMAGE_ENDPOINT = getImageEndpoint();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("imageEndpoint", IMAGE_ENDPOINT);
        String path = request.getServletPath();
        if (path.isEmpty() || "/".equals(path)) {
            response.sendRedirect(request.getContextPath() + "/app");
            return;
        }
        if ("/login".equals(path)) {
            if (nickname(request) != null) {
                response.sendRedirect(request.getContextPath() + "/app");
            } else {
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            }
            return;
        }
        if ("/logout".equals(path)) {
            cerrarSesion(request, response);
            return;
        }
        if (!requireStudent(request, response)) {
            return;
        }

        try {
            switch (path) {
                case "/app" -> mostrarCursos(request, response);
                case "/curso" -> mostrarCurso(request, response);
                case "/edicion" -> mostrarEdicion(request, response);
                default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (WebServiceException error) {
            LOGGER.log(Level.WARNING, "No se pudo consultar el servicio central desde Mobile.", error);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "El servicio central no está disponible. Intente nuevamente.");
        } catch (Exception_Exception error) {
            LOGGER.log(Level.INFO, "El servicio central no encontró el recurso solicitado.", error);
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "No se encontró el curso o la edición solicitada.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        switch (request.getServletPath()) {
            case "/login" -> iniciarSesion(request, response);
            case "/logout" -> cerrarSesion(request, response);
            default -> response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    private void iniciarSesion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String identificador = request.getParameter("identificador");
        String password = request.getParameter("password");
        if (identificador == null || identificador.isBlank() || password == null || password.isBlank()) {
            mostrarLoginError(request, response, HttpServletResponse.SC_BAD_REQUEST,
                    "Ingresá tu usuario o correo y contraseña.");
            return;
        }

        try {
            if (!SoapClients.usuarios().iniciarSesion(identificador.trim(), password)) {
                mostrarLoginError(request, response, HttpServletResponse.SC_UNAUTHORIZED,
                        "No se pudo iniciar sesión. Revisá tus datos.");
                return;
            }

            String nickname = SoapClients.usuarios().resolverNickname(identificador.trim());
            DtUsuario usuario = nickname == null ? null : SoapClients.usuarios().obtenerInfoUsuario(nickname);
            if (!(usuario instanceof DtEstudiante estudiante)) {
                mostrarLoginError(request, response, HttpServletResponse.SC_FORBIDDEN,
                        "El acceso Mobile está habilitado únicamente para estudiantes.");
                return;
            }

            HttpSession anterior = request.getSession(false);
            if (anterior != null) {
                anterior.invalidate();
            }
            HttpSession sesion = request.getSession(true);
            sesion.setMaxInactiveInterval(30 * 60);
            sesion.setAttribute(SESSION_NICKNAME, estudiante.getNickname());
            sesion.setAttribute(SESSION_NAME, estudiante.getNombre());
            response.sendRedirect(request.getContextPath() + "/app");
        } catch (WebServiceException error) {
            LOGGER.log(Level.WARNING, "No se pudo autenticar en el servicio central.", error);
            mostrarLoginError(request, response, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "El servicio central no está disponible. Intentá nuevamente.");
        } catch (Exception_Exception error) {
            LOGGER.log(Level.INFO, "El servicio central rechazó las credenciales de Mobile.");
            mostrarLoginError(request, response, HttpServletResponse.SC_UNAUTHORIZED,
                    "No se pudo iniciar sesión. Revisá tus datos.");
        }
    }

    private void mostrarCursos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, Exception_Exception {
        List<String> nombres = SoapClients.cursos().listarCursos().stream()
                .filter(nombre -> nombre != null && !nombre.isBlank())
                .toList();
        List<DtCurso> cursos = new ArrayList<>();
        for (int inicio = 0; inicio < nombres.size(); inicio += 100) {
            int fin = Math.min(inicio + 100, nombres.size());
            cursos.addAll(SoapClients.cursos().consultarCursos(nombres.subList(inicio, fin)));
        }
        request.setAttribute("cursos", cursos);
        request.getRequestDispatcher("/WEB-INF/views/cursos.jsp").forward(request, response);
    }

    private void mostrarCurso(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, Exception_Exception {
        String nombre = request.getParameter("nombre");
        if (nombre == null || nombre.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el nombre del curso.");
            return;
        }
        DtCurso curso = SoapClients.cursos().consultarCurso(nombre);
        List<String> nombresEdiciones = SoapClients.ediciones().listarEdicionesPorCurso(curso.getNombre());
        request.setAttribute("curso", curso);
        request.setAttribute("ediciones", nombresEdiciones);
        request.getRequestDispatcher("/WEB-INF/views/curso.jsp").forward(request, response);
    }

    private void mostrarEdicion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, Exception_Exception {
        String nombre = request.getParameter("nombre");
        if (nombre == null || nombre.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el nombre de la edición.");
            return;
        }
        DtEdicionCurso edicion = SoapClients.ediciones().mostrarDetalleEdicion(nombre);
        if (edicion == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No se encontró la edición.");
            return;
        }
        String nombreCurso = SoapClients.ediciones().obtenerCursoDeEdicion(nombre);
        DtCurso curso = nombreCurso == null ? null : SoapClients.cursos().consultarCurso(nombreCurso);
        request.setAttribute("edicion", edicion);
        request.setAttribute("curso", curso);
        request.getRequestDispatcher("/WEB-INF/views/edicion.jsp").forward(request, response);
    }

    private boolean requireStudent(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (nickname(request) != null) {
            return true;
        }
        response.sendRedirect(request.getContextPath() + "/login");
        return false;
    }

    private String nickname(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (String) session.getAttribute(SESSION_NICKNAME);
    }

    private static String getImageEndpoint() {
        String configured = System.getenv("EDEXT_WEB_IMAGE_ENDPOINT");
        if (configured == null || configured.isBlank()) {
            return "/web/imagenes-curso";
        }
        String endpoint = configured.trim();
        if (!endpoint.startsWith("/") || endpoint.startsWith("//")
                || endpoint.contains("?") || endpoint.contains("#")) {
            throw new IllegalStateException(
                    "EDEXT_WEB_IMAGE_ENDPOINT debe ser una ruta local, por ejemplo /web/imagenes-curso.");
        }
        return endpoint;
    }

    private void cerrarSesion(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/login?cerrada=1");
    }

    private void mostrarLoginError(HttpServletRequest request, HttpServletResponse response,
            int status, String error) throws ServletException, IOException {
        response.setStatus(status);
        request.setAttribute("error", error);
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }
}
