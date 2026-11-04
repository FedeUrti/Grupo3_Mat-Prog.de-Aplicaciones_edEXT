package com.grupo3_mat.edEXT.Logica.DataTypes;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.util.LinkedHashMap;
import java.util.Map;

public class ValoracionesAdapter extends XmlAdapter<ValoracionesXml, Map<Integer, Integer>> {
    @Override
    public Map<Integer, Integer> unmarshal(ValoracionesXml xml) {
        if (xml == null) {
            return null;
        }
        Map<Integer, Integer> resultado = new LinkedHashMap<>();
        for (ValoracionesXml.Entrada entrada : xml.getValoraciones()) {
            resultado.put(entrada.getPuntaje(), entrada.getCantidad());
        }
        return resultado;
    }

    @Override
    public ValoracionesXml marshal(Map<Integer, Integer> valoraciones) {
        if (valoraciones == null) {
            return null;
        }
        ValoracionesXml xml = new ValoracionesXml();
        for (Map.Entry<Integer, Integer> entrada : valoraciones.entrySet()) {
            xml.getValoraciones().add(new ValoracionesXml.Entrada(entrada.getKey(), entrada.getValue()));
        }
        return xml;
    }
}
