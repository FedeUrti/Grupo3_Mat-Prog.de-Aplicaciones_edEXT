<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Cursos | edEXT</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <link href="assets/css/edext.css" rel="stylesheet">
</head>
<body>
<!-- Barra de navegación y búsqueda del catálogo. -->
<header class="custom-header py-2 px-4 shadow-sm">
    <div class="container-fluid d-flex align-items-center justify-content-between gap-3">
        <a href="inicio" class="text-decoration-none" aria-label="Inicio edEXT">
            <div class="logo-circle">edEXT</div>
        </a>
        <form class="search-area flex-grow-1 mx-3" action="cursos" method="get" role="search">
            <label class="visually-hidden" for="busqueda">Buscar cursos</label>
            <input type="hidden" name="instituto" value="<c:out value='${filtroInstituto}'/>">
            <input type="hidden" name="categoria" value="<c:out value='${filtroCategoria}'/>">
            <div class="input-group">
                <span class="input-group-text bg-white border-end-0 rounded-start-pill ps-3 text-muted">
                    <i class="bi bi-search" aria-hidden="true"></i>
                </span>
                <input id="busqueda" name="query" class="form-control border-start-0 rounded-end-pill py-2"
                       placeholder="Buscar cursos" value="<c:out value='${query}'/>">
            </div>
        </form>
        <nav class="d-flex gap-2" aria-label="Navegación principal">
            <a href="cursos" class="btn btn-outline-light">Cursos</a>
            <c:choose>
                <c:when test="${not empty sessionScope.usuarioNickname}">
                    <c:if test="${sessionScope.usuarioDocente}">
                        <a href="alta-curso" class="btn btn-light text-dark">Alta de curso</a>
                    </c:if>
                    <a href="mi-cuenta" class="btn btn-outline-light">Mi cuenta</a>
                </c:when>
                <c:otherwise>
                    <a href="login" class="btn btn-outline-light">Iniciar sesión</a>
                </c:otherwise>
            </c:choose>
        </nav>
    </div>
</header>

<!-- El servlet carga opciones, aplica filtros y entrega la lista de cursos a esta vista. -->
<main class="container my-4">
    <div class="d-flex flex-wrap align-items-end justify-content-between gap-3 mb-4">
        <div>
            <p class="text-uppercase fw-bold small text-muted mb-1">Oferta académica</p>
            <h1 class="page-heading fw-bold mb-1">Cursos</h1>
            <p class="text-muted mb-0">Explorá cursos de extensión por instituto y categoría.</p>
        </div>
        <c:if test="${sessionScope.usuarioDocente}">
            <a href="alta-curso" class="btn btn-dark">
                <i class="bi bi-plus-lg me-1" aria-hidden="true"></i> Dar de alta un curso
            </a>
        </c:if>
    </div>

    <div class="row g-4">
        <!-- Los filtros usan institutos y categorías reales recibidos en el request. -->
        <aside class="col-lg-3">
            <form action="cursos" method="get" class="content-card p-3">
                <h2 class="h6 fw-bold mb-3">Filtrar cursos</h2>
                <label class="form-label" for="instituto">Instituto</label>
                <select class="form-select mb-3" id="instituto" name="instituto">
                    <option value="">Todos los institutos</option>
                    <c:forEach items="${institutos}" var="instituto">
                        <option value="<c:out value='${instituto}'/>"
                                <c:if test="${instituto eq filtroInstituto}">selected</c:if>>
                            <c:out value="${instituto}"/>
                        </option>
                    </c:forEach>
                </select>
                <label class="form-label" for="categoria">Categoría</label>
                <select class="form-select mb-3" id="categoria" name="categoria">
                    <option value="">Todas las categorías</option>
                    <c:forEach items="${categorias}" var="categoria">
                        <option value="<c:out value='${categoria}'/>"
                                <c:if test="${categoria eq filtroCategoria}">selected</c:if>>
                            <c:out value="${categoria}"/>
                        </option>
                    </c:forEach>
                </select>
                <button class="btn btn-dark w-100" type="submit">Aplicar filtros</button>
            </form>
        </aside>

        <!-- Mostrar errores, estado vacío o tarjetas según la respuesta del backend. -->
        <section class="col-lg-9" aria-label="Resultados de cursos">
            <c:if test="${param.acceso eq 'docente'}">
                <div class="alert alert-warning" role="alert">Solo los docentes pueden dar de alta cursos.</div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-danger" role="alert"><c:out value="${error}"/></div>
            </c:if>
            <c:if test="${empty error and empty cursos}">
                <div class="content-card p-4 p-md-5 text-center">
                    <div class="course-image-placeholder mx-auto mb-3" style="max-width: 220px; min-height: 150px;">
                        <i class="bi bi-mortarboard display-4" aria-hidden="true"></i>
                    </div>
                    <h2 class="h4 fw-bold">No encontramos cursos</h2>
                    <p class="text-muted mx-auto mb-4" style="max-width: 560px;">
                        Probá con otros filtros o volvé más tarde para consultar la oferta disponible.
                    </p>
                    <c:if test="${sessionScope.usuarioDocente}">
                        <a class="btn btn-read-more text-decoration-none" href="alta-curso">Registrar un curso</a>
                    </c:if>
                </div>
            </c:if>
            <div class="d-flex flex-column gap-3">
                <c:forEach items="${cursos}" var="curso">
                    <article class="course-card p-3">
                        <div class="row align-items-center g-3">
                            <div class="col-md-4 text-center">
                                <c:choose>
                                    <c:when test="${not empty curso.imagenPath}">
                                        <c:url var="imagenCursoUrl" value="/imagenes-curso">
                                            <c:param name="nombre" value="${curso.imagenPath}"/>
                                        </c:url>
                                        <img src="<c:out value='${imagenCursoUrl}'/>"
                                             class="img-fluid rounded" alt="Imagen de <c:out value='${curso.nombre}'/>">
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
                                <p class="text-muted mb-2"><c:out value="${curso.descripcion}"/></p>
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
        </section>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
