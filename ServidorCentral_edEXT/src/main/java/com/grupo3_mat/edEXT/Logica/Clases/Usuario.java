package com.grupo3_mat.edEXT.Logica.Clases;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private String password;

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

    public Usuario(String nickname, String nombre, String apellido, String correo, LocalDate fechaNacimiento, String imagenPath) {
        this.nickname = nickname;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.fechaNacimiento = fechaNacimiento;
        this.imagenPath = imagenPath;
    }

    public Usuario(String nickname, String nombre, String apellido, String correo, LocalDate fechaNacimiento, String imagenPath, String password) {
        this(nickname, nombre, apellido, correo, fechaNacimiento, imagenPath);
        this.password = password;
    }

    // Getters y Setters Básicos
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getImagenPath() { return imagenPath; }
    public void setImagenPath(String imagenPath) { this.imagenPath = imagenPath; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Set<Usuario> getSeguidos() { return seguidos; }
    public void setSeguidos(Set<Usuario> seguidos) { this.seguidos = seguidos; }

    public Set<Usuario> getSeguidores() { return seguidores; }
    public void setSeguidores(Set<Usuario> seguidores) { this.seguidores = seguidores; }

    // Métodos de Seguimiento requeridos por ControladorUsuario
    public boolean sigueA(String nickname) {
        if (nickname == null) return false;
        for (Usuario u : seguidos) {
            if (u.getNickname().equals(nickname)) {
                return true;
            }
        }
        return false;
    }

    public void agregarSeguido(Usuario seguido) {
        if (seguido != null) {
            this.seguidos.add(seguido);
        }
    }

    public void removerSeguido(Usuario seguido) {
        if (seguido != null) {
            this.seguidos.remove(seguido);
        }
    }

    public void agregarSeguidor(Usuario seguidor) {
        if (seguidor != null) {
            this.seguidores.add(seguidor);
        }
    }

    public void removerSeguidor(Usuario seguidor) {
        if (seguidor != null) {
            this.seguidores.remove(seguidor);
        }
    }

    public List<String> getNicknamesSeguidores() {
        List<String> list = new ArrayList<>();
        for (Usuario u : seguidores) {
            list.add(u.getNickname());
        }
        return list;
    }

    public List<String> getNicknamesSeguidos() {
        List<String> list = new ArrayList<>();
        for (Usuario u : seguidos) {
            list.add(u.getNickname());
        }
        return list;
    }

    public void seguirUsuario(Usuario usuarioASeguir) {
        if (usuarioASeguir != null && !usuarioASeguir.getNickname().equals(this.nickname)) {
            this.agregarSeguido(usuarioASeguir);
            usuarioASeguir.agregarSeguidor(this);
        }
    }

    public void dejarDeSeguirUsuario(Usuario usuarioADejar) {
        if (usuarioADejar != null) {
            this.removerSeguido(usuarioADejar);
            usuarioADejar.removerSeguidor(this);
        }
    }
}