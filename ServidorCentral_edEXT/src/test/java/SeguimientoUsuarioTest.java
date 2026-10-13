import com.grupo3_mat.edEXT.Logica.Clases.Estudiante;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SeguimientoUsuarioTest {

    @Test
    void eliminaRelacionesAunqueCadaConsultaDevuelvaInstanciasDistintas() {
        Estudiante seguidor = new Estudiante(
                "seguidor", "Nombre", "Seguidor", "seguidor@test.com", LocalDate.now(), "", "hash");
        Estudiante seguidoEnRelacion = new Estudiante(
                "seguido", "Nombre", "Seguido", "seguido@test.com", LocalDate.now(), "", "hash");
        Estudiante seguidoConsultado = new Estudiante(
                "seguido", "Nombre", "Seguido", "seguido@test.com", LocalDate.now(), "", "hash");

        seguidor.agregarSeguido(seguidoEnRelacion);
        seguidoEnRelacion.agregarSeguidor(seguidor);

        seguidor.removerSeguido(seguidoConsultado);
        seguidoEnRelacion.removerSeguidor(new Estudiante(
                "seguidor", "Nombre", "Seguidor", "seguidor@test.com", LocalDate.now(), "", "hash"));

        assertFalse(seguidor.sigueA("seguido"));
        assertTrue(seguidoEnRelacion.getNicknamesSeguidores().isEmpty());
    }
}
