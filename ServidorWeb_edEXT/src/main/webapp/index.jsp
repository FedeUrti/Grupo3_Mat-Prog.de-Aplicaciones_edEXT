<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
    
    <style>
        body {
            background-color: #f4f6f9;
        }
        /* Estilos del Header */
        .custom-header {
            background-color: #1a2738 !important;
        }
        .logo-circle {
            width: 58px;
            height: 58px;
            border-radius: 50%;
            background-color: #2b5288;
            border: 2px solid #ffffff;
            color: #ffffff;
            font-weight: 800;
            font-size: 1.1rem;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        /* Estilos del Menú Lateral */
        .sidebar-title {
            font-size: 0.85rem;
            font-weight: 800;
            color: #333333;
            letter-spacing: 0.05em;
            margin-top: 0.8rem;
            margin-bottom: 0.6rem;
            padding-left: 0.2rem;
        }
        .custom-item {
            border: none;
            background-color: #eaeef3;
            color: #222222;
            font-weight: 500;
            font-size: 0.95rem;
            border-radius: 6px !important;
            margin-bottom: 4px;
            transition: all 0.2s ease;
        }
        .custom-item:hover, .custom-item.active {
            background-color: #d8e0e8;
            color: #000000;
        }
        /* Estilos de las Tarjetas de Contenido */
        .course-card {
            border-radius: 12px;
            border: 1px solid #e2e8f0;
            background-color: #ffffff;
            box-shadow: 0 2px 6px rgba(0,0,0,0.03);
        }
        .btn-read-more {
            background-color: #1c1c1c;
            color: #ffffff;
            border-radius: 8px;
            padding: 4px 16px;
            font-weight: 500;
            font-size: 0.9rem;
        }
        .btn-read-more:hover {
            background-color: #333333;
            color: #ffffff;
        }
    </style>
</head>
<body>

    <!-- 1. CABEZAL / NAVBAR -->
    <header class="custom-header py-2 px-4 shadow-sm">
        <div class="container-fluid d-flex align-items-center justify-content-between">
            
            <!-- Logo edEXT -->
            <a href="index.jsp" class="text-decoration-none">
                <div class="logo-circle">
                    edEXT
                </div>
            </a>

            <!-- Buscador Universal con Formato de Píldora -->
            <div class="w-50 mx-3">
                <form action="buscar" method="GET">
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

            <!-- Botones de Usuario (Visitante) -->
            <div class="d-flex gap-2">
                <a href="login.jsp" class="btn btn-outline-light px-3 py-1 fw-semibold">Iniciar Sesión</a>
                <a href="registro.jsp" class="btn btn-light text-dark px-3 py-1 fw-semibold">Registrarse</a>
            </div>

        </div>
    </header>

    <!-- 2. CUERPO PRINCIPAL (2 COLUMNAS) -->
    <div class="container-fluid my-4 px-4">
        <div class="row g-4">

            <!-- COLUMNA IZQUIERDA: MENÚ LATERAL (INSTITUTOS Y CATEGORÍAS) -->
            <div class="col-md-3 col-lg-2">
                <div class="bg-white p-3 rounded-3 shadow-sm">

                    <!-- SECCIÓN INSTITUTOS -->
                    <div class="sidebar-title text-uppercase">INSTITUTOS</div>
                    <div class="list-group list-group-flush">
                        <a href="consulta-curso?instituto=INCO" class="list-group-item list-group-item-action custom-item d-flex align-items-center gap-2">
                            <i class="bi bi-gear-wide-connected"></i> INCO
                        </a>
                        <a href="consulta-curso?instituto=IMERL" class="list-group-item list-group-item-action custom-item d-flex align-items-center gap-2">
                            <i class="bi bi-gear"></i> IMERL
                        </a>
                        <a href="consulta-curso?instituto=Fisica" class="list-group-item list-group-item-action custom-item d-flex align-items-center gap-2">
                            <i class="bi bi-compass"></i> Física
                        </a>
                        <a href="consulta-curso?instituto=IMPII" class="list-group-item list-group-item-action custom-item d-flex align-items-center gap-2">
                            <i class="bi bi-cpu"></i> IMPII
                        </a>
                        <a href="consulta-curso?instituto=Electrica" class="list-group-item list-group-item-action custom-item d-flex align-items-center gap-2">
                            <i class="bi bi-diagram-3"></i> Eléctrica
                        </a>
                        <a href="consulta-curso?instituto=DISI" class="list-group-item list-group-item-action custom-item d-flex align-items-center gap-2">
                            <i class="bi bi-grid-3x3"></i> DISI
                        </a>
                    </div>

                    <hr class="my-3 text-muted">

                    <!-- SECCIÓN CATEGORÍAS -->
                    <div class="sidebar-title text-uppercase">CATEGORÍAS</div>
                    <div class="list-group list-group-flush">
                        <a href="consulta-curso?categoria=Social" class="list-group-item list-group-item-action custom-item d-flex align-items-center gap-2">
                            <i class="bi bi-people"></i> Social
                        </a>
                        <a href="consulta-curso?categoria=Industrial" class="list-group-item list-group-item-action custom-item d-flex align-items-center gap-2">
                            <i class="bi bi-bag"></i> Industrial
                        </a>
                        <a href="consulta-curso?categoria=Educativos" class="list-group-item list-group-item-action custom-item d-flex align-items-center gap-2">
                            <i class="bi bi-mortarboard"></i> Educativos
                        </a>
                        <a href="consulta-curso?categoria=Interdisciplinario" class="list-group-item list-group-item-action custom-item d-flex align-items-center gap-2">
                            <i class="bi bi-list-task"></i> Interdisciplinario
                        </a>
                    </div>

                </div>
            </div>

            <!-- COLUMNA DERECHA: TARJETAS DE CURSOS / PROYECTOS -->
            <div class="col-md-9 col-lg-10">
                <div class="d-flex flex-column gap-3">

                    <!-- Tarjeta 1: ButiáX -->
                    <div class="card course-card p-3">
                        <div class="row align-items-center g-3">
                            <div class="col-md-4 text-center">
                                <img src="https://via.placeholder.com/300x120?text=BUTIÁX" class="img-fluid rounded" alt="Robot Butiá">
                            </div>
                            <div class="col-md-8">
                                <p class="card-text text-dark fs-5 fw-normal mb-2">
                                    La segunda etapa consiste en que trabajen en grupo sobre el diseño e implementación de una experiencia didáctica de inclusión del robot Butiá en el aula, utilizando los conocimientos aprendidos en clase.
                                </p>
                                <a href="consulta-curso?nombre=ButiaX" class="btn btn-read-more text-decoration-none">Leer más</a>
                            </div>
                        </div>
                    </div>

                    <!-- Tarjeta 2: Dalavuelta -->
                    <div class="card course-card p-3">
                        <div class="row align-items-center g-3">
                            <div class="col-md-4 text-center">
                                <img src="https://via.placeholder.com/300x120?text=Dalavuelta" class="img-fluid rounded" alt="Dalavuelta">
                            </div>
                            <div class="col-md-8">
                                <p class="card-text text-dark fs-5 fw-normal mb-2">
                                    <strong>Dalavuelta</strong> es un proyecto de extensión que nace en el Instituto de Ingeniería Mecánica y Producción Industrial (IIMPI) de Fing, que si bien inicia su trabajo en el desarrollo de bicicletas accesibles para...
                                </p>
                                <a href="consulta-curso?nombre=Dalavuelta" class="btn btn-read-more text-decoration-none">Leer más</a>
                            </div>
                        </div>
                    </div>

                    <!-- Tarjeta 3: Flor del Ceibo -->
                    <div class="card course-card p-3">
                        <div class="row align-items-center g-3">
                            <div class="col-md-4 text-center">
                                <img src="https://via.placeholder.com/300x120?text=Flor+del+Ceibo" class="img-fluid rounded" alt="Flor del Ceibo">
                            </div>
                            <div class="col-md-8">
                                <p class="card-text text-dark fs-5 fw-normal mb-2">
                                    <strong>Flor del Ceibo</strong> es un proyecto central de la Universidad de la República, que tiene misión por movilizar la participación de estudiantes universitarios en...
                                </p>
                                <a href="consulta-curso?nombre=FlorDelCeibo" class="btn btn-read-more text-decoration-none">Leer más</a>
                            </div>
                        </div>
                    </div>

                </div>
            </div>

        </div>
    </div>

    <!-- Bootstrap 5 JS Bundle CDN -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>