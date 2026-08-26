package com.grupo3_mat.edEXT.Logica.Interfaces;

import com.grupo3_mat.edEXT.Logica.Datatypes.DtCurso;
import java.time.LocalDate;
import java.util.List;

public interface IControladorCurso {
    void altaCurso(String nomInst, String cursoNom, String desc, int dur, int cantHoras, int creditos, String url, LocalDate fecha, List<String> previas);
    List<String> listarCursosPorInstituto(String nomInst);
    DtCurso consultarCurso(String nombreCurso);
    List<String> listarCursos();
}
