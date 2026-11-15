package com.grupo3_mat.edEXT.Logica.Manejadores;

import com.grupo3_mat.edEXT.Logica.Clases.Docente;
import com.grupo3_mat.edEXT.Logica.Clases.Estudiante;
import com.grupo3_mat.edEXT.Logica.Clases.Instituto;
import com.grupo3_mat.edEXT.Logica.Clases.Usuario;
import com.grupo3_mat.edEXT.Persistencia.Conexion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.ArrayList;
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
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            em.getTransaction().begin();
            if (usuario instanceof Docente docente) {
                // Las búsquedas previas devuelven institutos desconectados; se vuelven referencias de esta transacción.
                List<Instituto> institutosGestionados = new ArrayList<>();
                for (Instituto instituto : docente.getInstitutos()) {
                    institutosGestionados.add(em.getReference(Instituto.class, instituto.getNombre()));
                }
                docente.setInstitutos(institutosGestionados);
            }
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
        EntityManager em = Conexion.getInstancia().getEntityManager();
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

    public void dejarDeSeguirUsuario(String nicknameSeguidor, String nicknameASeguir) {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            em.getTransaction().begin();
            Usuario seguidor = em.find(Usuario.class, nicknameSeguidor);
            Usuario seguido = em.find(Usuario.class, nicknameASeguir);
            if (seguidor == null || seguido == null) {
                throw new IllegalArgumentException("Uno o ambos usuarios no existen.");
            }
            if (!seguidor.sigueA(nicknameASeguir)) {
                throw new IllegalArgumentException("No sigues a este usuario.");
            }

            seguidor.removerSeguido(seguido);
            seguido.removerSeguidor(seguidor);
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

    public Usuario buscarUsuarioPorNickname(String nickname) {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            return em.find(Usuario.class, nickname);
        } finally {
            em.close();
        }
    }

    public DatosUsuarioBasicos buscarDatosBasicosPorNickname(String nickname) {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            List<Object[]> resultados = em.createQuery(
                    "SELECT u.nickname, u.nombre, u.apellido, u.correo, u.fechaNacimiento, "
                    + "u.imagenPath, TYPE(u) FROM Usuario u WHERE u.nickname = :nickname",
                    Object[].class)
                    .setParameter("nickname", nickname)
                    .setMaxResults(1)
                    .getResultList();
            if (resultados.isEmpty()) {
                return null;
            }
            Object[] datos = resultados.get(0);
            return new DatosUsuarioBasicos(
                    (String) datos[0], (String) datos[1], (String) datos[2], (String) datos[3],
                    (java.time.LocalDate) datos[4], (String) datos[5], datos[6] == Docente.class);
        } finally {
            em.close();
        }
    }

    public DatosAcceso buscarDatosAcceso(String identificador) {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            Object[] datos = em.createQuery(
                    "SELECT u.nickname, u.password, u.nombre, u.apellido, u.correo, "
                    + "u.fechaNacimiento, u.imagenPath, TYPE(u) FROM Usuario u "
                    + "WHERE u.nickname = :identificador OR u.correo = :identificador", Object[].class)
                    .setParameter("identificador", identificador)
                    .getSingleResult();
            return new DatosAcceso(
                    (String) datos[0], (String) datos[1], (String) datos[2], (String) datos[3],
                    (String) datos[4], (java.time.LocalDate) datos[5], (String) datos[6],
                    datos[7] == Docente.class);
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public void actualizarPassword(String nickname, String passwordHash) {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            em.getTransaction().begin();
            int actualizados = em.createQuery(
                    "UPDATE Usuario u SET u.password = :password WHERE u.nickname = :nickname")
                    .setParameter("password", passwordHash)
                    .setParameter("nickname", nickname)
                    .executeUpdate();
            if (actualizados != 1) {
                throw new IllegalStateException("No se pudo actualizar la contraseña del usuario.");
            }
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

    public List<Usuario> buscarUsuariosPorNicknames(List<String> nicknames) {
        if (nicknames == null || nicknames.isEmpty()) {
            return List.of();
        }
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            return em.createQuery("SELECT u FROM Usuario u WHERE u.nickname IN :nicknames", Usuario.class)
                    .setParameter("nicknames", nicknames)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<String> listarNicknamesUsuarios() {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            return em.createQuery("SELECT u.nickname FROM Usuario u", String.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<String> listarNicknamesDocentes() {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            return em.createQuery("SELECT d.nickname FROM Docente d", String.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<String> listarNicknamesEstudiantes() {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            return em.createQuery("SELECT e.nickname FROM Estudiante e", String.class)
                    .getResultList();
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

    public void actualizarDatosBasicos(String nickname, String nombre, String apellido,
            java.time.LocalDate fechaNacimiento, String imagenPath) {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            em.getTransaction().begin();
            int actualizados = em.createQuery(
                    "UPDATE Usuario u SET u.nombre = :nombre, u.apellido = :apellido, "
                    + "u.fechaNacimiento = :fechaNacimiento, u.imagenPath = :imagenPath "
                    + "WHERE u.nickname = :nickname")
                    .setParameter("nombre", nombre)
                    .setParameter("apellido", apellido)
                    .setParameter("fechaNacimiento", fechaNacimiento)
                    .setParameter("imagenPath", imagenPath)
                    .setParameter("nickname", nickname)
                    .executeUpdate();
            if (actualizados != 1) {
                throw new IllegalArgumentException("El usuario seleccionado no existe.");
            }
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

    public List<Estudiante> getEstudiantes() {
        EntityManager em = Conexion.getInstancia().getEntityManager();
        try {
            return em.createQuery("SELECT e FROM Estudiante e", Estudiante.class).getResultList();
        } finally {
            em.close();
        }
    }

    public record DatosAcceso(String nickname, String passwordHash, String nombre, String apellido,
            String correo, java.time.LocalDate fechaNacimiento, String imagenPath, boolean docente) {
    }

    public record DatosUsuarioBasicos(String nickname, String nombre, String apellido, String correo,
            java.time.LocalDate fechaNacimiento, String imagenPath, boolean docente) {
    }
}