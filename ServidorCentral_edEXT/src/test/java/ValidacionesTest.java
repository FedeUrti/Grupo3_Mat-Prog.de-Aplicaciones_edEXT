

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class ValidacionesTest {

    @Test
    @DisplayName("Debe validar correctamente un correo con formato válido")
    void testCorreoValido() {
        String regexEmail = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        String email = "usuario@ejemplo.com";
        
        assertTrue(email.matches(regexEmail));
    }

    @Test
    @DisplayName("Debe rechazar un nickname que empiece con números")
    void testNicknameInvalido() {
        String regexNick = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ][a-zA-Z0-9_.-]*$";
        String nick = "123Usuario";
        
        assertFalse(nick.matches(regexNick));
    }
}