package com.grupo3_mat.edEXT.Logica.Interfaces;

import com.grupo3_mat.edEXT.Logica.DataTypes.DtUsuario;
import java.time.LocalDate;
import java.util.List;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import com.grupo3_mat.edEXT.Logica.DataTypes.LocalDateAdapter;

@WebService(name = "UsuarioSoap", targetNamespace = "http://soap.edext.grupo3_mat.com/")
public interface IControladorUsuario {
    void altaUsuario(@WebParam(name = "nickname") String nickname, @WebParam(name = "nombre") String nombre,
            @WebParam(name = "apellido") String apellido, @WebParam(name = "correo") String correo,
            @WebParam(name = "fechaNacimiento") @XmlJavaTypeAdapter(LocalDateAdapter.class) LocalDate fechaNac,
            @WebParam(name = "imagenPath") String imagenPath,
            @WebParam(name = "nombreInstituto") String nomInstituto,
            @WebParam(name = "password") String password) throws Exception;
    // Una lista vacía crea estudiante; una lista con uno o más institutos crea docente.
        void altaUsuarioConInstitutos(@WebParam(name = "nickname") String nickname,
                @WebParam(name = "nombre") String nombre, @WebParam(name = "apellido") String apellido,
                @WebParam(name = "correo") String correo,
                @WebParam(name = "fechaNacimiento") @XmlJavaTypeAdapter(LocalDateAdapter.class)
                LocalDate fechaNac, @WebParam(name = "imagenPath") String imagenPath,
                @WebParam(name = "nombresInstitutos") List<String> nombresInstitutos,
                @WebParam(name = "password") String password) throws Exception;
    List<String> listarNicknamesUsuarios();
    String resolverNickname(@WebParam(name = "identificador") String identificador);
    DtUsuario obtenerInfoUsuario(@WebParam(name = "nickname") String nickname);
    List<DtUsuario> obtenerInfoUsuarios(@WebParam(name = "nicknames") List<String> nicknames);
    DtUsuario iniciarSesionConDatos(@WebParam(name = "identificador") String nicknameOrEmail,
            @WebParam(name = "password") String password) throws Exception;
    void modificarDatosUsuario(@WebParam(name = "nickname") String nickname,
            @WebParam(name = "nombre") String nombre, @WebParam(name = "apellido") String apellido,
            @WebParam(name = "fechaNacimiento") @XmlJavaTypeAdapter(LocalDateAdapter.class)
            LocalDate fechaNac, @WebParam(name = "imagenPath") String imagenPath) throws Exception;
    List<String> listarNicknamesDocentesPorInstituto(@WebParam(name = "nombreInstituto") String nomInstituto);
    List<String> listarEstudiantes();
    List<String> listarDocentes();
    
    // Métodos faltantes de la Tarea 2
    boolean iniciarSesion(@WebParam(name = "identificador") String nicknameOrEmail,
            @WebParam(name = "password") String password) throws Exception;
    void seguirUsuario(@WebParam(name = "nicknameSeguidor") String nicknameSeguidor,
            @WebParam(name = "nicknameASeguir") String nicknameASeguir) throws Exception;
    void dejarDeSeguirUsuario(@WebParam(name = "nicknameSeguidor") String nicknameSeguidor,
            @WebParam(name = "nicknameASeguir") String nicknameASeguir) throws Exception;
}