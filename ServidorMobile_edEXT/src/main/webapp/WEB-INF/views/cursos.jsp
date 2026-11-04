<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">
    <meta name="theme-color" content="#f4f6fb">
    <title>Cursos · edEXT Mobile</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/mobile.css">
</head>
<body>
<%@ include file="fragments/header.jspf" %>
<main class="page">
    <section class="welcome">
        <p class="eyebrow">Hola, <c:out value="${sessionScope['mobile.nombre']}"/>.</p>
        <h1>Explorá tus cursos</h1>
        <p class="muted">Consultá la información y las ediciones disponibles.</p>
    </section>
    <section class="course-list" aria-label="Cursos">
        <c:choose>
            <c:when test="${empty cursos}">
                <div class="empty-card">Todavía no hay cursos para mostrar.</div>
            </c:when>
            <c:otherwise>
                <c:forEach var="curso" items="${cursos}">
                    <c:url var="cursoUrl" value="/curso">
                        <c:param name="nombre" value="${curso.nombre}"/>
                    </c:url>
                    <c:url var="imagenUrl" value="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}/imagenes-curso">
                        <c:param name="nombre" value="${curso.imagenPath}"/>
                    </c:url>
                    <a class="course-card" href="<c:out value='${cursoUrl}'/>">
                        <c:choose>
                            <c:when test="${not empty curso.imagenPath}">
                                <img class="course-art course-photo" src="<c:out value='${imagenUrl}'/>"
                                     alt="Imagen de <c:out value='${curso.nombre}'/>">
                            </c:when>
                            <c:otherwise><span class="course-art" aria-hidden="true"><span>edEXT</span></span></c:otherwise>
                        </c:choose>
                        <span class="course-copy">
                            <span class="course-institute"><c:out value="${curso.nomInstituto}"/></span>
                            <strong><c:out value="${curso.nombre}"/></strong>
                            <span class="course-description"><c:out value="${curso.descripcion}"/></span>
                            <span class="course-link">Ver curso <span aria-hidden="true">→</span></span>
                        </span>
                    </a>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </section>
</main>
</body>
</html>
