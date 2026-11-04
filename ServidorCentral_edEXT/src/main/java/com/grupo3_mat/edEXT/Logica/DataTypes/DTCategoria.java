package com.grupo3_mat.edEXT.Logica.DataTypes;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class DTCategoria {
    private String nombre;

    public DTCategoria() {
    }

    public DTCategoria(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() { return nombre; }

    @Override
    public String toString() { return nombre; }
}