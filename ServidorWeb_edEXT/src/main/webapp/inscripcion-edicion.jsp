<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Inscripción a Edición de Curso | edEXT</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <link href="assets/css/edext.css" rel="stylesheet">
</head>
<body>

<header class="custom-header py-2 px-4 shadow-sm">
    <div class="container-fluid d-flex align-items-center justify-content-between gap-3">
        <a href="inicio" class="text-decoration-none" aria-label="Inicio edEXT">
            <div class="logo-circle">edEXT</div>
        </a>
        <nav class="d-flex gap-2">
            <a href="cursos" class="btn btn-outline-light">Cursos</a>
            <a href="mi-cuenta" class="btn btn-outline-light">Mi cuenta</a>
            <form action="logout" method="post" class="d-inline">
                <button class="btn btn-light text-dark" type="submit">Cerrar sesión</button>
            </form>
        </nav>
    </div>
</header>

<main class="container my-4 my-lg-5">
    
    <div class="d-flex flex-wrap align-items-end justify-content-between gap-3 mb-4">
        <div>
            <a href="inicio" class="text-decoration-none text-muted">
                <i class="bi bi-arrow-left me-1" aria-hidden="true"></i> Volver al inicio
            </a>
            <p class="text-uppercase fw-bold small text-muted mb-1">Inscripciones</p>
            <h1 class="page-heading fw-bold mb-1">Inscripción a Edición de Curso</h1>
            <p class="text-muted mb-0">Seleccioná un instituto o categoría para consultar los cursos y sus ediciones vigentes.</p>
        </div>
    </div>

    <!-- Mensajes de Estado -->
    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show mb-4" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2"></i>
            <c:out value="${error}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar"></button>
        </div>
    </c:if>
    
    <c:if test="${not empty mensajeExito}">
        <div class="alert alert-success alert-dismissible fade show mb-4" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i>
            <c:out value="${mensajeExito}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar"></button>
        </div>
    </c:if>

    <c:if test="${not empty mensajeInfo}">
        <div class="alert alert-info alert-dismissible fade show mb-4" role="alert">
            <i class="bi bi-info-circle-fill me-2"></i>
            <c:out value="${mensajeInfo}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar"></button>
        </div>
    </c:if>

    <div class="row g-4">
        <!-- Panel Izquierdo: Selección de Filtro y Cursos por GET -->
        <aside class="col-lg-4">
            <div class="content-card p-4">
                <h2 class="h6 text-uppercase fw-bold text-muted mb-3">1. Filtrar Cursos</h2>

                <form action="inscripcion-edicion" method="get" class="mb-3">
                    <label for="selectInstituto" class="form-label fw-semibold">Por Instituto</label>
                    <select class="form-select mb-2" id="selectInstituto" name="instituto" onchange="this.form.submit()">
                        <option value="">-- Seleccionar Instituto --</option>
                        <c:forEach items="${institutos}" var="inst">
                            <option value="<c:out value='${inst}'/>" <c:if test="${inst eq filtroInstituto}">selected</c:if>>
                                <c:out value="${inst}"/>
                            </option>
                        </c:forEach>
                    </select>
                </form>

                <form action="inscripcion-edicion" method="get" class="mb-3">
                    <label for="selectCategoria" class="form-label fw-semibold">Por Categoría</label>
                    <select class="form-select mb-2" id="selectCategoria" name="categoria" onchange="this.form.submit()">
                        <option value="">-- Seleccionar Categoría --</option>
                        <c:forEach items="${categorias}" var="cat">
                            <option value="<c:out value='${cat}'/>" <c:if test="${cat eq filtroCategoria}">selected</c:if>>
                                <c:out value="${cat}"/>
                            </option>
                        </c:forEach>
                    </select>
                </form>

                <c:if test="${not empty cursos}">
                    <hr class="my-4">
                    <h2 class="h6 text-uppercase fw-bold text-muted mb-3">2. Seleccionar Curso</h2>
                    <form action="inscripcion-edicion" method="get">
                        <c:if test="${not empty filtroInstituto}">
                            <input type="hidden" name="instituto" value="<c:out value='${filtroInstituto}'/>"/>
                        </c:if>
                        <c:if test="${not empty filtroCategoria}">
                            <input type="hidden" name="categoria" value="<c:out value='${filtroCategoria}'/>"/>
                        </c:if>
                        <select class="form-select mb-3" name="curso" onchange="this.form.submit()">
                            <option value="">-- Seleccionar Curso --</option>
                            <c:forEach items="${cursos}" var="c">
                                <option value="<c:out value='${c}'/>" <c:if test="${c eq cursoSeleccionado}">selected</c:if>>
                                    <c:out value="${c}"/>
                                </option>
                            </c:forEach>
                        </select>
                    </form>
                </c:if>
            </div>
        </aside>

        <!-- Panel Derecho: Edición Vigente y Formulario de Inscripción -->
        <section class="col-lg-8">
            <c:choose>
                <c:when test="${not empty edicionVigente}">
                    <article class="content-card p-4 p-md-5">
                        <span class="badge text-bg-primary mb-2"><c:out value="${cursoSeleccionado}"/></span>
                        <h2 class="h3 fw-bold mb-3"><c:out value="${edicionVigente.nombre}"/></h2>

                        <dl class="row mb-4">
                            <dt class="col-sm-4 text-muted fw-semibold">Fecha de Inicio:</dt>
                            <dd class="col-sm-8"><c:out value="${edicionVigente.fechaInicio}"/></dd>

                            <dt class="col-sm-4 text-muted fw-semibold">Fecha de Fin:</dt>
                            <dd class="col-sm-8"><c:out value="${edicionVigente.fechaFin}"/></dd>

                            <dt class="col-sm-4 text-muted fw-semibold">Cupos Disponibles:</dt>
                            <dd class="col-sm-8">
                                <c:choose>
                                    <c:when test="${edicionVigente.cupo > 0}">
                                        <span class="badge ${edicionVigente.cupoDisponible > 0 ? 'bg-success' : 'bg-danger'}">
                                            <c:out value="${edicionVigente.cupoDisponible}"/> de <c:out value="${edicionVigente.cupo}"/>
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-success fw-semibold">Ilimitado</span>
                                    </c:otherwise>
                                </c:choose>
                            </dd>
                        </dl>

                        <!-- Evaluación del Estado de Inscripción -->
                        <c:choose>
                            <c:when test="${yaInscripto}">
                                <div class="card border-warning bg-light p-3 mb-4">
                                    <h3 class="h6 fw-bold text-warning-emphasis mb-2">
                                        <i class="bi bi-info-circle me-1"></i> Ya estás inscripto a esta edición
                                    </h3>
                                    <p class="small text-muted mb-0">
                                        Actualmente te encontrás registrado en la edición <strong><c:out value="${edicionVigente.nombre}"/></strong>.
                                    </p>
                                </div>
                                <a href="inscripcion-edicion" class="btn btn-outline-secondary">
                                    <i class="bi bi-arrow-repeat me-1"></i> Seleccionar otro curso (Descartar)
                                </a>
                            </c:when>

                            <c:otherwise>
                                <form action="inscripcion-edicion" method="post">
                                    <input type="hidden" name="edicion" value="<c:out value='${edicionVigente.nombre}'/>"/>
                                    <input type="hidden" name="curso" value="<c:out value='${cursoSeleccionado}'/>"/>
                                    <c:if test="${not empty filtroInstituto}">
                                        <input type="hidden" name="instituto" value="<c:out value='${filtroInstituto}'/>"/>
                                    </c:if>
                                    <c:if test="${not empty filtroCategoria}">
                                        <input type="hidden" name="categoria" value="<c:out value='${filtroCategoria}'/>"/>
                                    </c:if>

                                    <c:if test="${edicionVigente.cupo == 0 or edicionVigente.cupoDisponible > 0}">
                                        <button type="submit" class="btn btn-primary btn-lg w-100">
                                            <i class="bi bi-check-lg me-1"></i> Confirmar Inscripción
                                        </button>
                                    </c:if>

                                    <c:if test="${edicionVigente.cupo > 0 and edicionVigente.cupoDisponible <= 0}">
                                        <button type="button" class="btn btn-secondary btn-lg w-100" disabled>
                                            Cupo Agotado
                                        </button>
                                    </c:if>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </article>
                </c:when>

                <c:otherwise>
                    <div class="content-card p-5 text-center">
                        <i class="bi bi-journal-check display-4 text-muted mb-3"></i>
                        <h2 class="h5 fw-bold">Inscripción a Cursos</h2>
                        <p class="text-muted mb-0">Seleccioná un instituto o categoría y elegí un curso para verificar si posee ediciones vigentes e inscribirte.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>