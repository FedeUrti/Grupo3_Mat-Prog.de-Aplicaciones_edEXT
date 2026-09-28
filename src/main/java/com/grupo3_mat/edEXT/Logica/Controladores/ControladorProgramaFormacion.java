package com.grupo3_mat.edEXT.Logica.Controladores;

import com.grupo3_mat.edEXT.Logica.Clases.Categoria;
import com.grupo3_mat.edEXT.Logica.Clases.Curso;
import com.grupo3_mat.edEXT.Logica.Clases.Estudiante;
import com.grupo3_mat.edEXT.Logica.Clases.InscripcionPF;
import com.grupo3_mat.edEXT.Logica.Clases.Docente;
import com.grupo3_mat.edEXT.Logica.Clases.ProgramaFormacion;
import com.grupo3_mat.edEXT.Logica.Clases.Usuario;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtCurso;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTProgramaFormacion;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorProgramaFormacion;
import com.grupo3_mat.edEXT.Logica.Fabrica;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorProgramaFormacion;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorUsuario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ControladorProgramaFormacion implements IControladorProgramaFormacion {

    @Override
    @Deprecated
    public void crearProgramaFormacion(String nombre, String descripcion, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaAlta, String imagenPath) throws Exception {
        // Se conserva la firma para detectar llamadas antiguas y explicar el dato que ahora es obligatorio.
        throw new Exception("Debe indicar el nickname del docente responsable del programa.");
    }

    @Override
    public void crearProgramaFormacion(String nicknameDocente, String nombre, String descripcion, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaAlta, String imagenPath) throws Exception {
        if (nicknameDocente == null || nicknameDocente.isBlank()) {
            throw new Exception("Debe indicar el docente responsable del programa.");
        }
        registrarPrograma(nicknameDocente, nombre, descripcion, fechaInicio, fechaFin, fechaAlta, imagenPath);
    }

    private void registrarPrograma(String nicknameDocente, String nombre, String descripcion,
                                   LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaAlta,
                                   String imagenPath) throws Exception {
        ManejadorProgramaFormacion mpf = ManejadorProgramaFormacion.getInstancia();
        // No se permiten dos programas con el mismo nombre.
        if (mpf.buscarPrograma(nombre) != null) {
            throw new Exception("Ya existe un programa de formación con el nombre: " + nombre);
        }

        ProgramaFormacion pf = new ProgramaFormacion(nombre, descripcion, fechaInicio, fechaFin, fechaAlta, imagenPath);
        if (nicknameDocente != null) {
            // La relación se asigna solo después de confirmar que el nickname corresponde a un docente.
            Usuario usuario = ManejadorUsuario.getInstancia().buscarUsuarioPorNickname(nicknameDocente);
            if (!(usuario instanceof Docente docente)) {
                throw new Exception("El docente responsable no existe.");
            }
            pf.setDocente(docente);
        }
        mpf.agregarPrograma(pf);
    }

    @Override
    public void agregarCursoAPrograma(String nombrePrograma, String nombreCurso) throws Exception {
        ManejadorProgramaFormacion mpf = ManejadorProgramaFormacion.getInstancia();
        mpf.agregarCursoAPrograma(nombrePrograma, nombreCurso);
    }

  @Override
    public List<DTProgramaFormacion> listarProgramas() throws Exception {
        ManejadorProgramaFormacion mpf = ManejadorProgramaFormacion.getInstancia();
        List<DTProgramaFormacion> resultado = new ArrayList<>();
        // El controlador transforma las entidades del modelo en DTOs para la interfaz.
        for (ProgramaFormacion programa : mpf.listarEntidades()) {
            resultado.add(convertirADataType(programa));
        }
        return resultado;
    }

    @Override
    public DTProgramaFormacion seleccionarPrograma(String nombre) throws Exception {
        return consultarPrograma(nombre);
    }

    @Override
    public DtCurso seleccionarCurso(String nombreCurso) throws Exception {
        IControladorCurso cc = Fabrica.getInstance().getIControladorCurso();
        return cc.consultarCurso(nombreCurso);
    }

    @Override
    public DTProgramaFormacion consultarPrograma(String nombre) throws Exception {
        ManejadorProgramaFormacion mpf = ManejadorProgramaFormacion.getInstancia();
        ProgramaFormacion pf = mpf.buscarPrograma(nombre);

        if (pf == null) {
            throw new Exception("El programa de formación seleccionado no existe.");
        }

        return convertirADataType(pf);
    }

    private DTProgramaFormacion convertirADataType(ProgramaFormacion pf) {
        // Se exponen nombres simples en lugar de entregar entidades JPA fuera de la capa lógica.
        List<String> nombresCursos = new ArrayList<>();
        if (pf.getCursos() != null) {
            for (Curso c : pf.getCursos().values()) {
                nombresCursos.add(c.getNombre());
            }
        }

        List<String> nombresCategorias = new ArrayList<>();
        if (pf.getCategorias() != null) {
            for (Categoria cat : pf.getCategorias()) {
                nombresCategorias.add(cat.getNombre());
            }
        }

        // Los institutos del programa se deducen de los cursos que lo integran.
        List<String> nombresInstitutos = new ArrayList<>();
        for (com.grupo3_mat.edEXT.Logica.Clases.Instituto instituto : pf.getInstitutos()) {
            nombresInstitutos.add(instituto.getNombre());
        }

        return new DTProgramaFormacion(
                pf.getNombre(),
                pf.getDescripcion(),
                pf.getFechaInicio(),
                pf.getFechaFin(),
                pf.getFechaAlta(),
                nombresCursos,
                nombresCategorias,
                pf.getImagenPath(),
                pf.getDocente() != null ? pf.getDocente().getNickname() : null,
                nombresInstitutos
        );
    }

    @Override
    public void inscribirEstudianteAPrograma(String nickname, String nombrePrograma, LocalDate fechaInscripcion) throws Exception {
        ManejadorUsuario mu = ManejadorUsuario.getInstancia();
        ManejadorProgramaFormacion mpf = ManejadorProgramaFormacion.getInstancia();

        Usuario u = mu.buscarUsuarioPorNickname(nickname);
        if (u == null || !(u instanceof Estudiante)) {
            throw new Exception("El estudiante especificado no existe.");
        }
        Estudiante est = (Estudiante) u;

        ProgramaFormacion pf = mpf.buscarPrograma(nombrePrograma);
        if (pf == null) {
            throw new Exception("El programa de formación no existe.");
        }

        if (est.estaInscriptoAPrograma(nombrePrograma)) {
            throw new Exception("El estudiante ya está inscripto a este programa de formación.");
        }

        InscripcionPF inscripcion = new InscripcionPF(est, pf, fechaInscripcion);
        est.agregarInscripcionPrograma(inscripcion);
        pf.agregarInscripcion(inscripcion);

        mu.modificarUsuario(est);
    }
}