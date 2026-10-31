package com.grupo3_mat.edEXT.Logica.DataTypes;

import java.time.LocalDate;

public class DTResultadoEstudio {

    private final String nombreCurso;
    private final String nombreEdicion;
    private final Integer evaluacion;
    private final LocalDate fechaResultado;

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

    public boolean isAprobado() {
        return evaluacion != null && evaluacion >= 6;
    }

    public boolean isTieneResultado() {
        return evaluacion != null;
    }
}
