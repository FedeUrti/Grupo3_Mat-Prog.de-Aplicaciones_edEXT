package com.grupo3_mat.edEXT.Logica.Interfaces;

import com.grupo3_mat.edEXT.Logica.DataTypes.DTEdicionCurso;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTInscripcionEdicion;
import com.grupo3_mat.edEXT.Logica.DataTypes.CriterioOrdenInscripciones;
import com.grupo3_mat.edEXT.Logica.Clases.EstadoInscripcion;
import java.time.LocalDate;
import java.util.List;
import jakarta.jws.WebParam;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import com.grupo3_mat.edEXT.Logica.DataTypes.LocalDateAdapter;
import jakarta.xml.ws.RequestWrapper;
import jakarta.xml.ws.ResponseWrapper;

@WebService(name = "EdicionSoap", targetNamespace = "http://soap.edext.grupo3_mat.com/")
public interface IControladorEdicion {
    void altaEdicionCurso(@WebParam(name = "nombreCurso") String nombreCurso,
            @WebParam(name = "datosEdicion") DTEdicionCurso datosEdicion) throws Exception;
    List<String> listarEdicionesPorCurso(@WebParam(name = "nombreCurso") String nombreCurso);
    DTEdicionCurso mostrarDetalleEdicion(@WebParam(name = "nombreEdicion") String nombreEdicion);
    @WebMethod(operationName = "inscribirEstudianteAEdicion")
    @RequestWrapper(localName = "inscribirEstudianteAEdicion",
            targetNamespace = "http://soap.edext.grupo3_mat.com/",
            className = "com.grupo3_mat.edEXT.Logica.Interfaces.jaxws.InscribirEstudianteAEdicion")
    @ResponseWrapper(localName = "inscribirEstudianteAEdicionResponse",
            targetNamespace = "http://soap.edext.grupo3_mat.com/",
            className = "com.grupo3_mat.edEXT.Logica.Interfaces.jaxws.InscribirEstudianteAEdicionResponse")
    void inscribirEstudianteAEdicion(@WebParam(name = "nicknameEstudiante") String nicknameEstudiante,
            @WebParam(name = "nombreEdicion") String nombreEdicion,
            @WebParam(name = "fechaInscripcion") @XmlJavaTypeAdapter(LocalDateAdapter.class)
            LocalDate fechaInscripcion) throws Exception;
    @WebMethod(operationName = "inscribirEstudianteAEdicionConVideo")
    @RequestWrapper(localName = "inscribirEstudianteAEdicionConVideo",
            targetNamespace = "http://soap.edext.grupo3_mat.com/",
            className = "com.grupo3_mat.edEXT.Logica.Interfaces.jaxws.InscribirEstudianteAEdicionConVideo")
    @ResponseWrapper(localName = "inscribirEstudianteAEdicionConVideoResponse",
            targetNamespace = "http://soap.edext.grupo3_mat.com/",
            className = "com.grupo3_mat.edEXT.Logica.Interfaces.jaxws.InscribirEstudianteAEdicionConVideoResponse")
    void inscribirEstudianteAEdicion(@WebParam(name = "nicknameEstudiante") String nicknameEstudiante,
            @WebParam(name = "nombreEdicion") String nombreEdicion,
            @WebParam(name = "fechaInscripcion") @XmlJavaTypeAdapter(LocalDateAdapter.class)
            LocalDate fechaInscripcion, @WebParam(name = "enlaceVideo") String enlaceVideo) throws Exception;
    String obtenerCursoDeEdicion(@WebParam(name = "nombreEdicion") String nombreEdicion);
    List<String> listarEdicionesDeDocente(@WebParam(name = "nicknameDocente") String nicknameDocente);
    List<String> listarEdicionesDeEstudiante(@WebParam(name = "nicknameEstudiante") String nicknameEstudiante);
    List<String> listarCursosDeEdiciones(@WebParam(name = "nombresEdiciones") List<String> nombresEdiciones);
    
    // Métodos faltantes para selección de estudiantes
    List<DTInscripcionEdicion> listarInscripcionesAEdicion(
            @WebParam(name = "nicknameDocente") String nicknameDocente,
            @WebParam(name = "nombreEdicion") String nombreEdicion,
            @WebParam(name = "criterio") CriterioOrdenInscripciones criterio) throws Exception;
    List<DTInscripcionEdicion> consultarEvaluacionesEdicion(
            @WebParam(name = "nicknameDocente") String nicknameDocente,
            @WebParam(name = "nombreEdicion") String nombreEdicion) throws Exception;
    List<DTInscripcionEdicion> listarAceptadosAEdicion(
            @WebParam(name = "nicknameDocente") String nicknameDocente,
            @WebParam(name = "nombreEdicion") String nombreEdicion) throws Exception;
    List<DTInscripcionEdicion> listarResultadosInscripcionesEstudiante(
            @WebParam(name = "nicknameEstudiante") String nicknameEstudiante);
    void cambiarEstadoInscripcion(@WebParam(name = "nicknameDocente") String nicknameDocente,
            @WebParam(name = "nombreEdicion") String nombreEdicion,
            @WebParam(name = "nicknameEstudiante") String nicknameEstudiante,
            @WebParam(name = "nuevoEstado") EstadoInscripcion nuevoEstado) throws Exception;
    void evaluarInscripcion(@WebParam(name = "nicknameDocente") String nicknameDocente,
            @WebParam(name = "nombreEdicion") String nombreEdicion,
            @WebParam(name = "nicknameEstudiante") String nicknameEstudiante,
            @WebParam(name = "evaluacion") int evaluacion) throws Exception;
    void cerrarEdicion(@WebParam(name = "nicknameDocente") String nicknameDocente,
            @WebParam(name = "nombreEdicion") String nombreEdicion) throws Exception;
    void desistirInscripcionEstudiante(@WebParam(name = "nicknameEstudiante") String nicknameEstudiante,
            @WebParam(name = "nombreEdicion") String nombreEdicion) throws Exception;
}