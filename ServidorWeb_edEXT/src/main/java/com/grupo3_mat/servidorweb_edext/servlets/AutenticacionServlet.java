package com.grupo3_mat.servidorweb_edext.servlets;

import com.grupo3_mat.edEXT.Logica.DataTypes.DtUsuario;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtDocente;
import com.grupo3_mat.edEXT.Logica.Fabrica;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorUsuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = {"/login", "/logout", "/mi-cuenta"})
public class AutenticacionServlet extends HttpServlet {

    private static final String CLAVE_USUARIO = "usuarioNickname";
    private final IControladorUsuario usuarios = Fabrica.getInstance().getIControladorUsuario();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // GET solo presenta el login o la cuenta; las operaciones de sesión usan POST.
        switch (request.getServletPath()) {
            case "/login":
                if (request.getSession(false) != null
                        && request.getSession(false).getAttribute(CLAVE_USUARIO) != null) {
                    response.sendRedirect(request.getContextPath() + "/mi-cuenta");
                    return;
                }
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            case "/mi-cuenta":
                mostrarCuenta(request, response);
                return;
            default:
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if ("/logout".equals(request.getServletPath())) {
            // Invalida toda la sesión para eliminar también atributos ajenos al login.
            HttpSession sesion = request.getSession(false);
            if (sesion != null) {
                sesion.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        if (!"/login".equals(request.getServletPath())) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        String identificador = request.getParameter("identificador");
        String password = request.getParameter("password");
        try {
            // 1. Delegar la verificación de credenciales y hashes al controlador central.
            usuarios.iniciarSesion(identificador, password);
            // 2. Resolver el nickname para asociar la cuenta a la sesión web.
            String nickname = resolverNickname(identificador);
            if (nickname == null) {
                throw new IllegalStateException("La cuenta autenticada no se pudo identificar.");
            }
            DtUsuario usuario = usuarios.obtenerInfoUsuario(nickname);
            if (usuario == null) {
                throw new IllegalStateException("La cuenta autenticada ya no existe.");
            }

            // 3. Regenerar el identificador antes de marcar la sesión como autenticada.
            HttpSession sesion = request.getSession(true);
            request.changeSessionId();
            sesion.setAttribute(CLAVE_USUARIO, nickname);
            sesion.setAttribute("usuarioDocente", usuario instanceof DtDocente);
            response.sendRedirect(request.getContextPath() + "/mi-cuenta");
        } catch (Exception e) {
            // El detalle queda en el log del servidor; la pantalla no revela datos internos.
            getServletContext().log("No se pudo validar el inicio de sesión.", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            request.setAttribute("error", "No se pudo iniciar sesión. Revisá tu usuario o correo y contraseña.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    private void mostrarCuenta(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Solo se muestran datos si la sesión contiene un usuario autenticado.
        HttpSession sesion = request.getSession(false);
        String nickname = sesion == null ? null : (String) sesion.getAttribute(CLAVE_USUARIO);
        if (nickname == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        DtUsuario usuario = usuarios.obtenerInfoUsuario(nickname);
        if (usuario == null) {
            sesion.invalidate();
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        request.setAttribute("usuario", usuario);
        request.getRequestDispatcher("/mi-cuenta.jsp").forward(request, response);
    }

    private String resolverNickname(String identificador) {
        for (String nickname : usuarios.listarNicknamesUsuarios()) {
            DtUsuario usuario = usuarios.obtenerInfoUsuario(nickname);
            if (nickname.equals(identificador)
                    || (usuario != null && usuario.getCorreo() != null
                    && usuario.getCorreo().equalsIgnoreCase(identificador.trim()))) {
                return nickname;
            }
        }
        return null;
    }
}
