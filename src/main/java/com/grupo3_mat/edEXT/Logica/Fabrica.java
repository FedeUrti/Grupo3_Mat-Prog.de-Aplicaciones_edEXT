package com.grupo3_mat.edEXT.Logica;

import com.grupo3_mat.edEXT.Logica.Controladores.*;
import com.grupo3_mat.edEXT.Logica.Interfaces.*;

public class Fabrica {

    private static Fabrica instancia = null;

    private Fabrica() {
    }

    public static Fabrica getInstance() {
        if (instancia == null) {
            instancia = new Fabrica();
        }
        return instancia;
    }

    // Un método por cada controlador de tu sistema
    public IControladorCurso getIControladorCurso() {
        return new ControladorCurso();
    }

    public IControladorInstituto getIControladorInstituto() {
        return new ControladorInstituto();
    }

    /*public IControladorUsuario getIControladorUsuario() {
        return new ControladorUsuario();
    }*/
}