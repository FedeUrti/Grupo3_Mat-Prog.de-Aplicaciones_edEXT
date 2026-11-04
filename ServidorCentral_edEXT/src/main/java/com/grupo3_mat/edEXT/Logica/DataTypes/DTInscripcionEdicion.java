package com.grupo3_mat.edEXT.Logica.DataTypes;

import com.grupo3_mat.edEXT.Logica.Clases.EstadoInscripcion;
import java.net.URI;
import java.time.LocalDate;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class DTInscripcionEdicion {
    private String nicknameEstudiante;
    private String nombreEdicion;
    private LocalDate fechaInscripcion;
    private EstadoInscripcion estado;
    // Es decimal porque cada rechazo previo suma medio punto de prioridad.
    private Double prioridad;
    private String enlaceVideo;
    private Integer evaluacion;
    private boolean edicionCerrada;

    public DTInscripcionEdicion() {
    }

    public DTInscripcionEdicion(String nicknameEstudiante, String nombreEdicion, LocalDate fechaInscripcion, EstadoInscripcion estado, Double prioridad) {
        this(nicknameEstudiante, nombreEdicion, fechaInscripcion, estado, prioridad, null);
    }

    public DTInscripcionEdicion(String nicknameEstudiante, String nombreEdicion, LocalDate fechaInscripcion,
            EstadoInscripcion estado, Double prioridad, String enlaceVideo) {
        this(nicknameEstudiante, nombreEdicion, fechaInscripcion, estado, prioridad, enlaceVideo, null, false);
    }

    public DTInscripcionEdicion(String nicknameEstudiante, String nombreEdicion, LocalDate fechaInscripcion,
            EstadoInscripcion estado, Double prioridad, String enlaceVideo, Integer evaluacion, boolean edicionCerrada) {
        this.nicknameEstudiante = nicknameEstudiante;
        this.nombreEdicion = nombreEdicion;
        this.fechaInscripcion = fechaInscripcion;
        this.estado = estado;
        this.prioridad = prioridad;
        this.enlaceVideo = enlaceVideo;
        this.evaluacion = evaluacion;
        this.edicionCerrada = edicionCerrada;
    }

    public String getNicknameEstudiante() { return nicknameEstudiante; }
    public String getNombreEdicion() { return nombreEdicion; }
    public LocalDate getFechaInscripcion() { return fechaInscripcion; }
    public EstadoInscripcion getEstado() { return estado; }
    public Double getPrioridad() { return prioridad; }
    public String getEnlaceVideo() { return enlaceVideo; }
    public Integer getEvaluacion() { return evaluacion; }
    public boolean isEdicionCerrada() { return edicionCerrada; }

    @XmlElement
    public String getEnlaceVideoEmbebido() {
        if (enlaceVideo == null) {
            return null;
        }
        try {
            URI uri = URI.create(enlaceVideo);
            String host = uri.getHost();
            String path = uri.getPath();
            String id = null;
            if (host == null || path == null) {
                return null;
            }
            if (host.equalsIgnoreCase("youtu.be")) {
                id = path.substring(1);
            } else if (host.equalsIgnoreCase("youtube.com") || host.equalsIgnoreCase("www.youtube.com")
                    || host.equalsIgnoreCase("m.youtube.com")) {
                if (path.equals("/watch")) {
                    id = uri.getQuery() == null ? null : java.util.Arrays.stream(uri.getQuery().split("&"))
                            .filter(parametro -> parametro.startsWith("v="))
                            .map(parametro -> parametro.substring(2))
                            .findFirst().orElse(null);
                } else if (path.startsWith("/embed/") || path.startsWith("/shorts/")) {
                    id = path.substring(path.lastIndexOf('/') + 1);
                }
            } else if (host.equalsIgnoreCase("vimeo.com") || host.equalsIgnoreCase("www.vimeo.com")
                    || host.equalsIgnoreCase("player.vimeo.com")) {
                id = path.substring(path.lastIndexOf('/') + 1);
                if (!id.matches("[0-9]+")) {
                    return null;
                }
                return "https://player.vimeo.com/video/" + id;
            }
            if (id != null && id.matches("[A-Za-z0-9_-]{11}")) {
                return "https://www.youtube-nocookie.com/embed/" + id;
            }
        } catch (IllegalArgumentException e) {
            return null;
        }
        return null;
    }
}