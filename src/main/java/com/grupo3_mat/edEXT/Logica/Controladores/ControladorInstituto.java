package com.grupo3_mat.edEXT.Logica.Controladores;

import com.grupo3_mat.edEXT.Logica.Clases.Instituto;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorInstituto;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorInstituto;
import java.util.ArrayList;
import java.util.List;

public class ControladorInstituto implements IControladorInstituto {

    @Override
    public void altaInstituto(String nombre) {
        ManejadorInstituto mi = ManejadorInstituto.getInstancia();
        if (mi.buscarInstituto(nombre) != null) {
            throw new IllegalArgumentException("Ya existe un instituto con el nombre '" + nombre + "'.");
        }
        mi.agregarInstituto(new Instituto(nombre));
    }

    @Override
    public List<String> listarInstitutos() {
        ManejadorInstituto mi = ManejadorInstituto.getInstancia();
        List<Instituto> institutos = mi.listarInstitutos();
        List<String> nombres = new ArrayList<>();
        for (Instituto inst : institutos) {
            nombres.add(inst.getNombre());
        }
        return nombres;
    }
}