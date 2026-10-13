package com.grupo3_mat.servidorweb_edext.servlets;

import com.grupo3_mat.edEXT.Logica.DataTypes.DTProgramaFormacion;
import com.grupo3_mat.edEXT.Logica.Fabrica;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCategoria;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorInstituto;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorProgramaFormacion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet(
    name = "ProgramaServlet", 
    urlPatterns = {"/ProgramaServlet", "/programas", "/crear-programa"}
)
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 50
)
public class ProgramaServlet extends HttpServlet {

    private final IControladorProgramaFormacion controladorPrograma = Fabrica.getInstance().getIControladorProgramaFormacion();
    private final IControladorInstituto controladorInstituto = Fabrica.getInstance().getIControladorInstituto();
    private final IControladorCategoria controladorCategoria = Fabrica.getInstance().getIControladorCategoria();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Desactivar caché para ver cambios al instante
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        if ("/crear-programa".equals(request.getServletPath())) {
            if (!requiereDocente(request, response)) {
                return;
            }
            try {
                cargarDatosFormulario(request);
            } catch (Exception e) {
                getServletContext().log("No se pudieron cargar los datos del formulario de programa.", e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                request.setAttribute("error", "No se pudieron cargar los datos necesarios para crear el programa.");
            }
            request.getRequestDispatcher("/WEB-INF/views/crear-programa.jsp").forward(request, response);
            return;
        }

        try {
            cargarSidebar(request);
            List<DTProgramaFormacion> listaProgramas = controladorPrograma.listarProgramas();
            request.setAttribute("programas", listaProgramas);

        } catch (Exception e) {
            getServletContext().log("No se pudieron cargar los programas de formación.", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("error", "No se pudieron cargar los programas de formación.");
        }

        request.getRequestDispatcher("/WEB-INF/views/programas.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        String nicknameDocente = session == null ? null : (String) session.getAttribute("usuarioNickname");
        Boolean esDocente = session == null ? null : (Boolean) session.getAttribute("usuarioDocente");

        if (nicknameDocente == null || !Boolean.TRUE.equals(esDocente)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Solo un docente puede crear programas.");
            return;
        }

        String nombreArchivoFinal = null;
        try {
            String nombre = requerido(request, "nombre");
            String descripcion = requerido(request, "descripcion");
            LocalDate fechaInicio = LocalDate.parse(requerido(request, "fechaInicio"));
            LocalDate fechaFin = LocalDate.parse(requerido(request, "fechaFin"));
            if (!fechaFin.isAfter(fechaInicio)) {
                throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio.");
            }

            Part filePart = request.getPart("imagen");
            if (filePart != null && filePart.getSize() > 0) {
                String extension = extensionImagen(filePart);
                nombreArchivoFinal = java.util.UUID.randomUUID() + extension;
                String envDir = System.getenv("EDEXT_UPLOAD_DIR");
                java.nio.file.Path carpetaUploads = envDir != null && !envDir.isBlank()
                        ? java.nio.file.Paths.get(envDir).toAbsolutePath().normalize()
                        : java.nio.file.Paths.get(System.getProperty("user.home"), ".edext", "uploads")
                                .toAbsolutePath().normalize();
                java.nio.file.Files.createDirectories(carpetaUploads);
                filePart.write(carpetaUploads.resolve(nombreArchivoFinal).toString());
            }

            controladorPrograma.crearProgramaFormacion(nicknameDocente, nombre, descripcion,
                    fechaInicio, fechaFin, LocalDate.now(), nombreArchivoFinal);
            response.sendRedirect(request.getContextPath() + "/programas?creado=1");
        } catch (Exception e) {
            if (nombreArchivoFinal != null) {
                borrarImagen(nombreArchivoFinal);
            }
            getServletContext().log("No se pudo crear el programa de formación.", e);
            try {
                cargarDatosFormulario(request);
            } catch (Exception cargaError) {
                getServletContext().log("No se pudieron recargar los datos del formulario de programa.", cargaError);
            }
            conservarFormulario(request);
            response.setStatus(e instanceof IllegalArgumentException || e instanceof java.time.DateTimeException
                    ? HttpServletResponse.SC_BAD_REQUEST
                    : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("error", mensajeError(e));
            request.getRequestDispatcher("/WEB-INF/views/crear-programa.jsp").forward(request, response);
        }
    }

    private boolean requiereDocente(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        String nickname = session == null ? null : (String) session.getAttribute("usuarioNickname");
        Boolean esDocente = session == null ? null : (Boolean) session.getAttribute("usuarioDocente");
        if (nickname == null) {
            response.sendRedirect(request.getContextPath() + "/login?motivo=docente");
            return false;
        }
        if (!Boolean.TRUE.equals(esDocente)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Solo un docente puede crear programas.");
            return false;
        }
        return true;
    }

    private void cargarSidebar(HttpServletRequest request) throws Exception {
        request.setAttribute("institutos", controladorInstituto.listarInstitutos());
        request.setAttribute("categorias", controladorCategoria.listarCategorias());
        request.setAttribute("filtroInstituto", "");
        request.setAttribute("filtroCategoria", "");
    }

    private void cargarDatosFormulario(HttpServletRequest request) throws Exception {
        cargarSidebar(request);
    }

    private void conservarFormulario(HttpServletRequest request) {
        request.setAttribute("nombre", request.getParameter("nombre"));
        request.setAttribute("descripcion", request.getParameter("descripcion"));
        request.setAttribute("fechaInicio", request.getParameter("fechaInicio"));
        request.setAttribute("fechaFin", request.getParameter("fechaFin"));
    }

    private static String requerido(HttpServletRequest request, String campo) {
        String valor = request.getParameter(campo);
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("Completá todos los campos obligatorios.");
        }
        return valor.trim();
    }

    private static String extensionImagen(Part imagen) {
        String tipo = imagen.getContentType();
        if ("image/png".equalsIgnoreCase(tipo)) {
            return ".png";
        }
        if ("image/jpeg".equalsIgnoreCase(tipo)) {
            return ".jpg";
        }
        if ("image/gif".equalsIgnoreCase(tipo)) {
            return ".gif";
        }
        if ("image/webp".equalsIgnoreCase(tipo)) {
            return ".webp";
        }
        throw new IllegalArgumentException("Usá una imagen PNG, JPEG, GIF o WebP.");
    }

    private void borrarImagen(String nombreArchivo) {
        try {
            String envDir = System.getenv("EDEXT_UPLOAD_DIR");
            java.nio.file.Path carpetaUploads = envDir != null && !envDir.isBlank()
                    ? java.nio.file.Paths.get(envDir).toAbsolutePath().normalize()
                    : java.nio.file.Paths.get(System.getProperty("user.home"), ".edext", "uploads")
                            .toAbsolutePath().normalize();
            java.nio.file.Files.deleteIfExists(carpetaUploads.resolve(nombreArchivo));
        } catch (IOException e) {
            getServletContext().log("No se pudo limpiar la imagen del programa fallido.", e);
        }
    }

    private static String mensajeError(Exception error) {
        String mensaje = error.getMessage();
        return mensaje == null || mensaje.isBlank()
                ? "No se pudo crear el programa. Revisá los datos e intentá nuevamente."
                : mensaje;
    }

}