<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">
    <meta name="theme-color" content="#f4f6fb">
    <title>Iniciar sesión · edEXT Mobile</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/mobile.css">
</head>
<body class="login-page">
<main class="login-shell">
    <a class="brand login-brand" href="${pageContext.request.contextPath}/login">
        <span class="brand-mark">e</span><span>edEXT <strong>Mobile</strong></span>
    </a>
    <section class="login-card">
        <p class="eyebrow">Acceso para estudiantes</p>
        <h1>Iniciar sesión</h1>
        <p class="muted">Usá la cuenta de estudiante que ya registraste en edEXT.</p>
        <c:if test="${not empty error}">
            <div class="notice notice-error" role="alert"><c:out value="${error}"/></div>
        </c:if>
        <c:if test="${param.cerrada eq '1'}">
            <div class="notice notice-success" role="status">Cerraste sesión correctamente.</div>
        </c:if>
        <form method="post" action="${pageContext.request.contextPath}/login" class="form-stack">
            <label for="identificador">Correo o nickname</label>
            <input id="identificador" name="identificador" type="text" autocomplete="username"
                   placeholder="tu correo o nickname" required>
            <label for="password">Contraseña</label>
            <input id="password" name="password" type="password" autocomplete="current-password"
                   placeholder="Tu contraseña" required>
            <button class="button button-primary button-wide" type="submit">Ingresar</button>
        </form>
    </section>
    <p class="login-footnote">Consulta de cursos y ediciones desde tu móvil.</p>
</main>
</body>
</html>
