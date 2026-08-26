package com.grupo3_mat.edEXT.Logica.Controladores;

import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorEdicion;
import com.grupo3_mat.edEXT.Logica.Clases.EdicionCurso;
import com.grupo3_mat.edEXT.Logica.Clases.Curso;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorEdicion;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorCurso;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorUsuario;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTEdicionCurso;
import java.time.LocalDate;

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
                Docente doc = mu.buscarDocente(nick);
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
    public void mostrarDetalleEdicion() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mostrarEdicionVigente() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void inscribirEdicionCurso() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}