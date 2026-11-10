package com.grupo3_mat.edEXT.Persistencia;

import com.grupo3_mat.edEXT.Configuracion.ConfiguracionServidor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class Conexion {

    private static Conexion instancia = null;
    private static EntityManagerFactory emf = null;

    private Conexion() {
        // 1. Solo se agregan propiedades de entorno que realmente estén configuradas.
        // 2. La configuración resuelta prioriza entorno, archivo y valor predeterminado.
        Map<String, Object> propiedades = new HashMap<>();
        ConfiguracionServidor configuracion = ConfiguracionServidor.get();
        propiedades.put("jakarta.persistence.jdbc.url", configuracion.getJdbcUrl());
        propiedades.put("jakarta.persistence.jdbc.user", configuracion.getJdbcUser());
        propiedades.put("jakarta.persistence.jdbc.password", configuracion.getJdbcPassword());
        propiedades.put("hibernate.hbm2ddl.auto", configuracion.getHbm2ddlAuto());
        // 3. Se construye una única fábrica compartida para toda la aplicación.
        emf = Persistence.createEntityManagerFactory("edEXT_PU", propiedades);
    }

    public static Conexion getInstancia() {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}