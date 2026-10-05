<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>edEXT - Plataforma de Extensión Universitaria</title>
    <!-- Bootstrap 5 CSS CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <link href="assets/css/edext.css" rel="stylesheet">
</head>
<body>

    <!-- 1. CABEZAL: búsqueda, navegación a cursos y estado de sesión del usuario. -->
    <header class="custom-header py-2 px-4 shadow-sm">
        <div class="container-fluid d-flex align-items-center justify-content-between">
            
            <!-- Logo edEXT -->
            <a href="inicio" class="text-decoration-none">
                <div class="logo-circle">
                    edEXT
                </div>
            </a>

            <!-- Buscador Universal con Formato de Píldora -->
            <div class="w-50 mx-3 search-area">
                <form action="cursos" method="GET">
                    <div class="input-group">
                        <span class="input-group-text bg-white border-end-0 rounded-start-pill ps-3 text-muted">
                            <i class="bi bi-search"></i>
                        </span>
                        <input type="text" name="query" class="form-control border-start-0 border-end-0 py-2" placeholder="Cursos, Programas..." aria-label="Buscar">
                        <span class="input-group-text bg-white border-start-0 rounded-end-pill pe-3 text-muted">
                            <i class="bi bi-mic-fill"></i>
                        </span>
                    </div>
                </form>
            </div>

            <!-- La barra cambia sus acciones según exista una sesión autenticada. -->
            <div class="d-flex gap-2">
                <a href="cursos" class="btn btn-outline-light px-3 py-1 fw-semibold">Cursos</a>
                <c:choose>
                    <c:when test="${not empty sessionScope.usuarioNickname}">
                        <c:if test="${sessionScope.usuarioDocente}">
                            <a href="alta-curso" class="btn btn-light text-dark px-3 py-1 fw-semibold">Alta de curso</a>
                        </c:if>
                        <a href="mi-cuenta" class="btn btn-outline-light px-3 py-1 fw-semibold">Mi cuenta</a>
                        <form action="logout" method="post">
                            <button class="btn btn-light text-dark px-3 py-1 fw-semibold" type="submit">Cerrar sesión</button>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <a href="login" class="btn btn-light text-dark px-3 py-1 fw-semibold">Iniciar sesión</a>
                    </c:otherwise>
                </c:choose>
            </div>

        </div>
    </header>

    <!-- 2. CUERPO: filtros y cursos existentes cargados por InicioServlet. -->
    <div class="container-fluid my-4 px-4">
        <div class="row g-4">

            <!-- COLUMNA IZQUIERDA: MENÚ LATERAL (INSTITUTOS Y CATEGORÍAS) -->
            <div class="col-md-3 col-lg-2">
                <div class="bg-white p-3 rounded-3 shadow-sm">

                    <!-- SECCIÓN INSTITUTOS -->
                    <div class="sidebar-title text-uppercase">INSTITUTOS</div>
                    <div class="list-group list-group-flush">
                        <c:forEach items="${institutos}" var="instituto">
                            <c:url var="filtroInstitutoUrl" value="/cursos">
                                <c:param name="instituto" value="${instituto}"/>
                            </c:url>
                            <a href="<c:out value='${filtroInstitutoUrl}'/>"
                               class="list-group-item list-group-item-action custom-item d-flex align-items-center gap-2">
                                <i class="bi bi-building" aria-hidden="true"></i> <c:out value="${instituto}"/>
                            </a>
                        </c:forEach>
                    </div>

                    <c:if test="${not empty categorias}">
                        <hr class="my-3 text-muted">

                        <!-- SECCIÓN CATEGORÍAS: solo se muestran las que existen en la base. -->
                        <div class="sidebar-title text-uppercase">CATEGORÍAS</div>
                        <div class="list-group list-group-flush">
                            <c:forEach items="${categorias}" var="categoria">
                                <c:url var="filtroCategoriaUrl" value="/cursos">
                                    <c:param name="categoria" value="${categoria}"/>
                                </c:url>
                                <a href="<c:out value='${filtroCategoriaUrl}'/>"
                                   class="list-group-item list-group-item-action custom-item d-flex align-items-center gap-2">
                                    <i class="bi bi-bookmark" aria-hidden="true"></i> <c:out value="${categoria}"/>
                                </a>
                            </c:forEach>
                        </div>
                    </c:if>

                </div>
            </div>

            <!-- COLUMNA DERECHA: tarjetas construidas únicamente con cursos de la base. -->
            <div class="col-md-9 col-lg-10">
                <c:if test="${not empty error}">
                    <div class="alert alert-danger" role="alert"><c:out value="${error}"/></div>
                </c:if>
                <c:if test="${empty error and empty cursos}">
                    <div class="content-card p-4 text-center">
                        <h2 class="h5 fw-bold">Todavía no hay cursos</h2>
                        <p class="text-muted mb-0">Los cursos aparecerán aquí cuando se carguen en el sistema.</p>
                    </div>
                </c:if>
                <div class="d-flex flex-column gap-3">
                    <c:forEach items="${cursos}" var="curso">
                        <article class="card course-card p-3">
                            <div class="row align-items-center g-3">
                                <div class="col-md-4 text-center">
                                    <c:choose>
                                        <c:when test="${not empty curso.imagenPath}">
                                            <c:url var="imagenUrl" value="/imagenes-curso">
                                                <c:param name="nombre" value="${curso.imagenPath}"/>
                                            </c:url>
                                            <img src="<c:out value='${imagenUrl}'/>" class="img-fluid rounded"
                                                 alt="Imagen de <c:out value='${curso.nombre}'/>">
                                        </c:when>
                                        <c:otherwise>
                                            <div class="course-image-placeholder">
                                                <i class="bi bi-mortarboard display-5" aria-hidden="true"></i>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="col-md-8">
                                    <span class="badge text-bg-primary mb-2"><c:out value="${curso.nomInstituto}"/></span>
                                    <h2 class="h5 fw-bold"><c:out value="${curso.nombre}"/></h2>
                                    <p class="card-text text-dark mb-2"><c:out value="${curso.descripcion}"/></p>
                                    <c:url var="detalleUrl" value="/consulta-curso">
                                        <c:param name="nombre" value="${curso.nombre}"/>
                                    </c:url>
                                    <a href="<c:out value='${detalleUrl}'/>"
                                       class="btn btn-read-more text-decoration-none">Ver curso</a>
                                </div>
                            </div>
                        </article>
                    </c:forEach>
                </div>
            </div>

        </div>
    </div>

    <!-- Bootstrap 5 JS Bundle CDN -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>