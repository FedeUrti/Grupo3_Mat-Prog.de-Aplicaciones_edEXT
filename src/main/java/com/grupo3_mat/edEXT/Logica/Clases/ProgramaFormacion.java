package com.grupo3_mat.edEXT.Logica.Clases;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToMany;

/**
 *
 * @author ssant
 */
@Entity
@Table(name = "ProgramaFormacion")
public class ProgramaFormacion {
    @Id
    private String nombre;
    private String descripcion;
    private Date fechaInicio;
    private Date fechaFin;
    private Date fechaAlta;
    @ManyToMany
    private Map<String, Curso> cursos;
    
    public ProgramaFormacion(String nombre, String descripcion, Date fechaInicio, Date fechaFin, Date fechaAlta){
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.fechaAlta = fechaAlta;
        this.cursos = new HashMap<>();
    }
    
    public void agregarCurso(Curso c) {
        this.cursos.put(c.nombre, c);
    }

    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public Map<String, Curso> getCursos() {
        return cursos;
    }

    public void setCursos(Map<String, Curso> cursos) {
        this.cursos = cursos;
    }
    
}
