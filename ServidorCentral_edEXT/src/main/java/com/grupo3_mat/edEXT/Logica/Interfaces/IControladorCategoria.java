package com.grupo3_mat.edEXT.Logica.Interfaces;

import java.util.List;

public interface IControladorCategoria {
    void altaCategoria(String nombre) throws Exception;
    void modificarCategoria(String nombreActual, String nuevoNombre) throws Exception;
    List<String> listarCategorias();
}