<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${esPropietario ? 'Mi perfil' : 'Perfil de usuario'} | edEXT</title>
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
    <c:if test="${not empty error}">
        <div class="alert alert-danger" role="alert"><c:out value="${error}"/></div>
    </c:if>
    <c:if test="${not empty mensaje}">
        <div class="alert alert-success" role="status"><c:out value="${mensaje}"/></div>
    </c:if>
    <c:if test="${param.registrado eq '1'}">
        <div class="alert alert-success" role="status">Tu cuenta se creó correctamente.</div>
    </c:if>
    <c:if test="${param.actualizado eq '1'}">
        <div class="alert alert-success" role="status">Tus datos se actualizaron correctamente.</div>
    </c:if>

    <c:choose>
        <c:when test="${not empty usuario}">
            <section class="content-card p-4 p-md-5 mb-4" aria-label="Perfil de usuario">
                <div class="d-flex flex-column flex-md-row align-items-center gap-4">
                    <c:url var="imagenUsuarioUrl" value="/imagenes-usuario">
                        <c:param name="nombre" value="${imagenPerfil}"/>
                    </c:url>
                    <img class="avatar-perfil" src="<c:out value='${imagenUsuarioUrl}'/>"
                         alt="Foto de <c:out value='${usuario.nombre}'/>">
                    <div class="flex-grow-1 text-center text-md-start">
                        <p class="text-uppercase fw-bold small text-muted mb-1">
                            <c:out value="${esPropietario ? 'Mi perfil' : 'Perfil de usuario'}"/>
                        </p>
                        <h1 class="page-heading h2 fw-bold mb-1">
                            <c:out value="${usuario.nombre}"/> <c:out value="${usuario.apellido}"/>
                        </h1>
                        <p class="text-muted mb-2">@<c:out value="${usuario.nickname}"/> · <c:out value="${tipoUsuario}"/></p>
                        <div class="profile-stats d-flex justify-content-center justify-content-md-start flex-wrap gap-2">
                            <button class="profile-stat" id="seguidos-resumen" type="button"
                                    data-profile-tab="#seguidos-panel"
                                    aria-controls="seguidos-panel">
                                <strong><c:out value="${usuario.seguidos.size()}"/></strong> seguidos
                            </button>
                            <button class="profile-stat" id="seguidores-resumen" type="button"
                                    data-profile-tab="#seguidores-panel"
                                    aria-controls="seguidores-panel">
                                <strong><c:out value="${usuario.seguidores.size()}"/></strong> seguidores
                            </button>
                        </div>
                    </div>
                    <div class="d-flex flex-wrap justify-content-center gap-2">
                        <c:choose>
                            <c:when test="${esPropietario}">
                                <button class="btn btn-outline-primary" type="button"
                                        data-bs-toggle="modal" data-bs-target="#editar-perfil-modal">
                                    <i class="bi bi-pencil-square me-1" aria-hidden="true"></i>Modificar datos
                                </button>
                            </c:when>
                            <c:when test="${not empty sessionScope.usuarioNickname}">
                                <c:url var="seguirUsuarioUrl" value="${siguiendoUsuario ? '/dejar-de-seguir-usuario' : '/seguir-usuario'}"/>
                                <form action="<c:out value='${seguirUsuarioUrl}'/>" method="post">
                                    <input type="hidden" name="nicknameASeguir"
                                           value="<c:out value='${usuario.nickname}'/>">
                                    <button class="btn ${siguiendoUsuario ? 'btn-siguiendo' : 'btn-primary'}"
                                            type="submit">
                                        <i class="bi ${siguiendoUsuario ? 'bi-person-check-fill' : 'bi-person-plus-fill'} me-1"
                                           aria-hidden="true"></i>
                                        <c:out value="${siguiendoUsuario ? 'Dejar de seguir' : 'Seguir'}"/>
                                    </button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <c:url var="loginUrl" value="/login"/>
                                <a class="btn btn-primary" href="<c:out value='${loginUrl}'/>">Iniciá sesión para seguir</a>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </section>

            <%-- Las pestañas principales separan datos personales, ediciones y programas del usuario. --%>
            <section class="content-card mb-5">
                <div class="card-header bg-white pt-3">
                    <ul class="nav nav-tabs card-header-tabs" role="tablist">
                        <li class="nav-item" role="presentation">
                            <button class="nav-link active" id="informacion-tab" data-bs-toggle="tab"
                                    data-bs-target="#informacion-panel" type="button" role="tab"
                                    aria-controls="informacion-panel" aria-selected="true">Información general</button>
                        </li>
                        <li class="nav-item" role="presentation">
                            <button class="nav-link" id="ediciones-tab" data-bs-toggle="tab"
                                    data-bs-target="#ediciones-panel" type="button" role="tab"
                                    aria-controls="ediciones-panel" aria-selected="false">Ediciones</button>
                        </li>
                        <li class="nav-item" role="presentation">
                            <button class="nav-link" id="programas-tab" data-bs-toggle="tab"
                                    data-bs-target="#programas-panel" type="button" role="tab"
                                    aria-controls="programas-panel" aria-selected="false">Programas</button>
                        </li>
                    </ul>
                </div>
                <div class="card-body tab-content p-4">
                    <div class="tab-pane fade show active" id="informacion-panel" role="tabpanel"
                         aria-labelledby="informacion-tab" tabindex="0">
                        <%-- Seguidos y seguidores son subpestañas para mantener ambas listas dentro de Información general. --%>
                        <ul class="nav nav-pills profile-subtabs mb-4" role="tablist"
                            aria-label="Información del usuario">
                            <li class="nav-item" role="presentation">
                                <button class="nav-link active" id="datos-tab" data-bs-toggle="tab"
                                        data-bs-target="#datos-panel" type="button" role="tab"
                                        aria-controls="datos-panel" aria-selected="true">Datos personales</button>
                            </li>
                            <li class="nav-item" role="presentation">
                                <button class="nav-link" id="seguidos-tab" data-bs-toggle="tab"
                                        data-bs-target="#seguidos-panel" type="button" role="tab"
                                        aria-controls="seguidos-panel" aria-selected="false">
                                    Seguidos <span class="badge text-bg-light"><c:out value="${usuario.seguidos.size()}"/></span>
                                </button>
                            </li>
                            <li class="nav-item" role="presentation">
                                <button class="nav-link" id="seguidores-tab" data-bs-toggle="tab"
                                        data-bs-target="#seguidores-panel" type="button" role="tab"
                                        aria-controls="seguidores-panel" aria-selected="false">
                                    Seguidores <span class="badge text-bg-light"><c:out value="${usuario.seguidores.size()}"/></span>
                                </button>
                            </li>
                        </ul>
                        <div class="tab-content profile-subtab-content">
                            <div class="tab-pane fade show active" id="datos-panel" role="tabpanel"
                                 aria-labelledby="datos-tab" tabindex="0">
                                <h2 class="h5 fw-bold mb-3">Datos personales</h2>
                                <dl class="row mb-0">
                                    <dt class="col-sm-4">Nickname</dt>
                                    <dd class="col-sm-8"><c:out value="${usuario.nickname}"/></dd>
                                    <dt class="col-sm-4">Correo</dt>
                                    <dd class="col-sm-8"><c:out value="${usuario.correo}"/></dd>
                                    <dt class="col-sm-4">Nombre</dt>
                                    <dd class="col-sm-8"><c:out value="${usuario.nombre}"/> <c:out value="${usuario.apellido}"/></dd>
                                    <dt class="col-sm-4">Fecha de nacimiento</dt>
                                    <dd class="col-sm-8"><c:out value="${usuario.fechaNacimiento}"/></dd>
                                    <c:if test="${tipoUsuario eq 'Docente'}">
                                        <dt class="col-sm-4">Institutos</dt>
                                        <dd class="col-sm-8">
                                            <div class="d-flex flex-wrap gap-2">
                                                <c:forEach items="${usuario.institutos}" var="instituto">
                                                    <c:url var="institutoUrl" value="/inicio">
                                                        <c:param name="instituto" value="${instituto}"/>
                                                    </c:url>
                                                    <a class="badge rounded-pill text-bg-light border text-decoration-none"
                                                       href="<c:out value='${institutoUrl}'/>">
                                                        <i class="bi bi-building me-1" aria-hidden="true"></i>
                                                        <c:out value="${instituto}"/>
                                                    </a>
                                                </c:forEach>
                                            </div>
                                        </dd>
                                    </c:if>
                                </dl>
                            </div>
                            <div class="tab-pane fade" id="seguidos-panel" role="tabpanel"
                                 aria-labelledby="seguidos-tab" tabindex="0">
                                <h2 class="h5 fw-bold mb-3">Usuarios que sigue</h2>
                                <c:choose>
                                    <c:when test="${not empty usuario.seguidos}">
                                        <%-- Los perfiles y las imágenes se cargan juntos en el servlet para evitar consultas por cada campo. --%>
                                        <div class="list-group list-group-flush profile-follow-list">
                                            <c:forEach items="${usuario.seguidos}" var="nicknameSeguido">
                                                <c:url var="perfilSeguidoUrl" value="/consulta-usuario">
                                                    <c:param name="nickname" value="${nicknameSeguido}"/>
                                                </c:url>
                                                <c:url var="imagenSeguidoUrl" value="/imagenes-usuario">
                                                    <c:param name="nombre" value="${imagenesRelacionadas[nicknameSeguido]}"/>
                                                </c:url>
                                                <div class="list-group-item d-flex align-items-center gap-3"
                                                     data-follow-row="<c:out value='${nicknameSeguido}'/>">
                                                    <a class="list-group-item-action d-flex align-items-center gap-3 p-0 flex-grow-1 text-decoration-none"
                                                       href="<c:out value='${perfilSeguidoUrl}'/>">
                                                        <img class="profile-follow-avatar"
                                                             src="<c:out value='${imagenSeguidoUrl}'/>"
                                                             alt="Foto de <c:out value='${perfilesRelacionados[nicknameSeguido].nombre}'/>">
                                                        <span class="profile-follow-name">
                                                            <strong><c:out value="${perfilesRelacionados[nicknameSeguido].nombre}"/>
                                                                <c:out value="${perfilesRelacionados[nicknameSeguido].apellido}"/></strong>
                                                            <small>@<c:out value="${nicknameSeguido}"/></small>
                                                        </span>
                                                        <i class="bi bi-arrow-up-right ms-auto text-muted" aria-hidden="true"></i>
                                                    </a>
                                                    <c:if test="${esPropietario and not empty sessionScope.usuarioNickname}">
                                                        <c:url var="dejarDeSeguirUrl" value="/dejar-de-seguir-usuario"/>
                                                        <c:url var="volverSeguidosUrl" value="/consulta-usuario">
                                                            <c:param name="nickname" value="${usuario.nickname}"/>
                                                        </c:url>
                                                        <form action="<c:out value='${dejarDeSeguirUrl}'/>" method="post"
                                                              data-follow-form data-following="true" data-follow-redirect>
                                                            <input type="hidden" name="nicknameASeguir"
                                                                   value="<c:out value='${nicknameSeguido}'/>">
                                                            <input type="hidden" name="volverA"
                                                                   value="<c:out value='${volverSeguidosUrl}'/>#seguidos-panel">
                                                            <button class="btn btn-sm btn-siguiendo" type="submit">
                                                                Dejar de seguir
                                                            </button>
                                                        </form>
                                                    </c:if>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <p class="text-muted mb-0">Este usuario todavía no sigue a nadie.</p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="tab-pane fade" id="seguidores-panel" role="tabpanel"
                                 aria-labelledby="seguidores-tab" tabindex="0">
                                <h2 class="h5 fw-bold mb-3">Seguidores</h2>
                                <c:choose>
                                    <c:when test="${not empty usuario.seguidores}">
                                        <div class="list-group list-group-flush profile-follow-list">
                                            <c:forEach items="${usuario.seguidores}" var="nicknameSeguidor">
                                                <c:url var="perfilSeguidorUrl" value="/consulta-usuario">
                                                    <c:param name="nickname" value="${nicknameSeguidor}"/>
                                                </c:url>
                                                <c:url var="imagenSeguidorUrl" value="/imagenes-usuario">
                                                    <c:param name="nombre" value="${imagenesRelacionadas[nicknameSeguidor]}"/>
                                                </c:url>
                                                <a class="list-group-item list-group-item-action"
                                                   href="<c:out value='${perfilSeguidorUrl}'/>">
                                                    <img class="profile-follow-avatar me-3"
                                                         src="<c:out value='${imagenSeguidorUrl}'/>"
                                                         alt="Foto de <c:out value='${perfilesRelacionados[nicknameSeguidor].nombre}'/>">
                                                    <span class="profile-follow-name">
                                                        <strong><c:out value="${perfilesRelacionados[nicknameSeguidor].nombre}"/>
                                                            <c:out value="${perfilesRelacionados[nicknameSeguidor].apellido}"/></strong>
                                                        <small>@<c:out value="${nicknameSeguidor}"/></small>
                                                    </span>
                                                    <i class="bi bi-arrow-up-right ms-auto text-muted" aria-hidden="true"></i>
                                                </a>
                                            </c:forEach>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <p class="text-muted mb-0">Este usuario todavía no tiene seguidores.</p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                    <div class="tab-pane fade" id="ediciones-panel" role="tabpanel"
                         aria-labelledby="ediciones-tab" tabindex="0">
                        <c:choose>
                            <c:when test="${not empty edicionesUsuario}">
                                <div class="list-group">
                                    <c:forEach items="${edicionesUsuario}" var="edicion">
                                        <div class="list-group-item d-flex flex-wrap align-items-center justify-content-between gap-2 py-3">
                                            <div>
                                                <strong><c:out value="${edicion.nombre}"/></strong>
                                                <c:if test="${not empty estadosPorEdicion[edicion.nombre]}">
                                                    <span class="badge text-bg-secondary ms-2">
                                                        <c:out value="${estadosPorEdicion[edicion.nombre]}"/>
                                                    </span>
                                                </c:if>
                                                <div class="small text-muted">
                                                    <c:out value="${edicion.fechaInicio}"/> –
                                                    <c:out value="${edicion.fechaFin}"/>
                                                </div>
                                            </div>
                                            <c:if test="${not empty cursosPorEdicion[edicion.nombre]}">
                                                <c:url var="detalleEdicionUrl" value="/consulta-edicion-curso">
                                                    <c:param name="curso" value="${cursosPorEdicion[edicion.nombre]}"/>
                                                    <c:param name="edicion" value="${edicion.nombre}"/>
                                                </c:url>
                                                <c:url var="cursoEdicionUrl" value="/consulta-curso">
                                                    <c:param name="nombre" value="${cursosPorEdicion[edicion.nombre]}"/>
                                                </c:url>
                                                <div class="d-flex flex-wrap gap-2">
                                                    <a class="btn btn-sm btn-outline-primary"
                                                       href="<c:out value='${detalleEdicionUrl}'/>">Ver edición</a>
                                                    <a class="btn btn-sm btn-outline-secondary"
                                                       href="<c:out value='${cursoEdicionUrl}'/>">Ver curso</a>
                                                </div>
                                            </c:if>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <p class="text-muted mb-3">No hay ediciones asociadas para mostrar.</p>
                                <c:if test="${esPropietario and tipoUsuario eq 'Estudiante'}">
                                    <c:url var="inscripcionUrl" value="/inscripcion-edicion"/>
                                    <a class="btn btn-primary" href="<c:out value='${inscripcionUrl}'/>">
                                        Buscar ediciones e inscribirme
                                    </a>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="tab-pane fade" id="programas-panel" role="tabpanel"
                         aria-labelledby="programas-tab" tabindex="0">
                        <c:choose>
                            <c:when test="${not empty programasUsuario}">
                                <ul class="list-group list-group-flush">
                                    <c:forEach items="${programasUsuario}" var="programa">
                                        <li class="list-group-item"><c:out value="${programa}"/></li>
                                    </c:forEach>
                                </ul>
                            </c:when>
                            <c:otherwise>
                                <p class="text-muted mb-0">No hay programas asociados para mostrar.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </section>
            <c:if test="${esPropietario}">
                <%-- El mismo fragmento de formulario se reutiliza aquí y en la pantalla de modificación independiente. --%>
                <div class="modal fade" id="editar-perfil-modal" tabindex="-1"
                     aria-labelledby="editar-perfil-titulo" aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered modal-lg">
                        <div class="modal-content">
                            <div class="modal-header">
                                <div>
                                    <h2 class="modal-title fs-5 fw-bold" id="editar-perfil-titulo">Modificar mis datos</h2>
                                    <p class="text-muted small mb-0">El nickname y el correo no se pueden cambiar.</p>
                                </div>
                                <button class="btn-close" type="button" data-bs-dismiss="modal"
                                        aria-label="Cerrar"></button>
                            </div>
                            <div class="modal-body">
                                <c:set var="formularioEnModal" value="true"/>
                                <%@ include file="formulario-modificar-usuario.jspf" %>
                            </div>
                        </div>
                    </div>
                </div>
            </c:if>
        </c:when>
        <c:otherwise>
            <div class="alert alert-info text-center" role="status">
                <i class="bi bi-info-circle-fill me-2" aria-hidden="true"></i>
                Buscá un usuario en la barra superior y seleccioná un resultado para ver su perfil.
            </div>
        </c:otherwise>
    </c:choose>
        </div>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<c:url var="perfilScriptUrl" value="/assets/js/perfil-usuario.js">
    <c:param name="v" value="20261012-6"/>
</c:url>
<script src="<c:out value='${perfilScriptUrl}'/>" defer></script>
</body>
</html>
