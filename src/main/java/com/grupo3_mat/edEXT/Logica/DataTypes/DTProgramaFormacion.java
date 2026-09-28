package com.grupo3_mat.edEXT.Logica.DataTypes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DTProgramaFormacion {
    
    private final String nombre;
    private final String descripcion;
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    private final LocalDate fechaAlta;
    private final List<String> cursos;
    private final List<String> categorias;
    private final String imagenPath;
    // Se guardan nombres simples para no exponer entidades persistentes en la presentación.
    private final String nicknameDocente;
    // Lista derivada de los institutos de los cursos incluidos en el programa.
    private final List<String> institutos;
    
    public DTProgramaFormacion(String nombre, String descripcion, LocalDate fechaInicio, LocalDate fechaFin, 
                               LocalDate fechaAlta, List<String> cursos, List<String> categorias, String imagenPath) {
        this(nombre, descripcion, fechaInicio, fechaFin, fechaAlta, cursos, categorias, imagenPath, null, null);
    }

    public DTProgramaFormacion(String nombre, String descripcion, LocalDate fechaInicio, LocalDate fechaFin,
                               LocalDate fechaAlta, List<String> cursos, List<String> categorias,
                               String imagenPath, String nicknameDocente, List<String> institutos) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.fechaAlta = fechaAlta;
        this.cursos = (cursos != null) ? cursos : new ArrayList<>();
        this.categorias = (categorias != null) ? categorias : new ArrayList<>();
        this.imagenPath = imagenPath;
        this.nicknameDocente = nicknameDocente;
        this.institutos = (institutos != null) ? institutos : new ArrayList<>();
    }
    
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public LocalDate getFechaAlta() { return fechaAlta; }
    public List<String> getCursos() { return cursos; }
    public List<String> getCategorias() { return categorias; }
    public String getImagenPath() { return imagenPath; }
    public String getNicknameDocente() { return nicknameDocente; }
    public List<String> getInstitutos() { return institutos; }
    
    @Override
    public String toString() { return nombre; }
}