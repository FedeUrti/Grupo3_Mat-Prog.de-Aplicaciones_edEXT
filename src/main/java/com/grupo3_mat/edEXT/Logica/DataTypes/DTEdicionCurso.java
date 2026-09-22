package com.grupo3_mat.edEXT.Logica.DataTypes;

import java.time.LocalDate;
import java.util.List;

public class DTEdicionCurso {

    private final String nombre;
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    private final int cupo;
    private final int cupoDisponible;
    private final LocalDate fechaPublicacion;
    private final List<String> docentes; // Lista con los nicknames de docentes

    // Constructor completo (para Consulta)
    public DTEdicionCurso(String nombre, LocalDate fechaInicio, LocalDate fechaFin, int cupo, int cupoDisponible, LocalDate fechaPublicacion, List<String> docentes) {
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cupo = cupo;
        this.cupoDisponible = cupoDisponible;
        this.fechaPublicacion = fechaPublicacion;
        this.docentes = docentes;
    }
        
    public String getNombre() {
        return nombre;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public int getCupo() {
        return cupo;
    }
    
    public int getCupoDisponible() {
        return cupoDisponible;
    }

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    public List<String> getDocentes() {
        return docentes;
    }
}
