package com.grupo3_mat.edEXT.Logica.DataTypes;

import java.time.LocalDate;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {
    @Override
    public LocalDate unmarshal(String value) {
        return value == null ? null : LocalDate.parse(value);
    }

    @Override
    public String marshal(LocalDate date) {
        return date == null ? null : date.toString();
    }
}
