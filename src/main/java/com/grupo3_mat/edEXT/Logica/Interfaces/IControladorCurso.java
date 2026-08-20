/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.grupo3_mat.edEXT.Logica.Interfaces;

import java.util.Date;

/**
 *
 * @author fede1
 */
public interface IControladorCurso {
    void altaCurso(String nomInst, String cursoNom, String desc, int dur, int cantHoras, int creditos, String url, Date fecha);
    void consultarCurso();
    void listarPrevias();
}
