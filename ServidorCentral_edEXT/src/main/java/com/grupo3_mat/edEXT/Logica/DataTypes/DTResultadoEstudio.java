package com.grupo3_mat.edEXT.Logica.DataTypes;

import java.time.LocalDate;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class DTResultadoEstudio {

    private String nombreCurso;
    private String nombreEdicion;
    private Integer evaluacion;
    private LocalDate fechaResultado;

    public DTResultadoEstudio() {
    }

    public DTResultadoEstudio(String nombreCurso, String nombreEdicion, Integer evaluacion,
            LocalDate fechaResultado) {
        this.nombreCurso = nombreCurso;
        this.nombreEdicion = nombreEdicion;
        this.evaluacion = evaluacion;
        this.fechaResultado = fechaResultado;
    }

    public String getNombreCurso() {
        return nombreCurso;
    }

    public String getNombreEdicion() {
        return nombreEdicion;
    }

    public Integer getEvaluacion() {
        return evaluacion;
    }

    public LocalDate getFechaResultado() {
        return fechaResultado;
    }

    @XmlElement
    public boolean isAprobado() {
        return evaluacion != null && evaluacion >= 6;
    }

    @XmlElement
    public boolean isTieneResultado() {
        return evaluacion != null;
    }
}
