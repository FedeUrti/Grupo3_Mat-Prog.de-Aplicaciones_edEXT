package com.grupo3_mat.edEXT.Logica.DataTypes;

import java.time.LocalDate;

public class DtUsuario {
    private String nickname;
    private String nombre;
    private String apellido;
    private String correo;
    private LocalDate fechaNacimiento;
    private String imagenPath;

    public DtUsuario(String nickname, String nombre, String apellido, String correo, LocalDate fechaNacimiento, String imagenPath) {
        this.nickname = nickname;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.fechaNacimiento = fechaNacimiento;
        this.imagenPath = imagenPath;
    }

    public String getNickname() { return nickname; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getCorreo() { return correo; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public String getImagenPath() { return imagenPath; }
}