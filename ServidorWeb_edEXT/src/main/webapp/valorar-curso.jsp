<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Valorar un curso | edEXT</title>
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
                <a href="inicio" class="text-decoration-none text-muted">
                    <i class="bi bi-arrow-left me-1" aria-hidden="true"></i>Volver al inicio
                </a>
                <p class="text-uppercase fw-bold small text-muted mb-1 mt-3">Cursos</p>
                <h1 class="page-heading fw-bold mb-1">Valorar un curso</h1>
                <p class="text-muted mb-0">Elegí un curso en el que tengas una inscripción aceptada para consultar y publicar tu valoración.</p>
            </div>

            <c:if test="${not empty error}">
                <div class="alert alert-danger" role="alert"><c:out value="${error}"/></div>
            </c:if>
            <c:if test="${param.publicada eq '1'}">
                <div class="alert alert-success" role="status">Tu valoración se publicó correctamente.</div>
            </c:if>

            <div class="row g-4">
                <aside class="col-lg-4">
                    <div class="content-card p-4">
                        <label for="curso" class="form-label fw-semibold">Cursos en los que participás</label>
                        <form action="valorar-curso" method="get">
                            <select class="form-select" id="curso" name="curso" required>
                                <option value="">-- Seleccionar curso --</option>
                                <c:forEach items="${cursos}" var="curso">
                                    <option value="<c:out value='${curso}'/>"
                                            <c:if test="${curso eq cursoSeleccionado}">selected</c:if>>
                                        <c:out value="${curso}"/>
                                    </option>
                                </c:forEach>
                            </select>
                            <button type="submit" class="btn btn-outline-primary w-100 mt-3">Consultar curso</button>
                        </form>
                        <c:if test="${empty cursos}">
                            <p class="small text-muted mt-3 mb-0">Todavía no tenés inscripciones aceptadas en cursos para valorar.</p>
                        </c:if>
                    </div>
                </aside>

                <section class="col-lg-8">
                    <c:choose>
                        <c:when test="${not empty cursoSeleccionado}">
                            <article class="content-card p-4 p-md-5">
                                <span class="badge text-bg-primary mb-2">Curso</span>
                                <h2 class="h3 fw-bold mb-4"><c:out value="${cursoSeleccionado}"/></h2>

                                <div class="d-flex flex-wrap align-items-baseline gap-2 mb-2">
                                    <span class="display-5 fw-bold"><c:out value="${promedio}"/></span>
                                    <span class="text-muted">/ 5 · promedio de valoraciones</span>
                                </div>
                                <h3 class="h6 fw-bold mt-4">Distribución de puntajes</h3>
                                <ul class="list-group list-group-flush mb-4">
                                    <c:forEach items="${conteos}" var="conteo">
                                        <li class="list-group-item d-flex justify-content-between px-0">
                                            <span>
                                                <c:out value="${conteo.key}"/> <i class="bi bi-star-fill text-warning" aria-hidden="true"></i>
                                            </span>
                                            <span><c:out value="${conteo.value}"/> valoraciones</span>
                                        </li>
                                    </c:forEach>
                                </ul>

                                <c:choose>
                                    <c:when test="${not empty puntajePropio}">
                                        <div class="alert alert-info mb-0" role="status">
                                            Ya valoraste este curso con
                                            <strong><c:out value="${puntajePropio}"/> / 5</strong>.
                                            Cada estudiante puede valorar un curso una sola vez.
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <form action="valorar-curso" method="post">
                                            <input type="hidden" name="curso" value="<c:out value='${cursoSeleccionado}'/>"/>
                                            <label for="puntaje" class="form-label fw-semibold">Tu puntaje</label>
                                            <select class="form-select mb-3" id="puntaje" name="puntaje" required>
                                                <option value="">-- Elegir puntaje --</option>
                                                <c:forEach begin="1" end="5" var="puntaje">
                                                    <option value="${puntaje}">
                                                        <c:out value="${puntaje}"/> <c:out value="${puntaje eq 1 ? 'estrella' : 'estrellas'}"/>
                                                    </option>
                                                </c:forEach>
                                            </select>
                                            <button type="submit" class="btn btn-primary">
                                                <i class="bi bi-star me-1" aria-hidden="true"></i>Publicar valoración
                                            </button>
                                        </form>
                                    </c:otherwise>
                                </c:choose>
                            </article>
                        </c:when>
                        <c:otherwise>
                            <div class="content-card p-5 text-center">
                                <i class="bi bi-star display-4 text-muted mb-3" aria-hidden="true"></i>
                                <h2 class="h5 fw-bold">Elegí un curso</h2>
                                <p class="text-muted mb-0">Vas a poder ver el promedio y tu valoración, si ya publicaste una.</p>
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
