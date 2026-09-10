/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.grupo3_mat.edEXT.Logica.Interfaces;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTEdicionCurso;
import java.time.LocalDate;
import java.util.List;
/**
 *
 * @author benja
 */
public interface IControladorEdicion {
    void altaEdicionCurso(String nombreCurso, DTEdicionCurso datosEdicion) throws Exception;

    List<String> listarEdicionesPorCurso(String nombreCurso);

    DTEdicionCurso mostrarDetalleEdicion(String nombreEdicion);

    void inscribirEstudianteAEdicion(String nicknameEstudiante, String nombreEdicion, LocalDate fechaInscripcion) throws Exception;

    public String obtenerCursoDeEdicion(String nombreEdicion);

    public List<String> listarEdicionesDeDocente(String nicknameDocente);

    // Método a agregar:
    public List<String> listarEdicionesDeEstudiante(String nicknameEstudiante);
    
}
