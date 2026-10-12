<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Detalle del curso | edEXT</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <c:url var="estilosUrl" value="/assets/css/edext.css">
        <c:param name="v" value="20261012-3"/>
    </c:url>
    <link href="<c:out value='${estilosUrl}'/>" rel="stylesheet">
</head>
<body>
<!-- Navegación compartida y búsqueda en todos los tipos de contenido. -->
<%@ include file="/WEB-INF/views/buscador-global.jspf" %>
<c:url var="inicioUrl" value="/inicio"/>

<!-- El servlet consulta el nombre solicitado y expone el DTO como atributo "curso". -->
<main class="container-fluid page-layout py-4 px-3 px-lg-4">
    <div class="row g-4 align-items-start">
        <%@ include file="/WEB-INF/views/sidebar-principal.jspf" %>
        <div class="col main-content-column">
    <a href="<c:out value='${inicioUrl}'/>" class="text-decoration-none text-muted">
        <i class="bi bi-arrow-left me-1" aria-hidden="true"></i> Volver al inicio
    </a>

    <c:if test="${not empty error}">
        <div class="alert alert-danger mt-3" role="alert"><c:out value="${error}"/></div>
    </c:if>
    <c:if test="${param.registrado eq '1'}">
        <div class="alert alert-success mt-3" role="status">El curso se registró correctamente.</div>
    </c:if>

    <c:choose>
        <c:when test="${not empty curso}">
            <div class="row g-4 mt-2">
                <section class="col-lg-8">
                    <article class="content-card p-4 p-md-5 course-detail-card">
                        <span class="badge text-bg-primary mb-3"><c:out value="${curso.nomInstituto}"/></span>
                        <h1 class="page-heading fw-bold"><c:out value="${curso.nombre}"/></h1>
                        <p class="lead text-muted"><c:out value="${curso.descripcion}"/></p>
                        <hr>
                        <%-- Las relaciones del curso van en pestañas propias para mantener visible y ordenada la información general. --%>
                        <ul class="nav nav-tabs course-detail-tabs mb-4" role="tablist">
                            <li class="nav-item" role="presentation">
                                <button class="nav-link active" id="curso-info-tab" data-bs-toggle="tab"
                                        data-bs-target="#curso-info-panel" type="button" role="tab"
                                        aria-controls="curso-info-panel" aria-selected="true">
                                    Información general
                                </button>
                            </li>
                            <li class="nav-item" role="presentation">
                                <button class="nav-link" id="curso-ediciones-tab" data-bs-toggle="tab"
                                        data-bs-target="#curso-ediciones-panel" type="button" role="tab"
                                        aria-controls="curso-ediciones-panel" aria-selected="false">
                                    Ediciones <span class="badge text-bg-light"><c:out value="${curso.ediciones.size()}"/></span>
                                </button>
                            </li>
                            <li class="nav-item" role="presentation">
                                <button class="nav-link" id="curso-programas-tab" data-bs-toggle="tab"
                                        data-bs-target="#curso-programas-panel" type="button" role="tab"
                                        aria-controls="curso-programas-panel" aria-selected="false">
                                    Programas <span class="badge text-bg-light"><c:out value="${curso.programas.size()}"/></span>
                                </button>
                            </li>
                        </ul>
                        <div class="tab-content">
                            <div class="tab-pane fade show active" id="curso-info-panel" role="tabpanel"
                                 aria-labelledby="curso-info-tab" tabindex="0">
                                <h2 class="h5 fw-bold">Información del curso</h2>
                                <dl class="row mb-0">
                                    <dt class="col-sm-4">Duración</dt>
                                    <dd class="col-sm-8"><c:out value="${curso.duracion}"/> meses</dd>
                                    <dt class="col-sm-4">Cantidad de horas</dt>
                                    <dd class="col-sm-8"><c:out value="${curso.cantHoras}"/></dd>
                                    <dt class="col-sm-4">Créditos</dt>
                                    <dd class="col-sm-8"><c:out value="${curso.creditos}"/></dd>
                                    <dt class="col-sm-4">Fecha de alta</dt>
                                    <dd class="col-sm-8"><c:out value="${curso.fecha}"/></dd>
                                    <dt class="col-sm-4">Categorías</dt>
                                    <dd class="col-sm-8">
                                        <c:choose>
                                            <c:when test="${not empty curso.categorias}">
                                                <div class="course-detail-tags">
                                                    <c:forEach items="${curso.categorias}" var="categoria">
                                                        <%-- c:url codifica el nombre de la categoría como parámetro de filtro. --%>
                                                        <c:url var="categoriaUrl" value="/inicio">
                                                            <c:param name="categoria" value="${categoria}"/>
                                                        </c:url>
                                                        <a class="badge rounded-pill text-bg-primary text-decoration-none"
                                                           href="<c:out value='${categoriaUrl}'/>">
                                                            <c:out value="${categoria}"/>
                                                        </a>
                                                    </c:forEach>
                                                </div>
                                            </c:when>
                                            <c:otherwise><span class="text-muted">Sin categorías</span></c:otherwise>
                                        </c:choose>
                                    </dd>
                                    <dt class="col-sm-4">Cursos previos</dt>
                                    <dd class="col-sm-8">
                                        <c:choose>
                                            <c:when test="${not empty curso.previas}">
                                                <div class="course-detail-tags">
                                                    <c:forEach items="${curso.previas}" var="previa">
                                                        <%-- Las previas enlazan al detalle de curso usando su nombre como parámetro. --%>
                                                        <c:url var="cursoPreviaUrl" value="/consulta-curso">
                                                            <c:param name="nombre" value="${previa}"/>
                                                        </c:url>
                                                        <a class="badge rounded-pill text-bg-light border text-decoration-none"
                                                           href="<c:out value='${cursoPreviaUrl}'/>">
                                                            <i class="bi bi-arrow-up-right me-1" aria-hidden="true"></i>
                                                            <c:out value="${previa}"/>
                                                        </a>
                                                    </c:forEach>
                                                </div>
                                            </c:when>
                                            <c:otherwise><span class="text-muted">No requiere cursos previos</span></c:otherwise>
                                        </c:choose>
                                    </dd>
                                    <dt class="col-sm-4">URL asociada</dt>
                                    <dd class="col-sm-8 text-break"><c:out value="${curso.url}"/></dd>
                                </dl>
                            </div>
                            <div class="tab-pane fade" id="curso-ediciones-panel" role="tabpanel"
                                 aria-labelledby="curso-ediciones-tab" tabindex="0">
                                <h2 class="h5 fw-bold mb-3">Ediciones del curso</h2>
                                <c:choose>
                                    <c:when test="${not empty curso.ediciones}">
                                        <ul class="list-group list-group-flush">
                                            <c:forEach items="${curso.ediciones}" var="edicion">
                                                <li class="list-group-item d-flex align-items-center gap-2">
                                                    <i class="bi bi-calendar3 text-primary" aria-hidden="true"></i>
                                                    <c:out value="${edicion}"/>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </c:when>
                                    <c:otherwise>
                                        <p class="text-muted mb-0">Este curso todavía no tiene ediciones asociadas.</p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="tab-pane fade" id="curso-programas-panel" role="tabpanel"
                                 aria-labelledby="curso-programas-tab" tabindex="0">
                                <h2 class="h5 fw-bold mb-3">Programas de formación</h2>
                                <c:choose>
                                    <c:when test="${not empty curso.programas}">
                                        <ul class="list-group list-group-flush">
                                            <c:forEach items="${curso.programas}" var="programa">
                                                <li class="list-group-item d-flex align-items-center gap-2">
                                                    <i class="bi bi-mortarboard text-primary" aria-hidden="true"></i>
                                                    <c:out value="${programa}"/>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </c:when>
                                    <c:otherwise>
                                        <p class="text-muted mb-0">Este curso todavía no pertenece a programas de formación.</p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </article>
                </section>
                <aside class="col-lg-4">
                    <div class="content-card p-3">
                        <c:choose>
                            <c:when test="${not empty curso.imagenPath}">
                                <c:url var="imagenCursoUrl" value="/imagenes-curso">
                                    <c:param name="nombre" value="${curso.imagenPath}"/>
                                </c:url>
                                <img src="<c:out value='${imagenCursoUrl}'/>"
                                     class="course-detail-image" alt="Imagen de <c:out value='${curso.nombre}'/>">
                            </c:when>
                            <c:otherwise>
                                <div class="course-image-placeholder">
                                    <i class="bi bi-image display-4" aria-hidden="true"></i>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </aside>
            </div>
        </c:when>
        <c:otherwise>
            <div class="content-card p-4 p-md-5 mt-4 text-center">
                <i class="bi bi-info-circle display-5 text-primary" aria-hidden="true"></i>
                <h1 class="h4 fw-bold mt-3">No hay datos de curso para mostrar</h1>
                <p class="text-muted mb-4">El controlador debe cargar el curso solicitado antes de mostrar este detalle.</p>
                <a href="<c:out value='${inicioUrl}'/>" class="btn btn-dark">Ir al inicio</a>
            </div>
        </c:otherwise>
    </c:choose>
        </div>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
