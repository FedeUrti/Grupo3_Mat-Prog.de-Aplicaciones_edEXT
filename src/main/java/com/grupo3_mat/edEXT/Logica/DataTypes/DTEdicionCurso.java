package com.grupo3_mat.edEXT.Logica.DataTypes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DTEdicionCurso {

    private final String nombre;
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    private final int cupo;
    private final int cupoDisponible;
    private final LocalDate fechaPublicacion;
    private final List<String> docentes;
    private final String imagenPath;

    // Constructor completo de 8 parámetros
    public DTEdicionCurso(String nombre, LocalDate fechaInicio, LocalDate fechaFin, int cupo, 
                           int cupoDisponible, LocalDate fechaPublicacion, List<String> docentes, String imagenPath) {
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cupo = cupo;
        this.cupoDisponible = cupoDisponible;
        this.fechaPublicacion = fechaPublicacion;
        this.docentes = (docentes != null) ? docentes : new ArrayList<>();
        this.imagenPath = imagenPath;
    }

    // Constructor sobrecargado de 7 parámetros para mantener compatibilidad
    public DTEdicionCurso(String nombre, LocalDate fechaInicio, LocalDate fechaFin, int cupo, 
                           int cupoDisponible, LocalDate fechaPublicacion, List<String> docentes) {
        this(nombre, fechaInicio, fechaFin, cupo, cupoDisponible, fechaPublicacion, docentes, "");
    }
        
    public String getNombre() { return nombre; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public int getCupo() { return cupo; }
    public int getCupoDisponible() { return cupoDisponible; }
    public LocalDate getFechaPublicacion() { return fechaPublicacion; }
    public List<String> getDocentes() { return docentes; }
    public String getImagenPath() { return imagenPath; }
}