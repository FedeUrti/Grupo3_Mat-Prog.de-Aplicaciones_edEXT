<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Crear programa | edEXT</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <c:url var="estilosUrl" value="/assets/css/edext.css">
        <c:param name="v" value="20261015-2"/>
    </c:url>
    <link href="<c:out value='${estilosUrl}'/>" rel="stylesheet">
</head>
<body>
    <%@ include file="/WEB-INF/views/buscador-global.jspf" %>
    <c:url var="programasUrl" value="/programas"/>
    <c:url var="crearProgramaPostUrl" value="/crear-programa"/>

    <main class="container-fluid page-layout py-4 px-3 px-lg-4">
        <div class="row g-4 align-items-start">
            <%@ include file="/WEB-INF/views/sidebar-principal.jspf" %>
            <div class="col main-content-column">
                <a href="<c:out value='${programasUrl}'/>" class="text-decoration-none text-muted">
                    <i class="bi bi-arrow-left me-1" aria-hidden="true"></i> Volver a programas
                </a>
                <div class="my-4">
                    <p class="text-uppercase fw-bold small text-muted mb-1">Gestión académica</p>
                    <h1 class="page-heading fw-bold mb-1">Crear programa de formación</h1>
                    <p class="text-muted mb-0">Completá los datos del programa para publicarlo.</p>
                </div>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger" role="alert"><c:out value="${error}"/></div>
                </c:if>

                <form action="<c:out value='${crearProgramaPostUrl}'/>"
                      method="post" enctype="multipart/form-data"
                      class="content-card p-3 p-md-4">
                    <div class="mb-3">
                        <label for="nombre" class="form-label fw-semibold">Nombre del programa <span class="required-mark">*</span></label>
                        <input type="text" class="form-control" id="nombre" name="nombre"
                               value="<c:out value='${nombre}'/>" maxlength="120"
                               placeholder="Ej. Diplomado en Desarrollo Web" required>
                    </div>

                    <div class="mb-3">
                        <label for="descripcion" class="form-label fw-semibold">Descripción <span class="required-mark">*</span></label>
                        <textarea class="form-control" id="descripcion" name="descripcion" rows="4"
                                  maxlength="2000" required><c:out value="${descripcion}"/></textarea>
                    </div>

                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label for="fechaInicio" class="form-label fw-semibold">Fecha de inicio <span class="required-mark">*</span></label>
                            <input type="date" class="form-control" id="fechaInicio" name="fechaInicio"
                                   value="<c:out value='${fechaInicio}'/>" required>
                        </div>
                        <div class="col-md-6">
                            <label for="fechaFin" class="form-label fw-semibold">Fecha de fin <span class="required-mark">*</span></label>
                            <input type="date" class="form-control" id="fechaFin" name="fechaFin"
                                   value="<c:out value='${fechaFin}'/>" required>
                        </div>
                    </div>

                    <div class="mb-4">
                        <label for="imagen" class="form-label fw-semibold">Imagen del programa</label>
                        <input type="file" class="form-control" id="imagen" name="imagen"
                               accept="image/png,image/jpeg,image/gif,image/webp">
                        <div class="form-text">Opcional. Se aceptan imágenes PNG, JPEG, GIF o WebP de hasta 10 MB.</div>
                    </div>

                    <div class="d-flex justify-content-end gap-2">
                        <a href="<c:out value='${programasUrl}'/>" class="btn btn-outline-secondary">
                            Cancelar
                        </a>
                        <button type="submit" class="btn btn-primary">
                            <i class="bi bi-plus-circle me-1" aria-hidden="true"></i> Crear programa
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </main>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>