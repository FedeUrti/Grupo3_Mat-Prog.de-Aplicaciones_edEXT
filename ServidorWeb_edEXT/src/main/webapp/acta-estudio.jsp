<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Generar acta de estudio | edEXT</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <c:url var="estilosUrl" value="/assets/css/edext.css">
        <c:param name="v" value="20261015-2"/>
    </c:url>
    <link href="<c:out value='${estilosUrl}'/>" rel="stylesheet">
</head>
<body>
<%@ include file="/WEB-INF/views/buscador-global.jspf" %>
<main class="container-fluid page-layout py-4 px-3 px-lg-4">
    <div class="row g-4 align-items-start">
        <%@ include file="/WEB-INF/views/sidebar-principal.jspf" %>
        <div class="col main-content-column">
            <div class="mb-4">
                <a href="mi-cuenta" class="text-decoration-none text-muted">
                    <i class="bi bi-arrow-left me-1" aria-hidden="true"></i>Volver a mi perfil
                </a>
                <p class="text-uppercase fw-bold small text-muted mb-1 mt-3">Estudios</p>
                <h1 class="page-heading fw-bold mb-1">Generar acta de estudio</h1>
                <p class="text-muted mb-0">Seleccioná un programa en el que estés inscripto para descargar su acta en PDF.</p>
            </div>

            <section class="content-card p-4 p-md-5" style="max-width: 760px;">
                <c:if test="${not empty error}">
                    <div class="alert alert-warning" role="status"><c:out value="${error}"/></div>
                </c:if>
                <c:choose>
                    <c:when test="${not empty programas}">
                        <c:choose>
                            <c:when test="${not empty programasDisponibles}">
                                <form action="acta-estudio" method="get">
                                    <label for="programa" class="form-label fw-semibold">Programa aprobado</label>
                                    <select class="form-select mb-3" id="programa" name="programa" required>
                                        <c:forEach items="${programasDisponibles}" var="programa">
                                            <option value="<c:out value='${programa}'/>"
                                                    <c:if test="${programa eq programaSeleccionado}">selected</c:if>>
                                                <c:out value="${programa}"/>
                                            </option>
                                        </c:forEach>
                                    </select>
                                    <input type="hidden" name="descargar" value="1"/>
                                    <p class="form-text mb-4">
                                        El PDF se genera al descargarlo e incluye los resultados de las ediciones cerradas.
                                    </p>
                                    <button type="submit" class="btn btn-primary">
                                        <i class="bi bi-file-earmark-pdf me-1" aria-hidden="true"></i>Generar y descargar PDF
                                    </button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <div class="text-center py-3">
                                    <i class="bi bi-journal-x display-5 text-muted mb-3" aria-hidden="true"></i>
                                    <h2 class="h5 fw-bold">Todavía no hay actas disponibles</h2>
                                    <p class="text-muted mb-0">
                                        Tenés que aprobar todos los cursos de un programa para generar su acta.
                                    </p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                        <c:if test="${not empty programasPendientes}">
                            <div class="mt-4">
                                <h2 class="h6 fw-bold">Programas con cursos pendientes</h2>
                                <ul class="mb-0">
                                    <c:forEach items="${programasPendientes}" var="programa">
                                        <li><c:out value="${programa}"/></li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </c:if>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center py-3">
                            <i class="bi bi-journal-x display-5 text-muted mb-3" aria-hidden="true"></i>
                            <h2 class="h5 fw-bold">No tenés programas inscriptos</h2>
                            <p class="text-muted mb-0">Cuando te inscribas a un programa, vas a poder generar su acta desde aquí.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </section>
        </div>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
