package com.grupo3_mat.edEXT.Logica.Interfaces;

import com.grupo3_mat.edEXT.Logica.DataTypes.DtCurso;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTProgramaFormacion;
import java.time.LocalDate;
import java.util.List;

public interface IControladorProgramaFormacion {
    /** Crea un programa asociado al docente que será responsable de gestionarlo. */
    void crearProgramaFormacion(String nicknameDocente, String nombre, String descripcion, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaAlta, String imagenPath) throws Exception;
    /** Firma antigua, conservada para que las llamadas viejas reciban un error claro. */
    @Deprecated
    void crearProgramaFormacion(String nombre, String descripcion, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaAlta, String imagenPath) throws Exception;
    void agregarCursoAPrograma(String nombrePrograma, String nombreCurso) throws Exception;
    List<DTProgramaFormacion> listarProgramas() throws Exception;
    DTProgramaFormacion seleccionarPrograma(String nombre) throws Exception;
    DtCurso seleccionarCurso(String nombreCurso) throws Exception;
    DTProgramaFormacion consultarPrograma(String nombre) throws Exception;
    void inscribirEstudianteAPrograma(String nickname, String nombrePrograma, LocalDate fechaInscripcion) throws Exception;
}