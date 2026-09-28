package com.grupo3_mat.edEXT.Logica.DataTypes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DtDocente extends DtUsuario {
    // Un docente puede pertenecer a varios institutos y programas.
    private final List<String> institutos;
    // Se exponen los nombres de las ediciones para navegar desde el perfil.
    private final List<String> edicionesAsociadas;
    private final List<String> programas;

    public DtDocente(String nickname, String nombre, String apellido, String correo, LocalDate fechaNacimiento, String imagenPath, String instituto, List<String> edicionesAsociadas) {
        // Constructor de compatibilidad para los lugares que todavía envían un solo instituto.
        this(nickname, nombre, apellido, correo, fechaNacimiento, imagenPath, null, null,
                instituto != null ? List.of(instituto) : List.of(), edicionesAsociadas, null);
    }

    public DtDocente(String nickname, String nombre, String apellido, String correo, LocalDate fechaNacimiento, String imagenPath, List<String> seguidores, List<String> seguidos, List<String> institutos, List<String> edicionesAsociadas, List<String> programas) {
        super(nickname, nombre, apellido, correo, fechaNacimiento, imagenPath, seguidores, seguidos);
        this.institutos = (institutos != null) ? institutos : new ArrayList<>();
        this.edicionesAsociadas = (edicionesAsociadas != null) ? edicionesAsociadas : new ArrayList<>();
        this.programas = (programas != null) ? programas : new ArrayList<>();
    }

    public String getInstituto() { return institutos.isEmpty() ? null : institutos.get(0); }
    public List<String> getInstitutos() { return institutos; }
    public List<String> getEdicionesAsociadas() { return edicionesAsociadas; }
    public List<String> getEdiciones() { return edicionesAsociadas; }
    public List<String> getProgramas() { return programas; }
}