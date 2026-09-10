package com.grupo3_mat.edEXT.Logica.Manejadores;

import com.grupo3_mat.edEXT.Logica.Clases.Instituto;
import com.grupo3_mat.edEXT.Persistencia.Conexion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class ManejadorInstituto {

    private static ManejadorInstituto instancia = null;

    private ManejadorInstituto() {
      
    }

    public static ManejadorInstituto getInstancia() {
        if (instancia == null) {
            instancia = new ManejadorInstituto();
        }
        return instancia;
    }

    public void agregarInstituto(Instituto inst) {
        EntityManager em = Conexion.getInstancia().getEntityManager();;
        try {
            em.getTransaction().begin();
            em.persist(inst);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Instituto buscarInstituto(String nombre) {
        EntityManager em = Conexion.getInstancia().getEntityManager();;
        try {
            return em.find(Instituto.class, nombre);
        } finally {
            em.close();
        }
    }

    public List<Instituto> listarInstitutos() {
        EntityManager em = Conexion.getInstancia().getEntityManager();;
        try {
            return em.createQuery("SELECT i FROM Instituto i", Instituto.class).getResultList();
        } finally {
            em.close();
        }
    }
}