package com.grupo3_mat.edEXT.Logica.Controladores;

import com.grupo3_mat.edEXT.Logica.Clases.Curso;
import com.grupo3_mat.edEXT.Logica.Clases.ProgramaFormacion;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTProgramaFormacion;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorProgramaFormacion;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorCurso;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorProgramaFormacion;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ControladorProgramaFormacion implements IControladorProgramaFormacion {
    
    @Override
    public void crearProgramaFormacion(String nombre, String descripcion, Date fechaInicio, Date fechaFin, Date fechaAlta) throws Exception {
        ManejadorProgramaFormacion mpf = ManejadorProgramaFormacion.getInstancia();
        
        // 1. Validar que no exista un programa con el mismo nombre
        if (mpf.buscarPrograma(nombre) != null) {
            throw new Exception("Ya existe un programa de formación con el nombre: " + nombre);
        }
        
        // 2. Instanciar la entidad
        ProgramaFormacion pf = new ProgramaFormacion(nombre, descripcion, fechaInicio, fechaFin, fechaAlta);
        
        // 3. Permitir que el manejador lo persista
        mpf.agregarPrograma(pf);
    }

    @Override
    public void agregarCursoAPrograma(String nombrePrograma, String nombreCurso) throws Exception {   
        ManejadorProgramaFormacion mpf = ManejadorProgramaFormacion.getInstancia();
        // Delegamos la asociación completa al manejador para que se ejecute en una sola transacción
        mpf.agregarCursoAPrograma(nombrePrograma, nombreCurso);
    }

    // Devuelve la lista completa de programas registrados en el sistema
    @Override
    public List<ProgramaFormacion> listarProgramas() throws Exception {
        ManejadorProgramaFormacion mpf = ManejadorProgramaFormacion.getInstancia();
        return mpf.getProgramas(); 
    }

    // Busca y devuelve un programa por su nombre
    @Override
    public ProgramaFormacion seleccionarPrograma(String nombre) throws Exception {
        ManejadorProgramaFormacion mpf = ManejadorProgramaFormacion.getInstancia();
        ProgramaFormacion pf = mpf.buscarPrograma(nombre);
        
        if (pf == null) {
            throw new Exception("El programa de formación seleccionado no existe.");
        }
        return pf;        
    }

    // Busca los datos de un curso específico por su nombre
    @Override
    public Curso seleccionarCurso(String nombreCurso) throws Exception {
        ManejadorCurso mc = ManejadorCurso.getInstancia();
        Curso c = mc.buscarCurso(nombreCurso);
        
        if (c == null) {
            throw new Exception("El curso seleccionado no existe.");
        }
        return c;
    }
    
    @Override
    public DTProgramaFormacion consultarPrograma(String nombre) throws Exception {
        ProgramaFormacion pf = seleccionarPrograma(nombre);

        List<String> nombresCursos = new ArrayList<>();
        if (pf.getCursos() != null) {
            for (Curso c : pf.getCursos().values()) {
                nombresCursos.add(c.getNombre());
            }
        }

        return new DTProgramaFormacion(
                pf.getNombre(),
                pf.getDescripcion(),
                pf.getFechaInicio(),
                pf.getFechaFin(),
                pf.getFechaAlta(),
                nombresCursos
        );
    }

}