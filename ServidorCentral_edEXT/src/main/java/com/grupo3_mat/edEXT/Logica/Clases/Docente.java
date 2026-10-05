package com.grupo3_mat.edEXT.Logica.Clases;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Docente")
public class Docente extends Usuario {

    // Un docente puede trabajar con varios institutos, y cada instituto puede tener varios docentes.
    @ManyToMany(fetch = FetchType.EAGER)
        @JoinTable(
            name = "Docente_Instituto",
            joinColumns = @JoinColumn(name = "docente_nickname"),
            inverseJoinColumns = @JoinColumn(name = "instituto_nombre")
        )
        private List<Instituto> institutos = new ArrayList<>();

    @ManyToMany(mappedBy = "docentes", fetch = FetchType.EAGER)
    private List<EdicionCurso> ediciones = new ArrayList<>();

        // Esta lista es el lado inverso: el responsable de cada programa se guarda en ProgramaFormacion.
        @OneToMany(mappedBy = "docente", fetch = FetchType.EAGER)
        private List<ProgramaFormacion> programas = new ArrayList<>();

    public Docente() {
        super();
    }

    public Docente(String nickname, String nombre, String apellido, String correo, LocalDate fechaNacimiento, String imagenPath, String password, Instituto instituto) {
        // Conserva el constructor antiguo y convierte su único instituto en una lista.
        this(nickname, nombre, apellido, correo, fechaNacimiento, imagenPath, password,
                instituto != null ? List.of(instituto) : List.of());
    }

    public Docente(String nickname, String nombre, String apellido, String correo, LocalDate fechaNacimiento, String imagenPath, String password, List<Instituto> institutos) {
        super(nickname, nombre, apellido, correo, fechaNacimiento, imagenPath, password);
        setInstitutos(institutos);
    }

    public Instituto getInstituto() {
        // Compatibilidad con código viejo: devuelve el primer instituto si todavía lo solicita.
        return institutos.isEmpty() ? null : institutos.get(0);
    }

    public void setInstituto(Instituto instituto) {
        setInstitutos(instituto != null ? List.of(instituto) : List.of());
    }

    public List<Instituto> getInstitutos() {
        return institutos;
    }

    public void setInstitutos(List<Instituto> institutos) {
        // Se reemplaza la lista completa para que altas y modificaciones guarden la selección actual.
        this.institutos.clear();
        if (institutos == null) {
            return;
        }
        for (Instituto instituto : institutos) {
            agregarInstituto(instituto);
        }
    }

    public void agregarInstituto(Instituto instituto) {
        if (instituto != null && !institutos.contains(instituto)) {
            institutos.add(instituto);
        }
    }

    public boolean perteneceAInstituto(String nombreInstituto) {
        // Se usa al filtrar docentes o comprobar si pueden participar en cursos de un instituto.
        return nombreInstituto != null && institutos.stream()
                .anyMatch(instituto -> nombreInstituto.equals(instituto.getNombre()));
    }

    public List<EdicionCurso> getEdiciones() {
        return ediciones;
    }

    public void setEdiciones(List<EdicionCurso> ediciones) {
        this.ediciones = ediciones;
    }
    public List<ProgramaFormacion> getProgramas()
    {
        return programas;
    }

    public void agregarPrograma(ProgramaFormacion programa) {
        // Evita duplicar el programa en la lista inversa del docente.
        if (programa != null && !programas.contains(programa)) {
            programas.add(programa);
        }
    }

    public void removerPrograma(ProgramaFormacion programa) {
        programas.remove(programa);
    }
}