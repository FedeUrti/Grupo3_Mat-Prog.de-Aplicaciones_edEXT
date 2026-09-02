package com.grupo3_mat.edEXT.Logica.Controladores;

import com.grupo3_mat.edEXT.Logica.Clases.Curso;
import com.grupo3_mat.edEXT.Logica.Clases.EdicionCurso;
import com.grupo3_mat.edEXT.Logica.Clases.Instituto;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCurso;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorCurso;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorInstituto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ControladorCurso implements IControladorCurso {

    @Override
    public void altaCurso(String nomInst, String cursoNom, String desc, int dur, int cantHoras, int creditos, String url, LocalDate fecha, List<String> previas) {
        ManejadorCurso mc = ManejadorCurso.getInstancia();
        ManejadorInstituto mi = ManejadorInstituto.getInstancia();

        if (mc.buscarCurso(cursoNom) != null) {
            throw new IllegalArgumentException("El curso '" + cursoNom + "' ya existe.");
        }

        Instituto inst = mi.buscarInstituto(nomInst);
        if (inst == null) {
            throw new IllegalArgumentException("El instituto '" + nomInst + "' no existe.");
        }

        Curso curso = new Curso(inst, cursoNom, desc, dur, cantHoras, creditos, url, fecha);

        if (previas != null && !previas.isEmpty()) {
            List<Curso> listaPrevias = new ArrayList<>();
            for (String nomPrevia : previas) {
                Curso cPrevia = mc.buscarCurso(nomPrevia);
                if (cPrevia != null) {
                    listaPrevias.add(cPrevia);
                }
            }
            curso.setPrevias(listaPrevias);
        }

        mc.agregarCurso(curso);
    }

    @Override
    public List<String> listarCursosPorInstituto(String nomInst) {
        ManejadorCurso mc = ManejadorCurso.getInstancia();
        List<Curso> cursos = mc.listarCursosPorInstituto(nomInst);
        List<String> nombres = new ArrayList<>();
        for (Curso c : cursos) {
            nombres.add(c.getNombre());
        }
        return nombres;
    }

    @Override
    public DtCurso consultarCurso(String nombreCurso) {
        ManejadorCurso mc = ManejadorCurso.getInstancia();
        Curso curso = mc.buscarCurso(nombreCurso);

        if (curso == null) {
            throw new IllegalArgumentException("El curso '" + nombreCurso + "' no existe.");
        }

        // 1. Obtener nombres de las previas
        List<String> nomPrevias = new ArrayList<>();
        if (curso.getPrevias() != null) {
            for (Curso previa : curso.getPrevias()) {
                nomPrevias.add(previa.getNombre());
            }
        }

        // 2. Obtener nombres de las EDICIONES REALES desde la entidad Curso
        List<String> nomEdiciones = new ArrayList<>();
        if (curso.getEdiciones() != null) {
            for (EdicionCurso edicion : curso.getEdiciones()) {
                nomEdiciones.add(edicion.getNombre());
            }
        }

        // 3. Obtener nombres de los PROGRAMAS (Descomentar si agregás la relación en la entidad Curso)
        List<String> nomProgramas = new ArrayList<>();
        /*
        if (curso.getProgramas() != null) {
            for (ProgramaFormacion pf : curso.getProgramas()) {
                nomProgramas.add(pf.getNombre());
            }
        }
        */

        return new DtCurso(
                curso.getNombre(),
                curso.getInstituto().getNombre(),
                curso.getDescripcion(),
                curso.getDuracion(),
                curso.getCantHoras(),
                curso.getCreditos(),
                curso.getUrl(),
                curso.getFecha(),
                nomPrevias,
                nomEdiciones,
                nomProgramas
        );
    }

    @Override
    public List<String> listarCursos() {
        ManejadorCurso mc = ManejadorCurso.getInstancia();
        List<Curso> cursos = mc.listarCursos();
        List<String> nombres = new ArrayList<>();
        for (Curso c : cursos) {
            nombres.add(c.getNombre());
        }
        return nombres;
    }
    
    @Override
    public String obtenerEdicionVigente(String nombreCurso, LocalDate fechaReferencia) throws Exception {
        ManejadorCurso mc = ManejadorCurso.getInstancia();
        Curso curso = mc.buscarCurso(nombreCurso);

        if (curso == null) {
            throw new Exception("El curso " + nombreCurso + " no existe.");
        }

        EdicionCurso edicionVigente = curso.obtenerProximaEdicion(fechaReferencia);

        return (edicionVigente != null) ? edicionVigente.getNombre() : "";
    }
}