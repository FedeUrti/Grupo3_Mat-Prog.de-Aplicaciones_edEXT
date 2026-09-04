package com.grupo3_mat.edEXT.Logica.Controladores;

import com.grupo3_mat.edEXT.Logica.DataTypes.DtCurso;
import com.grupo3_mat.edEXT.Logica.Fabrica;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorInstituto;

import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ControladorCursoTest {

    private static IControladorCurso ctrlCurso;
    private static IControladorInstituto ctrlInstituto;

    // Datos reales tomados del documento DatosPrueba2026.pdf
    private static final String INST_IMERL = "IMERL";
    private static final String INST_IMPII = "IMPII";

    private static final String C1_NOMBRE = "Talleres plenarios";
    private static final String C2_NOMBRE = "Seminarios de Resolución de Problemas";
    private static final String C3_NOMBRE = "Dalavuelta";

    @BeforeAll
    public static void setUp() {
        ctrlCurso = Fabrica.getInstance().getIControladorCurso();
        ctrlInstituto = Fabrica.getInstance().getIControladorInstituto();

        // 1. Carga previa de Institutos requeridos según la letra (IMERL e IMPII)
        try {
            ctrlInstituto.altaInstituto(INST_IMERL);
            ctrlInstituto.altaInstituto(INST_IMPII);
        } catch (Exception ignored) {
            // Ignorar si ya fueron creados previamente en la BD
        }
    }

    // =========================================================================
    // TESTS PARA: altaCurso(...)
    // =========================================================================

    @Test
    @Order(1)
    @DisplayName("altaCurso - C1: Crear 'Talleres plenarios' (Sin previas)")
    public void testAltaCursoC1Exito() {
        // C1: IMERL | 3 semanas | 15 hrs | 1 crédito | 01/02/2026
        assertDoesNotThrow(() -> {
            ctrlCurso.altaCurso(
                INST_IMERL,
                C1_NOMBRE,
                "Talleres plenarios*: presentados por cuatro reconocidos matemáticos...",
                3,
                15,
                1,
                "www.tmu.edu.uy",
                LocalDate.of(2026, 2, 1),
                null
            );
        });
    }

    @Test
    @Order(2)
    @DisplayName("altaCurso - C2: Crear 'Seminarios de Resolución de Problemas' (Con previa C1)")
    public void testAltaCursoC2ConPrevia() {
        // C2: IMERL | 5 semanas | 30 hrs | 2 créditos | 12/07/2026 | Previa: C1
        List<String> previas = new ArrayList<>();
        previas.add(C1_NOMBRE);

        assertDoesNotThrow(() -> {
            ctrlCurso.altaCurso(
                INST_IMERL,
                C2_NOMBRE,
                "Seminario, todos los jueves en Facultad de Ingeniería...",
                5,
                30,
                2,
                "www.tmu.edu.uy",
                LocalDate.of(2026, 7, 12),
                previas
            );
        });
    }

    @Test
    @Order(3)
    @DisplayName("altaCurso - Error al registrar curso repetido (C1)")
    public void testAltaCursoDuplicadoException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            ctrlCurso.altaCurso(
                INST_IMERL,
                C1_NOMBRE,
                "Descripción duplicada",
                3, 15, 1, "www.tmu.edu.uy", LocalDate.of(2026, 2, 1), null
            );
        });

        assertTrue(ex.getMessage().contains("ya existe"));
    }

    @Test
    @Order(4)
    @DisplayName("altaCurso - Error al usar instituto inexistente")
    public void testAltaCursoInstitutoInexistenteException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            ctrlCurso.altaCurso(
                "INSTITUTO_FANTASMA",
                "Curso Test",
                "Desc", 1, 10, 1, "http://test.com", LocalDate.now(), null
            );
        });

        assertTrue(ex.getMessage().contains("no existe"));
    }

    // =========================================================================
    // TESTS PARA: consultarCurso(...)
    // =========================================================================

    @Test
    @Order(5)
    @DisplayName("consultarCurso - Obtener datos y previa del curso C2")
    public void testConsultarCursoExito() {
        DtCurso dt = ctrlCurso.consultarCurso(C2_NOMBRE);

        assertNotNull(dt);
        assertEquals(C2_NOMBRE, dt.getNombre());
        assertEquals(INST_IMERL, dt.getInstituto());
        assertEquals(2, dt.getCreditos());
        assertTrue(dt.getPrevias().contains(C1_NOMBRE)); // Verifica asignación de la previa C1
    }

    @Test
    @Order(6)
    @DisplayName("consultarCurso - Error al consultar curso no registrado")
    public void testConsultarCursoInexistenteException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            ctrlCurso.consultarCurso("Curso Inexistente");
        });

        assertTrue(ex.getMessage().contains("no existe"));
    }

    // =========================================================================
    // TESTS PARA: Listados de cursos
    // =========================================================================

    @Test
    @Order(7)
    @DisplayName("listarCursos - Comprobar presencia de cursos C1 y C2")
    public void testListarCursos() {
        List<String> cursos = ctrlCurso.listarCursos();

        assertNotNull(cursos);
        assertTrue(cursos.contains(C1_NOMBRE));
        assertTrue(cursos.contains(C2_NOMBRE));
    }

    @Test
    @Order(8)
    @DisplayName("listarCursosPorInstituto - Filtrar cursos pertenecientes a IMERL")
    public void testListarCursosPorInstituto() {
        List<String> cursosImerl = ctrlCurso.listarCursosPorInstituto(INST_IMERL);

        assertNotNull(cursosImerl);
        assertTrue(cursosImerl.contains(C1_NOMBRE));
        assertTrue(cursosImerl.contains(C2_NOMBRE));
    }

    // =========================================================================
    // TESTS PARA: obtenerEdicionVigente(...)
    // =========================================================================

    @Test
    @Order(9)
    @DisplayName("obtenerEdicionVigente - Retorna cadena vacía cuando el curso no posee ediciones activas")
    public void testObtenerEdicionVigenteSinEdicion() throws Exception {
        String edicion = ctrlCurso.obtenerEdicionVigente(C1_NOMBRE, LocalDate.of(2026, 3, 1));

        assertNotNull(edicion);
        assertEquals("", edicion);
    }

    @Test
    @Order(10)
    @DisplayName("obtenerEdicionVigente - Error al consultar curso inexistente")
    public void testObtenerEdicionVigenteException() {
        Exception ex = assertThrows(Exception.class, () -> {
            ctrlCurso.obtenerEdicionVigente("Curso Inexistente", LocalDate.now());
        });

        assertTrue(ex.getMessage().contains("no existe"));
    }
}