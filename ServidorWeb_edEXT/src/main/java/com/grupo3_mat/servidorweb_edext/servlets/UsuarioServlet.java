package com.grupo3_mat.servidorweb_edext.servlets;

import com.grupo3_mat.edEXT.Logica.DataTypes.DtDocente;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtEstudiante;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTEdicionCurso;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtUsuario;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTInscripcionEdicion;
import com.grupo3_mat.edEXT.Logica.Fabrica;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorEdicion;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCategoria;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorInstituto;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorUsuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@WebServlet(urlPatterns = {
    "/alta-usuario", "/consulta-usuario", "/modificar-usuario",
    "/seguir-usuario", "/dejar-de-seguir-usuario"
})
// El contenedor limita el tamaño total y por archivo de las imágenes de perfil recibidas.
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 6 * 1024 * 1024)
public class UsuarioServlet extends HttpServlet {

    // Se comparte la clave que AutenticacionServlet guarda al validar las credenciales.
    private static final String CLAVE_USUARIO = "usuarioNickname";
    // Las rutas son contratos para las JSP que se conectarán a estos endpoints.
    private static final String VISTA_ALTA = "/WEB-INF/views/alta_usuario.jsp";
    private static final String VISTA_CONSULTA = "/WEB-INF/views/consulta_usuario.jsp";
    private static final String VISTA_MODIFICAR = "/WEB-INF/views/modificar-usuario.jsp";
    // Los resultados de seguir/dejar de seguir sobreviven a la redirección y se consumen una sola vez.
    private static final String CLAVE_MENSAJE = "mensajeUsuario";
    private static final String CLAVE_ERROR = "errorUsuario";

    // La lógica de negocio y el acceso a los datos permanecen delegados al servidor central.
    private final IControladorUsuario usuarios = Fabrica.getInstance().getIControladorUsuario();
    private final IControladorInstituto institutos = Fabrica.getInstance().getIControladorInstituto();
    private final IControladorCategoria categorias = Fabrica.getInstance().getIControladorCategoria();
    private final IControladorEdicion ediciones = Fabrica.getInstance().getIControladorEdicion();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // GET presenta formularios o consulta datos; las modificaciones se reservan para POST.
        switch (request.getServletPath()) {
            case "/alta-usuario":
                mostrarAlta(request, response);
                break;
            case "/consulta-usuario":
                mostrarConsulta(request, response);
                break;
            case "/modificar-usuario":
                mostrarModificacion(request, response);
                break;
            case "/seguir-usuario":
            case "/dejar-de-seguir-usuario":
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Permite que los nombres y apellidos enviados en español se interpreten correctamente.
        request.setCharacterEncoding("UTF-8");
        // Cada acción de escritura tiene su ruta explícita; ninguna depende de parámetros de modo.
        switch (request.getServletPath()) {
            case "/alta-usuario":
                registrarUsuario(request, response);
                break;
            case "/modificar-usuario":
                modificarUsuario(request, response);
                break;
            case "/seguir-usuario":
                cambiarSeguimiento(request, response, true);
                break;
            case "/dejar-de-seguir-usuario":
                cambiarSeguimiento(request, response, false);
                break;
            case "/consulta-usuario":
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void mostrarAlta(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // El formulario necesita el catálogo vigente para permitir elegir institutos al docente.
        if (cargarInstitutos(request, response)) {
            request.getRequestDispatcher(VISTA_ALTA).forward(request, response);
        }
    }

    private void registrarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String imagenGuardada = null;
        try {
            // El servidor vuelve a validar los campos aunque el navegador también aplique restricciones.
            String nickname = requerido(request, "nickname");
            String nombre = requerido(request, "nombre");
            String apellido = requerido(request, "apellido");
            String correo = requerido(request, "correo");
            LocalDate fechaNacimiento = fechaRequerida(request, "fechaNacimiento");
            String tipo = tipoRequerido(request);
            validarNickname(nickname);
            validarNombre(nombre, "nombre");
            validarNombre(apellido, "apellido");
            validarCorreo(correo);
            String password = request.getParameter("password");
            String confirmacion = request.getParameter("confirmarPassword");
            if (password == null || password.isBlank()) {
                throw new IllegalArgumentException("La contraseña es obligatoria.");
            }
            if (confirmacion == null || confirmacion.isBlank()) {
                throw new IllegalArgumentException("La confirmación de contraseña es obligatoria.");
            }
            if (!password.equals(confirmacion)) {
                throw new IllegalArgumentException("Las contraseñas no coinciden.");
            }

            // En el modelo, una lista de institutos convierte la cuenta en docente; vacía crea estudiante.
            List<String> nombresInstitutos = valoresMultiples(request.getParameterValues("institutos"));
            if ("docente".equals(tipo) && nombresInstitutos.isEmpty()) {
                throw new IllegalArgumentException("Seleccioná al menos un instituto para el docente.");
            }
            if ("estudiante".equals(tipo)) {
                nombresInstitutos = List.of();
            }

            // Se guarda la imagen con un nombre generado y el controlador aplica las reglas de alta.
            imagenGuardada = guardarImagen(opcionalPart(request, "imagen"));
            usuarios.altaUsuarioConInstitutos(nickname, nombre, apellido, correo, fechaNacimiento,
                    imagenGuardada, nombresInstitutos, password);

            response.sendRedirect(request.getContextPath() + "/consulta-usuario?nickname="
                    + codificar(nickname) + "&registrado=1");
        } catch (IllegalArgumentException e) {
            // Si la validación falla, no se deja una imagen huérfana en el directorio de cargas.
            borrarImagenSiExiste(imagenGuardada);
            errorAlta(request, response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (ServletException e) {
            borrarImagenSiExiste(imagenGuardada);
            getServletContext().log("No se pudo procesar la imagen del usuario.", e);
            errorAlta(request, response, HttpServletResponse.SC_BAD_REQUEST,
                    "No se pudo procesar la imagen. Usá PNG, JPEG, GIF o WebP de hasta 5 MB.");
        } catch (Exception e) {
            borrarImagenSiExiste(imagenGuardada);
            getServletContext().log("No se pudo registrar el usuario.", e);
            int estado = esConflictoUsuario(e) ? HttpServletResponse.SC_CONFLICT
                    : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            errorAlta(request, response, estado, mensajeSeguro(e));
        }
    }

    private void mostrarConsulta(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Si no se seleccionó a nadie, se muestra el perfil de la sesión cuando existe.
        String nickname = limpiar(request.getParameter("nickname"));
        if (nickname.isEmpty()) {
            HttpSession sesion = request.getSession(false);
            nickname = sesion == null ? "" : limpiar((String) sesion.getAttribute(CLAVE_USUARIO));
        }
        try {
            // La consulta comparte filtros y navegación con la portada.
            request.setAttribute("institutos", institutos.listarInstitutos());
            request.setAttribute("categorias", categorias.listarCategorias());
            // El detalle se construye a partir del DTO central, incluidas las relaciones propias del subtipo.
            if (!nickname.isEmpty()) {
                DtUsuario usuario = usuarios.obtenerInfoUsuario(nickname);
                if (usuario == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    request.setAttribute("error", "No se encontró el usuario solicitado.");
                } else {
                    request.setAttribute("usuario", usuario);
                    request.setAttribute("tipoUsuario", usuario instanceof DtDocente ? "Docente" : "Estudiante");
                    prepararRelacionesUsuario(request, usuario);
                    request.setAttribute("imagenPerfil", imagenPerfil(usuario));
                    Map<String, DtUsuario> perfilesRelacionados = prepararPerfilesRelacionados(usuario);
                    request.setAttribute("perfilesRelacionados", perfilesRelacionados);
                    Map<String, String> imagenesRelacionadas = new HashMap<>();
                    for (Map.Entry<String, DtUsuario> perfil : perfilesRelacionados.entrySet()) {
                        imagenesRelacionadas.put(perfil.getKey(), imagenPerfil(perfil.getValue()));
                    }
                    request.setAttribute("imagenesRelacionadas", imagenesRelacionadas);
                    HttpSession sesion = request.getSession(false);
                    String nicknameSesion = sesion == null ? null
                            : (String) sesion.getAttribute(CLAVE_USUARIO);
                    request.setAttribute("esPropietario", nickname.equals(nicknameSesion));
                    request.setAttribute("siguiendoUsuario",
                            nicknameSesion != null && usuario.getSeguidores().contains(nicknameSesion));
                }
            }

            tomarMensajes(request);
        } catch (Exception e) {
            getServletContext().log("No se pudo consultar la información de usuarios.", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("error", "No se pudo consultar a los usuarios. Intentá nuevamente.");
        }
        request.getRequestDispatcher(VISTA_CONSULTA).forward(request, response);
    }

    private void prepararRelacionesUsuario(HttpServletRequest request, DtUsuario usuario) {
        // Docentes y estudiantes exponen relaciones distintas; aquí se unifican para que la JSP use una sola vista.
        List<String> nombresEdiciones = new ArrayList<>();
        Map<String, String> cursosPorEdicion = new HashMap<>();
        Map<String, String> estadosPorEdicion = new HashMap<>();

        if (usuario instanceof DtDocente docente) {
            nombresEdiciones.addAll(docente.getEdicionesAsociadas());
            request.setAttribute("programasUsuario", docente.getProgramas());
        } else if (usuario instanceof DtEstudiante estudiante) {
            for (DTInscripcionEdicion inscripcion : estudiante.getInscripcionesEdicion()) {
                nombresEdiciones.add(inscripcion.getNombreEdicion());
                estadosPorEdicion.put(inscripcion.getNombreEdicion(),
                        inscripcion.getEstado() == null ? "" : inscripcion.getEstado().toString());
            }
            request.setAttribute("programasUsuario", estudiante.getProgramasInscripto());
        }

        List<DTEdicionCurso> detallesEdiciones = new ArrayList<>();
        for (String nombreEdicion : nombresEdiciones) {
            DTEdicionCurso detalle = ediciones.mostrarDetalleEdicion(nombreEdicion);
            if (detalle != null) {
                detallesEdiciones.add(detalle);
                cursosPorEdicion.put(nombreEdicion, ediciones.obtenerCursoDeEdicion(nombreEdicion));
            }
        }
        request.setAttribute("edicionesUsuario", detallesEdiciones);
        request.setAttribute("cursosPorEdicion", cursosPorEdicion);
        request.setAttribute("estadosPorEdicion", estadosPorEdicion);
    }

    private Map<String, DtUsuario> prepararPerfilesRelacionados(DtUsuario usuario) {
        // Se combinan seguidos y seguidores sin duplicados para preparar los datos de ambas subpestañas.
        Map<String, DtUsuario> perfiles = new HashMap<>();
        List<String> nicknamesRelacionados = new ArrayList<>(usuario.getSeguidos());
        for (String nickname : usuario.getSeguidores()) {
            if (!nicknamesRelacionados.contains(nickname)) {
                nicknamesRelacionados.add(nickname);
            }
        }
        for (String nickname : nicknamesRelacionados) {
            DtUsuario perfil = usuarios.obtenerInfoUsuario(nickname);
            if (perfil != null) {
                perfiles.put(nickname, perfil);
            }
        }
        return perfiles;
    }

    private static String imagenPerfil(DtUsuario usuario) {
        String imagen = usuario.getImagenPath();
        if (imagen != null && (imagen.matches("[a-f0-9-]{36}\\.(png|jpg|gif|webp)")
                || imagen.endsWith("docente_placeholder.png")
                || imagen.endsWith("estudiante_placeholder.png"))) {
            return imagen;
        }
        return usuario instanceof DtDocente ? "docente_placeholder.png" : "estudiante_placeholder.png";
    }

    private void mostrarModificacion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Solo una sesión autenticada puede abrir la pantalla de modificación de su propia cuenta.
        String nicknameSesion = nicknameSesion(request, response);
        if (nicknameSesion == null) {
            return;
        }
        String nicknameSolicitado = limpiar(request.getParameter("nickname"));
        if (!nicknameSolicitado.isEmpty() && !nicknameSesion.equals(nicknameSolicitado)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        DtUsuario usuario = usuarios.obtenerInfoUsuario(nicknameSesion);
        if (usuario == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No existe el usuario de la sesión.");
            return;
        }
        request.setAttribute("usuario", usuario);
        request.getRequestDispatcher(VISTA_MODIFICAR).forward(request, response);
    }

    private void modificarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // El nickname objetivo sale de la sesión, no de un campo editable enviado por el navegador.
        String nickname = nicknameSesion(request, response);
        if (nickname == null) {
            return;
        }
        String nicknameSolicitado = limpiar(request.getParameter("nickname"));
        if (!nicknameSolicitado.isEmpty() && !nickname.equals(nicknameSolicitado)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String imagenGuardada = null;
        try {
            String nombre = requerido(request, "nombre");
            String apellido = requerido(request, "apellido");
            validarNombre(nombre, "nombre");
            validarNombre(apellido, "apellido");
            LocalDate fechaNacimiento = fechaRequerida(request, "fechaNacimiento");
            DtUsuario usuarioActual = usuarios.obtenerInfoUsuario(nickname);
            if (usuarioActual == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "No existe el usuario de la sesión.");
                return;
            }

            imagenGuardada = guardarImagen(opcionalPart(request, "imagen"));
            // Si no se eligió otra imagen, se conserva la que ya tenía el perfil.
            String imagenPath = imagenGuardada != null ? imagenGuardada : usuarioActual.getImagenPath();
            usuarios.modificarDatosUsuario(nickname, nombre, apellido, fechaNacimiento, imagenPath);
            if (esSolicitudAjax(request)) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                return;
            }
            response.sendRedirect(request.getContextPath() + "/consulta-usuario?nickname="
                    + codificar(nickname) + "&actualizado=1");
        } catch (IllegalArgumentException e) {
            borrarImagenSiExiste(imagenGuardada);
            errorModificacion(request, response, nickname, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (ServletException e) {
            borrarImagenSiExiste(imagenGuardada);
            getServletContext().log("No se pudo procesar la imagen del usuario.", e);
            errorModificacion(request, response, nickname, HttpServletResponse.SC_BAD_REQUEST,
                    "No se pudo procesar la imagen. Usá PNG, JPEG, GIF o WebP de hasta 5 MB.");
        } catch (Exception e) {
            borrarImagenSiExiste(imagenGuardada);
            getServletContext().log("No se pudo modificar el usuario " + nickname + ".", e);
            int estado = esConflictoUsuario(e) ? HttpServletResponse.SC_CONFLICT
                    : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            errorModificacion(request, response, nickname, estado, mensajeSeguro(e));
        }
    }

    private void cambiarSeguimiento(HttpServletRequest request, HttpServletResponse response, boolean seguir)
            throws IOException {
        // La identidad del seguidor siempre es la autenticada; el formulario solo indica el destinatario.
        String nicknameSeguidor = nicknameSesion(request, response);
        if (nicknameSeguidor == null) {
            return;
        }
        String nicknameASeguir = limpiar(request.getParameter("nicknameASeguir"));
        if (nicknameASeguir.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el nickname del usuario.");
            return;
        }

        try {
            // El controlador central valida la existencia de ambas cuentas y las reglas de seguimiento.
            if (seguir) {
                usuarios.seguirUsuario(nicknameSeguidor, nicknameASeguir);
            } else {
                usuarios.dejarDeSeguirUsuario(nicknameSeguidor, nicknameASeguir);
            }
            guardarMensaje(request, CLAVE_MENSAJE,
                    seguir ? "Ahora seguís a @" + nicknameASeguir + "." : "Dejaste de seguir a @" + nicknameASeguir + ".");
        } catch (Exception e) {
            getServletContext().log("No se pudo actualizar el seguimiento de " + nicknameSeguidor + ".", e);
            guardarMensaje(request, CLAVE_ERROR, mensajeSeguro(e));
        }
        response.sendRedirect(request.getContextPath() + "/consulta-usuario?nickname=" + codificar(nicknameASeguir));
    }

    private boolean cargarInstitutos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Mantener este catálogo en el request permite que el formulario se renderice con datos actuales.
            request.setAttribute("institutos", institutos.listarInstitutos());
            if (request.getAttribute("institutosMarcados") == null) {
                request.setAttribute("institutosMarcados", List.of());
            }
            return true;
        } catch (Exception e) {
            getServletContext().log("No se pudieron cargar los institutos para el alta de usuario.", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("error", "No se pudieron cargar los institutos. Intentá nuevamente.");
            request.getRequestDispatcher(VISTA_ALTA).forward(request, response);
            return false;
        }
    }

    private void errorAlta(HttpServletRequest request, HttpServletResponse response, int estado, String mensaje)
            throws ServletException, IOException {
        // Se conservan los campos no sensibles para que la persona pueda corregir el formulario sin empezar de cero.
        response.setStatus(estado);
        request.setAttribute("error", mensaje);
        request.setAttribute("form_nickname", request.getParameter("nickname"));
        request.setAttribute("form_nombre", request.getParameter("nombre"));
        request.setAttribute("form_apellido", request.getParameter("apellido"));
        request.setAttribute("form_correo", request.getParameter("correo"));
        request.setAttribute("form_fechaNacimiento", request.getParameter("fechaNacimiento"));
        request.setAttribute("form_tipoUsuario", request.getParameter("tipoUsuario"));
        request.setAttribute("institutosMarcados", valoresMultiples(request.getParameterValues("institutos")));
        if (cargarInstitutos(request, response)) {
            request.getRequestDispatcher(VISTA_ALTA).forward(request, response);
        }
    }

    private void errorModificacion(HttpServletRequest request, HttpServletResponse response, String nickname,
            int estado, String mensaje) throws ServletException, IOException {
        if (esSolicitudAjax(request)) {
            response.setStatus(estado);
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write(mensaje);
            return;
        }
        // Se recarga el perfil persistido y se separan los valores enviados para volver a poblar la vista.
        response.setStatus(estado);
        request.setAttribute("error", mensaje);
        request.setAttribute("form_nombre", request.getParameter("nombre"));
        request.setAttribute("form_apellido", request.getParameter("apellido"));
        request.setAttribute("form_fechaNacimiento", request.getParameter("fechaNacimiento"));
        request.setAttribute("usuario", usuarios.obtenerInfoUsuario(nickname));
        request.getRequestDispatcher(VISTA_MODIFICAR).forward(request, response);
    }

    private static boolean esSolicitudAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
    }

    private String nicknameSesion(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // No se crea una sesión nueva al comprobar permisos: una sesión ausente debe seguir siendo anónima.
        HttpSession sesion = request.getSession(false);
        String nickname = sesion == null ? null : (String) sesion.getAttribute(CLAVE_USUARIO);
        if (nickname == null) {
            response.sendRedirect(request.getContextPath() + "/login");
        }
        return nickname;
    }

    private void tomarMensajes(HttpServletRequest request) {
        // Los avisos guardados antes de una redirección se trasladan a la vista y se eliminan de la sesión.
        HttpSession sesion = request.getSession(false);
        if (sesion == null) {
            return;
        }
        Object mensaje = sesion.getAttribute(CLAVE_MENSAJE);
        Object error = sesion.getAttribute(CLAVE_ERROR);
        if (mensaje != null) {
            request.setAttribute("mensaje", mensaje);
            sesion.removeAttribute(CLAVE_MENSAJE);
        }
        if (error != null) {
            request.setAttribute("error", error);
            sesion.removeAttribute(CLAVE_ERROR);
        }
    }

    private void guardarMensaje(HttpServletRequest request, String clave, String mensaje) {
        request.getSession(true).setAttribute(clave, mensaje);
    }

    private Part opcionalPart(HttpServletRequest request, String nombre) throws IOException, ServletException {
        // Los formularios sin archivo pueden ser urlencoded; solo se solicita una Part en envíos multipart.
        String contentType = request.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("multipart/")) {
            return null;
        }
        Part part = request.getPart(nombre);
        return part == null || part.getSize() == 0 ? null : part;
    }

    private String guardarImagen(Part imagen) throws IOException, ServletException {
        if (imagen == null) {
            return null;
        }
        // Se acepta una lista cerrada de tipos y se ignora el nombre original del archivo del cliente.
        String extension;
        String tipo = imagen.getContentType();
        if (tipo == null) {
            throw new ServletException("La imagen no informa su tipo.");
        }
        switch (tipo.toLowerCase(Locale.ROOT)) {
            case "image/png":
                extension = ".png";
                break;
            case "image/jpeg":
                extension = ".jpg";
                break;
            case "image/gif":
                extension = ".gif";
                break;
            case "image/webp":
                extension = ".webp";
                break;
            default:
                throw new ServletException("Tipo de imagen no admitido.");
        }

        Path carpeta = carpetaImagenes();
        Files.createDirectories(carpeta);
        // El UUID evita colisiones y evita confiar en rutas aportadas por el navegador.
        String nombre = UUID.randomUUID() + extension;
        Path archivo = carpeta.resolve(nombre);
        try (InputStream contenido = imagen.getInputStream()) {
            Files.copy(contenido, archivo);
        } catch (IOException e) {
            // Un error de copia no debe dejar un archivo parcial en el directorio de cargas.
            try {
                Files.deleteIfExists(archivo);
            } catch (IOException errorLimpieza) {
                e.addSuppressed(errorLimpieza);
            }
            throw e;
        }
        return nombre;
    }

    private Path carpetaImagenes() {
        // La variable de entorno permite compartir el mismo directorio con el despliegue en contenedor.
        String configurada = System.getenv("EDEXT_UPLOAD_DIR");
        if (configurada != null && !configurada.isBlank()) {
            return Paths.get(configurada).toAbsolutePath().normalize();
        }
        return Paths.get(System.getProperty("user.home"), ".edext", "uploads")
                .toAbsolutePath().normalize();
    }

    private void borrarImagenSiExiste(String nombre) {
        if (nombre == null) {
            return;
        }
        try {
            Files.deleteIfExists(carpetaImagenes().resolve(nombre).normalize());
        } catch (IOException e) {
            // El error de limpieza queda registrado aunque no oculte el fallo original del alta.
            getServletContext().log("No se pudo limpiar la imagen de un alta fallida: " + nombre, e);
        }
    }

    private static String tipoRequerido(HttpServletRequest request) {
        // Se rechazan roles inesperados en vez de tratarlos como un tipo válido por defecto.
        String tipo = requerido(request, "tipoUsuario").toLowerCase(Locale.ROOT);
        if (!tipo.equals("docente") && !tipo.equals("estudiante")) {
            throw new IllegalArgumentException("Seleccioná un tipo de usuario válido.");
        }
        return tipo;
    }

    private static void validarNickname(String nickname) {
        if (!nickname.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ][a-zA-Z0-9_.-]*$")) {
            throw new IllegalArgumentException(
                    "El nickname debe comenzar con una letra y no puede contener espacios ni empezar con números o símbolos.");
        }
    }

    private static void validarNombre(String nombre, String campo) {
        if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ]+[a-zA-ZáéíóúÁÉÍÓÚñÑ0-9\\s]*$")) {
            throw new IllegalArgumentException("El " + campo + " debe comenzar obligatoriamente con letras.");
        }
    }

    private static void validarCorreo(String correo) {
        if (!correo.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Ingresá un correo electrónico válido.");
        }
    }

    private static LocalDate fechaRequerida(HttpServletRequest request, String nombre) {
        try {
            return LocalDate.parse(requerido(request, nombre));
        } catch (DateTimeParseException e) {
            // El formulario envía fechas ISO, que LocalDate puede validar sin depender de la región del servidor.
            throw new IllegalArgumentException("Ingresá una fecha válida con formato AAAA-MM-DD.");
        }
    }

    private static String requerido(HttpServletRequest request, String nombre) {
        String valor = limpiar(request.getParameter(nombre));
        if (valor.isEmpty()) {
            throw new IllegalArgumentException("El campo " + nombre + " es obligatorio.");
        }
        return valor;
    }

    private static List<String> valoresMultiples(String[] valores) {
        if (valores == null) {
            return new ArrayList<>();
        }
        // Se descartan selecciones vacías y se recortan espacios antes de llamar al controlador.
        List<String> limpios = new ArrayList<>();
        for (String valor : valores) {
            if (valor != null && !valor.isBlank()) {
                limpios.add(valor.trim());
            }
        }
        return limpios;
    }

    private static String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private static String codificar(String valor) {
        return URLEncoder.encode(valor, StandardCharsets.UTF_8);
    }

    private static boolean esConflictoUsuario(Exception e) {
        // Los conflictos recuperables (por ejemplo, nickname repetido) se responden con HTTP 409.
        String mensaje = e.getMessage();
        return mensaje != null && (mensaje.contains("ya está en uso")
                || mensaje.contains("ya está registrado")
                || mensaje.contains("Ya sigues")
                || mensaje.contains("No sigues")
                || mensaje.contains("no puede seguirse a sí mismo"));
    }

    private static String mensajeSeguro(Exception e) {
        // Solo se muestran mensajes de validación conocidos; el detalle técnico permanece en el log.
        String mensaje = e.getMessage();
        if (mensaje != null && (mensaje.contains("ya está en uso")
                || mensaje.contains("ya está registrado")
                || mensaje.contains("no existe")
                || mensaje.contains("no pueden estar vacíos")
                || mensaje.contains("seleccionado más de una vez")
                || mensaje.contains("Ya sigues")
                || mensaje.contains("No sigues")
                || mensaje.contains("no puede seguirse a sí mismo"))) {
            return mensaje;
        }
        return "No se pudo completar la operación. Revisá los datos e intentá nuevamente.";
    }
}
