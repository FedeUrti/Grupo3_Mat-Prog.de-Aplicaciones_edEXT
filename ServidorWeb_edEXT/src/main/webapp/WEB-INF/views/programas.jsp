<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Programas de formación | edEXT</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <c:url var="estilosUrl" value="/assets/css/edext.css">
        <c:param name="v" value="20261015-1"/>
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
                    <p class="text-uppercase fw-bold small text-muted mb-1">Oferta académica</p>
                    <h1 class="page-heading fw-bold mb-1">Programas de formación</h1>
                    <p class="text-muted mb-0">Explorá los programas académicos disponibles.</p>
                </div>

                <c:if test="${param.creado eq '1'}">
                    <div class="alert alert-success" role="status">El programa se creó correctamente.</div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger" role="alert"><c:out value="${error}"/></div>
                </c:if>

                <c:choose>
                    <c:when test="${not empty programas}">
                        <div class="row row-cols-1 row-cols-md-2 row-cols-xl-3 g-4">
                            <c:forEach items="${programas}" var="programa">
                                <div class="col">
                                    <article class="content-card h-100 d-flex flex-column overflow-hidden">
                                        <c:choose>
                                            <c:when test="${not empty programa.imagenPath}">
                                                <c:url var="imagenProgramaUrl" value="/imagenes-curso">
                                                    <c:param name="nombre" value="${programa.imagenPath}"/>
                                                </c:url>
                                                <img src="<c:out value='${imagenProgramaUrl}'/>"
                                                     class="w-100" style="height: 180px; object-fit: cover;"
                                                     alt="Imagen de <c:out value='${programa.nombre}'/>">
                                            </c:when>
                                            <c:otherwise>
                                                <div class="bg-light text-secondary d-flex align-items-center justify-content-center"
                                                     style="height: 180px;" aria-hidden="true">
                                                    <i class="bi bi-journal-bookmark display-4"></i>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                        <div class="p-3 d-flex flex-column flex-grow-1">
                                            <h2 class="h5 fw-bold mb-2"><c:out value="${programa.nombre}"/></h2>
                                            <p class="text-muted small flex-grow-1 mb-3">
                                                <c:out value="${programa.descripcion}"/>
                                            </p>
                                            <div class="small text-secondary mb-3">
                                                <div><i class="bi bi-calendar-event me-1" aria-hidden="true"></i>
                                                    <strong>Inicio:</strong> <c:out value="${programa.fechaInicio}"/></div>
                                                <div><i class="bi bi-calendar-check me-1" aria-hidden="true"></i>
                                                    <strong>Fin:</strong> <c:out value="${programa.fechaFin}"/></div>
                                            </div>
                                            <c:url var="detalleProgramaUrl" value="/consulta-programa">
                                                <c:param name="nombre" value="${programa.nombre}"/>
                                            </c:url>
                                            <a href="<c:out value='${detalleProgramaUrl}'/>"
                                               class="btn btn-outline-primary w-100 mt-auto">Consultar programa</a>
                                        </div>
                                    </article>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:if test="${empty error}">
                            <div class="content-card p-5 text-center">
                                <i class="bi bi-journal-x display-3 text-muted mb-3 d-block" aria-hidden="true"></i>
                                <h2 class="h5 fw-bold">Todavía no hay programas de formación</h2>
                                <p class="text-muted mb-0">Los programas registrados aparecerán listados en este apartado.</p>
                            </div>
                        </c:if>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </main>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
