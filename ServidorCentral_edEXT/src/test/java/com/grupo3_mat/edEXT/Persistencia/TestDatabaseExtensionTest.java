package com.grupo3_mat.edEXT.Persistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionConfigurationException;

class TestDatabaseExtensionTest {
    @Test
    void conservaServidorPuertoYOpcionesAlCambiarAlEsquemaDePrueba() {
        String url = TestDatabaseExtension.urlEsquemaPrueba(
                "jdbc:mysql://127.0.0.1:3306/edext_db?serverTimezone=UTC&useSSL=false");

        assertEquals("jdbc:mysql://127.0.0.1:3306/edext_test?serverTimezone=UTC&useSSL=false", url);
    }

    @Test
    void rechazaUrlsQueNoSeanMysql() {
        assertThrows(ExtensionConfigurationException.class,
                () -> TestDatabaseExtension.urlEsquemaPrueba("jdbc:postgresql://localhost/edext_db"));
    }

    @Test
    void creaLaConexionDeServidorSinEsquemaConOpcionesMysql() {
        String url = TestDatabaseExtension.urlSinEsquema(
                "jdbc:mysql://127.0.0.1:3306/edext_test?serverTimezone=UTC&useSSL=false");

        assertEquals("jdbc:mysql://127.0.0.1:3306/?serverTimezone=UTC&useSSL=false", url);
    }

    @Test
    void rechazaUrlsInvalidasAlCrearLaConexionDeServidor() {
        assertThrows(ExtensionConfigurationException.class,
                () -> TestDatabaseExtension.urlSinEsquema("jdbc:mysql://localhost"));
    }
}
