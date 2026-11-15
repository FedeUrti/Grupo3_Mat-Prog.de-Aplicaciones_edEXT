package com.grupo3_mat.edEXT.Logica.Manejadores;

import com.grupo3_mat.edEXT.Logica.DataTypes.DtResultadoBusqueda;
import com.grupo3_mat.edEXT.Persistencia.Conexion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ManejadorBusqueda {
    private static final int MAX_LIMITE = 100;

    private ManejadorBusqueda() {
    }

    public static List<DtResultadoBusqueda> buscar(String consulta, String tipo, String nicknameSesion,
            String ordenarPor, String direccion, int limitePorTipo) {
        if (limitePorTipo < 0 || limitePorTipo > MAX_LIMITE) {
            throw new IllegalArgumentException("El límite debe estar entre 0 y " + MAX_LIMITE + ".");
        }
        String termino = consulta == null ? "" : consulta.trim().toLowerCase(Locale.ROOT);
        if (termino.length() > 100) {
            throw new IllegalArgumentException("La búsqueda no puede superar los 100 caracteres.");
        }
        String clase = tipo == null || tipo.isBlank() ? "todos" : tipo.toLowerCase(Locale.ROOT);
        if (!Set.of("todos", "curso", "edicion", "programa", "usuario").contains(clase)) {
            throw new IllegalArgumentException("El tipo de búsqueda no es válido.");
        }
        String orden = ordenarPor == null ? "alfabetico" : ordenarPor.toLowerCase(Locale.ROOT);
        String sentido = direccion == null ? "asc" : direccion.toLowerCase(Locale.ROOT);
        if (!Set.of("alfabetico", "publicacion").contains(orden)
                || !Set.of("asc", "desc").contains(sentido)) {
            throw new IllegalArgumentException("El orden de búsqueda no es válido.");
        }

        String terminoLiteral = escaparLike(termino);
        String patron = "%" + terminoLiteral + "%";
        String prefijo = terminoLiteral + "%";
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            List<DtResultadoBusqueda> resultados = new ArrayList<>();
            if (clase.equals("todos") || clase.equals("curso")) {
                resultados.addAll(buscarCursos(em, termino, patron, prefijo, orden, sentido, limitePorTipo));
                completarValoraciones(em, resultados);
            }
            if (clase.equals("todos") || clase.equals("edicion")) {
                resultados.addAll(buscarEdiciones(em, termino, patron, prefijo, orden, sentido, limitePorTipo));
            }
            if (clase.equals("todos") || clase.equals("programa")) {
                resultados.addAll(buscarProgramas(em, termino, patron, prefijo, orden, sentido, limitePorTipo));
            }
            if (clase.equals("todos") || clase.equals("usuario")) {
                resultados.addAll(buscarUsuarios(em, termino, patron, prefijo, nicknameSesion,
                        orden, sentido, limitePorTipo));
            }
            return resultados;
        } finally {
            em.close();
        }
    }

    private static List<DtResultadoBusqueda> buscarCursos(EntityManager em, String termino, String patron,
            String prefijo, String ordenarPor, String direccion, int limite) {
        String relevancia = "CASE WHEN c.nombre = :termino THEN 0 "
                + "WHEN c.nombre LIKE :prefijo ESCAPE '!' THEN 1 ELSE 2 END";
        String jpql = "SELECT c.nombre, c.descripcion, c.instituto.nombre, c.imagenPath, c.fecha, "
                + relevancia + " FROM Curso c "
                + "WHERE c.nombre LIKE :patron ESCAPE '!' "
                + "OR c.descripcion LIKE :patron ESCAPE '!' "
                + "OR c.instituto.nombre LIKE :patron ESCAPE '!' "
                + "OR EXISTS (SELECT cat FROM Categoria cat JOIN cat.cursos relacionado "
                + "WHERE relacionado = c AND cat.nombre LIKE :patron ESCAPE '!') "
                + orden(relevancia, ordenarPor, direccion, "c.fecha", "c.nombre");
        TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class)
                .setParameter("termino", termino)
                .setParameter("patron", patron)
                .setParameter("prefijo", prefijo);
        limitar(query, limite);

        List<DtResultadoBusqueda> resultados = new ArrayList<>();
        for (Object[] fila : query.getResultList()) {
            DtResultadoBusqueda resultado = nuevo("Curso", (String) fila[0], (String) fila[1],
                    (String) fila[3], fila[4], ((Number) fila[5]).intValue());
            resultado.setInstituto((String) fila[2]);
            resultados.add(resultado);
        }
        return resultados;
    }

    private static List<DtResultadoBusqueda> buscarEdiciones(EntityManager em, String termino, String patron,
            String prefijo, String ordenarPor, String direccion, int limite) {
        String relevancia = "CASE WHEN e.nombre = :termino THEN 0 "
                + "WHEN e.nombre LIKE :prefijo ESCAPE '!' THEN 1 ELSE 2 END";
        String jpql = "SELECT e.nombre, e.curso.nombre, e.fechaInicio, e.fechaFin, "
                + "e.fechaPublicacion, e.imagenPath, " + relevancia + " "
                + "FROM EdicionCurso e WHERE e.nombre LIKE :patron ESCAPE '!' "
                + "OR e.curso.nombre LIKE :patron ESCAPE '!' "
                + orden(relevancia, ordenarPor, direccion, "e.fechaPublicacion", "e.nombre");
        TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class)
                .setParameter("termino", termino)
                .setParameter("patron", patron)
                .setParameter("prefijo", prefijo);
        limitar(query, limite);

        List<DtResultadoBusqueda> resultados = new ArrayList<>();
        for (Object[] fila : query.getResultList()) {
            DtResultadoBusqueda resultado = nuevo("Edición", (String) fila[0], null,
                    (String) fila[5], fila[4], ((Number) fila[6]).intValue());
            resultado.setNombreCurso((String) fila[1]);
            resultado.setFechaInicio(fecha(fila[2]));
            resultado.setFechaFin(fecha(fila[3]));
            resultados.add(resultado);
        }
        return resultados;
    }

    private static List<DtResultadoBusqueda> buscarProgramas(EntityManager em, String termino, String patron,
            String prefijo, String ordenarPor, String direccion, int limite) {
        String relevancia = "CASE WHEN p.nombre = :termino THEN 0 "
                + "WHEN p.nombre LIKE :prefijo ESCAPE '!' THEN 1 ELSE 2 END";
        String jpql = "SELECT p.nombre, p.descripcion, p.imagenPath, p.fechaAlta, "
                + "p.docente.nickname, " + relevancia + " FROM ProgramaFormacion p "
                + "WHERE p.nombre LIKE :patron ESCAPE '!' "
                + "OR p.descripcion LIKE :patron ESCAPE '!' "
                + "OR p.docente.nickname LIKE :patron ESCAPE '!' "
                + "OR EXISTS (SELECT c FROM Curso c JOIN c.programas programa "
                + "WHERE programa = p AND c.nombre LIKE :patron ESCAPE '!') "
                + "OR EXISTS (SELECT c FROM Curso c JOIN c.programas programa JOIN c.categorias cat "
                + "WHERE programa = p AND cat.nombre LIKE :patron ESCAPE '!') "
                + "OR EXISTS (SELECT c FROM Curso c JOIN c.programas programa "
                + "WHERE programa = p AND c.instituto.nombre LIKE :patron ESCAPE '!') "
                + orden(relevancia, ordenarPor, direccion, "p.fechaAlta", "p.nombre");
        TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class)
                .setParameter("termino", termino)
                .setParameter("patron", patron)
                .setParameter("prefijo", prefijo);
        limitar(query, limite);

        List<DtResultadoBusqueda> resultados = new ArrayList<>();
        for (Object[] fila : query.getResultList()) {
            DtResultadoBusqueda resultado = nuevo("Programa", (String) fila[0], (String) fila[1],
                    (String) fila[2], fila[3], ((Number) fila[5]).intValue());
            resultado.setNicknameDocente((String) fila[4]);
            resultados.add(resultado);
        }
        return resultados;
    }

    private static List<DtResultadoBusqueda> buscarUsuarios(EntityManager em, String termino, String patron,
            String prefijo, String nicknameSesion, String ordenarPor, String direccion, int limite) {
        String relevancia = "CASE WHEN u.nickname = :termino THEN 0 "
                + "WHEN u.nickname LIKE :prefijo ESCAPE '!' THEN 1 ELSE 2 END";
        String jpql = "SELECT u.nickname, u.nombre, u.apellido, u.imagenPath, TYPE(u), "
                + relevancia + " FROM Usuario u WHERE u.nickname LIKE :patron ESCAPE '!' "
                + "OR u.nombre LIKE :patron ESCAPE '!' "
                + "OR u.apellido LIKE :patron ESCAPE '!' "
                + "OR u.correo LIKE :patron ESCAPE '!' "
                + "OR EXISTS (SELECT d FROM Docente d JOIN d.institutos i "
                + "WHERE d.nickname = u.nickname AND i.nombre LIKE :patron ESCAPE '!') "
                + orden(relevancia, "alfabetico", direccion, "u.fechaNacimiento", "u.nickname");
        TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class)
                .setParameter("termino", termino)
                .setParameter("patron", patron)
                .setParameter("prefijo", prefijo);
        limitar(query, limite);

        List<Object[]> filas = query.getResultList();
        List<String> nicknames = filas.stream().map(fila -> (String) fila[0]).toList();
        Set<String> seguidos = buscarSeguidos(em, nicknameSesion, nicknames);
        List<DtResultadoBusqueda> resultados = new ArrayList<>(filas.size());
        for (Object[] fila : filas) {
            DtResultadoBusqueda resultado = nuevo("Usuario", (String) fila[1], null,
                    (String) fila[3], null, ((Number) fila[5]).intValue());
            resultado.setNickname((String) fila[0]);
            resultado.setApellido((String) fila[2]);
            resultado.setRol(fila[4] instanceof Class<?> tipoUsuario
                    && tipoUsuario.getSimpleName().equals("Docente") ? "Docente" : "Estudiante");
            resultado.setSiguiendo(seguidos.contains(resultado.getNickname()));
            resultado.setPropio(resultado.getNickname().equals(nicknameSesion));
            resultados.add(resultado);
        }
        return resultados;
    }

    private static Set<String> buscarSeguidos(EntityManager em, String nicknameSesion, List<String> nicknames) {
        if (nicknameSesion == null || nicknameSesion.isBlank() || nicknames.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(em.createQuery(
                "SELECT seguido.nickname FROM Usuario seguidor JOIN seguidor.seguidos seguido "
                + "WHERE seguidor.nickname = :nickname AND seguido.nickname IN :nicknames", String.class)
                .setParameter("nickname", nicknameSesion)
                .setParameter("nicknames", nicknames)
                .getResultList());
    }

    private static void completarValoraciones(EntityManager em, List<DtResultadoBusqueda> cursos) {
        if (cursos.isEmpty()) {
            return;
        }
        Map<String, DtResultadoBusqueda> porNombre = cursos.stream()
                .collect(Collectors.toMap(DtResultadoBusqueda::getNombre, Function.identity()));
        List<String> nombres = new ArrayList<>(porNombre.keySet());
        for (int inicio = 0; inicio < nombres.size(); inicio += MAX_LIMITE) {
            List<String> lote = nombres.subList(inicio, Math.min(inicio + MAX_LIMITE, nombres.size()));
            List<Object[]> valoraciones = em.createQuery(
                    "SELECT v.curso.nombre, AVG(v.puntaje), COUNT(v) FROM ValoracionCurso v "
                    + "WHERE v.curso.nombre IN :nombres GROUP BY v.curso.nombre", Object[].class)
                    .setParameter("nombres", lote)
                    .getResultList();
            for (Object[] fila : valoraciones) {
                DtResultadoBusqueda curso = porNombre.get((String) fila[0]);
                Number promedio = (Number) fila[1];
                curso.setPromedioValoracion(promedio == null ? BigDecimal.ZERO.setScale(2)
                        : BigDecimal.valueOf(promedio.doubleValue()).setScale(2, RoundingMode.HALF_UP));
                curso.setCantidadValoraciones(((Number) fila[2]).longValue());
            }
        }
    }

    private static String orden(String relevancia, String ordenarPor, String direccion,
            String campoFecha, String campoNombre) {
        StringBuilder resultado = new StringBuilder(" ORDER BY ").append(relevancia);
        if ("publicacion".equals(ordenarPor)) {
            resultado.append(", CASE WHEN ").append(campoFecha)
                    .append(" IS NULL THEN 1 ELSE 0 END ASC, ").append(campoFecha).append(' ')
                    .append(direccion.equals("desc") ? "DESC" : "ASC");
        }
        return resultado.append(", ").append(campoNombre).append(' ')
                .append(direccion.equals("desc") ? "DESC" : "ASC").toString();
    }

    private static void limitar(TypedQuery<?> query, int limite) {
        if (limite > 0) {
            query.setMaxResults(limite);
        }
    }

    private static DtResultadoBusqueda nuevo(String tipo, String nombre, String descripcion,
            String imagenPath, Object fechaPublicacion, int relevancia) {
        DtResultadoBusqueda resultado = new DtResultadoBusqueda();
        resultado.setTipo(tipo);
        resultado.setNombre(nombre);
        resultado.setDescripcion(descripcion);
        resultado.setImagenPath(imagenPath);
        resultado.setFechaPublicacion(fecha(fechaPublicacion));
        resultado.setRelevancia(relevancia);
        return resultado;
    }

    private static String fecha(Object valor) {
        return valor instanceof LocalDate fecha ? fecha.toString() : null;
    }

    private static String escaparLike(String valor) {
        return valor.replace("!", "!!").replace("%", "!%").replace("_", "!_");
    }
}
