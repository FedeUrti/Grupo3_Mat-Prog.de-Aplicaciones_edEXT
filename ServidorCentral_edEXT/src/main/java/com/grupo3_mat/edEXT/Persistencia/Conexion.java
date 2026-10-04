package com.grupo3_mat.edEXT.Persistencia;

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
        // 2. Las propiedades agregadas reemplazan los valores locales del persistence.xml.
        Map<String, Object> propiedades = new HashMap<>();
        agregarPropiedad(propiedades, "jakarta.persistence.jdbc.url", "EDEXT_JDBC_URL");
        agregarPropiedad(propiedades, "jakarta.persistence.jdbc.user", "EDEXT_JDBC_USER");
        agregarPropiedad(propiedades, "jakarta.persistence.jdbc.password", "EDEXT_JDBC_PASSWORD");
        agregarPropiedad(propiedades, "hibernate.hbm2ddl.auto", "EDEXT_HBM2DDL_AUTO");
        // 3. Se construye una única fábrica compartida para toda la aplicación.
        emf = Persistence.createEntityManagerFactory("edEXT_PU", propiedades);
    }

    private static void agregarPropiedad(Map<String, Object> propiedades, String clave, String variableEntorno) {
        // Las variables ausentes conservan el valor seguro/predeterminado del archivo de persistencia.
        String valor = System.getenv(variableEntorno);
        if (valor != null && !valor.isBlank()) {
            propiedades.put(clave, valor);
        }
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