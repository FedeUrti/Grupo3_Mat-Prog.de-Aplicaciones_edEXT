package com.grupo3_mat.edEXT.Presentacion.Pantallas.InternalFrames;

import com.grupo3_mat.edEXT.Logica.Fabrica;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCategoria;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AltaCategoriaFrame extends JInternalFrame {
    private final JTextField nombreCategoria = new JTextField(24);

    public AltaCategoriaFrame() {
        super("Alta de Categoría", true, true, false, true);

        JPanel formulario = new JPanel(new BorderLayout(8, 8));
        formulario.add(new JLabel("Nombre:"), BorderLayout.WEST);
        formulario.add(nombreCategoria, BorderLayout.CENTER);

        JButton guardar = new JButton("Guardar");
        guardar.addActionListener(event -> guardarCategoria());
        JButton cancelar = new JButton("Cancelar");
        cancelar.addActionListener(event -> dispose());

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acciones.add(guardar);
        acciones.add(cancelar);

        setLayout(new BorderLayout(8, 8));
        add(formulario, BorderLayout.CENTER);
        add(acciones, BorderLayout.SOUTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
    }

    private void guardarCategoria() {
        String nombre = nombreCategoria.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre de la categoría.", "Dato requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            IControladorCategoria controlador = Fabrica.getInstance().getIControladorCategoria();
            if (controlador.listarCategorias().contains(nombre)) {
                int modificar = JOptionPane.showConfirmDialog(
                        this,
                        "La categoría '" + nombre + "' ya existe. ¿Desea modificar su nombre?",
                        "Categoría existente",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (modificar == JOptionPane.YES_OPTION) {
                    String nuevoNombre = JOptionPane.showInputDialog(this, "Nuevo nombre para la categoría:", nombre);
                    if (nuevoNombre == null) {
                        return;
                    }
                    nuevoNombre = nuevoNombre.trim();
                    if (nuevoNombre.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Ingrese un nombre válido.", "Dato requerido", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    controlador.modificarCategoria(nombre, nuevoNombre);
                    JOptionPane.showMessageDialog(this, "Categoría modificada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                }
                return;
            }
            controlador.altaCategoria(nombre);
            JOptionPane.showMessageDialog(this, "Categoría creada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "No se pudo crear la categoría", JOptionPane.ERROR_MESSAGE);
        }
    }
}
