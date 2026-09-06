package com.grupo3_mat.edEXT.Logica.DataTypes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DtCurso {

    private final String nombre;
    private final String descripcion;
    private final int duracion;
    private final int cantHoras;
    private final int creditos;
    private final String url;
    private final LocalDate fecha;
    private final String nomInstituto;
    private final List<String> previas;
    private final List<String> ediciones;
    private final List<String> programas;

    // Constructor completo (con previas, ediciones y programas)
    public DtCurso(String nombre, String descripcion, int duracion, int cantHoras, int creditos, String url, LocalDate fecha, String nomInstituto, List<String> previas, List<String> ediciones, List<String> programas) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.cantHoras = cantHoras;
        this.creditos = creditos;
        this.url = url;
        this.fecha = fecha;
        this.nomInstituto = nomInstituto;
        this.previas = (previas != null) ? previas : new ArrayList<>();
        this.ediciones = (ediciones != null) ? ediciones : new ArrayList<>();
        this.programas = (programas != null) ? programas : new ArrayList<>();
    }

    // Sobrecarga por si no se especifican previas
    public DtCurso(String nombre, String descripcion, int duracion, int cantHoras, int creditos, String url, LocalDate fecha, String nomInstituto, List<String> ediciones, List<String> programas) {
        this(nombre, descripcion, duracion, cantHoras, creditos, url, fecha, nomInstituto, new ArrayList<>(), ediciones, programas);
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public int getDuracion() { return duracion; }
    public int getCantHoras() { return cantHoras; }
    public int getCreditos() { return creditos; }
    public String getUrl() { return url; }
    public LocalDate getFecha() { return fecha; }
    public String getNomInstituto() { return nomInstituto; }
    public List<String> getPrevias() { return previas; }
    public List<String> getEdiciones() { return ediciones; }
    public List<String> getProgramas() { return programas; }
}