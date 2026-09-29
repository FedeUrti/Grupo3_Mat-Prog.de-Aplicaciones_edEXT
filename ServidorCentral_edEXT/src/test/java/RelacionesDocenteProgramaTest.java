import com.grupo3_mat.edEXT.Logica.Clases.Curso;
import com.grupo3_mat.edEXT.Logica.Clases.Docente;
import com.grupo3_mat.edEXT.Logica.Clases.Instituto;
import com.grupo3_mat.edEXT.Logica.Clases.ProgramaFormacion;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTProgramaFormacion;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtDocente;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RelacionesDocenteProgramaTest {

    @Test
    void docentePuedeTenerVariosInstitutosYProgramasDerivanLosInstitutosDeSusCursos() {
        Instituto institutoA = new Instituto("INCO");
        Instituto institutoB = new Instituto("IMERL");
        Docente docente = new Docente(
                "docente", "Nombre", "Apellido", "docente@test.com",
                LocalDate.now(), "", "hash", List.of(institutoA, institutoB)
        );
        ProgramaFormacion programa = new ProgramaFormacion(
                "Programa", "Descripción", LocalDate.now(), LocalDate.now().plusDays(1), LocalDate.now(), ""
        );
        programa.setDocente(docente);
        programa.agregarCurso(new Curso(institutoA, "Curso A", "", 1, 1, 1, "", LocalDate.now()));
        programa.agregarCurso(new Curso(institutoB, "Curso B", "", 1, 1, 1, "", LocalDate.now()));

        assertTrue(docente.perteneceAInstituto("INCO"));
        assertTrue(docente.perteneceAInstituto("IMERL"));
        assertEquals(List.of(programa), docente.getProgramas());
        assertEquals(2, programa.getInstitutos().size());
        assertTrue(programa.getInstitutos().contains(institutoA));
        assertTrue(programa.getInstitutos().contains(institutoB));
    }

    @Test
    void dtosExponenInstitutosYResponsableDelPrograma() {
        DtDocente docente = new DtDocente(
                "docente", "Nombre", "Apellido", "docente@test.com", LocalDate.now(), "",
                null, null, List.of("INCO", "IMERL"), List.of("Edición 1"), List.of("Programa")
        );
        DTProgramaFormacion programa = new DTProgramaFormacion(
                "Programa", "Descripción", LocalDate.now(), LocalDate.now().plusDays(1), LocalDate.now(),
                List.of("Curso"), List.of(), "", "docente", List.of("INCO", "IMERL")
        );

        assertEquals(List.of("INCO", "IMERL"), docente.getInstitutos());
        assertEquals(List.of("Programa"), docente.getProgramas());
        assertEquals("docente", programa.getNicknameDocente());
        assertEquals(List.of("INCO", "IMERL"), programa.getInstitutos());
    }
}