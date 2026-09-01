package com.grupo3_mat.edEXT.Logica.Manejadores;

import com.grupo3_mat.edEXT.Logica.Clases.ProgramaFormacion;
import com.grupo3_mat.edEXT.Persistencia.Conexion;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

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

    //Para guardar registros nuevos por primera vez
    public void agregarPrograma(ProgramaFormacion pf) {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(pf);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
    //Para actualizar registros ya existentes en la BD
    public void modificarPrograma(ProgramaFormacion pf) {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(pf);
            em.getTransaction().commit();
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
