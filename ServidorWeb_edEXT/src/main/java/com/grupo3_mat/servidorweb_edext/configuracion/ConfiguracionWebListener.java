package com.grupo3_mat.servidorweb_edext.configuracion;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class ConfiguracionWebListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent evento) {
        evento.getServletContext().setSessionTimeout(ConfiguracionWeb.get().getSessionTimeoutMinutes());
    }
}
