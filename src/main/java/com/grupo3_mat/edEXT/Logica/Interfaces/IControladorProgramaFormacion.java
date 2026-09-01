package com.grupo3_mat.edEXT.Logica.Interfaces;

import com.grupo3_mat.edEXT.Logica.Clases.Curso;
import com.grupo3_mat.edEXT.Logica.Clases.ProgramaFormacion;
import java.util.Date;
import java.util.List;
/**
 *
 * @author ssant
 */
public interface IControladorProgramaFormacion {
    
    //CU9 : Crear Programa de Formación
    public abstract void crearProgramaFormacion(String nombre, String descripcion, Date fechaInicio, Date fechaFin, Date fechaAlta) throws Exception;
    
    //CU10 : Agregar Cruso a Programa de Formación
    public abstract void agregarCursoAPrograma(String nombrePrograma, String nombreCurso) throws Exception;
    
    //CU11 : Consulta de Programa de Formación
    public abstract List<ProgramaFormacion> listarProgramas() throws Exception;
    public abstract ProgramaFormacion seleccionarPrograma(String nombre) throws Exception;
    public abstract Curso seleccionarCurso(String nombreCurso) throws Exception;
    
}
