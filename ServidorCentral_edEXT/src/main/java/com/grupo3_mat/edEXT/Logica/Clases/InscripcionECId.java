package com.grupo3_mat.edEXT.Logica.Clases;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class InscripcionECId implements Serializable {

    private static final long serialVersionUID = 1L;

    private String edicionNombre;
    private String estudianteNickname;

    // Constructor vacío (Requerido por JPA)
    public InscripcionECId() {
    }

    // Constructor con parámetros
    public InscripcionECId(String edicionNombre, String estudianteNickname) {
        this.edicionNombre = edicionNombre;
        this.estudianteNickname = estudianteNickname;
    }

    // Getters y Setters
    public String getEdicionNombre() {
        return edicionNombre;
    }

    public void setEdicionNombre(String edicionNombre) {
        this.edicionNombre = edicionNombre;
    }

    public String getEstudianteNickname() {
        return estudianteNickname;
    }

    public void setEstudianteNickname(String estudianteNickname) {
        this.estudianteNickname = estudianteNickname;
    }

    // Implementación obligatoria de equals y hashCode para claves compuestas
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InscripcionECId that = (InscripcionECId) o;
        return Objects.equals(edicionNombre, that.edicionNombre) &&
               Objects.equals(estudianteNickname, that.estudianteNickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(edicionNombre, estudianteNickname);
    }
}