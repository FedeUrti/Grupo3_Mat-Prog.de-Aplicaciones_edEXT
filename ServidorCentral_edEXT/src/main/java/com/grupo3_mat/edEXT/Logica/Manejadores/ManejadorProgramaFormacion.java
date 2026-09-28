package com.grupo3_mat.edEXT.Logica.Manejadores;

import com.grupo3_mat.edEXT.Logica.Clases.Curso;
import com.grupo3_mat.edEXT.Logica.Clases.Docente;
import com.grupo3_mat.edEXT.Logica.Clases.ProgramaFormacion;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTProgramaFormacion;
import com.grupo3_mat.edEXT.Persistencia.Conexion;
import jakarta.persistence.EntityManager;
import java.util.List;

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

    public void agregarPrograma(ProgramaFormacion pf) {
        // Se valida aquí también para evitar programas sin responsable aunque se saltee el controlador.
        if (pf.getDocente() == null) {
            throw new IllegalArgumentException("El programa debe tener un docente responsable.");
        }
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            em.getTransaction().begin();
            // El docente viene de otra consulta y se enlaza a la transacción que guarda el programa.
            Docente docenteGestionado = em.getReference(Docente.class, pf.getDocente().getNickname());
            pf.setDocente(docenteGestionado);
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

            // Se cargan ambas entidades dentro de la misma transacción antes de asociarlas.
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

            pf.agregarCurso(c);
            if (c.getProgramas() != null && !c.getProgramas().contains(pf)) {
                c.getProgramas().add(pf);
            }

            em.getTransaction().commit();
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            Throwable rootCause = ex;
            while (rootCause.getCause() != null) {
                rootCause = rootCause.getCause();
            }
            throw new Exception("Error al asociar en la BD: " + rootCause.getMessage(), ex);
        } finally {
            em.close();
        }
    }

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

    public List<DTProgramaFormacion> getProgramas() {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            List<ProgramaFormacion> programas = em.createQuery(
                    "SELECT p FROM ProgramaFormacion p", ProgramaFormacion.class).getResultList();
            List<DTProgramaFormacion> resultado = new java.util.ArrayList<>();
            for (ProgramaFormacion programa : programas) {
                resultado.add(convertirADataType(programa));
            }
            return resultado;
        } finally {
            em.close();
        }
    }

    public List<ProgramaFormacion> listarEntidades() {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            return em.createQuery("SELECT p FROM ProgramaFormacion p", ProgramaFormacion.class).getResultList();
        } finally {
            em.close();
        }
    }

    private DTProgramaFormacion convertirADataType(ProgramaFormacion programa) {
        // El DTO contiene nombres y datos de presentación, no referencias a objetos persistentes.
        List<String> cursos = new java.util.ArrayList<>();
        for (Curso curso : programa.getCursos().values()) {
            cursos.add(curso.getNombre());
        }

        List<String> categorias = new java.util.ArrayList<>();
        for (com.grupo3_mat.edEXT.Logica.Clases.Categoria categoria : programa.getCategorias()) {
            categorias.add(categoria.getNombre());
        }

        List<String> institutos = new java.util.ArrayList<>();
        for (com.grupo3_mat.edEXT.Logica.Clases.Instituto instituto : programa.getInstitutos()) {
            institutos.add(instituto.getNombre());
        }

        return new DTProgramaFormacion(
                programa.getNombre(), programa.getDescripcion(), programa.getFechaInicio(),
                programa.getFechaFin(), programa.getFechaAlta(), cursos, categorias,
                programa.getImagenPath(),
                programa.getDocente() != null ? programa.getDocente().getNickname() : null,
                institutos
        );
    }
}