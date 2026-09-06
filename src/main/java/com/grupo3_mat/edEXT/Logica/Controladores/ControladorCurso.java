package com.grupo3_mat.edEXT.Logica.Controladores;

import com.grupo3_mat.edEXT.Logica.Clases.Curso;
import com.grupo3_mat.edEXT.Logica.Clases.EdicionCurso;
import com.grupo3_mat.edEXT.Logica.Clases.Instituto;
import com.grupo3_mat.edEXT.Logica.Clases.ProgramaFormacion;
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
    public DtCurso consultarCurso(String nombreCurso) throws Exception {
        ManejadorCurso mc = ManejadorCurso.getInstancia();
        Curso curso = mc.buscarCurso(nombreCurso);

        if (curso == null) {
            throw new Exception("No existe el curso especificado.");
        }

        // 1. Obtener nombres de las ediciones
        List<String> ediciones = new ArrayList<>();
        if (curso.getEdiciones() != null) {
            for (EdicionCurso ed : curso.getEdiciones()) {
                ediciones.add(ed.getNombre());
            }
        }

        // 2. Obtener nombres de los programas de formación asociados
        List<String> programas = new ArrayList<>();
        if (curso.getProgramas() != null) {
            for (ProgramaFormacion pf : curso.getProgramas()) {
                programas.add(pf.getNombre());
            }
        }

        // 3. Obtener nombres de las previas
        List<String> previas = new ArrayList<>();
        if (curso.getPrevias() != null) {
            for (Curso p : curso.getPrevias()) {
                previas.add(p.getNombre());
            }
        }

        // 4. Retornar el DtCurso con todos sus datos sincronizados
        return new DtCurso(
                curso.getNombre(),
                curso.getDescripcion(),
                curso.getDuracion(),
                curso.getCantHoras(),
                curso.getCreditos(),
                curso.getUrl(),
                curso.getFecha(),
                curso.getInstituto() != null ? curso.getInstituto().getNombre() : "",
                previas,
                ediciones,
                programas
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