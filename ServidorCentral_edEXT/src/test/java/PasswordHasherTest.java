import com.grupo3_mat.edEXT.Logica.Seguridad.PasswordHasher;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordHasherTest {

    @Test
    void rejectsMissingPasswordsAndMalformedHashes() {
        assertThrows(IllegalArgumentException.class, () -> PasswordHasher.hash(null));
        assertThrows(IllegalArgumentException.class, () -> PasswordHasher.hash("  "));
        assertFalse(PasswordHasher.verify(null, "clave"));
        assertFalse(PasswordHasher.verify("clave", null));
        assertFalse(PasswordHasher.verify("clave", "$otro$210000$c2FsdA$aGFzaA"));
        assertFalse(PasswordHasher.verify("clave", "$pbkdf2-sha256$0$c2FsdA$aGFzaA"));
        assertFalse(PasswordHasher.verify("clave", "$pbkdf2-sha256$2000001$c2FsdA$aGFzaA"));
        assertFalse(PasswordHasher.verify("clave", "$pbkdf2-sha256$abc$c2FsdA$aGFzaA"));
        assertFalse(PasswordHasher.verify("clave", "$pbkdf2-sha256$210000$%%%$aGFzaA"));
        assertFalse(PasswordHasher.verify("clave", "$pbkdf2-sha256$210000$AQ$aGFzaA"));
        assertFalse(PasswordHasher.verify("clave", "$pbkdf2-sha256$210000$c2FsdA$"));
        assertTrue(PasswordHasher.needsRehash(null));
        assertTrue(PasswordHasher.needsRehash("legacy"));
        assertTrue(PasswordHasher.needsRehash("$pbkdf2-sha256$nope$salt$hash"));
    }

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