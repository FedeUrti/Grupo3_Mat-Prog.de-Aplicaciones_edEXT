package com.grupo3_mat.edEXT.Logica.Interfaces;

import com.grupo3_mat.edEXT.Logica.Clases.Curso;
import com.grupo3_mat.edEXT.Logica.Clases.ProgramaFormacion;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTProgramaFormacion;
import java.util.Date;
import java.util.List;

public interface IControladorProgramaFormacion {

    public abstract void crearProgramaFormacion(String nombre, String descripcion, Date fechaInicio, Date fechaFin, Date fechaAlta) throws Exception;
    public abstract void agregarCursoAPrograma(String nombrePrograma, String nombreCurso) throws Exception;
    public abstract List<ProgramaFormacion> listarProgramas() throws Exception;
    public abstract ProgramaFormacion seleccionarPrograma(String nombre) throws Exception;
    public abstract Curso seleccionarCurso(String nombreCurso) throws Exception;

    // Nuevo método para obtener los datos encapsulados en DataType
    public abstract DTProgramaFormacion consultarPrograma(String nombre) throws Exception;
}