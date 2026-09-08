/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.grupo3_mat.edEXT.Logica.Clases;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author benja
 */
@Entity
@Table(name = "EdicionCurso")
public class EdicionCurso {
    
    @Id
    private String nombre;
    
    @ManyToOne  //Relación con Curso
    @JoinColumn(name = "curso_nombre")
    private Curso curso;
    
    @ManyToMany(fetch = FetchType.EAGER) // <-- Debe ir aquí
    @JoinTable(
            name = "Docente_Participa_EdicionCurso",
            joinColumns = @JoinColumn(name = "edicion_nombre"),
            inverseJoinColumns = @JoinColumn(name = "docente_nickname")
    )
    private List<Docente> docentes = new ArrayList<>();
    
    @OneToMany(mappedBy = "edicion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @MapKey(name = "id.estudianteNickname")
    private Map<String, InscripcionEC> inscripciones = new HashMap<>(); // <-- Agregar = new HashMap<>()
    
    private LocalDate fechaInicio;
    
    private LocalDate fechaFin;
    
    private int cupo;
    
    private LocalDate fechaPublicacion;

    // Constructor por defecto (requerido por JPA según Gemini jaja lol)
    public EdicionCurso() {
    }

    // Constructor completo
    public EdicionCurso(String nombre, LocalDate fechaInicio, LocalDate fechaFin, int cupo, LocalDate fechaPublicacion, Curso curso) {
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cupo = cupo;
        this.fechaPublicacion = fechaPublicacion;
        this.curso = curso;
    }

    // Getters
    public String getNombre() {return nombre;}
    public LocalDate getFechaInicio() {return fechaInicio;}
    public LocalDate getFechaFin() {return fechaFin;}
    public int getCupo() {return cupo;}
    public LocalDate getFechaPublicacion() {return fechaPublicacion;}
    public Curso getCurso() {return curso;}
    public List<Docente> getDocentes() {return docentes;}
    public Map<String, InscripcionEC> getInscripciones() {return inscripciones;}
    
    //Setters
    public void setNombre(String nombre) {this.nombre = nombre;}
    public void setFechaInicio(LocalDate fechaInicio) {this.fechaInicio = fechaInicio;}
    public void setFechaFin(LocalDate fechaFin) {this.fechaFin = fechaFin;}
    public void setCupo(int cupo) {this.cupo = cupo;}
    public void setFechaPublicacion(LocalDate fechaPublicacion) {this.fechaPublicacion = fechaPublicacion;}
    public void setCurso(Curso curso) {this.curso = curso;}
    public void setDocentes(List<Docente> docentes) {this.docentes = docentes;}
    
    //Auxiliares
    public void agregarDocente(Docente docente) {this.docentes.add(docente);}
    
    public void agregarInscripcion(InscripcionEC inscripcion) {
        String nicknameEstudiante = inscripcion.getEstudiante().getNickname();
        this.inscripciones.put(nicknameEstudiante, inscripcion);
        inscripcion.setEdicion(this);
    }

    public boolean estaInscripto(String nicknameEstudiante) {
        return this.inscripciones.containsKey(nicknameEstudiante);
    }
    
    public boolean tieneCupoDisponible() {
        if (this.cupo <= 0) {
            return true; // No hay límite
        }
        return this.inscripciones.size() < this.cupo; // Comparar inscriptos con la capacidad del cupo
    }
}
