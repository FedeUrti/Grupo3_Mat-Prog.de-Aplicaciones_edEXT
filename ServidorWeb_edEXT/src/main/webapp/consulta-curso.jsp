<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Detalle del curso | edEXT</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <link href="assets/css/edext.css" rel="stylesheet">
</head>
<body>
<!-- Navegación común que mantiene el acceso al catálogo y las demás páginas. -->
<header class="custom-header py-2 px-4 shadow-sm">
    <div class="container-fluid d-flex align-items-center justify-content-between">
        <a href="index.jsp" class="text-decoration-none" aria-label="Inicio edEXT">
            <div class="logo-circle">edEXT</div>
        </a>
        <nav class="d-flex gap-2" aria-label="Navegación principal">
            <a href="cursos" class="btn btn-outline-light">Cursos</a>
            <a href="alta-curso" class="btn btn-light text-dark">Alta de curso</a>
            <c:choose>
                <c:when test="${not empty sessionScope.usuarioNickname}">
                    <a href="mi-cuenta" class="btn btn-outline-light">Mi cuenta</a>
                </c:when>
                <c:otherwise>
                    <a href="login" class="btn btn-outline-light">Iniciar sesión</a>
                </c:otherwise>
            </c:choose>
        </nav>
    </div>
</header>

<!-- El servlet consulta el nombre solicitado y expone el DTO como atributo "curso". -->
<main class="container my-4 my-lg-5">
    <a href="cursos" class="text-decoration-none text-muted">
        <i class="bi bi-arrow-left me-1" aria-hidden="true"></i> Volver a cursos
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
                    <article class="content-card p-4 p-md-5">
                        <span class="badge text-bg-primary mb-3"><c:out value="${curso.nomInstituto}"/></span>
                        <h1 class="page-heading fw-bold"><c:out value="${curso.nombre}"/></h1>
                        <p class="lead text-muted"><c:out value="${curso.descripcion}"/></p>
                        <hr>
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
                            <dd class="col-sm-8"><c:out value="${curso.categorias}"/></dd>
                            <dt class="col-sm-4">Cursos previos</dt>
                            <dd class="col-sm-8"><c:out value="${curso.previas}"/></dd>
                            <dt class="col-sm-4">URL asociada</dt>
                            <dd class="col-sm-8 text-break"><c:out value="${curso.url}"/></dd>
                        </dl>
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
                                     class="img-fluid rounded" alt="Imagen de <c:out value='${curso.nombre}'/>">
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
                <a href="cursos" class="btn btn-dark">Ver cursos</a>
            </div>
        </c:otherwise>
    </c:choose>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
