package com.grupo3_mat.edEXT.Logica.Clases;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Curso")
public class Curso {

    @Id
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "instituto_nombre")
    private Instituto instituto;

    private String descripcion;
    private int duracion;
    private int cantHoras;
    private int creditos;
    private String url;
    private LocalDate fecha;

    // Cambiado de List a Set para evitar MultipleBagFetchException en Hibernate
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Curso> previas = new HashSet<>();

    @OneToMany(
            mappedBy = "curso",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private Set<EdicionCurso> ediciones = new HashSet<>();

    @ManyToMany(mappedBy = "cursos", fetch = FetchType.EAGER)
    private Set<ProgramaFormacion> programas = new HashSet<>();

    public Curso() {
    }

    public Curso(Instituto instituto, String nombre, String descripcion, int duracion, int cantHoras, int creditos, String url, LocalDate fecha) {
        this.instituto = instituto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.cantHoras = cantHoras;
        this.creditos = creditos;
        this.url = url;
        this.fecha = fecha;
    }

    public String getNombre() {
        return nombre;
    }

    public Instituto getInstituto() {
        return instituto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getDuracion() {
        return duracion;
    }

    public int getCantHoras() {
        return cantHoras;
    }

    public int getCreditos() {
        return creditos;
    }

    public String getUrl() {
        return url;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Set<Curso> getPrevias() {
        return previas;
    }

    public void setPrevias(Set<Curso> previas) {
        this.previas = previas;
    }

    // Sobrecarga para mantener compatibilidad con ControladorCurso
    public void setPrevias(List<Curso> previas) {
        this.previas = (previas != null) ? new HashSet<>(previas) : new HashSet<>();
    }

    public Set<EdicionCurso> getEdiciones() {
        return ediciones;
    }

    public void setEdiciones(Set<EdicionCurso> ediciones) {
        this.ediciones = ediciones;
    }

    public Set<ProgramaFormacion> getProgramas() {
        return programas;
    }

    public void setProgramas(Set<ProgramaFormacion> programas) {
        this.programas = programas;
    }

    public void agregarEdicion(EdicionCurso edicion) {
        this.ediciones.add(edicion);
    }

    public EdicionCurso obtenerProximaEdicion(LocalDate fechaRef) {
        if (this.ediciones == null || this.ediciones.isEmpty() || fechaRef == null) {
            return null;
        }

        EdicionCurso proximaEdicion = null;
        for (EdicionCurso ed : this.ediciones) {
            LocalDate fechaInicioEd = ed.getFechaInicio();

            if (fechaInicioEd != null && !fechaInicioEd.isBefore(fechaRef)) {
                if (proximaEdicion == null || fechaInicioEd.isBefore(proximaEdicion.getFechaInicio())) {
                    proximaEdicion = ed;
                }
            }
        }
        return proximaEdicion;
    }
}
