package com.grupo3_mat.edEXT.Logica.DataTypes;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class ValoracionesXml {
    @XmlElement(name = "valoracion")
    private List<Entrada> valoraciones = new ArrayList<>();

    public ValoracionesXml() {
    }

    public List<Entrada> getValoraciones() {
        return valoraciones;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Entrada {
        private int puntaje;
        private int cantidad;

        public Entrada() {
        }

        public Entrada(int puntaje, int cantidad) {
            this.puntaje = puntaje;
            this.cantidad = cantidad;
        }

        public int getPuntaje() {
            return puntaje;
        }

        public int getCantidad() {
            return cantidad;
        }
    }
}
