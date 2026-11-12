package com.grupo3_mat.edEXT.Logica.Controladores;

import com.grupo3_mat.edEXT.Logica.DataTypes.DtResultadoBusqueda;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorBusqueda;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorBusqueda;
import jakarta.jws.WebService;
import java.util.List;

@WebService(endpointInterface = "com.grupo3_mat.edEXT.Logica.Interfaces.IControladorBusqueda",
        serviceName = "BusquedaSoapService", portName = "BusquedaSoapPort",
        targetNamespace = "http://soap.edext.grupo3_mat.com/")
public class ControladorBusqueda implements IControladorBusqueda {
    @Override
    public List<DtResultadoBusqueda> buscarResultados(String consulta, String tipo, String nicknameSesion,
            String ordenarPor, String direccion, int limitePorTipo) {
        return ManejadorBusqueda.buscar(consulta, tipo, nicknameSesion, ordenarPor, direccion, limitePorTipo);
    }
}
