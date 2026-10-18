import com.grupo3_mat.edEXT.Logica.Clases.Categoria;
import com.grupo3_mat.edEXT.Logica.Clases.Curso;
import com.grupo3_mat.edEXT.Logica.Clases.Docente;
import com.grupo3_mat.edEXT.Logica.Clases.EdicionCurso;
import com.grupo3_mat.edEXT.Logica.Clases.EstadoInscripcion;
import com.grupo3_mat.edEXT.Logica.Clases.Estudiante;
import com.grupo3_mat.edEXT.Logica.Clases.InscripcionEC;
import com.grupo3_mat.edEXT.Logica.Clases.InscripcionECId;
import com.grupo3_mat.edEXT.Logica.Clases.InscripcionPF;
import com.grupo3_mat.edEXT.Logica.Clases.InscripcionPFId;
import com.grupo3_mat.edEXT.Logica.Clases.Instituto;
import com.grupo3_mat.edEXT.Logica.Clases.ProgramaFormacion;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTCategoria;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTEdicionCurso;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTInscripcionEdicion;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTProgramaFormacion;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtCurso;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtDocente;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtEstudiante;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtInstituto;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtUsuario;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelosLogicaTest {

    private static final LocalDate HOY = LocalDate.of(2026, 10, 17);

    @Test
    void cursoExponeSusDatosYAdministraSusRelaciones() {
        Instituto instituto = new Instituto("INCO");
        Curso curso = new Curso(instituto, "Curso", "Descripcion", 4, 20, 5, "url", HOY, "curso.png");
        curso.setNombre("Curso nuevo");
        curso.setInstituto(null);
        curso.setDescripcion("Nueva descripcion");
        curso.setDuracion(6);
        curso.setCantHoras(30);
        curso.setCreditos(8);
        curso.setUrl("nueva-url");
        curso.setFecha(HOY.plusDays(1));
        curso.setImagenPath("nuevo.png");
        curso.setHorasSemanales(12);
        curso.setFechaRegistro(HOY);

        assertAll(
                () -> assertEquals("Curso nuevo", curso.getNombre()),
                () -> assertNull(curso.getInstituto()),
                () -> assertEquals("Nueva descripcion", curso.getDescripcion()),
                () -> assertEquals(6, curso.getDuracion()),
                () -> assertEquals(12, curso.getCantHoras()),
                () -> assertEquals(8, curso.getCreditos()),
                () -> assertEquals("nueva-url", curso.getUrl()),
                () -> assertEquals(HOY, curso.getFecha()),
                () -> assertEquals("nuevo.png", curso.getImagenPath()),
                () -> assertEquals(12, curso.getHorasSemanales()),
                () -> assertEquals(HOY, curso.getFechaRegistro()));

        Curso previa = new Curso(null, "Previa", "", 1, 1, 1, "", HOY);
        curso.setPrevias((List<Curso>) null);
        curso.agregarPrevia(null);
        curso.agregarPrevia(previa);
        curso.agregarPrecorrelativa(previa);
        assertEquals(List.of(previa), curso.getPrecorrelativas());
        curso.setPrevias((java.util.Set<Curso>) null);
        assertNull(curso.getPrevias());
        assertNull(curso.obtenerProximaEdicion(HOY));
        curso.setPrevias(List.of(previa));

        Categoria categoria = new Categoria("Social");
        curso.agregarCategoria(null);
        curso.agregarCategoria(categoria);
        assertTrue(curso.getCategorias().contains(categoria));
        assertTrue(categoria.getCursos().contains(curso));
        curso.setCategorias((List<Categoria>) null);
        assertTrue(curso.getCategorias().isEmpty());
        curso.setCategorias(new HashSet<>(List.of(categoria)));

        curso.agregarEdicion(null);
        EdicionCurso sinFecha = new EdicionCurso("Sin fecha", null, HOY, 1, HOY, curso);
        EdicionCurso tardia = new EdicionCurso("Tardia", HOY.plusDays(10), HOY.plusDays(20), 1, HOY, curso);
        EdicionCurso proxima = new EdicionCurso("Proxima", HOY.plusDays(2), HOY.plusDays(5), 1, HOY, curso);
        curso.agregarEdicion(sinFecha);
        curso.agregarEdicion(tardia);
        curso.agregarEdicion(proxima);
        assertSame(curso, proxima.getCurso());
        assertSame(proxima, curso.obtenerProximaEdicion(HOY));
        assertNull(curso.obtenerProximaEdicion(null));
        assertNull(new Curso().obtenerProximaEdicion(HOY));

        ProgramaFormacion programa = new ProgramaFormacion("Programa", "", HOY, HOY, HOY, "");
        curso.setProgramas(new HashSet<>(List.of(programa)));
        assertTrue(curso.getProgramas().contains(programa));
        curso.setEdiciones(new HashSet<>());
        assertTrue(curso.getEdiciones().isEmpty());
    }

    @Test
    void usuarioEstudianteYDocenteMantienenRelacionesBidireccionales() {
        Estudiante estudiante = new Estudiante("est", "Nombre", "Apellido", "correo", HOY, "img", "hash");
        estudiante.setNickname("estudiante");
        estudiante.setNombre("Ana");
        estudiante.setApellido("Perez");
        estudiante.setCorreo("ana@test.com");
        estudiante.setFechaNacimiento(HOY.minusYears(20));
        estudiante.setImagenPath("ana.png");
        estudiante.setPassword("nuevo-hash");
        estudiante.agregarSeguido(null);
        estudiante.agregarSeguidor(null);
        estudiante.seguirUsuario(estudiante);
        assertFalse(estudiante.sigueA(null));
        assertFalse(estudiante.sigueA("otro"));

        Estudiante seguido = new Estudiante("seguido", "Beto", "Diaz", "beto@test.com", HOY, "", "hash");
        estudiante.seguirUsuario(seguido);
        estudiante.seguirUsuario(seguido);
        assertTrue(estudiante.sigueA("seguido"));
        assertEquals(List.of("seguido"), estudiante.getNicknamesSeguidos());
        assertEquals(List.of("estudiante"), seguido.getNicknamesSeguidores());
        estudiante.dejarDeSeguirUsuario(null);
        estudiante.dejarDeSeguirUsuario(seguido);
        assertFalse(estudiante.sigueA("seguido"));
        assertTrue(seguido.getNicknamesSeguidores().isEmpty());
        estudiante.setSeguidos(new HashSet<>());
        estudiante.setSeguidores(new HashSet<>());
        assertEquals("Ana", estudiante.getNombre());
        assertEquals("Perez", estudiante.getApellido());
        assertEquals("ana@test.com", estudiante.getCorreo());
        assertEquals(HOY.minusYears(20), estudiante.getFechaNacimiento());
        assertEquals("ana.png", estudiante.getImagenPath());
        assertEquals("nuevo-hash", estudiante.getPassword());

        Instituto inco = new Instituto("INCO");
        Instituto imerl = new Instituto("IMERL");
        Docente docente = new Docente("doc", "Doc", "Uno", "doc@test.com", HOY, "", "hash", List.of(inco, imerl));
        docente.agregarInstituto(null);
        docente.agregarInstituto(inco);
        assertEquals(List.of(inco, imerl), docente.getInstitutos());
        assertSame(inco, docente.getInstituto());
        assertTrue(docente.perteneceAInstituto("INCO"));
        assertFalse(docente.perteneceAInstituto(null));
        assertFalse(docente.perteneceAInstituto("FING"));
        docente.setInstitutos(null);
        assertNull(docente.getInstituto());
        docente.setInstituto(inco);
        assertEquals(List.of(inco), docente.getInstitutos());
        docente.setInstituto(null);
        assertTrue(docente.getInstitutos().isEmpty());
        docente.setEdiciones(List.of());
        assertTrue(docente.getEdiciones().isEmpty());

        ProgramaFormacion programa = new ProgramaFormacion("EFI", "", HOY, HOY, HOY, "");
        docente.agregarPrograma(null);
        programa.setDocente(docente);
        programa.setDocente(docente);
        assertEquals(List.of(programa), docente.getProgramas());
        Docente otroDocente = new Docente();
        programa.setDocente(otroDocente);
        assertTrue(docente.getProgramas().isEmpty());
        assertEquals(List.of(programa), otroDocente.getProgramas());
        programa.setDocente(null);
        assertTrue(otroDocente.getProgramas().isEmpty());
    }

    @Test
    void programaEInscripcionesMantienenIdentidadYDatos() {
        Estudiante estudiante = new Estudiante("est", "Nombre", "Apellido", "correo", HOY, "", "hash");
        Curso curso = new Curso(new Instituto("INCO"), "Curso", "", 1, 2, 3, "", HOY);
        Categoria categoria = new Categoria("Educativo");
        curso.agregarCategoria(categoria);
        ProgramaFormacion programa = new ProgramaFormacion("Programa", "Descripcion", HOY, HOY.plusDays(1), HOY, "p.png");
        programa.setNombre("Programa nuevo");
        programa.setDescripcion("Nueva descripcion");
        programa.setFechaInicio(HOY.plusDays(2));
        programa.setFechaFin(HOY.plusDays(3));
        programa.setFechaAlta(HOY.plusDays(4));
        programa.setImagenPath("nuevo.png");
        programa.agregarCurso(null);
        programa.agregarCurso(curso);
        assertAll(
                () -> assertEquals("Programa nuevo", programa.getNombre()),
                () -> assertEquals("Nueva descripcion", programa.getDescripcion()),
                () -> assertEquals(HOY.plusDays(2), programa.getFechaInicio()),
                () -> assertEquals(HOY.plusDays(3), programa.getFechaFin()),
                () -> assertEquals(HOY.plusDays(4), programa.getFechaAlta()),
                () -> assertEquals("nuevo.png", programa.getImagenPath()),
                () -> assertTrue(programa.getCategorias().contains(categoria)),
                () -> assertEquals(1, programa.getInstitutos().size()));
        programa.setCursos(null);
        assertTrue(programa.getCategorias().isEmpty());
        assertTrue(programa.getInstitutos().isEmpty());
        programa.setCursos(Map.of("Curso", curso));

        InscripcionPF inscripcionPF = new InscripcionPF(estudiante, programa, HOY);
        assertEquals("Programa nuevo", inscripcionPF.getId().getProgramaNombre());
        assertEquals("est", inscripcionPF.getId().getEstudianteNickname());
        assertSame(estudiante, inscripcionPF.getEstudiante());
        assertSame(programa, inscripcionPF.getPrograma());
        assertEquals(HOY, inscripcionPF.getFechaInscripcion());
        inscripcionPF.setFechaInscripcion(HOY.plusDays(1));
        inscripcionPF.setEstudiante(null);
        inscripcionPF.setPrograma(null);
        assertEquals(HOY.plusDays(1), inscripcionPF.getFechaInscripcion());
        assertNull(inscripcionPF.getEstudiante());
        assertNull(inscripcionPF.getPrograma());
        estudiante.agregarInscripcionPrograma(null);
        estudiante.agregarInscripcionPF(inscripcionPF);
        assertFalse(estudiante.estaInscriptoAPrograma("Programa nuevo"));
        inscripcionPF.setPrograma(programa);
        estudiante.agregarInscripcionPrograma(inscripcionPF);
        assertTrue(estudiante.estaInscriptoAPrograma("Programa nuevo"));
        assertEquals(List.of(inscripcionPF), estudiante.getInscripcionesProgramas());

        InscripcionPFId idPrograma = new InscripcionPFId("P", "E");
        InscripcionPFId otroIdPrograma = new InscripcionPFId("P", "E");
        assertEquals(idPrograma, otroIdPrograma);
        assertEquals(idPrograma.hashCode(), otroIdPrograma.hashCode());
        assertNotEquals(idPrograma, new InscripcionPFId("P2", "E"));
        assertNotEquals(idPrograma, null);
        assertNotEquals(idPrograma, new Object());
        idPrograma.setProgramaNombre("P2");
        idPrograma.setEstudianteNickname("E2");
        assertEquals("P2", idPrograma.getProgramaNombre());
        assertEquals("E2", idPrograma.getEstudianteNickname());

        InscripcionECId idEdicion = new InscripcionECId("Edicion", "E");
        assertEquals(idEdicion, new InscripcionECId("Edicion", "E"));
        assertNotEquals(idEdicion, new InscripcionECId("Otra", "E"));
        assertNotEquals(idEdicion, null);
        assertNotEquals(idEdicion, new Object());
        idEdicion.setEdicionNombre("Nueva edicion");
        idEdicion.setEstudianteNickname("Nuevo estudiante");
        assertEquals("Nueva edicion", idEdicion.getEdicionNombre());
        assertEquals("Nuevo estudiante", idEdicion.getEstudianteNickname());

        EdicionCurso edicion = new EdicionCurso("Edicion", HOY, HOY.plusDays(2), 3, HOY, curso);
        InscripcionEC inscripcionEC = new InscripcionEC(estudiante, edicion, HOY, 0.5);
        assertEquals(HOY, inscripcionEC.getFecha());
        assertEquals(EstadoInscripcion.INSCRIPTO, inscripcionEC.getEstado());
        assertEquals(0.5, inscripcionEC.getPrioridad());
        estudiante.agregarInscripcion(null);
        estudiante.agregarInscripcion(inscripcionEC);
        assertTrue(estudiante.estaInscriptoAEdicion("Edicion"));
        inscripcionEC.setFechaInscripcion(HOY.plusDays(1));
        inscripcionEC.setEstado(EstadoInscripcion.ACEPTADA);
        inscripcionEC.setPrioridad(1.0);
        assertEquals(HOY.plusDays(1), inscripcionEC.getFechaInscripcion());
        assertEquals(EstadoInscripcion.ACEPTADA, inscripcionEC.getEstado());
        assertEquals(1.0, inscripcionEC.getPrioridad());
        assertSame(edicion, inscripcionEC.getEdicion());
        assertSame(estudiante, inscripcionEC.getEstudiante());
    }

    @Test
    void dtosConservanConstructoresCompatiblesYValoresNulos() {
        DtCurso curso = new DtCurso("Curso", "Descripcion", 2, 10, 3, "url", HOY, "INCO",
                List.of("Previa"), List.of("Edicion"), List.of("Programa"), List.of("Social"), "curso.png");
        assertAll(
                () -> assertEquals("Curso", curso.getNombre()),
                () -> assertEquals("Descripcion", curso.getDescripcion()),
                () -> assertEquals(2, curso.getDuracion()),
                () -> assertEquals(10, curso.getCantHoras()),
                () -> assertEquals(3, curso.getCreditos()),
                () -> assertEquals("url", curso.getUrl()),
                () -> assertEquals(HOY, curso.getFecha()),
                () -> assertEquals("INCO", curso.getNomInstituto()),
                () -> assertEquals("curso.png", curso.getImagenPath()),
                () -> assertEquals(List.of("Previa"), curso.getPrevias()),
                () -> assertEquals(List.of("Edicion"), curso.getEdiciones()),
                () -> assertEquals(List.of("Programa"), curso.getProgramas()),
                () -> assertEquals(List.of("Social"), curso.getCategorias()));
        DtCurso cursoCompatibilidad = new DtCurso("C", "", 1, 2, 3, "", HOY, "INCO", null, null, null);
        assertTrue(cursoCompatibilidad.getPrevias().isEmpty());
        assertTrue(cursoCompatibilidad.getCategorias().isEmpty());
        DtCurso cursoAnterior = new DtCurso("C", "", 1, 2, 3, "", HOY, "INCO", List.of("E"), List.of("P"));
        assertTrue(cursoAnterior.getPrevias().isEmpty());

        DtUsuario usuario = new DtUsuario("nick", "Nombre", "Apellido", "correo", HOY, "img", List.of("s"), List.of("a"));
        assertEquals("nick", usuario.getNickname());
        assertEquals("Nombre", usuario.getNombre());
        assertEquals("Apellido", usuario.getApellido());
        assertEquals("correo", usuario.getCorreo());
        assertEquals(HOY, usuario.getFechaNacimiento());
        assertEquals("img", usuario.getImagenPath());
        assertEquals(List.of("s"), usuario.getSeguidores());
        assertEquals(List.of("a"), usuario.getSeguidos());
        assertTrue(new DtUsuario("n", "", "", "", HOY, "").getSeguidores().isEmpty());

        DtDocente docente = new DtDocente("d", "", "", "", HOY, "", "INCO", List.of("E"));
        assertEquals("INCO", docente.getInstituto());
        assertEquals(List.of("INCO"), docente.getInstitutos());
        assertEquals(List.of("E"), docente.getEdiciones());
        DtDocente docenteVacio = new DtDocente("d", "", "", "", HOY, "", null, null, null, null, null);
        assertNull(docenteVacio.getInstituto());
        assertTrue(docenteVacio.getInstitutos().isEmpty());
        assertTrue(docenteVacio.getEdicionesAsociadas().isEmpty());
        assertTrue(docenteVacio.getProgramas().isEmpty());

        DTInscripcionEdicion datoInscripcion = new DTInscripcionEdicion("e", "Edicion", HOY, EstadoInscripcion.ACEPTADA, 0.5);
        DtEstudiante estudiante = new DtEstudiante("e", "", "", "", HOY, "", null, null,
                java.util.Arrays.asList(datoInscripcion, null), List.of("Programa"));
        assertEquals(java.util.Arrays.asList(datoInscripcion, null), estudiante.getInscripcionesEdicion());
        assertEquals(List.of("Edicion"), estudiante.getEdicionesInscripto());
        assertEquals(List.of("Programa"), estudiante.getProgramasInscripto());
        DtEstudiante estudianteCompatibilidad = new DtEstudiante("e", "", "", "", HOY, "", List.of("E"), List.of("P"));
        assertEquals(List.of("E"), estudianteCompatibilidad.getEdicionesInscripto());

        DTProgramaFormacion programa = new DTProgramaFormacion("P", "D", HOY, HOY.plusDays(1), HOY,
                List.of("C"), List.of("Categoria"), "img", "docente", List.of("INCO"));
        assertEquals("P", programa.getNombre());
        assertEquals("D", programa.getDescripcion());
        assertEquals(HOY, programa.getFechaInicio());
        assertEquals(HOY.plusDays(1), programa.getFechaFin());
        assertEquals(HOY, programa.getFechaAlta());
        assertEquals(List.of("C"), programa.getCursos());
        assertEquals(List.of("Categoria"), programa.getCategorias());
        assertEquals("img", programa.getImagenPath());
        assertEquals("docente", programa.getNicknameDocente());
        assertEquals(List.of("INCO"), programa.getInstitutos());
        assertEquals("P", programa.toString());
        assertTrue(new DTProgramaFormacion("P", "", HOY, HOY, HOY, null, null, "").getCursos().isEmpty());

        DTEdicionCurso edicion = new DTEdicionCurso("E", HOY, HOY.plusDays(1), 10, 5, HOY, List.of("doc"), "img");
        assertEquals("E", edicion.getNombre());
        assertEquals(HOY, edicion.getFechaInicio());
        assertEquals(HOY.plusDays(1), edicion.getFechaFin());
        assertEquals(10, edicion.getCupo());
        assertEquals(5, edicion.getCupoDisponible());
        assertEquals(HOY, edicion.getFechaPublicacion());
        assertEquals(List.of("doc"), edicion.getDocentes());
        assertEquals("img", edicion.getImagenPath());
        assertTrue(new DTEdicionCurso("E", HOY, HOY, 1, 1, HOY, null).getDocentes().isEmpty());

        assertEquals("e", datoInscripcion.getNicknameEstudiante());
        assertEquals("Edicion", datoInscripcion.getNombreEdicion());
        assertEquals(HOY, datoInscripcion.getFechaInscripcion());
        assertEquals(EstadoInscripcion.ACEPTADA, datoInscripcion.getEstado());
        assertEquals(0.5, datoInscripcion.getPrioridad());
        DTCategoria dtCategoria = new DTCategoria("Social");
        assertEquals("Social", dtCategoria.getNombre());
        assertEquals("Social", dtCategoria.toString());
        DtInstituto dtInstituto = new DtInstituto("INCO");
        dtInstituto.setNombre("IMERL");
        assertEquals("IMERL", dtInstituto.getNombre());
    }

        @Test
        void institutoYCategoriaExponenYActualizanSusRelaciones() {
                Instituto instituto = new Instituto("INCO");
                Docente docente = new Docente("d", "Doc", "Uno", "d@test.com", HOY, "", "hash", instituto);
                instituto.setNombre("IMERL");
                instituto.setDocentes(List.of(docente));
                assertEquals("IMERL", instituto.getNombre());
                assertEquals(List.of(docente), instituto.getDocentes());

                Categoria categoria = new Categoria("Social");
                Curso curso = new Curso();
                categoria.setNombre("Educativo");
                categoria.setCursos(new HashSet<>());
                categoria.agregarCurso(curso);
                assertEquals("Educativo", categoria.getNombre());
                assertTrue(categoria.getCursos().contains(curso));
                categoria.setCursos(null);
                assertNull(categoria.getCursos());
        }
}
