package com.grupo3_mat.edEXT.Logica.DataTypes;

public class DTCategoria {
    private final String nombre;

    public DTCategoria(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() { return nombre; }

    @Override
    public String toString() { return nombre; }
}