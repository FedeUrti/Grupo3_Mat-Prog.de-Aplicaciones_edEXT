package com.grupo3_mat.edEXT.Logica;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTEdicionCurso;
import com.grupo3_mat.edEXT.Logica.Fabrica;
import com.grupo3_mat.edEXT.Logica.Interfaces.*;
// Asegúrate de importar tu Fabrica y otras dependencias necesarias

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CargadorDatosPrueba {

    public void cargarDatosTotales() {
        // Obtener las instancias de tu fábrica
        Fabrica fabrica = Fabrica.getInstance();
        IControladorInstituto ctrlInst = fabrica.getIControladorInstituto();
        IControladorUsuario ctrlUsu = fabrica.getIControladorUsuario();
        IControladorCurso ctrlCurso = fabrica.getIControladorCurso();
        IControladorEdicion ctrlEdi = fabrica.getIControladorEdicion();
        IControladorProgramaFormacion ctrlProg = fabrica.getIControladorProgramaFormacion();

        try {
            System.out.println("Iniciando carga masiva de datos...");
            cargarInstitutos(ctrlInst);
            cargarUsuarios(ctrlUsu);
            cargarCursos(ctrlCurso);
            cargarEdiciones(ctrlEdi);
            cargarInscripciones(ctrlEdi);
            cargarProgramas(ctrlProg);
            System.out.println("¡Datos de prueba cargados con éxito!");
        } catch (Exception e) {
            System.err.println("Error en la carga: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarInstitutos(IControladorInstituto ctrl) {
        // AltaInstituto solo recibe el String[cite: 44]
        ctrl.altaInstituto("INCO");
        ctrl.altaInstituto("IMERL");
        ctrl.altaInstituto("Física");
        ctrl.altaInstituto("IMPII");
        ctrl.altaInstituto("Eléctrica");
        ctrl.altaInstituto("DISI");
    }

    private void cargarUsuarios(IControladorUsuario ctrl) throws Exception {
        // Estudiantes: nomInstituto va en null[cite: 46]
        ctrl.altaUsuario("eleven11", "Eleven", "Twelve", "eleven11@gmail.com", LocalDate.of(1971, 12, 31), "", null);
        ctrl.altaUsuario("costas", "Gerardo", "Costas", "gcostas@gmail.com", LocalDate.of(1983, 11, 15), "", null);
        ctrl.altaUsuario("roro", "Rodrigo", "Cotelo", "rcotelo@yahoo.com", LocalDate.of(1975, 8, 2), "", null);
        ctrl.altaUsuario("chechi", "Cecilia", "Garrido", "cgarrido@hotmail.com", LocalDate.of(1987, 9, 12), "", null);
        ctrl.altaUsuario("jeffw", "Jeff", "Williams", "jwilliams@gmail.com", LocalDate.of(1964, 11, 27), "", null);
        ctrl.altaUsuario("weiss", "Adrian", "Weiss", "aweiss@hotmail.com", LocalDate.of(1978, 12, 23), "", null);

        // Docentes: pasamos el nombre del Instituto[cite: 46]
        ctrl.altaUsuario("heisenberg", "Walter", "White", "heisenberg@gmail.com", LocalDate.of(1956, 3, 7), "", "INCO");
        ctrl.altaUsuario("benkenobi", "Obi-Wan", "Kenobi", "benKenobi@gmail.com", LocalDate.of(1914, 4, 2), "", "INCO");
        ctrl.altaUsuario("waston", "Emma", "Watson", "e.watson@gmail.com", LocalDate.of(1990, 4, 15), "", "INCO");
        ctrl.altaUsuario("house", "Gregory", "House", "greghouse@gmail.com", LocalDate.of(1959, 5, 15), "", "Eléctrica");
        ctrl.altaUsuario("timmy", "Tim", "Cook", "tim.cook@apple.com", LocalDate.of(1960, 11, 1), "", "IMERL");
        ctrl.altaUsuario("danny", "Daniel", "Riccio", "dan.riccio@gmail.com", LocalDate.of(1963, 7, 5), "", "IMERL");
        ctrl.altaUsuario("phils", "Philip", "Schiller", "schiller@gmail.com", LocalDate.of(1961, 10, 7), "", "IMPII");
        ctrl.altaUsuario("bruces", "Bruce", "Sewell", "sewell@gmail.com", LocalDate.of(1959, 12, 3), "", "DISI");
        ctrl.altaUsuario("adri", "Adriana", "García", "agarcia@gmail.com", LocalDate.of(1978, 7, 28), "", "DISI");
    }

    private void cargarCursos(IControladorCurso ctrl) throws Exception {
        // Nota: La duración en ControladorCurso es un int[cite: 42], extraje el número de las semanas.
        // Se deben cargar primero los cursos que NO tienen previas, y luego los que sí.

        // 1. Cursos SIN previas[cite: 42]
        ctrl.altaCurso("IMERL", "Talleres plenarios", "Talleres presentados por...", 3, 15, 1, "www.tmu.edu.uy", LocalDate.of(2026, 2, 1), new ArrayList<>());
        ctrl.altaCurso("IMPII", "Inclusión Energética", "En el proyecto se conjuga...", 6, 45, 3, "https://eva.fing.edu.uy/course/view.php?id=783#section-2", LocalDate.of(2026, 2, 1), new ArrayList<>());
        ctrl.altaCurso("DISI", "Flor del Ceibo", "Flor de Ceibo es un proyecto...", 15, 150, 10, "http://www.flordeceibo.edu.uy/", LocalDate.of(2008, 7, 27), new ArrayList<>());
        ctrl.altaCurso("INCO", "Taller de robótica educativa", "La asignatura se organiza...", 8, 90, 6, "https://eva.fing.edu.uy/course/view.php?$id=1187$", LocalDate.of(2024, 2, 2), new ArrayList<>());
        ctrl.altaCurso("INCO", "Participación en investigación sobre el empleo del juego Komikan", "Se propone desarrollar...", 9, 45, 3, "https://eva.fing.edu.uy/mod/folder/view.php?id$1=89398$", LocalDate.of(2026, 6, 15), new ArrayList<>());
        ctrl.altaCurso("INCO", "Herramientas de apoyo a la enseñanza de inglés", "Se realizarán visitas...", 12, 60, 4, "https://eva.fing.edu.uy/mod/folder/view.php?id=89398$", LocalDate.of(2026, 5, 24), new ArrayList<>());
        ctrl.altaCurso("Eléctrica", "MicroBit", "El Centro Ceibal se encuentra...", 15, 105, 7, "https://www.fing.edu.uy/noticias/...", LocalDate.of(2026, 3, 13), new ArrayList<>());

        // 2. Cursos CON previas (Las listas se pasan en la misma firma)[cite: 42]
        List<String> previasC1 = Arrays.asList("Talleres plenarios");
        ctrl.altaCurso("IMERL", "Seminarios de Resolución de Problemas", "Seminario, todos los jueves...", 5, 30, 2, "www.tmu.edu.uy", LocalDate.of(2026, 7, 12), previasC1);
        ctrl.altaCurso("IMPII", "Dalavuelta", "Dalavuelta es un proyecto...", 10, 60, 4, "https://eva.fing.edu.uy/course/view.php?id 783#section-2", LocalDate.of(2024, 6, 25), previasC1);
        ctrl.altaCurso("IMPII", "Extensionismo Industrial", "El proyecto tiene como objetivo...", 12, 75, 5, "https://eva.fing.edu.uy/course/view.php?id=783#section-2", LocalDate.of(2025, 6, 16), previasC1);
    }

    private void cargarEdiciones(IControladorEdicion ctrl) throws Exception {
        // Se usa DTEdicionCurso, pasándole la lista de docentes directamente[cite: 43]

        ctrl.altaEdicionCurso("Flor del Ceibo", new DTEdicionCurso("Flor del Ceibo - 2010", LocalDate.of(2010, 3, 15), LocalDate.of(2010, 7, 7), -1, LocalDate.of(2010, 2, 16), Arrays.asList("bruces")));
        ctrl.altaEdicionCurso("Flor del Ceibo", new DTEdicionCurso("Flor del Ceibo - 2012", LocalDate.of(2012, 8, 1), LocalDate.of(2012, 11, 20), -1, LocalDate.of(2012, 7, 10), Arrays.asList("bruces", "adri")));
        ctrl.altaEdicionCurso("Flor del Ceibo", new DTEdicionCurso("Flor del Ceibo - 2025", LocalDate.of(2025, 4, 10), LocalDate.of(2025, 8, 7), -1, LocalDate.of(2025, 3, 6), Arrays.asList("bruces", "adri")));

        ctrl.altaEdicionCurso("Dalavuelta", new DTEdicionCurso("Dalavuelta - 2025", LocalDate.of(2024, 8, 20), LocalDate.of(2024, 11, 10), 15, LocalDate.of(2024, 7, 20), Arrays.asList("phils")));
        ctrl.altaEdicionCurso("Extensionismo Industrial", new DTEdicionCurso("Extensionismo Industrial - 2025", LocalDate.of(2025, 8, 10), LocalDate.of(2025, 11, 10), 15, LocalDate.of(2025, 7, 8), Arrays.asList("phils")));
        ctrl.altaEdicionCurso("Inclusión Energética", new DTEdicionCurso("Inclusión Energética - 2026", LocalDate.of(2026, 3, 15), LocalDate.of(2026, 4, 30), 30, LocalDate.of(2026, 2, 20), Arrays.asList("phils")));

        ctrl.altaEdicionCurso("Taller de robótica educativa", new DTEdicionCurso("Taller de robótica educativa - 2024", LocalDate.of(2024, 3, 10), LocalDate.of(2024, 5, 10), 10, LocalDate.of(2024, 2, 15), Arrays.asList("heisenberg")));
        ctrl.altaEdicionCurso("Taller de robótica educativa", new DTEdicionCurso("Taller de robótica educativa - 2026", LocalDate.of(2026, 3, 10), LocalDate.of(2026, 5, 10), 10, LocalDate.of(2026, 2, 15), Arrays.asList("heisenberg", "benkenobi")));
        ctrl.altaEdicionCurso("Taller de robótica educativa", new DTEdicionCurso("Taller de robótica educativa-2026-2", LocalDate.of(2026, 9, 10), LocalDate.of(2026, 11, 8), 20, LocalDate.of(2026, 8, 15), Arrays.asList("benkenobi", "waston")));

        ctrl.altaEdicionCurso("Participación en investigación sobre el empleo del juego Komikan", new DTEdicionCurso("Participación en investigación sobre el empleo del juego Komikan - 2026", LocalDate.of(2026, 7, 29), LocalDate.of(2026, 10, 7), 5, LocalDate.of(2026, 7, 10), Arrays.asList("waston")));
        ctrl.altaEdicionCurso("Herramientas de apoyo a la enseñanza de inglés", new DTEdicionCurso("Herramientas de apoyo a la enseñanza de inglés - 26", LocalDate.of(2026, 9, 15), LocalDate.of(2026, 12, 15), 5, LocalDate.of(2026, 6, 2), Arrays.asList("heisenberg")));

        ctrl.altaEdicionCurso("MicroBit", new DTEdicionCurso("MicroBit-2026", LocalDate.of(2026, 8, 12), LocalDate.of(2026, 12, 5), 30, LocalDate.of(2026, 7, 2), Arrays.asList("house")));
        ctrl.altaEdicionCurso("Talleres plenarios", new DTEdicionCurso("Talleres plenarios - 2026", LocalDate.of(2026, 3, 10), LocalDate.of(2026, 3, 30), -1, LocalDate.of(2026, 3, 2), Arrays.asList("timmy", "danny")));
        ctrl.altaEdicionCurso("Seminarios de Resolución de Problemas", new DTEdicionCurso("Seminarios de Resolución de Problemas - 2026", LocalDate.of(2026, 9, 10), LocalDate.of(2026, 10, 20), -1, LocalDate.of(2026, 7, 12), Arrays.asList("timmy")));
    }

    private void cargarInscripciones(IControladorEdicion ctrl) throws Exception {
        // firma: inscribirEstudianteAEdicion(String nicknameEstudiante, String nombreEdicion, LocalDate fechaInscripcion)[cite: 43]
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
        // ControladorPrograma usa java.util.Date[cite: 45]. Hacemos la conversión.
        Date fechaAltaSys = convertirLocalDateADate(LocalDate.of(2026, 1, 1)); // Fecha alta genérica

        ctrl.crearProgramaFormacion("EFI Ingeniería Mecánica", "Programa mecánica",
                convertirLocalDateADate(LocalDate.of(2026, 5, 1)), convertirLocalDateADate(LocalDate.of(2026, 10, 31)), fechaAltaSys);

        ctrl.crearProgramaFormacion("Formación integral", "Programa varios institutos",
                convertirLocalDateADate(LocalDate.of(2026, 7, 15)), convertirLocalDateADate(LocalDate.of(2027, 1, 1)), fechaAltaSys);

        ctrl.crearProgramaFormacion("EFI Robótica", "Programa robótica",
                convertirLocalDateADate(LocalDate.of(2026, 9, 3)), convertirLocalDateADate(LocalDate.of(2026, 11, 18)), fechaAltaSys);

        // Agregando Cursos a Programas[cite: 45]
        ctrl.agregarCursoAPrograma("EFI Ingeniería Mecánica", "Dalavuelta");
        ctrl.agregarCursoAPrograma("EFI Ingeniería Mecánica", "Extensionismo Industrial");
        ctrl.agregarCursoAPrograma("EFI Ingeniería Mecánica", "Inclusión Energética");

        ctrl.agregarCursoAPrograma("Formación integral", "Seminarios de Resolución de Problemas");
        ctrl.agregarCursoAPrograma("Formación integral", "Extensionismo Industrial");
        ctrl.agregarCursoAPrograma("Formación integral", "Flor del Ceibo");

        ctrl.agregarCursoAPrograma("EFI Robótica", "Taller de robótica educativa");
        ctrl.agregarCursoAPrograma("EFI Robótica", "MicroBit");
    }

    // Utilidad para convertir LocalDate a java.util.Date
    private Date convertirLocalDateADate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
