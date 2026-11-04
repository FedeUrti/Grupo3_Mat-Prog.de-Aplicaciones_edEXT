package com.grupo3_mat.edEXT.Logica.DataTypes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class DTProgramaFormacion {
    
    private String nombre;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDate fechaAlta;
    private List<String> cursos;
    private List<String> categorias;
    private String imagenPath;
    // Se guardan nombres simples para no exponer entidades persistentes en la presentación.
    private String nicknameDocente;
    // Lista derivada de los institutos de los cursos incluidos en el programa.
    private List<String> institutos;

    public DTProgramaFormacion() {
        this.cursos = new ArrayList<>();
        this.categorias = new ArrayList<>();
        this.institutos = new ArrayList<>();
    }
    
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