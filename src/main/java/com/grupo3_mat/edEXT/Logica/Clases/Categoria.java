package com.grupo3_mat.edEXT.Logica.Clases;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Categoria")
public class Categoria implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String nombre;

    @ManyToMany(mappedBy = "categorias")
    private Set<Curso> cursos = new HashSet<>();

    public Categoria() {
    }

    public Categoria(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Set<Curso> getCursos() {
        return cursos;
    }

    public void setCursos(Set<Curso> cursos) {
        this.cursos = cursos;
    }

    public void agregarCurso(Curso curso) {
        this.cursos.add(curso);
    }
}