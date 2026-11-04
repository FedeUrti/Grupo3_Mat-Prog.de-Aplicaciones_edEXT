package com.grupo3_mat.edEXT.Logica.Controladores;

import com.grupo3_mat.edEXT.Logica.Clases.Categoria;
import com.grupo3_mat.edEXT.Logica.Clases.Curso;
import com.grupo3_mat.edEXT.Logica.Clases.EdicionCurso;
import com.grupo3_mat.edEXT.Logica.Clases.Instituto;
import com.grupo3_mat.edEXT.Logica.Clases.ProgramaFormacion;
import com.grupo3_mat.edEXT.Logica.Clases.ValoracionCurso;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCurso;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorCategoria;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorCurso;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorInstituto;
import com.grupo3_mat.edEXT.Logica.Manejadores.ManejadorUsuario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jakarta.jws.WebService;

@WebService(endpointInterface = "com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCurso",
        serviceName = "CursoSoapService", portName = "CursoSoapPort",
        targetNamespace = "http://soap.edext.grupo3_mat.com/")
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
    public List<DtCurso> consultarCursos(List<String> nombresCurso) throws Exception {
        if (nombresCurso == null || nombresCurso.isEmpty()) {
            return new ArrayList<>();
        }
        if (nombresCurso.size() > 100) {
            throw new IllegalArgumentException("La consulta por lotes admite hasta 100 cursos.");
        }
        List<DtCurso> cursos = new ArrayList<>(nombresCurso.size());
        for (String nombreCurso : nombresCurso) {
            cursos.add(consultarCurso(nombreCurso));
        }
        return cursos;
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

        EdicionCurso edicionVigente = curso.obtenerEdicionVigente(fechaReferencia);

        return (edicionVigente != null) ? edicionVigente.getNombre() : "";
    }

    @Override
    public void ingresarValoracion(String nombreCurso, String nicknameEstudiante, int puntaje) throws Exception {
        if (puntaje < 1 || puntaje > 5) {
            throw new IllegalArgumentException("El puntaje debe estar entre 1 y 5.");
        }
        ManejadorCurso.getInstancia().agregarValoracionCurso(nombreCurso, nicknameEstudiante, puntaje);
    }

    @Override
    public int consultarValoracion(String nombreCurso, String nicknameEstudiante) throws Exception {
        ValoracionCurso valoracion = ManejadorCurso.getInstancia()
                .buscarValoracionCurso(nombreCurso, nicknameEstudiante);
        if (valoracion == null) {
            throw new Exception("El estudiante no publicó una valoración para este curso.");
        }
        return valoracion.getPuntaje();
    }

    @Override
    public List<String> listarCursosValorables(String nicknameEstudiante) {
        return ManejadorCurso.getInstancia().listarCursosValorables(nicknameEstudiante);
    }

    @Override
    public BigDecimal obtenerPuntajePromedioCurso(String nombreCurso) throws Exception {
        Curso curso = ManejadorCurso.getInstancia().buscarCurso(nombreCurso);
        if (curso == null) {
            throw new Exception("No existe el curso especificado.");
        }
        return curso.obtenerPuntajePromedio();
    }

    @Override
    public Map<Integer, Integer> obtenerConteoValoracionesCurso(String nombreCurso) throws Exception {
        Curso curso = ManejadorCurso.getInstancia().buscarCurso(nombreCurso);
        if (curso == null) {
            throw new Exception("No existe el curso especificado.");
        }
        return curso.obtenerConteoValoraciones();
    }

    @Override
    public Integer obtenerValoracionEstudiante(String nombreCurso, String nicknameEstudiante) {
        ValoracionCurso valoracion = ManejadorCurso.getInstancia()
                .buscarValoracionCurso(nombreCurso, nicknameEstudiante);
        return valoracion == null ? null : valoracion.getPuntaje();
    }
}