package com.grupo3_mat.edEXT.Logica.DataTypes;

import com.grupo3_mat.edEXT.Logica.Clases.EstadoInscripcion;
import java.time.LocalDate;

public class DTInscripcionEdicion {
    private final String nicknameEstudiante;
    private final String nombreEdicion;
    private final LocalDate fechaInscripcion;
    private final EstadoInscripcion estado;
    // Es decimal porque cada rechazo previo suma medio punto de prioridad.
    private final Double prioridad;

    public DTInscripcionEdicion(String nicknameEstudiante, String nombreEdicion, LocalDate fechaInscripcion, EstadoInscripcion estado, Double prioridad) {
        this.nicknameEstudiante = nicknameEstudiante;
        this.nombreEdicion = nombreEdicion;
        this.fechaInscripcion = fechaInscripcion;
        this.estado = estado;
        this.prioridad = prioridad;
    }

    public String getNicknameEstudiante() { return nicknameEstudiante; }
    public String getNombreEdicion() { return nombreEdicion; }
    public LocalDate getFechaInscripcion() { return fechaInscripcion; }
    public EstadoInscripcion getEstado() { return estado; }
    public Double getPrioridad() { return prioridad; }
}