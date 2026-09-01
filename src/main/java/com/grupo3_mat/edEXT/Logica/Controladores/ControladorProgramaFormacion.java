package com.grupo3_mat.edEXT.Logica.Controladores;

import com.grupo3_mat.edEXT.Logica.Clases.Curso;
import com.grupo3_mat.edEXT.Logica.Clases.ProgramaFormacion;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorProgramaFormacion;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorCurso;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorProgramaFormacion;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ssant
 */
public class ControladorProgramaFormacion implements IControladorProgramaFormacion {
    
    @Override
    public void crearProgramaFormacion(String nombre, String descripcion, Date fechaInicio, Date fechaFin, Date fechaAlta) throws Exception {
    ManejadorProgramaFormacion mpf = ManejadorProgramaFormacion.getInstancia();
    
    //1. Validar que no exista un programa con e mismo nombre
    if (mpf.buscarPrograma(nombre) != null) {
        throw new Exception("Ya existe un programa de formación con el nombre: " + nombre);
    }
    
    //2. Instanciar la entidad
    ProgramaFormacion pf = new ProgramaFormacion(nombre, descripcion, fechaInicio, fechaFin, fechaAlta);
    
    
    //3. Permitir que el manejador lo persista
    mpf.agregarPrograma(pf);
}

    
    @Override
    public void agregarCursoAPrograma(String nombrePrograma, String nombreCurso) throws Exception {   
        ManejadorProgramaFormacion mpf = ManejadorProgramaFormacion.getInstancia();
        ManejadorCurso mc = ManejadorCurso.getInstancia();
        
        //1. Verificar que el programa existe
        ProgramaFormacion pf = mpf.buscarPrograma(nombrePrograma);
        if (pf == null) {
            throw new Exception("No existe el programa de formación ingresado.");
        }
        
        //2.  Verificar que el curso existe
        Curso c = mc.buscarCurso(nombreCurso);
        if(c == null) {
            throw new Exception("No existe el curso ingresado.");
        }
        
        //3. Asocio el curso al programa
        pf.agregarCurso(c);
        
        //4. Actualizo el estado en la BD
        mpf.modificarPrograma(pf);
    }

    @Override
    public List<ProgramaFormacion> listarProgramas() throws Exception {
        ManejadorProgramaFormacion mpf = ManejadorProgramaFormacion.getInstancia();
        return mpf.getProgramas(); 
    }

    @Override
    public ProgramaFormacion seleccionarPrograma(String nombre) throws Exception {
        ManejadorProgramaFormacion mpf = ManejadorProgramaFormacion.getInstancia();
        ProgramaFormacion pf = mpf.buscarPrograma(nombre);
        
        if(pf == null) {
            throw new Exception("El programa de formación seleccionado no existe.");
        }
        return pf;        
    }

    @Override
    public Curso seleccionarCurso(String nombreCurso) throws Exception {
        ManejadorCurso mc = ManejadorCurso.getInstancia();
        Curso c = mc.buscarCurso(nombreCurso);
        
        if( c == null) {
            throw new Exception("El curso seleccionado no existe.");
        }
        return c;
    }
    
}
