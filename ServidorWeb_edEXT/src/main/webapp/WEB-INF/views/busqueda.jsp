<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Resultados de búsqueda | edEXT</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <c:url var="estilosUrl" value="/assets/css/edext.css">
        <c:param name="v" value="20261012-1"/>
    </c:url>
    <link href="<c:out value='${estilosUrl}'/>" rel="stylesheet">
</head>
<body>
<%@ include file="/WEB-INF/views/buscador-global.jspf" %>

<main class="container my-4 my-lg-5">
    <p class="text-uppercase fw-bold small text-muted mb-1">Búsqueda global</p>
    <h1 class="page-heading fw-bold mb-4">
        <c:choose>
            <c:when test="${not empty query}">Resultados para “<c:out value="${query}"/>”</c:when>
            <c:otherwise>Buscar en edEXT</c:otherwise>
        </c:choose>
    </h1>

    <c:if test="${not empty error}">
        <div class="alert alert-danger" role="alert"><c:out value="${error}"/></div>
    </c:if>

    <c:choose>
        <c:when test="${empty query and empty error}">
            <div class="content-card p-4 text-center">
                <p class="text-muted mb-0">Ingresá un término para buscar cursos, ediciones, programas y usuarios.</p>
            </div>
        </c:when>
        <c:when test="${empty resultadosBusqueda and empty error}">
            <div class="content-card p-4 text-center">
                <i class="bi bi-search display-5 text-primary" aria-hidden="true"></i>
                <h2 class="h5 fw-bold mt-3">No se encontraron resultados</h2>
                <p class="text-muted mb-0">Probá con otro nombre, nickname, instituto o programa.</p>
            </div>
        </c:when>
        <c:otherwise>
            <%-- El servlet ya entrega el texto de cada resultado y su URL de destino, si existe. --%>
            <div class="d-flex flex-column gap-3" aria-label="Resultados encontrados">
                <c:forEach items="${resultadosBusqueda}" var="resultado">
                    <article class="content-card p-3 p-md-4 d-flex flex-wrap align-items-center justify-content-between gap-3">
                        <div class="flex-grow-1">
                            <span class="badge text-bg-primary mb-2"><c:out value="${resultado.tipo}"/></span>
                            <h2 class="h5 fw-bold mb-1"><c:out value="${resultado.nombre}"/></h2>
                            <p class="text-muted mb-0"><c:out value="${resultado.detalle}"/></p>
                        </div>
                        <c:if test="${not empty resultado.url}">
                            <a class="btn btn-outline-primary flex-shrink-0"
                               href="<c:out value='${resultado.url}'/>">Ver resultado</a>
                        </c:if>
                    </article>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
