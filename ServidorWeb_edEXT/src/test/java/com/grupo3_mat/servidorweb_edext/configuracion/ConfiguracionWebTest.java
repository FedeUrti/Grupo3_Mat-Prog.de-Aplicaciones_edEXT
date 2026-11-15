package com.grupo3_mat.servidorweb_edext.configuracion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ConfiguracionWebTest {
    @TempDir
    Path home;

    @Test
    void lasVariablesDeEntornoTienenPrioridadSobreElArchivo() throws Exception {
        Path archivo = home.resolve(".edEXT").resolve("web.properties");
        Files.createDirectories(archivo.getParent());
        Files.writeString(archivo, "central.soap.url=http://archivo:9128/edext/soap\n");

        ConfiguracionWeb configuracion = ConfiguracionWeb.cargar(home, Map.of(
                "EDEXT_SOAP_BASE_URL", "http://entorno:9128/edext/soap"));

        assertEquals("http://entorno:9128/edext/soap", configuracion.getCentralSoapUrl());
    }

    @Test
    void creaElArchivoDeConfiguracionConValoresPorDefecto() {
        ConfiguracionWeb configuracion = ConfiguracionWeb.cargar(home, Map.of());

        assertEquals("http://localhost:9128/edext/soap", configuracion.getCentralSoapUrl());
        assertEquals(home.resolve(".edEXT").resolve("uploads"), configuracion.getUploadDirectory());
        assertTrue(Files.isRegularFile(home.resolve(".edEXT").resolve("web.properties")));
    }

    @Test
    void usaValoresPorDefectoSiElArchivoNoSePuedeLeer() throws Exception {
        Path archivo = home.resolve(".edEXT").resolve("web.properties");
        Files.createDirectories(archivo.getParent());
        Files.createDirectory(archivo);

        ConfiguracionWeb configuracion = ConfiguracionWeb.cargar(home, Map.of());

        assertEquals("http://localhost:9128/edext/soap", configuracion.getCentralSoapUrl());
        assertEquals(home.resolve(".edEXT").resolve("uploads"), configuracion.getUploadDirectory());
    }

    @Test
    void leeValoresUtf8ConEspaciosYAcentos() throws Exception {
        Path archivo = home.resolve(".edEXT").resolve("web.properties");
        Files.createDirectories(archivo.getParent());
        Files.writeString(archivo, "upload.dir=" + home.resolve("área de cargas") + "\n");

        ConfiguracionWeb configuracion = ConfiguracionWeb.cargar(home, Map.of());

        assertEquals(home.resolve("área de cargas").toAbsolutePath().normalize(),
                configuracion.getUploadDirectory());
    }
}
