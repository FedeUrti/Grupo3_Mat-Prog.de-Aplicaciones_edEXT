package com.grupo3_mat.servidorweb_edext.servlets;

import com.grupo3_mat.servidorweb_edext.soap.generated.DtResultadoEstudio;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActaEstudioServletTest {

    @Test
    void permiteElActaConEdicionesCerradasSinExigirAprobacion() {
        assertTrue(ActaEstudioServlet.todosLosCursosCerrados(List.of(
                resultado("Curso A", "Edición A1", 5),
                resultado("Curso A", "Edición A2", 8),
                resultado("Curso B", "Edición B1", 3))));
    }

    @Test
    void requiereUnaEdicionCerradaParaCadaCursoPeroNoUnaEvaluacion() {
        assertTrue(ActaEstudioServlet.todosLosCursosCerrados(List.of(
                resultado("Curso A", "Edición A1", 5),
                resultadoCerradoSinEvaluacion("Curso B", "Edición B1"))));
        assertFalse(ActaEstudioServlet.todosLosCursosCerrados(List.of(
                resultado("Curso A", "Edición A1", 8),
                resultadoSinEdicion("Curso B"))));
        assertFalse(ActaEstudioServlet.todosLosCursosCerrados(List.of()));
    }

    private static DtResultadoEstudio resultado(String curso, String edicion, int evaluacion) {
        DtResultadoEstudio resultado = new DtResultadoEstudio();
        resultado.setNombreCurso(curso);
        resultado.setNombreEdicion(edicion);
        resultado.setEvaluacion(evaluacion);
        resultado.setTieneResultado(true);
        resultado.setAprobado(evaluacion >= 6);
        return resultado;
    }

    private static DtResultadoEstudio resultadoCerradoSinEvaluacion(String curso, String edicion) {
        DtResultadoEstudio resultado = new DtResultadoEstudio();
        resultado.setNombreCurso(curso);
        resultado.setNombreEdicion(edicion);
        return resultado;
    }

    private static DtResultadoEstudio resultadoSinEdicion(String curso) {
        DtResultadoEstudio resultado = new DtResultadoEstudio();
        resultado.setNombreCurso(curso);
        return resultado;
    }
}
