package com.grupo3_mat.edEXT.Logica.Clases;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToMany(fetch = FetchType.EAGER) // Agregá o cambiá el FetchType
    private List<Curso> previas;
    
    @OneToMany(
            mappedBy = "curso",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<EdicionCurso> ediciones = new ArrayList<>();
    
    public Curso() {}

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

    public String getNombre() { return nombre; }
    public Instituto getInstituto() { return instituto; }
    public String getDescripcion() { return descripcion; }
    public int getDuracion() { return duracion; }
    public int getCantHoras() { return cantHoras; }
    public int getCreditos() { return creditos; }
    public String getUrl() { return url; }
    public LocalDate getFecha() { return fecha; }
    public List<Curso> getPrevias() { return previas; }
    public void setPrevias(List<Curso> previas) { this.previas = previas; }
    
    public void agregarEdicion(EdicionCurso edicion) {
        this.ediciones.add(edicion);
    }
}