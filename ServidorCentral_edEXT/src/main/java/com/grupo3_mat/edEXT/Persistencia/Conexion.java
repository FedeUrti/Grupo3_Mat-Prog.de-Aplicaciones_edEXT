package com.grupo3_mat.edEXT.Persistencia;

import com.grupo3_mat.edEXT.Configuracion.ConfiguracionServidor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class Conexion {

    private static volatile Conexion instancia;
    private final EntityManagerFactory emf;

    private Conexion() {
        Map<String, Object> propiedades = new HashMap<>();
        ConfiguracionServidor configuracion = ConfiguracionServidor.get();
        propiedades.put("jakarta.persistence.jdbc.url", configuracion.getJdbcUrl());
        propiedades.put("jakarta.persistence.jdbc.user", configuracion.getJdbcUser());
        propiedades.put("jakarta.persistence.jdbc.password", configuracion.getJdbcPassword());
        propiedades.put("hibernate.hbm2ddl.auto", configuracion.getHbm2ddlAuto());
        propiedades.put("hibernate.connection.provider_class",
                "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
        propiedades.put("hibernate.hikari.minimumIdle", "2");
        propiedades.put("hibernate.hikari.maximumPoolSize", "10");
        propiedades.put("hibernate.default_batch_fetch_size", "32");
        emf = Persistence.createEntityManagerFactory("edEXT_PU", propiedades);
    }

    public static Conexion getInstancia() {
        Conexion actual = instancia;
        if (actual == null) {
            synchronized (Conexion.class) {
                actual = instancia;
                if (actual == null) {
                    actual = new Conexion();
                    instancia = actual;
                }
            }
        }
        return actual;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static void cerrar() {
        Conexion actual = instancia;
        if (actual != null && actual.emf.isOpen()) {
            actual.emf.close();
        }
    }
}