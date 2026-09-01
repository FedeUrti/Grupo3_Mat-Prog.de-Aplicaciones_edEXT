package com.grupo3_mat.edEXT.Persistencia;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Conexion {

    private static Conexion instancia = null;
    private static EntityManagerFactory emf = null;

    private Conexion() {
        emf = Persistence.createEntityManagerFactory("edEXTPU");
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