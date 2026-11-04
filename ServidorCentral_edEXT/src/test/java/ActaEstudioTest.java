import com.grupo3_mat.edEXT.Logica.DataTypes.DTResultadoEstudio;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActaEstudioTest {

    @Test
    void resultadoDeEstudioAplicaNotaMinimaDeSeis() {
        assertTrue(new DTResultadoEstudio("Curso", "Edición aprobada", 6, LocalDate.now()).isAprobado());
        assertTrue(new DTResultadoEstudio("Curso", "Edición sobresaliente", 12, LocalDate.now()).isAprobado());
        assertFalse(new DTResultadoEstudio("Curso", "Edición no aprobada", 5, LocalDate.now()).isAprobado());
        assertFalse(new DTResultadoEstudio("Curso", null, null, null).isAprobado());
    }

    @Test
    void resultadoSinEvaluacionSeIdentificaComoNoRegistrado() {
        DTResultadoEstudio resultado = new DTResultadoEstudio("Curso", null, null, null);

        assertFalse(resultado.isTieneResultado());
        assertFalse(resultado.isAprobado());
    }
}
