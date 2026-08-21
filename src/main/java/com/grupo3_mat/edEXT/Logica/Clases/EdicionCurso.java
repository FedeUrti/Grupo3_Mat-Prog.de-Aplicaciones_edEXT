/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.grupo3_mat.edEXT.Logica.Clases;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
/**
 *
 * @author benja
 */
@Entity
@Table(name = "EdicionCurso")
public class EdicionCurso {
    
    @Id
    private String nombre;
    
    private LocalDate fechaInicio;
    
    private LocalDate fechaFin;
    
    private int cupo;
    
    private LocalDate fechaPublicacion;

    // Constructor por defecto (requerido por JPA/Hibernate según Gemini jaja lol)
    public EdicionCurso() {
    }

    // Constructor completo
    public EdicionCurso(String nombre, LocalDate fechaInicio, LocalDate fechaFin, int cupo, LocalDate fechaPublicacion) {
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cupo = cupo;
        this.fechaPublicacion = fechaPublicacion;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public int getCupo() {
        return cupo;
    }

    public void setCupo(int cupo) {
        this.cupo = cupo;
    }

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }
}