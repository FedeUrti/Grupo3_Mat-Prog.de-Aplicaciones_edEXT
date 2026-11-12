package com.grupo3_mat.edEXT.Logica.Interfaces;

import com.grupo3_mat.edEXT.Logica.DataTypes.DtCurso;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import com.grupo3_mat.edEXT.Logica.DataTypes.LocalDateAdapter;
import com.grupo3_mat.edEXT.Logica.DataTypes.ValoracionesAdapter;

@WebService(name = "CursoSoap", targetNamespace = "http://soap.edext.grupo3_mat.com/")
public interface IControladorCurso {
    void altaCurso(@WebParam(name = "nombreInstituto") String nomInst,
                   @WebParam(name = "nombreCurso") String cursoNom,
                   @WebParam(name = "descripcion") String desc,
                   @WebParam(name = "duracion") int dur,
                   @WebParam(name = "cantidadHoras") int cantHoras,
                   @WebParam(name = "creditos") int creditos,
                   @WebParam(name = "url") String url,
                   @WebParam(name = "fecha") @XmlJavaTypeAdapter(LocalDateAdapter.class) LocalDate fecha,
                   @WebParam(name = "previas") List<String> previas,
                   @WebParam(name = "categorias") List<String> categorias,
                   @WebParam(name = "imagenPath") String imagenPath) throws Exception;
    
    List<String> listarCursosPorInstituto(@WebParam(name = "nombreInstituto") String nomInst);
    List<String> listarCursosPorCategoria(@WebParam(name = "nombreCategoria") String nomCategoria);
    DtCurso consultarCurso(@WebParam(name = "nombreCurso") String nombreCurso) throws Exception;
    List<DtCurso> consultarCursos(@WebParam(name = "nombresCurso") List<String> nombresCurso) throws Exception;
    List<String> listarCursos();
    List<String> listarCursosPorVisitas();
    void registrarVisitaCurso(@WebParam(name = "nombreCurso") String nombreCurso);
    String obtenerEdicionVigente(@WebParam(name = "nombreCurso") String nombreCurso,
            @WebParam(name = "fechaReferencia") @XmlJavaTypeAdapter(LocalDateAdapter.class)
            LocalDate fechaReferencia) throws Exception;
    void ingresarValoracion(@WebParam(name = "nombreCurso") String nombreCurso,
            @WebParam(name = "nicknameEstudiante") String nicknameEstudiante,
            @WebParam(name = "puntaje") int puntaje) throws Exception;
    int consultarValoracion(@WebParam(name = "nombreCurso") String nombreCurso,
            @WebParam(name = "nicknameEstudiante") String nicknameEstudiante) throws Exception;
    List<String> listarCursosValorables(@WebParam(name = "nicknameEstudiante") String nicknameEstudiante);
    BigDecimal obtenerPuntajePromedioCurso(@WebParam(name = "nombreCurso") String nombreCurso) throws Exception;
    @XmlJavaTypeAdapter(ValoracionesAdapter.class)
    Map<Integer, Integer> obtenerConteoValoracionesCurso(@WebParam(name = "nombreCurso") String nombreCurso)
            throws Exception;
    Integer obtenerValoracionEstudiante(@WebParam(name = "nombreCurso") String nombreCurso,
            @WebParam(name = "nicknameEstudiante") String nicknameEstudiante);
}