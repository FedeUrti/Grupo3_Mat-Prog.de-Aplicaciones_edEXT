package com.grupo3_mat.edEXT.Logica.DataTypes;

import java.math.BigDecimal;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class DtResultadoBusqueda {
    private String nombre;
    private String apellido;
    private String nickname;
    private String tipo;
    private String descripcion;
    private String imagenPath;
    private String instituto;
    private String nombreCurso;
    private String nicknameDocente;
    private String fechaPublicacion;
    private String fechaInicio;
    private String fechaFin;
    private BigDecimal promedioValoracion = BigDecimal.ZERO.setScale(2);
    private long cantidadValoraciones;
    private String rol;
    private boolean siguiendo;
    private boolean propio;
    private int relevancia;

    public DtResultadoBusqueda() {
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getImagenPath() { return imagenPath; }
    public void setImagenPath(String imagenPath) { this.imagenPath = imagenPath; }
    public String getInstituto() { return instituto; }
    public void setInstituto(String instituto) { this.instituto = instituto; }
    public String getNombreCurso() { return nombreCurso; }
    public void setNombreCurso(String nombreCurso) { this.nombreCurso = nombreCurso; }
    public String getNicknameDocente() { return nicknameDocente; }
    public void setNicknameDocente(String nicknameDocente) { this.nicknameDocente = nicknameDocente; }
    public String getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(String fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }
    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }
    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }
    public BigDecimal getPromedioValoracion() { return promedioValoracion; }
    public void setPromedioValoracion(BigDecimal promedioValoracion) {
        this.promedioValoracion = promedioValoracion == null
                ? BigDecimal.ZERO.setScale(2) : promedioValoracion;
    }
    public long getCantidadValoraciones() { return cantidadValoraciones; }
    public void setCantidadValoraciones(long cantidadValoraciones) {
        this.cantidadValoraciones = cantidadValoraciones;
    }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public boolean isSiguiendo() { return siguiendo; }
    public void setSiguiendo(boolean siguiendo) { this.siguiendo = siguiendo; }
    public boolean isPropio() { return propio; }
    public void setPropio(boolean propio) { this.propio = propio; }
    public int getRelevancia() { return relevancia; }
    public void setRelevancia(int relevancia) { this.relevancia = relevancia; }
}
