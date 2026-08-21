package com.grupo3_mat.edEXT.Logica.DataTypes;

import java.time.LocalDate;
import java.util.List;

public class DTEdicionCurso {

    private final String nombre;
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    private final int cupo;
    private final LocalDate fechaPublicacion;
    private final List<String> docentes; // Lista con los nicknames de docentes

    // Constructor completo (para Consulta)
    public DTEdicionCurso(String nombre, LocalDate fechaInicio, LocalDate fechaFin, int cupo, LocalDate fechaPublicacion, List<String> docentes) {
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cupo = cupo;
        this.fechaPublicacion = fechaPublicacion;
        this.docentes = docentes;
    }

    // Constructor para Alta (asigna la fecha actual automáticamente)
    public DTEdicionCurso(String nombre, LocalDate fechaInicio, LocalDate fechaFin, int cupo, List<String> docentes) {
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cupo = cupo;
        this.fechaPublicacion = LocalDate.now();
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

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    public List<String> getDocentes() {
        return docentes;
    }
}
