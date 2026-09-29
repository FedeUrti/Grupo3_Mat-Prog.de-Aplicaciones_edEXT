package com.grupo3_mat.edEXT.Logica.Clases;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "ProgramaFormacion")
public class ProgramaFormacion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String nombre;
    private String descripcion;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDate fechaAlta;
    private String imagenPath;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "programa_curso",
            joinColumns = @JoinColumn(name = "programa_nombre"),
            inverseJoinColumns = @JoinColumn(name = "curso_nombre")
    )
    @MapKey(name = "nombre")
    private Map<String, Curso> cursos = new HashMap<>();

    @OneToMany(mappedBy = "programa", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<InscripcionPF> inscripciones = new ArrayList<>();
    // Todo programa nuevo debe tener un docente responsable.
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "docente_nickname", nullable = false)
    private Docente docente;

    public ProgramaFormacion() {}

    public ProgramaFormacion(String nombre, String descripcion, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaAlta, String imagePath) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.fechaAlta = fechaAlta;
        this.cursos = new HashMap<>();
        this.imagenPath = imagePath;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public LocalDate getFechaAlta() { return fechaAlta; }
    public void setFechaAlta(LocalDate fechaAlta) { this.fechaAlta = fechaAlta; }

    public String getImagenPath() { return imagenPath; }
    public void setImagenPath(String imagenPath) { this.imagenPath = imagenPath; }

    public Map<String, Curso> getCursos() { return cursos; }
    public void setCursos(Map<String, Curso> cursos) { this.cursos = cursos; }

    public List<InscripcionPF> getInscripciones() { return inscripciones; }
    public void setInscripciones(List<InscripcionPF> inscripciones) { this.inscripciones = inscripciones; }

    public void agregarCurso(Curso c) {
        if (c != null) {
            this.cursos.put(c.getNombre(), c);
        }
    }
    public Docente getDocente(){
        return docente;
    }

    public void setDocente(Docente docente) {
        if (this.docente == docente) {
            return;
        }
        // Mantiene sincronizada la lista inversa si se cambia el responsable.
        if (this.docente != null) {
            this.docente.removerPrograma(this);
        }
        this.docente = docente;
        if (docente != null) {
            docente.agregarPrograma(this);
        }
    }
    public void agregarInscripcion(InscripcionPF insc) {
        if (insc != null) {
            this.inscripciones.add(insc);
            insc.setPrograma(this);
        }
    }

    public Set<Categoria> getCategorias() {
        // Las categorías de un programa son la unión de las categorías de sus cursos.
        Set<Categoria> resultado = new HashSet<>();
        if (this.cursos != null) {
            for (Curso c : this.cursos.values()) {
                if (c.getCategorias() != null) {
                    resultado.addAll(c.getCategorias());
                }
            }
        }
        return resultado;
    }

    public Set<Instituto> getInstitutos() {
        // El programa toma sus institutos de los cursos que lo integran, sin guardar datos duplicados.
        Set<Instituto> resultado = new LinkedHashSet<>();
        if (cursos != null) {
            for (Curso curso : cursos.values()) {
                if (curso != null && curso.getInstituto() != null) {
                    resultado.add(curso.getInstituto());
                }
            }
        }
        return resultado;
    }
}