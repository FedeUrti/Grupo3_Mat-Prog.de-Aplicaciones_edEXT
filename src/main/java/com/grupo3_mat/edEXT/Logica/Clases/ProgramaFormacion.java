package com.grupo3_mat.edEXT.Logica.Clases;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;
import jakarta.persistence.*;

@Entity
@Table(name = "ProgramaFormacion")
public class ProgramaFormacion implements Serializable {
    @Id
    private String nombre;
    private String descripcion;
    
    @Temporal(TemporalType.DATE)
    private Date fechaInicio;
    
    @Temporal(TemporalType.DATE)
    private Date fechaFin;
    
    @Temporal(TemporalType.DATE)
    private Date fechaAlta;

// Quitamos PERSIST para evitar conflictos de claves duplicadas en la BD
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "programa_curso",
            joinColumns = @JoinColumn(name = "programa_nombre"),
            inverseJoinColumns = @JoinColumn(name = "curso_nombre")
    )
    @MapKey(name = "nombre")
    private Map<String, Curso> cursos = new HashMap<>();

    public ProgramaFormacion(){}

    public ProgramaFormacion(String nombre, String descripcion, Date fechaInicio, Date fechaFin, Date fechaAlta){
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.fechaAlta = fechaAlta;
        this.cursos = new HashMap<>();
    }
    
    public void agregarCurso(Curso c) {
        this.cursos.put(c.getNombre(), c);
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