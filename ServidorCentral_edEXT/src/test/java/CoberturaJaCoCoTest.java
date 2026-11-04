import com.grupo3_mat.edEXT.Logica.Clases.Curso;
import com.grupo3_mat.edEXT.Logica.Clases.EdicionCurso;
import com.grupo3_mat.edEXT.Logica.Clases.EstadoInscripcion;
import com.grupo3_mat.edEXT.Logica.Clases.Estudiante;
import com.grupo3_mat.edEXT.Logica.Clases.ValoracionCurso;
import com.grupo3_mat.edEXT.Logica.Clases.ValoracionCursoId;
import com.grupo3_mat.edEXT.Logica.Controladores.ControladorCategoria;
import com.grupo3_mat.edEXT.Logica.Controladores.ControladorCurso;
import com.grupo3_mat.edEXT.Logica.Controladores.ControladorEdicion;
import com.grupo3_mat.edEXT.Logica.Controladores.ControladorInstituto;
import com.grupo3_mat.edEXT.Logica.Controladores.ControladorProgramaFormacion;
import com.grupo3_mat.edEXT.Logica.Controladores.ControladorUsuario;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTEdicionCurso;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorCategoria;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorCurso;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorEdicion;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorInstituto;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoberturaJaCoCoTest {

    private final String sufijo = UUID.randomUUID().toString();
    private final LocalDate hoy = LocalDate.now();
    private String instituto;
    private String categoria;
    private String estudiante;
    private String docente;
    private String curso;
    private String cursoPrevia;
    private ControladorCurso cursos;
    private ControladorEdicion ediciones;
    private ControladorProgramaFormacion programas;

    @BeforeEach
    void prepararDatos() throws Exception {
        instituto = "Instituto-" + sufijo;
        categoria = "Categoria-" + sufijo;
        estudiante = "Estudiante-" + sufijo;
        docente = "Docente-" + sufijo;
        curso = "Curso-" + sufijo;
        cursoPrevia = "CursoPrevia-" + sufijo;

        new ControladorInstituto().altaInstituto(instituto);
        new ControladorCategoria().altaCategoria(categoria);
        ControladorUsuario usuarios = new ControladorUsuario();
        usuarios.altaUsuario(estudiante, "Nombre", "Estudiante", estudiante + "@test.com",
                hoy.minusYears(20), "", null, "clave");
        usuarios.altaUsuario(docente, "Nombre", "Docente", docente + "@test.com",
                hoy.minusYears(30), "", instituto, "clave");

        cursos = new ControladorCurso();
        cursos.altaCurso(instituto, cursoPrevia, "Previa", 1, 1, 1, "", hoy,
                null, null, "");
        cursos.altaCurso(instituto, curso, "Curso de prueba", 1, 1, 1, "", hoy,
                List.of(cursoPrevia), List.of(categoria), "");
        ediciones = new ControladorEdicion();
        programas = new ControladorProgramaFormacion();
    }

    @Test
    void cubreConsultasYValoracionesDeCursos() throws Exception {
        assertTrue(ManejadorCurso.getInstancia().listarCursosPorInstituto(instituto)
                .stream().anyMatch(resultado -> resultado.getNombre().equals(curso)));
        assertTrue(ManejadorCurso.getInstancia().listarCursosPorCategoria(categoria)
                .stream().anyMatch(resultado -> resultado.getNombre().equals(curso)));
        assertTrue(ManejadorCurso.getInstancia().listarCursos().stream()
                .anyMatch(resultado -> resultado.getNombre().equals(curso)));

        String edicion = "Edicion-" + sufijo;
        altaEdicion(edicion, curso, 0);
        ediciones.inscribirEstudianteAEdicion(estudiante, edicion, hoy);
        ediciones.cambiarEstadoInscripcion(docente, edicion, estudiante, EstadoInscripcion.ACEPTADA);

        ManejadorCurso manejador = ManejadorCurso.getInstancia();
        assertTrue(manejador.listarCursosValorables(estudiante).contains(curso));
        assertThrows(IllegalArgumentException.class,
                () -> manejador.agregarValoracionCurso(curso, estudiante, 0));
        assertThrows(IllegalArgumentException.class,
                () -> manejador.agregarValoracionCurso("curso-inexistente", estudiante, 4));
        assertThrows(IllegalStateException.class,
                () -> manejador.agregarValoracionCurso(cursoPrevia, estudiante, 4));

        cursos.ingresarValoracion(curso, estudiante, 4);
        assertEquals(4, cursos.consultarValoracion(curso, estudiante));
        assertEquals(4, cursos.obtenerValoracionEstudiante(curso, estudiante));
        assertEquals(4, manejador.buscarValoracionCurso(curso, estudiante).getPuntaje());
        assertNull(cursos.obtenerValoracionEstudiante(cursoPrevia, estudiante));
        assertNull(manejador.buscarValoracionCurso(cursoPrevia, estudiante));
        assertEquals(4, cursos.obtenerPuntajePromedioCurso(curso).intValue());
        assertEquals(1, cursos.obtenerConteoValoracionesCurso(curso).get(4));
        assertThrows(IllegalStateException.class,
                () -> manejador.agregarValoracionCurso(curso, estudiante, 5));

        assertTrue(cursos.listarCursosValorables(estudiante).contains(curso));
        assertTrue(cursos.consultarCurso(curso).getPrevias().contains(cursoPrevia));
        assertEquals(1, cursos.consultarCursos(List.of(curso)).size());
        assertTrue(cursos.consultarCursos(null).isEmpty());
        assertTrue(cursos.consultarCursos(List.of()).isEmpty());
        assertThrows(IllegalArgumentException.class,
                () -> cursos.consultarCursos(java.util.Collections.nCopies(101, curso)));
        assertThrows(Exception.class, () -> cursos.consultarCursos(List.of("curso-inexistente")));
        assertEquals(edicion, cursos.obtenerEdicionVigente(curso, hoy));
        assertThrows(Exception.class, () -> cursos.obtenerEdicionVigente("curso-inexistente", hoy));
        assertThrows(IllegalArgumentException.class, () -> cursos.ingresarValoracion(curso, estudiante, 6));
        assertThrows(Exception.class, () -> cursos.consultarValoracion(cursoPrevia, estudiante));
        assertThrows(Exception.class, () -> cursos.obtenerPuntajePromedioCurso("curso-inexistente"));
        assertThrows(Exception.class, () -> cursos.obtenerConteoValoracionesCurso("curso-inexistente"));
    }

    @Test
    void cubreOperacionesYConsultasDeEdiciones() throws Exception {
        String aceptada = "EdicionAceptada-" + sufijo;
        altaEdicion(aceptada, curso, 0);
        ediciones.inscribirEstudianteAEdicion(estudiante, aceptada, hoy, "https://example.com/video");
        assertEquals(1, ediciones.listarEdicionesDeEstudiante(estudiante).size());
        assertEquals(1, ediciones.listarInscripcionesAEdicion(
                docente, aceptada,
                com.grupo3_mat.edEXT.Logica.DataTypes.CriterioOrdenInscripciones.FECHA_INSCRIPCION).size());
        ediciones.cambiarEstadoInscripcion(docente, aceptada, estudiante, EstadoInscripcion.ACEPTADA);
        assertThrows(IllegalStateException.class, () -> ediciones.consultarEvaluacionesEdicion(docente, aceptada));
        ediciones.evaluarInscripcion(docente, aceptada, estudiante, 10);
        ediciones.cerrarEdicion(docente, aceptada);
        assertEquals(1, ediciones.consultarEvaluacionesEdicion(docente, aceptada).size());
        assertEquals(1, ediciones.listarResultadosInscripcionesEstudiante(estudiante).size());
        assertThrows(IllegalStateException.class,
                () -> ManejadorEdicion.getInstancia().evaluarInscripcion(aceptada, estudiante, 9));
        assertThrows(IllegalArgumentException.class,
                () -> ManejadorEdicion.getInstancia().evaluarInscripcion("edicion-inexistente", estudiante, 9));

        String pendiente = "EdicionPendiente-" + sufijo;
        altaEdicion(pendiente, curso, 0);
        ediciones.inscribirEstudianteAEdicion(estudiante, pendiente, null);
        ediciones.desistirInscripcionEstudiante(estudiante, pendiente);
        assertFalse(ManejadorEdicion.getInstancia().buscarEdicion(pendiente)
                .estaInscripto(estudiante));
        assertThrows(IllegalArgumentException.class,
                () -> ediciones.desistirInscripcionEstudiante(docente, pendiente));
        assertThrows(IllegalArgumentException.class,
                () -> ManejadorEdicion.getInstancia().desistirInscripcion(estudiante, "edicion-inexistente"));
        assertThrows(IllegalArgumentException.class,
                () -> ManejadorEdicion.getInstancia().cerrarEdicion("edicion-inexistente"));
        assertTrue(ediciones.listarEdicionesDeEstudiante(docente).isEmpty());
        assertTrue(ediciones.listarEdicionesPorCurso(curso).contains(aceptada));
        assertTrue(ediciones.listarEdicionesPorCurso("curso-inexistente").isEmpty());
        assertNotNull(ediciones.mostrarDetalleEdicion(aceptada));
        assertNull(ediciones.mostrarDetalleEdicion("edicion-inexistente"));
        assertEquals(curso, ediciones.obtenerCursoDeEdicion(aceptada));
        assertNull(ediciones.obtenerCursoDeEdicion("edicion-inexistente"));

        ManejadorEdicion manejador = ManejadorEdicion.getInstancia();
        assertTrue(manejador.listarEdiciones().stream().anyMatch(edicion -> edicion.getNombre().equals(aceptada)));
        assertTrue(manejador.buscarEdicionesPorDocente(docente).stream()
                .anyMatch(edicion -> edicion.getNombre().equals(aceptada)));
        assertThrows(Exception.class,
                () -> ediciones.evaluarInscripcion(docente, "edicion-inexistente", estudiante, 9));
        assertThrows(Exception.class,
                () -> ediciones.cerrarEdicion(docente, "edicion-inexistente"));

        manejador.eliminarEdicion(pendiente);
        manejador.eliminarEdicion("edicion-inexistente");
        assertNull(manejador.buscarEdicion(pendiente));

        assertThrows(IllegalArgumentException.class, () -> manejador.modificarEdicion(null));
        String sinCurso = "EdicionSinCurso-" + sufijo;
        manejador.agregarEdicion(new EdicionCurso(sinCurso, hoy, hoy.plusDays(1), 0, hoy, null));
        manejador.eliminarEdicion(sinCurso);
        assertNull(manejador.buscarEdicion(sinCurso));
    }

    @Test
    void cubreActasDeEstudioYClaveCompuesta() throws Exception {
        String programa = "Programa-" + sufijo;
        programas.crearProgramaFormacion(docente, programa, "Programa de prueba",
                hoy.minusDays(1), hoy.plusDays(30), hoy, "");
        programas.agregarCursoAPrograma(docente, programa, curso);
        programas.inscribirEstudianteAPrograma(estudiante, programa, hoy);
        String programaLegacy = programa + "-legacy";
        programas.crearProgramaFormacion(docente, programaLegacy, "Programa legacy",
                hoy.minusDays(1), hoy.plusDays(30), hoy, "");
        programas.agregarCursoAPrograma(programaLegacy, cursoPrevia);

        String edicion = "EdicionActa-" + sufijo;
        altaEdicion(edicion, curso, 0);
        ediciones.inscribirEstudianteAEdicion(estudiante, edicion, hoy);
        ediciones.cambiarEstadoInscripcion(docente, edicion, estudiante, EstadoInscripcion.ACEPTADA);
        ediciones.evaluarInscripcion(docente, edicion, estudiante, 10);
        ediciones.cerrarEdicion(docente, edicion);

        assertEquals(1, programas.consultarResultadosActa(estudiante, programa).size());
        assertThrows(IllegalArgumentException.class,
                () -> programas.consultarResultadosActa(docente, programa));
        assertThrows(IllegalArgumentException.class,
                () -> programas.consultarResultadosActa(estudiante, "programa-inexistente"));
        String otroEstudiante = estudiante + "-otro";
        new ControladorUsuario().altaUsuario(otroEstudiante, "Otro", "Estudiante",
                otroEstudiante + "@test.com", hoy.minusYears(20), "", null, "clave");
        assertThrows(SecurityException.class,
                () -> programas.consultarResultadosActa(otroEstudiante, programa));
        assertEquals(programa, programas.seleccionarPrograma(programa).getNombre());
        assertTrue(programas.listarProgramas().stream().anyMatch(item -> item.getNombre().equals(programa)));
        assertEquals(curso, programas.seleccionarCurso(curso).getNombre());
        assertThrows(Exception.class, () -> programas.seleccionarCurso("curso-inexistente"));
        assertThrows(Exception.class, () -> programas.crearProgramaFormacion(
                programa, "sin responsable", hoy, hoy.plusDays(1), hoy, ""));
        assertThrows(Exception.class, () -> programas.crearProgramaFormacion(
                " ", programa + "-sin-docente", "", hoy, hoy.plusDays(1), hoy, ""));
        assertThrows(Exception.class, () -> programas.crearProgramaFormacion(
                estudiante, programa + "-docente-invalido", "", hoy, hoy.plusDays(1), hoy, ""));
        assertThrows(IllegalArgumentException.class,
                () -> programas.agregarCursoAPrograma(" ", programa, curso));

        ValoracionCursoId id = new ValoracionCursoId(curso, estudiante);
        ValoracionCursoId igual = new ValoracionCursoId(curso, estudiante);
        ValoracionCursoId distinto = new ValoracionCursoId(cursoPrevia, estudiante);
        assertEquals(curso, id.getCursoNombre());
        assertEquals(estudiante, id.getEstudianteNickname());
        assertEquals(id, igual);
        assertEquals(id.hashCode(), igual.hashCode());
        assertFalse(id.equals(distinto));
        assertFalse(id.equals(null));
        assertFalse(id.equals("otro tipo"));
        id.setCursoNombre(cursoPrevia);
        id.setEstudianteNickname(docente);
        assertEquals(cursoPrevia, id.getCursoNombre());
        assertEquals(docente, id.getEstudianteNickname());

        assertThrows(IllegalArgumentException.class, () -> new ValoracionCurso(null, null, 3));
        ValoracionCurso valoracion = new ValoracionCurso();
        Curso cursoModelo = new Curso(null, curso, "", 1, 1, 1, "", hoy);
        Estudiante estudianteModelo = new Estudiante(estudiante, "Nombre", "Apellido",
                estudiante + "@modelo.test", hoy.minusYears(20), "", "clave");
        valoracion.setCurso(cursoModelo);
        valoracion.setEstudiante(estudianteModelo);
        valoracion.setPuntaje(3);
        assertEquals(curso, valoracion.getId().getCursoNombre());
        assertEquals(estudiante, valoracion.getId().getEstudianteNickname());
        valoracion.setCurso(null);
        valoracion.setEstudiante(null);
        assertEquals(3, valoracion.getPuntaje());
        assertNull(valoracion.getCurso());
        assertNull(valoracion.getEstudiante());
        assertThrows(IllegalArgumentException.class, () -> valoracion.setPuntaje(0));
        valoracion.setPuntaje(5);
        assertEquals(5, valoracion.getPuntaje());
    }

    private void altaEdicion(String nombre, String nombreCurso, int cupo) throws Exception {
        DTEdicionCurso datos = new DTEdicionCurso(nombre, hoy.minusDays(1), hoy.plusDays(1),
                cupo, cupo, hoy.minusDays(2), List.of(docente));
        ediciones.altaEdicionCurso(nombreCurso, datos);
    }
}
