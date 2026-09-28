package com.grupo3_mat.edEXT.Logica.Controladores;

import com.grupo3_mat.edEXT.Logica.Clases.Categoria;
import com.grupo3_mat.edEXT.Logica.Clases.Curso;
import com.grupo3_mat.edEXT.Logica.Clases.EdicionCurso;
import com.grupo3_mat.edEXT.Logica.Clases.Instituto;
import com.grupo3_mat.edEXT.Logica.Clases.ProgramaFormacion;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCurso;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorCategoria;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorCurso;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorInstituto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ControladorCurso implements IControladorCurso {

    @Override
    public void altaCurso(String nomInst, String cursoNom, String desc, int dur, int cantHoras, 
                          int creditos, String url, LocalDate fecha, List<String> previas, 
                          List<String> categorias, String imagenPath) throws Exception {
        ManejadorCurso mc = ManejadorCurso.getInstancia();
        ManejadorInstituto mi = ManejadorInstituto.getInstancia();
        ManejadorCategoria mcat = ManejadorCategoria.getInstancia();

        if (mc.buscarCurso(cursoNom) != null) {
            throw new Exception("El curso '" + cursoNom + "' ya existe.");
        }

        Instituto inst = mi.buscarInstituto(nomInst);
        if (inst == null) {
            throw new Exception("El instituto '" + nomInst + "' no existe.");
        }

        Curso curso = new Curso(inst, cursoNom, desc, dur, cantHoras, creditos, url, fecha);
        curso.setImagenPath(imagenPath);

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

        if (categorias != null && !categorias.isEmpty()) {
            List<Categoria> listaCat = new ArrayList<>();
            for (String nomCat : categorias) {
                Categoria cat = mcat.buscarCategoria(nomCat);
                if (cat != null) {
                    listaCat.add(cat);
                }
            }
            curso.setCategorias(listaCat);
        }

        mc.agregarCurso(curso);
    }

    @Override
    public List<String> listarCursosPorInstituto(String nomInst) {
        ManejadorCurso mc = ManejadorCurso.getInstancia();
        List<Curso> cursos = mc.listarCursosPorInstituto(nomInst);
        List<String> nombres = new ArrayList<>();
        if (cursos != null) {
            for (Curso c : cursos) {
                nombres.add(c.getNombre());
            }
        }
        return nombres;
    }

    @Override
    public List<String> listarCursosPorCategoria(String nomCategoria) {
        ManejadorCurso mc = ManejadorCurso.getInstancia();
        List<Curso> cursos = mc.listarCursosPorCategoria(nomCategoria);
        List<String> nombres = new ArrayList<>();
        if (cursos != null) {
            for (Curso c : cursos) {
                nombres.add(c.getNombre());
            }
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

        // El DTO entrega nombres para que la presentación no dependa de entidades JPA.
        List<String> ediciones = new ArrayList<>();
        if (curso.getEdiciones() != null) {
            for (EdicionCurso ed : curso.getEdiciones()) {
                ediciones.add(ed.getNombre());
            }
        }

        List<String> programas = new ArrayList<>();
        if (curso.getProgramas() != null) {
            for (ProgramaFormacion pf : curso.getProgramas()) {
                programas.add(pf.getNombre());
            }
        }

        List<String> previas = new ArrayList<>();
        if (curso.getPrevias() != null) {
            for (Curso p : curso.getPrevias()) {
                previas.add(p.getNombre());
            }
        }

        // También se traducen las categorías a nombres simples para el DTO.
        List<String> categorias = new ArrayList<>();
        if (curso.getCategorias() != null) {
            for (Categoria cat : curso.getCategorias()) {
                categorias.add(cat.getNombre());
            }
        }

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
                programas,
                categorias,
                curso.getImagenPath()
        );
    }

    @Override
    public List<String> listarCursos() {
        ManejadorCurso mc = ManejadorCurso.getInstancia();
        List<Curso> cursos = mc.listarCursos();
        List<String> nombres = new ArrayList<>();
        if (cursos != null) {
            for (Curso c : cursos) {
                nombres.add(c.getNombre());
            }
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