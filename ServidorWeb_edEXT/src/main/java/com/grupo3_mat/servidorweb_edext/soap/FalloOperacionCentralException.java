package com.grupo3_mat.servidorweb_edext.soap;

public class FalloOperacionCentralException extends RuntimeException {
    public FalloOperacionCentralException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
