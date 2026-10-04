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

@WebServlet("/imagenes-curso/*")
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
                response.setContentType("image/png");
                response.setHeader("X-Content-Type-Options", "nosniff");
                response.setHeader("Cache-Control", "public, max-age=86400");
                imagen.transferTo(response.getOutputStream());
            }
            return;
        }

        // 3. Las imágenes cargadas desde el formulario solo admiten nombres UUID y extensiones conocidas.
        if (!nombre.matches("[a-f0-9-]{36}\\.(png|jpg|gif|webp)")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 4. Usar la carpeta configurada para Docker o la carpeta local predeterminada.
        String configurada = System.getenv("EDEXT_UPLOAD_DIR");
        Path carpeta = configurada != null && !configurada.isBlank()
                ? Paths.get(configurada).toAbsolutePath().normalize()
                : Paths.get(System.getProperty("user.home"), ".edext", "uploads").toAbsolutePath().normalize();
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
                    : nombreArchivo.endsWith(".jpg") ? "image/jpeg"
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
        // Aceptar ruta antigua del modelo y el nombre plano, pero solo para archivos conocidos.
        String archivo = nombre.substring(nombre.lastIndexOf('/') + 1);
        switch (archivo) {
            case "imagen_predeterminada_curso_1.png":
            case "imagen_predeterminada_curso_2.png":
            case "imagen_predeterminada_curso_3.png":
                return "Presentacion/Recursos/" + archivo;
            default:
                return null;
        }
    }
}
