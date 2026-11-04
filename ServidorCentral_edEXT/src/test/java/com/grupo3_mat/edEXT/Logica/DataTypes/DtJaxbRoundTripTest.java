package com.grupo3_mat.edEXT.Logica.DataTypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grupo3_mat.edEXT.Logica.Clases.EstadoInscripcion;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DtJaxbRoundTripTest {
    private static final String NAMESPACE = "urn:edext:dto";
    private static JAXBContext context;

    @BeforeAll
    static void crearContextoJaxb() throws JAXBException {
        context = JAXBContext.newInstance(
                DTCategoria.class,
                DTEdicionCurso.class,
                DTInscripcionEdicion.class,
                DTProgramaFormacion.class,
                DTResultadoEstudio.class,
                DtCurso.class,
                DtDocente.class,
                DtEstudiante.class,
                DtInstituto.class,
                DtUsuario.class,
                ValoracionesXml.class);
    }

    @Test
    void conservaLosDatosDeTodosLosDtAlSerializarYLeer() throws JAXBException {
        DTCategoria categoria = roundTrip(new DTCategoria("Backend"), DTCategoria.class);
        assertEquals("Backend", categoria.getNombre());

        DtInstituto instituto = roundTrip(new DtInstituto("Facultad"), DtInstituto.class);
        assertEquals("Facultad", instituto.getNombre());

        LocalDate fecha = LocalDate.of(2025, 3, 14);
        DtCurso cursoOriginal = new DtCurso("Java", "Curso inicial", 12, 30, 8,
                "https://example.test/curso", fecha, "Facultad",
                List.of("Programacion I"), List.of("Java 2025"), List.of("Desarrollo"),
                List.of("Backend"), "java.png");
        DtCurso curso = roundTrip(cursoOriginal, DtCurso.class);
        assertEquals("Java", curso.getNombre());
        assertEquals("Curso inicial", curso.getDescripcion());
        assertEquals(12, curso.getDuracion());
        assertEquals(30, curso.getCantHoras());
        assertEquals(8, curso.getCreditos());
        assertEquals("https://example.test/curso", curso.getUrl());
        assertEquals(fecha, curso.getFecha());
        assertEquals("Facultad", curso.getNomInstituto());
        assertEquals(List.of("Programacion I"), curso.getPrevias());
        assertEquals(List.of("Java 2025"), curso.getEdiciones());
        assertEquals(List.of("Desarrollo"), curso.getProgramas());
        assertEquals(List.of("Backend"), curso.getCategorias());
        assertEquals("java.png", curso.getImagenPath());

        DTEdicionCurso edicionOriginal = new DTEdicionCurso("Java 2025", fecha,
                fecha.plusMonths(2), 25, 17, fecha.minusDays(10),
                List.of("docente1", "docente2"), "edicion.png", true);
        DTEdicionCurso edicion = roundTrip(edicionOriginal, DTEdicionCurso.class);
        assertEquals("Java 2025", edicion.getNombre());
        assertEquals(fecha, edicion.getFechaInicio());
        assertEquals(fecha.plusMonths(2), edicion.getFechaFin());
        assertEquals(25, edicion.getCupo());
        assertEquals(17, edicion.getCupoDisponible());
        assertEquals(fecha.minusDays(10), edicion.getFechaPublicacion());
        assertEquals(List.of("docente1", "docente2"), edicion.getDocentes());
        assertEquals("edicion.png", edicion.getImagenPath());
        assertTrue(edicion.isCerrada());

        DTInscripcionEdicion inscripcionOriginal = new DTInscripcionEdicion("estudiante1",
                "Java 2025", fecha, EstadoInscripcion.ACEPTADA, 1.5,
                "https://youtu.be/dQw4w9WgXcQ", 9, true);
        DTInscripcionEdicion inscripcion = roundTrip(inscripcionOriginal, DTInscripcionEdicion.class);
        assertEquals("estudiante1", inscripcion.getNicknameEstudiante());
        assertEquals("Java 2025", inscripcion.getNombreEdicion());
        assertEquals(fecha, inscripcion.getFechaInscripcion());
        assertEquals(EstadoInscripcion.ACEPTADA, inscripcion.getEstado());
        assertEquals(1.5, inscripcion.getPrioridad());
        assertEquals("https://youtu.be/dQw4w9WgXcQ", inscripcion.getEnlaceVideo());
        assertEquals(9, inscripcion.getEvaluacion());
        assertEquals("https://www.youtube-nocookie.com/embed/dQw4w9WgXcQ",
                inscripcion.getEnlaceVideoEmbebido());
        assertTrue(inscripcion.isEdicionCerrada());

        DTResultadoEstudio resultadoOriginal = new DTResultadoEstudio(
                "Java", "Java 2025", 8, fecha);
        DTResultadoEstudio resultado = roundTrip(resultadoOriginal, DTResultadoEstudio.class);
        assertEquals("Java", resultado.getNombreCurso());
        assertEquals("Java 2025", resultado.getNombreEdicion());
        assertEquals(fecha, resultado.getFechaResultado());
        assertEquals(8, resultado.getEvaluacion());
        assertTrue(resultado.isAprobado());
        assertTrue(resultado.isTieneResultado());

        DTProgramaFormacion programaOriginal = new DTProgramaFormacion("Desarrollo",
                "Plan", fecha, fecha.plusYears(1), fecha.minusDays(2),
                List.of("Java"), List.of("Backend"), "programa.png", "docente1",
                List.of("Facultad"));
        DTProgramaFormacion programa = roundTrip(programaOriginal, DTProgramaFormacion.class);
        assertEquals("Desarrollo", programa.getNombre());
        assertEquals("Plan", programa.getDescripcion());
        assertEquals(fecha, programa.getFechaInicio());
        assertEquals(fecha.plusYears(1), programa.getFechaFin());
        assertEquals(fecha.minusDays(2), programa.getFechaAlta());
        assertEquals(List.of("Java"), programa.getCursos());
        assertEquals(List.of("Backend"), programa.getCategorias());
        assertEquals("programa.png", programa.getImagenPath());
        assertEquals("docente1", programa.getNicknameDocente());
        assertEquals(List.of("Facultad"), programa.getInstitutos());

        DtUsuario usuarioOriginal = new DtUsuario("usuario1", "Nombre", "Apellido",
                "usuario@example.test", fecha, "usuario.png",
                List.of("seguidor1"), List.of("seguido1"));
        DtUsuario usuario = roundTrip(usuarioOriginal, DtUsuario.class);
        assertEquals("usuario1", usuario.getNickname());
        assertEquals("Nombre", usuario.getNombre());
        assertEquals("Apellido", usuario.getApellido());
        assertEquals("usuario@example.test", usuario.getCorreo());
        assertEquals(fecha, usuario.getFechaNacimiento());
        assertEquals("usuario.png", usuario.getImagenPath());
        assertEquals(List.of("seguidor1"), usuario.getSeguidores());
        assertEquals(List.of("seguido1"), usuario.getSeguidos());

        DtDocente docenteOriginal = new DtDocente("docente1", "Docente", "Apellido",
                "docente@example.test", fecha, "docente.png",
                List.of("seguidor2"), List.of("seguido2"), List.of("Facultad"),
                List.of("Java 2025"), List.of("Desarrollo"));
        DtDocente docente = roundTrip(docenteOriginal, DtDocente.class);
        assertEquals("docente1", docente.getNickname());
        assertEquals("Docente", docente.getNombre());
        assertEquals("Apellido", docente.getApellido());
        assertEquals("docente@example.test", docente.getCorreo());
        assertEquals(fecha, docente.getFechaNacimiento());
        assertEquals("docente.png", docente.getImagenPath());
        assertEquals(List.of("seguidor2"), docente.getSeguidores());
        assertEquals(List.of("seguido2"), docente.getSeguidos());
        assertEquals(List.of("Facultad"), docente.getInstitutos());
        assertEquals("Facultad", docente.getInstituto());
        assertEquals(List.of("Java 2025"), docente.getEdiciones());
        assertEquals(List.of("Desarrollo"), docente.getProgramas());
        DtUsuario docenteComoUsuario = roundTripComoUsuario(docenteOriginal);
        assertInstanceOf(DtDocente.class, docenteComoUsuario);
        assertEquals("docente1", docenteComoUsuario.getNickname());
        assertEquals(List.of("Facultad"), ((DtDocente) docenteComoUsuario).getInstitutos());

        DtEstudiante estudianteOriginal = new DtEstudiante("estudiante1", "Estudiante",
                "Apellido", "estudiante@example.test", fecha, "estudiante.png",
                List.of("seguidor3"), List.of("seguido3"), List.of(inscripcionOriginal),
                List.of("Desarrollo"));
        DtEstudiante estudiante = roundTrip(estudianteOriginal, DtEstudiante.class);
        assertEquals("estudiante1", estudiante.getNickname());
        assertEquals("Estudiante", estudiante.getNombre());
        assertEquals("Apellido", estudiante.getApellido());
        assertEquals("estudiante@example.test", estudiante.getCorreo());
        assertEquals(fecha, estudiante.getFechaNacimiento());
        assertEquals("estudiante.png", estudiante.getImagenPath());
        assertEquals(List.of("seguidor3"), estudiante.getSeguidores());
        assertEquals(List.of("seguido3"), estudiante.getSeguidos());
        assertEquals(List.of("Java 2025"), estudiante.getEdicionesInscripto());
        assertEquals(List.of("Desarrollo"), estudiante.getProgramasInscripto());
        assertEquals(fecha, estudiante.getInscripcionesEdicion().get(0).getFechaInscripcion());
        assertEquals("https://youtu.be/dQw4w9WgXcQ",
                estudiante.getInscripcionesEdicion().get(0).getEnlaceVideo());
        assertEquals(EstadoInscripcion.ACEPTADA,
                estudiante.getInscripcionesEdicion().get(0).getEstado());
        DtUsuario estudianteComoUsuario = roundTripComoUsuario(estudianteOriginal);
        assertInstanceOf(DtEstudiante.class, estudianteComoUsuario);
        assertEquals("estudiante1", estudianteComoUsuario.getNickname());
        assertEquals(List.of("Desarrollo"),
                ((DtEstudiante) estudianteComoUsuario).getProgramasInscripto());

        assertNull(roundTrip(new DtUsuario("sin-fecha", "Nombre", "Apellido",
                "sin-fecha@example.test", null, null), DtUsuario.class).getFechaNacimiento());
    }

    @Test
    void conservaValoracionesEnElAdaptadorJaxb() throws JAXBException {
        ValoracionesAdapter adapter = new ValoracionesAdapter();
        Map<Integer, Integer> valoraciones = Map.of(5, 3, 10, 7);
        ValoracionesXml xml = adapter.marshal(valoraciones);
        assertEquals(valoraciones, adapter.unmarshal(roundTrip(xml, ValoracionesXml.class)));
        assertNull(adapter.marshal(null));
        assertNull(adapter.unmarshal(null));
        assertTrue(adapter.unmarshal(new ValoracionesXml()).isEmpty());
    }

    @Test
    void conservaConstructoresCompatiblesYResuelveEnlacesDeVideo() {
        LocalDate fecha = LocalDate.of(2025, 3, 14);
        DTInscripcionEdicion basica = new DTInscripcionEdicion("estudiante1", "edicion1",
                fecha, EstadoInscripcion.INSCRIPTO, 0.5);
        assertEquals("estudiante1", basica.getNicknameEstudiante());
        assertNull(basica.getEnlaceVideo());
        assertNull(basica.getEnlaceVideoEmbebido());

        DTInscripcionEdicion conEnlace = new DTInscripcionEdicion("estudiante1", "edicion1",
                fecha, EstadoInscripcion.INSCRIPTO, 0.5, "https://youtu.be/dQw4w9WgXcQ");
        assertEquals("https://www.youtube-nocookie.com/embed/dQw4w9WgXcQ",
                conEnlace.getEnlaceVideoEmbebido());

        assertEquals("https://www.youtube-nocookie.com/embed/dQw4w9WgXcQ",
                inscripcionConEnlace("https://www.youtube.com/watch?v=dQw4w9WgXcQ")
                        .getEnlaceVideoEmbebido());
        assertEquals("https://www.youtube-nocookie.com/embed/dQw4w9WgXcQ",
                inscripcionConEnlace("https://www.youtube.com/embed/dQw4w9WgXcQ")
                        .getEnlaceVideoEmbebido());
        assertEquals("https://player.vimeo.com/video/123456",
                inscripcionConEnlace("https://vimeo.com/123456").getEnlaceVideoEmbebido());
        assertNull(inscripcionConEnlace("not a url").getEnlaceVideoEmbebido());
        assertNull(inscripcionConEnlace("https://example.test/video").getEnlaceVideoEmbebido());
        assertNull(inscripcionConEnlace("https://vimeo.com/not-numeric").getEnlaceVideoEmbebido());
    }

    private <T> T roundTrip(T value, Class<T> type) throws JAXBException {
        QName name = new QName(NAMESPACE, type.getSimpleName());
        JAXBElement<T> root = new JAXBElement<>(name, type, value);
        StringWriter xml = new StringWriter();
        Marshaller marshaller = context.createMarshaller();
        marshaller.marshal(root, xml);

        Unmarshaller unmarshaller = context.createUnmarshaller();
        return unmarshaller.unmarshal(new StreamSource(new StringReader(xml.toString())), type).getValue();
    }

    private DtUsuario roundTripComoUsuario(DtUsuario value) throws JAXBException {
        JAXBElement<DtUsuario> root = new JAXBElement<>(
                new QName(NAMESPACE, "usuario"), DtUsuario.class, value);
        StringWriter xml = new StringWriter();
        context.createMarshaller().marshal(root, xml);
        return context.createUnmarshaller()
                .unmarshal(new StreamSource(new StringReader(xml.toString())), DtUsuario.class)
                .getValue();
    }

    private DTInscripcionEdicion inscripcionConEnlace(String enlace) {
        return new DTInscripcionEdicion("estudiante1", "edicion1",
                LocalDate.of(2025, 3, 14), EstadoInscripcion.INSCRIPTO, 0.5,
                enlace);
    }
}
