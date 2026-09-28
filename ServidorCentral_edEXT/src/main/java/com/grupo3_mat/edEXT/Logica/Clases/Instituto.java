package com.grupo3_mat.edEXT.Logica.Clases;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Instituto")
public class Instituto {

    @Id
    private String nombre;

    // Docente mantiene la relación; esta lista permite recorrerla desde el instituto.
    @ManyToMany(mappedBy = "institutos")
    private List<Docente> docentes = new ArrayList<>();

    public Instituto() {}

    public Instituto(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public List<Docente> getDocentes() { return docentes; }
    public void setDocentes(List<Docente> docentes) { this.docentes = docentes; }
}