package com.grupo3_mat.servidorweb_edext.soap;

import com.grupo3_mat.servidorweb_edext.soap.generated.Entrada;
import com.grupo3_mat.servidorweb_edext.soap.generated.CursoSoap;
import com.grupo3_mat.servidorweb_edext.soap.generated.DtCurso;
import com.grupo3_mat.servidorweb_edext.soap.generated.DtUsuario;
import com.grupo3_mat.servidorweb_edext.soap.generated.Exception_Exception;
import com.grupo3_mat.servidorweb_edext.soap.generated.UsuarioSoap;
import com.grupo3_mat.servidorweb_edext.soap.generated.ValoracionesXml;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Convierte las formas XML escalares a los tipos idiomáticos que utiliza la web. */
public final class SoapValues {
    private SoapValues() {
    }

    public static String fecha(LocalDate fecha) {
        return fecha == null ? null : fecha.toString();
    }

    public static LocalDate fecha(String fecha) {
        return fecha == null || fecha.isBlank() ? null : LocalDate.parse(fecha);
    }

    public static Map<Integer, Integer> valoraciones(ValoracionesXml xml) {
        Map<Integer, Integer> resultado = new LinkedHashMap<>();
        if (xml != null) {
            for (Entrada entrada : xml.getValoracion()) {
                resultado.put(entrada.getPuntaje(), entrada.getCantidad());
            }
        }
        return resultado;
    }

    public static int cantidadValoraciones(ValoracionesXml xml) {
        return valoraciones(xml).values().stream().mapToInt(Integer::intValue).sum();
    }

    public static List<DtCurso> consultarCursos(CursoSoap servicio, List<String> nombres)
            throws Exception_Exception {
        List<DtCurso> cursos = new ArrayList<>();
        if (nombres == null) {
            return cursos;
        }
        for (int inicio = 0; inicio < nombres.size(); inicio += 100) {
            int fin = Math.min(inicio + 100, nombres.size());
            cursos.addAll(servicio.consultarCursos(nombres.subList(inicio, fin)));
        }
        return cursos;
    }

    public static List<DtUsuario> obtenerInfoUsuarios(UsuarioSoap servicio, List<String> nicknames) {
        List<DtUsuario> usuarios = new ArrayList<>();
        if (nicknames == null) {
            return usuarios;
        }
        for (int inicio = 0; inicio < nicknames.size(); inicio += 100) {
            int fin = Math.min(inicio + 100, nicknames.size());
            usuarios.addAll(servicio.obtenerInfoUsuarios(nicknames.subList(inicio, fin)));
        }
        return usuarios;
    }
}
