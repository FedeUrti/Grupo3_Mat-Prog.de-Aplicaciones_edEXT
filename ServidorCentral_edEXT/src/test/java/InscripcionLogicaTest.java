import com.grupo3_mat.edEXT.Logica.Clases.Curso;
import com.grupo3_mat.edEXT.Logica.Clases.EdicionCurso;
import com.grupo3_mat.edEXT.Logica.Clases.EstadoInscripcion;
import com.grupo3_mat.edEXT.Logica.Clases.Estudiante;
import com.grupo3_mat.edEXT.Logica.Clases.InscripcionEC;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InscripcionLogicaTest {

    @Test
    void prioridadCuentaSoloRechazosDeEdicionesFinalizadasDelMismoCurso() {
        LocalDate hoy = LocalDate.now();
        Curso curso = new Curso(null, "Curso A", "", 1, 1, 1, "", hoy);
        Curso otroCurso = new Curso(null, "Curso B", "", 1, 1, 1, "", hoy);
        Estudiante estudiante = new Estudiante("estudiante", "Nombre", "Apellido", "correo", hoy, "", "");

        InscripcionEC rechazadaFinalizada = inscripcion(estudiante, curso, "Edición anterior", hoy.minusDays(10), EstadoInscripcion.RECHAZADA);
        InscripcionEC rechazadaVigente = inscripcion(estudiante, curso, "Edición vigente", hoy.plusDays(5), EstadoInscripcion.RECHAZADA);
        InscripcionEC rechazadaOtroCurso = inscripcion(estudiante, otroCurso, "Otra edición", hoy.minusDays(10), EstadoInscripcion.RECHAZADA);
        estudiante.agregarInscripcion(rechazadaFinalizada);
        estudiante.agregarInscripcion(rechazadaVigente);
        estudiante.agregarInscripcion(rechazadaOtroCurso);

        assertEquals(1, estudiante.calcularIpr("Curso A"));
        assertEquals(0.5, estudiante.calcularPrioridadInscripcion("Curso A"));
    }

    @Test
    void inscripcionGuardaEnlaceDeVideoHttpValidoYPermiteDejarloVacio() {
        LocalDate hoy = LocalDate.now();
        Curso curso = new Curso(null, "Curso A", "", 1, 1, 1, "", hoy);
        Estudiante estudiante = new Estudiante("estudiante", "Nombre", "Apellido", "correo", hoy, "", "");
        EdicionCurso edicion = new EdicionCurso("Edición", hoy, hoy.plusDays(1), 10, hoy, curso);
        InscripcionEC inscripcion = new InscripcionEC(
                estudiante, edicion, hoy, 0.0, " https://youtu.be/abcdefghijk ");

        assertEquals("https://youtu.be/abcdefghijk", inscripcion.getEnlaceVideo());
        inscripcion.setEnlaceVideo(" ");
        assertNull(inscripcion.getEnlaceVideo());
    }

    @Test
    void inscripcionRechazaEnlacesQueNoSeanHttpOHttps() {
        LocalDate hoy = LocalDate.now();
        Curso curso = new Curso(null, "Curso A", "", 1, 1, 1, "", hoy);
        Estudiante estudiante = new Estudiante("estudiante", "Nombre", "Apellido", "correo", hoy, "", "");
        EdicionCurso edicion = new EdicionCurso("Edición", hoy, hoy.plusDays(1), 10, hoy, curso);
        InscripcionEC inscripcion = new InscripcionEC(estudiante, edicion, hoy);

        assertThrows(IllegalArgumentException.class, () -> inscripcion.setEnlaceVideo("javascript:alert(1)"));
        assertThrows(IllegalArgumentException.class, () -> inscripcion.setEnlaceVideo("no es un enlace"));
    }

    @Test
    void evaluacionAceptaValoresEntreCeroYDoce() {
        InscripcionEC inscripcion = inscripcionSinEstado("estudiante", "Curso", "Edición");

        inscripcion.setEvaluacion(0);
        assertEquals(0, inscripcion.getEvaluacion());
        inscripcion.setEvaluacion(1);
        assertEquals(1, inscripcion.getEvaluacion());
        assertEquals(LocalDate.now(), inscripcion.getFechaEvaluacion());
        inscripcion.setEvaluacion(12);
        assertEquals(12, inscripcion.getEvaluacion());
        assertEquals(LocalDate.now(), inscripcion.getFechaEvaluacion());
        inscripcion.setEvaluacion(null);
        assertNull(inscripcion.getFechaEvaluacion());
        assertThrows(IllegalArgumentException.class, () -> inscripcion.setEvaluacion(-1));
        assertThrows(IllegalArgumentException.class, () -> inscripcion.setEvaluacion(13));
    }

    @Test
    void sePuedeEvaluarInscripcionAceptadaDeEdicionAntiguaMientrasPermanezcaAbierta() {
        LocalDate hoy = LocalDate.of(2010, 3, 15);
        Curso curso = new Curso(null, "Curso", "", 1, 1, 1, "", hoy);
        EdicionCurso edicion = new EdicionCurso(
                "Edición antigua", hoy, LocalDate.of(2010, 7, 7), 10, hoy, curso);
        Estudiante aceptado = estudiante("aceptado");
        Estudiante pendiente = estudiante("pendiente");
        Estudiante rechazado = estudiante("rechazado");
        InscripcionEC inscripcionAceptada = new InscripcionEC(aceptado, edicion, hoy);
        inscripcionAceptada.setEstado(EstadoInscripcion.ACEPTADA);
        InscripcionEC inscripcionPendiente = new InscripcionEC(pendiente, edicion, hoy);
        InscripcionEC inscripcionRechazada = new InscripcionEC(rechazado, edicion, hoy);
        inscripcionRechazada.setEstado(EstadoInscripcion.RECHAZADA);
        edicion.agregarInscripcion(inscripcionAceptada);
        edicion.agregarInscripcion(inscripcionPendiente);
        edicion.agregarInscripcion(inscripcionRechazada);

        assertThrows(IllegalStateException.class, () -> edicion.evaluarInscripcion("pendiente", 8));
        assertThrows(IllegalStateException.class, () -> edicion.evaluarInscripcion("rechazado", 8));
        edicion.evaluarInscripcion("aceptado", 8);
        assertEquals(8, inscripcionAceptada.getEvaluacion());
        edicion.evaluarInscripcion("aceptado", 12);
        assertEquals(12, inscripcionAceptada.getEvaluacion());
        edicion.cerrar();
        assertTrue(edicion.isCerrada());
    }

    @Test
    void cierreExigeEvaluarTodasLasInscripcionesAceptadasPeroIgnoraLasDemas() {
        LocalDate hoy = LocalDate.now();
        Curso curso = new Curso(null, "Curso", "", 1, 1, 1, "", hoy);
        EdicionCurso edicion = new EdicionCurso("Edición", hoy, hoy.plusDays(1), 10, hoy, curso);
        Estudiante aceptado = estudiante("aceptado");
        Estudiante pendiente = estudiante("pendiente");
        Estudiante rechazado = estudiante("rechazado");
        InscripcionEC inscripcionAceptada = nuevaInscripcion(aceptado, edicion);
        inscripcionAceptada.setEstado(EstadoInscripcion.ACEPTADA);
        InscripcionEC inscripcionPendiente = nuevaInscripcion(pendiente, edicion);
        InscripcionEC inscripcionRechazada = nuevaInscripcion(rechazado, edicion);
        inscripcionRechazada.setEstado(EstadoInscripcion.RECHAZADA);
        edicion.agregarInscripcion(inscripcionAceptada);
        edicion.agregarInscripcion(inscripcionPendiente);
        edicion.agregarInscripcion(inscripcionRechazada);

        assertThrows(IllegalStateException.class, edicion::cerrar);
        edicion.evaluarInscripcion("aceptado", 10);
        edicion.cerrar();
        assertTrue(edicion.isCerrada());
        assertNull(inscripcionPendiente.getEvaluacion());
        assertNull(inscripcionRechazada.getEvaluacion());
        assertThrows(IllegalStateException.class, () -> edicion.evaluarInscripcion("aceptado", 11));
        assertThrows(IllegalStateException.class,
                () -> edicion.cambiarEstadoInscripcion("pendiente", EstadoInscripcion.ACEPTADA));
    }

    @Test
    void desistirEliminaLaInscripcionPendienteDeAmbasRelaciones() {
        LocalDate hoy = LocalDate.now();
        Curso curso = new Curso(null, "Curso", "", 1, 1, 1, "", hoy);
        Estudiante estudiante = estudiante("estudiante");
        EdicionCurso edicion = new EdicionCurso("Edición", hoy, hoy.plusDays(1), 10, hoy, curso);
        InscripcionEC inscripcion = nuevaInscripcion(estudiante, edicion);
        estudiante.agregarInscripcion(inscripcion);
        edicion.agregarInscripcion(inscripcion);

        assertEquals(inscripcion, edicion.desistirInscripcion("estudiante"));
        assertFalse(edicion.estaInscripto("estudiante"));
        assertFalse(estudiante.estaInscriptoAEdicion("Edición"));
    }

    @Test
    void desistirSoloSePermiteParaInscripcionesPendientesDeEdicionesAbiertas() {
        LocalDate hoy = LocalDate.now();
        Curso curso = new Curso(null, "Curso", "", 1, 1, 1, "", hoy);
        Estudiante aceptado = estudiante("aceptado");
        Estudiante rechazado = estudiante("rechazado");
        Estudiante pendiente = estudiante("pendiente");
        EdicionCurso edicion = new EdicionCurso("Edición", hoy, hoy.plusDays(1), 10, hoy, curso);
        InscripcionEC inscripcionAceptada = nuevaInscripcion(aceptado, edicion);
        inscripcionAceptada.setEstado(EstadoInscripcion.ACEPTADA);
        InscripcionEC inscripcionRechazada = nuevaInscripcion(rechazado, edicion);
        inscripcionRechazada.setEstado(EstadoInscripcion.RECHAZADA);
        InscripcionEC inscripcionPendiente = nuevaInscripcion(pendiente, edicion);
        edicion.agregarInscripcion(inscripcionAceptada);
        edicion.agregarInscripcion(inscripcionRechazada);
        edicion.agregarInscripcion(inscripcionPendiente);

        assertThrows(IllegalStateException.class, () -> edicion.desistirInscripcion("aceptado"));
        assertThrows(IllegalStateException.class, () -> edicion.desistirInscripcion("rechazado"));
        edicion.desistirInscripcion("pendiente");
        assertFalse(edicion.estaInscripto("pendiente"));

        EdicionCurso edicionCerrada = new EdicionCurso("Cerrada", hoy, hoy.plusDays(1), 10, hoy, curso);
        Estudiante estudianteCerrada = estudiante("estudianteCerrada");
        InscripcionEC inscripcionCerrada = nuevaInscripcion(estudianteCerrada, edicionCerrada);
        edicionCerrada.agregarInscripcion(inscripcionCerrada);
        edicionCerrada.cerrar();
        assertThrows(IllegalStateException.class,
                () -> edicionCerrada.desistirInscripcion("estudianteCerrada"));
    }

    @Test
    void soloPermiteTransicionarDeInscriptoAAceptadaORechazada() {
        LocalDate hoy = LocalDate.now();
        Curso curso = new Curso(null, "Curso A", "", 1, 1, 1, "", hoy);
        Estudiante estudiante = new Estudiante("estudiante", "Nombre", "Apellido", "correo", hoy, "", "");
        EdicionCurso edicion = new EdicionCurso("Edición", hoy, hoy.plusDays(1), 10, hoy, curso);
        InscripcionEC inscripcion = new InscripcionEC(estudiante, edicion, hoy);
        edicion.agregarInscripcion(inscripcion);

        edicion.cambiarEstadoInscripcion(estudiante.getNickname(), EstadoInscripcion.ACEPTADA);
        assertEquals(EstadoInscripcion.ACEPTADA, inscripcion.getEstado());
        assertThrows(IllegalStateException.class,
                () -> edicion.cambiarEstadoInscripcion(estudiante.getNickname(), EstadoInscripcion.RECHAZADA));
        assertThrows(IllegalArgumentException.class,
                () -> edicion.cambiarEstadoInscripcion("desconocido", EstadoInscripcion.RECHAZADA));
    }

    @Test
    void cupoSoloSeConsumeCuandoElDocenteAceptaLaInscripcion() {
        LocalDate hoy = LocalDate.now();
        Curso curso = new Curso(null, "Curso A", "", 1, 1, 1, "", hoy);
        EdicionCurso edicion = new EdicionCurso("Edición", hoy, hoy.plusDays(1), 1, hoy, curso);
        Estudiante pendiente = new Estudiante("pendiente", "Nombre", "Apellido", "correo", hoy, "", "");
        Estudiante aceptado = new Estudiante("aceptado", "Nombre", "Apellido", "correo", hoy, "", "");
        InscripcionEC solicitudPendiente = new InscripcionEC(pendiente, edicion, hoy);
        InscripcionEC solicitudAceptada = new InscripcionEC(aceptado, edicion, hoy);
        edicion.agregarInscripcion(solicitudPendiente);
        edicion.agregarInscripcion(solicitudAceptada);

        assertEquals(1, edicion.getCupoDisponible());
        assertTrue(edicion.tieneCupoDisponible());

        edicion.cambiarEstadoInscripcion("aceptado", EstadoInscripcion.ACEPTADA);
        assertEquals(0, edicion.getCupoDisponible());
        assertFalse(edicion.tieneCupoDisponible());
        assertThrows(IllegalStateException.class,
                () -> edicion.cambiarEstadoInscripcion("pendiente", EstadoInscripcion.ACEPTADA));
        assertEquals(EstadoInscripcion.RECHAZADA, solicitudPendiente.getEstado());
    }

    @Test
    void alCompletarElCupoLasSolicitudesPendientesRestantesSeRechazanAutomaticamente() {
        LocalDate hoy = LocalDate.now();
        Curso curso = new Curso(null, "Curso A", "", 1, 1, 1, "", hoy);
        EdicionCurso edicion = new EdicionCurso("Edición", hoy, hoy.plusDays(1), 5, hoy, curso);
        List<InscripcionEC> solicitudes = new java.util.ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            Estudiante estudiante = new Estudiante("estudiante" + i, "Nombre", "Apellido", "correo", hoy, "", "");
            InscripcionEC solicitud = new InscripcionEC(estudiante, edicion, hoy);
            solicitudes.add(solicitud);
            edicion.agregarInscripcion(solicitud);
        }

        for (int i = 0; i < 5; i++) {
            edicion.cambiarEstadoInscripcion("estudiante" + (i + 1), EstadoInscripcion.ACEPTADA);
        }

        assertEquals(5, solicitudes.stream()
                .filter(solicitud -> solicitud.getEstado() == EstadoInscripcion.ACEPTADA)
                .count());
        assertEquals(EstadoInscripcion.RECHAZADA, solicitudes.get(5).getEstado());
        assertEquals(0, edicion.getCupoDisponible());
    }

    @Test
    void rechazarInscripcionPendienteNoCambiaLosCupos() {
        LocalDate hoy = LocalDate.now();
        Curso curso = new Curso(null, "Curso A", "", 1, 1, 1, "", hoy);
        EdicionCurso edicion = new EdicionCurso("Edición", hoy, hoy.plusDays(1), 1, hoy, curso);
        Estudiante estudiante = new Estudiante("estudiante", "Nombre", "Apellido", "correo", hoy, "", "");
        edicion.agregarInscripcion(new InscripcionEC(estudiante, edicion, hoy));

        edicion.cambiarEstadoInscripcion("estudiante", EstadoInscripcion.RECHAZADA);

        assertEquals(1, edicion.getCupoDisponible());
        assertTrue(edicion.tieneCupoDisponible());
    }

    @Test
    void tresSolicitudesConDosRechazosYUnaAceptacionDejanDiecinueveDeVeinteCupos() {
        LocalDate hoy = LocalDate.now();
        Curso curso = new Curso(null, "Curso A", "", 1, 1, 1, "", hoy);
        EdicionCurso edicion = new EdicionCurso("Edición", hoy, hoy.plusDays(1), 20, hoy, curso);
        Estudiante aceptado = new Estudiante("aceptado", "Nombre", "Apellido", "correo", hoy, "", "");
        Estudiante rechazadoUno = new Estudiante("rechazado1", "Nombre", "Apellido", "correo", hoy, "", "");
        Estudiante rechazadoDos = new Estudiante("rechazado2", "Nombre", "Apellido", "correo", hoy, "", "");
        edicion.agregarInscripcion(new InscripcionEC(aceptado, edicion, hoy));
        edicion.agregarInscripcion(new InscripcionEC(rechazadoUno, edicion, hoy));
        edicion.agregarInscripcion(new InscripcionEC(rechazadoDos, edicion, hoy));

        edicion.cambiarEstadoInscripcion("aceptado", EstadoInscripcion.ACEPTADA);
        edicion.cambiarEstadoInscripcion("rechazado1", EstadoInscripcion.RECHAZADA);
        edicion.cambiarEstadoInscripcion("rechazado2", EstadoInscripcion.RECHAZADA);

        assertEquals(19, edicion.getCupoDisponible());
    }

    private InscripcionEC inscripcion(Estudiante estudiante, Curso curso, String nombreEdicion, LocalDate fechaFin, EstadoInscripcion estado) {
        LocalDate hoy = LocalDate.now();
        EdicionCurso edicion = new EdicionCurso(nombreEdicion, fechaFin.minusDays(30), fechaFin, 10, hoy, curso);
        InscripcionEC inscripcion = new InscripcionEC(estudiante, edicion, hoy);
        inscripcion.setEstado(estado);
        return inscripcion;
    }

    private InscripcionEC inscripcionSinEstado(String nickname, String nombreCurso, String nombreEdicion) {
        LocalDate hoy = LocalDate.now();
        Curso curso = new Curso(null, nombreCurso, "", 1, 1, 1, "", hoy);
        EdicionCurso edicion = new EdicionCurso(nombreEdicion, hoy, hoy.plusDays(1), 10, hoy, curso);
        return nuevaInscripcion(estudiante(nickname), edicion);
    }

    private InscripcionEC nuevaInscripcion(Estudiante estudiante, EdicionCurso edicion) {
        return new InscripcionEC(estudiante, edicion, LocalDate.now());
    }

    private Estudiante estudiante(String nickname) {
        return new Estudiante(nickname, "Nombre", "Apellido", nickname + "@correo", LocalDate.now(), "", "");
    }
}