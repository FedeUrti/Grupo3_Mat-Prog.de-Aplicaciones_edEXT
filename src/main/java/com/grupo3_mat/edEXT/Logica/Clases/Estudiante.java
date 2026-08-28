package com.grupo3_mat.edEXT.Logica.Clases;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Estudiante")
public class Estudiante extends Usuario {

    // Colecciones para las inscripciones (se dejan comentadas hasta que tengas las clases)
    @OneToMany(mappedBy = "estudiante")
    private List<InscripcionEC> inscripcionesEdicion;
    
    // @OneToMany(mappedBy = "estudiante")
    //private List<InscripcionPrograma> inscripcionesPrograma;

    // Constructor vacío requerido por JPA
    public Estudiante() {
        super();
    }

    // Constructor que concuerda con tu ControladorUsuario
    public Estudiante(String nickname, String nombre, String apellido, String correo, LocalDate fechaNacimiento, String imagenPath) {
        super(nickname, nombre, apellido, correo, fechaNacimiento, imagenPath);
        this.inscripcionesEdicion = new ArrayList<>();
        //this.inscripcionesPrograma = new ArrayList<>();
    }

    // Getters y setters para las listas cuando las incorpores
    
    public List<InscripcionEC> getInscripcionesEdicion() {
        return inscripcionesEdicion;
    }

    public void setInscripcionesEdicion(List<InscripcionEC> inscripcionesEdicion) {
        this.inscripcionesEdicion = inscripcionesEdicion;
    }

    /*public List<InscripcionPrograma> getInscripcionesPrograma() {
        return inscripcionesPrograma;
    }

    public void setInscripcionesPrograma(List<InscripcionPrograma> inscripcionesPrograma) {
        this.inscripcionesPrograma = inscripcionesPrograma;
    }*/
    
}