<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Iniciar sesión | edEXT</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <c:url var="estilosUrl" value="/assets/css/edext.css">
        <c:param name="v" value="20261012-6"/>
    </c:url>
    <link href="<c:out value='${estilosUrl}'/>" rel="stylesheet">
</head>
<body>
<!-- También se puede buscar contenido desde la pantalla de inicio de sesión. -->
<%@ include file="/WEB-INF/views/buscador-global.jspf" %>

<!-- El formulario envía credenciales por POST; el servlet inicia o rechaza la sesión. -->
<main class="container my-5">
    <div class="row justify-content-center">
        <section class="col-sm-10 col-md-7 col-lg-5">
            <div class="content-card p-4 p-md-5">
                <div class="text-center mb-4">
                    <div class="course-image-placeholder login-avatar-placeholder mx-auto mb-3">
                        <i class="bi bi-person-circle fs-2" aria-hidden="true"></i>
                    </div>
                    <h1 class="page-heading h3 fw-bold">Iniciar sesión</h1>
                    <p class="text-muted mb-0">Ingresá con tu nickname o correo electrónico.</p>
                </div>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger" role="alert"><c:out value="${error}"/></div>
                </c:if>
                <c:if test="${param.motivo eq 'docente'}">
                    <div class="alert alert-info" role="status">
                        Para dar de alta cursos, iniciá sesión con una cuenta docente.
                    </div>
                </c:if>
                <c:if test="${param.motivo eq 'inscripcion'}">
                    <div class="alert alert-info" role="status">
                        Para inscribirte a una edición, iniciá sesión con una cuenta de estudiante.
                    </div>
                </c:if>

                <!-- Se admite nickname o correo, igual que en el controlador central. -->
                <form action="login" method="post">
                    <div class="mb-3">
                        <label class="form-label" for="identificador">Nickname o correo</label>
                        <input class="form-control" id="identificador" name="identificador" type="text"
                               autocomplete="username" value="<c:out value='${param.identificador}'/>" required>
                    </div>
                    <div class="mb-4">
                        <label class="form-label" for="password">Contraseña</label>
                        <input class="form-control" id="password" name="password" type="password"
                               autocomplete="current-password" required>
                    </div>
                    <button class="btn btn-dark w-100 py-2" type="submit">Entrar</button>
                </form>

                <p class="small text-muted text-center mt-4 mb-0">
                    ¿Aún no tenés una cuenta?
                    <c:url var="altaUsuarioUrl" value="/alta-usuario"/>
                    <a href="<c:out value='${altaUsuarioUrl}'/>">Registrate acá</a>.
                </p>
            </div>
        </section>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
