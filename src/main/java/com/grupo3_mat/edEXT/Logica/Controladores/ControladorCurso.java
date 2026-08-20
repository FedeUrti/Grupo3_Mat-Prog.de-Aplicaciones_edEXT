package com.grupo3_mat.edEXT.Logica.Controladores;

import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCurso;
import com.grupo3_mat.edEXT.Logica.Clases.Curso;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorCurso;
import java.util.Date;

public class ControladorCurso implements IControladorCurso {

    @Override
    public void altaCurso(String nomInst, String cursoNom, String desc, int dur, int cantHoras, int creditos, String url, Date fecha) {
        ManejadorCurso manejador = ManejadorCurso.getInstancia();

        // 1. Validar si el curso ya existe para no duplicar
        if (manejador.buscarCurso(cursoNom) != null) {
            throw new IllegalArgumentException("El curso con nombre '" + cursoNom + "' ya se encuentra registrado.");
        }

        // 2. Crear la entidad
        Curso curso = new Curso(nomInst, cursoNom, desc, dur, cantHoras, creditos, url, fecha);

        // 3. Delegar la persistencia al manejador
        manejador.agregarCurso(curso);
    }
    
    @Override
    public void consultarCurso() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void listarPrevias() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}