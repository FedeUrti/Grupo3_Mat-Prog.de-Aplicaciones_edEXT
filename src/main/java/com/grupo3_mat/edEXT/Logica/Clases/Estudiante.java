package com.grupo3_mat.edEXT.Logica.Clases;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "Estudiante")
public class Estudiante extends Usuario {

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @MapKey(name = "id.edicionNombre")
    private Map<String, InscripcionEC> inscripciones = new HashMap<>();

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @MapKey(name = "id.programaNombre")
    private Map<String, InscripcionPF> inscripcionesPF = new HashMap<>();

    public Estudiante() {
        super();
    }

    public Estudiante(String nickname, String nombre, String apellido, String correo, LocalDate fechaNacimiento, String imagenPath, String password) {
        super(nickname, nombre, apellido, correo, fechaNacimiento, imagenPath, password);
    }

    public Map<String, InscripcionEC> getInscripciones() { return inscripciones; }
    public Map<String, InscripcionPF> getInscripcionesPF() { return inscripcionesPF; }

    public List<InscripcionPF> getInscripcionesProgramas() {
        return new ArrayList<>(this.inscripcionesPF.values());
    }

    public boolean estaInscriptoAPrograma(String nombrePrograma) {
        return this.inscripcionesPF.containsKey(nombrePrograma);
    }

    public boolean estaInscriptoAEdicion(String nombreEdicion) {
        return this.inscripciones.containsKey(nombreEdicion);
    }

    public void agregarInscripcionPrograma(InscripcionPF inscripcion) {
        if (inscripcion != null && inscripcion.getPrograma() != null) {
            this.inscripcionesPF.put(inscripcion.getPrograma().getNombre(), inscripcion);
        }
    }

    public void agregarInscripcionPF(InscripcionPF inscripcion) {
        agregarInscripcionPrograma(inscripcion);
    }

    public void agregarInscripcion(InscripcionEC inscripcion) {
        if (inscripcion != null && inscripcion.getEdicion() != null) {
            this.inscripciones.put(inscripcion.getEdicion().getNombre(), inscripcion);
        }
    }

    // Cuenta rechazos previos del mismo curso solo en ediciones ya finalizadas.
    public int calcularIpr(String nombreCurso) {
        int ipr = 0; // Acumula la cantidad de inscripciones rechazadas que cumplen el criterio.
        LocalDate hoy = LocalDate.now(); // Se usa para decidir si una edición ya terminó.

        // Revisa todas las inscripciones del estudiante.
        for (InscripcionEC ins : this.inscripciones.values()) {
            // Una inscripción nula o sin edición no aporta al cálculo.
            EdicionCurso ed = ins != null ? ins.getEdicion() : null;

            // Solo cuentan inscripciones a una edición de un curso cuyo nombre coincide.
            if (ed != null && ed.getCurso() != null && nombreCurso != null
                    && nombreCurso.equals(ed.getCurso().getNombre())) {
                // Una edición cuenta como finalizada si tiene fecha de fin anterior a hoy.
                if (ed.getFechaFin() != null && ed.getFechaFin().isBefore(hoy)) {
                    // Solo los rechazos de esas ediciones finalizadas aumentan el IPR.
                    if (ins.getEstado() == EstadoInscripcion.RECHAZADA) {
                        ipr++;
                    }
                }
            }
        }
        return ipr; // Devuelve el total usado para calcular la prioridad de una nueva inscripción.
    }

    public double calcularPrioridadInscripcion(String nombreCurso) {
        // Cada rechazo previo contado por calcularIpr agrega medio punto.
        return calcularIpr(nombreCurso) * 0.5;
    }
}