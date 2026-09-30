package com.grupo3_mat.edEXT.Logica;

import com.grupo3_mat.edEXT.Logica.DataTypes.DTEdicionCurso;
import com.grupo3_mat.edEXT.Logica.Fabrica;
import com.grupo3_mat.edEXT.Logica.Interfaces.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CargadorDatosPrueba {
    
    public void cargarDatosTotales() {
        Fabrica fabrica = Fabrica.getInstance();
        IControladorInstituto ctrlInst = fabrica.getIControladorInstituto();
        IControladorUsuario ctrlUsu = fabrica.getIControladorUsuario();
        IControladorCurso ctrlCurso = fabrica.getIControladorCurso();
        IControladorEdicion ctrlEdi = fabrica.getIControladorEdicion();
        IControladorProgramaFormacion ctrlProg = fabrica.getIControladorProgramaFormacion();
        IControladorCategoria ctrlCat = fabrica.getIControladorCategoria();
        
        try {
            System.out.println("Iniciando carga masiva de datos...");
            cargarInstitutos(ctrlInst);
            cargarCategorias(ctrlCat);
            cargarUsuarios(ctrlUsu);
            cargarSeguimientos(ctrlUsu);
            cargarCursos(ctrlCurso);
            cargarEdiciones(ctrlEdi);
            cargarInscripciones(ctrlEdi);
            cargarProgramas(ctrlProg);
            cargarInscripcionesPF(ctrlProg);
            System.out.println("¡Datos de prueba cargados con éxito!");
        } catch (Exception e) {
            System.err.println("Error en la carga: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarInstitutos(IControladorInstituto ctrl) {
        ctrl.altaInstituto("INCO");
        ctrl.altaInstituto("IMERL");
        ctrl.altaInstituto("Física");
        ctrl.altaInstituto("IMPII");
        ctrl.altaInstituto("Eléctrica");
        ctrl.altaInstituto("DISI");
    }

        private void cargarCategorias(IControladorCategoria ctrl) throws Exception {
                ctrl.altaCategoria("Accesibilidad");
                ctrl.altaCategoria("Educación");
                ctrl.altaCategoria("Energía");
                ctrl.altaCategoria("Extensión");
                ctrl.altaCategoria("Industria");
                ctrl.altaCategoria("Inclusión");
                ctrl.altaCategoria("Investigación");
                ctrl.altaCategoria("Matemática");
                ctrl.altaCategoria("Robótica");
                ctrl.altaCategoria("Tecnología");
        }

    private void cargarUsuarios(IControladorUsuario ctrl) throws Exception {
        // Estudiantes
        ctrl.altaUsuario("eleven11", "Eleven", "Twelve", "eleven11@gmail.com", LocalDate.of(1971, 12, 31), "", null, "Pepe1");
        ctrl.altaUsuario("costas", "Gerardo", "Costas", "gcostas@gmail.com", LocalDate.of(1983, 11, 15), "", null, "Pepe1");
        ctrl.altaUsuario("roro", "Rodrigo", "Cotelo", "rcotelo@yahoo.com", LocalDate.of(1975, 8, 2), "", null, "Pepe1");
        ctrl.altaUsuario("chechi", "Cecilia", "Garrido", "cgarrido@hotmail.com", LocalDate.of(1987, 9, 12), "", null, "Pepe1");
        ctrl.altaUsuario("jeffw", "Jeff", "Williams", "jwilliams@gmail.com", LocalDate.of(1964, 11, 27), "", null, "Pepe1");
        ctrl.altaUsuario("weiss", "Adrian", "Weiss", "aweiss@hotmail.com", LocalDate.of(1978, 12, 23), "", null, "Pepe1");

        // Docentes
        ctrl.altaUsuarioConInstitutos("heisenberg", "Walter", "White", "heisenberg@gmail.com", LocalDate.of(1956, 3, 7), "", List.of("INCO", "IMERL", "Física"), "Pepe1");
        ctrl.altaUsuarioConInstitutos("benkenobi", "Obi-Wan", "Kenobi", "benKenobi@gmail.com", LocalDate.of(1914, 4, 2), "", List.of("INCO", "DISI"), "Pepe1");
        ctrl.altaUsuarioConInstitutos("waston", "Emma", "Watson", "e.watson@gmail.com", LocalDate.of(1990, 4, 15), "", List.of("INCO", "IMPII", "Eléctrica"), "Pepe1");
        ctrl.altaUsuarioConInstitutos("house", "Gregory", "House", "greghouse@gmail.com", LocalDate.of(1959, 5, 15), "", List.of("Eléctrica", "IMPII"), "Pepe1");
        ctrl.altaUsuarioConInstitutos("timmy", "Tim", "Cook", "tim.cook@apple.com", LocalDate.of(1960, 11, 1), "", List.of("IMERL", "INCO", "DISI"), "Pepe1");
        ctrl.altaUsuarioConInstitutos("danny", "Daniel", "Riccio", "dan.riccio@gmail.com", LocalDate.of(1963, 7, 5), "", List.of("IMERL", "Física"), "Pepe1");
        ctrl.altaUsuarioConInstitutos("phils", "Philip", "Schiller", "schiller@gmail.com", LocalDate.of(1961, 10, 7), "", List.of("IMPII", "INCO"), "Pepe1");
        ctrl.altaUsuarioConInstitutos("bruces", "Bruce", "Sewell", "sewell@gmail.com", LocalDate.of(1959, 12, 3), "", List.of("DISI", "Física", "IMERL"), "Pepe1");
        ctrl.altaUsuarioConInstitutos("adri", "Adriana", "García", "agarcia@gmail.com", LocalDate.of(1978, 7, 28), "", List.of("DISI", "Eléctrica"), "Pepe1");
    }

        private void cargarSeguimientos(IControladorUsuario ctrl) throws Exception {
                ctrl.seguirUsuario("eleven11", "heisenberg");
                ctrl.seguirUsuario("eleven11", "chechi");
                ctrl.seguirUsuario("costas", "heisenberg");
                ctrl.seguirUsuario("roro", "adri");
                ctrl.seguirUsuario("jeffw", "phils");
                ctrl.seguirUsuario("weiss", "waston");
                ctrl.seguirUsuario("heisenberg", "benkenobi");
        }

    private void cargarCursos(IControladorCurso ctrl) throws Exception {
                String imagenCurso1 = "/Presentacion/Recursos/imagen_predeterminada_curso_3.png";
                String imagenCurso2 = "/Presentacion/Recursos/imagen_predeterminada_curso_2.png";
        // Cursos SIN previas
        ctrl.altaCurso("IMERL", "Talleres plenarios", "Talleres plenarios*: presentados por cuatro reconocidos\n"
                + "matemáticos uruguayos, plantearán diversos tópicos de matemática\n"
                + "en el marco de los cuales se realizarán actividades fomentando la\n"
                + "integración entre\n"
                + "estudiantes, docentes e investigadores", 3, 15, 1, "www.tmu.edu.uy", LocalDate.of(2026, 2, 1), new ArrayList<>(), Arrays.asList("Matemática", "Extensión"), imagenCurso1);

        ctrl.altaCurso("IMPII", "Inclusión Energética", "En el proyecto se conjuga el trabajo de docentes y estudiantes de la\n"
                + "carrera Ingeniería Industrial Mecánica a través del Módulo de\n"
                + "Extensión, en donde se trabaja en el diseño, construcción y prueba\n"
                + "de un prototipo de colector solar adquiriendo conocimientos\n"
                + "relevantes para luego poder replicarlos junto a las familias en los\n"
                + "talleres. Las premisas fundamentales a la hora de pensar los diseños\n"
                + "son: por un lado el bajo costo de los materiales y por otro la fácil\n"
                + "construcción de forma de poder construirlos ellos mismos.", 6, 45, 3, "https://eva.fing.edu.uy/course/view.php?id=783#section-2", LocalDate.of(2026, 2, 1), new ArrayList<>(), Arrays.asList("Energía", "Extensión"), imagenCurso2);

        ctrl.altaCurso("DISI", "Flor del Ceibo", "Flor de Ceibo es un proyecto central de la Universidad de la\n"
                + "República, que tiene misión por movilizar la participación de\n"
                + "estudiantes universitarios en diversas tareas vinculadas con la\n"
                + "puesta en funcionamiento del Plan Ceibal en el territorio nacional.", 15, 150, 10, "http://www.flordeceibo.edu.uy/", LocalDate.of(2008, 7, 27), new ArrayList<>(), Arrays.asList("Inclusión", "Extensión"), imagenCurso1);

        ctrl.altaCurso("INCO", "Taller de robótica educativa", "La asignatura se organiza en dos etapas. La primer etapa se dicta a\n"
                + "través de clases teóricoprácticas, donde se espera además que cada\n"
                + "estudiante le dedique horas de estudio.\n"
                + "La segunda etapa consiste en que los estudiantes trabajen en grupo\n"
                + "sobre el diseño e implementación de una experiencia didáctica de\n"
                + "inclusión del robot Butiá en el aula, utilizando los conocimientos\n"
                + "aprendidos en clase.", 8, 90, 6, "https://eva.fing.edu.uy/course/view.php?$id=1187$", LocalDate.of(2024, 2, 2), new ArrayList<>(), Arrays.asList("Robótica", "Educación"), imagenCurso2);

        ctrl.altaCurso("INCO", "Participación en investigación sobre el empleo del juego Komikan", "Se propone desarrollar una aplicación interactiva para tablet\n"
                + "Android basada en el juego de tablero Komikan (versión web del\n"
                + "juego\n"
                + "(https://codepen.io/Borborem/full/OvZBvZ/), que incluya los\n"
                + "distintos aspectos concernientes al juego, así como a situaciones\n"
                + "específicas particulares.", 9, 45, 3, "https://eva.fing.edu.uy/mod/folder/view.php?id=89398", LocalDate.of(2026, 6, 15), new ArrayList<>(), Arrays.asList("Investigación", "Educación"), imagenCurso1);

        ctrl.altaCurso("INCO", "Herramientas de apoyo a la enseñanza de inglés", "Se realizarán visitas a escuelas rurales participantes en un proyecto\n"
                + "conjunto del grupo PLN y el Programa de Políticas Lingüísticas de\n"
                + "ANEP, en el marco del cual se desarrollaron diferentes herramientas\n"
                + "para uso de maestros que enseñan inglés con apoyo remoto de\n"
                + "profesores especializados desde Montevideo.", 12, 60, 4, "https://eva.fing.edu.uy/mod/folder/view.php?id=89398", LocalDate.of(2026, 5, 24), new ArrayList<>(), Arrays.asList("Educación", "Tecnología"), imagenCurso2);

        ctrl.altaCurso("Eléctrica", "MicroBit", "El Centro Ceibal se encuentra distribuyendo placas micro:bit\n"
                + "(https://microbit.ceibal.edu.uy/) para que estudiantes de primaria\n"
                + "y secundaria aprendan nociones básicas de robótica, electrónica y\n"
                + "programación de forma autónoma y lúdica. Estas placas se basan en\n"
                + "un microcontrolador y cuentan con leds, botones, acelerómetro,\n"
                + "brújula, bluetooth y otros sensores. Además, las placas se\n"
                + "programan fácilmente con lenguaje tipo “scratch” y python, por lo\n"
                + "que son muy útiles para un primer acercamiento a la temática.", 15, 105, 7, "https://www.fing.edu.uy/noticias/extension/modulo-de-tallerextension-microbit", LocalDate.of(2026, 3, 13), new ArrayList<>(), Arrays.asList("Robótica", "Tecnología"), imagenCurso1);

        // Cursos CON previas
        List<String> previasC1 = Arrays.asList("Talleres plenarios");
        ctrl.altaCurso("IMERL", "Seminarios de Resolución de Problemas", "Seminario, *todos los jueves* en Facultad de Ingeniería a\n"
                + "partir del jueves 25 de Julio, en las áreas en que se desarrollan los\n"
                + "problemas de las Olimpíadas de Matemática.", 5, 30, 2, "www.tmu.edu.uy", LocalDate.of(2026, 7, 12), previasC1, Arrays.asList("Matemática", "Investigación"), imagenCurso2);

        ctrl.altaCurso("IMPII", "Dalavuelta", "Dalavuelta es un proyecto de extensión que nace en el Instituto de\n"
                + "Ingeniería Mecánica y Producción Industrial (IIMPI) de Fing, que, si\n"
                + "bien inicia su trabajo en el desarrollo de bicicletas accesibles para\n"
                + "personas en situación de discapacidad motriz a partir de bicicletas\n"
                + "abandonadas, se propuso diseñar otras herramientas para fomentar\n"
                + "la accesibilidad.", 10, 60, 4, "https://eva.fing.edu.uy/course/view.php?id783#section-2", LocalDate.of(2024, 6, 25), previasC1, Arrays.asList("Accesibilidad", "Extensión"), imagenCurso1);

        ctrl.altaCurso("IMPII", "Extensionismo Industrial", "El proyecto tiene como objetivo desarrollar intervenciones\n"
                + "curriculares en pequeños emprendimientos productivos de\n"
                + "diferentes sectores de la industria nacional.La metodologías de\n"
                + "trabajo permite articular diversas intervenciones, combinando\n"
                + "actividades de enseñanza, extensión e investigación por parte de\n"
                + "docentes del IMPII.", 12, 75, 5, "https://eva.fing.edu.uy/course/view.php?id=783#section-2", LocalDate.of(2025, 6, 16), previasC1, Arrays.asList("Industria", "Extensión"), imagenCurso2);
    }

    private void cargarEdiciones(IControladorEdicion ctrl) throws Exception {
                String imagenEdicion1 = "/Presentacion/Recursos/imagen_predeterminada_edCurso_1.png";
                String imagenEdicion2 = "/Presentacion/Recursos/imagen_predeterminada_edCurso_2.jpg";
                ctrl.altaEdicionCurso("Flor del Ceibo", new DTEdicionCurso("Flor del Ceibo - 2010", LocalDate.of(2010, 3, 15), LocalDate.of(2010, 7, 7), -1, -1, LocalDate.of(2010, 2, 16), Arrays.asList("bruces"), imagenEdicion1));
                ctrl.altaEdicionCurso("Flor del Ceibo", new DTEdicionCurso("Flor del Ceibo - 2012", LocalDate.of(2012, 8, 1), LocalDate.of(2012, 11, 20), -1, -1, LocalDate.of(2012, 7, 10), Arrays.asList("bruces", "adri"), imagenEdicion2));
                ctrl.altaEdicionCurso("Flor del Ceibo", new DTEdicionCurso("Flor del Ceibo - 2025", LocalDate.of(2025, 4, 10), LocalDate.of(2025, 8, 7), -1, -1, LocalDate.of(2025, 3, 6), Arrays.asList("bruces", "adri"), imagenEdicion1));

        ctrl.altaEdicionCurso("Dalavuelta", new DTEdicionCurso("Dalavuelta - 2025", LocalDate.of(2024, 8, 20), LocalDate.of(2024, 11, 10), 15, 15, LocalDate.of(2024, 7, 20), Arrays.asList("phils"), imagenEdicion2));
        ctrl.altaEdicionCurso("Extensionismo Industrial", new DTEdicionCurso("Extensionismo Industrial - 2025", LocalDate.of(2025, 8, 10), LocalDate.of(2025, 11, 10), 15, 15, LocalDate.of(2025, 7, 8), Arrays.asList("phils"), imagenEdicion1));
        ctrl.altaEdicionCurso("Inclusión Energética", new DTEdicionCurso("Inclusión Energética - 2026", LocalDate.of(2026, 3, 15), LocalDate.of(2026, 4, 30), 30, 30, LocalDate.of(2026, 2, 20), Arrays.asList("phils"), imagenEdicion2));

        ctrl.altaEdicionCurso("Taller de robótica educativa", new DTEdicionCurso("Taller de robótica educativa - 2024", LocalDate.of(2024, 3, 10), LocalDate.of(2024, 5, 10), 10, 10, LocalDate.of(2024, 2, 15), Arrays.asList("heisenberg"), imagenEdicion1));
        ctrl.altaEdicionCurso("Taller de robótica educativa", new DTEdicionCurso("Taller de robótica educativa - 2026", LocalDate.of(2026, 3, 10), LocalDate.of(2026, 5, 10), 10, 10,  LocalDate.of(2026, 2, 15), Arrays.asList("heisenberg", "benkenobi"), imagenEdicion2));
        ctrl.altaEdicionCurso("Taller de robótica educativa", new DTEdicionCurso("Taller de robótica educativa-2026-2", LocalDate.of(2026, 9, 10), LocalDate.of(2026, 11, 8), 20, 20, LocalDate.of(2026, 8, 15), Arrays.asList("benkenobi", "waston"), imagenEdicion1));

        ctrl.altaEdicionCurso("Participación en investigación sobre el empleo del juego Komikan", new DTEdicionCurso("Participación en investigación sobre el empleo del juego Komikan - 2026", LocalDate.of(2026, 7, 29), LocalDate.of(2026, 10, 7), 5, 5, LocalDate.of(2026, 7, 10), Arrays.asList("waston"), imagenEdicion2));
        ctrl.altaEdicionCurso("Herramientas de apoyo a la enseñanza de inglés", new DTEdicionCurso("Herramientas de apoyo a la enseñanza de inglés - 26", LocalDate.of(2026, 9, 15), LocalDate.of(2026, 12, 15), 5, 5, LocalDate.of(2026, 6, 2), Arrays.asList("heisenberg"), imagenEdicion1));

        ctrl.altaEdicionCurso("MicroBit", new DTEdicionCurso("MicroBit-2026", LocalDate.of(2026, 8, 12), LocalDate.of(2026, 12, 5), 30, 30, LocalDate.of(2026, 7, 2), Arrays.asList("house"), imagenEdicion2));
        ctrl.altaEdicionCurso("Talleres plenarios", new DTEdicionCurso("Talleres plenarios - 2026", LocalDate.of(2026, 3, 10), LocalDate.of(2026, 3, 30), -1, -1, LocalDate.of(2026, 3, 2), Arrays.asList("timmy", "danny"), imagenEdicion1));
        ctrl.altaEdicionCurso("Seminarios de Resolución de Problemas", new DTEdicionCurso("Seminarios de Resolución de Problemas - 2026", LocalDate.of(2026, 9, 10), LocalDate.of(2026, 10, 20), -1, 0, LocalDate.of(2026, 7, 12), Arrays.asList("timmy"), imagenEdicion2));
    }

    private void cargarInscripciones(IControladorEdicion ctrl) throws Exception {
        ctrl.inscribirEstudianteAEdicion("eleven11", "Flor del Ceibo - 2010", LocalDate.of(2010, 2, 20));
        ctrl.inscribirEstudianteAEdicion("chechi", "Flor del Ceibo - 2010", LocalDate.of(2010, 2, 25));

        ctrl.inscribirEstudianteAEdicion("costas", "Flor del Ceibo - 2012", LocalDate.of(2012, 7, 12));
        ctrl.inscribirEstudianteAEdicion("roro", "Flor del Ceibo - 2012", LocalDate.of(2012, 7, 15));
        ctrl.inscribirEstudianteAEdicion("weiss", "Flor del Ceibo - 2012", LocalDate.of(2012, 7, 30));

        ctrl.inscribirEstudianteAEdicion("chechi", "Dalavuelta - 2025", LocalDate.of(2024, 7, 25));
        ctrl.inscribirEstudianteAEdicion("eleven11", "Dalavuelta - 2025", LocalDate.of(2024, 7, 28));

        ctrl.inscribirEstudianteAEdicion("costas", "Extensionismo Industrial - 2025", LocalDate.of(2025, 7, 18));
        ctrl.inscribirEstudianteAEdicion("chechi", "Extensionismo Industrial - 2025", LocalDate.of(2025, 7, 20));

        ctrl.inscribirEstudianteAEdicion("weiss", "Taller de robótica educativa - 2024", LocalDate.of(2017, 2, 18));
        ctrl.inscribirEstudianteAEdicion("roro", "Taller de robótica educativa - 2024", LocalDate.of(2024, 2, 20));
    }

    private void cargarProgramas(IControladorProgramaFormacion ctrl) throws Exception {
        LocalDate fechaAltaSys = LocalDate.of(2026, 1, 1);
                String imagenPrograma1 = "/Presentacion/Recursos/imagen_predeterminada_progForm_2.png";
                String imagenPrograma2 = "/Presentacion/Recursos/imagen_predeterminada_progForm_1.png";

                // Los programas de ejemplo también guardan quién es responsable de administrarlos.
        ctrl.crearProgramaFormacion("heisenberg", "EFI Ingeniería Mecánica", "Programa mecánica",
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 10, 31), fechaAltaSys, imagenPrograma1);

        ctrl.crearProgramaFormacion("heisenberg", "Formación integral", "Programa varios institutos",
                LocalDate.of(2026, 7, 15), LocalDate.of(2027, 1, 1), fechaAltaSys, imagenPrograma2);

        ctrl.crearProgramaFormacion("heisenberg", "EFI Robótica", "Programa robótica",
                LocalDate.of(2026, 9, 3), LocalDate.of(2026, 11, 18), fechaAltaSys, imagenPrograma1);

        ctrl.agregarCursoAPrograma("EFI Ingeniería Mecánica", "Dalavuelta");
        ctrl.agregarCursoAPrograma("EFI Ingeniería Mecánica", "Extensionismo Industrial");
        ctrl.agregarCursoAPrograma("EFI Ingeniería Mecánica", "Inclusión Energética");

        ctrl.agregarCursoAPrograma("Formación integral", "Seminarios de Resolución de Problemas");
        ctrl.agregarCursoAPrograma("Formación integral", "Extensionismo Industrial");
        ctrl.agregarCursoAPrograma("Formación integral", "Flor del Ceibo");

        ctrl.agregarCursoAPrograma("EFI Robótica", "Taller de robótica educativa");
        ctrl.agregarCursoAPrograma("EFI Robótica", "MicroBit");
    }

        private void cargarInscripcionesPF(IControladorProgramaFormacion ctrl) throws Exception {
                ctrl.inscribirEstudianteAPrograma("eleven11", "EFI Ingeniería Mecánica", LocalDate.of(2026, 4, 15));
                ctrl.inscribirEstudianteAPrograma("costas", "EFI Ingeniería Mecánica", LocalDate.of(2026, 4, 20));
                ctrl.inscribirEstudianteAPrograma("chechi", "Formación integral", LocalDate.of(2026, 7, 10));
                ctrl.inscribirEstudianteAPrograma("roro", "Formación integral", LocalDate.of(2026, 7, 12));
                ctrl.inscribirEstudianteAPrograma("weiss", "EFI Robótica", LocalDate.of(2026, 8, 20));
                ctrl.inscribirEstudianteAPrograma("jeffw", "EFI Robótica", LocalDate.of(2026, 8, 25));
        }
}