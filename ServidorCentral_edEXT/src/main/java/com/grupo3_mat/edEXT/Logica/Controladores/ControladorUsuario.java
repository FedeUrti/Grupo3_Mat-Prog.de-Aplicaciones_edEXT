package com.grupo3_mat.edEXT.Logica.Controladores;

import com.grupo3_mat.edEXT.Logica.Clases.*;
import com.grupo3_mat.edEXT.Logica.DataTypes.*;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorUsuario;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorInstituto;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorUsuario;
import com.grupo3_mat.edEXT.Logica.Seguridad.PasswordHasher;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ControladorUsuario implements IControladorUsuario {

    public ControladorUsuario() {
    }

    @Override
    public void altaUsuario(String nickname, String nombre, String apellido, String correo, 
                            LocalDate fechaNac, String imagenPath, String nomInstituto, String password) throws Exception {
        // La firma antigua sigue funcionando: transforma el instituto único en una lista de cero o uno.
        List<String> institutos = nomInstituto == null || nomInstituto.isBlank()
                ? List.of()
                : List.of(nomInstituto);
        altaUsuarioConInstitutos(nickname, nombre, apellido, correo, fechaNac, imagenPath, institutos, password);
    }

    @Override
    public void altaUsuarioConInstitutos(String nickname, String nombre, String apellido, String correo,
                                         LocalDate fechaNac, String imagenPath, List<String> nombresInstitutos,
                                         String password) throws Exception {
        // Sin contraseña no se crea ninguna cuenta.
        if (password == null || password.isBlank()) {
            throw new Exception("La contraseña es obligatoria.");
        }
        ManejadorUsuario mu = ManejadorUsuario.getInstancia();

        // El nickname y el correo son identificadores únicos.
        if (mu.buscarUsuarioPorNickname(nickname) != null) {
            throw new Exception("El nickname ya está en uso.");
        }
        if (mu.buscarUsuarioPorCorreo(correo) != null) {
            throw new Exception("El correo electrónico ya está registrado.");
        }

        // Se convierten los nombres elegidos en institutos reales y se rechazan repetidos o inexistentes.
        List<Instituto> institutos = new ArrayList<>();
        Set<String> nombresAgregados = new HashSet<>();
        if (nombresInstitutos != null) {
            ManejadorInstituto mi = ManejadorInstituto.getInstancia();
            for (String nombreInstituto : nombresInstitutos) {
                if (nombreInstituto == null || nombreInstituto.isBlank()) {
                    throw new Exception("Los nombres de instituto seleccionados no pueden estar vacíos.");
                }
                String nombreNormalizado = nombreInstituto.trim();
                if (!nombresAgregados.add(nombreNormalizado)) {
                    throw new Exception("El instituto '" + nombreNormalizado + "' fue seleccionado más de una vez.");
                }
                Instituto instituto = mi.buscarInstituto(nombreNormalizado);
                if (instituto == null) {
                    throw new Exception("El instituto '" + nombreNormalizado + "' no existe.");
                }
                institutos.add(instituto);
            }
        }

        // La contraseña se guarda protegida, nunca como texto legible.
        String passwordHash = PasswordHasher.hash(password);
        Usuario nuevoUsuario;
        // Tener al menos un instituto define al usuario como docente; sin institutos se crea estudiante.
        if (!institutos.isEmpty()) {
            nuevoUsuario = new Docente(nickname, nombre, apellido, correo, fechaNac, imagenPath, passwordHash, institutos);
        } else {
            nuevoUsuario = new Estudiante(nickname, nombre, apellido, correo, fechaNac, imagenPath, passwordHash);
        }

        mu.agregarUsuario(nuevoUsuario);
    }

    @Override
    public List<String> listarNicknamesUsuarios() {
        ManejadorUsuario mu = ManejadorUsuario.getInstancia();
        List<Usuario> usuarios = mu.getUsuarios();
        List<String> nicknames = new ArrayList<>();

        if (usuarios != null) {
            for (Usuario u : usuarios) {
                nicknames.add(u.getNickname());
            }
        }
        return nicknames;
    }

    @Override
    public DtUsuario obtenerInfoUsuario(String nickname) {
        ManejadorUsuario mu = ManejadorUsuario.getInstancia();
        Usuario usuario = mu.buscarUsuarioPorNickname(nickname);
        if (usuario == null) {
            return null;
        }

        List<String> seguidores = usuario.getNicknamesSeguidores();
        List<String> seguidos = usuario.getNicknamesSeguidos();
        // Cada tipo de usuario se convierte al DTO que representa sus relaciones propias.
        if (usuario instanceof Docente docente) {
            List<String> nombresInstitutos = new ArrayList<>();
            for (Instituto instituto : docente.getInstitutos()) {
                nombresInstitutos.add(instituto.getNombre());
            }

            List<String> edicionesAsociadas = new ArrayList<>();
            for (EdicionCurso edicion : docente.getEdiciones()) {
                edicionesAsociadas.add(edicion.getNombre());
            }

            List<String> programas = new ArrayList<>();
            for (ProgramaFormacion programa : docente.getProgramas()) {
                programas.add(programa.getNombre());
            }

            return new DtDocente(
                    docente.getNickname(), docente.getNombre(), docente.getApellido(),
                    docente.getCorreo(), docente.getFechaNacimiento(), docente.getImagenPath(),
                    seguidores, seguidos, nombresInstitutos, edicionesAsociadas, programas
            );
        }

        Estudiante estudiante = (Estudiante) usuario;
        List<DTInscripcionEdicion> inscripcionesEdicion = new ArrayList<>();
        if (estudiante.getInscripciones() != null) {
            for (InscripcionEC inscripcion : estudiante.getInscripciones().values()) {
                if (inscripcion != null && inscripcion.getEdicion() != null) {
                    inscripcionesEdicion.add(new DTInscripcionEdicion(
                            estudiante.getNickname(), inscripcion.getEdicion().getNombre(),
                            inscripcion.getFecha(), inscripcion.getEstado(), inscripcion.getPrioridad()
                    ));
                }
            }
        }

        List<String> programas = new ArrayList<>();
        for (InscripcionPF inscripcion : estudiante.getInscripcionesProgramas()) {
            if (inscripcion.getPrograma() != null) {
                programas.add(inscripcion.getPrograma().getNombre());
            }
        }

        return new DtEstudiante(
                estudiante.getNickname(), estudiante.getNombre(), estudiante.getApellido(),
                estudiante.getCorreo(), estudiante.getFechaNacimiento(), estudiante.getImagenPath(),
                seguidores, seguidos, inscripcionesEdicion, programas
        );
    }

    @Override
    public void modificarDatosUsuario(String nickname, String nombre, String apellido, LocalDate fechaNac, String imagenPath) throws Exception {
        ManejadorUsuario mu = ManejadorUsuario.getInstancia();
        Usuario u = mu.buscarUsuarioPorNickname(nickname);

        if (u == null) {
            throw new Exception("El usuario seleccionado no existe.");
        }

        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setFechaNacimiento(fechaNac);
        u.setImagenPath(imagenPath);

        mu.modificarUsuario(u);
    }

    @Override
    public List<String> listarNicknamesDocentesPorInstituto(String nomInstituto) {
        ManejadorUsuario mu = ManejadorUsuario.getInstancia();
        List<Usuario> usuarios = mu.getUsuarios();
        List<String> docentes = new ArrayList<>();

        if (usuarios != null) {
            for (Usuario u : usuarios) {
                if (u instanceof Docente) {
                    Docente d = (Docente) u;
                    // Un docente aparece si pertenece al instituto, aunque también trabaje en otros.
                    if (d.perteneceAInstituto(nomInstituto)) {
                        docentes.add(d.getNickname());
                    }
                }
            }
        }
        return docentes;
    }

    @Override
    public List<String> listarEstudiantes() {
        List<String> estudiantes = new ArrayList<>();
        ManejadorUsuario mu = ManejadorUsuario.getInstancia();
        List<Usuario> usuarios = mu.getUsuarios();

        if (usuarios != null) {
            for (Usuario u : usuarios) {
                if (u instanceof Estudiante) {
                    estudiantes.add(u.getNickname());
                }
            }
        }
        return estudiantes;
    }

    @Override
    public List<String> listarDocentes() {
        List<String> docentes = new ArrayList<>();
        ManejadorUsuario mu = ManejadorUsuario.getInstancia();
        List<Usuario> usuarios = mu.getUsuarios();

        if (usuarios != null) {
            for (Usuario u : usuarios) {
                if (u instanceof Docente) {
                    docentes.add(u.getNickname());
                }
            }
        }
        return docentes;
    }

    @Override
    public boolean iniciarSesion(String nicknameOrEmail, String password) throws Exception {
        if (nicknameOrEmail == null || nicknameOrEmail.isBlank() || password == null || password.isEmpty()) {
            throw new Exception("Ingrese usuario y contraseña.");
        }

        ManejadorUsuario mu = ManejadorUsuario.getInstancia();
        // Primero se intenta encontrar la cuenta por nickname y luego por correo.
        Usuario u = mu.buscarUsuarioPorNickname(nicknameOrEmail);

        if (u == null) {
            u = mu.buscarUsuarioPorCorreo(nicknameOrEmail);
        }

        if (u == null) {
            throw new Exception("El usuario o correo ingresado no existe.");
        }

        // El hasher también acepta claves antiguas para poder migrarlas al iniciar sesión.
        String passwordAlmacenada = u.getPassword();
        if (!PasswordHasher.verify(password, passwordAlmacenada)) {
            throw new Exception("Contraseña incorrecta.");
        }

        // Una autenticación válida actualiza el hash si fue creado con un formato viejo.
        if (PasswordHasher.needsRehash(passwordAlmacenada)) {
            u.setPassword(PasswordHasher.hash(password));
            mu.modificarUsuario(u);
        }

        return true;
    }

    @Override
    public void seguirUsuario(String nicknameSeguidor, String nicknameASeguir) throws Exception {
        if (nicknameSeguidor.equals(nicknameASeguir)) {
            throw new Exception("Un usuario no puede seguirse a sí mismo.");
        }

        ManejadorUsuario mu = ManejadorUsuario.getInstancia();
        Usuario seguidor = mu.buscarUsuarioPorNickname(nicknameSeguidor);
        Usuario seguido = mu.buscarUsuarioPorNickname(nicknameASeguir);

        if (seguidor == null || seguido == null) {
            throw new Exception("Uno o ambos usuarios no existen.");
        }

        if (seguidor.sigueA(nicknameASeguir)) {
            throw new Exception("Ya sigues a este usuario.");
        }

        seguidor.agregarSeguido(seguido);
        seguido.agregarSeguidor(seguidor);

        mu.modificarUsuario(seguidor);
        mu.modificarUsuario(seguido);
    }

    @Override
    public void dejarDeSeguirUsuario(String nicknameSeguidor, String nicknameASeguir) throws Exception {
        ManejadorUsuario mu = ManejadorUsuario.getInstancia();
        Usuario seguidor = mu.buscarUsuarioPorNickname(nicknameSeguidor);
        Usuario seguido = mu.buscarUsuarioPorNickname(nicknameASeguir);

        if (seguidor == null || seguido == null) {
            throw new Exception("Uno o ambos usuarios no existen.");
        }

        if (!seguidor.sigueA(nicknameASeguir)) {
            throw new Exception("No sigues a este usuario.");
        }

        seguidor.removerSeguido(seguido);
        seguido.removerSeguidor(seguidor);

        mu.modificarUsuario(seguidor);
        mu.modificarUsuario(seguido);
    }
}