<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Mi cuenta | edEXT</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <link href="assets/css/edext.css" rel="stylesheet">
</head>
<body>
<!-- La navegación ofrece cerrar sesión mediante POST para invalidar la sesión actual. -->
<header class="custom-header py-2 px-4 shadow-sm">
    <div class="container-fluid d-flex align-items-center justify-content-between">
        <a href="./" class="text-decoration-none" aria-label="Inicio edEXT">
            <div class="logo-circle">edEXT</div>
        </a>
        <nav class="d-flex align-items-center gap-2" aria-label="Navegación principal">
            <a href="cursos" class="btn btn-outline-light">Cursos</a>
            <form action="logout" method="post" class="m-0">
                <button class="btn btn-light text-dark" type="submit">Cerrar sesión</button>
            </form>
        </nav>
    </div>
</header>

<!-- El servlet carga un DtUsuario sin exponer la contraseña ni otros datos internos. -->
<main class="container my-4 my-lg-5">
    <p class="text-uppercase fw-bold small text-muted mb-1">Cuenta personal</p>
    <h1 class="page-heading fw-bold mb-4">Mi cuenta</h1>

    <section class="content-card p-4 p-md-5" aria-label="Datos del usuario">
        <div class="d-flex align-items-center gap-3 mb-4">
            <div class="logo-circle"><i class="bi bi-person-fill" aria-hidden="true"></i></div>
            <div>
                <h2 class="h4 fw-bold mb-1"><c:out value="${usuario.nombre}"/> <c:out value="${usuario.apellido}"/></h2>
                <p class="text-muted mb-0">@<c:out value="${usuario.nickname}"/></p>
            </div>
        </div>
        <dl class="row mb-0">
            <dt class="col-sm-4">Correo</dt>
            <dd class="col-sm-8"><c:out value="${usuario.correo}"/></dd>
            <dt class="col-sm-4">Fecha de nacimiento</dt>
            <dd class="col-sm-8"><c:out value="${usuario.fechaNacimiento}"/></dd>
            <dt class="col-sm-4">Seguidores</dt>
            <dd class="col-sm-8"><c:out value="${usuario.seguidores.size()}"/></dd>
            <dt class="col-sm-4">Siguiendo</dt>
            <dd class="col-sm-8"><c:out value="${usuario.seguidos.size()}"/></dd>
        </dl>
    </section>

    <div class="mt-4">
        <a href="cursos" class="btn btn-dark">Explorar cursos</a>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
