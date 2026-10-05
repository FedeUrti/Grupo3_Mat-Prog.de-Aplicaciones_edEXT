
import com.grupo3_mat.edEXT.Logica.Fabrica;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCategoria;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorEdicion;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorInstituto;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorProgramaFormacion;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorUsuario;

import com.grupo3_mat.edEXT.Logica.Clases.Curso;
import com.grupo3_mat.edEXT.Logica.Clases.ProgramaFormacion;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTEdicionCurso;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtCurso;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtDocente;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtEstudiante;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTProgramaFormacion;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtUsuario;
import com.grupo3_mat.edEXT.Logica.DataTypes.CriterioOrdenInscripciones;
import com.grupo3_mat.edEXT.Logica.Clases.EstadoInscripcion;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ControladoresTest {

    private static IControladorInstituto ctrlInstituto;
    private static IControladorCategoria ctrlCategoria;
    private static IControladorUsuario ctrlUsuario;
    private static IControladorCurso ctrlCurso;
    private static IControladorEdicion ctrlEdicion;
    private static IControladorProgramaFormacion ctrlPrograma;

    @BeforeAll
    static void setUp() {
        
        Fabrica fabrica = Fabrica.getInstance();
        ctrlInstituto = fabrica.getIControladorInstituto();
        ctrlCategoria = fabrica.getIControladorCategoria();
        ctrlUsuario = fabrica.getIControladorUsuario();
        ctrlCurso = fabrica.getIControladorCurso();
        ctrlEdicion = fabrica.getIControladorEdicion();
        ctrlPrograma = fabrica.getIControladorProgramaFormacion();

    }

    // -------------------------------------------------------------------------
    // 1. PRUEBAS PARA CONTROLADOR INSTITUTO
    // -------------------------------------------------------------------------
    @Test
    @Order(1)
    @DisplayName("Instituto: Alta y Listar Institutos")
    void testInstitutoAltaYListar() {
        ctrlInstituto.altaInstituto("INCO");
        ctrlInstituto.altaInstituto("FING");
        ctrlInstituto.altaInstituto("IMERL");

        List<String> institutos = ctrlInstituto.listarInstitutos();
        assertTrue(institutos.contains("INCO"));
        assertTrue(institutos.contains("FING"));
        assertTrue(institutos.contains("IMERL"));

        // Excepción al repetir nombre
        assertThrows(IllegalArgumentException.class, () -> ctrlInstituto.altaInstituto("INCO"));
    }

    // -------------------------------------------------------------------------
    // 2. PRUEBAS PARA CONTROLADOR USUARIO
    // -------------------------------------------------------------------------
    @Test
    @Order(2)
    @DisplayName("Usuario: Alta Estudiante y Docente + Consultas")
    void testUsuarioAltaYConsultas() throws Exception {
        // Alta Estudiante
        ctrlUsuario.altaUsuario("estudiante1", "Juan", "Perez", "juan@test.com", LocalDate.of(2000, 1, 1), "/img/est.png", null, "clave-estudiante1");

        // Alta Docente
        ctrlUsuario.altaUsuario("docente1", "Maria", "Gomez", "maria@test.com", LocalDate.of(1985, 5, 10), "/img/doc.png", "INCO", "clave-docente1");
        ctrlUsuario.altaUsuarioConInstitutos(
            "docenteMulti", "Docente", "Multi", "multi@test.com", LocalDate.of(1980, 1, 1), "",
            List.of("INCO", "IMERL"), "clave-docente-multi");

        // Listar Nicknames
        List<String> nicknames = ctrlUsuario.listarNicknamesUsuarios();
        assertTrue(nicknames.contains("estudiante1"));
        assertTrue(nicknames.contains("docente1"));

        // Listar Estudiantes
        List<String> estudiantes = ctrlUsuario.listarEstudiantes();
        assertTrue(estudiantes.contains("estudiante1"));
        assertFalse(estudiantes.contains("docente1"));

        // Listar Docentes por Instituto
        List<String> docentesInco = ctrlUsuario.listarNicknamesDocentesPorInstituto("INCO");
        assertTrue(docentesInco.contains("docente1"));
        assertTrue(docentesInco.contains("docenteMulti"));
        assertTrue(ctrlUsuario.listarNicknamesDocentesPorInstituto("IMERL").contains("docenteMulti"));

        // Obtener Info Usuario (Estudiante)
        DtUsuario infoEst = ctrlUsuario.obtenerInfoUsuario("estudiante1");
        assertNotNull(infoEst);
        assertTrue(infoEst instanceof DtEstudiante);

        // Obtener Info Usuario (Docente)
        DtUsuario infoDoc = ctrlUsuario.obtenerInfoUsuario("docente1");
        assertNotNull(infoDoc);
        assertTrue(infoDoc instanceof DtDocente);
        DtDocente infoDocMulti = (DtDocente) ctrlUsuario.obtenerInfoUsuario("docenteMulti");
        assertEquals(2, infoDocMulti.getInstitutos().size());
        assertTrue(infoDocMulti.getInstitutos().containsAll(List.of("INCO", "IMERL")));
        assertTrue(ctrlUsuario.iniciarSesion("estudiante1", "clave-estudiante1"));
        assertTrue(ctrlUsuario.iniciarSesion("maria@test.com", "clave-docente1"));
        assertThrows(Exception.class, () -> ctrlUsuario.iniciarSesion("estudiante1", "incorrecta"));

        // Modificar Datos Usuario
        ctrlUsuario.modificarDatosUsuario("estudiante1", "Juan Carlos", "Perez Gomez", LocalDate.of(2000, 1, 1), "/img/est2.png");
        DtUsuario infoEstMod = ctrlUsuario.obtenerInfoUsuario("estudiante1");
        assertEquals("Juan Carlos", infoEstMod.getNombre());
    }

    @Test
    @Order(3)
    @DisplayName("Usuario: Excepciones de Alta y Modificación")
    void testUsuarioExcepciones() {
        assertThrows(Exception.class, ()
            -> ctrlUsuario.altaUsuario("sinClave", "Sin", "Clave", "sin-clave@test.com", LocalDate.now(), "", null, "")
        );

        // Nickname duplicado
        assertThrows(Exception.class, ()
                -> ctrlUsuario.altaUsuario("estudiante1", "Pedro", "García", "pedro@test.com", LocalDate.now(), "", null, "clave-prueba")
        );

        // Correo duplicado
        assertThrows(Exception.class, ()
                -> ctrlUsuario.altaUsuario("estudiante2", "Pedro", "García", "juan@test.com", LocalDate.now(), "", null, "clave-prueba")
        );

        // Instituto inexistente para docente
        assertThrows(Exception.class, ()
                -> ctrlUsuario.altaUsuario("docente2", "Ana", "Lopez", "ana@test.com", LocalDate.now(), "", "InstitutoInexistente", "clave-prueba")
        );

        // Modificar usuario inexistente
        assertThrows(Exception.class, ()
                -> ctrlUsuario.modificarDatosUsuario("noExiste", "Nom", "Ape", LocalDate.now(), "")
        );

        // Info de usuario inexistente retorna null
        assertNull(ctrlUsuario.obtenerInfoUsuario("noExiste"));
    }

    // -------------------------------------------------------------------------
    // 3. PRUEBAS PARA CONTROLADOR CURSO
    // -------------------------------------------------------------------------
    @Test
    @Order(4)
    @DisplayName("Curso: Alta con previas, Consultar y Listar Cursos")
    void testCursoAltaYConsultas() throws Exception {
        ctrlCategoria.altaCategoria("Educativo");

        // Curso previo
        ctrlCurso.altaCurso("INCO", "Prog1", "Introduccion a la Programacion", 10, 60, 10, "http://prog1.com", LocalDate.now(), null, null, "");

        // Curso principal con previa
        List<String> previas = new ArrayList<>();
        previas.add("Prog1");
        ctrlCurso.altaCurso("INCO", "Prog2", "Programacion Avanzada", 12, 80, 12, "http://prog2.com", LocalDate.now(), previas, List.of("Educativo"), "");

        // Listar Cursos
        List<String> todosCursos = ctrlCurso.listarCursos();
        assertTrue(todosCursos.contains("Prog1"));
        assertTrue(todosCursos.contains("Prog2"));

        // Listar por Instituto
        List<String> cursosInco = ctrlCurso.listarCursosPorInstituto("INCO");
        assertTrue(cursosInco.contains("Prog1"));

        // Consultar Curso
        DtCurso dtProg2 = ctrlCurso.consultarCurso("Prog2");
        assertNotNull(dtProg2);
        assertEquals("Prog2", dtProg2.getNombre());
        assertTrue(dtProg2.getPrevias().contains("Prog1"));
        assertTrue(dtProg2.getCategorias().contains("Educativo"));
        assertThrows(Exception.class, () -> ctrlCategoria.altaCategoria("Educativo"));
    }

    @Test
    @Order(5)
    @DisplayName("Curso: Excepciones")
    void testCursoExcepciones() {
        // Curso duplicado
        assertThrows(IllegalArgumentException.class, ()
                -> ctrlCurso.altaCurso("INCO", "Prog1", "Desc", 5, 40, 5, "", LocalDate.now(), null, null , "")
        );

        // Instituto inexistente
        assertThrows(IllegalArgumentException.class, ()
                -> ctrlCurso.altaCurso("NoExisteInst", "Fisica1", "Desc", 5, 40, 5, "", LocalDate.now(),  null, null , "")
        );

        // Consultar curso inexistente
        assertThrows(Exception.class, () -> ctrlCurso.consultarCurso("CursoQueNoExiste"));

        // Edicion vigente de curso inexistente
        assertThrows(Exception.class, () -> ctrlCurso.obtenerEdicionVigente("CursoQueNoExiste", LocalDate.now()));
    }

    // -------------------------------------------------------------------------
    // 4. PRUEBAS PARA CONTROLADOR EDICION
    // -------------------------------------------------------------------------
    @Test
    @Order(6)
    @DisplayName("Edicion: Alta, Detalle, Listados e Inscripción")
    void testEdicionAltaYInscripcion() throws Exception {
        List<String> docentes = new ArrayList<>();
        docentes.add("docente1");

        DTEdicionCurso dtEd = new DTEdicionCurso(
                "Prog1-2026",
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 7, 1),
                30,
                10,
                LocalDate.of(2026, 2, 1),
                docentes
        );

        // Alta Edición
        ctrlEdicion.altaEdicionCurso("Prog1", dtEd);

        // Listar Ediciones por Curso
        List<String> edProg1 = ctrlEdicion.listarEdicionesPorCurso("Prog1");
        assertTrue(edProg1.contains("Prog1-2026"));

        // Detalle de Edición
        DTEdicionCurso detalle = ctrlEdicion.mostrarDetalleEdicion("Prog1-2026");
        assertNotNull(detalle);
        assertEquals("Prog1-2026", detalle.getNombre());

        // Obtener Curso de Edición
        String nomCurso = ctrlEdicion.obtenerCursoDeEdicion("Prog1-2026");
        assertEquals("Prog1", nomCurso);

        // Listar Ediciones de Docente
        List<String> edDocente = ctrlEdicion.listarEdicionesDeDocente("docente1");
        assertTrue(edDocente.contains("Prog1-2026"));

        // Inscribir Estudiante
        LocalDate hoy = LocalDate.now();
        String edicionAnterior = "Prog1-2025-Rechazada";
        DTEdicionCurso datosEdicionAnterior = new DTEdicionCurso(
            edicionAnterior,
            hoy.minusYears(2),
            hoy.minusYears(1),
            -1,
            -1,
            hoy.minusYears(2),
            docentes
        );
        ctrlEdicion.altaEdicionCurso("Prog1", datosEdicionAnterior);
        ctrlEdicion.inscribirEstudianteAEdicion("estudiante1", edicionAnterior, hoy);
        ctrlEdicion.cambiarEstadoInscripcion("docente1", edicionAnterior, "estudiante1", EstadoInscripcion.RECHAZADA);

        ctrlEdicion.inscribirEstudianteAEdicion("estudiante1", "Prog1-2026", LocalDate.now());

        List<com.grupo3_mat.edEXT.Logica.DataTypes.DTInscripcionEdicion> inscripciones =
            ctrlEdicion.listarInscripcionesAEdicion("docente1", "Prog1-2026", CriterioOrdenInscripciones.PRIORIDAD);
        assertEquals(1, inscripciones.size());
        assertEquals(0.5, inscripciones.get(0).getPrioridad());

        List<com.grupo3_mat.edEXT.Logica.DataTypes.DTInscripcionEdicion> resultados =
            ctrlEdicion.listarResultadosInscripcionesEstudiante("estudiante1");
        assertEquals(2, resultados.size());
        assertTrue(resultados.stream().anyMatch(inscripcion -> inscripcion.getEstado() == EstadoInscripcion.RECHAZADA));

        ctrlUsuario.altaUsuario("docenteNoAsignado", "Otro", "Docente", "otro-docente@test.com", hoy, "", "INCO", "clave-docente-no-asignado");
        assertThrows(Exception.class, () -> ctrlEdicion.listarInscripcionesAEdicion(
            "docenteNoAsignado", "Prog1-2026", CriterioOrdenInscripciones.FECHA_INSCRIPCION));

        ctrlEdicion.cambiarEstadoInscripcion("docente1", "Prog1-2026", "estudiante1", EstadoInscripcion.ACEPTADA);
        assertEquals(1, ctrlEdicion.listarAceptadosAEdicion("docente1", "Prog1-2026").size());
        assertThrows(Exception.class, () -> ctrlEdicion.cambiarEstadoInscripcion(
            "docente1", "Prog1-2026", "estudiante1", EstadoInscripcion.RECHAZADA));
    }

    @Test
    @Order(7)
    @DisplayName("Edicion: Excepciones")
    void testEdicionExcepciones() {
        DTEdicionCurso dtEd = new DTEdicionCurso("Prog1-2026", LocalDate.now(), LocalDate.now(),30, 10, LocalDate.now(), null);

        // Edición duplicada
        assertThrows(Exception.class, () -> ctrlEdicion.altaEdicionCurso("Prog1", dtEd));

        // Curso inexistente
        DTEdicionCurso dtEd2 = new DTEdicionCurso("EdicionX", LocalDate.now(), LocalDate.now(),30, 10, LocalDate.now(), null);
        assertThrows(Exception.class, () -> ctrlEdicion.altaEdicionCurso("CursoInexistente", dtEd2));

        // Docente inexistente al crear edición
        List<String> docInexistente = new ArrayList<>();
        docInexistente.add("docenteInexistente");
        DTEdicionCurso dtEdBadDoc = new DTEdicionCurso("EdicionBadDoc", LocalDate.now(), LocalDate.now(), 30,10, LocalDate.now(), docInexistente);
        assertThrows(Exception.class, () -> ctrlEdicion.altaEdicionCurso("Prog1", dtEdBadDoc));

        // Inscribir estudiante ya inscripto
        assertThrows(Exception.class, ()
                -> ctrlEdicion.inscribirEstudianteAEdicion("estudiante1", "Prog1-2026", LocalDate.now())
        );

        // Inscribir usuario que no es estudiante (es docente)
        assertThrows(Exception.class, ()
                -> ctrlEdicion.inscribirEstudianteAEdicion("docente1", "Prog1-2026", LocalDate.now())
        );
    }

    // -------------------------------------------------------------------------
    // 5. PRUEBAS PARA CONTROLADOR PROGRAMA DE FORMACION
    // -------------------------------------------------------------------------
    @Test
    @Order(8)
    @DisplayName("ProgramaFormacion: Crear, Listar, Seleccionar y Agregar Curso")
    void testProgramaFormacion() throws Exception {
        LocalDate ahora = LocalDate.now();

        // Crear Programa
        ctrlPrograma.crearProgramaFormacion("docente1", "Desarrollo Java", "Programa completo de Java", ahora, ahora, ahora, "aa");

        // Listar Programas
        List<DTProgramaFormacion> programas = ctrlPrograma.listarProgramas();
        assertFalse(programas.isEmpty());

        // Seleccionar Programa
        DTProgramaFormacion pf = ctrlPrograma.seleccionarPrograma("Desarrollo Java");
        assertNotNull(pf);
        assertEquals("Desarrollo Java", pf.getNombre());
        assertEquals("docente1", pf.getNicknameDocente());

        // Seleccionar Curso
        DtCurso curso = ctrlPrograma.seleccionarCurso("Prog1");
        assertNotNull(curso);
        assertEquals("Prog1", curso.getNombre());

        // Agregar Curso a Programa
        ctrlPrograma.agregarCursoAPrograma("Desarrollo Java", "Prog1");
        DTProgramaFormacion programaConCurso = ctrlPrograma.consultarPrograma("Desarrollo Java");
        assertEquals(List.of("INCO"), programaConCurso.getInstitutos());
    }

    @Test
    @Order(9)
    @DisplayName("ProgramaFormacion: Excepciones")
    void testProgramaFormacionExcepciones() {
        LocalDate ahora = LocalDate.now();

        // Crear duplicado
        assertThrows(Exception.class, ()
                -> ctrlPrograma.crearProgramaFormacion("docente1", "Desarrollo Java", "Desc", ahora, ahora, ahora,"")
        );

        // Seleccionar programa inexistente
        assertThrows(Exception.class, () -> ctrlPrograma.seleccionarPrograma("ProgramaInexistente"));

        // Seleccionar curso inexistente
        assertThrows(Exception.class, () -> ctrlPrograma.seleccionarCurso("CursoInexistente"));
    }
}
