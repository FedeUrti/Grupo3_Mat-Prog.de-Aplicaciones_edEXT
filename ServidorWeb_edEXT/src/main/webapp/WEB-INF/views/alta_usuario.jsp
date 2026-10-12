<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Alta de usuario | edEXT</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <c:url var="estilosUrl" value="/assets/css/edext.css">
        <c:param name="v" value="20261012-1"/>
    </c:url>
    <link href="<c:out value='${estilosUrl}'/>" rel="stylesheet">
</head>
<body>
<%@ include file="/WEB-INF/views/buscador-global.jspf" %>

<main class="container py-4 py-lg-5 usuario-formulario">
    <div class="row justify-content-center">
        <section class="col-lg-8">
            <div class="content-card p-4 p-md-5">
                <div class="text-center mb-4">
                    <h1 class="page-heading h2 fw-bold">
                        <i class="bi bi-person-plus-fill me-2" aria-hidden="true"></i>Alta de usuario
                    </h1>
                    <p class="text-muted mb-0">Completá tus datos para crear una cuenta en edEXT.</p>
                </div>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger" role="alert"><c:out value="${error}"/></div>
                </c:if>

                <%-- El servlet valida los campos, almacena la imagen y delega el alta al controlador central. --%>
                <c:url var="altaUsuarioUrl" value="/alta-usuario"/>
                <form action="<c:out value='${altaUsuarioUrl}'/>" method="post" enctype="multipart/form-data">
                    <div class="mb-3">
                        <label for="tipoUsuario" class="form-label">Tipo de usuario <span class="required-mark">*</span></label>
                        <select class="form-select" id="tipoUsuario" name="tipoUsuario" required>
                            <option value="estudiante"
                                    <c:if test="${empty form_tipoUsuario or form_tipoUsuario eq 'estudiante'}">selected</c:if>>
                                Estudiante
                            </option>
                            <option value="docente"
                                    <c:if test="${form_tipoUsuario eq 'docente'}">selected</c:if>>
                                Docente
                            </option>
                        </select>
                    </div>

                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label for="nickname" class="form-label">Nickname <span class="required-mark">*</span></label>
                            <input class="form-control" id="nickname" name="nickname" type="text"
                                   autocomplete="username" maxlength="60"
                                   value="<c:out value='${form_nickname}'/>" required>
                        </div>
                        <div class="col-md-6">
                            <label for="correo" class="form-label">Correo electrónico <span class="required-mark">*</span></label>
                            <input class="form-control" id="correo" name="correo" type="email"
                                   autocomplete="email" maxlength="254"
                                   value="<c:out value='${form_correo}'/>" required>
                        </div>
                    </div>

                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label for="nombre" class="form-label">Nombre <span class="required-mark">*</span></label>
                            <input class="form-control" id="nombre" name="nombre" type="text" maxlength="100"
                                   value="<c:out value='${form_nombre}'/>" required>
                        </div>
                        <div class="col-md-6">
                            <label for="apellido" class="form-label">Apellido <span class="required-mark">*</span></label>
                            <input class="form-control" id="apellido" name="apellido" type="text" maxlength="100"
                                   value="<c:out value='${form_apellido}'/>" required>
                        </div>
                    </div>

                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label for="password" class="form-label">Contraseña <span class="required-mark">*</span></label>
                            <input class="form-control" id="password" name="password" type="password"
                                   autocomplete="new-password" required>
                        </div>
                        <div class="col-md-6">
                            <label for="confirmarPassword" class="form-label">Confirmar contraseña <span class="required-mark">*</span></label>
                            <input class="form-control" id="confirmarPassword" name="confirmarPassword" type="password"
                                   autocomplete="new-password" required>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="fechaNacimiento" class="form-label">Fecha de nacimiento <span class="required-mark">*</span></label>
                        <input class="form-control" id="fechaNacimiento" name="fechaNacimiento" type="date"
                               value="<c:out value='${form_fechaNacimiento}'/>" required>
                    </div>

                    <%-- Solo se envían institutos marcados; para estudiantes el servlet ignora esta selección. --%>
                    <fieldset class="mb-4" id="bloqueInstitutos">
                        <legend class="form-label">Institutos <span class="text-muted fw-normal">(requerido para docentes)</span></legend>
                        <c:choose>
                            <c:when test="${not empty institutos}">
                                <div class="row g-2">
                                    <c:forEach items="${institutos}" var="instituto">
                                        <div class="col-sm-6">
                                            <label class="instituto-opcion">
                                                <input class="form-check-input me-2" type="checkbox" name="institutos"
                                                       value="<c:out value='${instituto}'/>"
                                                       <c:if test="${institutosMarcados.contains(instituto)}">checked</c:if>>
                                                <c:out value="${instituto}"/>
                                            </label>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <p class="text-muted mb-0">No hay institutos disponibles actualmente.</p>
                            </c:otherwise>
                        </c:choose>
                        <div class="form-text">Podés seleccionar más de un instituto si sos docente.</div>
                    </fieldset>

                    <div class="mb-4">
                        <label for="imagen" class="form-label">Foto de perfil <span class="text-muted fw-normal">(opcional)</span></label>
                        <input class="form-control" id="imagen" name="imagen" type="file"
                               accept="image/png,image/jpeg,image/gif,image/webp">
                        <div class="form-text">PNG, JPEG, GIF o WebP; tamaño máximo de 5 MB.</div>
                    </div>

                    <div class="d-flex flex-wrap justify-content-between gap-2 pt-3 border-top">
                        <c:url var="consultaUsuariosUrl" value="/consulta-usuario"/>
                        <a class="btn btn-outline-secondary" href="<c:out value='${consultaUsuariosUrl}'/>">Ir a consulta</a>
                        <button class="btn btn-primary px-4" type="submit">Registrarse</button>
                    </div>
                </form>
            </div>
        </section>
    </div>
</main>

<script>
    // La selección de institutos se muestra solo para docentes; la obligatoriedad se valida también en el servlet.
    const selectorTipo = document.getElementById("tipoUsuario");
    const bloqueInstitutos = document.getElementById("bloqueInstitutos");
    const actualizarInstitutos = () => {
        bloqueInstitutos.hidden = selectorTipo.value !== "docente";
    };
    selectorTipo.addEventListener("change", actualizarInstitutos);
    actualizarInstitutos();
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
