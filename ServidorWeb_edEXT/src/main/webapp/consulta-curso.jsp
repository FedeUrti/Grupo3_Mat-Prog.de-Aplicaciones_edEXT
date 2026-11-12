<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Detalle del curso | edEXT</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        .rating-stars { display: flex; justify-content: flex-start; }
        .rating-stars button { color: #adb5bd; text-decoration: none; }
        .rating-stars button.selected, .rating-stars button:hover { color: #ffc107; }
    </style>
    <c:url var="estilosUrl" value="/assets/css/edext.css">
        <c:param name="v" value="20261015-2"/>
    </c:url>
    <link href="<c:out value='${estilosUrl}'/>" rel="stylesheet">
</head>
<body>
<!-- Navegación compartida y búsqueda en todos los tipos de contenido. -->
<%@ include file="/WEB-INF/views/buscador-global.jspf" %>
<c:url var="inicioUrl" value="/inicio"/>

<!-- El servlet consulta el nombre solicitado y expone el DTO como atributo "curso". -->
<main class="container-fluid page-layout py-4 px-3 px-lg-4">
    <div class="row g-4 align-items-start">
        <%@ include file="/WEB-INF/views/sidebar-principal.jspf" %>
        <div class="col main-content-column">
    <a href="<c:out value='${inicioUrl}'/>" class="text-decoration-none text-muted">
        <i class="bi bi-arrow-left me-1" aria-hidden="true"></i> Volver al inicio
    </a>

    <c:if test="${not empty error}">
        <div class="alert alert-danger mt-3" role="alert"><c:out value="${error}"/></div>
    </c:if>
    <c:if test="${param.registrado eq '1'}">
        <div class="alert alert-success mt-3" role="status">El curso se registró correctamente.</div>
    </c:if>
    <c:if test="${param.exitoEdicion eq 'true'}">
        <div class="alert alert-success mt-3" role="status">La edición del curso se registró correctamente.</div>
    </c:if>
    <c:if test="${not empty mensajeValoracion}">
        <div class="alert alert-success mt-3" role="status"><c:out value="${mensajeValoracion}"/></div>
    </c:if>
    <c:if test="${not empty errorValoracion}">
        <div class="alert alert-danger mt-3" role="alert"><c:out value="${errorValoracion}"/></div>
    </c:if>

    <c:choose>
        <c:when test="${not empty curso}">
            <div class="row g-4 mt-2">
                <section class="col-lg-8">
                    <article class="content-card p-4 p-md-5 course-detail-card">
                        <span class="badge text-bg-primary mb-3"><c:out value="${curso.nomInstituto}"/></span>
                        <h1 class="page-heading fw-bold"><c:out value="${curso.nombre}"/></h1>
                        <p class="lead text-muted"><c:out value="${curso.descripcion}"/></p>
                        <div class="d-flex flex-wrap gap-2">
                            <c:if test="${sessionScope.usuarioTipo eq 'Estudiante'}">
                                <c:url var="inscripcionCursoUrl" value="/inscripcion-edicion">
                                    <c:param name="curso" value="${curso.nombre}"/>
                                </c:url>
                                <a class="btn btn-primary" href="<c:out value='${inscripcionCursoUrl}'/>">
                                    <i class="bi bi-pencil-square me-1" aria-hidden="true"></i> Inscribirme a una edición vigente
                                </a>
                            </c:if>
                            <c:if test="${sessionScope.usuarioDocente}">
                                <c:url var="altaEdicionCursoUrl" value="/alta-edicion-curso">
                                    <c:param name="curso" value="${curso.nombre}"/>
                                    <c:param name="instituto" value="${curso.nomInstituto}"/>
                                </c:url>
                                <a class="btn btn-outline-primary" href="<c:out value='${altaEdicionCursoUrl}'/>">
                                    <i class="bi bi-calendar-plus me-1" aria-hidden="true"></i> Crear edición
                                </a>
                            </c:if>
                        </div>
                        <hr>
                        <%-- Las relaciones del curso van en pestañas propias para mantener visible y ordenada la información general. --%>
                        <ul class="nav nav-tabs course-detail-tabs mb-4" role="tablist">
                            <li class="nav-item" role="presentation">
                                <button class="nav-link active" id="curso-info-tab" data-bs-toggle="tab"
                                        data-bs-target="#curso-info-panel" type="button" role="tab"
                                        aria-controls="curso-info-panel" aria-selected="true">
                                    Información general
                                </button>
                            </li>
                            <li class="nav-item" role="presentation">
                                <button class="nav-link" id="curso-ediciones-tab" data-bs-toggle="tab"
                                        data-bs-target="#curso-ediciones-panel" type="button" role="tab"
                                        aria-controls="curso-ediciones-panel" aria-selected="false">
                                    Ediciones <span class="badge text-bg-light"><c:out value="${curso.ediciones.size()}"/></span>
                                </button>
                            </li>
                            <li class="nav-item" role="presentation">
                                <button class="nav-link" id="curso-programas-tab" data-bs-toggle="tab"
                                        data-bs-target="#curso-programas-panel" type="button" role="tab"
                                        aria-controls="curso-programas-panel" aria-selected="false">
                                    Programas <span class="badge text-bg-light"><c:out value="${curso.programas.size()}"/></span>
                                </button>
                            </li>
                        </ul>
                        <div class="tab-content">
                            <div class="tab-pane fade show active" id="curso-info-panel" role="tabpanel"
                                 aria-labelledby="curso-info-tab" tabindex="0">
                                <h2 class="h5 fw-bold">Información del curso</h2>
                                <dl class="row mb-0">
                                    <dt class="col-sm-4">Duración</dt>
                                    <dd class="col-sm-8"><c:out value="${curso.duracion}"/> meses</dd>
                                    <dt class="col-sm-4">Cantidad de horas</dt>
                                    <dd class="col-sm-8"><c:out value="${curso.cantHoras}"/></dd>
                                    <dt class="col-sm-4">Créditos</dt>
                                    <dd class="col-sm-8"><c:out value="${curso.creditos}"/></dd>
                                    <dt class="col-sm-4">Visitas</dt>
                                    <dd class="col-sm-8"><c:out value="${curso.visitas}"/></dd>
                                    <dt class="col-sm-4">Fecha de alta</dt>
                                    <dd class="col-sm-8"><c:out value="${curso.fecha}"/></dd>
                                    <dt class="col-sm-4">Categorías</dt>
                                    <dd class="col-sm-8">
                                        <c:choose>
                                            <c:when test="${not empty curso.categorias}">
                                                <div class="course-detail-tags">
                                                    <c:forEach items="${curso.categorias}" var="categoria">
                                                        <%-- c:url codifica el nombre de la categoría como parámetro de filtro. --%>
                                                        <c:url var="categoriaUrl" value="/inicio">
                                                            <c:param name="categoria" value="${categoria}"/>
                                                        </c:url>
                                                        <a class="badge rounded-pill text-bg-primary text-decoration-none"
                                                           href="<c:out value='${categoriaUrl}'/>">
                                                            <c:out value="${categoria}"/>
                                                        </a>
                                                    </c:forEach>
                                                </div>
                                            </c:when>
                                            <c:otherwise><span class="text-muted">Sin categorías</span></c:otherwise>
                                        </c:choose>
                                    </dd>
                                    <dt class="col-sm-4">Cursos previos</dt>
                                    <dd class="col-sm-8">
                                        <c:choose>
                                            <c:when test="${not empty curso.previas}">
                                                <div class="course-detail-tags">
                                                    <c:forEach items="${curso.previas}" var="previa">
                                                        <%-- Las previas enlazan al detalle de curso usando su nombre como parámetro. --%>
                                                        <c:url var="cursoPreviaUrl" value="/consulta-curso">
                                                            <c:param name="nombre" value="${previa}"/>
                                                        </c:url>
                                                        <a class="badge rounded-pill text-bg-light border text-decoration-none"
                                                           href="<c:out value='${cursoPreviaUrl}'/>">
                                                            <i class="bi bi-arrow-up-right me-1" aria-hidden="true"></i>
                                                            <c:out value="${previa}"/>
                                                        </a>
                                                    </c:forEach>
                                                </div>
                                            </c:when>
                                            <c:otherwise><span class="text-muted">No requiere cursos previos</span></c:otherwise>
                                        </c:choose>
                                    </dd>
                                    <dt class="col-sm-4">URL asociada</dt>
                                    <dd class="col-sm-8 text-break"><c:out value="${curso.url}"/></dd>
                                </dl>
                            </div>
                            <div class="tab-pane fade" id="curso-ediciones-panel" role="tabpanel"
                                 aria-labelledby="curso-ediciones-tab" tabindex="0">
                                <h2 class="h5 fw-bold mb-3">Ediciones del curso</h2>
                                <c:choose>
                                    <c:when test="${not empty edicionesCurso}">
                                        <ul class="list-group list-group-flush">
                                            <c:forEach items="${edicionesCurso}" var="edicion" varStatus="estadoEdicion">
                                                <c:url var="paginaEdicionUrl" value="/consulta-edicion-curso">
                                                    <c:param name="curso" value="${curso.nombre}"/>
                                                    <c:param name="edicion" value="${edicion.nombre}"/>
                                                </c:url>
                                                <li class="list-group-item d-flex flex-wrap align-items-center justify-content-between gap-2">
                                                    <span><i class="bi bi-calendar3 text-primary me-2" aria-hidden="true"></i>
                                                        <c:out value="${edicion.nombre}"/></span>
                                                    <button class="btn btn-sm btn-outline-primary" type="button"
                                                            data-bs-toggle="modal"
                                                            data-bs-target="#detalle-edicion-${estadoEdicion.index}">
                                                        Consultar edición
                                                    </button>
                                                <div class="modal fade" id="detalle-edicion-${estadoEdicion.index}" tabindex="-1"
                                                     aria-labelledby="titulo-edicion-${estadoEdicion.index}" aria-hidden="true">
                                                    <div class="modal-dialog modal-dialog-centered">
                                                        <div class="modal-content">
                                                            <div class="modal-header">
                                                                <div>
                                                                    <p class="text-muted small mb-1"><c:out value="${curso.nombre}"/></p>
                                                                    <h2 class="modal-title fs-5 fw-bold" id="titulo-edicion-${estadoEdicion.index}">
                                                                        <c:out value="${edicion.nombre}"/>
                                                                    </h2>
                                                                </div>
                                                                <button class="btn-close" type="button" data-bs-dismiss="modal"
                                                                        aria-label="Cerrar"></button>
                                                            </div>
                                                            <div class="modal-body">
                                                                <dl class="row mb-0">
                                                                    <dt class="col-6">Inicio</dt>
                                                                    <dd class="col-6"><c:out value="${edicion.fechaInicio}"/></dd>
                                                                    <dt class="col-6">Finalización</dt>
                                                                    <dd class="col-6"><c:out value="${edicion.fechaFin}"/></dd>
                                                                    <dt class="col-6">Publicación</dt>
                                                                    <dd class="col-6"><c:out value="${edicion.fechaPublicacion}"/></dd>
                                                                    <dt class="col-6">Cupos</dt>
                                                                    <dd class="col-6">
                                                                        <c:choose>
                                                                            <c:when test="${edicion.cupo gt 0}">
                                                                                <c:out value="${edicion.cupoDisponible}"/> de
                                                                                <c:out value="${edicion.cupo}"/> disponibles
                                                                            </c:when>
                                                                            <c:otherwise>Ilimitados</c:otherwise>
                                                                        </c:choose>
                                                                    </dd>
                                                                    <dt class="col-12">Docentes</dt>
                                                                    <dd class="col-12 mb-0">
                                                                        <c:choose>
                                                                            <c:when test="${not empty docentesPorEdicion[edicion.nombre]}">
                                                                                <c:forEach items="${docentesPorEdicion[edicion.nombre]}" var="docente" varStatus="estadoDocente">
                                                                                    <c:if test="${not estadoDocente.first}">, </c:if>
                                                                                    <c:url var="perfilDocenteUrl" value="/consulta-usuario">
                                                                                        <c:param name="nickname" value="${docente.nickname}"/>
                                                                                    </c:url>
                                                                                    <c:url var="imagenDocenteUrl" value="/imagenes-usuario">
                                                                                        <c:param name="nombre" value="${imagenesDocentes[docente.nickname]}"/>
                                                                                    </c:url>
                                                                                    <a href="<c:out value='${perfilDocenteUrl}'/>">
                                                                                        <img src="<c:out value='${imagenDocenteUrl}'/>"
                                                                                             class="rounded-circle me-1" width="24" height="24"
                                                                                             style="object-fit: cover;"
                                                                                             alt="Foto de <c:out value='${docente.nombre}'/>">
                                                                                        <c:out value="${docente.nombre}"/> <c:out value="${docente.apellido}"/>
                                                                                    </a>
                                                                                </c:forEach>
                                                                            </c:when>
                                                                            <c:otherwise>No hay docentes asignados.</c:otherwise>
                                                                        </c:choose>
                                                                    </dd>
                                                                </dl>
                                                            </div>
                                                            <div class="modal-footer">
                                                                <a class="btn btn-primary" href="<c:out value='${paginaEdicionUrl}'/>">Ver página completa</a>
                                                                <button class="btn btn-outline-secondary" type="button"
                                                                        data-bs-dismiss="modal">Cerrar</button>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </c:when>
                                    <c:otherwise>
                                        <p class="text-muted mb-0">Este curso todavía no tiene ediciones asociadas.</p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="tab-pane fade" id="curso-programas-panel" role="tabpanel"
                                 aria-labelledby="curso-programas-tab" tabindex="0">
                                <h2 class="h5 fw-bold mb-3">Programas de formación</h2>
                                <c:choose>
                                    <c:when test="${not empty programasCurso}">
                                        <ul class="list-group list-group-flush">
                                            <c:forEach items="${programasCurso}" var="programa">
                                                <c:url var="detalleProgramaUrl" value="/consulta-programa">
                                                    <c:param name="nombre" value="${programa.nombre}"/>
                                                </c:url>
                                                <li class="list-group-item d-flex flex-wrap align-items-center justify-content-between gap-2 py-3">
                                                    <span>
                                                        <i class="bi bi-mortarboard text-primary me-2" aria-hidden="true"></i>
                                                        <c:out value="${programa.nombre}"/>
                                                    </span>
                                                    <a class="btn btn-sm btn-outline-primary"
                                                       href="<c:out value='${detalleProgramaUrl}'/>">Consultar programa</a>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </c:when>
                                    <c:otherwise>
                                        <p class="text-muted mb-0">Este curso todavía no pertenece a programas de formación.</p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </article>
                </section>
                <aside class="col-lg-4">
                    <div class="content-card p-3">
                        <c:choose>
                            <c:when test="${not empty curso.imagenPath}">
                                <c:url var="imagenCursoUrl" value="/imagenes-curso">
                                    <c:param name="nombre" value="${curso.imagenPath}"/>
                                </c:url>
                                <img src="<c:out value='${imagenCursoUrl}'/>"
                                     class="course-detail-image" alt="Imagen de <c:out value='${curso.nombre}'/>">
                            </c:when>
                            <c:otherwise>
                                <div class="course-image-placeholder">
                                    <i class="bi bi-image display-4" aria-hidden="true"></i>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <section class="content-card p-4 mt-4" id="valoraciones" aria-labelledby="valoraciones-titulo">
                        <h2 class="h5 fw-bold mb-3" id="valoraciones-titulo">Valoración general</h2>
                        <div class="row g-3 align-items-center">
                            <div class="col-7">
                                <c:forEach items="${conteosValoraciones}" var="conteo">
                                    <div class="d-flex align-items-center gap-2 mb-2">
                                        <span class="small text-nowrap"><c:out value="${conteo.key}"/> <i class="bi bi-star-fill text-warning" aria-hidden="true"></i></span>
                                        <div class="progress flex-grow-1" role="progressbar"
                                             aria-label="${conteo.key} estrellas"
                                             data-rating-progress-key="${conteo.key}"
                                             aria-valuenow="${conteo.value}" aria-valuemin="0"
                                             aria-valuemax="${cantidadValoraciones}">
                                            <c:choose>
                                                <c:when test="${cantidadValoraciones gt 0}">
                                                    <div class="progress-bar bg-warning"
                                                         data-rating-progress-bar="${conteo.key}"
                                                         style="width: ${conteo.value * 100 / cantidadValoraciones}%"></div>
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="progress-bar bg-warning"
                                                         data-rating-progress-bar="${conteo.key}" style="width: 0%"></div>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <span class="small text-muted text-end" style="min-width: 1.5rem"
                                              data-rating-breakdown-value="${conteo.key}">
                                            <c:out value="${conteo.value}"/>
                                        </span>
                                    </div>
                                </c:forEach>
                            </div>
                            <div class="col-5 text-center">
                                <div class="display-6 fw-bold" data-rating-average><c:out value="${promedioValoraciones}"/></div>
                                <div class="d-flex justify-content-center gap-1 my-1" data-rating-stars
                                     role="img" aria-label="Promedio visual de ${promedioEstrellas} de 5 estrellas">
                                    <c:forEach begin="1" end="5" var="estrella">
                                        <c:choose>
                                            <c:when test="${estrella le promedioEstrellas}">
                                                <i class="bi bi-star-fill text-warning" data-rating-star="${estrella}" aria-hidden="true"></i>
                                            </c:when>
                                            <c:when test="${promedioEstrellas ge estrella - 0.5}">
                                                <i class="bi bi-star-half text-warning" data-rating-star="${estrella}" aria-hidden="true"></i>
                                            </c:when>
                                            <c:otherwise>
                                                <i class="bi bi-star text-secondary" data-rating-star="${estrella}" aria-hidden="true"></i>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </div>
                                <div class="small text-muted">
                                    <span data-rating-count><c:out value="${cantidadValoraciones}"/></span>
                                    <c:out value="${cantidadValoraciones eq 1 ? ' voto' : ' votos'}"/>
                                </div>
                            </div>
                        </div>
                        <c:if test="${puedeValorarCurso}">
                            <hr>
                            <h3 class="h6 fw-bold">Tu valoración</h3>
                            <c:choose>
                                <c:when test="${not empty puntajePropio}">
                                    <div class="d-flex align-items-center gap-2" role="img"
                                         aria-label="Tu puntaje: ${puntajePropio} de 5 estrellas">
                                        <c:forEach begin="1" end="5" var="estrella">
                                            <i class="bi ${estrella le puntajePropio ? 'bi-star-fill text-warning' : 'bi-star text-secondary'}"
                                               aria-hidden="true"></i>
                                        </c:forEach>
                                        <span class="small text-muted"><c:out value="${puntajePropio}"/> / 5</span>
                                    </div>
                                    <p class="small text-muted mt-2 mb-0">Ya valoraste este curso.</p>
                                </c:when>
                                <c:otherwise>
                                    <p class="small text-muted mb-1">Elegí tus estrellas y confirmá el puntaje.</p>
                                    <form action="valorar-curso" method="post" class="rating-form"
                                          data-rating-form
                                          aria-label="Elegí tu puntaje de una a cinco estrellas">
                                        <input type="hidden" name="curso" value="<c:out value='${curso.nombre}'/>">
                                        <input type="hidden" name="puntaje" value="">
                                        <div class="rating-stars mb-2" role="group"
                                             aria-label="Seleccionar puntaje entre una y cinco estrellas">
                                        <c:forEach begin="1" end="5" var="estrella">
                                            <button class="btn btn-link btn-lg p-1" type="button"
                                                    data-rating-value="${estrella}" aria-pressed="false"
                                                    aria-label="${estrella} ${estrella eq 1 ? 'estrella' : 'estrellas'}"
                                                    title="${estrella} de 5 estrellas">
                                                <i class="bi bi-star" aria-hidden="true"></i>
                                            </button>
                                        </c:forEach>
                                        </div>
                                        <p class="small text-muted" data-rating-label aria-live="polite">Ningún puntaje seleccionado</p>
                                        <button type="submit" class="btn btn-primary" disabled>
                                            <i class="bi bi-star me-1" aria-hidden="true"></i>Confirmar valoración
                                        </button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                    </section>
                </aside>
            </div>
        </c:when>
        <c:otherwise>
            <div class="content-card p-4 p-md-5 mt-4 text-center">
                <i class="bi bi-info-circle display-5 text-primary" aria-hidden="true"></i>
                <h1 class="h4 fw-bold mt-3">No hay datos de curso para mostrar</h1>
                <p class="text-muted mb-4">El controlador debe cargar el curso solicitado antes de mostrar este detalle.</p>
                <a href="<c:out value='${inicioUrl}'/>" class="btn btn-dark">Ir al inicio</a>
            </div>
        </c:otherwise>
    </c:choose>
        </div>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    const dibujarEstrellasPromedio = (estrellas) => {
        document.querySelectorAll("[data-rating-star]").forEach((icono) => {
            const valor = Number(icono.dataset.ratingStar);
            icono.className = valor <= estrellas
                ? "bi bi-star-fill text-warning"
                : valor - 0.5 <= estrellas
                    ? "bi bi-star-half text-warning"
                    : "bi bi-star text-secondary";
        });
        document.querySelector("[data-rating-stars]")?.setAttribute(
            "aria-label", `Promedio visual de ${estrellas} de 5 estrellas`);
    };

    document.querySelectorAll(".rating-form").forEach((form) => {
        const score = form.querySelector('input[name="puntaje"]');
        const buttons = Array.from(form.querySelectorAll("[data-rating-value]"));
        const label = form.querySelector("[data-rating-label]");
        const submit = form.querySelector('button[type="submit"]');

        buttons.forEach((button) => {
            button.addEventListener("click", () => {
                const selected = Number(button.dataset.ratingValue);
                score.value = selected;
                buttons.forEach((star) => {
                    const isSelected = Number(star.dataset.ratingValue) <= selected;
                    star.classList.toggle("selected", isSelected);
                    star.setAttribute("aria-pressed", String(isSelected));
                    star.querySelector("i").className = isSelected
                        ? "bi bi-star-fill"
                        : "bi bi-star";
                });
                label.textContent = `${selected} de 5 estrellas`;
                submit.disabled = false;
            });
        });

    });
</script>
</body>
</html>
