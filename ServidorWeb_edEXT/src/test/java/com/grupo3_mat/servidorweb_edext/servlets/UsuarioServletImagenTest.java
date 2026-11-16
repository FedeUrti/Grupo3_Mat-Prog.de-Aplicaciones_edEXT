package com.grupo3_mat.servidorweb_edext.servlets;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UsuarioServletImagenTest {
    @Test
    void conservaLasFotosDePerfilConNombreGeneradoPorLaEstacion() {
        assertEquals("ana_1731772800123.png",
                UsuarioServlet.imagenPerfil("ana_1731772800123.png", false));
        assertEquals("María-Lopez_1731772800123.JPG",
                UsuarioServlet.imagenPerfil("María-Lopez_1731772800123.JPG", true));
    }

    @Test
    void reemplazaRutasNoSegurasPorElPlaceholder() {
        assertEquals("estudiante_placeholder.png",
                UsuarioServlet.imagenPerfil("../ana_1731772800123.png", false));
    }
}
