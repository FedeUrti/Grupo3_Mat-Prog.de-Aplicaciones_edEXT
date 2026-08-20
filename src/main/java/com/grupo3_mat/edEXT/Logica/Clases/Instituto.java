/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.grupo3_mat.edEXT.Logica.Clases;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
/**
 *
 * @author fede1
 */
@Entity
@Table(name = "Instituto")
public class Instituto {
    @Id
    String nombre;
    
    
    public Instituto(String nombre)
    {
        this.nombre = nombre;
    }
    
}
