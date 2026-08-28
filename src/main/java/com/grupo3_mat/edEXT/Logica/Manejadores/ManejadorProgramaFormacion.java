package com.grupo3_mat.edEXT.Logica.Manejadores;

import com.grupo3_mat.edEXT.Logica.Clases.ProgramaFormacion;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 *
 * @author ssant
 */
public class ManejadorProgramaFormacion {

    private static ManejadorProgramaFormacion instancia = null;
    private EntityManagerFactory emf;

    private ManejadorProgramaFormacion() {
        this.emf = Persistence.createEntityManagerFactory("edEXT_PU");
    }

    public static ManejadorProgramaFormacion getInstancia() {
        if (instancia == null) {
            instancia = new ManejadorProgramaFormacion();
        }
        return instancia;
    }

    //Para guardar registros nuevos por primera vez
    public void agregarPrograma(ProgramaFormacion pf) {
        EntityManager em = emf.createEntityManager();
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
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(pf);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public ProgramaFormacion buscarPrograma(String nombre) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(ProgramaFormacion.class, nombre);
        } finally {
            em.close();
        }
    }    
        

    public List<ProgramaFormacion> getProgramas() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM ProgramaFormacion p", ProgramaFormacion.class).getResultList();
        } finally {
            em.close();
        }
    }
}
