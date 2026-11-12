package com.grupo3_mat.edEXT.Logica.Interfaces;

import com.grupo3_mat.edEXT.Logica.DataTypes.DtResultadoBusqueda;
import java.util.List;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService(name = "BusquedaSoap", targetNamespace = "http://soap.edext.grupo3_mat.com/")
public interface IControladorBusqueda {
    List<DtResultadoBusqueda> buscarResultados(
            @WebParam(name = "consulta") String consulta,
            @WebParam(name = "tipo") String tipo,
            @WebParam(name = "nicknameSesion") String nicknameSesion,
            @WebParam(name = "ordenarPor") String ordenarPor,
            @WebParam(name = "direccion") String direccion,
            @WebParam(name = "limitePorTipo") int limitePorTipo);
}
