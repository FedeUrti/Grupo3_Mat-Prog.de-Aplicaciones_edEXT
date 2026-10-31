package com.grupo3_mat.edEXT.Logica.Manejadores;

import com.grupo3_mat.edEXT.Logica.Clases.Curso;
import com.grupo3_mat.edEXT.Logica.Clases.Categoria;
import com.grupo3_mat.edEXT.Logica.Clases.Instituto;
import com.grupo3_mat.edEXT.Logica.Clases.Estudiante;
import com.grupo3_mat.edEXT.Logica.Clases.ValoracionCurso;
import com.grupo3_mat.edEXT.Logica.Clases.ValoracionCursoId;
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

    public List<String> listarCursosValorables(String nicknameEstudiante) {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            // Las ediciones finalizadas siguen habilitando la valoración si la inscripción fue aceptada.
            List<?> cursos = em.createNativeQuery(
                    "SELECT DISTINCT ed.curso_nombre "
                    + "FROM InscripcionEC ins "
                    + "JOIN EdicionCurso ed ON ed.nombre = ins.edicion_nombre "
                    + "WHERE ins.estudiante_nickname = :nickname "
                    + "AND UPPER(ins.estado) = 'ACEPTADA' "
                    + "ORDER BY ed.curso_nombre")
                    .setParameter("nickname", nicknameEstudiante)
                    .getResultList();
            return cursos.stream().map(String::valueOf).toList();
        } finally {
            em.close();
        }
    }

    public void agregarValoracionCurso(String nombreCurso, String nicknameEstudiante, int puntaje) {
        if (puntaje < 1 || puntaje > 5) {
            throw new IllegalArgumentException("El puntaje debe estar entre 1 y 5.");
        }

        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            em.getTransaction().begin();
            Curso curso = em.find(Curso.class, nombreCurso);
            Estudiante estudiante = em.find(Estudiante.class, nicknameEstudiante);
            if (curso == null || estudiante == null) {
                throw new IllegalArgumentException("El curso o el estudiante especificado no existe.");
            }

            ValoracionCursoId id = new ValoracionCursoId(nombreCurso, nicknameEstudiante);
            if (em.find(ValoracionCurso.class, id) != null) {
                throw new IllegalStateException("El estudiante ya publicó una valoración para este curso.");
            }

            Number inscripcionesAceptadas = (Number) em.createNativeQuery(
                    "SELECT COUNT(*) "
                    + "FROM InscripcionEC ins "
                    + "JOIN EdicionCurso ed ON ed.nombre = ins.edicion_nombre "
                    + "WHERE ins.estudiante_nickname = :nickname "
                    + "AND ed.curso_nombre = :curso "
                    + "AND UPPER(ins.estado) = 'ACEPTADA'")
                    .setParameter("nickname", nicknameEstudiante)
                    .setParameter("curso", nombreCurso)
                    .getSingleResult();
            if (inscripcionesAceptadas.longValue() == 0) {
                throw new IllegalStateException(
                        "El estudiante debe tener una inscripción aceptada en alguna edición del curso.");
            }

            ValoracionCurso valoracion = new ValoracionCurso(estudiante, curso, puntaje);
            curso.agregarValoracion(valoracion);
            em.persist(valoracion);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public ValoracionCurso buscarValoracionCurso(String nombreCurso, String nicknameEstudiante) {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            return em.find(ValoracionCurso.class, new ValoracionCursoId(nombreCurso, nicknameEstudiante));
        } finally {
            em.close();
        }
    }
}