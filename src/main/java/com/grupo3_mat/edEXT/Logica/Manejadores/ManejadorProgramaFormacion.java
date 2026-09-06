package com.grupo3_mat.edEXT.Logica.Manejadores;

import com.grupo3_mat.edEXT.Logica.Clases.Curso;
import com.grupo3_mat.edEXT.Logica.Clases.ProgramaFormacion;
import com.grupo3_mat.edEXT.Persistencia.Conexion;
import java.util.List;
import jakarta.persistence.EntityManager;

public class ManejadorProgramaFormacion {

    private static ManejadorProgramaFormacion instancia = null;

    private ManejadorProgramaFormacion() {
    }

    public static ManejadorProgramaFormacion getInstancia() {
        if (instancia == null) {
            instancia = new ManejadorProgramaFormacion();
        }
        return instancia;
    }

    // Para guardar registros nuevos por primera vez
    public void agregarPrograma(ProgramaFormacion pf) {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(pf);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public void agregarCursoAPrograma(String nombrePrograma, String nombreCurso) throws Exception {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            em.getTransaction().begin();

            ProgramaFormacion pf = em.find(ProgramaFormacion.class, nombrePrograma);
            Curso c = em.find(Curso.class, nombreCurso);

            if (pf == null) {
                throw new Exception("No existe el programa de formación ingresado.");
            }
            if (c == null) {
                throw new Exception("No existe el curso ingresado.");
            }

            if (pf.getCursos() != null && pf.getCursos().containsKey(c.getNombre())) {
                throw new Exception("El curso '" + c.getNombre() + "' ya pertenece al programa '" + pf.getNombre() + "'.");
            }

            // 1. Sincronizar ambos lados de la relación bidireccional en memoria
            pf.agregarCurso(c);
            if (c.getProgramas() != null && !c.getProgramas().contains(pf)) {
                c.getProgramas().add(pf);
            }

            // 2. Persistir
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            // Imprime el StackTrace completo en la consola de NetBeans para ver la causa exacta
            ex.printStackTrace();

            // Extrae el mensaje de la excepción de nivel más bajo (ej. SQLException)
            Throwable rootCause = ex;
            while (rootCause.getCause() != null) {
                rootCause = rootCause.getCause();
            }
            throw new Exception("Error al asociar en la BD: " + rootCause.getMessage(), ex);
        } finally {
            em.close();
        }
    }

    // Para actualizar registros ya existentes en la BD
    public void modificarPrograma(ProgramaFormacion pf) {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(pf);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public ProgramaFormacion buscarPrograma(String nombre) {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            return em.find(ProgramaFormacion.class, nombre);
        } finally {
            em.close();
        }
    }

    public List<ProgramaFormacion> getProgramas() {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            return em.createQuery("SELECT p FROM ProgramaFormacion p", ProgramaFormacion.class).getResultList();
        } finally {
            em.close();
        }
    }
}
