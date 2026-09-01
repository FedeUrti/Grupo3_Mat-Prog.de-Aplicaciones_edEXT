/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.grupo3_mat.edEXT.Logica;

import com.grupo3_mat.edEXT.Logica.Controladores.*;
import com.grupo3_mat.edEXT.Logica.Interfaces.*;

public class Fabrica {

    private static Fabrica instancia = null;

    private Fabrica() {}

    public static Fabrica getInstance() {
        if (instancia == null) {
            instancia = new Fabrica();
        }
        return instancia;
    }

    public IControladorCurso getIControladorCurso() {
        return new ControladorCurso();
    }
    
    public IControladorInstituto getIControladorInstituto() {
        return new ControladorInstituto();
    }

    public IControladorEdicion getIControladorEdicion() {
        return new ControladorEdicion();
    }

    public IControladorUsuario getIControladorUsuario() {
        return new ControladorUsuario();
    }
    
    public IControladorProgramaFormacion getIControladorProgramaFormacion() {
        return new ControladorProgramaFormacion();
    }   
}