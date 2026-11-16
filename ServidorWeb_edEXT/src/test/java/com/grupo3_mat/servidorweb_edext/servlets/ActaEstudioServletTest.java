package com.grupo3_mat.servidorweb_edext.servlets;

import com.grupo3_mat.servidorweb_edext.soap.generated.DtResultadoEstudio;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActaEstudioServletTest {

    @Test
    void requiereAprobacionDeCadaCursoYPermiteReintentos() {
        assertTrue(ActaEstudioServlet.todosLosCursosAprobados(List.of(
                resultado("Curso A", 5),
                resultado("Curso A", 8),
                resultado("Curso B", 6))));
        assertFalse(ActaEstudioServlet.todosLosCursosAprobados(List.of(
                resultado("Curso A", 8),
                resultadoSinResultado("Curso B"))));
        assertFalse(ActaEstudioServlet.todosLosCursosAprobados(List.of(
                resultado("Curso A", 8),
                resultado("Curso B", 5))));
        assertFalse(ActaEstudioServlet.todosLosCursosAprobados(List.of()));
    }

    private static DtResultadoEstudio resultado(String curso, int evaluacion) {
        DtResultadoEstudio resultado = new DtResultadoEstudio();
        resultado.setNombreCurso(curso);
        resultado.setEvaluacion(evaluacion);
        resultado.setTieneResultado(true);
        resultado.setAprobado(evaluacion >= 6);
        return resultado;
    }

    private static DtResultadoEstudio resultadoSinResultado(String curso) {
        DtResultadoEstudio resultado = new DtResultadoEstudio();
        resultado.setNombreCurso(curso);
        return resultado;
    }
}
