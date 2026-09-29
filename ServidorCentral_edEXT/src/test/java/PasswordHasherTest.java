import com.grupo3_mat.edEXT.Logica.Seguridad.PasswordHasher;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordHasherTest {

    @Test
    void hashesAndVerifiesPasswordsWithIndependentSalts() {
        String firstHash = PasswordHasher.hash("clave-segura");
        String secondHash = PasswordHasher.hash("clave-segura");

        assertNotEquals("clave-segura", firstHash);
        assertNotEquals(firstHash, secondHash);
        assertTrue(PasswordHasher.verify("clave-segura", firstHash));
        assertFalse(PasswordHasher.verify("otra-clave", firstHash));
        assertFalse(PasswordHasher.needsRehash(firstHash));
    }

    @Test
    void acceptsLegacyPlaintextForOneTimeUpgradeOnLogin() {
        assertTrue(PasswordHasher.verify("clave-antigua", "clave-antigua"));
        assertTrue(PasswordHasher.needsRehash("clave-antigua"));
    }
}