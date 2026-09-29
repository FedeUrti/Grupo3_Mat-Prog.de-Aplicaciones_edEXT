package com.grupo3_mat.edEXT.Logica.Manejadores;

import com.grupo3_mat.edEXT.Logica.Clases.Categoria;
import com.grupo3_mat.edEXT.Logica.Clases.Curso;
import com.grupo3_mat.edEXT.Persistencia.Conexion;
import jakarta.persistence.EntityManager;
import java.util.List;

public class ManejadorCategoria {

    private static ManejadorCategoria instancia = null;

    private ManejadorCategoria() {
    }

    public static ManejadorCategoria getInstancia() {
        if (instancia == null) {
            instancia = new ManejadorCategoria();
        }
        return instancia;
    }

    public void agregarCategoria(Categoria cat) {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(cat);
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

    public void modificarCategoria(String nombreActual, String nuevoNombre) {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            em.getTransaction().begin();
            Categoria anterior = em.find(Categoria.class, nombreActual);
            if (anterior == null) {
                throw new IllegalArgumentException("La categoría '" + nombreActual + "' no existe.");
            }

            Categoria nueva = new Categoria(nuevoNombre);
            em.persist(nueva);
            List<Curso> cursos = em.createQuery(
                    "SELECT DISTINCT c FROM Curso c JOIN c.categorias categoria WHERE categoria.nombre = :nombre",
                    Curso.class
            ).setParameter("nombre", nombreActual).getResultList();
            for (Curso curso : cursos) {
                curso.getCategorias().remove(anterior);
                curso.getCategorias().add(nueva);
            }
            em.flush();
            em.remove(anterior);
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

    public Categoria buscarCategoria(String nombre) {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            return em.find(Categoria.class, nombre);
        } finally {
            em.close();
        }
    }

    public List<Categoria> listarCategorias() {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Categoria c", Categoria.class).getResultList();
        } finally {
            em.close();
        }
    }
}