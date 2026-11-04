<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">
    <meta name="theme-color" content="#f4f6fb">
    <title><c:out value="${edicion.nombre}"/> · edEXT Mobile</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/mobile.css">
</head>
<body>
<%@ include file="fragments/header.jspf" %>
<main class="page">
    <c:if test="${not empty curso}">
        <c:url var="cursoUrl" value="/curso">
            <c:param name="nombre" value="${curso.nombre}"/>
        </c:url>
        <a class="back-link" href="<c:out value='${cursoUrl}'/>">
            ← <c:out value="${curso.nombre}"/>
        </a>
    </c:if>
    <article class="detail-card edition-card">
        <c:if test="${not empty edicion.imagenPath}">
            <c:url var="imagenUrl" value="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}/imagenes-curso">
                <c:param name="nombre" value="${edicion.imagenPath}"/>
            </c:url>
            <img class="detail-art detail-photo" src="<c:out value='${imagenUrl}'/>"
                 alt="Imagen de <c:out value='${edicion.nombre}'/>">
        </c:if>
        <div class="detail-content">
            <p class="eyebrow">Edición del curso</p>
            <h1><c:out value="${edicion.nombre}"/></h1>
            <dl class="facts">
                <div><dt>Inicio</dt><dd><c:out value="${edicion.fechaInicio}"/></dd></div>
                <div><dt>Finalización</dt><dd><c:out value="${edicion.fechaFin}"/></dd></div>
                <div><dt>Publicación</dt><dd><c:out value="${edicion.fechaPublicacion}"/></dd></div>
                <div><dt>Cupo</dt><dd><c:out value="${edicion.cupoDisponible}"/> disponibles</dd></div>
                <div><dt>Estado</dt><dd><c:choose><c:when test="${edicion.cerrada}">Cerrada</c:when><c:otherwise>Abierta</c:otherwise></c:choose></dd></div>
            </dl>
            <section class="detail-section">
                <h2>Docentes</h2>
                <c:choose>
                    <c:when test="${empty edicion.docentes}"><p class="muted">Sin docentes asignados.</p></c:when>
                    <c:otherwise>
                        <ul class="plain-list">
                            <c:forEach var="docente" items="${edicion.docentes}">
                                <li><c:out value="${docente}"/></li>
                            </c:forEach>
                        </ul>
                    </c:otherwise>
                </c:choose>
            </section>
        </div>
    </article>
</main>
</body>
</html>
