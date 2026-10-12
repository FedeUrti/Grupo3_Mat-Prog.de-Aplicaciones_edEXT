(() => {
    // La búsqueda se comparte entre vistas; en la portada los resultados se integran al contenido principal.
    const contenedor = document.querySelector("[data-global-search]");
    if (!contenedor) {
        return;
    }

    const formulario = contenedor.querySelector("[data-global-search-form]");
    const entrada = contenedor.querySelector("[data-global-search-input]");
    const resultados = contenedor.querySelector("[data-global-search-results]");
    const estado = contenedor.querySelector("[data-global-search-status]");
    const resultadosInicio = document.querySelector("[data-home-search-results]");
    const cursosInicio = document.querySelector("[data-home-course-list]");
    const filtro = contenedor.querySelector("[data-search-filter]");
    const urlBusqueda = contenedor.dataset.searchUrl;
    const categorias = [
        { tipo: "Curso", etiqueta: "Cursos" },
        { tipo: "Edición", etiqueta: "Ediciones" },
        { tipo: "Programa", etiqueta: "Programas" },
        { tipo: "Usuario", etiqueta: "Usuarios" }
    ];
    let temporizador;
    let solicitud;
    let indiceActivo = -1;
    const opcionesCursos = cursosInicio ? [...cursosInicio.querySelectorAll("[data-course-card]")] : [];
    const botonMasCursos = document.querySelector("[data-show-more-courses]");
    const incrementoCursos = Number(botonMasCursos?.dataset.increment || 8);
    let cursosVisibles = incrementoCursos;

    if (resultadosInicio) {
        entrada.removeAttribute("role");
        entrada.removeAttribute("aria-autocomplete");
        entrada.removeAttribute("aria-controls");
        entrada.removeAttribute("aria-expanded");
    } else {
        entrada.setAttribute("role", "combobox");
        entrada.setAttribute("aria-autocomplete", "list");
        entrada.setAttribute("aria-expanded", "false");
        entrada.setAttribute("aria-controls", "resultados-busqueda-global");
    }

    const actualizarDireccion = () => {
        const direccion = new URL(window.location.href);
        const consulta = entrada.value.trim();
        if (consulta) {
            direccion.searchParams.set("query", consulta);
        } else {
            direccion.searchParams.delete("query");
        }
        if (filtro && filtro.value !== "todos") {
            direccion.searchParams.set("tipo", filtro.value);
        } else {
            direccion.searchParams.delete("tipo");
        }
        window.history.replaceState(null, "", direccion);
    };

    const actualizarPaginacionCursos = () => {
        opcionesCursos.forEach((curso, indice) => {
            curso.hidden = indice >= cursosVisibles;
        });
        if (botonMasCursos) {
            botonMasCursos.hidden = cursosVisibles >= opcionesCursos.length;
        }
    };

    actualizarPaginacionCursos();
    botonMasCursos?.addEventListener("click", () => {
        cursosVisibles += incrementoCursos;
        actualizarPaginacionCursos();
    });

    // Cierra el panel y restablece los atributos ARIA del campo de búsqueda.
    const cerrar = () => {
        resultados.hidden = true;
        entrada.setAttribute("aria-expanded", "false");
        entrada.removeAttribute("aria-activedescendant");
        indiceActivo = -1;
    };

    // Los mensajes se agregan como texto plano para evitar interpretar como HTML lo que llega del servidor.
    const mostrarMensaje = (mensaje) => {
        if (resultadosInicio) {
            resultados.replaceChildren();
            resultadosInicio.replaceChildren();
            const alerta = document.createElement("div");
            alerta.className = "content-card p-4 text-center";
            alerta.textContent = mensaje;
            resultadosInicio.append(alerta);
            resultadosInicio.hidden = false;
            cursosInicio.hidden = true;
            return;
        }
        resultados.replaceChildren();
        const elemento = document.createElement("div");
        elemento.className = "global-search-empty";
        elemento.textContent = mensaje;
        resultados.append(elemento);
        resultados.hidden = false;
        entrada.setAttribute("aria-expanded", "true");
        entrada.removeAttribute("aria-activedescendant");
        indiceActivo = -1;
    };

    // Permite recorrer las coincidencias con las flechas y anuncia la opción activa a tecnologías de asistencia.
    const activarOpcion = (opciones, indice) => {
        if (!opciones.length) {
            return;
        }
        indiceActivo = (indice + opciones.length) % opciones.length;
        opciones.forEach((opcion, posicion) => {
            const activa = posicion === indiceActivo;
            opcion.classList.toggle("active", activa);
            opcion.setAttribute("aria-selected", String(activa));
        });
        entrada.setAttribute("aria-activedescendant", opciones[indiceActivo].id);
        opciones[indiceActivo].scrollIntoView({ block: "nearest" });
    };

    // Cada resultado se crea con nodos DOM; nunca se concatena el contenido de la respuesta como marcado HTML.
    const mostrarResultados = (lista) => {
        if (resultadosInicio) {
            mostrarResultadosEnInicio(lista);
            resultados.replaceChildren();
            cerrar();
            return;
        }
        resultados.replaceChildren();
        if (!lista.length) {
            mostrarMensaje("No se encontraron resultados.");
            return;
        }

        lista.forEach((resultado, indice) => {
            const opcion = resultado.url ? document.createElement("a") : document.createElement("div");
            opcion.className = "global-search-result";
            opcion.id = `resultado-busqueda-${indice}`;
            opcion.setAttribute("role", "option");
            opcion.setAttribute("aria-selected", "false");
            if (resultado.url) {
                opcion.href = resultado.url;
            }

            const texto = document.createElement("span");
            texto.className = "global-search-result-text";
            const titulo = document.createElement("span");
            titulo.className = "global-search-result-title";
            titulo.textContent = resultado.nombre;
            const detalle = document.createElement("span");
            detalle.className = "global-search-result-detail";
            detalle.textContent = resultado.detalle;
            texto.append(titulo, detalle);

            const tipo = document.createElement("span");
            tipo.className = "global-search-result-type";
            tipo.textContent = resultado.tipo;
            opcion.append(texto, tipo);
            resultados.append(opcion);
        });

        resultados.hidden = false;
        entrada.setAttribute("aria-expanded", "true");
        indiceActivo = -1;
    };

    // En la portada los resultados ocupan el área principal y se separan en pestañas por tipo.
    const mostrarResultadosEnInicio = (lista) => {
        resultadosInicio.replaceChildren();
        resultadosInicio.hidden = false;
        cursosInicio.hidden = true;
        if (!lista.length) {
            const vacio = document.createElement("div");
            vacio.className = "content-card p-4 text-center";
            const icono = document.createElement("i");
            icono.className = "bi bi-search display-5 text-primary";
            icono.setAttribute("aria-hidden", "true");
            const titulo = document.createElement("h2");
            titulo.className = "h5 fw-bold mt-3";
            titulo.textContent = "No se encontraron resultados";
            const detalle = document.createElement("p");
            detalle.className = "text-muted mb-0";
            detalle.textContent = "Probá con otro nombre, nickname, instituto o programa.";
            vacio.append(icono, titulo, detalle);
            resultadosInicio.append(vacio);
            return;
        }

        const navegacion = document.createElement("div");
        navegacion.className = "nav nav-tabs search-category-tabs mb-3";
        navegacion.setAttribute("role", "tablist");
        navegacion.setAttribute("aria-label", "Categorías de resultados");
        const paneles = document.createElement("div");
        paneles.className = "tab-content";
        const primeraCategoriaConResultados = categorias.findIndex((categoria) =>
            lista.some((resultado) => resultado.tipo === categoria.tipo));
        const indiceInicial = primeraCategoriaConResultados < 0 ? 0 : primeraCategoriaConResultados;

        categorias.forEach((categoria, indice) => {
            const coincidencias = lista.filter((resultado) => resultado.tipo === categoria.tipo);
            const boton = document.createElement("button");
            const panelId = `busqueda-panel-${indice}`;
            const botonId = `busqueda-tab-${indice}`;
            const activoInicial = indice === indiceInicial;
            boton.className = `nav-link${activoInicial ? " active" : ""}`;
            boton.id = botonId;
            boton.type = "button";
            boton.setAttribute("role", "tab");
            boton.setAttribute("aria-controls", panelId);
            boton.setAttribute("aria-selected", String(activoInicial));
            boton.textContent = `${categoria.etiqueta} (${coincidencias.length})`;

            const panel = document.createElement("section");
            panel.className = `tab-pane fade${activoInicial ? " show active" : ""}`;
            panel.id = panelId;
            panel.setAttribute("role", "tabpanel");
            panel.setAttribute("aria-labelledby", botonId);
            panel.tabIndex = 0;
            panel.hidden = !activoInicial;

            if (!coincidencias.length) {
                const vacio = document.createElement("p");
                vacio.className = "content-card p-3 text-muted mb-0";
                vacio.textContent = `No se encontraron ${categoria.etiqueta.toLowerCase()}.`;
                panel.append(vacio);
            } else {
                const listaResultados = document.createElement("div");
                listaResultados.className = "d-flex flex-column gap-2";
                coincidencias.forEach((resultado) => {
                    const tarjeta = document.createElement("article");
                    tarjeta.className = "content-card search-result-card p-3 d-flex align-items-center justify-content-between gap-3";
                    const texto = document.createElement("div");
                    texto.className = "min-w-0";
                    const titulo = document.createElement("h3");
                    titulo.className = "h6 fw-bold mb-1";
                    if (resultado.url) {
                        const enlaceTitulo = document.createElement("a");
                        enlaceTitulo.className = "search-result-link";
                        enlaceTitulo.href = resultado.url;
                        enlaceTitulo.textContent = resultado.nombre;
                        titulo.append(enlaceTitulo);
                    } else {
                        titulo.textContent = resultado.nombre;
                    }
                    const detalle = document.createElement("p");
                    detalle.className = "text-muted small mb-0";
                    detalle.textContent = resultado.detalle;
                    texto.append(titulo, detalle);
                    tarjeta.append(texto);
                    if (resultado.url) {
                        const enlaceAccion = document.createElement("a");
                        enlaceAccion.className = "btn btn-outline-primary btn-sm flex-shrink-0";
                        enlaceAccion.href = resultado.url;
                        enlaceAccion.textContent = categoria.tipo === "Usuario" ? "Ver perfil" : "Ver";
                        tarjeta.append(enlaceAccion);
                    }
                    listaResultados.append(tarjeta);
                });
                panel.append(listaResultados);
            }

            boton.addEventListener("click", () => {
                navegacion.querySelectorAll('[role="tab"]').forEach((elemento) => {
                    const activo = elemento === boton;
                    elemento.classList.toggle("active", activo);
                    elemento.setAttribute("aria-selected", String(activo));
                });
                paneles.querySelectorAll('[role="tabpanel"]').forEach((elemento) => {
                    const activo = elemento === panel;
                    elemento.hidden = !activo;
                    elemento.classList.toggle("active", activo);
                    elemento.classList.toggle("show", activo);
                });
            });
            navegacion.append(boton);
            paneles.append(panel);
        });

        resultadosInicio.append(navegacion, paneles);
    };

    const buscar = async () => {
        const consulta = entrada.value.trim();
        if (!consulta) {
            if (solicitud) {
                solicitud.abort();
            }
            resultados.replaceChildren();
            cerrar();
            if (resultadosInicio) {
                resultadosInicio.replaceChildren();
                resultadosInicio.hidden = true;
                cursosInicio.hidden = false;
            }
            estado.textContent = "";
            return;
        }
        if (consulta.length > 100) {
            mostrarMensaje("La búsqueda no puede superar los 100 caracteres.");
            estado.textContent = "La búsqueda no puede superar los 100 caracteres.";
            return;
        }
        actualizarDireccion();
        if (resultadosInicio) {
            resultadosInicio.hidden = false;
            cursosInicio.hidden = true;
            resultadosInicio.replaceChildren();
            const cargando = document.createElement("div");
            cargando.className = "content-card p-3 text-muted";
            cargando.textContent = "Buscando cursos, ediciones, programas y usuarios...";
            resultadosInicio.append(cargando);
        }

        if (solicitud) {
            // Cancela la petición anterior para que una respuesta lenta no reemplace una búsqueda más nueva.
            solicitud.abort();
        }
        solicitud = new AbortController();
        const tipoSolicitado = filtro ? filtro.value : "todos";
        estado.textContent = "Buscando...";
        try {
            // Se construye la URL con la API de parámetros para codificar correctamente espacios y acentos.
            const url = new URL(urlBusqueda, window.location.href);
            url.searchParams.set("q", consulta);
            if (tipoSolicitado !== "todos") {
                url.searchParams.set("tipo", tipoSolicitado);
            }
            const respuesta = await fetch(url, {
                headers: { Accept: "application/json" },
                signal: solicitud.signal
            });
            if (!respuesta.ok) {
                throw new Error("No se pudo completar la búsqueda.");
            }
            const datos = await respuesta.json();
            // Una respuesta antigua puede terminar justo antes de cancelarse; se descarta si el texto cambió.
            if (entrada.value.trim() !== consulta
                    || (filtro && filtro.value !== tipoSolicitado)) {
                return;
            }
            mostrarResultados(Array.isArray(datos.results) ? datos.results : []);
            estado.textContent = datos.results && datos.results.length
                ? `Se encontraron ${datos.results.length} resultados.`
                : "No se encontraron resultados.";
        } catch (error) {
            // Las cancelaciones son normales al escribir; los demás fallos sí se anuncian en el desplegable.
            if (error.name === "AbortError") {
                return;
            }
            mostrarMensaje("No se pudo completar la búsqueda. Intentá nuevamente.");
            estado.textContent = "No se pudo completar la búsqueda.";
        }
    };

    entrada.addEventListener("input", () => {
        // Espera brevemente entre teclas para evitar enviar una petición por cada carácter escrito.
        window.clearTimeout(temporizador);
        actualizarDireccion();
        temporizador = window.setTimeout(buscar, 160);
    });

    filtro?.addEventListener("change", () => {
        actualizarDireccion();
        window.clearTimeout(temporizador);
        buscar();
    });

    formulario.addEventListener("submit", (evento) => {
        if (resultadosInicio) {
            // En inicio actualiza la dirección y los resultados sin recargar ni abandonar la portada.
            evento.preventDefault();
            actualizarDireccion();
            window.clearTimeout(temporizador);
            buscar();
        }
    });

    entrada.addEventListener("keydown", (evento) => {
        const opciones = [...resultados.querySelectorAll('[role="option"]')];
        // Las teclas de dirección permiten navegar los resultados sin usar el ratón.
        if (evento.key === "ArrowDown" && opciones.length) {
            evento.preventDefault();
            activarOpcion(opciones, indiceActivo + 1);
        } else if (evento.key === "ArrowUp" && opciones.length) {
            evento.preventDefault();
            activarOpcion(opciones, indiceActivo < 0 ? opciones.length - 1 : indiceActivo - 1);
        } else if (evento.key === "Enter" && indiceActivo >= 0 && opciones[indiceActivo].href) {
            evento.preventDefault();
            window.location.assign(opciones[indiceActivo].href);
        } else if (evento.key === "Escape") {
            if (resultadosInicio) {
                entrada.value = "";
                entrada.dispatchEvent(new Event("input", { bubbles: true }));
            } else {
                cerrar();
            }
        }
    });

    document.addEventListener("click", (evento) => {
        // Al hacer clic fuera del buscador se cierra el panel, como en un menú desplegable.
        if (!resultadosInicio && !contenedor.contains(evento.target)) {
            cerrar();
        }
    });

    // Una búsqueda compartida o recargada en la URL se vuelve a pintar al abrir la portada.
    if (resultadosInicio && entrada.value.trim()) {
        buscar();
    }
})();
