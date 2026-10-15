<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Agregar curso a programa | edEXT</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <c:url var="estilosUrl" value="/assets/css/edext.css">
        <c:param name="v" value="20261015-1"/>
    </c:url>
    <link href="<c:out value='${estilosUrl}'/>" rel="stylesheet">
</head>
<body>
    <%@ include file="/WEB-INF/views/buscador-global.jspf" %>
    <c:url var="programasUrl" value="/programas"/>
    <c:url var="agregarCursoUrl" value="/agregar-curso-programa"/>
    <main class="container-fluid page-layout py-4 px-3 px-lg-4">
        <div class="row g-4 align-items-start">
            <%@ include file="/WEB-INF/views/sidebar-principal.jspf" %>
            <div class="col main-content-column">
                <a href="<c:out value='${programasUrl}'/>" class="text-decoration-none text-muted">
                    <i class="bi bi-arrow-left me-1" aria-hidden="true"></i> Volver a programas
                </a>
                <div class="my-4">
                    <p class="text-uppercase fw-bold small text-muted mb-1">Gestión académica</p>
                    <h1 class="page-heading fw-bold mb-1">Agregar curso a programa</h1>
                    <p class="text-muted mb-0">Seleccioná el programa y el curso que querés integrar.</p>
                </div>

                <c:if test="${not empty param.error}">
                    <div class="alert alert-danger" role="alert"><c:out value="${param.error}"/></div>
                </c:if>

                <form action="<c:out value='${agregarCursoUrl}'/>"
                      method="post" class="content-card p-3 p-md-4">
                    <div class="mb-4">
                        <label for="nombrePrograma" class="form-label fw-semibold">Programa de formación</label>
                        <c:choose>
                            <c:when test="${not empty programaSeleccionado}">
                                <select class="form-select" id="nombrePrograma" name="nombrePrograma" required disabled>
                            </c:when>
                            <c:otherwise>
                                <select class="form-select" id="nombrePrograma" name="nombrePrograma" required>
                            </c:otherwise>
                        </c:choose>
                            <option value="">Seleccioná un programa</option>
                            <c:forEach var="programa" items="${programas}">
                                <option value="<c:out value='${programa.nombre}'/>"
                                        <c:if test="${programa.nombre eq programaSeleccionado}">selected</c:if>>
                                    <c:out value="${programa.nombre}"/>
                                </option>
                            </c:forEach>
                        </select>
                        <c:if test="${not empty programaSeleccionado}">
                            <input type="hidden" name="nombrePrograma"
                                   value="<c:out value='${programaSeleccionado}'/>">
                        </c:if>
                    </div>
                    <div class="mb-4">
                        <label for="nombreCurso" class="form-label fw-semibold">Curso a integrar</label>
                        <select class="form-select" id="nombreCurso" name="nombreCurso" required>
                            <option value="">Seleccioná un curso</option>
                            <c:forEach var="curso" items="${cursos}">
                                <option value="<c:out value='${curso}'/>"><c:out value="${curso}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="d-flex justify-content-end gap-2">
                        <c:choose>
                            <c:when test="${not empty programaSeleccionado}">
                                <c:url var="cancelarUrl" value="/consulta-programa">
                                    <c:param name="nombre" value="${programaSeleccionado}"/>
                                </c:url>
                            </c:when>
                            <c:otherwise>
                                <c:set var="cancelarUrl" value="${programasUrl}"/>
                            </c:otherwise>
                        </c:choose>
                        <a href="<c:out value='${cancelarUrl}'/>" class="btn btn-outline-secondary">Cancelar</a>
                        <button type="submit" class="btn btn-primary">
                            <i class="bi bi-plus-circle me-1" aria-hidden="true"></i> Agregar curso
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </main>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>