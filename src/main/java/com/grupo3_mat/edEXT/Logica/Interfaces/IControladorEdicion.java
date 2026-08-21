/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.grupo3_mat.edEXT.Logica.Interfaces;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTEdicionCurso;

/**
 *
 * @author benja
 */
public interface IControladorEdicion {
    void altaEdicionCurso(String nombreCurso, DTEdicionCurso datosEdicion);
    void mostrarDetalleEdicion();
    void mostrarEdicionVigente();
    void inscribirEdicionCurso();
}
