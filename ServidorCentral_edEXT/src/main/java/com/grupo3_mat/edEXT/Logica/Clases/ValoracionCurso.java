package com.grupo3_mat.edEXT.Logica.Clases;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "ValoracionCurso")
public class ValoracionCurso {

    @EmbeddedId
    private ValoracionCursoId id = new ValoracionCursoId();

    @ManyToOne
    @MapsId("cursoNombre")
    @JoinColumn(name = "curso_nombre")
    private Curso curso;

    @ManyToOne
    @MapsId("estudianteNickname")
    @JoinColumn(name = "estudiante_nickname")
    private Estudiante estudiante;

    private int puntaje;

    public ValoracionCurso() {
    }

    public ValoracionCurso(Estudiante estudiante, Curso curso, int puntaje) {
        if (estudiante == null || curso == null) {
            throw new IllegalArgumentException("La valoración debe tener un estudiante y un curso.");
        }
        this.estudiante = estudiante;
        this.curso = curso;
        this.id = new ValoracionCursoId(curso.getNombre(), estudiante.getNickname());
        setPuntaje(puntaje);
    }

    public ValoracionCursoId getId() {
        return id;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
        if (curso != null) {
            id.setCursoNombre(curso.getNombre());
        }
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
        if (estudiante != null) {
            id.setEstudianteNickname(estudiante.getNickname());
        }
    }

    public int getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(int puntaje) {
        if (puntaje < 1 || puntaje > 5) {
            throw new IllegalArgumentException("El puntaje debe estar entre 1 y 5.");
        }
        this.puntaje = puntaje;
    }
}
