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
    const urlSeguirUsuario = contenedor.dataset.followUrl;
    const urlDejarDeSeguirUsuario = contenedor.dataset.unfollowUrl;
    const usuarioAutenticado = contenedor.dataset.authenticated === "true";
    const categorias = [
        { tipo: "Curso", etiqueta: "Cursos" },
        { tipo: "Edición", etiqueta: "Ediciones" },
        { tipo: "Programa", etiqueta: "Programas" },
        { tipo: "Usuario", etiqueta: "Usuarios" }
    ];
    const cacheBusqueda = new Map();
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
            const fila = document.createElement("div");
            fila.className = "global-search-result";
            fila.setAttribute("role", "group");
            const opcion = document.createElement("div");
            opcion.className = "global-search-result-option";
            opcion.id = `resultado-busqueda-${indice}`;
            opcion.setAttribute("role", "option");
            opcion.setAttribute("aria-selected", "false");

            if (resultado.imagenUrl) {
                const imagen = document.createElement("img");
                imagen.className = `search-result-image${resultado.tipo === "Usuario" ? " search-result-avatar" : ""}`;
                imagen.src = resultado.imagenUrl;
                imagen.alt = `${resultado.tipo === "Usuario" ? "Foto de " : "Imagen de "}${resultado.nombre}`;
                imagen.loading = "lazy";
                opcion.append(imagen);
            }

            const texto = document.createElement("span");
            texto.className = "global-search-result-text";
            const titulo = document.createElement("span");
            titulo.className = "global-search-result-title";
            if (resultado.url) {
                const enlaceTitulo = document.createElement("a");
                enlaceTitulo.href = resultado.url;
                enlaceTitulo.textContent = resultado.nombre;
                enlaceTitulo.addEventListener("click", (evento) => evento.stopPropagation());
                titulo.append(enlaceTitulo);
            } else {
                titulo.textContent = resultado.nombre;
            }
            const detalle = document.createElement("span");
            detalle.className = "global-search-result-detail";
            detalle.textContent = resultado.detalle;
            texto.append(titulo, detalle);

            const tipo = document.createElement("span");
            tipo.className = "global-search-result-type";
            tipo.textContent = resultado.tipo;
            opcion.append(texto, tipo);
            const formularioSeguimiento = crearFormularioSeguimiento(resultado);
            if (formularioSeguimiento) {
                fila.append(formularioSeguimiento);
            }
            fila.prepend(opcion);
            resultados.append(fila);
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
                    tarjeta.className = "content-card search-result-card p-3 d-flex align-items-center gap-3";
                    if (resultado.imagenUrl) {
                        const imagen = document.createElement("img");
                        imagen.className = `search-result-image${categoria.tipo === "Usuario" ? " search-result-avatar" : ""}`;
                        imagen.src = resultado.imagenUrl;
                        imagen.alt = `${categoria.tipo === "Usuario" ? "Foto de " : "Imagen de "}${resultado.nombre}`;
                        imagen.loading = "lazy";
                        tarjeta.append(imagen);
                    }
                    const texto = document.createElement("div");
                    texto.className = "min-w-0 flex-grow-1";
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
                    const formularioSeguimiento = crearFormularioSeguimiento(resultado);
                    if (formularioSeguimiento) {
                        tarjeta.append(formularioSeguimiento);
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

    const crearFormularioSeguimiento = (resultado) => {
        if (resultado.tipo !== "Usuario" || !resultado.nickname || resultado.propio
                || !usuarioAutenticado || !urlSeguirUsuario || !urlDejarDeSeguirUsuario) {
            return null;
        }
        const formulario = document.createElement("form");
        formulario.method = "post";
        formulario.action = resultado.siguiendo ? urlDejarDeSeguirUsuario : urlSeguirUsuario;
        formulario.dataset.followForm = "";
        formulario.dataset.following = String(Boolean(resultado.siguiendo));

        const nickname = document.createElement("input");
        nickname.type = "hidden";
        nickname.name = "nicknameASeguir";
        nickname.value = resultado.nickname;
        formulario.append(nickname);

        const boton = document.createElement("button");
        boton.className = `btn btn-sm flex-shrink-0 ${resultado.siguiendo ? "btn-siguiendo" : "btn-primary"}`;
        boton.type = "submit";
        boton.textContent = resultado.siguiendo ? "Dejar de seguir" : "Seguir";
        formulario.append(boton);
        return formulario;
    };

    document.addEventListener("submit", async (evento) => {
        const formulario = evento.target.closest("[data-follow-form]");
        if (!formulario) {
            return;
        }
        evento.preventDefault();
        const boton = formulario.querySelector("button[type='submit']");
        const estado = formulario.querySelector("[data-follow-status]");
        const siguiendo = formulario.dataset.following === "true";
        boton.disabled = true;
        boton.setAttribute("aria-busy", "true");
        if (estado) {
            estado.textContent = "";
        }

        try {
            const respuesta = await fetch(formulario.action, {
                method: "POST",
                body: new FormData(formulario),
                headers: { "X-Requested-With": "XMLHttpRequest" }
            });
            if (respuesta.status === 401 || (respuesta.redirected
                    && new URL(respuesta.url).pathname.endsWith("/login"))) {
                window.location.assign(respuesta.url);
                return;
            }
            if (!respuesta.ok) {
                const mensaje = (await respuesta.text()).trim();
                throw new Error(mensaje || "No se pudo actualizar el seguimiento.");
            }

            if (formulario.hasAttribute("data-follow-redirect")) {
                const volverA = formulario.querySelector("[name='volverA']")?.value;
                window.location.assign(volverA || window.location.href);
                return;
            }

            const nuevoEstado = !siguiendo;
            formulario.dataset.following = String(nuevoEstado);
            formulario.action = nuevoEstado ? urlDejarDeSeguirUsuario : urlSeguirUsuario;
            const nicknameSeguido = formulario.dataset.followNickname
                    || formulario.querySelector("[name='nicknameASeguir']")?.value;
            const formulariosRelacionados = [...document.querySelectorAll("[data-follow-form]")]
                .filter((formularioRelacionado) => {
                    const destino = formularioRelacionado.dataset.followNickname
                            || formularioRelacionado.querySelector("[name='nicknameASeguir']")?.value;
                    return destino === nicknameSeguido;
                });
            formulariosRelacionados.forEach((formularioRelacionado) => {
                formularioRelacionado.dataset.following = String(nuevoEstado);
                formularioRelacionado.action = nuevoEstado ? urlDejarDeSeguirUsuario : urlSeguirUsuario;
                const botonRelacionado = formularioRelacionado.querySelector("button[type='submit']");
                botonRelacionado?.classList.toggle("btn-siguiendo", nuevoEstado);
                botonRelacionado?.classList.toggle("btn-primary", !nuevoEstado);
                const etiqueta = botonRelacionado?.querySelector("[data-follow-label]");
                if (etiqueta) {
                    etiqueta.textContent = nuevoEstado ? "Dejar de seguir" : "Seguir";
                } else if (botonRelacionado) {
                    botonRelacionado.textContent = nuevoEstado ? "Dejar de seguir" : "Seguir";
                }
                const icono = botonRelacionado?.querySelector(".bi-person-check-fill, .bi-person-plus-fill");
                icono?.classList.toggle("bi-person-check-fill", nuevoEstado);
                icono?.classList.toggle("bi-person-plus-fill", !nuevoEstado);
            });

            const variacion = nuevoEstado ? 1 : -1;
            if (formulario.hasAttribute("data-profile-follow-target")) {
                document.querySelectorAll("[data-profile-target-follower-count]").forEach((contador) => {
                    contador.textContent = String(Math.max(0, Number(contador.textContent) + variacion));
                });
            }
            if (formulario.dataset.profileOwner === "true"
                    || formulario.dataset.profileRemoveOnUnfollow === "true") {
                document.querySelectorAll("[data-profile-following-count]").forEach((contador) => {
                    contador.textContent = String(Math.max(0, Number(contador.textContent) + variacion));
                });
            }
            if (!nuevoEstado) {
                const formularioDeSeguidos = formulariosRelacionados.find((formularioRelacionado) =>
                    formularioRelacionado.dataset.profileRemoveOnUnfollow === "true");
                const fila = formularioDeSeguidos?.closest("[data-profile-follow-row='following']");
                const lista = fila?.closest("[data-profile-follow-list='following']");
                fila?.remove();
                if (lista && !lista.querySelector("[data-profile-follow-row='following']")) {
                    lista.hidden = true;
                    const mensajeVacio = lista.parentElement.querySelector("[data-profile-follow-empty]");
                    if (mensajeVacio) {
                        mensajeVacio.hidden = false;
                    }
                }
            }
            if (formulario.dataset.profileOwner === "true"
                    && formulario.closest("[data-profile-follow-list='followers']")) {
                history.replaceState(null, "", `${window.location.pathname}${window.location.search}#seguidores-panel`);
            }
        } catch (error) {
            if (estado) {
                estado.textContent = error.message || "No se pudo actualizar el seguimiento.";
            } else {
                const aviso = document.createElement("span");
                aviso.className = "small text-danger";
                aviso.dataset.followStatus = "";
                aviso.setAttribute("role", "status");
                aviso.textContent = error.message || "No se pudo actualizar el seguimiento.";
                formulario.append(aviso);
            }
        } finally {
            boton.disabled = false;
            boton.removeAttribute("aria-busy");
        }
    });

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
        if (consulta.length < 2) {
            if (solicitud) {
                solicitud.abort();
                solicitud = null;
            }
            resultados.replaceChildren();
            cerrar();
            if (resultadosInicio) {
                resultadosInicio.replaceChildren();
                resultadosInicio.hidden = true;
                cursosInicio.hidden = false;
            }
            estado.textContent = "Escribí al menos 2 caracteres para buscar.";
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
        const claveCache = `${tipoSolicitado}:${consulta.toLocaleLowerCase()}`;
        const cacheado = cacheBusqueda.get(claveCache);
        if (cacheado && cacheado.vence > Date.now()) {
            mostrarResultados(cacheado.resultados);
            estado.textContent = cacheado.resultados.length
                ? `Se encontraron ${cacheado.resultados.length} resultados.`
                : "No se encontraron resultados.";
            return;
        }
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
            const listaResultados = Array.isArray(datos.results) ? datos.results : [];
            cacheBusqueda.set(claveCache, { resultados: listaResultados, vence: Date.now() + 10000 });
            if (cacheBusqueda.size > 30) {
                cacheBusqueda.delete(cacheBusqueda.keys().next().value);
            }
            mostrarResultados(listaResultados);
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
        temporizador = window.setTimeout(buscar, 100);
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
        } else if (evento.key === "Enter" && indiceActivo >= 0) {
            evento.preventDefault();
            const enlace = opciones[indiceActivo].querySelector("a[href]");
            if (enlace) {
                window.location.assign(enlace.href);
            }
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
