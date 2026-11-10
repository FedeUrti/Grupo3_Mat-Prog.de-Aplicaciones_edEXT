<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">
    <meta name="theme-color" content="#f5f9ff">
    <title>Iniciar sesión · edEXT Mobile</title>
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
<body class="mobile-login-page">
<div class="mobile-login-orb mobile-login-orb-one" aria-hidden="true"></div>
<div class="mobile-login-orb mobile-login-orb-two" aria-hidden="true"></div>
<div class="mobile-login-orb mobile-login-orb-three" aria-hidden="true"></div>

<main class="mobile-login-shell">
    <section class="mobile-login-card" aria-labelledby="loginTitle">
        <div class="mobile-login-top">
            <a class="brand mobile-login-brand" href="${pageContext.request.contextPath}/login"
               aria-label="edEXT Mobile">
                <span class="brand-mark">e</span><span>edEXT <strong>Mobile</strong></span>
            </a>
            <button type="button" class="theme-toggle login-theme-toggle" data-theme-toggle
                    aria-label="Cambiar a modo oscuro">
                <svg class="theme-icon theme-icon-sun" viewBox="0 0 24 24" aria-hidden="true">
                    <circle cx="12" cy="12" r="4"></circle>
                    <path d="M12 2v2m0 16v2M4.93 4.93l1.42 1.42m11.3 11.3 1.42 1.42M2 12h2m16 0h2M4.93 19.07l1.42-1.42m11.3-11.3 1.42-1.42"></path>
                </svg>
                <svg class="theme-icon theme-icon-moon" viewBox="0 0 24 24" aria-hidden="true">
                    <path d="M20.9 13A9 9 0 0 1 11 3.1 9 9 0 1 0 20.9 13Z"></path>
                </svg>
                <span data-theme-label>Modo oscuro</span>
            </button>
        </div>

        <div class="mobile-login-avatar" aria-hidden="true">
            <svg viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
            </svg>
        </div>

        <header class="mobile-login-heading">
            <p class="eyebrow">Acceso para estudiantes</p>
            <h1 id="loginTitle">Iniciar sesión</h1>
            <p class="muted">Usá tu cuenta de estudiante para explorar cursos y ediciones.</p>
        </header>

        <c:if test="${not empty error}">
            <div class="notice notice-error" role="alert"><c:out value="${error}"/></div>
        </c:if>
        <c:if test="${param.cerrada eq '1'}">
            <div class="notice notice-success" role="status">Cerraste sesión correctamente.</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/login"
              class="mobile-login-form" autocomplete="on">
            <div class="mobile-login-input-group">
                <label class="visually-hidden" for="identificador">Correo o nickname</label>
                <svg class="mobile-login-input-icon" viewBox="0 0 24 24" fill="none"
                     stroke="currentColor" stroke-width="1.8" aria-hidden="true">
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                    <circle cx="12" cy="7" r="4"/>
                </svg>
                <input id="identificador" name="identificador" type="text"
                       autocomplete="username" placeholder="Correo o nickname" required>
            </div>

            <div class="mobile-login-input-group">
                <label class="visually-hidden" for="password">Contraseña</label>
                <svg class="mobile-login-input-icon" viewBox="0 0 24 24" fill="none"
                     stroke="currentColor" stroke-width="1.8" aria-hidden="true">
                    <rect x="4" y="10" width="16" height="11" rx="2"/>
                    <path d="M8 10V7a4 4 0 0 1 8 0v3"/>
                </svg>
                <input id="password" name="password" type="password"
                       autocomplete="current-password" placeholder="Contraseña" required>
            </div>

            <button type="submit" class="mobile-login-button">Ingresar</button>
        </form>

        <p class="mobile-login-footnote">Consulta de cursos y ediciones desde tu móvil.</p>
    </section>
</main>
<script src="${pageContext.request.contextPath}/assets/theme.js?v=20261110-1" defer></script>
</body>
</html>
