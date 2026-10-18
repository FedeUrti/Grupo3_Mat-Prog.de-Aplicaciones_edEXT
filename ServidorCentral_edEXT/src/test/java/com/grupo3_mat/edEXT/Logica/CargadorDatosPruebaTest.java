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

        assertEquals(16, persistencia.sentencias().size());
        assertEquals("DELETE FROM Usuario_Seguimiento", persistencia.sentencias().get(0));
        assertEquals("DELETE FROM Instituto", persistencia.sentencias().get(15));
        assertTrue(persistencia.confirmada().get());
        assertFalse(persistencia.revertida().get());
        assertTrue(persistencia.cerrada().get());
        assertTrue(llamadas.getOrDefault("altaInstituto", 0) > 0);
        assertTrue(llamadas.getOrDefault("altaCategoria", 0) > 0);
        assertTrue(llamadas.getOrDefault("altaUsuario", 0) > 0);
        assertTrue(llamadas.getOrDefault("seguirUsuario", 0) > 0);
        assertTrue(llamadas.getOrDefault("altaCurso", 0) > 0);
        assertTrue(llamadas.getOrDefault("altaEdicionCurso", 0) > 0);
        assertTrue(llamadas.getOrDefault("inscribirEstudianteAEdicion", 0) > 0);
        assertTrue(llamadas.getOrDefault("crearProgramaFormacion", 0) > 0);
        assertTrue(llamadas.getOrDefault("inscribirEstudianteAPrograma", 0) > 0);
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
        List<String> sentencias = new ArrayList<>();
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
                    if (metodo.getName().equals("close")) {
                        cerrada.set(true);
                    }
                    return valorPredeterminado(metodo.getReturnType());
                });

        return new FakePersistence(entityManager, sentencias, confirmada, revertida, cerrada);
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
            AtomicBoolean confirmada, AtomicBoolean revertida, AtomicBoolean cerrada) {
    }
}
