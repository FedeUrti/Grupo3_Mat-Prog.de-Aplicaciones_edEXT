package com.grupo3_mat.servidorweb_edext.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@WebServlet(urlPatterns = {"/imagenes-curso/*", "/imagenes-usuario"})
public class ImagenCursoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1. El dato llega como parámetro para admitir tanto UUID subidos como recursos del backend.
        String nombre = request.getParameter("nombre");
        if (nombre == null || nombre.isBlank()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 2. Las imágenes predeterminadas se sirven desde el JAR central; el nombre se limita a una lista segura.
        String recursoPredeterminado = obtenerRecursoPredeterminado(nombre);
        if (recursoPredeterminado != null) {
            try (InputStream imagen = getClass().getClassLoader().getResourceAsStream(recursoPredeterminado)) {
                if (imagen == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                response.setContentType(tipoImagen(recursoPredeterminado));
                response.setHeader("X-Content-Type-Options", "nosniff");
                response.setHeader("Cache-Control", "public, max-age=86400");
                imagen.transferTo(response.getOutputStream());
            }
            return;
        }

        // 3. Los programas creados en la aplicación de escritorio guardan nombres acotados en su carpeta propia.
        boolean imagenProgramaEscritorio = nombre.matches(
                "(?i)programa_[a-z0-9_-]+_\\d+\\.(png|jpg|jpeg|gif|webp)");
        if (imagenProgramaEscritorio) {
            Path carpetaProgramas = Paths.get(System.getProperty("user.home"), ".edEXT", "imagenes")
                    .toAbsolutePath().normalize();
            servirArchivo(carpetaProgramas, nombre, response);
            return;
        }

        // 4. Las imágenes cargadas desde el formulario solo admiten nombres UUID y extensiones conocidas.
        if (!nombre.matches("(?i)[a-f0-9-]{36}\\.(png|jpg|jpeg|gif|webp)")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 5. Usar la carpeta configurada para Docker o la carpeta local predeterminada.
        String configurada = System.getenv("EDEXT_UPLOAD_DIR");
        Path carpeta = configurada != null && !configurada.isBlank()
                ? Paths.get(configurada).toAbsolutePath().normalize()
                : Paths.get(System.getProperty("user.home"), ".edext", "uploads").toAbsolutePath().normalize();
        servirArchivo(carpeta, nombre, response);
    }

    private static void servirArchivo(Path carpeta, String nombre, HttpServletResponse response)
            throws IOException {
        Path archivo = carpeta.resolve(nombre).normalize();
        if (!archivo.startsWith(carpeta) || !Files.isRegularFile(archivo)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 5. Detectar el tipo de contenido y enviarlo con cabeceras seguras.
        String tipo = Files.probeContentType(archivo);
        if (tipo == null) {
            String nombreArchivo = archivo.getFileName().toString().toLowerCase(java.util.Locale.ROOT);
            tipo = nombreArchivo.endsWith(".png") ? "image/png"
                    : nombreArchivo.endsWith(".jpg") || nombreArchivo.endsWith(".jpeg") ? "image/jpeg"
                    : nombreArchivo.endsWith(".gif") ? "image/gif" : "image/webp";
        }
        if (tipo == null || !tipo.startsWith("image/")) {
            response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            return;
        }
        response.setContentType(tipo);
        response.setContentLengthLong(Files.size(archivo));
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("Cache-Control", "public, max-age=86400");
        Files.copy(archivo, response.getOutputStream());
    }

    private static String obtenerRecursoPredeterminado(String nombre) {
        String prefijoRecursos = "Presentacion/Recursos/";
        String rutaNormalizada = nombre.replace('\\', '/');
        if (rutaNormalizada.startsWith(prefijoRecursos)) {
            rutaNormalizada = rutaNormalizada.substring(prefijoRecursos.length());
        }
        if (rutaNormalizada.matches("datos_prueba/(usuario|curso|edicion|programa)_[a-z0-9_-]+\\.(png|jpg|jpeg|webp)")) {
            return prefijoRecursos + rutaNormalizada;
        }

        // Aceptar ruta antigua del modelo y el nombre plano, pero solo para archivos conocidos.
        String archivo = rutaNormalizada.substring(rutaNormalizada.lastIndexOf('/') + 1);
        switch (archivo) {
            case "imagen_predeterminada_curso_1.png":
            case "imagen_predeterminada_curso_2.png":
            case "imagen_predeterminada_curso_3.png":
            case "imagen_predeterminada_edCurso_1.png":
            case "imagen_predeterminada_edCurso_2.jpg":
            case "imagen_predeterminada_progForm_1.png":
            case "imagen_predeterminada_progForm_2.png":
            case "docente_placeholder.png":
            case "estudiante_placeholder.png":
                return "Presentacion/Recursos/" + archivo;
            default:
                return null;
        }
    }

    private static String tipoImagen(String recurso) {
        String recursoNormalizado = recurso.toLowerCase(java.util.Locale.ROOT);
        return recursoNormalizado.endsWith(".jpg") || recursoNormalizado.endsWith(".jpeg")
            ? "image/jpeg" : recursoNormalizado.endsWith(".webp") ? "image/webp" : "image/png";
    }
}
