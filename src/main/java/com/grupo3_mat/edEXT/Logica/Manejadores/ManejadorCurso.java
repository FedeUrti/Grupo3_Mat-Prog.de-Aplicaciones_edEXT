package com.grupo3_mat.edEXT.Logica.Manejadores;

import com.grupo3_mat.edEXT.Logica.Clases.Curso;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class ManejadorCurso {

    private static ManejadorCurso instancia = null;
    private final EntityManagerFactory emf;

    private ManejadorCurso() {
        this.emf = Persistence.createEntityManagerFactory("edEXT_PU");
    }

    public static ManejadorCurso getInstancia() {
        if (instancia == null) {
            instancia = new ManejadorCurso();
        }
        return instancia;
    }

    public void agregarCurso(Curso curso) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(curso);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Curso buscarCurso(String nombre) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Curso.class, nombre);
        } finally {
            em.close();
        }
    }

    public List<Curso> listarCursosPorInstituto(String nomInstituto) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT c FROM Curso c WHERE c.instituto.nombre = :inst", Curso.class)
                    .setParameter("inst", nomInstituto)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Curso> listarCursos() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT c FROM Curso c", Curso.class).getResultList();
        } finally {
            em.close();
        }
    }
}