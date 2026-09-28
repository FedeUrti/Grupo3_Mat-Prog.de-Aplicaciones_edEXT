package com.grupo3_mat.edEXT.Logica.Clases;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "EdicionCurso")
public class EdicionCurso implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "curso_nombre")
    private Curso curso;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "Docente_Participa_EdicionCurso",
            joinColumns = @JoinColumn(name = "edicion_nombre"),
            inverseJoinColumns = @JoinColumn(name = "docente_nickname")
    )
    private List<Docente> docentes = new ArrayList<>();

    @OneToMany(mappedBy = "edicion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @MapKey(name = "id.estudianteNickname")
    private Map<String, InscripcionEC> inscripciones = new HashMap<>();

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private int cupo;
    private LocalDate fechaPublicacion;
    private String imagenPath;

    public EdicionCurso() {
    }

    public EdicionCurso(String nombre, LocalDate fechaInicio, LocalDate fechaFin, int cupo, LocalDate fechaPublicacion, Curso curso) {
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cupo = cupo;
        this.fechaPublicacion = fechaPublicacion;
        this.curso = curso;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public int getCupo() { return cupo; }
    public void setCupo(int cupo) { this.cupo = cupo; }

    public LocalDate getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(LocalDate fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }

    public Curso getCurso() { return curso; }
    public void setCurso(Curso curso) { this.curso = curso; }

    public List<Docente> getDocentes() { return docentes; }
    public void setDocentes(List<Docente> docentes) { this.docentes = docentes; }

    public Map<String, InscripcionEC> getInscripciones() { return inscripciones; }
    public void setInscripciones(Map<String, InscripcionEC> inscripciones) { this.inscripciones = inscripciones; }

    public String getImagenPath() { return imagenPath; }
    public void setImagenPath(String imagenPath) { this.imagenPath = imagenPath; }

    public void agregarDocente(Docente docente) {
        if (docente != null && !this.docentes.contains(docente)) {
            this.docentes.add(docente);
        }
    }

    public void agregarInscripcion(InscripcionEC inscripcion) {
        if (inscripcion != null && inscripcion.getEstudiante() != null) {
            String nicknameEstudiante = inscripcion.getEstudiante().getNickname();
            this.inscripciones.put(nicknameEstudiante, inscripcion);
            inscripcion.setEdicion(this);
        }
    }

    public boolean estaInscripto(String nicknameEstudiante) {
        return this.inscripciones.containsKey(nicknameEstudiante);
    }

    public InscripcionEC buscarInscripcionPorEstudiante(String nicknameEstudiante) {
        return this.inscripciones.get(nicknameEstudiante);
    }

    public boolean tieneCupoDisponible() {
        if (this.cupo <= 0) {
            return true;
        }
        return this.inscripciones.size() < this.cupo;
    }

    public int getCupoDisponible() {
        if (this.cupo <= 0) {
            return -1;
        }
        return this.cupo - this.inscripciones.size();
    }

    public void cambiarEstadoInscripcion(String nicknameEstudiante, EstadoInscripcion nuevoEstado) {
        // Primero se comprueba que exista la inscripción que se quiere revisar.
        InscripcionEC insc = buscarInscripcionPorEstudiante(nicknameEstudiante);
        if (insc == null) {
            throw new IllegalArgumentException("El estudiante no está inscripto en esta edición.");
        }
        // La tarea solo permite decidir sobre inscripciones que todavía están pendientes.
        if (insc.getEstado() != EstadoInscripcion.INSCRIPTO) {
            throw new IllegalStateException("Solo se pueden procesar inscripciones en estado Inscripto.");
        }
        // El docente puede aceptar o rechazar, pero no volver a dejarla como Inscripto.
        if (nuevoEstado != EstadoInscripcion.ACEPTADA && nuevoEstado != EstadoInscripcion.RECHAZADA) {
            throw new IllegalArgumentException("El estado de destino debe ser Aceptada o Rechazada.");
        }
        insc.setEstado(nuevoEstado);
    }
}