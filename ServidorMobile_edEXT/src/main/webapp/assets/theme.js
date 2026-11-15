(() => {
    const storageKey = "edext-mobile-theme";
    const toggles = [...document.querySelectorAll("[data-theme-toggle]")];

    const currentTheme = () => document.documentElement.dataset.theme === "dark" ? "dark" : "light";

    const updateToggles = () => {
        const dark = currentTheme() === "dark";
        const themeColor = document.querySelector('meta[name="theme-color"]');
        if (themeColor) {
            themeColor.content = dark ? "#0d1420" : "#f5f9ff";
        }
        toggles.forEach((toggle) => {
            const label = toggle.querySelector("[data-theme-label]");
            const action = dark ? "claro" : "oscuro";
            toggle.setAttribute("aria-label", `Cambiar a modo ${action}`);
            toggle.setAttribute("title", `Cambiar a modo ${action}`);
            toggle.setAttribute("aria-pressed", String(dark));
            if (label) {
                label.textContent = `Modo ${action}`;
            }
        });
    };

    toggles.forEach((toggle) => {
        toggle.addEventListener("click", () => {
            const theme = currentTheme() === "dark" ? "light" : "dark";
            document.documentElement.dataset.theme = theme;
            try {
                localStorage.setItem(storageKey, theme);
            } catch (error) {
                // El tema cambia en la página actual aunque no se pueda persistir.
            }
            updateToggles();
        });
    });

    updateToggles();
})();
