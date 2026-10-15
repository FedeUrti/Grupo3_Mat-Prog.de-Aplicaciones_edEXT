<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Modificar mis datos | edEXT</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <c:url var="estilosUrl" value="/assets/css/edext.css">
        <c:param name="v" value="20261015-1"/>
    </c:url>
    <link href="<c:out value='${estilosUrl}'/>" rel="stylesheet">
</head>
<body>
<%@ include file="/WEB-INF/views/buscador-global.jspf" %>
<main class="container py-4 py-lg-5 usuario-formulario">
    <div class="row justify-content-center">
        <section class="col-lg-7">
            <div class="content-card p-4 p-md-5">
                <h1 class="page-heading h2 fw-bold mb-2">Modificar mis datos</h1>
                <p class="text-muted mb-4">El nickname y el correo no se pueden modificar desde esta pantalla.</p>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger" role="alert"><c:out value="${error}"/></div>
                </c:if>

                <%-- La sesión determina qué cuenta se actualiza; no se acepta un nickname editable en el formulario. --%>
                <%@ include file="formulario-modificar-usuario.jspf" %>
            </div>
        </section>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
