package com.grupo3_mat.servidorweb_edext.servlets;

import com.grupo3_mat.edEXT.Logica.DataTypes.DtCurso;
import com.grupo3_mat.edEXT.Logica.Fabrica;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCategoria;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorInstituto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@WebServlet(urlPatterns = {"/alta-curso", "/consulta-curso"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 6 * 1024 * 1024)
public class CursoServlet extends HttpServlet {

    private final IControladorCurso cursos = Fabrica.getInstance().getIControladorCurso();
    private final IControladorInstituto institutos = Fabrica.getInstance().getIControladorInstituto();
    private final IControladorCategoria categorias = Fabrica.getInstance().getIControladorCategoria();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1. La ruta de alta prepara catálogos antes de mostrar el formulario.
        if ("/alta-curso".equals(request.getServletPath())) {
            cargarCatalogos(request, response);
            request.getRequestDispatcher("/alta-curso.jsp").forward(request, response);
            return;
        }

        // 2. La otra ruta consulta el curso y envía su DTO a la página de detalle.
        String nombre = limpiar(request.getParameter("nombre"));
        if (nombre.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el nombre del curso.");
            return;
        }

        try {
            request.setAttribute("curso", cursos.consultarCurso(nombre));
            request.getRequestDispatcher("/consulta-curso.jsp").forward(request, response);
        } catch (Exception e) {
            getServletContext().log("No se pudo consultar el curso " + nombre + ".", e);
            boolean noEncontrado = "No existe el curso especificado.".equals(e.getMessage());
            response.setStatus(noEncontrado
                    ? HttpServletResponse.SC_NOT_FOUND
                    : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("error", noEncontrado
                    ? "No se encontró el curso solicitado."
                    : "No se pudo consultar el curso. Verificá la conexión con la base de datos.");
            request.getRequestDispatcher("/consulta-curso.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String imagenGuardada = null;
        try {
            request.setCharacterEncoding("UTF-8");

            // 1. Validar de nuevo en servidor: no se confía en las validaciones del navegador.
            String instituto = requerido(request, "instituto");
            String nombre = requerido(request, "nombre");
            String descripcion = requerido(request, "descripcion");
            String url = requerido(request, "url");
            LocalDate fecha = LocalDate.parse(requerido(request, "fecha"));
            int duracion = entero(request, "duracion", 1);
            int cantHoras = entero(request, "cantHoras", 1);
            int creditos = entero(request, "creditos", 0);

            validarInstituto(instituto);
            if (Character.isDigit(nombre.codePointAt(0))) {
                throw new IllegalArgumentException("El nombre del curso no puede comenzar con un número.");
            }
            validarUrl(url);

            List<String> categoriasElegidas = valoresMultiples(request.getParameterValues("categorias"));
            if (categoriasElegidas.isEmpty()) {
                throw new IllegalArgumentException("Seleccioná al menos una categoría.");
            }
            validarCategorias(categoriasElegidas);

            List<String> previasElegidas = valoresMultiples(request.getParameterValues("previas"));
            validarPrevias(previasElegidas, instituto, nombre);

            // 2. Guardar la imagen con nombre aleatorio fuera del WAR y enviar ese nombre al controlador.
            imagenGuardada = guardarImagen(request.getPart("imagen"));
            cursos.altaCurso(instituto, nombre, descripcion, duracion, cantHoras, creditos,
                    url, fecha, previasElegidas, categoriasElegidas, imagenGuardada);

            // 3. Redirigir tras guardar evita duplicar el alta al recargar (patrón PRG).
            String nombreUrl = URLEncoder.encode(nombre, java.nio.charset.StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/consulta-curso?nombre=" + nombreUrl + "&registrado=1");
        } catch (IllegalArgumentException | java.time.DateTimeException e) {
            borrarImagenSiExiste(imagenGuardada);
            cargarCatalogos(request, response);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", e.getMessage());
            conservarFormulario(request);
            request.getRequestDispatcher("/alta-curso.jsp").forward(request, response);
        } catch (ServletException e) {
            borrarImagenSiExiste(imagenGuardada);
            getServletContext().log("No se pudo procesar la imagen del curso.", e);
            cargarCatalogos(request, response);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", "No se pudo procesar la imagen. Usá PNG, JPEG, GIF o WebP de hasta 5 MB.");
            conservarFormulario(request);
            request.getRequestDispatcher("/alta-curso.jsp").forward(request, response);
        } catch (Exception e) {
            borrarImagenSiExiste(imagenGuardada);
            getServletContext().log("No se pudo registrar el curso.", e);
            cargarCatalogos(request, response);
            String mensaje = mensajeAlta(e);
            response.setStatus(mensaje.startsWith("El curso '")
                    ? HttpServletResponse.SC_CONFLICT
                    : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("error", mensaje);
            conservarFormulario(request);
            request.getRequestDispatcher("/alta-curso.jsp").forward(request, response);
        }
    }

    private void cargarCatalogos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Los selectores se alimentan de la base; la JSP no mantiene listas duplicadas.
            request.setAttribute("institutos", institutos.listarInstitutos());
            request.setAttribute("categorias", categorias.listarCategorias());
            List<DtCurso> cursosDisponibles = new ArrayList<>();
            for (String nombre : cursos.listarCursos()) {
                cursosDisponibles.add(cursos.consultarCurso(nombre));
            }
            request.setAttribute("cursosDisponibles", cursosDisponibles);
        } catch (Exception e) {
            getServletContext().log("No se pudieron cargar los datos para el formulario de curso.", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("error", "No se pudieron cargar institutos y categorías. Verificá la conexión con la base.");
        }
    }

    private void validarInstituto(String instituto) {
        if (!institutos.listarInstitutos().contains(instituto)) {
            throw new IllegalArgumentException("Seleccioná un instituto válido.");
        }
    }

    private void validarCategorias(List<String> seleccionadas) {
        Set<String> disponibles = new HashSet<>(categorias.listarCategorias());
        if (!disponibles.containsAll(seleccionadas)) {
            throw new IllegalArgumentException("Una de las categorías seleccionadas ya no está disponible.");
        }
    }

    private void validarPrevias(List<String> previas, String instituto, String nombre) throws Exception {
        Set<String> unicas = new HashSet<>();
        for (String previa : previas) {
            if (!unicas.add(previa)) {
                throw new IllegalArgumentException("No repitas cursos previos.");
            }
            if (previa.equalsIgnoreCase(nombre)) {
                throw new IllegalArgumentException("Un curso no puede ser previa de sí mismo.");
            }
            DtCurso cursoPrevia = cursos.consultarCurso(previa);
            if (!instituto.equals(cursoPrevia.getNomInstituto())) {
                throw new IllegalArgumentException("Las previas deben pertenecer al instituto seleccionado.");
            }
        }
    }

    private String guardarImagen(Part imagen) throws IOException, ServletException {
        if (imagen == null || imagen.getSize() == 0) {
            return null;
        }

        String extension = extensionImagen(imagen.getContentType());
        Path carpeta = carpetaImagenes();
        Files.createDirectories(carpeta);
        // Nunca se usa el nombre de archivo enviado por el navegador.
        String nombreSeguro = UUID.randomUUID() + extension;
        Path archivo = carpeta.resolve(nombreSeguro);
        try (InputStream contenido = imagen.getInputStream()) {
            Files.copy(contenido, archivo);
        } catch (IOException e) {
            try {
                Files.deleteIfExists(archivo);
            } catch (IOException errorLimpieza) {
                e.addSuppressed(errorLimpieza);
            }
            throw e;
        }
        return nombreSeguro;
    }

    private String extensionImagen(String contentType) throws ServletException {
        if (contentType == null) {
            throw new ServletException("La imagen no informa su tipo.");
        }
        switch (contentType.toLowerCase(java.util.Locale.ROOT)) {
            case "image/png":
                return ".png";
            case "image/jpeg":
                return ".jpg";
            case "image/gif":
                return ".gif";
            case "image/webp":
                return ".webp";
            default:
                throw new ServletException("Tipo de imagen no admitido.");
        }
    }

    private Path carpetaImagenes() {
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
            getServletContext().log("No se pudo limpiar la imagen de un alta fallida: " + nombre, e);
        }
    }

    private static List<String> valoresMultiples(String[] valores) {
        if (valores == null) {
            return new ArrayList<>();
        }
        List<String> limpios = new ArrayList<>();
        for (String valor : valores) {
            if (valor != null && !valor.isBlank()) {
                limpios.add(valor.trim());
            }
        }
        return limpios;
    }

    private static String requerido(HttpServletRequest request, String nombre) {
        String valor = limpiar(request.getParameter(nombre));
        if (valor.isEmpty()) {
            throw new IllegalArgumentException("El campo " + nombre + " es obligatorio.");
        }
        return valor;
    }

    private static int entero(HttpServletRequest request, String nombre, int minimo) {
        try {
            int valor = Integer.parseInt(requerido(request, nombre));
            if (valor < minimo) {
                throw new IllegalArgumentException("El campo " + nombre + " debe ser mayor o igual a " + minimo + ".");
            }
            return valor;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El campo " + nombre + " debe ser un número entero.");
        }
    }

    private static void validarUrl(String valor) {
        try {
            URI uri = URI.create(valor);
            String esquema = uri.getScheme();
            if (uri.getHost() == null || esquema == null
                    || !(esquema.equalsIgnoreCase("http") || esquema.equalsIgnoreCase("https"))) {
                throw new IllegalArgumentException("Ingresá una URL válida que comience con http:// o https://.");
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Ingresá una URL válida que comience con http:// o https://.");
        }
    }

    private void conservarFormulario(HttpServletRequest request) {
        // Permite corregir errores sin volver a escribir los campos de texto.
        for (String campo : Arrays.asList("instituto", "nombre", "descripcion", "duracion",
                "cantHoras", "creditos", "url", "fecha")) {
            request.setAttribute("form_" + campo, request.getParameter(campo));
        }
        request.setAttribute("categoriasMarcadas", selecciones(request.getParameterValues("categorias")));
        request.setAttribute("previasMarcadas", selecciones(request.getParameterValues("previas")));
    }

    private static java.util.Map<String, Boolean> selecciones(String[] valores) {
        java.util.Map<String, Boolean> marcadas = new java.util.HashMap<>();
        for (String valor : valoresMultiples(valores)) {
            marcadas.put(valor, Boolean.TRUE);
        }
        return marcadas;
    }

    private static String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private static String mensajeAlta(Exception e) {
        String mensaje = e.getMessage();
        if (mensaje != null && mensaje.startsWith("El curso '") && mensaje.endsWith(" ya existe.")) {
            return mensaje;
        }
        return "No se pudo registrar el curso. Verificá la conexión y los datos, e intentá nuevamente.";
    }
}
