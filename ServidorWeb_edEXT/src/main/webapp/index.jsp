<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>edEXT - Plataforma de Extensión Universitaria</title>
    <!-- Bootstrap 5 CSS CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <c:url var="estilosUrl" value="/assets/css/edext.css">
        <c:param name="v" value="20261015-1"/>
    </c:url>
    <link href="<c:out value='${estilosUrl}'/>" rel="stylesheet">
</head>
<body>

    <!-- El buscador global es el mismo en todas las páginas y consulta el backend en vivo. -->
    <%@ include file="/WEB-INF/views/buscador-global.jspf" %>

    <!-- 2. CUERPO: filtros y cursos existentes cargados por InicioServlet. -->
    <div class="container-fluid my-4 px-4">
        <div class="row g-4">

            <%@ include file="/WEB-INF/views/sidebar-principal.jspf" %>

            <!-- El buscador actualiza esta sección con AJAX; los filtros del inicio permanecen debajo. -->
            <div class="col main-content-column">
                <section class="home-search-results mb-4" data-home-search-results hidden
                         aria-live="polite" aria-label="Resultados de la búsqueda"></section>

                <div data-home-course-list>
                    <c:if test="${param.acceso eq 'docente'}">
                        <div class="alert alert-warning" role="alert">Solo los docentes pueden dar de alta cursos.</div>
                    </c:if>
                    <c:if test="${param.acceso eq 'estudiante'}">
                        <div class="alert alert-warning" role="alert">Esta acción está disponible únicamente para estudiantes.</div>
                    </c:if>
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger" role="alert"><c:out value="${error}"/></div>
                    </c:if>
                    <c:if test="${empty error and empty cursos}">
                        <div class="content-card p-4 text-center">
                            <c:choose>
                                <c:when test="${not empty filtroInstituto or not empty filtroCategoria}">
                                    <h2 class="h5 fw-bold">No hay cursos para estos filtros</h2>
                                    <p class="text-muted mb-0">Probá con otros filtros.</p>
                                </c:when>
                                <c:otherwise>
                                    <h2 class="h5 fw-bold">Todavía no hay cursos</h2>
                                    <p class="text-muted mb-0">Los cursos aparecerán aquí cuando se carguen en el sistema.</p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:if>
                    <div class="course-list d-flex flex-column gap-3" data-course-list>
                        <c:forEach items="${cursos}" var="curso">
                            <article class="card course-card p-3" data-course-card>
                                <div class="row align-items-center g-3">
                                    <div class="col-md-4 text-center">
                                        <c:choose>
                                            <c:when test="${not empty curso.imagenPath}">
                                                <c:url var="imagenUrl" value="/imagenes-curso">
                                                    <c:param name="nombre" value="${curso.imagenPath}"/>
                                                </c:url>
                                                <img src="<c:out value='${imagenUrl}'/>"
                                                     class="course-thumbnail home-course-thumbnail"
                                                     alt="Imagen de <c:out value='${curso.nombre}'/>">
                                            </c:when>
                                            <c:otherwise>
                                                <div class="course-image-placeholder">
                                                    <i class="bi bi-mortarboard display-5" aria-hidden="true"></i>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="col-md-8">
                                        <span class="badge text-bg-primary mb-2"><c:out value="${curso.nomInstituto}"/></span>
                                        <h2 class="h5 fw-bold"><c:out value="${curso.nombre}"/></h2>
                                        <p class="card-text text-dark mb-2"><c:out value="${curso.descripcion}"/></p>
                                        <c:url var="detalleUrl" value="/consulta-curso">
                                            <c:param name="nombre" value="${curso.nombre}"/>
                                        </c:url>
                                        <a href="<c:out value='${detalleUrl}'/>"
                                           class="btn btn-read-more text-decoration-none">Ver curso</a>
                                    </div>
                                </div>
                            </article>
                        </c:forEach>
                    </div>
                    <c:if test="${fn:length(cursos) gt 8}">
                        <button class="btn btn-outline-primary mt-3 w-100" type="button"
                                data-show-more-courses data-increment="8" hidden>
                            Mostrar más cursos
                        </button>
                    </c:if>
                </div>
            </div>

        </div>
    </div>

    <!-- Bootstrap 5 JS Bundle CDN -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>