package com.grupo3_mat.servidorweb_edext.servlets;

import com.grupo3_mat.servidorweb_edext.soap.generated.DtResultadoBusqueda;
import com.grupo3_mat.servidorweb_edext.soap.ControladoresRemotos;
import com.grupo3_mat.servidorweb_edext.soap.generated.BusquedaSoap;
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
import java.time.LocalDate;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@WebServlet("/buscar")
public class BusquedaServlet extends HttpServlet {

    // El desplegable muestra una cantidad acotada de cada clase para que haya resultados variados.
    private static final int MAX_RESULTADOS_POR_TIPO = 8;
    private final BusquedaSoap busqueda = ControladoresRemotos.getBusquedaSoap();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String consultaCompleta = request.getParameter("query");
        String ordenarPor = valorO(request.getParameter("ordenarPor"), "alfabetico");
        String direccion = valorO(request.getParameter("direccion"), "asc");
        if (consultaCompleta != null) {
            mostrarResultados(request, response, limpiar(consultaCompleta), limpiar(request.getParameter("tipo")),
                    ordenarPor, direccion);
            return;
        }

        // "q" es el único dato necesario: este endpoint responde JSON y no renderiza una JSP.
        String query = limpiar(request.getParameter("q"));
        String tipo = limpiar(request.getParameter("tipo"));
        if (!tipoValido(tipo)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El filtro de búsqueda no es válido.");
            return;
        }
        if (!ordenValido(ordenarPor) || !direccionValida(direccion)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El orden de búsqueda no es válido.");
            return;
        }
        if (query.length() > 100) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "La búsqueda no puede superar los 100 caracteres.");
            return;
        }

        try {
            String nicknameSesion = nicknameSesion(request);
            boolean incluirTodos = "true".equalsIgnoreCase(request.getParameter("incluirTodos"));
            List<Resultado> resultados = query.isBlank() && !incluirTodos
                ? List.of()
                : buscar(query, request.getContextPath(), nicknameSesion, tipo, ordenarPor, direccion,
                    !incluirTodos);
            // No se cachean coincidencias para que el autocompletado refleje cambios recientes en la base.
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType("application/json");
            response.setHeader("Cache-Control", "no-store");
            response.getWriter().write(serializar(resultados));
        } catch (Exception e) {
            // El detalle técnico queda en el log; el cliente recibe un error genérico y seguro.
            getServletContext().log("No se pudo completar la búsqueda global.", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"No se pudo completar la búsqueda.\"}");
        }
    }

    private void mostrarResultados(HttpServletRequest request, HttpServletResponse response, String query, String tipo,
            String ordenarPor, String direccion) throws ServletException, IOException {
        request.setAttribute("busquedaIniciada", true);
        request.setAttribute("query", query);
        request.setAttribute("filtroBusqueda", tipo.isEmpty() ? "todos" : tipo);
        request.setAttribute("ordenarPor", ordenarPor);
        request.setAttribute("direccion", direccion);
        if (!tipoValido(tipo)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", "El filtro de búsqueda no es válido.");
        } else if (!ordenValido(ordenarPor) || !direccionValida(direccion)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", "El orden de búsqueda no es válido.");
        } else if (query.length() > 100) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", "La búsqueda no puede superar los 100 caracteres.");
        } else {
            try {
                String nicknameSesion = nicknameSesion(request);
                request.setAttribute("resultadosBusqueda",
                        buscar(query, request.getContextPath(), nicknameSesion, tipo, ordenarPor, direccion, false));
                request.setAttribute("urlRetornoBusqueda", urlBusqueda(request.getContextPath(), query,
                        tipo.isBlank() ? "todos" : tipo, ordenarPor, direccion));
            } catch (Exception e) {
                getServletContext().log("No se pudo completar la búsqueda global.", e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                request.setAttribute("error", "No se pudo completar la búsqueda. Intentá nuevamente.");
            }
        }
        tomarMensajes(request);
        request.getRequestDispatcher("/WEB-INF/views/busqueda.jsp").forward(request, response);
    }

    private List<Resultado> buscar(String consulta, String contextPath, String nicknameSesion, String tipo,
            String ordenarPor, String direccion, boolean limitarResultados) throws Exception {
        List<Resultado> resultados = new ArrayList<>();
        List<DtResultadoBusqueda> resultadosCentral = busqueda.buscarResultados(
                consulta, tipo, nicknameSesion, ordenarPor, direccion,
                limitarResultados ? MAX_RESULTADOS_POR_TIPO : 0);
        for (DtResultadoBusqueda resultadoCentral : resultadosCentral) {
            LocalDate fechaPublicacion = fecha(resultadoCentral.getFechaPublicacion());
            String nombre = resultadoCentral.getNombre();
            switch (resultadoCentral.getTipo()) {
                case "Curso" -> {
                    BigDecimal promedio = resultadoCentral.getPromedioValoracion() == null
                            ? BigDecimal.ZERO : resultadoCentral.getPromedioValoracion();
                    String detalle = combinar(resultadoCentral.getInstituto(), resultadoCentral.getDescripcion());
                    detalle = combinar(detalle, textoPublicacion(fechaPublicacion));
                    resultados.add(new Resultado(nombre, "Curso", detalle,
                            urlCurso(contextPath, nombre),
                            urlImagen(contextPath, imagenCurso(resultadoCentral.getImagenPath(), false)),
                            null, false, false, fechaPublicacion, promedio,
                            Math.toIntExact(resultadoCentral.getCantidadValoraciones()),
                            Math.round(promedio.doubleValue() * 2.0) / 2.0,
                            resultadoCentral.getRelevancia()));
                }
                case "Edición" -> {
                    String detalle = combinar("Curso: " + resultadoCentral.getNombreCurso(),
                            fechas(fecha(resultadoCentral.getFechaInicio()), fecha(resultadoCentral.getFechaFin())));
                    detalle = combinar(detalle, textoPublicacion(fechaPublicacion));
                    resultados.add(new Resultado(nombre, "Edición", detalle,
                            urlCurso(contextPath, resultadoCentral.getNombreCurso()),
                            urlImagen(contextPath, imagenCurso(resultadoCentral.getImagenPath(), true)),
                            null, false, false, fechaPublicacion, null, 0, 0.0,
                            resultadoCentral.getRelevancia()));
                }
                case "Programa" -> {
                    String detalle = resultadoCentral.getDescripcion();
                    if (resultadoCentral.getNicknameDocente() != null
                            && !resultadoCentral.getNicknameDocente().isBlank()) {
                        detalle = combinar(detalle, "Docente: " + resultadoCentral.getNicknameDocente());
                    }
                    detalle = combinar(detalle, textoPublicacion(fechaPublicacion));
                    resultados.add(new Resultado(nombre, "Programa", detalle,
                            urlPrograma(contextPath, nombre),
                            urlImagen(contextPath, imagenPrograma(resultadoCentral.getImagenPath())),
                            null, false, false, fechaPublicacion, null, 0, 0.0,
                            resultadoCentral.getRelevancia()));
                }
                case "Usuario" -> {
                    String displayName = combinar(nombre, resultadoCentral.getApellido());
                    String detalle = "@" + resultadoCentral.getNickname() + " · "
                            + resultadoCentral.getRol() + " · edEXT";
                    String imagen = UsuarioServlet.imagenPerfil(resultadoCentral.getImagenPath(),
                            "Docente".equals(resultadoCentral.getRol()));
                    resultados.add(new Resultado(displayName, "Usuario", detalle,
                            urlUsuario(contextPath, resultadoCentral.getNickname()),
                            urlImagen(contextPath, imagen), resultadoCentral.getNickname(),
                            resultadoCentral.isSiguiendo(), resultadoCentral.isPropio(), null,
                            null, 0, 0.0, resultadoCentral.getRelevancia()));
                }
                default -> throw new IllegalStateException(
                        "El servicio de búsqueda devolvió un tipo desconocido: " + resultadoCentral.getTipo());
            }
        }
        ordenarResultados(resultados, ordenarPor, direccion);
        return resultados;
    }

    private static void ordenarResultados(List<Resultado> resultados, String ordenarPor, String direccion) {
        Comparator<Resultado> porNombre = Comparator
                .comparing((Resultado resultado) -> normalizar(resultado.nombre))
                .thenComparing(Resultado::getNombre, String.CASE_INSENSITIVE_ORDER);
        Comparator<Resultado> comparador = Comparator.comparingInt(resultado -> resultado.relevancia);
        if ("publicacion".equals(ordenarPor)) {
            Comparator<LocalDate> comparadorFechas = "desc".equals(direccion)
                    ? Comparator.nullsLast(Comparator.reverseOrder())
                    : Comparator.nullsLast(Comparator.naturalOrder());
            comparador = comparador.thenComparing(Resultado::getFechaPublicacion, comparadorFechas)
                    .thenComparing(porNombre);
        } else if ("desc".equals(direccion)) {
            comparador = comparador.thenComparing(porNombre.reversed());
        } else {
            comparador = comparador.thenComparing(porNombre);
        }
        resultados.sort(comparador);
    }

    private static String textoPublicacion(LocalDate fecha) {
        return fecha == null ? "" : "Publicado: " + fecha;
    }

    private static boolean tipoValido(String tipo) {
        return tipo == null || tipo.isBlank() || tipo.equalsIgnoreCase("todos")
                || tipo.equalsIgnoreCase("curso") || tipo.equalsIgnoreCase("usuario")
                || tipo.equalsIgnoreCase("programa") || tipo.equalsIgnoreCase("edicion");
    }

    private static boolean ordenValido(String ordenarPor) {
        return "alfabetico".equals(ordenarPor) || "publicacion".equals(ordenarPor);
    }

    private static boolean direccionValida(String direccion) {
        return "asc".equals(direccion) || "desc".equals(direccion);
    }

    private static String valorO(String valor, String predeterminado) {
        String limpio = limpiar(valor);
        return limpio.isEmpty() ? predeterminado : limpio.toLowerCase(Locale.ROOT);
    }

    private static String normalizar(String texto) {
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return sinTildes.toLowerCase(Locale.ROOT);
    }

    private static String fechas(LocalDate inicio, LocalDate fin) {
        if (inicio == null && fin == null) {
            return "";
        }
        return "Fechas: " + (inicio == null ? "sin fecha de inicio" : inicio)
                + " – " + (fin == null ? "sin fecha de fin" : fin);
    }

    private static LocalDate fecha(String fecha) {
        return fecha == null || fecha.isBlank() ? null : LocalDate.parse(fecha);
    }

    private static String combinar(String primero, String segundo) {
        // Arma los textos auxiliares sin dejar separadores cuando uno de los dos datos falta.
        String a = primero == null ? "" : primero.trim();
        String b = segundo == null ? "" : segundo.trim();
        if (a.isEmpty()) {
            return b;
        }
        return b.isEmpty() ? a : a + " · " + b;
    }

    private static String urlCurso(String contextPath, String nombre) {
        // El nombre del curso se escapa como parámetro y la ruta respeta el contexto del despliegue.
        return contextPath + "/consulta-curso?nombre=" + URLEncoder.encode(nombre, StandardCharsets.UTF_8);
    }

    private static String urlPrograma(String contextPath, String nombre) {
        return contextPath + "/consulta-programa?nombre=" + URLEncoder.encode(nombre, StandardCharsets.UTF_8);
    }

    private static String urlUsuario(String contextPath, String nickname) {
        // Los perfiles ya tienen una vista web, así que una coincidencia de usuario es navegable.
        return contextPath + "/consulta-usuario?nickname="
                + URLEncoder.encode(nickname, StandardCharsets.UTF_8);
    }

    private static String urlImagen(String contextPath, String nombre) {
        boolean imagenUsuario = nombre.equals("docente_placeholder.png") || nombre.equals("estudiante_placeholder.png")
                || nombre.matches("Presentacion/Recursos/datos_prueba/usuario_[a-z0-9_-]+\\.(png|jpg|jpeg)");
        String servlet = imagenUsuario
                ? "/imagenes-usuario" : "/imagenes-curso";
        return contextPath + servlet + "?nombre=" + URLEncoder.encode(nombre, StandardCharsets.UTF_8);
    }

    private static String imagenCurso(String ruta, boolean edicion) {
        String rutaNormalizada = ruta == null ? "" : ruta.replace('\\', '/');
        String prefijoRecursos = "Presentacion/Recursos/";
        if (rutaNormalizada.matches("Presentacion/Recursos/datos_prueba/(curso|edicion)_[a-z0-9_-]+\\.(png|jpg|jpeg)")) {
            return rutaNormalizada.substring(prefijoRecursos.length());
        }
        String nombre = rutaNormalizada.substring(rutaNormalizada.lastIndexOf('/') + 1);
        if (nombre.matches("[a-f0-9-]{36}\\.(png|jpg|gif|webp)")
                || nombre.equals("imagen_predeterminada_curso_1.png")
                || nombre.equals("imagen_predeterminada_curso_2.png")
                || nombre.equals("imagen_predeterminada_curso_3.png")
                || nombre.equals("imagen_predeterminada_edCurso_1.png")
                || nombre.equals("imagen_predeterminada_edCurso_2.jpg")) {
            return nombre;
        }
        return edicion ? "imagen_predeterminada_edCurso_1.png" : "imagen_predeterminada_curso_1.png";
    }

    private static String imagenPrograma(String ruta) {
        String rutaNormalizada = ruta == null ? "" : ruta.replace('\\', '/');
        String prefijoRecursos = "Presentacion/Recursos/";
        if (rutaNormalizada.matches("Presentacion/Recursos/datos_prueba/programa_[a-z0-9_-]+\\.(png|jpg|jpeg)")) {
            return rutaNormalizada.substring(prefijoRecursos.length());
        }
        String nombre = rutaNormalizada.substring(rutaNormalizada.lastIndexOf('/') + 1);
        if (nombre.matches("(?i)[a-f0-9-]{36}\\.(png|jpg|jpeg|gif|webp)")
                || nombre.matches("(?i)programa_[a-z0-9_-]+_\\d+\\.(png|jpg|jpeg|gif|webp)")
                || nombre.equals("imagen_predeterminada_progForm_1.png")
                || nombre.equals("imagen_predeterminada_progForm_2.png")) {
            return nombre;
        }
        return "imagen_predeterminada_progForm_1.png";
    }

    private static String urlBusqueda(String contextPath, String consulta, String tipo,
            String ordenarPor, String direccion) {
        // Si todavía no existe una vista de detalle, se abre la búsqueda acotada a ese programa.
        return contextPath + "/buscar?query=" + URLEncoder.encode(consulta, StandardCharsets.UTF_8)
                + "&tipo=" + URLEncoder.encode(tipo, StandardCharsets.UTF_8)
                + "&ordenarPor=" + URLEncoder.encode(ordenarPor, StandardCharsets.UTF_8)
                + "&direccion=" + URLEncoder.encode(direccion, StandardCharsets.UTF_8);
    }

    private static String serializar(List<Resultado> resultados) {
        // Se construye solo el formato acotado que necesita el cliente, escapando cada texto como JSON.
        StringBuilder json = new StringBuilder("{\"results\":[");
        for (int i = 0; i < resultados.size(); i++) {
            if (i > 0) {
                json.append(',');
            }
            Resultado resultado = resultados.get(i);
            json.append("{\"nombre\":\"").append(jsonEscapar(resultado.nombre))
                    .append("\",\"tipo\":\"").append(jsonEscapar(resultado.tipo))
                    .append("\",\"detalle\":\"").append(jsonEscapar(resultado.detalle))
                    .append("\",\"url\":");
            if (resultado.url == null) {
                json.append("null");
            } else {
                json.append('"').append(jsonEscapar(resultado.url)).append('"');
            }
            json.append(",\"fechaPublicacion\":");
            if (resultado.fechaPublicacion == null) {
                json.append("null");
            } else {
                json.append('\"').append(resultado.fechaPublicacion).append('\"');
            }
            json.append(",\"imagenUrl\":");
            if (resultado.imagenUrl == null) {
                json.append("null");
            } else {
                json.append('"').append(jsonEscapar(resultado.imagenUrl)).append('"');
            }
            json.append(",\"nickname\":");
            if (resultado.nickname == null) {
                json.append("null");
            } else {
                json.append('"').append(jsonEscapar(resultado.nickname)).append('"');
            }
            json.append(",\"siguiendo\":").append(resultado.siguiendo)
                    .append(",\"propio\":").append(resultado.propio)
                    .append(",\"promedioValoracion\":").append(resultado.promedioValoracion)
                    .append(",\"cantidadValoraciones\":").append(resultado.cantidadValoraciones)
                    .append(",\"estrellasValoracion\":").append(resultado.estrellasValoracion);
            json.append('}');
        }
        return json.append("]}").toString();
    }

    private static String jsonEscapar(String texto) {
        // JSON requiere escapar comillas, barras, saltos y los caracteres de control.
        StringBuilder salida = new StringBuilder(texto.length());
        for (int i = 0; i < texto.length(); i++) {
            char caracter = texto.charAt(i);
            switch (caracter) {
                case '"':
                    salida.append("\\\"");
                    break;
                case '\\':
                    salida.append("\\\\");
                    break;
                case '\b':
                    salida.append("\\b");
                    break;
                case '\f':
                    salida.append("\\f");
                    break;
                case '\n':
                    salida.append("\\n");
                    break;
                case '\r':
                    salida.append("\\r");
                    break;
                case '\t':
                    salida.append("\\t");
                    break;
                default:
                    if (caracter < 0x20) {
                        salida.append(String.format("\\u%04x", (int) caracter));
                    } else {
                        salida.append(caracter);
                    }
            }
        }
        return salida.toString();
    }

    private static String limpiar(String valor) {
        // Una búsqueda ausente se trata como vacía y los espacios externos no afectan la coincidencia.
        return valor == null ? "" : valor.trim();
    }

    // El DTO web evita serializar directamente los objetos de negocio o entidades de persistencia.
    public static final class Resultado {

        private final String nombre;
        private final String tipo;
        private final String detalle;
        private final String url;
        private final LocalDate fechaPublicacion;
        private final String imagenUrl;
        private final String nickname;
        private final boolean siguiendo;
        private final boolean propio;
        private final BigDecimal promedioValoracion;
        private final int cantidadValoraciones;
        private final double estrellasValoracion;
        private final int relevancia;

        private Resultado(String nombre, String tipo, String detalle, String url) {
            this(nombre, tipo, detalle, url, null, null, false, false, null);
        }

        private Resultado(String nombre, String tipo, String detalle, String url,
                String imagenUrl) {
            this(nombre, tipo, detalle, url, imagenUrl, null, false, false, null);
        }

        private Resultado(String nombre, String tipo, String detalle, String url,
                String imagenUrl, String nickname, boolean siguiendo, boolean propio) {
            this(nombre, tipo, detalle, url, imagenUrl, nickname, siguiendo, propio, null);
        }

        private Resultado(String nombre, String tipo, String detalle, String url,
                String imagenUrl, String nickname, boolean siguiendo, boolean propio, LocalDate fechaPublicacion) {
            this(nombre, tipo, detalle, url, imagenUrl, nickname, siguiendo, propio, fechaPublicacion,
                    null, 0, 0.0);
        }

        private Resultado(String nombre, String tipo, String detalle, String url,
                String imagenUrl, String nickname, boolean siguiendo, boolean propio, LocalDate fechaPublicacion,
                BigDecimal promedioValoracion, int cantidadValoraciones, double estrellasValoracion) {
            this(nombre, tipo, detalle, url, imagenUrl, nickname, siguiendo, propio, fechaPublicacion,
                    promedioValoracion, cantidadValoraciones, estrellasValoracion, 2);
        }

        private Resultado(String nombre, String tipo, String detalle, String url,
                String imagenUrl, String nickname, boolean siguiendo, boolean propio, LocalDate fechaPublicacion,
                BigDecimal promedioValoracion, int cantidadValoraciones, double estrellasValoracion,
                int relevancia) {
            this.nombre = nombre == null ? "" : nombre;
            this.tipo = tipo == null ? "" : tipo;
            this.detalle = detalle == null ? "" : detalle;
            this.url = url;
            this.fechaPublicacion = fechaPublicacion;
            this.imagenUrl = imagenUrl;
            this.nickname = nickname;
            this.siguiendo = siguiendo;
            this.propio = propio;
            this.promedioValoracion = promedioValoracion;
            this.cantidadValoraciones = cantidadValoraciones;
            this.estrellasValoracion = estrellasValoracion;
            this.relevancia = relevancia;
        }

        public String getNombre() {
            return nombre;
        }

        public String getTipo() {
            return tipo;
        }

        public String getDetalle() {
            return detalle;
        }

        public String getUrl() {
            return url;
        }

        public LocalDate getFechaPublicacion() {
            return fechaPublicacion;
        }

        public String getImagenUrl() {
            return imagenUrl;
        }

        public String getNickname() {
            return nickname;
        }

        public boolean isSiguiendo() {
            return siguiendo;
        }

        public boolean isPropio() {
            return propio;
        }

        public BigDecimal getPromedioValoracion() {
            return promedioValoracion;
        }

        public int getCantidadValoraciones() {
            return cantidadValoraciones;
        }

        public double getEstrellasValoracion() {
            return estrellasValoracion;
        }
    }

    private static String nicknameSesion(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        return sesion == null ? null : (String) sesion.getAttribute("usuarioNickname");
    }

    private static void tomarMensajes(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        if (sesion == null) {
            return;
        }
        Object mensaje = sesion.getAttribute("mensajeUsuario");
        Object error = sesion.getAttribute("errorUsuario");
        if (mensaje != null) {
            request.setAttribute("mensaje", mensaje);
            sesion.removeAttribute("mensajeUsuario");
        }
        if (error != null) {
            request.setAttribute("error", error);
            sesion.removeAttribute("errorUsuario");
        }
    }
}
