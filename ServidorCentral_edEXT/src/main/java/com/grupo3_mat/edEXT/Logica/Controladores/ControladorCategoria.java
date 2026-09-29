package com.grupo3_mat.edEXT.Logica.Controladores;

import com.grupo3_mat.edEXT.Logica.Clases.Categoria;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCategoria;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorCategoria;
import java.util.ArrayList;
import java.util.List;

public class ControladorCategoria implements IControladorCategoria {

    @Override
    public void altaCategoria(String nombre) throws Exception {
        nombre = nombre.trim();
        ManejadorCategoria mc = ManejadorCategoria.getInstancia();
        if (mc.buscarCategoria(nombre) != null) {
            throw new Exception("La categoría '" + nombre + "' ya existe.");
        }
        mc.agregarCategoria(new Categoria(nombre));
    }

    @Override
    public void modificarCategoria(String nombreActual, String nuevoNombre) throws Exception {
        String nombreOriginal = nombreActual != null ? nombreActual.trim() : "";
        String nombreNuevo = nuevoNombre != null ? nuevoNombre.trim() : "";
        if (nombreOriginal.isEmpty() || nombreNuevo.isEmpty()) {
            throw new Exception("El nombre de la categoría es obligatorio.");
        }

        ManejadorCategoria mc = ManejadorCategoria.getInstancia();
        if (mc.buscarCategoria(nombreOriginal) == null) {
            throw new Exception("La categoría '" + nombreOriginal + "' no existe.");
        }
        if (!nombreOriginal.equals(nombreNuevo) && mc.buscarCategoria(nombreNuevo) != null) {
            throw new Exception("La categoría '" + nombreNuevo + "' ya existe.");
        }
        if (!nombreOriginal.equals(nombreNuevo)) {
            mc.modificarCategoria(nombreOriginal, nombreNuevo);
        }
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