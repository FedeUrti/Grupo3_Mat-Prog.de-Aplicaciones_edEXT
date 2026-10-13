document.addEventListener("click", (event) => {
    const trigger = event.target.closest("[data-profile-tab]");
    if (!trigger) {
        return;
    }

    const tab = document.querySelector(`[data-bs-target="${trigger.dataset.profileTab}"]`);
    const informationTab = document.querySelector("#informacion-tab");
    if (!tab || !informationTab) {
        return;
    }

    if (!document.querySelector("#informacion-panel").classList.contains("active")) {
        informationTab.addEventListener("shown.bs.tab", () => {
            bootstrap.Tab.getOrCreateInstance(tab).show();
        }, {once: true});
        bootstrap.Tab.getOrCreateInstance(informationTab).show();
    } else {
        bootstrap.Tab.getOrCreateInstance(tab).show();
    }
});

document.addEventListener("DOMContentLoaded", () => {
    const panelTabs = {
        "#ediciones-panel": "#ediciones-tab",
        "#seguidos-panel": "#seguidos-tab"
    };
    const tabSelector = panelTabs[window.location.hash];
    const tab = tabSelector && document.querySelector(tabSelector);
    if (tab) {
        bootstrap.Tab.getOrCreateInstance(tab).show();
    }
});

document.addEventListener("submit", async (event) => {
    const form = event.target.closest("[data-profile-edit-form]");
    if (!form) {
        return;
    }

    event.preventDefault();
    const button = form.querySelector("[data-profile-edit-submit]");
    const error = form.querySelector("[data-profile-edit-error]");
    const originalButtonText = button.textContent;
    button.disabled = true;
    button.setAttribute("aria-busy", "true");
    button.textContent = "Guardando...";
    error.classList.add("d-none");
    error.textContent = "";

    try {
        const response = await fetch(form.action, {
            method: "POST",
            body: new FormData(form),
            headers: {"X-Requested-With": "XMLHttpRequest"}
        });

        if (response.redirected && new URL(response.url).pathname.endsWith("/login")) {
            window.location.assign(response.url);
            return;
        }

        if (response.status === 204) {
            const profileUrl = new URL(window.location.href);
            profileUrl.searchParams.set("actualizado", "1");
            window.location.assign(profileUrl);
            return;
        }

        const message = (await response.text()).trim();
        error.textContent = message || "No se pudieron guardar los cambios. Intentá nuevamente.";
    } catch {
        error.textContent = "No se pudo conectar con el servidor. Revisá tu conexión e intentá nuevamente.";
    }

    error.classList.remove("d-none");
    button.disabled = false;
    button.removeAttribute("aria-busy");
    button.textContent = originalButtonText;
});
