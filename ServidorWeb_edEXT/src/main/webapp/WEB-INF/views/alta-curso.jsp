<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Alta de curso | edEXT</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <link href="assets/css/edext.css" rel="stylesheet">
</head>
<body>
<!-- Navegación común hacia el catálogo, la página principal y la cuenta. -->
<header class="custom-header py-2 px-4 shadow-sm">
    <div class="container-fluid d-flex align-items-center justify-content-between">
        <a href="inicio" class="text-decoration-none" aria-label="Inicio edEXT">
            <div class="logo-circle">edEXT</div>
        </a>
        <nav class="d-flex gap-2" aria-label="Navegación principal">
            <a href="cursos" class="btn btn-outline-light">Cursos</a>
            <c:choose>
                <c:when test="${not empty sessionScope.usuarioNickname}">
                    <a href="mi-cuenta" class="btn btn-light text-dark">Mi cuenta</a>
                </c:when>
                <c:otherwise>
                    <a href="login" class="btn btn-light text-dark">Iniciar sesión</a>
                </c:otherwise>
            </c:choose>
        </nav>
    </div>
</header>

<!-- El formulario se publica al servlet, que valida y guarda usando el controlador central. -->
<main class="container my-4 my-lg-5">
    <div class="mb-4">
        <a href="cursos" class="text-decoration-none text-muted">
            <i class="bi bi-arrow-left me-1" aria-hidden="true"></i> Volver a cursos
        </a>
        <p class="text-uppercase fw-bold small text-muted mt-3 mb-1">Gestión académica</p>
        <h1 class="page-heading fw-bold">Alta de curso</h1>
        <p class="text-muted mb-0">Completá los datos generales y las relaciones del curso.</p>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger" role="alert"><c:out value="${error}"/></div>
    </c:if>
    <c:if test="${empty institutos or empty categorias}">
        <div class="alert alert-warning" role="status">
            <c:choose>
                <c:when test="${not empty error}"><c:out value="${error}"/></c:when>
                <c:otherwise>Primero cargá institutos y categorías en el sistema para registrar cursos.</c:otherwise>
            </c:choose>
        </div>
    </c:if>

    <form action="alta-curso" method="post" enctype="multipart/form-data">
        <!-- Datos simples del curso que corresponden a los campos del DTO central. -->
        <section class="content-card p-3 p-md-4 mb-4" aria-labelledby="datos-generales">
            <div class="mb-4">
                <h2 class="h5 fw-bold mb-1" id="datos-generales">Datos generales</h2>
                <p class="text-muted small mb-0">Los campos marcados con * son obligatorios.</p>
            </div>
            <div class="row g-3">
                <div class="col-md-6">
                    <label for="instituto" class="form-label">Instituto <span class="required-mark">*</span></label>
                    <select class="form-select" id="instituto" name="instituto" required>
                        <option value="" disabled <c:if test="${empty form_instituto}">selected</c:if>>Seleccioná un instituto</option>
                        <c:forEach items="${institutos}" var="instituto">
                            <option value="<c:out value='${instituto}'/>"
                                    data-instituto="<c:out value='${instituto}'/>"
                                    <c:if test="${instituto eq form_instituto}">selected</c:if>>
                                <c:out value="${instituto}"/>
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-6">
                    <label for="nombre" class="form-label">Nombre del curso <span class="required-mark">*</span></label>
                    <input class="form-control" id="nombre" name="nombre" type="text" maxlength="120"
                           pattern="[^0-9].*" title="El nombre no puede comenzar con un número"
                           placeholder="Ej.: Introducción a..." value="<c:out value='${form_nombre}'/>" required>
                </div>
                <div class="col-12">
                    <label for="descripcion" class="form-label">Descripción <span class="required-mark">*</span></label>
                    <textarea class="form-control" id="descripcion" name="descripcion" rows="5"
                              placeholder="Contá de qué trata el curso" required><c:out value="${form_descripcion}"/></textarea>
                </div>
                <div class="col-sm-4">
                    <label for="duracion" class="form-label">Duración (meses) <span class="required-mark">*</span></label>
                    <input class="form-control" id="duracion" name="duracion" type="number" min="1" step="1" value="<c:out value='${form_duracion}'/>" required>
                </div>
                <div class="col-sm-4">
                    <label for="horas" class="form-label">Cantidad de horas <span class="required-mark">*</span></label>
                    <input class="form-control" id="horas" name="cantHoras" type="number" min="1" step="1" value="<c:out value='${form_cantHoras}'/>" required>
                </div>
                <div class="col-sm-4">
                    <label for="creditos" class="form-label">Créditos <span class="required-mark">*</span></label>
                    <input class="form-control" id="creditos" name="creditos" type="number" min="0" step="1" value="<c:out value='${form_creditos}'/>" required>
                </div>
                <div class="col-md-8">
                    <label for="url" class="form-label">URL asociada <span class="required-mark">*</span></label>
                    <input class="form-control" id="url" name="url" type="url" pattern="https?://.+"
                           placeholder="https://ejemplo.edu.uy/curso" value="<c:out value='${form_url}'/>" required>
                    <div class="form-text">Usá una dirección que comience con http:// o https://.</div>
                </div>
                <div class="col-md-4">
                    <label for="fecha" class="form-label">Fecha de alta <span class="required-mark">*</span></label>
                    <input class="form-control" id="fecha" name="fecha" type="date" value="<c:out value='${form_fecha}'/>" required>
                </div>
            </div>
        </section>

        <!-- Las categorías y previas se cargan desde la base por el servlet. -->
        <section class="content-card p-3 p-md-4 mb-4" aria-labelledby="relaciones-curso">
            <h2 class="h5 fw-bold mb-1" id="relaciones-curso">Relaciones del curso</h2>
            <p class="text-muted small mb-4">Seleccioná al menos una categoría. Las previas son opcionales.</p>
            <fieldset class="mb-4">
                <legend class="form-label fs-6">Categorías <span class="required-mark">*</span></legend>
                <div class="row g-2">
                    <c:forEach items="${categorias}" var="categoria" varStatus="estado">
                        <div class="col-sm-6 col-lg-3">
                            <div class="form-check">
                                <input class="form-check-input categoria" id="cat-${estado.index}"
                                       name="categorias" type="checkbox" value="<c:out value='${categoria}'/>"
                                       <c:if test="${categoriasMarcadas[categoria]}">checked</c:if>>
                                <label class="form-check-label" for="cat-${estado.index}"><c:out value="${categoria}"/></label>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </fieldset>
            <div class="form-check form-switch mb-3">
                <input class="form-check-input" id="tiene-previas" type="checkbox" role="switch"
                       aria-controls="previas-container">
                <label class="form-check-label fw-semibold" for="tiene-previas">El curso tiene cursos previos</label>
            </div>
            <div id="previas-container" class="row g-3" hidden>
                <div class="col-md-8">
                    <label for="previas" class="form-label">Cursos previos</label>
                    <select class="form-select" id="previas" name="previas" multiple size="4" disabled>
                        <c:forEach items="${cursosDisponibles}" var="curso">
                            <option value="<c:out value='${curso.nombre}'/>"
                                    data-instituto="<c:out value='${curso.nomInstituto}'/>"
                                    <c:if test="${previasMarcadas[curso.nombre]}">selected</c:if>>
                                <c:out value="${curso.nombre}"/>
                            </option>
                        </c:forEach>
                    </select>
                    <div class="form-text">Podés seleccionar más de un curso del mismo instituto.</div>
                </div>
            </div>
        </section>

        <!-- La imagen es opcional y se envía como multipart al servidor. -->
        <section class="content-card p-3 p-md-4 mb-4" aria-labelledby="imagen-curso">
            <h2 class="h5 fw-bold mb-1" id="imagen-curso">Imagen del curso</h2>
            <p class="text-muted small">Opcional. Si no seleccionás una imagen, el catálogo mostrará un marcador neutral.</p>
            <label for="imagen" class="form-label">Seleccionar imagen</label>
            <input class="form-control" id="imagen" name="imagen" type="file" accept="image/*">
        </section>

        <div class="d-flex flex-wrap justify-content-end gap-2">
            <a href="cursos" class="btn btn-outline-secondary px-4">Cancelar</a>
            <button class="btn btn-dark px-4" type="submit">
                <i class="bi bi-check2-circle me-1" aria-hidden="true"></i> Registrar curso
            </button>
        </div>
    </form>
</main>
<script>
    // Mantener solo previas del instituto elegido y validar categorías antes del envío.
    const togglePrevias = document.getElementById("tiene-previas");
    const previasContainer = document.getElementById("previas-container");
    const previasSelect = document.getElementById("previas");
    const categorias = Array.from(document.querySelectorAll(".categoria"));
    const institutoSelect = document.getElementById("instituto");
    const form = document.querySelector("form");

    const filtrarPrevias = () => {
        const institutoActual = institutoSelect.value;
        Array.from(previasSelect.options).forEach(option => {
            const mismaInstitucion = Boolean(institutoActual)
                && option.dataset.instituto === institutoActual;
            option.hidden = !mismaInstitucion;
            option.disabled = !mismaInstitucion;
            if (!mismaInstitucion) {
                option.selected = false;
            }
        });
    };

    const validarCategorias = () => {
        const hayCategoria = categorias.some(categoria => categoria.checked);
        if (categorias.length > 0) {
            categorias[0].setCustomValidity(hayCategoria ? "" : "Seleccioná al menos una categoría.");
        }
    };

    categorias.forEach(categoria => categoria.addEventListener("change", validarCategorias));
    validarCategorias();
    institutoSelect.addEventListener("change", filtrarPrevias);
    filtrarPrevias();
    form.addEventListener("submit", event => {
        validarCategorias();
        if (!form.checkValidity()) {
            event.preventDefault();
            form.reportValidity();
        }
    });

    togglePrevias.addEventListener("change", () => {
        previasContainer.hidden = !togglePrevias.checked;
        previasSelect.disabled = !togglePrevias.checked;
        if (!togglePrevias.checked) {
            Array.from(previasSelect.options).forEach(option => option.selected = false);
        }
    });
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
