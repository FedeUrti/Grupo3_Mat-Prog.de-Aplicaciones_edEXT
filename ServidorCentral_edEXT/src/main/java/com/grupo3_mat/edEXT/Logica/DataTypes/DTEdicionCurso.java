package com.grupo3_mat.edEXT.Logica.DataTypes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class DTEdicionCurso {

    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private int cupo;
    private int cupoDisponible;
    private LocalDate fechaPublicacion;
    private List<String> docentes;
    private String imagenPath;
    private boolean cerrada;

    public DTEdicionCurso() {
        this.docentes = new ArrayList<>();
    }

    // Constructor completo de 8 parámetros
    public DTEdicionCurso(String nombre, LocalDate fechaInicio, LocalDate fechaFin, int cupo, 
                           int cupoDisponible, LocalDate fechaPublicacion, List<String> docentes, String imagenPath) {
        this(nombre, fechaInicio, fechaFin, cupo, cupoDisponible, fechaPublicacion, docentes, imagenPath, false);
    }

    public DTEdicionCurso(String nombre, LocalDate fechaInicio, LocalDate fechaFin, int cupo,
            int cupoDisponible, LocalDate fechaPublicacion, List<String> docentes, String imagenPath,
            boolean cerrada) {
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cupo = cupo;
        this.cupoDisponible = cupoDisponible;
        this.fechaPublicacion = fechaPublicacion;
        this.docentes = (docentes != null) ? docentes : new ArrayList<>();
        this.imagenPath = imagenPath;
        this.cerrada = cerrada;
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
    public boolean isCerrada() { return cerrada; }
}