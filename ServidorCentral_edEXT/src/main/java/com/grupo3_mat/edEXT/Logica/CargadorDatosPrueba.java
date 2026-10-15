package com.grupo3_mat.edEXT.Logica;

import com.grupo3_mat.edEXT.Logica.Clases.EstadoInscripcion;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTEdicionCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCategoria;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorEdicion;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorInstituto;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorProgramaFormacion;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorUsuario;
import com.grupo3_mat.edEXT.Persistencia.Conexion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.time.LocalDate;
import java.util.List;

public class CargadorDatosPrueba {

    private static final String PASSWORD_PRUEBA = "1234";
    private static final String IMAGEN_ESTUDIANTE = "/Presentacion/Recursos/estudiante_placeholder.png";
    private static final String IMAGEN_DOCENTE = "/Presentacion/Recursos/docente_placeholder.png";
    private static final String IMAGEN_CURSO = "/Presentacion/Recursos/imagen_predeterminada_curso_1.png";
    private static final String IMAGEN_EDICION = "/Presentacion/Recursos/imagen_predeterminada_edCurso_1.png";
    private static final String IMAGEN_PROGRAMA = "/Presentacion/Recursos/imagen_predeterminada_progForm_1.png";

    public void cargarDatosTotales() throws Exception {
        Fabrica fabrica = Fabrica.getInstance();
        IControladorInstituto institutos = fabrica.getIControladorInstituto();
        IControladorCategoria categorias = fabrica.getIControladorCategoria();
        IControladorUsuario usuarios = fabrica.getIControladorUsuario();
        IControladorCurso cursos = fabrica.getIControladorCurso();
        IControladorEdicion ediciones = fabrica.getIControladorEdicion();
        IControladorProgramaFormacion programas = fabrica.getIControladorProgramaFormacion();
        String fase = "limpieza de los datos anteriores";

        try {
            limpiarDatosEdext();
            fase = "institutos";
            cargarInstitutos(institutos);
            fase = "categorías";
            cargarCategorias(categorias);
            fase = "usuarios";
            cargarUsuarios(usuarios);
            fase = "seguimientos";
            cargarSeguimientos(usuarios);
            fase = "cursos";
            cargarCursos(cursos);
            fase = "ediciones";
            cargarEdiciones(ediciones);
            fase = "inscripciones a ediciones";
            cargarInscripcionesEdiciones(ediciones);
            fase = "programas de formación";
            cargarProgramas(programas);
            fase = "inscripciones a programas";
            cargarInscripcionesProgramas(programas);
            System.out.println("Datos de prueba 2026 cargados correctamente.");
        } catch (Exception e) {
            throw new Exception("Error al cargar " + fase + ": " + e.getMessage(), e);
        }
    }

    public static void main(String[] args) throws Exception {
        new CargadorDatosPrueba().cargarDatosTotales();
    }

    private void limpiarDatosEdext() {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        EntityTransaction transaccion = em.getTransaction();
        List<String> tablasEnOrden = List.of(
                "Usuario_Seguimiento", "Docente_Instituto", "Docente_Participa_EdicionCurso",
                "Curso_Categoria", "Curso_Precorrelativa", "programa_curso", "InscripcionEC",
                "InscripcionPF", "EdicionCurso", "ProgramaFormacion", "Estudiante", "Docente",
                "Usuario", "Curso", "Categoria", "Instituto");
        try {
            transaccion.begin();
            for (String tabla : tablasEnOrden) {
                em.createNativeQuery("DELETE FROM " + tabla).executeUpdate();
            }
            transaccion.commit();
        } catch (RuntimeException e) {
            if (transaccion.isActive()) {
                transaccion.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    private void cargarInstitutos(IControladorInstituto controlador) {
        for (String nombre : List.of("INCO", "IMERL", "Física", "IMPII", "Eléctrica", "DISI")) {
            controlador.altaInstituto(nombre);
        }
    }

    private void cargarCategorias(IControladorCategoria controlador) throws Exception {
        for (String nombre : List.of("Social", "Industrial", "Educativo", "Interdisciplinario")) {
            controlador.altaCategoria(nombre);
        }
    }

    private void cargarUsuarios(IControladorUsuario controlador) throws Exception {
        controlador.altaUsuario("eleven11", "Eleven", "Twelve", "eleven11@gmail.com",
                LocalDate.of(1971, 12, 31), IMAGEN_ESTUDIANTE, null, PASSWORD_PRUEBA);
        controlador.altaUsuario("costas", "Gerardo", "Costas", "gcostas@gmail.com",
                LocalDate.of(1983, 11, 15), IMAGEN_ESTUDIANTE, null, PASSWORD_PRUEBA);
        controlador.altaUsuario("roro", "Rodrigo", "Cotelo", "rcotelo@yahoo.com",
                LocalDate.of(1975, 8, 2), IMAGEN_ESTUDIANTE, null, PASSWORD_PRUEBA);
        controlador.altaUsuario("chechi", "Cecilia", "Garrido", "cgarrido@hotmail.com",
                LocalDate.of(1987, 9, 12), IMAGEN_ESTUDIANTE, null, PASSWORD_PRUEBA);
        controlador.altaUsuario("jeffw", "Jeff", "Williams", "jwilliams@gmail.com",
                LocalDate.of(1964, 11, 27), IMAGEN_ESTUDIANTE, null, PASSWORD_PRUEBA);
        controlador.altaUsuario("weiss", "Adrian", "Weiss", "aweiss@hotmail.com",
                LocalDate.of(1978, 12, 23), IMAGEN_ESTUDIANTE, null, PASSWORD_PRUEBA);
        altaDocente(controlador, "heisenberg", "Walter", "White", "heisenberg@gmail.com", LocalDate.of(1956, 3, 7), "INCO");
        altaDocente(controlador, "benkenobi", "Obi-Wan", "Kenobi", "benKenobi@gmail.com", LocalDate.of(1914, 4, 2), "INCO");
        altaDocente(controlador, "waston", "Emma", "Watson", "e.watson@gmail.com", LocalDate.of(1990, 4, 15), "INCO");
        altaDocente(controlador, "house", "Gregory", "House", "greghouse@gmail.com", LocalDate.of(1959, 5, 15), "Eléctrica");
        altaDocente(controlador, "timmy", "Tim", "Cook", "tim.cook@apple.com", LocalDate.of(1960, 11, 1), "IMERL");
        altaDocente(controlador, "danny", "Daniel", "Riccio", "dan.riccio@gmail.com", LocalDate.of(1963, 7, 5), "IMERL");
        altaDocente(controlador, "phils", "Philip", "Schiller", "schiller@gmail.com", LocalDate.of(1961, 10, 7), "IMPII");
        altaDocente(controlador, "bruces", "Bruce", "Sewell", "sewell@gmail.com", LocalDate.of(1959, 12, 3), "DISI");
        altaDocente(controlador, "adri", "Adriana", "García", "agarcia@gmail.com", LocalDate.of(1978, 7, 28), "DISI");
    }

    private void altaDocente(IControladorUsuario controlador, String nickname, String nombre, String apellido,
            String correo, LocalDate nacimiento, String instituto) throws Exception {
        controlador.altaUsuarioConInstitutos(nickname, nombre, apellido, correo, nacimiento,
                IMAGEN_DOCENTE, List.of(instituto), PASSWORD_PRUEBA);
    }

    private void cargarSeguimientos(IControladorUsuario controlador) throws Exception {
        seguir(controlador, "eleven11", "roro", "heisenberg", "waston");
        seguir(controlador, "costas", "chechi", "jeffw", "benkenobi", "danny", "phils", "adri");
        seguir(controlador, "roro", "costas", "weiss");
        seguir(controlador, "chechi", "eleven11", "jeffw", "house");
        seguir(controlador, "jeffw", "eleven11");
        seguir(controlador, "weiss", "costas", "heisenberg", "timmy");
        seguir(controlador, "heisenberg", "roro");
        seguir(controlador, "benkenobi", "danny");
        seguir(controlador, "waston", "roro", "weiss", "heisenberg");
        seguir(controlador, "house", "jeffw", "weiss", "benkenobi");
        seguir(controlador, "timmy", "eleven11", "chechi", "jeffw");
        seguir(controlador, "danny", "costas", "weiss", "heisenberg");
        seguir(controlador, "phils", "bruces");
        seguir(controlador, "bruces", "phils");
        seguir(controlador, "adri", "heisenberg");
    }

    private void seguir(IControladorUsuario controlador, String seguidor, String... seguidos) throws Exception {
        for (String seguido : seguidos) {
            controlador.seguirUsuario(seguidor, seguido);
        }
    }

    private void cargarCursos(IControladorCurso controlador) throws Exception {
        String c1 = "Talleres plenarios";
        String c2 = "Seminarios de Resolución de Problemas";
        String c3 = "Dalavuelta";
        String c4 = "Extensionismo Industrial";
        String c5 = "Inclusión Energética";
        String c6 = "Flor del Ceibo";
        String c7 = "Taller de robótica educativa";
        String c8 = "Participación en investigación sobre el empleo del juego Komikan";
        String c9 = "Herramientas de apoyo a la enseñanza de inglés";
        String c10 = "MicroBit";

        controlador.altaCurso("IMERL", c1,
                "Talleres plenarios: presentados por cuatro reconocidos matemáticos uruguayos, plantearán diversos "
                + "tópicos de matemática en el marco de los cuales se realizarán actividades fomentando la integración "
                + "entre estudiantes, docentes e investigadores.", 3, 15, 1, "www.tmu.edu.uy",
                LocalDate.of(2026, 2, 1), List.of(), List.of(), IMAGEN_CURSO);
        controlador.altaCurso("IMERL", c2,
                "Seminario, todos los jueves en Facultad de Ingeniería a partir del jueves 25 de Julio, en las áreas "
                + "en que se desarrollan los problemas de las Olimpíadas de Matemática.", 5, 30, 2, "www.tmu.edu.uy",
                LocalDate.of(2026, 7, 12), List.of(c1), List.of(), IMAGEN_CURSO);
        controlador.altaCurso("IMPII", c3,
                "Dalavuelta es un proyecto de extensión que nace en el Instituto de Ingeniería Mecánica y Producción "
                + "Industrial (IIMPI) de Fing, que, si bien inicia su trabajo en el desarrollo de bicicletas accesibles "
                + "para personas en situación de discapacidad motriz a partir de bicicletas abandonadas, se propuso "
                + "diseñar otras herramientas para fomentar la accesibilidad.", 10, 60, 4,
                "https://eva.fing.edu.uy/course/view.php?id=783#section-2", LocalDate.of(2024, 6, 25),
                List.of(c1), List.of("Social"), IMAGEN_CURSO);
        controlador.altaCurso("IMPII", c4,
                "El proyecto tiene como objetivo desarrollar intervenciones curriculares en pequeños emprendimientos "
                + "productivos de diferentes sectores de la industria nacional. La metodología de trabajo permite "
                + "articular diversas intervenciones, combinando actividades de enseñanza, extensión e investigación "
                + "por parte de docentes del IMPII.", 12, 75, 5,
                "https://eva.fing.edu.uy/course/view.php?id=783#section-2", LocalDate.of(2025, 6, 16),
                List.of(c1), List.of("Industrial"), IMAGEN_CURSO);
        controlador.altaCurso("IMPII", c5,
                "En el proyecto se conjuga el trabajo de docentes y estudiantes de la carrera Ingeniería Industrial "
                + "Mecánica a través del Módulo de Extensión, en donde se trabaja en el diseño, construcción y prueba "
                + "de un prototipo de colector solar adquiriendo conocimientos relevantes para luego poder replicarlos "
                + "junto a las familias en los talleres. Las premisas fundamentales a la hora de pensar los diseños "
                + "son: por un lado el bajo costo de los materiales y por otro la fácil construcción de forma de poder "
                + "construirlos ellos mismos.", 6, 45, 3,
                "https://eva.fing.edu.uy/course/view.php?id=783#section-2", LocalDate.of(2026, 2, 1),
                List.of(), List.of("Industrial"), IMAGEN_CURSO);
        controlador.altaCurso("DISI", c6,
                "Flor de Ceibo es un proyecto central de la Universidad de la República, que tiene misión por "
                + "movilizar la participación de estudiantes universitarios en diversas tareas vinculadas con la puesta "
                + "en funcionamiento del Plan Ceibal en el territorio nacional.", 15, 150, 10,
                "http://www.flordeceibo.edu.uy/", LocalDate.of(2008, 7, 27), List.of(),
                List.of("Educativo", "Interdisciplinario"), IMAGEN_CURSO);
        controlador.altaCurso("INCO", c7,
                "La asignatura se organiza en dos etapas. La primer etapa se dicta a través de clases "
                + "teóricoprácticas, donde se espera además que cada estudiante le dedique horas de estudio. La segunda "
                + "etapa consiste en que los estudiantes trabajen en grupo sobre el diseño e implementación de una "
                + "experiencia didáctica de inclusión del robot Butiá en el aula, utilizando los conocimientos "
                + "aprendidos en clase.", 8, 90, 6,
                "https://eva.fing.edu.uy/course/view.php?id=1187", LocalDate.of(2024, 2, 2), List.of(),
                List.of("Social", "Educativo"), IMAGEN_CURSO);
        controlador.altaCurso("INCO", c8,
                "Se propone desarrollar una aplicación interactiva para tablet Android basada en el juego de tablero "
            + "Komikan (versión web del juego https://codepen.io/Borborem/full/OvZBvZ/), que incluya los "
            + "distintos aspectos concernientes al juego, así como a situaciones específicas particulares.", 9, 45, 3,
                "https://eva.fing.edu.uy/mod/folder/view.php?id=89398", LocalDate.of(2026, 6, 15), List.of(),
                List.of("Educativo"), IMAGEN_CURSO);
        controlador.altaCurso("INCO", c9,
                "Se realizarán visitas a escuelas rurales participantes en un proyecto conjunto del grupo PLN y el "
                + "Programa de Políticas Lingüísticas de ANEP, en el marco del cual se desarrollaron diferentes "
                + "herramientas para uso de maestros que enseñan inglés con apoyo remoto de profesores especializados "
                + "desde Montevideo.", 12, 60, 4,
                "https://eva.fing.edu.uy/mod/folder/view.php?id=89398", LocalDate.of(2026, 5, 24), List.of(),
                List.of("Social", "Educativo"), IMAGEN_CURSO);
        controlador.altaCurso("Eléctrica", c10,
            "El Centro Ceibal se encuentra distribuyendo placas micro:bit "
            + "(https://microbit.ceibal.edu.uy/) para que estudiantes de primaria y "
                + "secundaria aprendan nociones básicas de robótica, electrónica y programación de forma autónoma y "
                + "lúdica. Estas placas se basan en un microcontrolador y cuentan con leds, botones, acelerómetro, "
                + "brújula, bluetooth y otros sensores. Además, las placas se programan fácilmente con lenguaje tipo "
                + "scratch y python, por lo que son muy útiles para un primer acercamiento a la temática.", 15, 105, 7,
                "https://www.fing.edu.uy/noticias/extension/modulo-de-tallerextension-microbit",
                LocalDate.of(2026, 3, 13), List.of(), List.of("Industrial", "Educativo"), IMAGEN_CURSO);
    }

    private void cargarEdiciones(IControladorEdicion controlador) throws Exception {
        altaEdicion(controlador, "Flor del Ceibo", "Flor del Ceibo - 2010", "2010-03-15", "2010-07-07", -1, "2010-02-16", "bruces");
        altaEdicion(controlador, "Flor del Ceibo", "Flor del Ceibo - 2012", "2012-08-01", "2012-11-20", -1, "2012-07-10", "bruces", "adri");
        altaEdicion(controlador, "Flor del Ceibo", "Flor del Ceibo - 2025", "2025-04-10", "2025-08-07", -1, "2025-03-06", "bruces", "adri");
        altaEdicion(controlador, "Dalavuelta", "Dalavuelta - 2025", "2024-08-20", "2024-11-10", 15, "2024-07-20", "phils");
        altaEdicion(controlador, "Extensionismo Industrial", "Extensionismo Industrial - 2025", "2025-08-10", "2025-11-10", 15, "2025-07-08", "phils");
        altaEdicion(controlador, "Inclusión Energética", "Inclusión Energética - 2026", "2026-03-15", "2026-04-30", 30, "2026-02-20", "phils");
        altaEdicion(controlador, "Taller de robótica educativa", "Taller de robótica educativa - 2024", "2024-03-10", "2024-05-10", 10, "2024-02-15", "heisenberg");
        altaEdicion(controlador, "Taller de robótica educativa", "Taller de robótica educativa - 2026", "2026-03-10", "2026-05-10", 10, "2026-02-15", "heisenberg", "benkenobi");
        altaEdicion(controlador, "Taller de robótica educativa", "Taller de robótica educativa - 2026-2", "2026-09-10", "2026-11-08", 20, "2026-08-15", "benkenobi", "waston");
        altaEdicion(controlador, "Participación en investigación sobre el empleo del juego Komikan", "Participación en investigación sobre el empleo del juego Komikan - 2026", "2026-07-29", "2026-10-07", 5, "2026-07-10", "waston");
        altaEdicion(controlador, "Herramientas de apoyo a la enseñanza de inglés", "Herramientas de apoyo a la enseñanza de inglés - 26", "2026-09-15", "2026-12-15", 5, "2026-06-02", "heisenberg");
        altaEdicion(controlador, "MicroBit", "MicroBit - 2026", "2026-08-12", "2026-12-05", 30, "2026-07-02", "house");
        altaEdicion(controlador, "Talleres plenarios", "Talleres plenarios - 2026", "2026-03-10", "2026-03-30", -1, "2026-03-02", "timmy", "danny");
        altaEdicion(controlador, "Seminarios de Resolución de Problemas", "Seminarios de Resolución de Problemas - 2026", "2026-09-10", "2026-10-20", -1, "2026-07-12", "timmy");
    }

    private void altaEdicion(IControladorEdicion controlador, String curso, String nombre, String inicio,
            String fin, int cupo, String publicacion, String... docentes) throws Exception {
        controlador.altaEdicionCurso(curso, new DTEdicionCurso(nombre, LocalDate.parse(inicio), LocalDate.parse(fin),
                cupo, cupo, LocalDate.parse(publicacion), List.of(docentes), IMAGEN_EDICION));
    }

    private void cargarInscripcionesEdiciones(IControladorEdicion controlador) throws Exception {
        inscribir(controlador, "eleven11", "Flor del Ceibo - 2010", "2017-02-20", EstadoInscripcion.ACEPTADA, "bruces");
        inscribir(controlador, "chechi", "Flor del Ceibo - 2010", "2017-02-25", EstadoInscripcion.ACEPTADA, "bruces");
        inscribir(controlador, "costas", "Flor del Ceibo - 2012", "2019-07-12", EstadoInscripcion.ACEPTADA, "bruces");
        inscribir(controlador, "roro", "Flor del Ceibo - 2012", "2019-07-15", EstadoInscripcion.RECHAZADA, "bruces");
        inscribir(controlador, "weiss", "Flor del Ceibo - 2012", "2019-07-30", EstadoInscripcion.ACEPTADA, "bruces");
        inscribir(controlador, "roro", "Flor del Ceibo - 2025", "2025-03-10", EstadoInscripcion.ACEPTADA, "bruces");
        inscribir(controlador, "jeffw", "Flor del Ceibo - 2025", "2025-03-15", EstadoInscripcion.ACEPTADA, "bruces");
        inscribir(controlador, "chechi", "Dalavuelta - 2025", "2024-07-25", EstadoInscripcion.ACEPTADA, "phils");
        inscribir(controlador, "eleven11", "Dalavuelta - 2025", "2024-07-28", EstadoInscripcion.RECHAZADA, "phils");
        inscribir(controlador, "roro", "Dalavuelta - 2025", "2024-08-02", EstadoInscripcion.ACEPTADA, "phils");
        inscribir(controlador, "costas", "Dalavuelta - 2025", "2024-08-10", EstadoInscripcion.ACEPTADA, "phils");
        inscribir(controlador, "jeffw", "Dalavuelta - 2025", "2024-08-15", EstadoInscripcion.RECHAZADA, "phils");
        inscribir(controlador, "costas", "Extensionismo Industrial - 2025", "2025-07-18", EstadoInscripcion.ACEPTADA, "phils");
        inscribir(controlador, "chechi", "Extensionismo Industrial - 2025", "2025-07-20", EstadoInscripcion.ACEPTADA, "phils");
        inscribir(controlador, "eleven11", "Extensionismo Industrial - 2025", "2025-07-29", EstadoInscripcion.ACEPTADA, "phils");
        inscribir(controlador, "weiss", "Extensionismo Industrial - 2025", "2025-08-05", EstadoInscripcion.ACEPTADA, "phils");
        inscribir(controlador, "roro", "Inclusión Energética - 2026", "2026-02-23", EstadoInscripcion.ACEPTADA, "phils");
        inscribir(controlador, "weiss", "Inclusión Energética - 2026", "2026-02-25", EstadoInscripcion.RECHAZADA, "phils");
        inscribir(controlador, "chechi", "Inclusión Energética - 2026", "2026-02-28", EstadoInscripcion.ACEPTADA, "phils");
        inscribir(controlador, "eleven11", "Inclusión Energética - 2026", "2026-03-03", EstadoInscripcion.RECHAZADA, "phils");
        inscribir(controlador, "weiss", "Taller de robótica educativa - 2024", "2017-02-18", EstadoInscripcion.RECHAZADA, "heisenberg");
        inscribir(controlador, "roro", "Taller de robótica educativa - 2024", "2024-02-20", EstadoInscripcion.RECHAZADA, "heisenberg");
        inscribir(controlador, "eleven11", "Taller de robótica educativa - 2024", "2024-03-03", EstadoInscripcion.ACEPTADA, "heisenberg");
        inscribir(controlador, "chechi", "Taller de robótica educativa - 2024", "2024-03-05", EstadoInscripcion.ACEPTADA, "heisenberg");
        inscribir(controlador, "jeffw", "Taller de robótica educativa - 2026", "2026-02-18", EstadoInscripcion.ACEPTADA, "heisenberg");
        inscribir(controlador, "costas", "Taller de robótica educativa - 2026", "2026-02-22", EstadoInscripcion.ACEPTADA, "heisenberg");
        inscribir(controlador, "roro", "Taller de robótica educativa - 2026", "2026-02-23", EstadoInscripcion.RECHAZADA, "heisenberg");
        inscribir(controlador, "weiss", "Taller de robótica educativa - 2026-2", "2026-08-18", EstadoInscripcion.INSCRIPTO, "benkenobi");
        inscribir(controlador, "chechi", "Taller de robótica educativa - 2026-2", "2026-08-22", EstadoInscripcion.INSCRIPTO, "benkenobi");
        inscribir(controlador, "roro", "Taller de robótica educativa - 2026-2", "2026-09-03", EstadoInscripcion.INSCRIPTO, "benkenobi");
        inscribir(controlador, "chechi", "Participación en investigación sobre el empleo del juego Komikan - 2026", "2026-07-13", EstadoInscripcion.ACEPTADA, "waston");
        inscribir(controlador, "weiss", "Participación en investigación sobre el empleo del juego Komikan - 2026", "2026-07-20", EstadoInscripcion.ACEPTADA, "waston");
        inscribir(controlador, "roro", "Participación en investigación sobre el empleo del juego Komikan - 2026", "2026-07-22", EstadoInscripcion.ACEPTADA, "waston");
        inscribir(controlador, "weiss", "Herramientas de apoyo a la enseñanza de inglés - 26", "2026-06-04", EstadoInscripcion.INSCRIPTO, "heisenberg");
        inscribir(controlador, "eleven11", "Herramientas de apoyo a la enseñanza de inglés - 26", "2026-07-18", EstadoInscripcion.INSCRIPTO, "heisenberg");
        inscribir(controlador, "jeffw", "Herramientas de apoyo a la enseñanza de inglés - 26", "2026-08-20", EstadoInscripcion.INSCRIPTO, "heisenberg");
        inscribir(controlador, "chechi", "MicroBit - 2026", "2026-07-12", EstadoInscripcion.INSCRIPTO, "house");
        inscribir(controlador, "roro", "MicroBit - 2026", "2026-07-14", EstadoInscripcion.INSCRIPTO, "house");
        inscribir(controlador, "eleven11", "MicroBit - 2026", "2026-07-25", EstadoInscripcion.INSCRIPTO, "house");
        inscribir(controlador, "jeffw", "MicroBit - 2026", "2026-08-05", EstadoInscripcion.INSCRIPTO, "house");
        inscribir(controlador, "costas", "Talleres plenarios - 2026", "2026-03-05", EstadoInscripcion.RECHAZADA, "timmy");
        inscribir(controlador, "weiss", "Talleres plenarios - 2026", "2026-03-04", EstadoInscripcion.ACEPTADA, "timmy");
        inscribir(controlador, "roro", "Talleres plenarios - 2026", "2026-03-07", EstadoInscripcion.ACEPTADA, "timmy");
        inscribir(controlador, "weiss", "Seminarios de Resolución de Problemas - 2026", "2026-07-15", EstadoInscripcion.ACEPTADA, "timmy");
        inscribir(controlador, "costas", "Seminarios de Resolución de Problemas - 2026", "2026-07-20", EstadoInscripcion.RECHAZADA, "timmy");
        inscribir(controlador, "roro", "Seminarios de Resolución de Problemas - 2026", "2026-08-06", EstadoInscripcion.ACEPTADA, "timmy");
        inscribir(controlador, "chechi", "Seminarios de Resolución de Problemas - 2026", "2026-08-30", EstadoInscripcion.RECHAZADA, "timmy");
    }

    private void inscribir(IControladorEdicion controlador, String estudiante, String edicion, String fecha,
            EstadoInscripcion estado, String docente) throws Exception {
        controlador.inscribirEstudianteAEdicion(estudiante, edicion, LocalDate.parse(fecha));
        if (estado != EstadoInscripcion.INSCRIPTO) {
            controlador.cambiarEstadoInscripcion(docente, edicion, estudiante, estado);
        }
    }

    private void cargarProgramas(IControladorProgramaFormacion controlador) throws Exception {
        LocalDate fechaAlta = LocalDate.of(2026, 1, 1);
        controlador.crearProgramaFormacion("phils", "EFI Ingeniería Mecánica", "Programa mecánica",
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 10, 31), fechaAlta, IMAGEN_PROGRAMA);
        controlador.crearProgramaFormacion("timmy", "Formación integral", "Programa varios institutos",
                LocalDate.of(2026, 7, 15), LocalDate.of(2027, 1, 1), fechaAlta, IMAGEN_PROGRAMA);
        controlador.crearProgramaFormacion("adri", "EFI Robótica", "Programa robótica",
                LocalDate.of(2026, 9, 3), LocalDate.of(2026, 11, 18), fechaAlta, IMAGEN_PROGRAMA);
        agregarCursos(controlador, "EFI Ingeniería Mecánica",
                "Dalavuelta", "Extensionismo Industrial", "Inclusión Energética");
        agregarCursos(controlador, "Formación integral",
                "Seminarios de Resolución de Problemas", "Extensionismo Industrial", "Flor del Ceibo",
                "Participación en investigación sobre el empleo del juego Komikan");
        agregarCursos(controlador, "EFI Robótica", "Taller de robótica educativa", "MicroBit");
    }

        private void agregarCursos(IControladorProgramaFormacion controlador, String programa, String... cursos)
            throws Exception {
        for (String curso : cursos) {
            controlador.agregarCursoAPrograma(programa, curso);
        }
    }

    private void cargarInscripcionesProgramas(IControladorProgramaFormacion controlador) throws Exception {
        inscribirPrograma(controlador, "eleven11", "EFI Ingeniería Mecánica", "2026-04-15");
        inscribirPrograma(controlador, "chechi", "EFI Ingeniería Mecánica", "2026-04-20");
        inscribirPrograma(controlador, "costas", "Formación integral", "2026-07-10");
        inscribirPrograma(controlador, "chechi", "Formación integral", "2026-07-10");
        inscribirPrograma(controlador, "eleven11", "Formación integral", "2026-07-12");
        inscribirPrograma(controlador, "weiss", "Formación integral", "2026-07-15");
        inscribirPrograma(controlador, "roro", "Formación integral", "2026-07-18");
        inscribirPrograma(controlador, "costas", "EFI Robótica", "2026-08-11");
        inscribirPrograma(controlador, "chechi", "EFI Robótica", "2026-08-15");
        inscribirPrograma(controlador, "roro", "EFI Robótica", "2026-08-22");
        inscribirPrograma(controlador, "jeffw", "EFI Robótica", "2026-08-30");
    }

    private void inscribirPrograma(IControladorProgramaFormacion controlador, String estudiante,
            String programa, String fecha) throws Exception {
        controlador.inscribirEstudianteAPrograma(estudiante, programa, LocalDate.parse(fecha));
    }
}
