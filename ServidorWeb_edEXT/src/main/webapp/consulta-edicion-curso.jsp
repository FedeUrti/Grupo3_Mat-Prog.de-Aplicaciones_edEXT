<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Consulta de Edición de Curso | edEXT</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <c:url var="estilosUrl" value="/assets/css/edext.css">
        <c:param name="v" value="20261012-6"/>
    </c:url>
    <link href="<c:out value='${estilosUrl}'/>" rel="stylesheet">
</head>
<body>
<%@ include file="/WEB-INF/views/buscador-global.jspf" %>
<main class="container-fluid page-layout py-4 px-3 px-lg-4">
    <div class="row g-4 align-items-start">
        <%@ include file="/WEB-INF/views/sidebar-principal.jspf" %>
        <div class="col main-content-column">
    <!-- Encabezado de la página -->
    <div class="d-flex flex-wrap align-items-end justify-content-between gap-3 mb-4">
        <div>
            <a href="inicio" class="text-decoration-none text-muted">
                <i class="bi bi-arrow-left me-1" aria-hidden="true"></i> Volver al inicio
            </a>
            <p class="text-uppercase fw-bold small text-muted mb-1">Gestión académica</p>
            <h1 class="page-heading fw-bold mb-1">Consulta de Edición de Curso</h1>
            <p class="text-muted mb-0">Seleccioná un instituto o categoría para explorar sus cursos y sus ediciones correspondientes.</p>
        </div>
    </div>

    <!-- Errores de servidor -->
    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show mb-4" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2" aria-hidden="true"></i>
            <c:out value="${error}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar"></button>
        </div>
    </c:if>

    <div class="row g-4">
        <!-- Panel Izquierdo: Selección / Filtros -->
        <aside class="col-lg-4">
            <div class="content-card p-4">
                <h2 class="h6 text-uppercase fw-bold text-muted mb-3">1. Seleccionar Filtro</h2>

                <form action="consulta-edicion-curso" method="GET" class="mb-4">
                    <c:if test="${not empty filtroCategoria}">
                        <input type="hidden" name="categoria" value="<c:out value='${filtroCategoria}'/>">
                    </c:if>
                    <div class="mb-3">
                        <label for="instituto" class="form-label fw-semibold">Por Instituto</label>
                        <select class="form-select" id="instituto" name="instituto" onchange="this.form.submit()">
                            <option value="">-- Seleccionar Instituto --</option>
                            <c:forEach items="${institutos}" var="inst">
                                <option value="<c:out value='${inst}'/>" <c:if test="${inst eq filtroInstituto}">selected</c:if>>
                                    <c:out value="${inst}"/>
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </form>

                <form action="consulta-edicion-curso" method="GET" class="mb-4">
                    <c:if test="${not empty filtroInstituto}">
                        <input type="hidden" name="instituto" value="<c:out value='${filtroInstituto}'/>">
                    </c:if>
                    <div class="mb-3">
                        <label for="categoria" class="form-label fw-semibold">Por Categoría</label>
                        <select class="form-select" id="categoria" name="categoria" onchange="this.form.submit()">
                            <option value="">-- Seleccionar Categoría --</option>
                            <c:forEach items="${categorias}" var="cat">
                                <option value="<c:out value='${cat}'/>" <c:if test="${cat eq filtroCategoria}">selected</c:if>>
                                    <c:out value="${cat}"/>
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </form>

                <c:if test="${(not empty filtroInstituto or not empty filtroCategoria) and empty cursos}">
                    <div class="alert alert-info mb-0" role="status">
                        No hay cursos disponibles para los filtros seleccionados.
                    </div>
                </c:if>

                <!-- Selección de Curso (Paso 2) -->
                <c:if test="${not empty cursos}">
                    <hr class="my-4">
                    <h2 class="h6 text-uppercase fw-bold text-muted mb-3">2. Cursos Disponibles</h2>
                    <form action="consulta-edicion-curso" method="GET">
                        <c:if test="${not empty filtroInstituto}">
                            <input type="hidden" name="instituto" value="<c:out value='${filtroInstituto}'/>">
                        </c:if>
                        <c:if test="${not empty filtroCategoria}">
                            <input type="hidden" name="categoria" value="<c:out value='${filtroCategoria}'/>">
                        </c:if>

                        <div class="mb-3">
                            <label for="curso" class="form-label fw-semibold">Seleccionar Curso</label>
                            <select class="form-select" id="curso" name="curso" onchange="this.form.submit()">
                                <option value="">-- Seleccionar Curso --</option>
                                <c:forEach items="${cursos}" var="c">
                                    <option value="<c:out value='${c}'/>" <c:if test="${c eq cursoSeleccionado}">selected</c:if>>
                                        <c:out value="${c}"/>
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </form>
                </c:if>

                <!-- Selección de Edición (Paso 3) -->
                <c:if test="${not empty ediciones}">
                    <hr class="my-4">
                    <h2 class="h6 text-uppercase fw-bold text-muted mb-3">3. Ediciones del Curso</h2>
                    <div class="list-group">
                        <c:forEach items="${ediciones}" var="ed">
                            <c:url var="edicionUrl" value="/consulta-edicion-curso">
                                <c:if test="${not empty filtroInstituto}"><c:param name="instituto" value="${filtroInstituto}"/></c:if>
                                <c:if test="${not empty filtroCategoria}"><c:param name="categoria" value="${filtroCategoria}"/></c:if>
                                <c:param name="curso" value="${cursoSeleccionado}"/>
                                <c:param name="edicion" value="${ed}"/>
                            </c:url>
                            <a href="<c:out value='${edicionUrl}'/>"
                               class="list-group-item list-group-item-action d-flex align-items-center justify-content-between <c:if test='${ed eq edicionSeleccionada}'>active</c:if>">
                                <span><c:out value="${ed}"/></span>
                                <i class="bi bi-chevron-right small" aria-hidden="true"></i>
                            </a>
                        </c:forEach>
                    </div>
                </c:if>
            </div>
        </aside>

        <!-- Panel Derecho: Detalle de la Edición Seleccionada -->
        <section class="col-lg-8" aria-label="Detalle de la edición">
            <c:choose>
                <c:when test="${not empty edicion}">
                    <article class="content-card p-4 p-md-5">
                        <div class="row align-items-start g-4">
                            <!-- Imagen Representativa -->
                            <div class="col-md-5 text-center">
                                <c:choose>
                                    <c:when test="${not empty edicion.imagenPath}">
                                        <c:url var="imagenEdicionUrl" value="/imagenes-curso">
                                            <c:param name="nombre" value="${edicion.imagenPath}"/>
                                        </c:url>
                                        <img src="<c:out value='${imagenEdicionUrl}'/>"
                                             class="img-fluid rounded shadow-sm w-100"
                                             alt="Imagen de <c:out value='${edicion.nombre}'/>"
                                             style="max-height: 250px; object-fit: cover;">
                                    </c:when>
                                    <c:otherwise>
                                        <div class="course-image-placeholder rounded p-4 text-center bg-light border">
                                            <i class="bi bi-journal-bookmark display-3 text-muted" aria-hidden="true"></i>
                                            <p class="text-muted small mb-0 mt-2">Sin imagen asociada</p>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <!-- Información General -->
                            <div class="col-md-7">
                                <span class="badge text-bg-primary mb-2"><c:out value="${cursoSeleccionado}"/></span>
                                <h2 class="h3 fw-bold mb-3"><c:out value="${edicion.nombre}"/></h2>

                                <dl class="row mb-0">
                                    <dt class="col-sm-5 text-muted fw-semibold">Fecha de Inicio:</dt>
                                    <dd class="col-sm-7"><c:out value="${edicion.fechaInicio}"/></dd>

                                    <dt class="col-sm-5 text-muted fw-semibold">Fecha de Finalización:</dt>
                                    <dd class="col-sm-7"><c:out value="${edicion.fechaFin}"/></dd>

                                    <dt class="col-sm-5 text-muted fw-semibold">Publicación:</dt>
                                    <dd class="col-sm-7"><c:out value="${edicion.fechaPublicacion}"/></dd>

                                    <dt class="col-sm-5 text-muted fw-semibold">Cupos Disponibles:</dt>
                                    <dd class="col-sm-7">
                                        <c:choose>
                                            <c:when test="${edicion.cupo > 0}">
                                                <span class="badge ${edicion.cupoDisponible > 0 ? 'bg-success' : 'bg-danger'}">
                                                    <c:out value="${edicion.cupoDisponible}"/> de <c:out value="${edicion.cupo}"/>
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-success fw-semibold">Ilimitado</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </dd>
                                </dl>
                            </div>
                        </div>

                        <!-- Docentes Asignados -->
                        <div class="border-top pt-4 mt-4">
                            <h3 class="h6 text-uppercase fw-bold text-muted mb-3">Docentes Asignados</h3>
                            <c:choose>
                                <c:when test="${not empty docentesEdicion}">
                                    <div class="d-flex flex-wrap gap-2">
                                        <c:forEach items="${docentesEdicion}" var="docente">
                                            <c:url var="perfilDocenteUrl" value="/consulta-usuario">
                                                <c:param name="nickname" value="${docente.nickname}"/>
                                            </c:url>
                                            <c:url var="imagenDocenteUrl" value="/imagenes-usuario">
                                                <c:param name="nombre" value="${imagenesDocentesEdicion[docente.nickname]}"/>
                                            </c:url>
                                            <a href="<c:out value='${perfilDocenteUrl}'/>"
                                               class="d-inline-flex align-items-center gap-2 badge bg-light text-dark border px-3 py-2 text-decoration-none">
                                                <img src="<c:out value='${imagenDocenteUrl}'/>"
                                                     class="rounded-circle" width="36" height="36"
                                                     style="object-fit: cover;"
                                                     alt="Foto de <c:out value='${docente.nombre}'/>">
                                                <span><c:out value="${docente.nombre}"/> <c:out value="${docente.apellido}"/></span>
                                            </a>
                                        </c:forEach>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <p class="text-muted small mb-0">No hay docentes asignados a esta edición.</p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <c:if test="${sessionScope.usuarioTipo eq 'Estudiante'}">
                            <c:url var="inscripcionEdicionUrl" value="/inscripcion-edicion">
                                <c:param name="curso" value="${cursoSeleccionado}"/>
                            </c:url>
                            <div class="border-top pt-4 mt-4">
                                <a class="btn btn-primary" href="<c:out value='${inscripcionEdicionUrl}'/>">
                                    <i class="bi bi-pencil-square me-1" aria-hidden="true"></i>
                                    Consultar edición vigente e inscribirme
                                </a>
                            </div>
                        </c:if>
                        <c:if test="${puedeVerAceptados}">
                            <c:url var="aceptadosEdicionUrl" value="/consulta-edicion-curso">
                                <c:if test="${not empty filtroInstituto}"><c:param name="instituto" value="${filtroInstituto}"/></c:if>
                                <c:if test="${not empty filtroCategoria}"><c:param name="categoria" value="${filtroCategoria}"/></c:if>
                                <c:param name="curso" value="${cursoSeleccionado}"/>
                                <c:param name="edicion" value="${edicion.nombre}"/>
                                <c:param name="verAceptados" value="true"/>
                            </c:url>
                            <div class="border-top pt-4 mt-4">
                                <a class="btn btn-outline-primary" href="<c:out value='${aceptadosEdicionUrl}'/>">
                                    <i class="bi bi-people me-1" aria-hidden="true"></i>
                                    Ver estudiantes aceptados
                                </a>
                            </div>
                            <c:url var="gestionInscriptosUrl" value="/seleccion-estudiantes">
                                <c:param name="instituto" value="${institutoCurso}"/>
                                <c:if test="${not empty filtroCategoria}"><c:param name="categoria" value="${filtroCategoria}"/></c:if>
                                <c:param name="curso" value="${cursoSeleccionado}"/>
                                <c:param name="edicion" value="${edicion.nombre}"/>
                            </c:url>
                            <div class="border-top pt-4 mt-4">
                                <a class="btn btn-outline-secondary" href="<c:out value='${gestionInscriptosUrl}'/>">
                                    <i class="bi bi-gear me-1" aria-hidden="true"></i>
                                    Gestionar inscriptos
                                </a>
                            </div>
                        </c:if>
                    </article>
                    <c:if test="${mostrarAceptados}">
                        <section class="content-card p-4 p-md-5 mt-4" aria-labelledby="aceptados-heading">
                            <h3 class="h5 fw-bold mb-3" id="aceptados-heading">
                                Estudiantes aceptados en <c:out value="${edicion.nombre}"/>
                            </h3>
                            <c:choose>
                                <c:when test="${not empty inscripcionesAceptadas}">
                                    <div class="table-responsive">
                                        <table class="table align-middle mb-0">
                                            <thead class="table-light">
                                                <tr>
                                                    <th>Estudiante</th>
                                                    <th>Fecha de inscripción</th>
                                                    <th>Prioridad</th>
                                                    <th>Estado</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach items="${inscripcionesAceptadas}" var="inscripcionAceptada">
                                                    <tr>
                                                        <td><c:out value="${inscripcionAceptada.nicknameEstudiante}"/></td>
                                                        <td><c:out value="${inscripcionAceptada.fechaInscripcion}"/></td>
                                                        <td><c:out value="${empty inscripcionAceptada.prioridad ? '-' : inscripcionAceptada.prioridad}"/></td>
                                                        <td><span class="badge text-bg-success">Aceptada</span></td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <p class="text-muted mb-0">Todavía no hay estudiantes aceptados en esta edición.</p>
                                </c:otherwise>
                            </c:choose>
                        </section>
                    </c:if>
                </c:when>

                <c:when test="${not empty cursoSeleccionado and empty ediciones}">
                    <div class="content-card p-5 text-center">
                        <i class="bi bi-info-circle display-4 text-muted mb-3" aria-hidden="true"></i>
                        <h2 class="h5 fw-bold">El curso no posee ediciones</h2>
                        <p class="text-muted mb-0">El curso <strong><c:out value="${cursoSeleccionado}"/></strong> no tiene ediciones registradas actualmente.</p>
                    </div>
                </c:when>

                <c:otherwise>
                    <div class="content-card p-5 text-center">
                        <i class="bi bi-cursor display-4 text-muted mb-3" aria-hidden="true"></i>
                        <h2 class="h5 fw-bold">Seleccioná un curso y edición</h2>
                        <p class="text-muted mb-0">Usá el panel de la izquierda para seleccionar el instituto o categoría, elegir un curso y ver el detalle de sus ediciones.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
    </div>
        </div>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>