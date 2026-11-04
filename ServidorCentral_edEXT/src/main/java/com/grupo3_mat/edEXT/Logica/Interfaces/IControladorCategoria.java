package com.grupo3_mat.edEXT.Logica.Interfaces;

import java.util.List;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService(name = "CategoriaSoap", targetNamespace = "http://soap.edext.grupo3_mat.com/")
public interface IControladorCategoria {
    void altaCategoria(@WebParam(name = "nombre") String nombre) throws Exception;
    void modificarCategoria(@WebParam(name = "nombreActual") String nombreActual,
            @WebParam(name = "nuevoNombre") String nuevoNombre) throws Exception;
    List<String> listarCategorias();
}