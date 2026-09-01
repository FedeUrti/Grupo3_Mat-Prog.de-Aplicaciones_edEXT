package com.grupo3_mat.edEXT.Logica.Clases;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "Estudiante")
public class Estudiante extends Usuario {

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKey(name = "id.edicionNombre")
    private Map<String, InscripcionEC> inscripciones;

    // Constructor vacío requerido por JPA
    public Estudiante() {
        super();
        this.inscripciones = new HashMap<>();
    }

    // Constructor
    public Estudiante(String nickname, String nombre, String apellido, String correo, LocalDate fechaNacimiento, String imagenPath) {
        super(nickname, nombre, apellido, correo, fechaNacimiento, imagenPath);
        this.inscripciones = new HashMap<>();
    }

    // Getters y Setters
    public Map<String, InscripcionEC> getInscripciones() {
        return inscripciones;
    }

    public void setInscripciones(Map<String, InscripcionEC> inscripciones) {
        this.inscripciones = inscripciones;
    }
}