package com.grupo3_mat.edEXT.Logica.Clases;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "InscripcionPF")
public class InscripcionPF {

    @EmbeddedId
    private InscripcionPFId id = new InscripcionPFId();

    @ManyToOne
    @MapsId("programaNombre")
    @JoinColumn(name = "programa_nombre")
    private ProgramaFormacion programa;

    @ManyToOne
    @MapsId("estudianteNickname")
    @JoinColumn(name = "estudiante_nickname")
    private Estudiante estudiante;

    private LocalDate fechaInscripcion;

    public InscripcionPF() {
    }

    public InscripcionPF(Estudiante estudiante, ProgramaFormacion programa, LocalDate fechaInscripcion) {
        this.estudiante = estudiante;
        this.programa = programa;
        this.fechaInscripcion = fechaInscripcion;
        this.id = new InscripcionPFId(programa.getNombre(), estudiante.getNickname());
    }

    public InscripcionPFId getId() {
        return id;
    }

    public ProgramaFormacion getPrograma() {
        return programa;
    }

    public void setPrograma(ProgramaFormacion programa) {
        this.programa = programa;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public LocalDate getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(LocalDate fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }
}