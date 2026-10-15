<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Consulta de programa | edEXT</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <c:url var="estilosUrl" value="/assets/css/edext.css">
        <c:param name="v" value="20261012-7"/>
    </c:url>
    <link href="<c:out value='${estilosUrl}'/>" rel="stylesheet">
</head>
<body>
    <%@ include file="/WEB-INF/views/buscador-global.jspf" %>
    <c:url var="programasUrl" value="/programas"/>

    <main class="container-fluid page-layout py-4 px-3 px-lg-4">
        <div class="row g-4 align-items-start">
            <%@ include file="/WEB-INF/views/sidebar-principal.jspf" %>
            <div class="col main-content-column">
                <a href="<c:out value='${programasUrl}'/>" class="text-decoration-none text-muted">
                    <i class="bi bi-arrow-left me-1" aria-hidden="true"></i> Volver a programas
                </a>
                <div class="my-4">
                    <p class="text-uppercase fw-bold small text-muted mb-1">Oferta académica</p>
                    <h1 class="page-heading fw-bold mb-0">Consulta de programa</h1>
                </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger mb-4" role="alert">
                <c:out value="${error}"/>
            </div>
        </c:if>
        <c:if test="${not empty param.error and empty error}">
            <div class="alert alert-danger mb-4" role="alert">
                <c:out value="${param.error}"/>
            </div>
        </c:if>

        <!-- 1. ALERTAS DE ÉXITO/ERROR PARA DOCENTES (Agregar Curso) -->
        <c:if test="${param.agregado eq '1'}">
            <div class="alert alert-success alert-dismissible fade show mb-4" role="alert">
                El curso se agregó correctamente al programa de formación.
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <!-- 2. ALERTAS DE ÉXITO/ERROR PARA ESTUDIANTES (Inscripción) -->
        <c:if test="${param.inscripto eq '1'}">
            <div class="alert alert-success alert-dismissible fade show mb-4" role="alert">
                Te inscribiste correctamente a este programa de formación.
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <c:if test="${not empty param.errorInscripcion}">
            <div class="alert alert-warning alert-dismissible fade show mb-4" role="alert">
                <strong>Aviso:</strong> <c:out value="${param.errorInscripcion}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <c:if test="${not empty programa}">
            <!-- Ficha Principal del Programa -->
            <div class="content-card p-3 p-md-4 mb-4">
                    <div class="row align-items-start g-4">
                        
                        <!-- Imagen del Programa -->
                        <c:if test="${not empty programa.imagenPath}">
                            <div class="col-md-5 text-center">
                                <c:url var="imagenProgramaUrl" value="/imagenes-curso">
                                    <c:param name="nombre" value="${programa.imagenPath}"/>
                                </c:url>
                                <img src="<c:out value='${imagenProgramaUrl}'/>"
                                     alt="Imagen de <c:out value='${programa.nombre}'/>"
                                     class="img-fluid rounded shadow-sm w-100"
                                     style="max-height: 250px; object-fit: cover;">
                            </div>
                        </c:if>

                        <!-- Datos del Programa -->
                        <div class="${not empty programa.imagenPath ? 'col-md-7' : 'col-12'}">
                            <h2 class="page-heading fw-bold mb-3"><c:out value="${programa.nombre}"/></h2>
                            <p class="lead text-secondary"><c:out value="${programa.descripcion}"/></p>
                            
                            <hr class="my-3">
                            
                            <div class="row mb-3">
                                <div class="col-sm-6">
                                    <strong>Fecha de Inicio:</strong> 
                                    <span class="text-dark"><c:out value="${programa.fechaInicio}"/></span>
                                </div>
                                <div class="col-sm-6">
                                    <strong>Fecha de Fin:</strong> 
                                    <span class="text-dark"><c:out value="${programa.fechaFin}"/></span>
                                </div>
                            </div>

                            <!-- Categorías ordenadas fijas -->
                            <div>
                                <h6 class="fw-bold mb-2">Categorías asociadas:</h6>
                                <c:choose>
                                    <c:when test="${not empty categoriasOrdenadas}">
                                        <div class="course-detail-tags">
                                            <c:forEach var="cat" items="${categoriasOrdenadas}">
                                                <c:url var="categoriaUrl" value="/inicio">
                                                    <c:param name="categoria" value="${cat}"/>
                                                </c:url>
                                                <a class="badge rounded-pill text-bg-primary text-decoration-none"
                                                   href="<c:out value='${categoriaUrl}'/>"><c:out value="${cat}"/></a>
                                            </c:forEach>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-muted small">Sin categorías asociadas.</span>
                                    </c:otherwise>
                                </c:choose>
                                <c:if test="${not empty programa.institutos}">
                                    <h6 class="fw-bold mt-3 mb-2">Institutos:</h6>
                                    <div class="course-detail-tags">
                                        <c:forEach var="instituto" items="${programa.institutos}">
                                            <c:url var="institutoUrl" value="/inicio">
                                                <c:param name="instituto" value="${instituto}"/>
                                            </c:url>
                                            <a class="badge rounded-pill text-bg-light border text-decoration-none"
                                               href="<c:out value='${institutoUrl}'/>"><c:out value="${instituto}"/></a>
                                        </c:forEach>
                                    </div>
                                </c:if>
                                <div class="d-flex flex-wrap align-items-center gap-2 mt-4">
                                    <c:if test="${not empty sessionScope.usuarioNickname and sessionScope.usuarioDocente eq false}">
                                        <c:choose>
                                            <c:when test="${estudianteInscripto}">
                                                <span class="text-success small">
                                                    <i class="bi bi-check-circle me-1" aria-hidden="true"></i>
                                                    Ya estás inscripto a este programa.
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <c:url var="inscribirProgramaUrl" value="/inscribir-programa"/>
                                                <form action="<c:out value='${inscribirProgramaUrl}'/>" method="post" class="m-0">
                                                    <input type="hidden" name="nombrePrograma" value="<c:out value='${programa.nombre}'/>">
                                                    <button type="submit" class="btn btn-primary fw-bold">
                                                        <i class="bi bi-check-circle me-1" aria-hidden="true"></i>
                                                        Inscribirme a este programa
                                                    </button>
                                                </form>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:if>
                                    <c:if test="${sessionScope.usuarioDocente eq true and sessionScope.usuarioNickname eq programa.nicknameDocente}">
                                        <button type="button" class="btn btn-outline-success fw-bold"
                                                data-bs-toggle="modal" data-bs-target="#modalAgregarCurso">
                                            <i class="bi bi-plus-circle me-1" aria-hidden="true"></i>
                                            Agregar curso
                                        </button>
                                    </c:if>
                                </div>
                        </div>

                    </div>
                </div>
            </div>

            <!-- Cursos que integran el programa -->
            <h3 class="mb-4 text-dark fw-bold">Cursos que integran este programa</h3>
            <div class="row">
                <c:choose>
                    <c:when test="${not empty cursosDetalle}">
                        <c:forEach var="curso" items="${cursosDetalle}">
                            <div class="col-md-6 col-xl-4 mb-4">
                                <article class="content-card h-100 d-flex flex-column overflow-hidden">
                                    <c:choose>
                                        <c:when test="${not empty curso.imagenPath}">
                                            <c:url var="imagenCursoUrl" value="/imagenes-curso">
                                                <c:param name="nombre" value="${curso.imagenPath}"/>
                                            </c:url>
                                            <img src="<c:out value='${imagenCursoUrl}'/>" class="w-100"
                                                 alt="Imagen de <c:out value='${curso.nombre}'/>"
                                                 style="height: 180px; object-fit: cover;">
                                        </c:when>
                                        <c:otherwise>
                                            <div class="bg-light text-secondary d-flex align-items-center justify-content-center"
                                                 style="height: 180px;" aria-hidden="true">
                                                <i class="bi bi-book display-4"></i>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                    <div class="p-3 d-flex flex-column flex-grow-1">
                                        <h3 class="h5 fw-bold"><c:out value="${curso.nombre}"/></h3>
                                        <p class="text-muted small flex-grow-1"><c:out value="${curso.descripcion}"/></p>
                                        
                                        <ul class="list-unstyled small mb-3">
                                            <li><strong>Instituto:</strong> <c:out value="${curso.nomInstituto}"/></li>
                                            <li><strong>Duración:</strong> <c:out value="${curso.duracion}"/> semanas</li>
                                            <li><strong>Créditos:</strong> <c:out value="${curso.creditos}"/></li>
                                        </ul>

                                        <c:url var="detalleCursoUrl" value="/consulta-curso">
                                            <c:param name="nombre" value="${curso.nombre}"/>
                                        </c:url>
                                        <a href="<c:out value='${detalleCursoUrl}'/>"
                                           class="btn btn-outline-primary btn-sm w-100">
                                            Ver Detalle del Curso
                                        </a>
                                    </div>
                                </article>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="col-12">
                            <div class="alert alert-warning" role="alert">
                                Este programa aún no tiene cursos asignados.
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Modal de gestión; el botón de acceso está junto a la información del programa. -->
            <c:if test="${sessionScope.usuarioDocente eq true and sessionScope.usuarioNickname eq programa.nicknameDocente}">
                <div class="modal fade" id="modalAgregarCurso" tabindex="-1" aria-labelledby="modalAgregarCursoLabel" aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content border-0 shadow">
                            
                            <div class="modal-header bg-success text-white">
                                <h5 class="modal-title fw-bold" id="modalAgregarCursoLabel">Agregar Curso al Programa</h5>
                                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>

                            <c:url var="agregarCursoProgramaUrl" value="/agregar-curso-programa"/>
                            <form action="<c:out value='${agregarCursoProgramaUrl}'/>" method="post">
                                <div class="modal-body p-4">
                                    
                                    <div class="mb-3">
                                        <label class="form-label fw-bold">Programa de Formación</label>
                                        <input type="text" class="form-control bg-light" value="<c:out value='${programa.nombre}'/>" readonly>
                                        <input type="hidden" name="nombrePrograma" value="<c:out value='${programa.nombre}'/>">
                                    </div>

                                    <div class="mb-3">
                                        <label for="nombreCurso" class="form-label fw-bold">Seleccioná un Curso</label>
                                        <select class="form-select" id="nombreCurso" name="nombreCurso" required>
                                            <option value="">-- Seleccionar curso --</option>
                                            <c:forEach var="c" items="${todosLosCursos}">
                                                <option value="<c:out value='${c}'/>"><c:out value="${c}"/></option>
                                            </c:forEach>
                                        </select>
                                        <div class="form-text">Solo se muestran cursos de tus institutos.</div>
                                    </div>

                                </div>

                                <div class="modal-footer bg-light">
                                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancelar</button>
                                    <button type="submit" class="btn btn-success fw-bold">Agregar al Programa</button>
                                </div>
                            </form>

                        </div>
                    </div>
                </div>
            </c:if>

            <!-- SECCIÓN VISITANTE: Invitación al Login -->
            <c:if test="${empty sessionScope.usuarioNickname}">
                <div class="mt-4 p-3 bg-white border rounded text-center text-muted small shadow-sm">
                    ¿Sos estudiante y querés cursar este programa? 
                    <c:url var="loginUrl" value="/login"/>
                    <a href="<c:out value='${loginUrl}'/>" class="fw-bold text-decoration-underline ms-1">Iniciá sesión</a> para inscribirte.
                </div>
            </c:if>

        </c:if>

            </div>
        </div>
    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>