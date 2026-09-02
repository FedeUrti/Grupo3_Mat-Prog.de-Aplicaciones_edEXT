package com.grupo3_mat.edEXT.Logica.Controladores;

import com.grupo3_mat.edEXT.Logica.Clases.*;
import com.grupo3_mat.edEXT.Logica.DataTypes.*;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorUsuario;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorInstituto;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorUsuario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ControladorUsuario implements IControladorUsuario {

    public ControladorUsuario() {
    }

    @Override
    public void altaUsuario(String nickname, String nombre, String apellido, String correo, LocalDate fechaNac, String imagenPath, String nomInstituto) throws Exception {
        ManejadorUsuario mu = ManejadorUsuario.getInstancia();

        // Validaciones de unicidad de nickname y correo
        if (mu.buscarUsuarioPorNickname(nickname) != null) {
            throw new Exception("El nickname ya está en uso.");
        }
        if (mu.buscarUsuarioPorCorreo(correo) != null) {
            throw new Exception("El correo electrónico ya está registrado.");
        }

        Usuario nuevoUsuario;
        if (nomInstituto != null && !nomInstituto.trim().isEmpty()) {
            // Es un Docente: se busca la entidad Instituto
            ManejadorInstituto mi = ManejadorInstituto.getInstancia();
            Instituto inst = mi.buscarInstituto(nomInstituto);
            
            if (inst == null) {
                throw new Exception("El instituto especificado no existe.");
            }
            
            nuevoUsuario = new Docente(nickname, nombre, apellido, correo, fechaNac, imagenPath, inst);
        } else {
            // Es un Estudiante
            nuevoUsuario = new Estudiante(nickname, nombre, apellido, correo, fechaNac, imagenPath);
        }

        mu.agregarUsuario(nuevoUsuario);
    }

    @Override
    public List<String> listarNicknamesUsuarios() {
        ManejadorUsuario mu = ManejadorUsuario.getInstancia();
        List<Usuario> usuarios = mu.getUsuarios();
        List<String> nicknames = new ArrayList<>();
        
        for (Usuario u : usuarios) {
            nicknames.add(u.getNickname());
        }
        return nicknames;
    }

    @Override
    public DtUsuario obtenerInfoUsuario(String nickname) {
        ManejadorUsuario mu = ManejadorUsuario.getInstancia();
        Usuario u = mu.buscarUsuarioPorNickname(nickname);

        if (u == null) {
            return null;
        }

        if (u instanceof Docente) {
            Docente d = (Docente) u;
            String nomInst = (d.getInstituto() != null) ? d.getInstituto().getNombre() : "Sin Instituto";
            
            List<String> cursos = new ArrayList<>(); // A completar cuando la relación con Cursos esté vinculada
            
            return new DtDocente(
                d.getNickname(),
                d.getNombre(),
                d.getApellido(),
                d.getCorreo(),
                d.getFechaNacimiento(),
                d.getImagenPath(),
                nomInst,
                cursos
            );
        } else {
            Estudiante e = (Estudiante) u;
            
            List<String> ediciones = new ArrayList<>(); // A completar con las ediciones del estudiante
            List<String> programas = new ArrayList<>(); // A completar con los programas del estudiante
            
            return new DtEstudiante(
                e.getNickname(),
                e.getNombre(),
                e.getApellido(),
                e.getCorreo(),
                e.getFechaNacimiento(),
                e.getImagenPath(),
                ediciones,
                programas
            );
        }
    }

    @Override
    public void modificarDatosUsuario(String nickname, String nombre, String apellido, LocalDate fechaNac, String imagenPath) throws Exception {
        ManejadorUsuario mu = ManejadorUsuario.getInstancia();
        Usuario u = mu.buscarUsuarioPorNickname(nickname);

        if (u == null) {
            throw new Exception("El usuario seleccionado no existe.");
        }

        // Se actualizan únicamente los campos editables según la letra
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setFechaNacimiento(fechaNac);
        u.setImagenPath(imagenPath);

        mu.modificarUsuario(u);
    }
    
    @Override
    public List<String> listarNicknamesDocentesPorInstituto(String nomInstituto) {
        ManejadorUsuario mu = ManejadorUsuario.getInstancia();
        List<Usuario> usuarios = mu.getUsuarios();
        List<String> docentes = new ArrayList<>();
    
        for (Usuario u : usuarios) {
            if (u instanceof Docente) {
                Docente d = (Docente) u;
                if (d.getInstituto() != null && d.getInstituto().getNombre().equals(nomInstituto)) {
                    docentes.add(d.getNickname());
                }
            }
        }
        return docentes;
    }
    
    @Override
    public List<String> listarEstudiantes(){
        List<String> estudiantes = new ArrayList<>();
        ManejadorUsuario mu = ManejadorUsuario.getInstancia();
        
        List<Usuario> usuarios = mu.getUsuarios();

        for (Usuario u : usuarios) {
            if (u instanceof Estudiante) {
                estudiantes.add(u.getNickname()); 
            }
        }
        return estudiantes;
    }
}
