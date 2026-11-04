package com.grupo3_mat.edEXT.Logica.Interfaces;

import com.grupo3_mat.edEXT.Logica.DataTypes.DtCurso;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTProgramaFormacion;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTResultadoEstudio;
import java.time.LocalDate;
import java.util.List;
import jakarta.jws.WebParam;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import jakarta.xml.ws.RequestWrapper;
import jakarta.xml.ws.ResponseWrapper;
import com.grupo3_mat.edEXT.Logica.DataTypes.LocalDateAdapter;

@WebService(name = "ProgramaFormacionSoap", targetNamespace = "http://soap.edext.grupo3_mat.com/")
public interface IControladorProgramaFormacion {
    /** Crea un programa asociado al docente que será responsable de gestionarlo. */
    @WebMethod(operationName = "crearProgramaFormacionConDocente")
    @RequestWrapper(localName = "crearProgramaFormacionConDocente",
            targetNamespace = "http://soap.edext.grupo3_mat.com/",
            className = "com.grupo3_mat.edEXT.Logica.Interfaces.jaxws.CrearProgramaFormacionConDocente")
    @ResponseWrapper(localName = "crearProgramaFormacionConDocenteResponse",
            targetNamespace = "http://soap.edext.grupo3_mat.com/",
            className = "com.grupo3_mat.edEXT.Logica.Interfaces.jaxws.CrearProgramaFormacionConDocenteResponse")
    void crearProgramaFormacion(@WebParam(name = "nicknameDocente") String nicknameDocente,
            @WebParam(name = "nombre") String nombre, @WebParam(name = "descripcion") String descripcion,
            @WebParam(name = "fechaInicio") @XmlJavaTypeAdapter(LocalDateAdapter.class) LocalDate fechaInicio,
            @WebParam(name = "fechaFin") @XmlJavaTypeAdapter(LocalDateAdapter.class) LocalDate fechaFin,
            @WebParam(name = "fechaAlta") @XmlJavaTypeAdapter(LocalDateAdapter.class) LocalDate fechaAlta,
            @WebParam(name = "imagenPath") String imagenPath) throws Exception;
    /** Firma antigua, conservada para que las llamadas viejas reciban un error claro. */
    @Deprecated
    @WebMethod(operationName = "crearProgramaFormacionLegacy")
    @RequestWrapper(localName = "crearProgramaFormacionLegacy",
            targetNamespace = "http://soap.edext.grupo3_mat.com/",
            className = "com.grupo3_mat.edEXT.Logica.Interfaces.jaxws.CrearProgramaFormacionLegacy")
    @ResponseWrapper(localName = "crearProgramaFormacionLegacyResponse",
            targetNamespace = "http://soap.edext.grupo3_mat.com/",
            className = "com.grupo3_mat.edEXT.Logica.Interfaces.jaxws.CrearProgramaFormacionLegacyResponse")
    void crearProgramaFormacion(@WebParam(name = "nombre") String nombre,
            @WebParam(name = "descripcion") String descripcion,
            @WebParam(name = "fechaInicio") @XmlJavaTypeAdapter(LocalDateAdapter.class) LocalDate fechaInicio,
            @WebParam(name = "fechaFin") @XmlJavaTypeAdapter(LocalDateAdapter.class) LocalDate fechaFin,
            @WebParam(name = "fechaAlta") @XmlJavaTypeAdapter(LocalDateAdapter.class) LocalDate fechaAlta,
            @WebParam(name = "imagenPath") String imagenPath) throws Exception;
    @WebMethod(operationName = "agregarCursoAPrograma")
    @RequestWrapper(localName = "agregarCursoAPrograma",
            targetNamespace = "http://soap.edext.grupo3_mat.com/",
            className = "com.grupo3_mat.edEXT.Logica.Interfaces.jaxws.AgregarCursoAPrograma")
    @ResponseWrapper(localName = "agregarCursoAProgramaResponse",
            targetNamespace = "http://soap.edext.grupo3_mat.com/",
            className = "com.grupo3_mat.edEXT.Logica.Interfaces.jaxws.AgregarCursoAProgramaResponse")
    void agregarCursoAPrograma(@WebParam(name = "nombrePrograma") String nombrePrograma,
            @WebParam(name = "nombreCurso") String nombreCurso) throws Exception;
    @WebMethod(operationName = "agregarCursoAProgramaComoDocente")
    @RequestWrapper(localName = "agregarCursoAProgramaComoDocente",
            targetNamespace = "http://soap.edext.grupo3_mat.com/",
            className = "com.grupo3_mat.edEXT.Logica.Interfaces.jaxws.AgregarCursoAProgramaComoDocente")
    @ResponseWrapper(localName = "agregarCursoAProgramaComoDocenteResponse",
            targetNamespace = "http://soap.edext.grupo3_mat.com/",
            className = "com.grupo3_mat.edEXT.Logica.Interfaces.jaxws.AgregarCursoAProgramaComoDocenteResponse")
    void agregarCursoAPrograma(@WebParam(name = "nicknameDocente") String nicknameDocente,
            @WebParam(name = "nombrePrograma") String nombrePrograma,
            @WebParam(name = "nombreCurso") String nombreCurso) throws Exception;
    List<DTProgramaFormacion> listarProgramas() throws Exception;
    DTProgramaFormacion seleccionarPrograma(@WebParam(name = "nombre") String nombre) throws Exception;
    DtCurso seleccionarCurso(@WebParam(name = "nombreCurso") String nombreCurso) throws Exception;
    DTProgramaFormacion consultarPrograma(@WebParam(name = "nombre") String nombre) throws Exception;
    void inscribirEstudianteAPrograma(@WebParam(name = "nickname") String nickname,
            @WebParam(name = "nombrePrograma") String nombrePrograma,
            @WebParam(name = "fechaInscripcion") @XmlJavaTypeAdapter(LocalDateAdapter.class)
            LocalDate fechaInscripcion) throws Exception;
    List<DTResultadoEstudio> consultarResultadosActa(
            @WebParam(name = "nicknameEstudiante") String nicknameEstudiante,
            @WebParam(name = "nombrePrograma") String nombrePrograma) throws Exception;
}