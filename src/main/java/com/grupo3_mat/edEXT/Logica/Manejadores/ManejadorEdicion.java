package com.grupo3_mat.edEXT.Logica.Manejadores;

/**
 *
 * @author benja
 */
import com.grupo3_mat.edEXT.Logica.Clases.EdicionCurso;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class ManejadorEdicion {

    private static ManejadorEdicion instancia = null;
    private final EntityManagerFactory emf;

    private ManejadorEdicion() {
        this.emf = Persistence.createEntityManagerFactory("edEXT_PU");
    }

    public static ManejadorEdicion getInstancia() {
        if (instancia == null) {
            instancia = new ManejadorEdicion();
        }
        return instancia;
    }

    public void agregarEdicion(EdicionCurso edicion) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(edicion);
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

    public EdicionCurso buscarEdicion(String nombre) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(EdicionCurso.class, nombre);
        } finally {
            em.close();
        }
    }

    public List<EdicionCurso> listarEdiciones() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT e FROM EdicionCurso e", EdicionCurso.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void modificarEdicion(EdicionCurso edicion) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(edicion);
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

    public void eliminarEdicion(String nombre) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            EdicionCurso edicion = em.find(EdicionCurso.class, nombre);
            if (edicion != null) {
                em.remove(edicion);
            }
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
}