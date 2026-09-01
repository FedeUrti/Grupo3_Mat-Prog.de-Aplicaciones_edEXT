package com.grupo3_mat.edEXT.Logica.DataTypes;

import java.util.Date;
import java.util.List;

/**
 *
 * @author ssant
 */
public class DTProgramaFormacion {
    
    private final String nombre;
    private final String descripcion;
    private final Date fechaInicio;
    private final Date fechaFin;
    private final Date fechaAlta;
    private final List<String> cursos;
    
    public DTProgramaFormacion(String nombre, String descripcion, Date fechaInicio, Date fechaFin, Date fechaAlta, List<String> cursos) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.fechaAlta = fechaAlta;
        this.cursos = cursos;
    }
    
    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public List<String> getCursos() {
        return cursos;
    }
    
    //Sobrecribo toString para que el JComboBox muetrre el nombre directamente 
    @Override
    public String toString() {
        return nombre;
    }
}
