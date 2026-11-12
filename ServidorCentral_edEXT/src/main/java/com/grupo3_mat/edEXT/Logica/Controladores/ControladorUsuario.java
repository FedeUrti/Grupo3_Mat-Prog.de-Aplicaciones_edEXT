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
import jakarta.jws.WebService;

@WebService(endpointInterface = "com.grupo3_mat.edEXT.Logica.Interfaces.IControladorUsuario",
        serviceName = "UsuarioSoapService", portName = "UsuarioSoapPort",
        targetNamespace = "http://soap.edext.grupo3_mat.com/")
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
        return ManejadorUsuario.getInstancia().listarNicknamesUsuarios();
    }

    @Override
    public String resolverNickname(String identificador) {
        if (identificador == null || identificador.isBlank()) {
            return null;
        }
        String valor = identificador.trim();
        ManejadorUsuario manejador = ManejadorUsuario.getInstancia();
        Usuario usuario = manejador.buscarUsuarioPorNickname(valor);
        if (usuario == null) {
            usuario = manejador.buscarUsuarioPorCorreo(valor);
        }
        return usuario == null ? null : usuario.getNickname();
    }

    @Override
    public DtUsuario obtenerInfoUsuario(String nickname) {
        ManejadorUsuario mu = ManejadorUsuario.getInstancia();
        Usuario usuario = mu.buscarUsuarioPorNickname(nickname);
        if (usuario == null) {
            return null;
        }

        return convertirUsuario(usuario);
    }

    @Override
    public DtUsuario obtenerInfoUsuarioBasica(String nickname) {
        ManejadorUsuario.DatosUsuarioBasicos datos =
                ManejadorUsuario.getInstancia().buscarDatosBasicosPorNickname(nickname);
        if (datos == null) {
            return null;
        }
        if (datos.docente()) {
            return new DtDocente(datos.nickname(), datos.nombre(), datos.apellido(), datos.correo(),
                    datos.fechaNacimiento(), datos.imagenPath(), List.of(), List.of(),
                    List.of(), List.of(), List.of());
        }
        return new DtEstudiante(datos.nickname(), datos.nombre(), datos.apellido(), datos.correo(),
                datos.fechaNacimiento(), datos.imagenPath(), List.of(), List.of(),
                List.of(), List.of());
    }

    private DtUsuario convertirUsuario(Usuario usuario) {
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
                            inscripcion.getFecha(), inscripcion.getEstado(), inscripcion.getPrioridad(),
                            inscripcion.getEnlaceVideo(), inscripcion.getEvaluacion(),
                            inscripcion.getEdicion().isCerrada()
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
    public List<DtUsuario> obtenerInfoUsuarios(List<String> nicknames) {
        List<DtUsuario> usuarios = new ArrayList<>();
        if (nicknames == null) {
            return usuarios;
        }
        if (nicknames.size() > 100) {
            throw new IllegalArgumentException("La consulta por lotes admite hasta 100 usuarios.");
        }
        List<String> solicitados = nicknames.stream()
                .filter(nickname -> nickname != null && !nickname.isBlank())
                .distinct()
                .toList();
        for (Usuario usuario : ManejadorUsuario.getInstancia().buscarUsuariosPorNicknames(solicitados)) {
            usuarios.add(convertirUsuario(usuario));
        }
        return usuarios;
    }

    @Override
    public void modificarDatosUsuario(String nickname, String nombre, String apellido, LocalDate fechaNac, String imagenPath) throws Exception {
        ManejadorUsuario.getInstancia().actualizarDatosBasicos(
                nickname, nombre, apellido, fechaNac, imagenPath);
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
        return ManejadorUsuario.getInstancia().listarNicknamesEstudiantes();
    }

    @Override
    public List<String> listarDocentes() {
        return ManejadorUsuario.getInstancia().listarNicknamesDocentes();
    }

    @Override
    public boolean iniciarSesion(String nicknameOrEmail, String password) throws Exception {
        autenticarUsuario(nicknameOrEmail, password);
        return true;
    }

    @Override
    public DtUsuario iniciarSesionConDatos(String nicknameOrEmail, String password) throws Exception {
        if (nicknameOrEmail == null || nicknameOrEmail.isBlank() || password == null || password.isEmpty()) {
            throw new Exception("Ingrese usuario y contraseña.");
        }

        ManejadorUsuario manejador = ManejadorUsuario.getInstancia();
        ManejadorUsuario.DatosAcceso datos = manejador.buscarDatosAcceso(nicknameOrEmail.trim());
        if (datos == null) {
            throw new Exception("El usuario o correo ingresado no existe.");
        }
        if (!PasswordHasher.verify(password, datos.passwordHash())) {
            throw new Exception("Contraseña incorrecta.");
        }
        if (PasswordHasher.needsRehash(datos.passwordHash())) {
            manejador.actualizarPassword(datos.nickname(), PasswordHasher.hash(password));
        }

        if (datos.docente()) {
            return new DtDocente(datos.nickname(), datos.nombre(), datos.apellido(), datos.correo(),
                    datos.fechaNacimiento(), datos.imagenPath(), List.of(), List.of(),
                    List.of(), List.of(), List.of());
        }
        return new DtEstudiante(datos.nickname(), datos.nombre(), datos.apellido(), datos.correo(),
                datos.fechaNacimiento(), datos.imagenPath(), List.of(), List.of(), List.of(), List.of());
    }

    private Usuario autenticarUsuario(String nicknameOrEmail, String password) throws Exception {
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

        return u;
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
        ManejadorUsuario.getInstancia().dejarDeSeguirUsuario(nicknameSeguidor, nicknameASeguir);
    }
}