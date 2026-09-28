package com.grupo3_mat.edEXT.Logica.Interfaces;

import com.grupo3_mat.edEXT.Logica.DataTypes.DTEdicionCurso;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTInscripcionEdicion;
import com.grupo3_mat.edEXT.Logica.DataTypes.CriterioOrdenInscripciones;
import com.grupo3_mat.edEXT.Logica.Clases.EstadoInscripcion;
import java.time.LocalDate;
import java.util.List;

public interface IControladorEdicion {
    void altaEdicionCurso(String nombreCurso, DTEdicionCurso datosEdicion) throws Exception;
    List<String> listarEdicionesPorCurso(String nombreCurso);
    DTEdicionCurso mostrarDetalleEdicion(String nombreEdicion);
    void inscribirEstudianteAEdicion(String nicknameEstudiante, String nombreEdicion, LocalDate fechaInscripcion) throws Exception;
    String obtenerCursoDeEdicion(String nombreEdicion);
    List<String> listarEdicionesDeDocente(String nicknameDocente);
    List<String> listarEdicionesDeEstudiante(String nicknameEstudiante);
    
    // Métodos faltantes para selección de estudiantes
    List<DTInscripcionEdicion> listarInscripcionesAEdicion(String nicknameDocente, String nombreEdicion, CriterioOrdenInscripciones criterio) throws Exception;
    List<DTInscripcionEdicion> listarAceptadosAEdicion(String nicknameDocente, String nombreEdicion) throws Exception;
    List<DTInscripcionEdicion> listarResultadosInscripcionesEstudiante(String nicknameEstudiante);
    void cambiarEstadoInscripcion(String nicknameDocente, String nombreEdicion, String nicknameEstudiante, EstadoInscripcion nuevoEstado) throws Exception;
}