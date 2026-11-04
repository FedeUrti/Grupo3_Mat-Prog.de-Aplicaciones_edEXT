import com.grupo3_mat.edEXT.Logica.Controladores.ControladorCategoria;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCategoria;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ControladorCategoriaTest {

    @Test
    void altaModificacionDuplicadosYListadoDeCategorias() throws Exception {
        IControladorCategoria controlador = new ControladorCategoria();
        String sufijo = UUID.randomUUID().toString();
        String nombreOriginal = "CategoriaOriginal-" + sufijo;
        String nombreNuevo = "CategoriaModificada-" + sufijo;
        String nombreExistente = "CategoriaExistente-" + sufijo;

        controlador.altaCategoria(" " + nombreOriginal + " ");
        controlador.altaCategoria(nombreExistente);

        assertTrue(controlador.listarCategorias().contains(nombreOriginal));
        assertThrows(Exception.class, () -> controlador.altaCategoria(nombreOriginal));

        controlador.modificarCategoria(" " + nombreOriginal + " ", " " + nombreNuevo + " ");
        assertTrue(controlador.listarCategorias().contains(nombreNuevo));
        assertFalse(controlador.listarCategorias().contains(nombreOriginal));

        assertThrows(Exception.class, () -> controlador.modificarCategoria(nombreNuevo, nombreExistente));
        assertThrows(Exception.class, () -> controlador.modificarCategoria("NoExiste-" + sufijo, nombreOriginal));
        assertThrows(Exception.class, () -> controlador.modificarCategoria(null, nombreNuevo));
        assertThrows(Exception.class, () -> controlador.modificarCategoria(nombreNuevo, " "));

        controlador.modificarCategoria(nombreNuevo, nombreNuevo);
        assertTrue(controlador.listarCategorias().contains(nombreNuevo));
    }
}