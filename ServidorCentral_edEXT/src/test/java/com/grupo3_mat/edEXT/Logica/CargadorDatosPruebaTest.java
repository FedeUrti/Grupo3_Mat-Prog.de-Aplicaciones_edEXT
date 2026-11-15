package com.grupo3_mat.edEXT.Logica;

import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCategoria;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorEdicion;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorInstituto;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorProgramaFormacion;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorUsuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CargadorDatosPruebaTest {

    @Test
    void constructorPredeterminadoConfiguraLasDependenciasDeProduccion() {
        assertNotNull(new CargadorDatosPrueba());
    }

    @Test
    void cargaTodosLosDatosSinConectarseAUnaBaseReal() throws Exception {
        Map<String, Integer> llamadas = new HashMap<>();
        FakePersistence persistencia = fakePersistence(null);

        crearCargador(llamadas, persistencia, null).cargarDatosTotales();

        assertEquals(17, persistencia.sentencias().size());
        assertEquals("DELETE FROM Usuario_Seguimiento", persistencia.sentencias().get(0));
        assertEquals("DELETE FROM ValoracionCurso", persistencia.sentencias().get(6));
        assertEquals("DELETE FROM Instituto", persistencia.sentencias().get(16));
        assertTrue(persistencia.confirmada().get());
        assertFalse(persistencia.revertida().get());
        assertTrue(persistencia.cerrada().get());
        assertTrue(llamadas.getOrDefault("altaInstituto", 0) > 0);
        assertTrue(llamadas.getOrDefault("altaCategoria", 0) > 0);
        assertEquals(6, llamadas.getOrDefault("altaUsuario", 0));
        assertEquals(9, llamadas.getOrDefault("altaUsuarioConInstitutos", 0));
        assertTrue(llamadas.getOrDefault("seguirUsuario", 0) > 0);
        assertTrue(llamadas.getOrDefault("altaCurso", 0) > 0);
        assertEquals(10, persistencia.actualizacionesVisitas().size());
        Map<String, Integer> visitas = new HashMap<>();
        for (Map<String, Object> actualizacion : persistencia.actualizacionesVisitas()) {
            visitas.put((String) actualizacion.get("nombre"), (Integer) actualizacion.get("visitas"));
        }
        assertEquals(430, visitas.get("Flor del Ceibo"));
        assertEquals(318, visitas.get("Taller de robótica educativa"));
        assertEquals(98, visitas.get("Inclusión Energética"));
        assertTrue(llamadas.getOrDefault("altaEdicionCurso", 0) > 0);
        assertEquals(44, llamadas.getOrDefault("inscribirEstudianteAEdicion", 0));
        assertEquals(25, llamadas.getOrDefault("evaluarInscripcion", 0));
        assertEquals(4, llamadas.getOrDefault("cerrarEdicion", 0));
        assertEquals(22, llamadas.getOrDefault("ingresarValoracion", 0));
        assertTrue(llamadas.getOrDefault("crearProgramaFormacion", 0) > 0);
        assertTrue(llamadas.getOrDefault("inscribirEstudianteAPrograma", 0) > 0);
    }

    @Test
    void noLimpiaNiCargaCuandoYaHayDatosEnLaBase() {
        Map<String, Integer> llamadas = new HashMap<>();
        FakePersistence persistencia = fakePersistence(null, true);
        CargadorDatosPrueba cargador = crearCargador(llamadas, persistencia, null);

        Exception error = assertThrows(Exception.class, cargador::cargarDatosTotales);

        assertTrue(error.getMessage().contains("Ya existen datos"));
        assertTrue(persistencia.sentencias().isEmpty());
        assertTrue(llamadas.isEmpty());
        assertFalse(persistencia.confirmada().get());
        assertTrue(persistencia.cerrada().get());
    }

    @Test
    void revierteLaLimpiezaYPropagaLaFaseCuandoFallaUnaSentencia() {
        FakePersistence persistencia = fakePersistence("DELETE FROM Usuario");
        CargadorDatosPrueba cargador = crearCargador(new HashMap<>(), persistencia, null);

        Exception error = assertThrows(Exception.class, cargador::cargarDatosTotales);

        assertTrue(error.getMessage().contains("limpieza de los datos anteriores"));
        assertTrue(persistencia.revertida().get());
        assertTrue(persistencia.cerrada().get());
    }

    @Test
    void informaLaFaseDeCargaCuandoFallaUnControlador() {
        FakePersistence persistencia = fakePersistence(null);
        CargadorDatosPrueba cargador = crearCargador(new HashMap<>(), persistencia, "altaInstituto");

        Exception error = assertThrows(Exception.class, cargador::cargarDatosTotales);

        assertTrue(error.getMessage().contains("Error al cargar institutos"));
        assertTrue(persistencia.confirmada().get());
    }

    private static CargadorDatosPrueba crearCargador(Map<String, Integer> llamadas,
            FakePersistence persistencia, String metodoQueFalla) {
        return new CargadorDatosPrueba(
                controlador(IControladorInstituto.class, llamadas, metodoQueFalla),
                controlador(IControladorCategoria.class, llamadas, metodoQueFalla),
                controlador(IControladorUsuario.class, llamadas, metodoQueFalla),
                controlador(IControladorCurso.class, llamadas, metodoQueFalla),
                controlador(IControladorEdicion.class, llamadas, metodoQueFalla),
                controlador(IControladorProgramaFormacion.class, llamadas, metodoQueFalla),
                () -> persistencia.entityManager());
    }

    private static <T> T controlador(Class<T> tipo, Map<String, Integer> llamadas,
            String metodoQueFalla) {
        Object proxy = Proxy.newProxyInstance(tipo.getClassLoader(), new Class<?>[]{tipo}, (objeto, metodo, args) -> {
            if (metodo.getDeclaringClass() == Object.class) {
                return switch (metodo.getName()) {
                    case "toString" -> tipo.getSimpleName() + "TestDouble";
                    case "hashCode" -> System.identityHashCode(objeto);
                    case "equals" -> objeto == args[0];
                    default -> null;
                };
            }
            llamadas.merge(metodo.getName(), 1, Integer::sum);
            if (metodo.getName().equals(metodoQueFalla)) {
                throw new IllegalStateException("fallo simulado");
            }
            return valorPredeterminado(metodo.getReturnType());
        });
        return tipo.cast(proxy);
    }

    private static FakePersistence fakePersistence(String sentenciaQueFalla) {
        return fakePersistence(sentenciaQueFalla, false);
    }

    private static FakePersistence fakePersistence(String sentenciaQueFalla, boolean hayDatos) {
        List<String> sentencias = new ArrayList<>();
        List<Map<String, Object>> actualizacionesVisitas = new ArrayList<>();
        AtomicBoolean activa = new AtomicBoolean();
        AtomicBoolean confirmada = new AtomicBoolean();
        AtomicBoolean revertida = new AtomicBoolean();
        AtomicBoolean cerrada = new AtomicBoolean();

        EntityTransaction transaccion = (EntityTransaction) Proxy.newProxyInstance(
                EntityTransaction.class.getClassLoader(), new Class<?>[]{EntityTransaction.class},
                (objeto, metodo, args) -> switch (metodo.getName()) {
                    case "begin" -> {
                        activa.set(true);
                        yield null;
                    }
                    case "commit" -> {
                        activa.set(false);
                        confirmada.set(true);
                        yield null;
                    }
                    case "rollback" -> {
                        activa.set(false);
                        revertida.set(true);
                        yield null;
                    }
                    case "isActive" -> activa.get();
                    default -> valorPredeterminado(metodo.getReturnType());
                });

        EntityManager entityManager = (EntityManager) Proxy.newProxyInstance(
                EntityManager.class.getClassLoader(), new Class<?>[]{EntityManager.class},
                (objeto, metodo, args) -> {
                    if (metodo.getName().equals("getTransaction")) {
                        return transaccion;
                    }
                    if (metodo.getName().equals("createNativeQuery")) {
                        String sentencia = (String) args[0];
                        if (sentencia.startsWith("SELECT ")) {
                            return Proxy.newProxyInstance(Query.class.getClassLoader(), new Class<?>[]{Query.class},
                                    (query, metodoQuery, argsQuery) -> {
                                        if (metodoQuery.getName().equals("getSingleResult")) {
                                            return hayDatos ? 1 : 0;
                                        }
                                        return valorPredeterminado(metodoQuery.getReturnType());
                                    });
                        }
                        sentencias.add(sentencia);
                        return Proxy.newProxyInstance(Query.class.getClassLoader(), new Class<?>[]{Query.class},
                                (query, metodoQuery, argsQuery) -> {
                                    if (metodoQuery.getName().equals("executeUpdate")) {
                                        if (sentencia.equals(sentenciaQueFalla)) {
                                            throw new IllegalStateException("fallo simulado de persistencia");
                                        }
                                        return 1;
                                    }
                                    return valorPredeterminado(metodoQuery.getReturnType());
                                });
                    }
                    if (metodo.getName().equals("createQuery")) {
                        Map<String, Object> parametros = new HashMap<>();
                        return Proxy.newProxyInstance(Query.class.getClassLoader(), new Class<?>[]{Query.class},
                                (query, metodoQuery, argsQuery) -> {
                                    if (metodoQuery.getName().equals("setParameter")) {
                                        parametros.put((String) argsQuery[0], argsQuery[1]);
                                        return query;
                                    }
                                    if (metodoQuery.getName().equals("executeUpdate")) {
                                        actualizacionesVisitas.add(new HashMap<>(parametros));
                                        return 1;
                                    }
                                    return valorPredeterminado(metodoQuery.getReturnType());
                                });
                    }
                    if (metodo.getName().equals("close")) {
                        cerrada.set(true);
                    }
                    return valorPredeterminado(metodo.getReturnType());
                });

        return new FakePersistence(entityManager, sentencias, actualizacionesVisitas,
                confirmada, revertida, cerrada);
    }

    private static Object valorPredeterminado(Class<?> tipo) {
        if (tipo == boolean.class) {
            return false;
        }
        if (tipo == int.class) {
            return 0;
        }
        if (tipo == long.class) {
            return 0L;
        }
        return null;
    }

    private record FakePersistence(EntityManager entityManager, List<String> sentencias,
            List<Map<String, Object>> actualizacionesVisitas,
            AtomicBoolean confirmada, AtomicBoolean revertida, AtomicBoolean cerrada) {
    }
}
