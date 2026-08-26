package com.grupo3_mat.edEXT.Logica.DataTypes;

import java.time.LocalDate;
import java.util.List;

public class DtDocente extends DtUsuario {
    private String instituto;
    private List<String> cursosRegistrados;

    public DtDocente(String nickname, String nombre, String apellido, String correo, LocalDate fechaNacimiento, String imagenPath, String instituto, List<String> cursosRegistrados) {
        super(nickname, nombre, apellido, correo, fechaNacimiento, imagenPath);
        this.instituto = instituto;
        this.cursosRegistrados = cursosRegistrados;
    }

    public String getInstituto() { return instituto; }
    public List<String> getCursosRegistrados() { return cursosRegistrados; }
}