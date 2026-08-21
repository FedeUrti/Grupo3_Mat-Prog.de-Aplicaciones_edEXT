package com.grupo3_mat.edEXT.Logica.Controladores;

import com.grupo3_mat.edEXT.Logica.Clases.Curso;
import com.grupo3_mat.edEXT.Logica.Clases.Instituto;
import com.grupo3_mat.edEXT.Logica.Datatypes.DtCurso;
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
    public List<String> listarCursosPorInstituto(String nomInst) 
    {
        ManejadorCurso mc = ManejadorCurso.getInstancia(); // Obtengo la instancia del manejador de curso
        List<Curso> cursos = mc.listarCursosPorInstituto(nomInst);
        List<String> nombres = new ArrayList<>();
        for (Curso c : cursos) {
            nombres.add(c.getNombre());
        }
        return nombres;
    }

    @Override
    public DtCurso consultarCurso(String nombreCurso) //Caso de uso Consultar cursos 
    {
        //Obtengo las instancias de manejador curso y llamo a la funcion buscarCurso del manejador.
        ManejadorCurso mc = ManejadorCurso.getInstancia();
        Curso curso = mc.buscarCurso(nombreCurso);
        //Si no encuentro un curso, tiro una excepcion que me dice que el curso NO existe                                            
        if (curso == null) {
            throw new IllegalArgumentException("El curso '" + nombreCurso + "' no existe.");
        }
        // Si el curso es real, en caso de tener listo las previas del mismo.
        List<String> nomPrevias = new ArrayList<>();
        for (Curso previa : curso.getPrevias()) {
            nomPrevias.add(previa.getNombre());
        }

        // MOCK de ediciones y programas para testeo.
        List<String> edicionesMock = List.of("Edicion 2026-1", "Edicion 2026-2");
        List<String> programasMock = List.of("Programa Desarrollo Web");
        //Retorno un Datatype de Curso con los datos  del solicitado
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
                edicionesMock,
                programasMock
        );
    }

    @Override
    public List<String> listarCursos() //Lista de cursos segun su nombre
    {
        ManejadorCurso mc = ManejadorCurso.getInstancia();
        List<Curso> cursos = mc.listarCursos();
        List<String> nombres = new ArrayList<>();
        for (Curso c : cursos) {
            nombres.add(c.getNombre());
        }
        return nombres;
    }
}