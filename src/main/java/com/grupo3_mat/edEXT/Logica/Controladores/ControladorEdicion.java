package com.grupo3_mat.edEXT.Logica.Controladores;

import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorEdicion;
import com.grupo3_mat.edEXT.Logica.Clases.EdicionCurso;
import com.grupo3_mat.edEXT.Logica.Clases.Curso;
import com.grupo3_mat.edEXT.Logica.Clases.Usuario;
import com.grupo3_mat.edEXT.Logica.Clases.Docente;
import com.grupo3_mat.edEXT.Logica.Clases.Estudiante;
import com.grupo3_mat.edEXT.Logica.Clases.InscripcionEC;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorEdicion;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorCurso;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorUsuario;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTEdicionCurso;
import java.time.LocalDate;
import java.util.*;

public class ControladorEdicion implements IControladorEdicion {

    @Override
    public void altaEdicionCurso(String nombreCurso, DTEdicionCurso datosEdicion) throws Exception {
        ManejadorEdicion me = ManejadorEdicion.getInstancia();
        ManejadorCurso mc = ManejadorCurso.getInstancia();
        ManejadorUsuario mu = ManejadorUsuario.getInstancia();

        //Validaciones previas
        if (me.buscarEdicion(datosEdicion.getNombre()) != null) {
            throw new Exception("La edición '" + datosEdicion.getNombre() + "' ya existe.");
        }

        Curso curso = mc.buscarCurso(nombreCurso);
        if (curso == null) {
            throw new Exception("El curso '" + nombreCurso + "' no existe.");
        }

        //Instanciar
        EdicionCurso nuevaEdicion = new EdicionCurso(
            datosEdicion.getNombre(),
            datosEdicion.getFechaInicio(),
            datosEdicion.getFechaFin(),
            datosEdicion.getCupo(),
            datosEdicion.getFechaPublicacion(),
            curso
        );

        //Asociar Docentes
        if (datosEdicion.getDocentes() != null) {
            for (String nick : datosEdicion.getDocentes()) {
                Docente doc = (Docente)mu.buscarUsuarioPorNickname(nick);
                if (doc == null) {
                    throw new Exception("El docente con nickname '" + nick + "' no existe.");
                }
                nuevaEdicion.agregarDocente(doc);
            }
        }

        //Vincular Curso
        curso.agregarEdicion(nuevaEdicion);

        me.agregarEdicion(nuevaEdicion); //Persistir
    }
    
    @Override
    public List<String> listarEdicionesPorCurso(String nombreCurso) {
        ManejadorCurso mc = ManejadorCurso.getInstancia();
        Curso curso = mc.buscarCurso(nombreCurso);

        if (curso == null || curso.getEdiciones() == null) {
            return new ArrayList<>();
        }

        List<String> nombresEdiciones = new ArrayList<>();

        for (EdicionCurso ed : curso.getEdiciones()) {
            nombresEdiciones.add(ed.getNombre());
        }

        return nombresEdiciones;
    }
    
    @Override
    public DTEdicionCurso mostrarDetalleEdicion(String nombreEdicion) {
        ManejadorEdicion me = ManejadorEdicion.getInstancia();
        
        EdicionCurso edicion = me.buscarEdicion(nombreEdicion);
        if (edicion == null) {
            return null;
        }

        // Convertir la lista/set de objetos Docente a una lista de nicknames
        List<String> nombresDocentes = new ArrayList<>();
        if (edicion.getDocentes() != null) {
            for (Docente doc : edicion.getDocentes()) {
                nombresDocentes.add(doc.getNickname());
            }
        }

        // Construcción del Data Transfer Object
        return new DTEdicionCurso(
            edicion.getNombre(),
            edicion.getFechaInicio(),
            edicion.getFechaFin(),
            edicion.getCupo(),
            edicion.getFechaPublicacion(),
            nombresDocentes
        );
    }
    
    @Override
    public void inscribirEstudianteAEdicion(String nicknameEstudiante, String nombreEdicion, LocalDate fechaInscripcion) throws Exception {
        // Obtener y validar el estudiante
        ManejadorUsuario mu = ManejadorUsuario.getInstancia();
        Usuario usr = mu.buscarUsuarioPorNickname(nicknameEstudiante);

        if (usr == null || !(usr instanceof Estudiante)) {
            throw new Exception("El usuario " + nicknameEstudiante + " no existe o no es un estudiante.");
        }
        Estudiante estudiante = (Estudiante) usr;

        // Obtener la edición del curso
        ManejadorEdicion me = ManejadorEdicion.getInstancia();
        EdicionCurso edicion = me.buscarEdicion(nombreEdicion);

        if (edicion == null) {
            throw new Exception("La edición " + nombreEdicion + " no existe.");
        }

        // Validar si el estudiante ya está inscrito a la edición
        if (edicion.estaInscripto(estudiante.getNickname())) {
            throw new Exception("El estudiante ya se encuentra inscripto a esta edición.");
        }

        // Crear la clase de asociación InscripcionEC
        InscripcionEC inscripcion = new InscripcionEC(estudiante, edicion, fechaInscripcion);

        // Vincular la inscripción con ambos lados de la relación
        estudiante.agregarInscripcion(inscripcion);
        edicion.agregarInscripcion(inscripcion);

        // PERSISTIR CAMBIOS EN LA BASE DE DATOS
        mu.modificarUsuario(estudiante);
    }
}
