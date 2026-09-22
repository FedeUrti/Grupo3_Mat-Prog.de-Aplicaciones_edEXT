package com.grupo3_mat.edEXT.Logica.Clases;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Usuario")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Usuario {

    @Id
    private String nickname;

    private String nombre;
    private String apellido;
    private String correo;
    private LocalDate fechaNacimiento;
    private String imagenPath;
    
    // Campo agregado para Tarea 2: Autenticación
    private String password;

    // Relación reflexiva para la funcionalidad de Seguir / Dejar de seguir
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "Usuario_Seguimiento",
        joinColumns = @JoinColumn(name = "seguidor_nickname"),
        inverseJoinColumns = @JoinColumn(name = "seguido_nickname")
    )
    private Set<Usuario> seguidos = new HashSet<>();

    @ManyToMany(mappedBy = "seguidos", fetch = FetchType.EAGER)
    private Set<Usuario> seguidores = new HashSet<>();

    public Usuario() {
    }

    // Constructor sin password (compatibilidad Tarea 1)
    public Usuario(String nickname, String nombre, String apellido, String correo, LocalDate fechaNacimiento, String imagenPath) {
        this.nickname = nickname;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.fechaNacimiento = fechaNacimiento;
        this.imagenPath = imagenPath;
    }

    // Constructor completo para Tarea 2
    public Usuario(String nickname, String nombre, String apellido, String correo, LocalDate fechaNacimiento, String imagenPath, String password) {
        this(nickname, nombre, apellido, correo, fechaNacimiento, imagenPath);
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getImagenPath() {
        return imagenPath;
    }

    public void setImagenPath(String imagenPath) {
        this.imagenPath = imagenPath;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Usuario> getSeguidos() {
        return seguidos;
    }

    public void setSeguidos(Set<Usuario> seguidos) {
        this.seguidos = seguidos;
    }

    public Set<Usuario> getSeguidores() {
        return seguidores;
    }

    public void setSeguidores(Set<Usuario> seguidores) {
        this.seguidores = seguidores;
    }

    // Métodos auxiliares de negocio
    public void seguirUsuario(Usuario usuarioASeguir) {
        if (usuarioASeguir != null && !usuarioASeguir.getNickname().equals(this.nickname)) {
            this.seguidos.add(usuarioASeguir);
            usuarioASeguir.getSeguidores().add(this);
        }
    }

    public void dejarDeSeguirUsuario(Usuario usuarioADejar) {
        if (usuarioADejar != null) {
            this.seguidos.remove(usuarioADejar);
            usuarioADejar.getSeguidores().remove(this);
        }
    }
}