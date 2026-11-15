package com.grupo3_mat.edEXT.Logica.Clases;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Entity
@Table(name = "Curso")
public class Curso implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "instituto_nombre")
    private Instituto instituto;

    @Lob
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
    
    private int duracion;
    private int cantHoras;
    private int creditos;
    private String url;
    private LocalDate fecha;
    private String imagenPath;
    @Column(name = "visitas", nullable = false, columnDefinition = "INT NOT NULL DEFAULT 0")
    private int visitas = 0;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "Curso_Categoria",
            joinColumns = @JoinColumn(name = "curso_nombre"),
            inverseJoinColumns = @JoinColumn(name = "categoria_nombre")
    )
    private Set<Categoria> categorias = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "Curso_Precorrelativa",
            joinColumns = @JoinColumn(name = "curso_nombre"),
            inverseJoinColumns = @JoinColumn(name = "precorrelativa_nombre")
    )
    private Set<Curso> previas = new HashSet<>();

    @OneToMany(
            mappedBy = "curso",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private Set<EdicionCurso> ediciones = new HashSet<>();

    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<ValoracionCurso> valoraciones = new HashSet<>();

    @ManyToMany(mappedBy = "cursos", fetch = FetchType.EAGER)
    private Set<ProgramaFormacion> programas = new HashSet<>();

    public Curso() {
    }

    public Curso(Instituto instituto, String nombre, String descripcion, int duracion, int cantHoras, int creditos, String url, LocalDate fecha) {
        this.instituto = instituto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.cantHoras = cantHoras;
        this.creditos = creditos;
        this.url = url;
        this.fecha = fecha;
    }

    public Curso(Instituto instituto, String nombre, String descripcion, int duracion, int cantHoras, int creditos, String url, LocalDate fecha, String imagenPath) {
        this(instituto, nombre, descripcion, duracion, cantHoras, creditos, url, fecha);
        this.imagenPath = imagenPath;
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Instituto getInstituto() { return instituto; }
    public void setInstituto(Instituto instituto) { this.instituto = instituto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getDuracion() { return duracion; }
    public void setDuracion(int duracion) { this.duracion = duracion; }

    public int getCantHoras() { return cantHoras; }
    public void setCantHoras(int cantHoras) { this.cantHoras = cantHoras; }

    public int getCreditos() { return creditos; }
    public void setCreditos(int creditos) { this.creditos = creditos; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getImagenPath() { return imagenPath; }
    public void setImagenPath(String imagenPath) { this.imagenPath = imagenPath; }
    public int getVisitas() { return visitas; }
    public void setVisitas(int visitas) { this.visitas = visitas; }

    // Relaciones
    public Set<Curso> getPrevias() { return previas; }
    public void setPrevias(Set<Curso> previas) { this.previas = previas; }
    public void setPrevias(List<Curso> previas) {
        this.previas = (previas != null) ? new HashSet<>(previas) : new HashSet<>();
    }

    public Set<EdicionCurso> getEdiciones() { return ediciones; }
    public void setEdiciones(Set<EdicionCurso> ediciones) { this.ediciones = ediciones; }

    public Set<ValoracionCurso> getValoraciones() { return valoraciones; }
    public void setValoraciones(Set<ValoracionCurso> valoraciones) {
        this.valoraciones = valoraciones != null ? valoraciones : new HashSet<>();
    }

    public Set<ProgramaFormacion> getProgramas() { return programas; }
    public void setProgramas(Set<ProgramaFormacion> programas) { this.programas = programas; }

    public Set<Categoria> getCategorias() { return categorias; }
    public void setCategorias(Set<Categoria> categorias) { this.categorias = categorias; }
    public void setCategorias(List<Categoria> categorias) {
        this.categorias = (categorias != null) ? new HashSet<>(categorias) : new HashSet<>();
    }

    // Aliases para compatibilidad con Controladores y Data Types
    public int getHorasSemanales() { return cantHoras; }
    public void setHorasSemanales(int horas) { this.cantHoras = horas; }

    public LocalDate getFechaRegistro() { return fecha; }
    public void setFechaRegistro(LocalDate fecha) { this.fecha = fecha; }

    public List<Curso> getPrecorrelativas() { return new ArrayList<>(this.previas); }

    // Lógica de negocio y auxiliares
    public void agregarCategoria(Categoria categoria) {
        if (categoria != null) {
            this.categorias.add(categoria);
            if (categoria.getCursos() != null) {
                categoria.getCursos().add(this);
            }
        }
    }

    public void agregarEdicion(EdicionCurso edicion) {
        if (edicion != null) {
            this.ediciones.add(edicion);
            edicion.setCurso(this);
        }
    }

    public void agregarPrevia(Curso previa) {
        if (previa != null) {
            this.previas.add(previa);
        }
    }

    public void agregarPrecorrelativa(Curso precorrelativa) {
        agregarPrevia(precorrelativa);
    }

    public EdicionCurso obtenerProximaEdicion(LocalDate fechaRef) {
        if (this.ediciones == null || this.ediciones.isEmpty() || fechaRef == null) {
            return null;
        }

        EdicionCurso proximaEdicion = null;
        for (EdicionCurso ed : this.ediciones) {
            LocalDate fechaInicioEd = ed.getFechaInicio();
            if (!ed.isCerrada() && fechaInicioEd != null && !fechaInicioEd.isBefore(fechaRef)) {
                if (proximaEdicion == null || fechaInicioEd.isBefore(proximaEdicion.getFechaInicio())) {
                    proximaEdicion = ed;
                }
            }
        }
        return proximaEdicion;
    }

    public EdicionCurso obtenerEdicionVigente(LocalDate fechaRef) {
        if (ediciones == null || ediciones.isEmpty() || fechaRef == null) {
            return null;
        }
        List<EdicionCurso> candidatas = ediciones.stream()
                .filter(edicion -> edicion != null && !edicion.isCerrada()
                        && edicion.getFechaInicio() != null && edicion.getFechaFin() != null
                        && !fechaRef.isAfter(edicion.getFechaFin()))
                .toList();
        return candidatas.stream()
                .filter(edicion -> !fechaRef.isBefore(edicion.getFechaInicio()))
                .min(java.util.Comparator.comparing(EdicionCurso::getFechaInicio))
                .orElseGet(() -> candidatas.stream()
                        .filter(edicion -> fechaRef.isBefore(edicion.getFechaInicio()))
                        .min(java.util.Comparator.comparing(EdicionCurso::getFechaInicio))
                        .orElse(null));
    }

    public BigDecimal obtenerPuntajePromedio() {
        if (valoraciones == null || valoraciones.isEmpty()) {
            return BigDecimal.ZERO.setScale(2);
        }
        int suma = valoraciones.stream().mapToInt(ValoracionCurso::getPuntaje).sum();
        return BigDecimal.valueOf(suma)
                .divide(BigDecimal.valueOf(valoraciones.size()), 2, RoundingMode.HALF_UP);
    }

    public Map<Integer, Integer> obtenerConteoValoraciones() {
        Map<Integer, Integer> conteos = new TreeMap<>();
        for (int puntaje = 1; puntaje <= 5; puntaje++) {
            conteos.put(puntaje, 0);
        }
        if (valoraciones != null) {
            for (ValoracionCurso valoracion : valoraciones) {
                conteos.computeIfPresent(valoracion.getPuntaje(), (puntaje, cantidad) -> cantidad + 1);
            }
        }
        return conteos;
    }

    public void agregarValoracion(ValoracionCurso valoracion) {
        if (valoracion == null || valoracion.getCurso() == null
                || !nombre.equals(valoracion.getCurso().getNombre())) {
            throw new IllegalArgumentException("La valoración debe corresponder a este curso.");
        }
        valoraciones.add(valoracion);
        if (valoracion.getEstudiante() != null) {
            valoracion.getEstudiante().agregarValoracion(valoracion);
        }
    }
}