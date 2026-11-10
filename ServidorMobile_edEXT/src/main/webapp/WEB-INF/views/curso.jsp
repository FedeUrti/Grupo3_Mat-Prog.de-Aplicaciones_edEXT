<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">
    <meta name="theme-color" content="#f4f6fb">
    <title><c:out value="${curso.nombre}"/> · edEXT Mobile</title>
    <script>
        try {
            if (localStorage.getItem("edext-mobile-theme") === "dark") {
                document.documentElement.dataset.theme = "dark";
            }
        } catch (error) {
            // Si el navegador bloquea el almacenamiento, se conserva el tema claro.
        }
    </script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/mobile.css?v=20261110-2">
</head>
<body>
<%@ include file="fragments/header.jspf" %>
<main class="page">
    <a class="back-link" href="${pageContext.request.contextPath}/app">← Todos los cursos</a>
    <article class="detail-card">
        <c:url var="imagenUrl" value="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}/imagenes-curso">
            <c:param name="nombre" value="${curso.imagenPath}"/>
        </c:url>
        <c:choose>
            <c:when test="${not empty curso.imagenPath}">
                <img class="detail-art detail-photo" src="<c:out value='${imagenUrl}'/>"
                     alt="Imagen de <c:out value='${curso.nombre}'/>">
            </c:when>
            <c:otherwise><div class="detail-art" aria-hidden="true"><span>edEXT</span></div></c:otherwise>
        </c:choose>
        <div class="detail-content">
            <p class="eyebrow"><c:out value="${curso.nomInstituto}"/></p>
            <h1><c:out value="${curso.nombre}"/></h1>
            <p class="lead"><c:out value="${curso.descripcion}"/></p>
            <dl class="facts">
                <div><dt>Duración</dt><dd><c:out value="${curso.duracion}"/> semanas</dd></div>
                <div><dt>Horas</dt><dd><c:out value="${curso.cantHoras}"/></dd></div>
                <div><dt>Créditos</dt><dd><c:out value="${curso.creditos}"/></dd></div>
                <div><dt>Fecha de registro</dt><dd><c:out value="${curso.fecha}"/></dd></div>
            </dl>
            <section class="detail-section">
                <h2>Ediciones del curso</h2>
                <c:choose>
                    <c:when test="${empty ediciones}">
                        <p class="muted">Este curso todavía no tiene ediciones.</p>
                    </c:when>
                    <c:otherwise>
                        <ul class="link-list">
                            <c:forEach var="nombreEdicion" items="${ediciones}">
                                <c:url var="edicionUrl" value="/edicion">
                                    <c:param name="nombre" value="${nombreEdicion}"/>
                                </c:url>
                                <li><a href="<c:out value='${edicionUrl}'/>">
                                    <c:out value="${nombreEdicion}"/> <span aria-hidden="true">→</span>
                                </a></li>
                            </c:forEach>
                        </ul>
                    </c:otherwise>
                </c:choose>
            </section>
            <c:if test="${not empty curso.programas}">
                <section class="detail-section">
                    <h2>Programas de formación</h2>
                    <ul class="plain-list">
                        <c:forEach var="programa" items="${curso.programas}">
                            <li><c:out value="${programa}"/></li>
                        </c:forEach>
                    </ul>
                </section>
            </c:if>
        </div>
    </article>
</main>
<script src="${pageContext.request.contextPath}/assets/theme.js?v=20261110-1" defer></script>
</body>
</html>
