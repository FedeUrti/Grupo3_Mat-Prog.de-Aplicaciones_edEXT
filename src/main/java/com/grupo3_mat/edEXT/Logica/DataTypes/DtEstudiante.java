package com.grupo3_mat.edEXT.Logica.DataTypes;

import java.time.LocalDate;
import java.util.List;

public class DtEstudiante extends DtUsuario {
    private List<String> edicionesInscripto;
    private List<String> programasInscripto;

    public DtEstudiante(String nickname, String nombre, String apellido, String correo, LocalDate fechaNacimiento, String imagenPath, List<String> edicionesInscripto, List<String> programasInscripto) {
        super(nickname, nombre, apellido, correo, fechaNacimiento, imagenPath);
        this.edicionesInscripto = edicionesInscripto;
        this.programasInscripto = programasInscripto;
    }

    public List<String> getEdicionesInscripto() { return edicionesInscripto; }
    public List<String> getProgramasInscripto() { return programasInscripto; }
}