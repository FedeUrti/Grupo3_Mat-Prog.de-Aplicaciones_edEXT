package com.grupo3_mat.edEXT.Logica.Clases;

import jakarta.persistence.*;
import java.time.LocalDate; // Importación agregada
/**
 *
 * @author benja
 */
@Entity
@Table(name = "InscripcionEC")
public class InscripcionEC {

    /*@EmbeddedId
    private InscripcionID id = new InscripcionID();
*/
    @ManyToOne
    @MapsId("estudianteNickname")
    @JoinColumn(name = "estudiante_nickname")
    private Estudiante estudiante;

    @ManyToOne
    @MapsId("edicionNombre")
    @JoinColumn(name = "edicion_nombre")
    private EdicionCurso edicion;

    private LocalDate fechaInscripcion;

    public InscripcionEC() {}

    public InscripcionEC(Estudiante estudiante, EdicionCurso edicion) {
        this.estudiante = estudiante;
        this.edicion = edicion;
        this.fechaInscripcion = LocalDate.now();
        //this.id = new InscripcionID(estudiante.getNickname(), edicion.getNombre());
    }

    // Getters
    public LocalDate getFechaInscripcion() { 
        return fechaInscripcion; 
    }
    
    public Estudiante getEstudiante() { 
        return estudiante; 
    }
    
    public EdicionCurso getEdicion() { 
        return edicion; 
    }

    // Setters
    public void setFechaInscripcion(LocalDate fechaInscripcion) { 
        this.fechaInscripcion = fechaInscripcion; 
    }
    
    public void setEstudiante(Estudiante estudiante) { 
        this.estudiante = estudiante; 
    }
    
    public void setEdicion(EdicionCurso edicion) { 
        this.edicion = edicion; 
    }
}