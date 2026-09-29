package com.grupo3_mat.edEXT.Logica.Seguridad;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordHasher {
    // El prefijo permite reconocer el formato y cambiarlo en el futuro sin confundirlo con claves antiguas.
    private static final String PREFIX = "$pbkdf2-sha256$";
    // Más iteraciones hacen más costoso probar contraseñas robadas por fuerza bruta.
    private static final int ITERATIONS = 210_000;
    // Evita aceptar un hash manipulado que obligue al servidor a hacer un trabajo excesivo.
    private static final int MAX_ITERATIONS = 2_000_000;
    private static final int SALT_BYTES = 16;
    private static final int KEY_BITS = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordHasher() {
    }

    public static String hash(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }

        // Cada contraseña recibe un valor aleatorio distinto, aunque dos usuarios elijan la misma clave.
        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);
        byte[] derived = derive(password, salt, ITERATIONS, KEY_BITS);
        // Se guardan algoritmo, costo, sal y resultado para poder verificarlo después.
        return PREFIX + ITERATIONS + "$"
                + Base64.getEncoder().withoutPadding().encodeToString(salt) + "$"
                + Base64.getEncoder().withoutPadding().encodeToString(derived);
    }

    public static boolean verify(String password, String storedPassword) {
        if (password == null || storedPassword == null) {
            return false;
        }
        // Las claves antiguas estaban en texto plano; se aceptan para migrarlas al iniciar sesión.
        if (!storedPassword.startsWith(PREFIX)) {
            return MessageDigest.isEqual(
                    password.getBytes(StandardCharsets.UTF_8),
                    storedPassword.getBytes(StandardCharsets.UTF_8)
            );
        }

        try {
            String[] parts = storedPassword.split("\\$", -1);
            if (parts.length != 5 || !"pbkdf2-sha256".equals(parts[1])) {
                return false;
            }
            int iterations = Integer.parseInt(parts[2]);
            if (iterations < 1 || iterations > MAX_ITERATIONS) {
                return false;
            }
            byte[] salt = Base64.getDecoder().decode(parts[3]);
            byte[] expected = Base64.getDecoder().decode(parts[4]);
            if (salt.length < 8 || expected.length == 0) {
                return false;
            }
            // Se repite el mismo cálculo y se compara sin revelar diferencias por el tiempo de respuesta.
            byte[] actual = derive(password, salt, iterations, expected.length * 8);
            return MessageDigest.isEqual(expected, actual);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean needsRehash(String storedPassword) {
        // Indica si la clave es antigua o usa menos vueltas que las recomendadas actualmente.
        if (storedPassword == null || !storedPassword.startsWith(PREFIX)) {
            return true;
        }
        String[] parts = storedPassword.split("\\$", -1);
        if (parts.length != 5) {
            return true;
        }
        try {
            return Integer.parseInt(parts[2]) < ITERATIONS;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private static byte[] derive(String password, byte[] salt, int iterations, int keyBits) {
        char[] passwordChars = password.toCharArray();
        PBEKeySpec spec = new PBEKeySpec(passwordChars, salt, iterations, keyBits);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo procesar la contraseña de forma segura.", e);
        } finally {
            // Se borran las copias temporales de la contraseña tan pronto como termina el cálculo.
            spec.clearPassword();
            Arrays.fill(passwordChars, '\0');
        }
    }
}