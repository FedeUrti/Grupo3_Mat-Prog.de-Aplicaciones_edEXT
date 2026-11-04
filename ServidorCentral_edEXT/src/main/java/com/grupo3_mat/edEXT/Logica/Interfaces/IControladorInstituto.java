package com.grupo3_mat.edEXT.Logica.Interfaces;

import java.util.List;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService(name = "InstitutoSoap", targetNamespace = "http://soap.edext.grupo3_mat.com/")
public interface IControladorInstituto {
    void altaInstituto(@WebParam(name = "nombre") String nombre);
    List<String> listarInstitutos();
}