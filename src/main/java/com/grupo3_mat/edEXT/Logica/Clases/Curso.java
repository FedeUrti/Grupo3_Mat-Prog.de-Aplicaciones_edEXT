/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.grupo3_mat.edEXT.Logica.Clases;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Date;
import jakarta.persistence.Table;

/**
 *
 * @author fede1
 */
@Entity
@Table(name = "Curso")
public class Curso {
    @Id
    String nombre;
    String nomInst;
    String descripcion;
    int duracion;
    int cantHoras;
    int creditos;
    String url;
    Date fecha;
    
    public Curso(String nomInst, String nombre, String descripcion, int duracion, int cantHoras, int creditos, String url, Date fecha)
    {
        this.nombre = nombre;
        this.nomInst = nomInst; 
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.cantHoras = cantHoras;
        this.creditos = creditos;
        this.url = url;
        this.fecha = fecha;
    }
}
