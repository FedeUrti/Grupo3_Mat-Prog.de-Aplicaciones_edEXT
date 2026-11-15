package com.grupo3_mat.edEXT.Configuracion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ConfiguracionServidorTest {
    @TempDir
    Path home;

    @Test
    void lasVariablesDeEntornoTienenPrioridadSobreElArchivo() throws Exception {
        Path archivo = home.resolve(".edEXT").resolve("servidor.properties");
        Files.createDirectories(archivo.getParent());
        Files.writeString(archivo, "soap.bind.url=http://archivo:9128/edext/soap\n");

        ConfiguracionServidor configuracion = ConfiguracionServidor.cargar(home, Map.of(
                "EDEXT_SOAP_BIND_ADDRESS", "http://entorno:9128/edext/soap"));

        assertEquals("http://entorno:9128/edext/soap", configuracion.getSoapBindUrl());
    }

    @Test
    void creaElArchivoDeConfiguracionConValoresPorDefecto() {
        ConfiguracionServidor configuracion = ConfiguracionServidor.cargar(home, Map.of());

        assertEquals("http://127.0.0.1:9128/edext/soap", configuracion.getSoapBindUrl());
        assertEquals("jdbc:mysql://localhost:3306/edext_db?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true",
                configuracion.getJdbcUrl());
        assertEquals("root", configuracion.getJdbcUser());
        assertTrue(Files.isRegularFile(home.resolve(".edEXT").resolve("servidor.properties")));
    }

    @Test
    void usaValoresPorDefectoSiElArchivoNoSePuedeLeer() throws Exception {
        Path archivo = home.resolve(".edEXT").resolve("servidor.properties");
        Files.createDirectories(archivo.getParent());
        Files.createDirectory(archivo);

        ConfiguracionServidor configuracion = ConfiguracionServidor.cargar(home, Map.of());

        assertEquals("http://127.0.0.1:9128/edext/soap", configuracion.getSoapBindUrl());
        assertEquals("root", configuracion.getJdbcUser());
    }

    @Test
    void leeValoresUtf8ConEspaciosYAcentos() throws Exception {
        Path archivo = home.resolve(".edEXT").resolve("servidor.properties");
        Files.createDirectories(archivo.getParent());
        Files.writeString(archivo, "directorios.imagenes=" + home.resolve("área de imágenes") + "\n");

        ConfiguracionServidor configuracion = ConfiguracionServidor.cargar(home, Map.of());

        assertEquals(home.resolve("área de imágenes").toAbsolutePath().normalize(),
                configuracion.getImageDirectory());
    }

    @Test
    void lasPropiedadesDeSistemaTienenPrioridadSobreElEntorno() {
        String valorAnterior = System.getProperty("EDEXT_JDBC_URL");
        System.setProperty("EDEXT_JDBC_URL", "jdbc:mysql://localhost:3307/edext_test");
        try {
            ConfiguracionServidor configuracion = ConfiguracionServidor.cargar(home, Map.of(
                    "EDEXT_JDBC_URL", "jdbc:mysql://localhost:3307/edext_db"));

            assertEquals("jdbc:mysql://localhost:3307/edext_test", configuracion.getJdbcUrl());
        } finally {
            if (valorAnterior == null) {
                System.clearProperty("EDEXT_JDBC_URL");
            } else {
                System.setProperty("EDEXT_JDBC_URL", valorAnterior);
            }
        }
    }

    @Test
    void lasPropiedadesDeSistemaSeAplicanASConfiguracionYaCargada() {
        ConfiguracionServidor configuracion = ConfiguracionServidor.cargar(home, Map.of());
        String valorAnterior = System.getProperty("EDEXT_JDBC_URL");
        System.setProperty("EDEXT_JDBC_URL", "jdbc:mysql://localhost:3306/edext_test");
        try {
            assertEquals("jdbc:mysql://localhost:3306/edext_test", configuracion.getJdbcUrl());
        } finally {
            if (valorAnterior == null) {
                System.clearProperty("EDEXT_JDBC_URL");
            } else {
                System.setProperty("EDEXT_JDBC_URL", valorAnterior);
            }
        }
    }
}
