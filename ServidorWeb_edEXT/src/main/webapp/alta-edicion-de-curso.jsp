<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Alta de Edición de Curso | edEXT</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <c:url var="estilosUrl" value="/assets/css/edext.css">
        <c:param name="v" value="20261015-1"/>
    </c:url>
    <link href="<c:out value='${estilosUrl}'/>" rel="stylesheet">
</head>
<body>
<%@ include file="/WEB-INF/views/buscador-global.jspf" %>
<c:url var="inicioUrl" value="/inicio"/>
<main class="container-fluid page-layout py-4 px-3 px-lg-4">
    <div class="row g-4 align-items-start">
        <%@ include file="/WEB-INF/views/sidebar-principal.jspf" %>
        <div class="col main-content-column">
    <!-- Título de sección alineado con el estilo de Cursos -->
    <div class="d-flex flex-wrap align-items-end justify-content-between gap-3 mb-4">
        <div>
            <p class="text-uppercase fw-bold small text-muted mb-1">Gestión académica</p>
            <h1 class="page-heading fw-bold mb-1">Alta de Edición de Curso</h1>
            <p class="text-muted mb-0">Completá los datos para registrar una nueva edición de un curso dictado.</p>
        </div>
    </div>

    <div class="row justify-content-center">
        <div class="col-lg-9">

            <!-- Mensajes de Alerta y Errores del Servidor -->
            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show mb-4" role="alert">
                    <i class="bi bi-exclamation-triangle-fill me-2" aria-hidden="true"></i>
                    <c:out value="${error}"/>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar"></button>
                </div>
            </c:if>

            <!-- Alerta de Validación Cliente (Docentes) -->
            <div id="alertaDocentes" class="alert alert-danger d-none alert-dismissible fade show mb-4" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2" aria-hidden="true"></i>
                Debe seleccionar al menos un docente para registrar la edición.
                <button type="button" class="btn-close" onclick="ocultarAlertaDocentes()" aria-label="Cerrar"></button>
            </div>

            <form id="formAltaEdicion" action="alta-edicion-curso" method="POST" enctype="multipart/form-data" class="content-card p-4 p-md-5">

                <!-- SECCIÓN 1: Ubicación del Curso -->
                <h2 class="h6 text-uppercase fw-bold text-muted mb-3">1. Selección de Curso</h2>
                <div class="row g-3 mb-4">
                    <div class="col-md-6">
                        <label for="instituto" class="form-label fw-semibold">Instituto <span class="text-danger">*</span></label>
                        <select class="form-select" id="instituto" name="instituto" required>
                            <option value="" disabled <c:if test="${empty institutoSeleccionado}">selected</c:if>>-- Seleccione un instituto --</option>
                            <c:forEach items="${institutosDocente}" var="inst">
                                <option value="<c:out value='${inst}'/>" <c:if test="${inst eq institutoSeleccionado}">selected</c:if>>
                                    <c:out value="${inst}"/>
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="col-md-6">
                        <label for="cursoSelect" class="form-label fw-semibold">Curso <span class="text-danger">*</span></label>
                        <select class="form-select" id="cursoSelect" name="curso" required disabled>
                            <option value="" disabled selected>-- Primero seleccione un instituto --</option>
                        </select>
                    </div>
                </div>

                <!-- SECCIÓN 2: Información Básica de la Edición -->
                <div class="border-top pt-4 mb-4">
                    <h2 class="h6 text-uppercase fw-bold text-muted mb-3">2. Datos de la Edición</h2>

                    <div class="mb-3">
                        <label for="nombreEdicion" class="form-label fw-semibold">Nombre de la Edición <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="nombreEdicion" name="nombreEdicion"
                               value="<c:out value='${not empty nombreEdicion ? nombreEdicion : param.nombreEdicion}'/>"
                               required placeholder="Ej: Verano 2026">
                    </div>

                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label for="fechaInicio" class="form-label fw-semibold">Fecha de Inicio <span class="text-danger">*</span></label>
                            <input type="date" class="form-control" id="fechaInicio" name="fechaInicio"
                                   value="<c:out value='${not empty fechaInicio ? fechaInicio : param.fechaInicio}'/>" required>
                        </div>
                        <div class="col-md-6">
                            <label for="fechaFin" class="form-label fw-semibold">Fecha de Fin <span class="text-danger">*</span></label>
                            <input type="date" class="form-control" id="fechaFin" name="fechaFin"
                                   value="<c:out value='${not empty fechaFin ? fechaFin : param.fechaFin}'/>" required>
                        </div>
                    </div>

                    <!-- Configuración de Cupo -->
                    <div class="card bg-light border-0 p-3 mb-3">
                        <div class="form-check mb-2">
                            <input class="form-check-input"
                                   type="checkbox"
                                   id="tieneCupo"
                                   name="tieneCupo"
                                   value="true"
                                   onchange="toggleCupoInput()"
                                   <c:if test="${tieneCupo eq 'true' or param.tieneCupo eq 'true'}">checked</c:if>>
                            <label class="form-check-label fw-semibold" for="tieneCupo">
                                Limitar cupo de participantes
                            </label>
                        </div>

                        <div id="contenedorCupo">
                            <label for="cupo" class="form-label small fw-semibold text-muted">Cantidad de cupos máximos</label>
                            <input type="number"
                                   class="form-control"
                                   id="cupo"
                                   name="cupo"
                                   min="1"
                                   placeholder="Ej: 30"
                                   value="<c:out value='${not empty cupo ? cupo : param.cupo}'/>"
                                   <c:if test="${tieneCupo ne 'true' and param.tieneCupo ne 'true'}">disabled</c:if>>
                            <div class="form-text">Si no se marca la casilla, la edición tendrá cupo ilimitado.</div>
                        </div>
                    </div>
                </div>

                <!-- SECCIÓN 3: Multimedia y Equipo Docente -->
                <div class="border-top pt-4 mb-4">
                    <h2 class="h6 text-uppercase fw-bold text-muted mb-3">3. Multimedia y Equipo</h2>

                    <div class="mb-4">
                        <label for="imagen" class="form-label fw-semibold">Imagen representativa (Opcional)</label>
                        <input type="file" class="form-control" id="imagen" name="imagen" accept="image/png, image/jpeg, image/gif, image/webp">
                        <div class="form-text">Formatos permitidos: PNG, JPEG, GIF o WebP (Máx. 5 MB).</div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">
                            Docentes asignados <span class="text-danger">*</span>
                        </label>
                        <div id="docentesContainer" class="border rounded p-3 bg-white" style="max-height: 200px; overflow-y: auto;">
                            <p class="text-muted mb-0 small">Primero seleccione un instituto para listar los docentes disponibles.</p>
                        </div>
                    </div>
                </div>

                <!-- Botones de Acción -->
                <div class="d-flex justify-content-end gap-2 pt-3 border-top">
                    <a href="<c:out value='${inicioUrl}'/>" class="btn btn-outline-secondary">Cancelar</a>
                    <button type="submit" class="btn btn-dark px-4 fw-semibold">
                        Registrar Edición
                    </button>
                </div>
            </form>

        </div>
    </div>
                </div>
            </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script>
function toggleCupoInput() {
    const tieneCupo = document.getElementById('tieneCupo').checked;
    const cupoInput = document.getElementById('cupo');

    cupoInput.disabled = !tieneCupo;
    if (!tieneCupo) {
        cupoInput.value = '';
    } else {
        cupoInput.focus();
    }
}

function ocultarAlertaDocentes() {
    document.getElementById('alertaDocentes').classList.add('d-none');
}

document.addEventListener("DOMContentLoaded", () => {
    // Datos en formato JSON
    const cursosMap = <c:out value="${not empty cursosPorInstJson ? cursosPorInstJson : '{}'}" escapeXml="false"/>;
    const docentesMap = <c:out value="${not empty docentesPorInstJson ? docentesPorInstJson : '{}'}" escapeXml="false"/>;

    // Recuperación de estado previo
    const cursoSeleccionadoPrevio = "<c:out value='${not empty cursoSeleccionado ? cursoSeleccionado : param.curso}'/>";
    const docentesSeleccionadosPrevios = [
        <c:if test="${not empty docentesSeleccionados}">
            <c:forEach items="${docentesSeleccionados}" var="d" varStatus="loop">
                "<c:out value='${d}'/>"<c:if test="${!loop.last}">,</c:if>
            </c:forEach>
        </c:if>
    ];

    const selectInstituto = document.getElementById("instituto");
    const selectCurso = document.getElementById("cursoSelect");
    const docentesContainer = document.getElementById("docentesContainer");
    const form = document.getElementById("formAltaEdicion");

    // Validar selección de docentes al enviar el formulario
    form.addEventListener("submit", (e) => {
        const checkboxesDocentes = docentesContainer.querySelectorAll('input[type="checkbox"][name="docentes"]');

        // Si hay checkboxes de docentes disponibles, se requiere al menos uno marcado
        if (checkboxesDocentes.length > 0) {
            const algunoSeleccionado = Array.from(checkboxesDocentes).some(cb => cb.checked);
            if (!algunoSeleccionado) {
                e.preventDefault();
                document.getElementById('alertaDocentes').classList.remove('d-none');
                docentesContainer.scrollIntoView({ behavior: 'smooth', block: 'center' });
                return false;
            }
        }
    });

    selectInstituto.addEventListener("change", (e) => {
        const instSel = e.target.value;
        if (!instSel) return;

        const cursos = cursosMap[instSel] || [];
        const docentes = docentesMap[instSel] || [];

        poblarSelectCursos(selectCurso, cursos, cursoSeleccionadoPrevio);
        selectCurso.disabled = false;

        poblarCheckboxesDocentes(docentesContainer, docentes, docentesSeleccionadosPrevios);
    });

    // Auto-disparar evento si hay un instituto preseleccionado
    if (selectInstituto.value !== "") {
        selectInstituto.dispatchEvent(new Event("change"));
    }

    function poblarSelectCursos(selectElem, listaCursos, valorSeleccionado) {
        selectElem.innerHTML = "";

        const optPlaceholder = document.createElement("option");
        optPlaceholder.value = "";
        optPlaceholder.disabled = true;
        optPlaceholder.selected = !valorSeleccionado;
        optPlaceholder.textContent = "-- Seleccione un curso --";
        selectElem.appendChild(optPlaceholder);

        if (listaCursos.length === 0) {
            const optVacio = document.createElement("option");
            optVacio.disabled = true;
            optVacio.textContent = "No hay cursos en este instituto";
            selectElem.appendChild(optVacio);
            return;
        }

        listaCursos.forEach(curso => {
            const opt = document.createElement("option");
            opt.value = curso;
            opt.textContent = curso;
            if (curso === valorSeleccionado) {
                opt.selected = true;
            }
            selectElem.appendChild(opt);
        });
    }

    function poblarCheckboxesDocentes(container, listaDocentes, seleccionados) {
        container.innerHTML = "";

        if (listaDocentes.length === 0) {
            container.innerHTML = `<p class="text-muted mb-0 small">No hay docentes registrados en este instituto.</p>`;
            return;
        }

        listaDocentes.forEach(doc => {
            const div = document.createElement("div");
            div.className = "form-check mb-1";

            const input = document.createElement("input");
            input.className = "form-check-input";
            input.type = "checkbox";
            input.name = "docentes";
            input.value = doc;
            input.id = `doc_${doc}`;

            // Ocultar mensaje de alerta al interactuar con cualquier casilla
            input.addEventListener('change', () => {
                ocultarAlertaDocentes();
            });

            if (seleccionados.includes(doc)) {
                input.checked = true;
            }

            const label = document.createElement("label");
            label.className = "form-check-label";
            label.htmlFor = `doc_${doc}`;
            label.textContent = doc;

            div.appendChild(input);
            div.appendChild(label);
            container.appendChild(div);
        });
    }
});
</script>
</body>
</html>