(() => {
    const form = document.querySelector("form[data-disponibilidad-url]");
    if (!form) {
        return;
    }

    const tipoUsuario = form.elements.tipoUsuario;
    const nickname = form.elements.nickname;
    const correo = form.elements.correo;
    const password = form.elements.password;
    const confirmarPassword = form.elements.confirmarPassword;
    const imagen = form.elements.imagen;
    const checksInstitutos = [...form.querySelectorAll('input[name="institutos"]')];
    const estadoInstitutos = document.getElementById("estadoInstitutos");
    const estadoConfirmarPassword = document.getElementById("estadoConfirmarPassword");
    const estadoImagen = document.getElementById("estadoImagen");
    const camposDisponibilidad = [
        {input: nickname, campo: "nickname", estado: document.getElementById("estadoNickname")},
        {input: correo, campo: "correo", estado: document.getElementById("estadoCorreo")}
    ];
    const estados = new Map(camposDisponibilidad.map(({input}) => [input, "inicial"]));
    const temporizadores = new Map();
    const solicitudes = new Map();
    const versiones = new Map(camposDisponibilidad.map(({input}) => [input, 0]));
    let reenviarAlCompletar = false;

    const mostrarEstado = (elemento, texto, clase = "text-muted") => {
        elemento.textContent = texto;
        elemento.className = `form-text ${clase}`;
    };

    const formatoValido = (input) => input.value.trim() !== ""
        && input.value.length <= Number(input.maxLength)
        && input.validity.typeMismatch === false
        && input.validity.patternMismatch === false;

    const envioListo = () => camposDisponibilidad.every(({input}) => estados.get(input) === "disponible");

    const enviarSiVerificado = () => {
        if (reenviarAlCompletar && envioListo()) {
            reenviarAlCompletar = false;
            form.requestSubmit();
        }
    };

    const verificarDisponibilidad = async (configuracion, version) => {
        const {input, campo, estado} = configuracion;
        if (versiones.get(input) !== version || !formatoValido(input)) {
            return;
        }

        estados.set(input, "verificando");
        input.setCustomValidity("");
        mostrarEstado(estado, "Verificando disponibilidad…");

        const controlador = new AbortController();
        solicitudes.set(input, controlador);
        const url = new URL(form.dataset.disponibilidadUrl, window.location.href);
        url.searchParams.set("campo", campo);
        url.searchParams.set("valor", input.value.trim());

        try {
            const respuesta = await fetch(url, {
                headers: {Accept: "application/json"},
                cache: "no-store",
                signal: controlador.signal
            });
            const resultado = await respuesta.json();
            if (!respuesta.ok || typeof resultado.disponible !== "boolean") {
                throw new Error("No se pudo verificar la disponibilidad.");
            }
            if (versiones.get(input) !== version) {
                return;
            }

            const disponible = resultado.disponible;
            estados.set(input, disponible ? "disponible" : "ocupado");
            if (disponible) {
                input.setCustomValidity("");
                mostrarEstado(estado,
                        campo === "nickname" ? "El nick está disponible." : "El correo está disponible.",
                        "text-success");
            } else {
                const mensaje = campo === "nickname"
                    ? "El Nick está en uso."
                    : "El correo ya está registrado.";
                input.setCustomValidity(mensaje);
                mostrarEstado(estado, mensaje, "text-danger");
            }
            enviarSiVerificado();
        } catch (error) {
            if (error.name === "AbortError" || versiones.get(input) !== version) {
                return;
            }
            estados.set(input, "error");
            input.setCustomValidity("No se pudo verificar la disponibilidad. Intentá nuevamente.");
            mostrarEstado(estado, input.validationMessage, "text-danger");
        } finally {
            if (solicitudes.get(input) === controlador) {
                solicitudes.delete(input);
            }
        }
    };

    const comprobarCampo = (configuracion, inmediata = false) => {
        const {input, estado} = configuracion;
        window.clearTimeout(temporizadores.get(input));
        const version = versiones.get(input) + 1;
        versiones.set(input, version);
        solicitudes.get(input)?.abort();
        input.setCustomValidity("");

        if (!input.value.trim()) {
            estados.set(input, "inicial");
            mostrarEstado(estado, "");
            return;
        }
        if (!formatoValido(input)) {
            estados.set(input, "invalido");
            mostrarEstado(estado, "");
            return;
        }

        estados.set(input, "pendiente");
        const demora = inmediata ? 0 : 400;
        temporizadores.set(input, window.setTimeout(
                () => verificarDisponibilidad(configuracion, version), demora));
    };

    camposDisponibilidad.forEach((configuracion) => {
        configuracion.input.addEventListener("input", () => {
            reenviarAlCompletar = false;
            comprobarCampo(configuracion);
        });
        configuracion.input.addEventListener("blur", () => comprobarCampo(configuracion, true));
        if (configuracion.input.value.trim()) {
            comprobarCampo(configuracion, true);
        }
    });

    const actualizarCoincidenciaPassword = () => {
        const coincide = confirmarPassword.value === password.value;
        confirmarPassword.setCustomValidity(coincide ? "" : "Las contraseñas no coinciden.");
        mostrarEstado(estadoConfirmarPassword,
                coincide || !confirmarPassword.value ? "" : "Las contraseñas no coinciden.",
                coincide ? "text-muted" : "text-danger");
    };
    password.addEventListener("input", actualizarCoincidenciaPassword);
    confirmarPassword.addEventListener("input", actualizarCoincidenciaPassword);

    const actualizarInstitutos = () => {
        const esDocente = tipoUsuario.value === "docente";
        const bloque = document.getElementById("bloqueInstitutos");
        bloque.hidden = !esDocente;
        const faltaInstituto = esDocente
            && (checksInstitutos.length === 0 || !checksInstitutos.some((check) => check.checked));
        const primero = checksInstitutos[0];
        if (primero) {
            primero.setCustomValidity(faltaInstituto ? "Seleccioná al menos un instituto para el docente." : "");
            tipoUsuario.setCustomValidity("");
        } else {
            tipoUsuario.setCustomValidity(faltaInstituto
                ? "No hay institutos disponibles para registrar un docente."
                : "");
        }
        mostrarEstado(estadoInstitutos,
                faltaInstituto
                    ? checksInstitutos.length === 0
                        ? "No hay institutos disponibles para registrar un docente."
                        : "Seleccioná al menos un instituto para el docente."
                    : "",
                esDocente ? "text-danger" : "text-muted");
    };
    tipoUsuario.addEventListener("change", actualizarInstitutos);
    checksInstitutos.forEach((check) => check.addEventListener("change", actualizarInstitutos));
    actualizarInstitutos();

    const actualizarImagen = () => {
        const archivo = imagen.files[0];
        if (!archivo) {
            imagen.setCustomValidity("");
            mostrarEstado(estadoImagen, "");
            return;
        }
        const tipoPermitido = ["image/png", "image/jpeg", "image/gif", "image/webp"].includes(archivo.type);
        const error = archivo.size > 5 * 1024 * 1024
            ? "La imagen debe pesar 5 MB o menos."
            : !tipoPermitido ? "Elegí una imagen PNG, JPEG, GIF o WebP." : "";
        imagen.setCustomValidity(error);
        mostrarEstado(estadoImagen, error, error ? "text-danger" : "text-success");
    };
    imagen.addEventListener("change", actualizarImagen);

    form.addEventListener("submit", (evento) => {
        actualizarCoincidenciaPassword();
        actualizarInstitutos();
        actualizarImagen();
        if (camposDisponibilidad.some(({input}) => estados.get(input) !== "disponible")) {
            evento.preventDefault();
            reenviarAlCompletar = true;
            camposDisponibilidad.forEach((configuracion) => {
                if (estados.get(configuracion.input) !== "disponible") {
                    comprobarCampo(configuracion, true);
                }
            });
            return;
        }
        if (!form.checkValidity()) {
            evento.preventDefault();
            form.reportValidity();
        }
    });
})();
