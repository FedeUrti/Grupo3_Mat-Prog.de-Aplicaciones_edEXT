package com.grupo3_mat.edEXT.Logica.Clases;

import jakarta.persistence.*;
import java.net.URI;
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

    @Column(length = 2048)
    private String enlaceVideo;

    private Integer evaluacion;
    private LocalDate fechaEvaluacion;

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

    public InscripcionEC(Estudiante estudiante, EdicionCurso edicion, LocalDate fechaInscripcion,
            Double prioridad, String enlaceVideo) {
        this(estudiante, edicion, fechaInscripcion, prioridad);
        setEnlaceVideo(enlaceVideo);
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

    public String getEnlaceVideo() {
        return enlaceVideo;
    }

    public void setEnlaceVideo(String enlaceVideo) {
        if (enlaceVideo == null || enlaceVideo.isBlank()) {
            this.enlaceVideo = null;
            return;
        }
        String enlaceNormalizado = enlaceVideo.trim();
        if (enlaceNormalizado.length() > 2048) {
            throw new IllegalArgumentException("El enlace del video no puede superar los 2048 caracteres.");
        }
        try {
            URI uri = URI.create(enlaceNormalizado);
            String esquema = uri.getScheme();
            if (uri.getHost() == null || esquema == null
                    || !(esquema.equalsIgnoreCase("http") || esquema.equalsIgnoreCase("https"))) {
                throw new IllegalArgumentException("Ingresá un enlace de video válido con protocolo HTTP o HTTPS.");
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Ingresá un enlace de video válido con protocolo HTTP o HTTPS.", e);
        }
        this.enlaceVideo = enlaceNormalizado;
    }

    public Integer getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(Integer evaluacion) {
        if (evaluacion != null && (evaluacion < 1 || evaluacion > 12)) {
            throw new IllegalArgumentException("La evaluación debe estar entre 1 y 12.");
        }
        fechaEvaluacion = evaluacion == null ? null : LocalDate.now();
        this.evaluacion = evaluacion;
    }

    public LocalDate getFechaEvaluacion() {
        return fechaEvaluacion;
    }
}