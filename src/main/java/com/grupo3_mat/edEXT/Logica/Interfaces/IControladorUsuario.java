package com.grupo3_mat.edEXT.Logica.Interfaces;

import com.grupo3_mat.edEXT.Logica.DataTypes.DtUsuario;
import java.time.LocalDate;
import java.util.List;

public interface IControladorUsuario {

    /**
     * Da de alta a un nuevo usuario (Docente o Estudiante).
     */
    void altaUsuario(String nickname, String nombre, String apellido, String correo, LocalDate fechaNac, String imagenPath, String nomInstituto) throws Exception;

    /**
     * Lista todos los nicknames de los usuarios registrados en el sistema.
     */
    List<String> listarNicknamesUsuarios();

    /**
     * Obtiene la información detallada de un usuario específico usando su nickname.
     * Retorna un DtUsuario (que puede ser DtDocente o DtEstudiante).
     */
    DtUsuario obtenerInfoUsuario(String nickname);

    /**
     * Modifica los datos editables de un usuario existente.
     */
    void modificarDatosUsuario(String nickname, String nombre, String apellido, LocalDate fechaNac, String imagenPath) throws Exception;
    
    List<String> listarNicknamesDocentesPorInstituto(String nomInstituto);
    
    List<String> listarEstudiantes();
}