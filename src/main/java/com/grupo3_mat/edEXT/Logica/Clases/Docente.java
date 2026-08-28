package com.grupo3_mat.edEXT.Logica.Clases;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Docente")
public class Docente extends Usuario {

    // Relación con el Instituto al que pertenece el docente
    @ManyToOne
    private Instituto instituto;

    // Relación con los cursos registrados (se deja comentada hasta que tengas la clase Curso)
    @OneToMany(mappedBy = "docente") 
    private List<Curso> cursosRegistrados;

    // Constructor vacío requerido por JPA
    public Docente() {
        super();
    }

    // Constructor que concuerda con tu ControladorUsuario
    public Docente(String nickname, String nombre, String apellido, String correo, LocalDate fechaNacimiento, String imagenPath, Instituto instituto) {
        super(nickname, nombre, apellido, correo, fechaNacimiento, imagenPath);
        this.instituto = instituto;
        this.cursosRegistrados = new ArrayList<>(); // Inicializar cuando agregues la lista
    }

    public Instituto getInstituto() {
        return instituto;
    }

    public void setInstituto(Instituto instituto) {
        this.instituto = instituto;
    }

    
    public List<Curso> getCursosRegistrados() {
        return cursosRegistrados;
    }

    public void setCursosRegistrados(List<Curso> cursosRegistrados) {
        this.cursosRegistrados = cursosRegistrados;
    }
   
}