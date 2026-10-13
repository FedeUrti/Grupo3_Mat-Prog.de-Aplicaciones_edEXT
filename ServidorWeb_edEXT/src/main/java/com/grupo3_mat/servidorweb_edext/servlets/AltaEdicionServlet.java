/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.grupo3_mat.servidorweb_edext.servlets;

import com.grupo3_mat.edEXT.Logica.Fabrica;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorEdicion;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorInstituto;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCategoria;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorUsuario;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtDocente;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtUsuario;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTEdicionCurso;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/alta-edicion-curso")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024, maxRequestSize = 6 * 1024 * 1024)
public class AltaEdicionServlet extends HttpServlet {

    private IControladorCurso controladorCurso;
    private IControladorEdicion controladorEdicion;
    private IControladorUsuario controladorUsuario;
    private IControladorInstituto controladorInstituto;
    private IControladorCategoria controladorCategoria;

    @Override
    public void init() throws ServletException {
        Fabrica fabrica = Fabrica.getInstance();
        this.controladorCurso = fabrica.getIControladorCurso();
        this.controladorEdicion = fabrica.getIControladorEdicion();
        this.controladorUsuario = fabrica.getIControladorUsuario();
        this.controladorInstituto = fabrica.getIControladorInstituto();
        this.controladorCategoria = fabrica.getIControladorCategoria();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String nickname = (session != null) ? (String) session.getAttribute("usuarioNickname") : null;

        if (nickname == null) {
            response.sendRedirect(request.getContextPath() + "/login?motivo=docente");
            return;
        }

        DtUsuario usuario = controladorUsuario.obtenerInfoUsuario(nickname);
        if (!(usuario instanceof DtDocente)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado: Se requiere rol de Docente.");
            return;
        }

        DtDocente docente = (DtDocente) usuario;
        List<String> institutosDocente = docente.getInstitutos();
        cargarCatalogosNavegacion(request);
        request.setAttribute("institutosDocente", institutosDocente);

        String institutoSeleccionado = request.getParameter("instituto");
        if ((institutoSeleccionado == null || institutoSeleccionado.isBlank()) && institutosDocente != null && !institutosDocente.isEmpty()) {
            institutoSeleccionado = institutosDocente.get(0);
        }
        request.setAttribute("institutoSeleccionado", institutoSeleccionado);

        String cursoSeleccionado = request.getParameter("curso");
        if (cursoSeleccionado != null && !cursoSeleccionado.isBlank()) {
            request.setAttribute("cursoSeleccionado", cursoSeleccionado);
        }

        armarEstructurasJson(request, institutosDocente);

        request.getRequestDispatcher("/alta-edicion-de-curso.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String nickname = (session != null) ? (String) session.getAttribute("usuarioNickname") : null;

        if (nickname == null) {
            response.sendRedirect(request.getContextPath() + "/login?motivo=docente");
            return;
        }

        DtUsuario usuario = controladorUsuario.obtenerInfoUsuario(nickname);
        if (!(usuario instanceof DtDocente)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado: Se requiere rol de Docente.");
            return;
        }

        DtDocente docente = (DtDocente) usuario;
        request.setCharacterEncoding("UTF-8");

        String nomInstituto = request.getParameter("instituto");
        String nombreCurso = request.getParameter("curso");
        String nombreEdicion = request.getParameter("nombreEdicion");
        String fechaInicioStr = request.getParameter("fechaInicio");
        String fechaFinStr = request.getParameter("fechaFin");
        String tieneCupoStr = request.getParameter("tieneCupo");
        String cupoStr = request.getParameter("cupo");
        String[] docentesSeleccionados = request.getParameterValues("docentes");

        String imagenGuardada = null;

        try {
            if (nomInstituto == null || nomInstituto.isBlank() ||
                nombreCurso == null || nombreCurso.isBlank() ||
                nombreEdicion == null || nombreEdicion.isBlank() ||
                fechaInicioStr == null || fechaInicioStr.isBlank() ||
                fechaFinStr == null || fechaFinStr.isBlank()) {
                throw new IllegalArgumentException("Todos los campos obligatorios deben ser completados.");
            }
            List<String> institutosDocente = docente.getInstitutos();
            if (institutosDocente == null || !institutosDocente.contains(nomInstituto)) {
                throw new IllegalArgumentException("No podés crear ediciones para este instituto.");
            }
            List<String> cursosDelInstituto = controladorCurso.listarCursosPorInstituto(nomInstituto);
            if (cursosDelInstituto == null || !cursosDelInstituto.contains(nombreCurso)) {
                throw new IllegalArgumentException("El curso seleccionado no pertenece al instituto indicado.");
            }
            List<String> docentesDelInstituto = controladorUsuario.listarNicknamesDocentesPorInstituto(nomInstituto);
            if (docentesSeleccionados != null) {
                for (String docenteSeleccionado : docentesSeleccionados) {
                    if (docentesDelInstituto == null || !docentesDelInstituto.contains(docenteSeleccionado)) {
                        throw new IllegalArgumentException("Solo podés asignar docentes de este instituto.");
                    }
                }
            }

            LocalDate fechaInicio = LocalDate.parse(fechaInicioStr);
            LocalDate fechaFin = LocalDate.parse(fechaFinStr);

            if (fechaFin.isBefore(fechaInicio)) {
                throw new IllegalArgumentException("La fecha de fin debe ser posterior o igual a la fecha de inicio.");
            }

            int cupo = 0;
            boolean tieneCupo = "true".equalsIgnoreCase(tieneCupoStr);

            if (tieneCupo) {
                if (cupoStr == null || cupoStr.isBlank()) {
                    throw new IllegalArgumentException("Si selecciona limitar el cupo, debe ingresar una cantidad.");
                }
                try {
                    cupo = Integer.parseInt(cupoStr);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("El cupo debe ser un valor numérico entero válido.");
                }
                if (cupo <= 0) {
                    throw new IllegalArgumentException("El cupo debe ser un número mayor a cero.");
                }
            }

            int cupoDisponible = cupo;
            LocalDate fechaPublicacion = LocalDate.now();

            List<String> listaDocentes = (docentesSeleccionados != null)
                ? Arrays.asList(docentesSeleccionados)
                : new ArrayList<>();

            // Guardado físico de la imagen en disco
            imagenGuardada = guardarImagen(request.getPart("imagen"));

            DTEdicionCurso dtEdicion = new DTEdicionCurso(
                nombreEdicion,
                fechaInicio,
                fechaFin,
                cupo,
                cupoDisponible,
                fechaPublicacion,
                listaDocentes,
                imagenGuardada != null ? imagenGuardada : ""
            );

            controladorEdicion.altaEdicionCurso(nombreCurso, dtEdicion);

            response.sendRedirect(request.getContextPath() + "/consulta-curso?nombre=" + URLEncoder.encode(nombreCurso, StandardCharsets.UTF_8) + "&exitoEdicion=true");

        } catch (DateTimeParseException e) {
            borrarImagenSiExiste(imagenGuardada);
            request.setAttribute("error", "El formato de las fechas ingresadas no es válido.");
            recargarDatosFormulario(request, docente, nomInstituto, nombreCurso, docentesSeleccionados);
            request.getRequestDispatcher("/alta-edicion-de-curso.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            borrarImagenSiExiste(imagenGuardada);
            request.setAttribute("error", e.getMessage());
            recargarDatosFormulario(request, docente, nomInstituto, nombreCurso, docentesSeleccionados);
            request.getRequestDispatcher("/alta-edicion-de-curso.jsp").forward(request, response);
        } catch (ServletException e) {
            borrarImagenSiExiste(imagenGuardada);
            getServletContext().log("No se pudo procesar la imagen de la edición.", e);
            request.setAttribute("error", "No se pudo procesar la imagen. Usá PNG, JPEG, GIF o WebP de hasta 5 MB.");
            recargarDatosFormulario(request, docente, nomInstituto, nombreCurso, docentesSeleccionados);
            request.getRequestDispatcher("/alta-edicion-de-curso.jsp").forward(request, response);
        } catch (Exception e) {
            borrarImagenSiExiste(imagenGuardada);
            getServletContext().log("No se pudo registrar la edición del curso.", e);
            request.setAttribute("error", "No se pudo registrar la edición: " + e.getMessage());
            recargarDatosFormulario(request, docente, nomInstituto, nombreCurso, docentesSeleccionados);
            request.getRequestDispatcher("/alta-edicion-de-curso.jsp").forward(request, response);
        }
    }

    private void recargarDatosFormulario(HttpServletRequest request, DtDocente docente, String nomInstituto, String nombreCurso, String[] docentesSeleccionados) {
        cargarCatalogosNavegacion(request);
        List<String> institutosDocente = docente.getInstitutos();
        request.setAttribute("institutosDocente", institutosDocente);

        request.setAttribute("institutoSeleccionado", nomInstituto);
        request.setAttribute("cursoSeleccionado", nombreCurso);

        if (docentesSeleccionados != null) {
            request.setAttribute("docentesSeleccionados", Arrays.asList(docentesSeleccionados));
        }

        request.setAttribute("nombreEdicion", request.getParameter("nombreEdicion"));
        request.setAttribute("fechaInicio", request.getParameter("fechaInicio"));
        request.setAttribute("fechaFin", request.getParameter("fechaFin"));

        // Conservar valores del checkbox y del cupo para rellenar la JSP
        String tieneCupoStr = request.getParameter("tieneCupo");
        request.setAttribute("tieneCupo", tieneCupoStr);
        if ("true".equalsIgnoreCase(tieneCupoStr)) {
            request.setAttribute("cupo", request.getParameter("cupo"));
        }

        armarEstructurasJson(request, institutosDocente);
    }

    private void cargarCatalogosNavegacion(HttpServletRequest request) {
        request.setAttribute("institutos", controladorInstituto.listarInstitutos());
        request.setAttribute("categorias", controladorCategoria.listarCategorias());
    }

    private void armarEstructurasJson(HttpServletRequest request, List<String> institutos) {
        Map<String, List<String>> cursosMap = new HashMap<>();
        Map<String, List<String>> docentesMap = new HashMap<>();

        if (institutos != null) {
            for (String inst : institutos) {
                List<String> cursos = controladorCurso.listarCursosPorInstituto(inst);
                List<String> docentes = controladorUsuario.listarNicknamesDocentesPorInstituto(inst);

                cursosMap.put(inst, cursos != null ? cursos : new ArrayList<>());
                docentesMap.put(inst, docentes != null ? docentes : new ArrayList<>());
            }
        }

        request.setAttribute("cursosPorInstJson", construirJsonDesdeMap(cursosMap));
        request.setAttribute("docentesPorInstJson", construirJsonDesdeMap(docentesMap));
    }

    private String construirJsonDesdeMap(Map<String, List<String>> mapa) {
        StringBuilder sb = new StringBuilder("{");
        boolean primerKey = true;

        for (Map.Entry<String, List<String>> entry : mapa.entrySet()) {
            if (!primerKey) sb.append(",");
            sb.append("\"").append(escapeJson(entry.getKey())).append("\":[");

            List<String> lista = entry.getValue();
            for (int i = 0; i < lista.size(); i++) {
                sb.append("\"").append(escapeJson(lista.get(i))).append("\"");
                if (i < lista.size() - 1) sb.append(",");
            }
            sb.append("]");
            primerKey = false;
        }

        sb.append("}");
        return sb.toString();
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // --- Métodos de gestión de imágenes ---

    private String guardarImagen(Part imagen) throws IOException, ServletException {
        if (imagen == null || imagen.getSize() == 0) {
            return null;
        }

        String extension = extensionImagen(imagen.getContentType());
        Path carpeta = carpetaImagenes();
        Files.createDirectories(carpeta);

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
            getServletContext().log("No se pudo limpiar la imagen de una edición fallida: " + nombre, e);
        }
    }
}