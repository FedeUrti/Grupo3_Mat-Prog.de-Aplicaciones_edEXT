package com.grupo3_mat.edEXT.Logica.Clases;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ValoracionCursoId implements Serializable {

    private static final long serialVersionUID = 1L;

    private String cursoNombre;
    private String estudianteNickname;

    public ValoracionCursoId() {
    }

    public ValoracionCursoId(String cursoNombre, String estudianteNickname) {
        this.cursoNombre = cursoNombre;
        this.estudianteNickname = estudianteNickname;
    }

    public String getCursoNombre() {
        return cursoNombre;
    }

    public void setCursoNombre(String cursoNombre) {
        this.cursoNombre = cursoNombre;
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
        ValoracionCursoId that = (ValoracionCursoId) o;
        return Objects.equals(cursoNombre, that.cursoNombre)
                && Objects.equals(estudianteNickname, that.estudianteNickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cursoNombre, estudianteNickname);
    }
}
