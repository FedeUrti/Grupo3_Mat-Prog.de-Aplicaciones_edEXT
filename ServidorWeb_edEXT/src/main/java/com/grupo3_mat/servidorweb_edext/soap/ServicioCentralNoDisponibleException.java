package com.grupo3_mat.servidorweb_edext.soap;

public class ServicioCentralNoDisponibleException extends RuntimeException {
    public ServicioCentralNoDisponibleException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
