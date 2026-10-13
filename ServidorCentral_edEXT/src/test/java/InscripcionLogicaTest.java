import com.grupo3_mat.edEXT.Logica.Clases.Curso;
import com.grupo3_mat.edEXT.Logica.Clases.EdicionCurso;
import com.grupo3_mat.edEXT.Logica.Clases.EstadoInscripcion;
import com.grupo3_mat.edEXT.Logica.Clases.Estudiante;
import com.grupo3_mat.edEXT.Logica.Clases.InscripcionEC;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        assertEquals(EstadoInscripcion.INSCRIPTO, solicitudPendiente.getEstado());
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
}