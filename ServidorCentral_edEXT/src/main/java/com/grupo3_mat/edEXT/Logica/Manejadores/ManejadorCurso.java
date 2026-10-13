package com.grupo3_mat.edEXT.Logica.Manejadores;

import com.grupo3_mat.edEXT.Logica.Clases.Curso;
import com.grupo3_mat.edEXT.Logica.Clases.Categoria;
import com.grupo3_mat.edEXT.Logica.Clases.Instituto;
import com.grupo3_mat.edEXT.Persistencia.Conexion;
import jakarta.persistence.EntityManager;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ManejadorCurso {

    private static ManejadorCurso instancia = null;

    private ManejadorCurso() {
    }

    public static ManejadorCurso getInstancia() {
        if (instancia == null) {
            instancia = new ManejadorCurso();
        }
        return instancia;
    }

    public void agregarCurso(Curso curso) {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            em.getTransaction().begin();
            Instituto instituto = curso.getInstituto();
            if (instituto != null) {
                curso.setInstituto(em.getReference(Instituto.class, instituto.getNombre()));
            }

            Set<Categoria> categoriasGestionadas = new HashSet<>();
            for (Categoria categoria : curso.getCategorias()) {
                categoriasGestionadas.add(em.getReference(Categoria.class, categoria.getNombre()));
            }
            curso.setCategorias(categoriasGestionadas);

            Set<Curso> previasGestionadas = new HashSet<>();
            for (Curso previa : curso.getPrevias()) {
                previasGestionadas.add(em.getReference(Curso.class, previa.getNombre()));
            }
            curso.setPrevias(previasGestionadas);

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
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            return em.find(Curso.class, nombre);
        } finally {
            em.close();
        }
    }

    public List<Curso> listarCursosPorInstituto(String nomInstituto) {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Curso c WHERE c.instituto.nombre = :inst", Curso.class)
                    .setParameter("inst", nomInstituto)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Curso> listarCursosPorCategoria(String nomCategoria) {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Curso c JOIN c.categorias cat WHERE cat.nombre = :cat", Curso.class)
                    .setParameter("cat", nomCategoria)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Curso> listarCursos() {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Curso c", Curso.class).getResultList();
        } finally {
            em.close();
        }
    }
}