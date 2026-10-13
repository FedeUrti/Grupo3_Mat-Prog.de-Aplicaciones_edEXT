<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Selección de Estudiantes | edEXT</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <c:url var="estilosUrl" value="/assets/css/edext.css">
        <c:param name="v" value="20261012-6"/>
    </c:url>
    <link href="<c:out value='${estilosUrl}'/>" rel="stylesheet">
</head>
<body>
<%@ include file="/WEB-INF/views/buscador-global.jspf" %>
<main class="container-fluid page-layout py-4 px-3 px-lg-4">
    <div class="row g-4 align-items-start">
        <%@ include file="/WEB-INF/views/sidebar-principal.jspf" %>
        <div class="col main-content-column">
    <div class="d-flex flex-wrap align-items-end justify-content-between gap-3 mb-4">
        <div>
            <a href="inicio" class="text-decoration-none text-muted">
                <i class="bi bi-arrow-left me-1" aria-hidden="true"></i> Volver al inicio
            </a>
            <p class="text-uppercase fw-bold small text-muted mb-1">Gestión Docente</p>
            <h1 class="page-heading fw-bold mb-1">Selección de Estudiantes</h1>
            <p class="text-muted mb-0">Seleccioná un instituto y curso para administrar el estado de las inscripciones de sus ediciones vigentes.</p>
        </div>
    </div>

    <!-- Mensajes -->
    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show mb-4" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2"></i>
            <c:out value="${error}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar"></button>
        </div>
    </c:if>

    <c:if test="${not empty mensajeExito}">
        <div class="alert alert-success alert-dismissible fade show mb-4" role="alert">
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
        <!-- Panel Izquierdo: Filtros -->
        <aside class="col-lg-4">
            <div class="content-card p-4">
                <h2 class="h6 text-uppercase fw-bold text-muted mb-3">1. Filtrar Cursos</h2>

                <form action="seleccion-estudiantes" method="get" class="mb-3">
                    <label for="selectInstituto" class="form-label fw-semibold">Por Instituto</label>
                    <select class="form-select mb-2" id="selectInstituto" name="instituto" onchange="this.form.submit()">
                        <option value="">-- Seleccionar Instituto --</option>
                        <c:forEach items="${institutos}" var="inst">
                            <option value="${inst}" <c:if test="${inst eq filtroInstituto}">selected</c:if>>
                                <c:out value="${inst}"/>
                            </option>
                        </c:forEach>
                    </select>
                </form>

                <c:if test="${not empty filtroInstituto and empty cursos}">
                    <div class="alert alert-info mb-0" role="status">
                        No hay cursos disponibles en este instituto.
                    </div>
                </c:if>

                <c:if test="${not empty cursos}">
                    <hr class="my-4">
                    <h2 class="h6 text-uppercase fw-bold text-muted mb-3">2. Seleccionar Curso</h2>
                    <form action="seleccion-estudiantes" method="get">
                        <c:if test="${not empty filtroInstituto}">
                            <input type="hidden" name="instituto" value="${filtroInstituto}"/>
                        </c:if>
                        <select class="form-select mb-3" name="curso" onchange="this.form.submit()">
                            <option value="">-- Seleccionar Curso --</option>
                            <c:forEach items="${cursos}" var="c">
                                <option value="${c}" <c:if test="${c eq cursoSeleccionado}">selected</c:if>>
                                    <c:out value="${c}"/>
                                </option>
                            </c:forEach>
                        </select>
                    </form>
                </c:if>
            </div>
        </aside>

        <!-- Panel Derecho: Listado de Inscriptos -->
        <section class="col-lg-8">
            <c:choose>
                <c:when test="${not empty edicionVigente}">
                    <div class="content-card p-4 p-md-5 mb-4">
                        <div class="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-3">
                            <div>
                                <span class="badge text-bg-primary mb-1"><c:out value="${cursoSeleccionado}"/></span>
                                <h2 class="h4 fw-bold mb-0">Edición: <c:out value="${edicionVigente.nombre}"/></h2>
                            </div>

                            <!-- Opciones de Criterio de Orden -->
                            <form action="seleccion-estudiantes" method="get" class="d-flex align-items-center gap-2">
                                <input type="hidden" name="instituto" value="${filtroInstituto}"/>
                                <input type="hidden" name="curso" value="${cursoSeleccionado}"/>
                                <label for="criterio" class="small text-muted fw-semibold text-nowrap">Ordenar por:</label>
                                <select class="form-select form-select-sm" id="criterio" name="criterio" onchange="this.form.submit()">
                                    <option value="fecha" <c:if test="${criterioSeleccionado eq 'fecha'}">selected</c:if>>Fecha de Inscripción</option>
                                    <option value="prioridad" <c:if test="${criterioSeleccionado eq 'prioridad'}">selected</c:if>>Prioridad</option>
                                </select>
                            </form>
                        </div>

                        <hr>

                        <!-- Tabla de Estudiantes Inscriptos -->
                        <c:choose>
                            <c:when test="${not empty inscripciones}">
                                <div class="table-responsive">
                                    <table class="table align-middle table-hover mb-0">
                                        <thead class="table-light">
                                            <tr>
                                                <th>Estudiante</th>
                                                <th>Fecha</th>
                                                <th>Estado Actual</th>
                                                <th class="text-end">Acciones</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach items="${inscripciones}" var="ins">
                                                <tr>
                                                    <td>
                                                        <div class="fw-semibold"><c:out value="${ins.nicknameEstudiante}"/></div>
                                                    </td>
                                                    <td><c:out value="${ins.fechaInscripcion}"/></td>
                                                    <td>
                                                        <span class="badge
                                                            <c:choose>
                                                                <c:when test="${ins.estado.name() eq 'ACEPTADA'}">bg-success</c:when>
                                                                <c:when test="${ins.estado.name() eq 'RECHAZADA'}">bg-danger</c:when>
                                                                <c:otherwise>bg-warning text-dark</c:otherwise>
                                                            </c:choose>">
                                                            <c:out value="${ins.estado}"/>
                                                        </span>
                                                    </td>
                                                    <td class="text-end">
                                                        <c:choose>
                                                            <%-- Si ya está Aceptada o Rechazada, no se muestran botones de acción --%>
                                                            <c:when test="${ins.estado.name() eq 'ACEPTADA' or ins.estado.name() eq 'RECHAZADA'}">
                                                                <span class="text-muted small fst-italic">Procesada</span>
                                                            </c:when>

                                                            <%-- Si está en estado INSCRIPTO, permite aceptar o rechazar --%>
                                                            <c:otherwise>
                                                                <form action="seleccion-estudiantes" method="post" class="d-inline-flex gap-1">
                                                                    <input type="hidden" name="edicion" value="${edicionVigente.nombre}"/>
                                                                    <input type="hidden" name="estudianteNickname" value="${ins.nicknameEstudiante}"/>
                                                                    <input type="hidden" name="instituto" value="${filtroInstituto}"/>
                                                                    <input type="hidden" name="curso" value="${cursoSeleccionado}"/>
                                                                    <input type="hidden" name="criterio" value="${criterioSeleccionado}"/>

                                                                    <button type="submit" name="nuevoEstado" value="ACEPTADA" class="btn btn-sm btn-outline-success" title="Aceptar">
                                                                        <i class="bi bi-check-lg"></i> Aceptar
                                                                    </button>
                                                                    <button type="submit" name="nuevoEstado" value="RECHAZADA" class="btn btn-sm btn-outline-danger" title="Rechazar">
                                                                        <i class="bi bi-x-lg"></i> Rechazar
                                                                    </button>
                                                                </form>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <p class="text-muted text-center py-4 mb-0">No hay estudiantes inscriptos a esta edición actualmente.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:when>

                <c:otherwise>
                    <div class="content-card p-5 text-center">
                        <i class="bi bi-people display-4 text-muted mb-3"></i>
                        <h2 class="h5 fw-bold">Selección de Estudiantes</h2>
                        <p class="text-muted mb-0">Seleccioná un instituto y un curso en el panel izquierdo para ver y gestionar las inscripciones a su edición vigente.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
    </div>
        </div>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>