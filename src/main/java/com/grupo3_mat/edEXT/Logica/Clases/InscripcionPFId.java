package com.grupo3_mat.edEXT.Logica.Clases;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class InscripcionPFId implements Serializable {

    private static final long serialVersionUID = 1L;

    private String programaNombre;
    private String estudianteNickname;

    public InscripcionPFId() {
    }

    public InscripcionPFId(String programaNombre, String estudianteNickname) {
        this.programaNombre = programaNombre;
        this.estudianteNickname = estudianteNickname;
    }

    public String getProgramaNombre() {
        return programaNombre;
    }

    public void setProgramaNombre(String programaNombre) {
        this.programaNombre = programaNombre;
    }

    public String getEstudianteNickname() {
        return estudianteNickname;
    }

    public void setEstudianteNickname(String estudianteNickname) {
        this.estudianteNickname = estudianteNickname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InscripcionPFId that = (InscripcionPFId) o;
        return Objects.equals(programaNombre, that.programaNombre) &&
               Objects.equals(estudianteNickname, that.estudianteNickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(programaNombre, estudianteNickname);
    }
}