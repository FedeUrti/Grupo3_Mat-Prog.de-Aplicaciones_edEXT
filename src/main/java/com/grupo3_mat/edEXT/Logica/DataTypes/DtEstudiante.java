package com.grupo3_mat.edEXT.Logica.DataTypes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DtEstudiante extends DtUsuario {
    // La inscripción detallada incluye estado y prioridad; la lista de nombres conserva compatibilidad.
    private final List<DTInscripcionEdicion> inscripcionesEdicion;
    private final List<String> edicionesInscripto;
    private final List<String> programasInscripto;

    public DtEstudiante(String nickname, String nombre, String apellido, String correo, LocalDate fechaNacimiento, String imagenPath, List<String> edicionesInscripto, List<String> programasInscripto) {
        this(nickname, nombre, apellido, correo, fechaNacimiento, imagenPath, null, null, new ArrayList<>(), edicionesInscripto, programasInscripto);
    }

    public DtEstudiante(String nickname, String nombre, String apellido, String correo, LocalDate fechaNacimiento, String imagenPath, List<String> seguidores, List<String> seguidos, List<DTInscripcionEdicion> inscripcionesEdicion, List<String> programasInscripto) {
        this(nickname, nombre, apellido, correo, fechaNacimiento, imagenPath, seguidores, seguidos, inscripcionesEdicion, null, programasInscripto);
    }

    private DtEstudiante(String nickname, String nombre, String apellido, String correo, LocalDate fechaNacimiento, String imagenPath, List<String> seguidores, List<String> seguidos, List<DTInscripcionEdicion> inscripcionesEdicion, List<String> edicionesInscripto, List<String> programasInscripto) {
        super(nickname, nombre, apellido, correo, fechaNacimiento, imagenPath, seguidores, seguidos);
        this.inscripcionesEdicion = (inscripcionesEdicion != null) ? inscripcionesEdicion : new ArrayList<>();
        this.edicionesInscripto = (edicionesInscripto != null) ? edicionesInscripto : nombresEdiciones(this.inscripcionesEdicion);
        this.programasInscripto = (programasInscripto != null) ? programasInscripto : new ArrayList<>();
    }

    public List<DTInscripcionEdicion> getInscripcionesEdicion() { return inscripcionesEdicion; }
    public List<String> getEdicionesInscripto() { return edicionesInscripto; }
    public List<String> getProgramasInscripto() { return programasInscripto; }

    private static List<String> nombresEdiciones(List<DTInscripcionEdicion> inscripciones) {
        // Se deriva la lista simple desde las inscripciones para no mantener dos datos distintos.
        List<String> nombres = new ArrayList<>();
        for (DTInscripcionEdicion inscripcion : inscripciones) {
            if (inscripcion != null) {
                nombres.add(inscripcion.getNombreEdicion());
            }
        }
        return nombres;
    }
}