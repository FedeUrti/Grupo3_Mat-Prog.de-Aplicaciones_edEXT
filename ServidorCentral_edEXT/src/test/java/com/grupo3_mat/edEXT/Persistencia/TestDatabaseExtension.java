package com.grupo3_mat.edEXT.Persistencia;

import com.grupo3_mat.edEXT.Configuracion.ConfiguracionServidor;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionConfigurationException;

public final class TestDatabaseExtension implements BeforeAllCallback, AfterAllCallback {
    private record PropiedadesAnteriores(Map<String, String> valores) {
    }

    private static final String DATABASE = "edext_test";
    private static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(TestDatabaseExtension.class);
    private static final String[] PROPIEDADES = {
        "EDEXT_JDBC_URL", "EDEXT_JDBC_USER", "EDEXT_JDBC_PASSWORD", "EDEXT_HBM2DDL_AUTO"
    };
    private static final Pattern MYSQL_URL =
            Pattern.compile("^jdbc:mysql://([^/]+)/([^?]+)(?:\\?(.*))?$");

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        synchronized (TestDatabaseExtension.class) {
            Map<String, String> entorno = System.getenv();
            ConfiguracionServidor configuracion = ConfiguracionServidor.get();
            String jdbcUrl = entorno.get("EDEXT_TEST_JDBC_URL");
            jdbcUrl = jdbcUrl == null || jdbcUrl.isBlank()
                    ? urlEsquemaPrueba(configuracion.getJdbcUrl()) : validarUrlPrueba(jdbcUrl);
            String usuario = valorConfigurado(entorno, "EDEXT_TEST_JDBC_USER",
                    configuracion.getJdbcUser());
            String clave = valorConfigurado(entorno, "EDEXT_TEST_JDBC_PASSWORD",
                    configuracion.getJdbcPassword());

            try {
                DriverManager.getConnection(jdbcUrl, usuario, clave).close();
            } catch (SQLException error) {
                if (error.getErrorCode() == 1045) {
                    throw new SQLException(
                            "MySQL rechazó el usuario '" + usuario + "'. Verifique jdbc.user y "
                            + "jdbc.password en ~/.edEXT/servidor.properties y que el usuario "
                            + "exacto tenga permisos sobre edext_test.",
                            error);
                }
                if (error.getErrorCode() != 1049) {
                    throw new SQLException(
                            "No se pudo conectar a MySQL para las pruebas. URL: " + jdbcUrl
                            + ". Verifique que MySQL esté iniciado y que ~/.edEXT/servidor.properties "
                            + "use el host y puerto accesibles desde este entorno.",
                            error);
                }
                crearEsquema(jdbcUrl, usuario, clave);
            }

            Map<String, String> valoresAnteriores = new HashMap<>();
            for (String propiedad : PROPIEDADES) {
                valoresAnteriores.put(propiedad, System.getProperty(propiedad));
            }
            context.getStore(NAMESPACE).put("propiedadesAnteriores",
                    new PropiedadesAnteriores(valoresAnteriores));
            System.setProperty("EDEXT_JDBC_URL", jdbcUrl);
            System.setProperty("EDEXT_JDBC_USER", usuario);
            System.setProperty("EDEXT_JDBC_PASSWORD", clave);
            System.setProperty("EDEXT_HBM2DDL_AUTO", "create-drop");
        }
    }

    @Override
    public void afterAll(ExtensionContext context) {
        PropiedadesAnteriores propiedadesAnteriores = context.getStore(NAMESPACE)
                .remove("propiedadesAnteriores", PropiedadesAnteriores.class);
        if (propiedadesAnteriores == null) {
            return;
        }
        propiedadesAnteriores.valores().forEach((propiedad, valor) -> {
            if (valor == null) {
                System.clearProperty(propiedad);
            } else {
                System.setProperty(propiedad, valor);
            }
        });
    }

    static String urlEsquemaPrueba(String jdbcUrl) {
        Matcher matcher = MYSQL_URL.matcher(jdbcUrl);
        if (!matcher.matches()) {
            throw new ExtensionConfigurationException(
                    "La URL JDBC debe tener formato jdbc:mysql://host:puerto/esquema");
        }
        return "jdbc:mysql://" + matcher.group(1) + "/" + DATABASE
                + (matcher.group(3) == null ? "" : "?" + matcher.group(3));
    }

    private static String validarUrlPrueba(String jdbcUrl) {
        Matcher matcher = MYSQL_URL.matcher(jdbcUrl);
        if (!matcher.matches() || !DATABASE.equals(matcher.group(2))) {
            throw new ExtensionConfigurationException(
                    "EDEXT_TEST_JDBC_URL debe apuntar exclusivamente al esquema " + DATABASE);
        }
        return jdbcUrl;
    }

    private static String valorConfigurado(Map<String, String> entorno, String variable,
            String valorDefecto) {
        String valor = entorno.get(variable);
        return valor == null || valor.isBlank() ? valorDefecto : valor;
    }

    private static void crearEsquema(String jdbcUrl, String usuario, String clave) throws SQLException {
        String urlServidor = urlSinEsquema(jdbcUrl);
        try (var conexion = DriverManager.getConnection(urlServidor, usuario, clave);
                var sentencia = conexion.createStatement()) {
            sentencia.execute("CREATE DATABASE " + DATABASE + " CHARACTER SET utf8mb4");
        }
    }

    static String urlSinEsquema(String jdbcUrl) {
        Matcher matcher = MYSQL_URL.matcher(jdbcUrl);
        if (!matcher.matches()) {
            throw new ExtensionConfigurationException(
                    "La URL JDBC debe tener formato jdbc:mysql://host:puerto/esquema");
        }
        return "jdbc:mysql://" + matcher.group(1) + "/"
                + (matcher.group(3) == null ? "" : "?" + matcher.group(3));
    }
}
