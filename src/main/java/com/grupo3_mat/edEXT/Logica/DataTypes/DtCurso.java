package com.grupo3_mat.edEXT.Logica.DataTypes;

import java.time.LocalDate;
import java.util.List;

public class DtCurso {

    private final String nombre;
    private final String nomInstituto;
    private final String descripcion;
    private final int duracion;
    private final int cantHoras;
    private final int creditos;
    private final String url;
    private final LocalDate fecha;
    private final List<String> previas;
    private final List<String> ediciones;
    private final List<String> programas;

    public DtCurso(String nombre, String nomInstituto, String descripcion, int duracion, int cantHoras, int creditos, String url, LocalDate fecha, List<String> previas, List<String> ediciones, List<String> programas) {
        this.nombre = nombre;
        this.nomInstituto = nomInstituto;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.cantHoras = cantHoras;
        this.creditos = creditos;
        this.url = url;
        this.fecha = fecha;
        this.previas = previas;
        this.ediciones = ediciones;
        this.programas = programas;
    }

    public String getNombre() { return nombre; }
    public String getNomInstituto() { return nomInstituto; }
    public String getDescripcion() { return descripcion; }
    public int getDuracion() { return duracion; }
    public int getCantHoras() { return cantHoras; }
    public int getCreditos() { return creditos; }
    public String getUrl() { return url; }
    public LocalDate getFecha() { return fecha; }
    public List<String> getPrevias() { return previas; }
    public List<String> getEdiciones() { return ediciones; }
    public List<String> getProgramas() { return programas; }
}