package com.grupo3_mat.servidorweb_edext.soap;

import com.grupo3_mat.servidorweb_edext.soap.generated.CursoSoap;
import com.grupo3_mat.servidorweb_edext.soap.generated.DtCurso;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ContratoSoapGeneradoTest {

    @Test
    void clienteGeneradoIncluyeElContadorDeVisitasYLasOperacionesDeTendencia() throws NoSuchMethodException {
        assertNotNull(DtCurso.class.getMethod("getVisitas"));
        assertNotNull(CursoSoap.class.getMethod("listarCursosPorVisitas"));
        assertNotNull(CursoSoap.class.getMethod("registrarVisitaCurso", String.class));
    }
}
