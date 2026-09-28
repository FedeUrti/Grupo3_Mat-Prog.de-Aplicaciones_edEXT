import com.grupo3_mat.edEXT.Logica.Clases.Curso;
import com.grupo3_mat.edEXT.Logica.Clases.EdicionCurso;
import com.grupo3_mat.edEXT.Logica.Clases.EstadoInscripcion;
import com.grupo3_mat.edEXT.Logica.Clases.Estudiante;
import com.grupo3_mat.edEXT.Logica.Clases.InscripcionEC;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    private InscripcionEC inscripcion(Estudiante estudiante, Curso curso, String nombreEdicion, LocalDate fechaFin, EstadoInscripcion estado) {
        LocalDate hoy = LocalDate.now();
        EdicionCurso edicion = new EdicionCurso(nombreEdicion, fechaFin.minusDays(30), fechaFin, 10, hoy, curso);
        InscripcionEC inscripcion = new InscripcionEC(estudiante, edicion, hoy);
        inscripcion.setEstado(estado);
        return inscripcion;
    }
}