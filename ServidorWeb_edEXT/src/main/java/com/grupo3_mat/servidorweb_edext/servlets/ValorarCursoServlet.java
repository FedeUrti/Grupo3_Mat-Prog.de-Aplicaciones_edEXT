package com.grupo3_mat.servidorweb_edext.servlets;

import com.grupo3_mat.edEXT.Logica.DataTypes.DtEstudiante;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtUsuario;
import com.grupo3_mat.edEXT.Logica.Fabrica;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorUsuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@WebServlet("/valorar-curso")
public class ValorarCursoServlet extends HttpServlet {

    private final IControladorCurso controladorCurso = Fabrica.getInstance().getIControladorCurso();
    private final IControladorUsuario controladorUsuario = Fabrica.getInstance().getIControladorUsuario();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        String nickname = estudianteActual(request, response);
        if (nickname == null) {
            return;
        }
        cargarVista(request, response, nickname, limpiar(request.getParameter("curso")));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String nickname = estudianteActual(request, response);
        if (nickname == null) {
            return;
        }
        String nombreCurso = limpiar(request.getParameter("curso"));
        try {
            if (nombreCurso.isEmpty()) {
                throw new IllegalArgumentException("Falta indicar el curso que querés valorar.");
            }
            int puntaje;
            try {
                puntaje = Integer.parseInt(limpiar(request.getParameter("puntaje")));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("El puntaje debe ser un número entero entre 1 y 5.", e);
            }
            controladorCurso.ingresarValoracion(nombreCurso, nickname, puntaje);
            if (esSolicitudAjax(request)) {
                BigDecimal promedio = controladorCurso.obtenerPuntajePromedioCurso(nombreCurso);
                Map<Integer, Integer> conteos = controladorCurso.obtenerConteoValoracionesCurso(nombreCurso);
                int cantidad = conteos.values().stream().mapToInt(Integer::intValue).sum();
                double estrellas = Math.round(promedio.doubleValue() * 2.0) / 2.0;
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.setContentType("application/json");
                response.getWriter().write("{\"promedio\":\"" + promedio.toPlainString()
                        + "\",\"cantidad\":" + cantidad
                        + ",\"estrellas\":" + estrellas
                        + ",\"conteos\":{\"1\":" + conteos.getOrDefault(1, 0)
                        + ",\"2\":" + conteos.getOrDefault(2, 0)
                        + ",\"3\":" + conteos.getOrDefault(3, 0)
                        + ",\"4\":" + conteos.getOrDefault(4, 0)
                        + ",\"5\":" + conteos.getOrDefault(5, 0) + "}"
                        + ",\"puntajePropio\":" + puntaje + "}");
                return;
            }
            request.getSession().setAttribute("mensajeValoracion", "Tu valoración se publicó correctamente.");
            String url = request.getContextPath() + "/consulta-curso?nombre="
                    + URLEncoder.encode(nombreCurso, StandardCharsets.UTF_8) + "#valoraciones";
            response.sendRedirect(url);
        } catch (IllegalArgumentException | IllegalStateException e) {
            if (esSolicitudAjax(request)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write(e.getMessage());
                return;
            }
            request.getSession().setAttribute("errorValoracion", e.getMessage());
            redirigirDetalleCurso(request, response, nombreCurso);
        } catch (Exception e) {
            getServletContext().log("No se pudo publicar la valoración del curso.", e);
            if (esSolicitudAjax(request)) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("No se pudo publicar la valoración. Intentá nuevamente.");
                return;
            }
            request.getSession().setAttribute("errorValoracion",
                    "No se pudo publicar la valoración. Intentá nuevamente.");
            redirigirDetalleCurso(request, response, nombreCurso);
        }

    }

    private static boolean esSolicitudAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))
                || (request.getHeader("Accept") != null
                && request.getHeader("Accept").contains("application/json"));
    }

    private static void redirigirDetalleCurso(HttpServletRequest request, HttpServletResponse response,
            String nombreCurso) throws IOException {
        String url = request.getContextPath() + "/consulta-curso?nombre="
                + URLEncoder.encode(nombreCurso, StandardCharsets.UTF_8) + "#valoraciones";
        response.sendRedirect(url);
    }

    private void cargarVista(HttpServletRequest request, HttpServletResponse response,
            String nickname, String cursoSeleccionado) throws ServletException, IOException {
        try {
            List<String> cursos = controladorCurso.listarCursosValorables(nickname);
            request.setAttribute("cursos", cursos);
            if (!cursoSeleccionado.isEmpty() && cursos.contains(cursoSeleccionado)) {
                BigDecimal promedio = controladorCurso.obtenerPuntajePromedioCurso(cursoSeleccionado);
                Map<Integer, Integer> conteos = controladorCurso.obtenerConteoValoracionesCurso(cursoSeleccionado);
                Integer puntajePropio = controladorCurso.obtenerValoracionEstudiante(cursoSeleccionado, nickname);
                request.setAttribute("cursoSeleccionado", cursoSeleccionado);
                request.setAttribute("promedio", promedio);
                request.setAttribute("conteos", conteos);
                request.setAttribute("puntajePropio", puntajePropio);
            } else if (!cursoSeleccionado.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                request.setAttribute("error", "Solo podés valorar cursos en los que tengas una inscripción aceptada.");
            }
        } catch (Exception e) {
            getServletContext().log("No se pudo cargar la información de valoraciones.", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("error", "No se pudo cargar la información de valoraciones. Intentá nuevamente.");
        }
        request.getRequestDispatcher("/valorar-curso.jsp").forward(request, response);
    }

    private String estudianteActual(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        String nickname = session == null ? null : (String) session.getAttribute("usuarioNickname");
        if (nickname == null) {
            response.sendRedirect(request.getContextPath() + "/login?motivo=valoracion");
            return null;
        }
        DtUsuario usuario = controladorUsuario.obtenerInfoUsuario(nickname);
        if (usuario instanceof DtEstudiante) {
            return nickname;
        }
        if (usuario == null && session != null) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login?motivo=valoracion");
            return null;
        }
        response.sendRedirect(request.getContextPath() + "/inicio?acceso=estudiante");
        return null;
    }

    private static String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
