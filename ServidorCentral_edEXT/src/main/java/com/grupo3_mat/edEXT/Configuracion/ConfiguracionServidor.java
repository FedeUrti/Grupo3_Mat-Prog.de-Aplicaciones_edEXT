package com.grupo3_mat.edEXT.Configuracion;

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

public final class ConfiguracionServidor {
    private static final Logger LOGGER = Logger.getLogger(ConfiguracionServidor.class.getName());
    private static volatile ConfiguracionServidor instancia;

    private final Map<String, String> valores;

    private ConfiguracionServidor(Path home, Map<String, String> entorno) {
        Path archivo = home.resolve(".edEXT").resolve("servidor.properties");
        Map<String, String> valoresDefecto = new LinkedHashMap<>();
        valoresDefecto.put("soap.bind.url", "http://127.0.0.1:9128/edext/soap");
        valoresDefecto.put("jdbc.url", "jdbc:mysql://localhost:3306/edext_db?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true");
        valoresDefecto.put("jdbc.user", "root");
        valoresDefecto.put("jdbc.password", "root");
        valoresDefecto.put("hbm2ddl.auto", "update");
        valoresDefecto.put("directorios.imagenes", home.resolve(".edEXT").resolve("imagenes").toString());

        Properties propiedades = leerOCrear(archivo, valoresDefecto);
        Map<String, String> nombresEntorno = Map.of(
                "soap.bind.url", "EDEXT_SOAP_BIND_ADDRESS",
                "jdbc.url", "EDEXT_JDBC_URL",
                "jdbc.user", "EDEXT_JDBC_USER",
                "jdbc.password", "EDEXT_JDBC_PASSWORD",
                "hbm2ddl.auto", "EDEXT_HBM2DDL_AUTO");

        this.valores = new LinkedHashMap<>();
        valoresDefecto.forEach((clave, defecto) -> {
            String variable = nombresEntorno.get(clave);
            String valorSistema = variable == null ? null : System.getProperty(variable);
            String valorEntorno = variable == null ? null : entorno.get(variable);
            String valorArchivo = propiedades.getProperty(clave);
            String valor = noVacio(valorSistema) ? valorSistema
                    : noVacio(valorEntorno) ? valorEntorno
                    : noVacio(valorArchivo) ? valorArchivo : defecto;
            this.valores.put(clave, valor.trim());
        });
    }

    public static ConfiguracionServidor get() {
        ConfiguracionServidor actual = instancia;
        if (actual == null) {
            synchronized (ConfiguracionServidor.class) {
                actual = instancia;
                if (actual == null) {
                    actual = cargar(Path.of(System.getProperty("user.home")), System.getenv());
                    instancia = actual;
                }
            }
        }
        return actual;
    }

    static ConfiguracionServidor cargar(Path home, Map<String, String> entorno) {
        return new ConfiguracionServidor(home, entorno);
    }

    public String getSoapBindUrl() {
        return valores.get("soap.bind.url");
    }

    public String getJdbcUrl() {
        return valorSistema("EDEXT_JDBC_URL", "jdbc.url");
    }

    public String getJdbcUser() {
        return valorSistema("EDEXT_JDBC_USER", "jdbc.user");
    }

    public String getJdbcPassword() {
        return valorSistema("EDEXT_JDBC_PASSWORD", "jdbc.password");
    }

    public String getHbm2ddlAuto() {
        return valorSistema("EDEXT_HBM2DDL_AUTO", "hbm2ddl.auto");
    }

    public Path getImageDirectory() {
        return Path.of(valores.get("directorios.imagenes")).toAbsolutePath().normalize();
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
                            "Configuración del servidor central y la Estación de Trabajo edEXT. Reinicie para aplicar cambios.\n"
                            + "soap.bind.url: URL base de publicación SOAP; en Docker se reemplaza por EDEXT_SOAP_BIND_ADDRESS.\n"
                            + "jdbc.url, jdbc.user y jdbc.password: conexión compartida por el central y la Estación.\n"
                            + "hbm2ddl.auto: estrategia de actualización del esquema de Hibernate.\n"
                            + "directorios.imagenes: carpeta de imágenes locales de la Estación de Trabajo.");
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

    private String valorSistema(String variable, String clave) {
        String valor = System.getProperty(variable);
        return noVacio(valor) ? valor.trim() : valores.get(clave);
    }
}
