package com.grupo3_mat.edEXT.Presentacion.Utils;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class GestorImagenes {

    private static final String DIRECTORIO_BASE = System.getProperty("user.home") 
            + File.separator + ".edEXT" 
            + File.separator + "imagenes";

    static {
        File dir = new File(DIRECTORIO_BASE);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static String guardarImagenLocal(File archivoOrigen, String nickname) throws IOException {
        if (archivoOrigen == null || !archivoOrigen.exists()) {
            return null;
        }

        String extension = "";
        int i = archivoOrigen.getName().lastIndexOf('.');
        if (i > 0) {
            extension = archivoOrigen.getName().substring(i);
        }

        String nombreFinal = nickname + "_" + System.currentTimeMillis() + extension;
        Path destino = Paths.get(DIRECTORIO_BASE, nombreFinal);

        Files.copy(archivoOrigen.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
        return nombreFinal;
    }

    /**
     * Carga la foto subida por el usuario si existe localmente.
     * Si no tiene o no se encuentra en el disco, carga el placeholder empaquetado en el proyecto.
     */
    public static void cargarImagenEnLabel(String nombreImagen, JLabel label, boolean esDocente) {
        File archivo = null;

        if (nombreImagen != null && !nombreImagen.trim().isEmpty() && !nombreImagen.equalsIgnoreCase("null")) {
            archivo = new File(DIRECTORIO_BASE + File.separator + nombreImagen);
            if (!archivo.exists()) {
                archivo = new File(nombreImagen);
            }
        }

        // 1. Si el usuario tiene imagen subida
        if (archivo != null && archivo.exists()) {
            desplegarImagen(new ImageIcon(archivo.getAbsolutePath()), label);
            return;
        }

        // 2. Si no tiene foto, cargar placeholder empaquetado
        String rutaRecurso = esDocente
                ? "/Presentacion/Recursos/docente_placeholder.png"
                : "/Presentacion/Recursos/estudiante_placeholder.png";

        URL urlPlaceholder = GestorImagenes.class.getResource(rutaRecurso);

        // --- LÍNEAS DE DIAGNÓSTICO EN CONSOLA ---
        System.out.println("Buscando placeholder en: " + rutaRecurso);
        System.out.println("Resultado de URL: " + urlPlaceholder);

        if (urlPlaceholder != null) {
            desplegarImagen(new ImageIcon(urlPlaceholder), label);
        } else {
            label.setIcon(null);
            label.setText(esDocente ? "[Docente sin foto]" : "[Estudiante sin foto]");
            label.revalidate();
            label.repaint();
        }
    }

    private static void desplegarImagen(ImageIcon icon, JLabel label) {
        int ancho = label.getWidth() > 0 ? label.getWidth() : 120;
        int alto = label.getHeight() > 0 ? label.getHeight() : 120;

        Image img = icon.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(img));
        label.setText("");

        // Fuerza a Swing a redibujar el componente inmediatamente
        label.revalidate();
        label.repaint();
    }
}