import com.grupo3_mat.edEXT.Logica.Clases.Curso;
import com.grupo3_mat.edEXT.Logica.Clases.Estudiante;
import com.grupo3_mat.edEXT.Logica.Clases.ValoracionCurso;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValoracionesCursoTest {

    @Test
    void promedioRedondeadoYConteoIncluyenTodosLosPuntajes() {
        LocalDate hoy = LocalDate.now();
        Curso curso = new Curso(null, "Curso", "", 1, 1, 1, "", hoy);
        Estudiante estudianteUno = estudiante("uno");
        Estudiante estudianteDos = estudiante("dos");
        Estudiante estudianteTres = estudiante("tres");
        curso.agregarValoracion(new ValoracionCurso(estudianteUno, curso, 1));
        curso.agregarValoracion(new ValoracionCurso(estudianteDos, curso, 2));
        curso.agregarValoracion(new ValoracionCurso(estudianteTres, curso, 5));

        assertEquals(new BigDecimal("2.67"), curso.obtenerPuntajePromedio());
        assertEquals(Map.of(1, 1, 2, 1, 3, 0, 4, 0, 5, 1), curso.obtenerConteoValoraciones());
    }

    @Test
    void promedioDeCursoSinValoracionesEsCeroConDosDecimales() {
        Curso curso = new Curso(null, "Curso", "", 1, 1, 1, "", LocalDate.now());

        assertEquals(new BigDecimal("0.00"), curso.obtenerPuntajePromedio());
        assertEquals(Map.of(1, 0, 2, 0, 3, 0, 4, 0, 5, 0), curso.obtenerConteoValoraciones());
    }

    @Test
    void valoracionSoloAceptaPuntajesEntreUnoYCinco() {
        LocalDate hoy = LocalDate.now();
        Curso curso = new Curso(null, "Curso", "", 1, 1, 1, "", hoy);
        Estudiante estudiante = estudiante("estudiante");

        assertThrows(IllegalArgumentException.class, () -> new ValoracionCurso(estudiante, curso, 0));
        assertThrows(IllegalArgumentException.class, () -> new ValoracionCurso(estudiante, curso, 6));
    }

    private Estudiante estudiante(String nickname) {
        return new Estudiante(nickname, "Nombre", "Apellido", nickname + "@correo", LocalDate.now(), "", "");
    }
}
