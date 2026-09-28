package com.grupo3_mat.edEXT.Logica.Clases;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "InscripcionEC")
public class InscripcionEC {

    @EmbeddedId
    private InscripcionECId id = new InscripcionECId();

    @ManyToOne
    @MapsId("edicionNombre")
    @JoinColumn(name = "edicion_nombre")
    private EdicionCurso edicion;

    @ManyToOne
    @MapsId("estudianteNickname")
    @JoinColumn(name = "estudiante_nickname")
    private Estudiante estudiante;

    @Enumerated(EnumType.STRING)
    private EstadoInscripcion estado = EstadoInscripcion.INSCRIPTO;

    private LocalDate fechaInscripcion;
    // Puede tener medios puntos: cada rechazo previo suma 0.5 a la prioridad.
    private Double prioridad;

    public InscripcionEC() {
    }

    public InscripcionEC(Estudiante estudiante, EdicionCurso edicion, LocalDate fechaInscripcion) {
        this.estudiante = estudiante;
        this.edicion = edicion;
        this.fechaInscripcion = fechaInscripcion;
        this.id = new InscripcionECId(edicion.getNombre(), estudiante.getNickname());
    }

    public InscripcionEC(Estudiante estudiante, EdicionCurso edicion, LocalDate fechaInscripcion, Double prioridad) {
        this(estudiante, edicion, fechaInscripcion);
        this.prioridad = prioridad;
    }

    public InscripcionECId getId() {
        return id;
    }

    public LocalDate getFechaInscripcion() {
        return fechaInscripcion;
    }

    public LocalDate getFecha() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(LocalDate fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public EdicionCurso getEdicion() {
        return edicion;
    }

    public void setEdicion(EdicionCurso edicion) {
        this.edicion = edicion;
    }

    public EstadoInscripcion getEstado() {
        return estado;
    }

    public void setEstado(EstadoInscripcion estado) {
        this.estado = estado;
    }

    public Double getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Double prioridad) {
        this.prioridad = prioridad;
    }
}