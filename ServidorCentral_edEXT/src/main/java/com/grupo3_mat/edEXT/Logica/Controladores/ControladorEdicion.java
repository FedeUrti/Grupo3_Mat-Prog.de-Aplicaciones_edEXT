package com.grupo3_mat.edEXT.Logica.Controladores;

import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorEdicion;
import com.grupo3_mat.edEXT.Logica.Clases.EdicionCurso;
import com.grupo3_mat.edEXT.Logica.Clases.Curso;
import com.grupo3_mat.edEXT.Logica.Clases.Usuario;
import com.grupo3_mat.edEXT.Logica.Clases.Docente;
import com.grupo3_mat.edEXT.Logica.Clases.Estudiante;
import com.grupo3_mat.edEXT.Logica.Clases.InscripcionEC;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorEdicion;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorCurso;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorUsuario;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTEdicionCurso;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTInscripcionEdicion;
import com.grupo3_mat.edEXT.Logica.DataTypes.CriterioOrdenInscripciones;
import com.grupo3_mat.edEXT.Logica.Clases.EstadoInscripcion;

import java.time.LocalDate;
import java.util.*;

public class ControladorEdicion implements IControladorEdicion {

    @Override
    public void altaEdicionCurso(String nombreCurso, DTEdicionCurso datosEdicion) throws Exception {
        ManejadorEdicion me = ManejadorEdicion.getInstancia();
        ManejadorCurso mc = ManejadorCurso.getInstancia();
        ManejadorUsuario mu = ManejadorUsuario.getInstancia();

        if (me.buscarEdicion(datosEdicion.getNombre()) != null) {
            throw new Exception("La edición '" + datosEdicion.getNombre() + "' ya existe.");
        }

        Curso curso = mc.buscarCurso(nombreCurso);
        if (curso == null) {
            throw new Exception("El curso '" + nombreCurso + "' no existe.");
        }

        EdicionCurso nuevaEdicion = new EdicionCurso(
            datosEdicion.getNombre(),
            datosEdicion.getFechaInicio(),
            datosEdicion.getFechaFin(),
            datosEdicion.getCupo(),
            datosEdicion.getFechaPublicacion(),
            curso
        );
        nuevaEdicion.setImagenPath(datosEdicion.getImagenPath());

        if (datosEdicion.getDocentes() != null) {
            for (String nick : datosEdicion.getDocentes()) {
                Usuario usuario = mu.buscarUsuarioPorNickname(nick);
                if (!(usuario instanceof Docente docente)) {
                    throw new Exception("El docente con nickname '" + nick + "' no existe.");
                }
                // El docente debe pertenecer al instituto del curso para participar en su edición.
                if (curso.getInstituto() != null && !docente.perteneceAInstituto(curso.getInstituto().getNombre())) {
                    throw new Exception("El docente '" + nick + "' no pertenece al instituto que brinda el curso.");
                }
                nuevaEdicion.agregarDocente(docente);
            }
        }

        curso.agregarEdicion(nuevaEdicion);
        me.agregarEdicion(nuevaEdicion);
    }

    @Override
    public List<String> listarEdicionesPorCurso(String nombreCurso) {
        ManejadorCurso mc = ManejadorCurso.getInstancia();
        Curso curso = mc.buscarCurso(nombreCurso);

        if (curso == null || curso.getEdiciones() == null) {
            return new ArrayList<>();
        }

        List<String> nombresEdiciones = new ArrayList<>();
        for (EdicionCurso ed : curso.getEdiciones()) {
            nombresEdiciones.add(ed.getNombre());
        }

        return nombresEdiciones;
    }

    @Override
    public DTEdicionCurso mostrarDetalleEdicion(String nombreEdicion) {
        ManejadorEdicion me = ManejadorEdicion.getInstancia();
        
        EdicionCurso edicion = me.buscarEdicion(nombreEdicion);
        if (edicion == null) {
            return null;
        }

        List<String> nombresDocentes = new ArrayList<>();
        if (edicion.getDocentes() != null) {
            for (Docente doc : edicion.getDocentes()) {
                nombresDocentes.add(doc.getNickname());
            }
        }

        return new DTEdicionCurso(
            edicion.getNombre(),
            edicion.getFechaInicio(),
            edicion.getFechaFin(),
            edicion.getCupo(),
            edicion.getCupoDisponible(),
            edicion.getFechaPublicacion(),
            nombresDocentes,
            edicion.getImagenPath()
        );
    }

    @Override
    public void inscribirEstudianteAEdicion(String nicknameEstudiante, String nombreEdicion, LocalDate fechaInscripcion) throws Exception {
        ManejadorUsuario mu = ManejadorUsuario.getInstancia();
        Usuario usr = mu.buscarUsuarioPorNickname(nicknameEstudiante);

        ManejadorEdicion me = ManejadorEdicion.getInstancia();
        EdicionCurso edicion = me.buscarEdicion(nombreEdicion);

        if (edicion == null) {
            throw new Exception("La edición " + nombreEdicion + " no existe.");
        }

        // Se comprueba el cupo antes de crear relaciones o guardar la nueva inscripción.
        if (!edicion.tieneCupoDisponible()) {
            throw new Exception("La edición " + edicion.getNombre() + " no tiene cupos disponibles.");
        }

        // Solo una cuenta de estudiante puede enviar una solicitud para una edición.
        if (usr == null || !(usr instanceof Estudiante)) {
            throw new Exception("El usuario " + nicknameEstudiante + " no existe o no es un estudiante.");
        }
        Estudiante estudiante = (Estudiante) usr;

        // La misma persona no puede tener dos solicitudes para la misma edición.
        if (edicion.estaInscripto(estudiante.getNickname())) {
            throw new Exception("El estudiante ya se encuentra inscripto a esta edición.");
        }

        // La prioridad se calcula al inscribirse: cada rechazo previo finalizado aporta 0.5.
        double prioridad = estudiante.calcularPrioridadInscripcion(edicion.getCurso().getNombre());
        InscripcionEC inscripcion = new InscripcionEC(
            estudiante,
            edicion,
            fechaInscripcion != null ? fechaInscripcion : LocalDate.now(),
            prioridad
        );
        // La inscripción queda visible desde el estudiante y desde la edición.
        estudiante.agregarInscripcion(inscripcion);
        edicion.agregarInscripcion(inscripcion);

        mu.modificarUsuario(estudiante);
    }

    @Override
    public List<String> listarEdicionesDeDocente(String nicknameDocente) {
        ManejadorEdicion me = ManejadorEdicion.getInstancia();
        List<EdicionCurso> ediciones = me.buscarEdicionesPorDocente(nicknameDocente);

        List<String> resultado = new ArrayList<>();
        if (ediciones != null) {
            for (EdicionCurso ed : ediciones) {
                resultado.add(ed.getNombre());
            }
        }
        return resultado;
    }

    @Override
    public String obtenerCursoDeEdicion(String nombreEdicion) {
        ManejadorEdicion me = ManejadorEdicion.getInstancia();
        EdicionCurso edicion = me.buscarEdicion(nombreEdicion);
        if (edicion != null && edicion.getCurso() != null) {
            return edicion.getCurso().getNombre();
        }
        return null;
    }

    @Override
    public List<String> listarEdicionesDeEstudiante(String nicknameEstudiante) {
        ManejadorUsuario mu = ManejadorUsuario.getInstancia();
        Usuario usr = mu.buscarUsuarioPorNickname(nicknameEstudiante);

        if (usr instanceof Estudiante) {
            Estudiante estudiante = (Estudiante) usr;
            if (estudiante.getInscripciones() != null) {
                return new ArrayList<>(estudiante.getInscripciones().keySet());
            }
        }

        return new ArrayList<>();
    }

    @Override
    public List<DTInscripcionEdicion> listarInscripcionesAEdicion(
            String nicknameDocente,
            String nombreEdicion,
            CriterioOrdenInscripciones criterio
    ) throws Exception {
        EdicionCurso edicion = buscarEdicionDeDocente(nicknameDocente, nombreEdicion);
        List<DTInscripcionEdicion> inscripciones = convertirInscripciones(edicion);

        // El criterio solo cambia el orden; la prioridad ya quedó guardada al crear la inscripción.
        if (criterio == CriterioOrdenInscripciones.PRIORIDAD) {
            // Las prioridades altas van primero; fecha y nickname resuelven empates de forma estable.
            inscripciones.sort(Comparator
                    .comparing(DTInscripcionEdicion::getPrioridad, Comparator.nullsLast(Comparator.reverseOrder()))
                    .thenComparing(DTInscripcionEdicion::getFechaInscripcion, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(DTInscripcionEdicion::getNicknameEstudiante));
        } else {
            // El orden por fecha empieza por la inscripción más antigua.
            inscripciones.sort(Comparator
                    .comparing(DTInscripcionEdicion::getFechaInscripcion, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(DTInscripcionEdicion::getNicknameEstudiante));
        }
        return inscripciones;
    }

    @Override
    public List<DTInscripcionEdicion> listarAceptadosAEdicion(String nicknameDocente, String nombreEdicion) throws Exception {
        EdicionCurso edicion = buscarEdicionDeDocente(nicknameDocente, nombreEdicion);
        List<DTInscripcionEdicion> aceptados = convertirInscripciones(edicion);
        // Se devuelven solo las solicitudes que el docente ya aceptó.
        aceptados.removeIf(inscripcion -> inscripcion.getEstado() != EstadoInscripcion.ACEPTADA);
        aceptados.sort(Comparator
                .comparing(DTInscripcionEdicion::getFechaInscripcion, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(DTInscripcionEdicion::getNicknameEstudiante));
        return aceptados;
    }

    @Override
    public List<DTInscripcionEdicion> listarResultadosInscripcionesEstudiante(String nicknameEstudiante) {
        ManejadorUsuario mu = ManejadorUsuario.getInstancia();
        Usuario usuario = mu.buscarUsuarioPorNickname(nicknameEstudiante);
        // Un nickname que no pertenece a un estudiante no tiene resultados de inscripción.
        if (!(usuario instanceof Estudiante estudiante) || estudiante.getInscripciones() == null) {
            return new ArrayList<>();
        }

        List<DTInscripcionEdicion> resultados = new ArrayList<>();
        for (InscripcionEC inscripcion : estudiante.getInscripciones().values()) {
            if (inscripcion != null && inscripcion.getEdicion() != null) {
                resultados.add(convertirInscripcion(inscripcion));
            }
        }
        resultados.sort(Comparator
                .comparing(DTInscripcionEdicion::getFechaInscripcion, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(DTInscripcionEdicion::getNombreEdicion));
        return resultados;
    }

    @Override
    public void cambiarEstadoInscripcion(String nicknameDocente, String nombreEdicion, String nicknameEstudiante, EstadoInscripcion nuevoEstado) throws Exception {
        // Se verifica que el docente tenga permiso antes de cambiar el estado.
        EdicionCurso edicion = buscarEdicionDeDocente(nicknameDocente, nombreEdicion);
        edicion.cambiarEstadoInscripcion(nicknameEstudiante, nuevoEstado);
        ManejadorEdicion me = ManejadorEdicion.getInstancia();
        me.modificarEdicion(edicion);
    }

    private EdicionCurso buscarEdicionDeDocente(String nicknameDocente, String nombreEdicion) throws Exception {
        ManejadorUsuario mu = ManejadorUsuario.getInstancia();
        Usuario usuario = mu.buscarUsuarioPorNickname(nicknameDocente);
        // Un usuario que no es docente no puede administrar inscripciones.
        if (!(usuario instanceof Docente)) {
            throw new Exception("El usuario indicado no es un docente.");
        }

        ManejadorEdicion me = ManejadorEdicion.getInstancia();
        EdicionCurso edicion = me.buscarEdicion(nombreEdicion);
        if (edicion == null) {
            throw new Exception("La edición '" + nombreEdicion + "' no existe.");
        }

        // Solo un docente asignado a esta edición puede revisar sus solicitudes o cambiar estados.
        boolean participa = edicion.getDocentes() != null && edicion.getDocentes().stream()
                .anyMatch(docente -> docente != null && nicknameDocente.equals(docente.getNickname()));
        if (!participa) {
            throw new Exception("El docente no participa en la edición indicada.");
        }
        return edicion;
    }

    private List<DTInscripcionEdicion> convertirInscripciones(EdicionCurso edicion) {
        List<DTInscripcionEdicion> resultado = new ArrayList<>();
        if (edicion.getInscripciones() != null) {
            for (InscripcionEC inscripcion : edicion.getInscripciones().values()) {
                if (inscripcion != null && inscripcion.getEstudiante() != null) {
                    resultado.add(convertirInscripcion(inscripcion));
                }
            }
        }
        return resultado;
    }

    private DTInscripcionEdicion convertirInscripcion(InscripcionEC inscripcion) {
        // El DTO expone datos de la inscripción sin entregar la entidad persistente.
        return new DTInscripcionEdicion(
                inscripcion.getEstudiante().getNickname(),
                inscripcion.getEdicion().getNombre(),
                inscripcion.getFecha(),
                inscripcion.getEstado(),
                inscripcion.getPrioridad()
        );
    }
}