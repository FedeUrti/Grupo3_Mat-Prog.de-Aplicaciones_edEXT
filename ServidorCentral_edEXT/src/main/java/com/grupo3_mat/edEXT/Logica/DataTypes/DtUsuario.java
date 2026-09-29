package com.grupo3_mat.edEXT.Logica.DataTypes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DtUsuario {
    private String nickname;
    private String nombre;
    private String apellido;
    private String correo;
    private LocalDate fechaNacimiento;
    private String imagenPath;
    // Se muestran los vínculos sociales, pero nunca se incluye la contraseña en el DTO.
    private final List<String> seguidores;
    private final List<String> seguidos;

    public DtUsuario(String nickname, String nombre, String apellido, String correo, LocalDate fechaNacimiento, String imagenPath) {
        this(nickname, nombre, apellido, correo, fechaNacimiento, imagenPath, null, null);
    }

    public DtUsuario(String nickname, String nombre, String apellido, String correo, LocalDate fechaNacimiento, String imagenPath, List<String> seguidores, List<String> seguidos) {
        this.nickname = nickname;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.fechaNacimiento = fechaNacimiento;
        this.imagenPath = imagenPath;
        this.seguidores = (seguidores != null) ? seguidores : new ArrayList<>();
        this.seguidos = (seguidos != null) ? seguidos : new ArrayList<>();
    }

    public String getNickname() { return nickname; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getCorreo() { return correo; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public String getImagenPath() { return imagenPath; }
    public List<String> getSeguidores() { return seguidores; }
    public List<String> getSeguidos() { return seguidos; }
}