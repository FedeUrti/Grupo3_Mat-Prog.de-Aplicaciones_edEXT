package com.grupo3_mat.edEXT.Logica.Interfaces;

import com.grupo3_mat.edEXT.Logica.DataTypes.DtCurso;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IControladorCurso {
    void altaCurso(String nomInst, String cursoNom, String desc, int dur, int cantHoras, 
                   int creditos, String url, LocalDate fecha, List<String> previas, 
                   List<String> categorias, String imagenPath) throws Exception;
    
    List<String> listarCursosPorInstituto(String nomInst);
    List<String> listarCursosPorCategoria(String nomCategoria);
    DtCurso consultarCurso(String nombreCurso) throws Exception;
    List<String> listarCursos();
    String obtenerEdicionVigente(String nombreCurso, LocalDate fechaReferencia) throws Exception;
    void ingresarValoracion(String nombreCurso, String nicknameEstudiante, int puntaje) throws Exception;
    int consultarValoracion(String nombreCurso, String nicknameEstudiante) throws Exception;
    List<String> listarCursosValorables(String nicknameEstudiante);
    BigDecimal obtenerPuntajePromedioCurso(String nombreCurso) throws Exception;
    Map<Integer, Integer> obtenerConteoValoracionesCurso(String nombreCurso) throws Exception;
    Integer obtenerValoracionEstudiante(String nombreCurso, String nicknameEstudiante);
}