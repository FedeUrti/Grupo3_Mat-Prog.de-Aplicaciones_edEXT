package com.grupo3_mat.edEXT.Logica.Interfaces;

import com.grupo3_mat.edEXT.Logica.DataTypes.DtUsuario;
import java.time.LocalDate;
import java.util.List;

public interface IControladorUsuario {
    void altaUsuario(String nickname, String nombre, String apellido, String correo, LocalDate fechaNac, String imagenPath, String nomInstituto, String password) throws Exception;
    // Una lista vacía crea estudiante; una lista con uno o más institutos crea docente.
        void altaUsuarioConInstitutos(String nickname, String nombre, String apellido, String correo, LocalDate fechaNac, String imagenPath, List<String> nombresInstitutos, String password) throws Exception;
    List<String> listarNicknamesUsuarios();
    DtUsuario obtenerInfoUsuario(String nickname);
    void modificarDatosUsuario(String nickname, String nombre, String apellido, LocalDate fechaNac, String imagenPath) throws Exception;
    List<String> listarNicknamesDocentesPorInstituto(String nomInstituto);
    List<String> listarEstudiantes();
    List<String> listarDocentes();
    
    // Métodos faltantes de la Tarea 2
    boolean iniciarSesion(String nicknameOrEmail, String password) throws Exception;
    void seguirUsuario(String nicknameSeguidor, String nicknameASeguir) throws Exception;
    void dejarDeSeguirUsuario(String nicknameSeguidor, String nicknameASeguir) throws Exception;
}