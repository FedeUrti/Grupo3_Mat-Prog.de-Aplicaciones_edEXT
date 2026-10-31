package com.grupo3_mat.servidorweb_edext.servlets;

import com.grupo3_mat.edEXT.Logica.DataTypes.DTResultadoEstudio;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtEstudiante;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtUsuario;
import com.grupo3_mat.edEXT.Logica.Fabrica;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorProgramaFormacion;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorUsuario;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/acta-estudio")
public class ActaEstudioServlet extends HttpServlet {

    private final IControladorProgramaFormacion controladorPrograma =
            Fabrica.getInstance().getIControladorProgramaFormacion();
    private final IControladorUsuario controladorUsuario =
            Fabrica.getInstance().getIControladorUsuario();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        DtEstudiante estudiante = estudianteActual(request, response);
        if (estudiante == null) {
            return;
        }

        String programa = limpiar(request.getParameter("programa"));
        List<String> programasInscripto = estudiante.getProgramasInscripto();
        request.setAttribute("programas", programasInscripto);
        request.setAttribute("programaSeleccionado", programa);
        if ("1".equals(request.getParameter("descargar"))) {
            if (programa.isEmpty() || !programasInscripto.contains(programa)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Solo podés generar el acta de un programa en el que estás inscripto.");
                return;
            }
            try {
                List<DTResultadoEstudio> resultados =
                        controladorPrograma.consultarResultadosActa(estudiante.getNickname(), programa);
                escribirPdf(estudiante, programa, resultados, response);
            } catch (SecurityException e) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            } catch (IllegalArgumentException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            } catch (Exception e) {
                getServletContext().log("No se pudo generar el acta de estudio.", e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "No se pudo generar el acta de estudio. Intentá nuevamente.");
            }
            return;
        }
        request.getRequestDispatcher("/acta-estudio.jsp").forward(request, response);
    }

    private DtEstudiante estudianteActual(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        String nickname = session == null ? null : (String) session.getAttribute("usuarioNickname");
        if (nickname == null) {
            response.sendRedirect(request.getContextPath() + "/login?motivo=acta");
            return null;
        }
        DtUsuario usuario = controladorUsuario.obtenerInfoUsuario(nickname);
        if (usuario instanceof DtEstudiante estudiante) {
            return estudiante;
        }
        if (usuario == null && session != null) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login?motivo=acta");
            return null;
        }
        response.sendRedirect(request.getContextPath() + "/inicio?acceso=estudiante");
        return null;
    }

    private void escribirPdf(DtEstudiante estudiante, String programa,
            List<DTResultadoEstudio> resultados, HttpServletResponse response) throws IOException {
        try (ByteArrayOutputStream contenido = new ByteArrayOutputStream()) {
            Document documento = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
            PdfWriter.getInstance(documento, contenido);
            documento.open();

            Font titulo = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font subtitulo = new Font(Font.HELVETICA, 12, Font.NORMAL);
            Font encabezado = new Font(Font.HELVETICA, 10, Font.BOLD);
            documento.add(new Paragraph("Acta de estudio", titulo));
            documento.add(new Paragraph("Programa: " + programa, subtitulo));
            documento.add(new Paragraph("Estudiante: " + estudiante.getNombre() + " "
                    + estudiante.getApellido() + " (" + estudiante.getNickname() + ")", subtitulo));
            documento.add(new Paragraph("Fecha de emisión: " + LocalDate.now(), subtitulo));
            documento.add(new Paragraph(" "));

            PdfPTable tabla = new PdfPTable(new float[]{3.2f, 2.6f, 1.4f, 1.8f, 1.8f});
            tabla.setWidthPercentage(100);
            agregarCelda(tabla, "Curso", encabezado);
            agregarCelda(tabla, "Edición", encabezado);
            agregarCelda(tabla, "Evaluación", encabezado);
            agregarCelda(tabla, "Resultado", encabezado);
            agregarCelda(tabla, "Fecha del resultado", encabezado);
            for (DTResultadoEstudio resultado : resultados) {
                agregarCelda(tabla, resultado.getNombreCurso(), subtitulo);
                agregarCelda(tabla, texto(resultado.getNombreEdicion(), "—"), subtitulo);
                agregarCelda(tabla, resultado.getEvaluacion() == null
                        ? "—" : resultado.getEvaluacion() + " / 12", subtitulo);
                agregarCelda(tabla, resultado.isTieneResultado()
                        ? (resultado.isAprobado() ? "Aprobado" : "No aprobado")
                        : "Sin resultado registrado", subtitulo);
                agregarCelda(tabla, resultado.getFechaResultado() == null
                        ? "No registrada" : resultado.getFechaResultado().toString(), subtitulo);
            }
            documento.add(tabla);
            documento.close();

            String nombreArchivo = "acta-estudio-"
                    + URLEncoder.encode(programa.replaceAll("[^A-Za-z0-9._-]", "_"), StandardCharsets.UTF_8)
                    + ".pdf";
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + nombreArchivo + "\"");
            response.setContentLength(contenido.size());
            contenido.writeTo(response.getOutputStream());
        } catch (DocumentException e) {
            throw new IOException("No se pudo crear el documento PDF del acta.", e);
        }
    }

    private static void agregarCelda(PdfPTable tabla, String texto, Font fuente) {
        com.lowagie.text.pdf.PdfPCell celda =
                new com.lowagie.text.pdf.PdfPCell(new Phrase(texto, fuente));
        celda.setPadding(6);
        tabla.addCell(celda);
    }

    private static String texto(String valor, String alternativo) {
        return valor == null || valor.isBlank() ? alternativo : valor;
    }

    private static String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
