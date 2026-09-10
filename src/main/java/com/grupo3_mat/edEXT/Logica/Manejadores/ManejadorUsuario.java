package com.grupo3_mat.edEXT.Logica.Manejadores;

import com.grupo3_mat.edEXT.Logica.Clases.Docente;
import com.grupo3_mat.edEXT.Logica.Clases.Estudiante;
import com.grupo3_mat.edEXT.Logica.Clases.Usuario;
import com.grupo3_mat.edEXT.Persistencia.Conexion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import java.util.List;

public class ManejadorUsuario {

    private static ManejadorUsuario instancia = null;

    private ManejadorUsuario() {
    }

    public static ManejadorUsuario getInstancia() {
        if (instancia == null) {
            instancia = new ManejadorUsuario();
        }
        return instancia;
    }

    public void agregarUsuario(Usuario usuario) {
        EntityManager em = Conexion.getInstancia().getEntityManager();;
        try {
            em.getTransaction().begin();
            em.persist(usuario);
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

    public void modificarUsuario(Usuario usuario) {
        EntityManager em = Conexion.getInstancia().getEntityManager();;
        try {
            em.getTransaction().begin();
            em.merge(usuario);
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

    public Usuario buscarUsuarioPorNickname(String nickname) {
        EntityManager em = Conexion.getInstancia().getEntityManager();;
        try {
            return em.find(Usuario.class, nickname);
        } finally {
            em.close();
        }
    }

    public Usuario buscarUsuarioPorCorreo(String correo) {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            return em.createQuery("SELECT u FROM Usuario u WHERE u.correo = :correo", Usuario.class)
                     .setParameter("correo", correo)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<Usuario> getUsuarios() {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            return em.createQuery("SELECT u FROM Usuario u", Usuario.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Docente> getDocentes() {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            return em.createQuery("SELECT d FROM Docente d", Docente.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Estudiante> getEstudiantes() {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            return em.createQuery("SELECT e FROM Estudiante e", Estudiante.class).getResultList();
        } finally {
            em.close();
        }
    }
}