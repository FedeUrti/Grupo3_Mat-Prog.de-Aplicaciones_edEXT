package com.grupo3_mat.servidorweb_edext.configuracion;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ConfiguracionWeb {
    private static final Logger LOGGER = Logger.getLogger(ConfiguracionWeb.class.getName());
    private static volatile ConfiguracionWeb instancia;

    private final Map<String, String> valores;

    private ConfiguracionWeb(Path home, Map<String, String> entorno) {
        Path carpeta = home.resolve(".edEXT");
        Map<String, String> valoresDefecto = new LinkedHashMap<>();
        valoresDefecto.put("central.soap.url", "http://localhost:9128/edext/soap");
        valoresDefecto.put("upload.dir", carpeta.resolve("uploads").toString());
        valoresDefecto.put("imagenes.dir", carpeta.resolve("imagenes").toString());
        valoresDefecto.put("soap.connect.timeout.ms", "5000");
        valoresDefecto.put("soap.request.timeout.ms", "30000");
        valoresDefecto.put("session.timeout.minutes", "30");

        Properties propiedades = leerOCrear(carpeta.resolve("web.properties"), valoresDefecto);
        Map<String, String> nombresEntorno = Map.of(
                "central.soap.url", "EDEXT_SOAP_BASE_URL",
                "upload.dir", "EDEXT_UPLOAD_DIR");
        valores = new LinkedHashMap<>();
        valoresDefecto.forEach((clave, defecto) -> {
            String variable = nombresEntorno.get(clave);
            String valorEntorno = variable == null ? null : entorno.get(variable);
            String valorArchivo = propiedades.getProperty(clave);
            valores.put(clave, (noVacio(valorEntorno) ? valorEntorno
                    : noVacio(valorArchivo) ? valorArchivo : defecto).trim());
        });
    }

    public static ConfiguracionWeb get() {
        ConfiguracionWeb actual = instancia;
        if (actual == null) {
            synchronized (ConfiguracionWeb.class) {
                actual = instancia;
                if (actual == null) {
                    actual = cargar(Path.of(System.getProperty("user.home")), System.getenv());
                    instancia = actual;
                }
            }
        }
        return actual;
    }

    static ConfiguracionWeb cargar(Path home, Map<String, String> entorno) {
        return new ConfiguracionWeb(home, entorno);
    }

    public String getCentralSoapUrl() {
        return valores.get("central.soap.url");
    }

    public Path getUploadDirectory() {
        return Path.of(valores.get("upload.dir")).toAbsolutePath().normalize();
    }

    public Path getImageDirectory() {
        return Path.of(valores.get("imagenes.dir")).toAbsolutePath().normalize();
    }

    public int getSoapConnectTimeoutMillis() {
        return entero("soap.connect.timeout.ms", 5000);
    }

    public int getSoapRequestTimeoutMillis() {
        return entero("soap.request.timeout.ms", 30000);
    }

    public int getSessionTimeoutMinutes() {
        return entero("session.timeout.minutes", 30);
    }

    private int entero(String clave, int defecto) {
        try {
            int valor = Integer.parseInt(valores.get(clave));
            if (valor > 0) {
                return valor;
            }
        } catch (NumberFormatException error) {
            LOGGER.log(Level.WARNING,
                    "El valor de " + clave + " en web.properties no es un entero positivo; se usará " + defecto + ".",
                    error);
        }
        return defecto;
    }

    private static Properties leerOCrear(Path archivo, Map<String, String> valoresDefecto) {
        Properties propiedades = new Properties();
        try {
            if (Files.exists(archivo)) {
                try (Reader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
                    propiedades.load(lector);
                }
            } else {
                Files.createDirectories(archivo.getParent());
            }
            boolean faltanValores = false;
            for (Map.Entry<String, String> entrada : valoresDefecto.entrySet()) {
                if (propiedades.getProperty(entrada.getKey()) == null) {
                    propiedades.setProperty(entrada.getKey(), entrada.getValue());
                    faltanValores = true;
                }
            }
            if (faltanValores) {
                try (Writer escritor = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
                    propiedades.store(escritor,
                            "Configuración del servidor web edEXT. Reinicie Tomcat para aplicar cambios.\n"
                            + "central.soap.url: URL base SOAP del servidor central.\n"
                            + "upload.dir e imagenes.dir: carpetas de imágenes cargadas y locales de escritorio.\n"
                            + "soap.*.timeout.ms: tiempos máximos de conexión y respuesta SOAP.\n"
                            + "session.timeout.minutes: inactividad máxima de una sesión web.");
                }
            }
        } catch (IOException | SecurityException error) {
            LOGGER.log(Level.WARNING,
                    "No se pudo leer o crear " + archivo + "; se usarán los valores predeterminados donde falte configuración.",
                    error);
        }
        return propiedades;
    }

    private static boolean noVacio(String valor) {
        return valor != null && !valor.isBlank();
    }
}
