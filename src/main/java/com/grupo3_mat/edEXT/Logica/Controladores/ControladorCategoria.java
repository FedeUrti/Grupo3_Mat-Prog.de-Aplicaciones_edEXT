package com.grupo3_mat.edEXT.Logica.Controladores;

import com.grupo3_mat.edEXT.Logica.Clases.Categoria;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCategoria;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorCategoria;
import java.util.ArrayList;
import java.util.List;

public class ControladorCategoria implements IControladorCategoria {

    @Override
    public void altaCategoria(String nombre) throws Exception {
        ManejadorCategoria mc = ManejadorCategoria.getInstancia();
        if (mc.buscarCategoria(nombre) != null) {
            throw new Exception("La categoría '" + nombre + "' ya existe.");
        }
        mc.agregarCategoria(new Categoria(nombre));
    }

    @Override
    public List<String> listarCategorias() {
        ManejadorCategoria mc = ManejadorCategoria.getInstancia();
        List<Categoria> categorias = mc.listarCategorias();
        List<String> nombres = new ArrayList<>();
        for (Categoria cat : categorias) {
            nombres.add(cat.getNombre());
        }
        return nombres;
    }
}