/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package com.grupo3_mat.edEXT.Presentacion.Pantallas.InternalFrames;

import com.grupo3_mat.edEXT.Logica.DataTypes.DTEdicionCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorInstituto;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorEdicion;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorUsuario;
import com.grupo3_mat.edEXT.Logica.Fabrica;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
/**
 *
 * @author fede1
 */
public class AltaEdicionCursoFrame extends javax.swing.JInternalFrame {
    private IControladorCurso icCurso;
    private IControladorInstituto icInstituto;
    private IControladorEdicion icEdicion;
    private IControladorUsuario icUsuario;

    // Componentes de la Interfaz
    private JComboBox<String> cmbInstitutos;
    private JComboBox<String> cmbCursos;
    private JTextField txtNombreEdicion;
    private JSpinner spinnerFechaInicio;
    private JSpinner spinnerFechaFin;
    private JCheckBox chkTieneCupo;
    private JTextField txtCupo;
    private JList<String> listDocentes;
    private DefaultListModel<String> listModelDocentes;

    public AltaEdicionCursoFrame() {
        // Inicializar controladores desde la Fábrica
        Fabrica fabrica = Fabrica.getInstancia();
        this.icCurso = fabrica.getIControladorCurso();
        this.icInstituto = fabrica.getIControladorInstituto();
        this.icEdicion = fabrica.getIControladorEdicion();
        this.icUsuario = fabrica.getIControladorUsuario();

        // Configuración básica del JInternalFrame
        setTitle("Alta de Edición de Curso");
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setSize(500, 450);

        initUI();
        cargarInstitutos();
        cargarDocentes();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridLayout(9, 2, 5, 5));

        //Selección de Instituto
        formPanel.add(new JLabel("Instituto:"));
        cmbInstitutos = new JComboBox<>();
        cmbInstitutos.addActionListener(e -> alSeleccionarInstituto());
        formPanel.add(cmbInstitutos);

        //Selección de Curso
        formPanel.add(new JLabel("Curso:"));
        cmbCursos = new JComboBox<>();
        formPanel.add(cmbCursos);

        //Nombre de la Edición
        formPanel.add(new JLabel("Nombre Edición:"));
        txtNombreEdicion = new JTextField();
        formPanel.add(txtNombreEdicion);

        //Fechas
        formPanel.add(new JLabel("Fecha Inicio:"));
        spinnerFechaInicio = new JSpinner(new SpinnerDateModel());
        spinnerFechaInicio.setEditor(new JSpinner.DateEditor(spinnerFechaInicio, "dd/MM/yyyy"));
        formPanel.add(spinnerFechaInicio);

        formPanel.add(new JLabel("Fecha Fin:"));
        spinnerFechaFin = new JSpinner(new SpinnerDateModel());
        spinnerFechaFin.setEditor(new JSpinner.DateEditor(spinnerFechaFin, "dd/MM/yyyy"));
        formPanel.add(spinnerFechaFin);

        // 5. Cupo (Opcional)
        chkTieneCupo = new JCheckBox("Cupo:");
        txtCupo = new JTextField();
        txtCupo.setEnabled(false);
        //La checkbox habilita el campo para el cupo
        chkTieneCupo.addActionListener(e -> {
            boolean seleccionado = chkTieneCupo.isSelected();
            txtCupo.setEnabled(seleccionado);
            if (!seleccionado) {
            txtCupo.setText(""); // Limpia el texto al desmarcar
            }
        });
        
        formPanel.add(chkTieneCupo);
        formPanel.add(txtCupo);
        
        //Selección de Docentes
        formPanel.add(new JLabel("Docentes participantes:"));
        listModelDocentes = new DefaultListModel<>();
        listDocentes = new JList<>(listModelDocentes);
        listDocentes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollDocentes = new JScrollPane(listDocentes);
        scrollDocentes.setPreferredSize(new Dimension(150, 60));

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(scrollDocentes, BorderLayout.CENTER);

        //Botones Aceptar / Cancelar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");

        btnAceptar.addActionListener(e -> btnAceptarActionPerformed());
        btnCancelar.addActionListener(e -> dispose()); // Cierra la ventana actual

        buttonPanel.add(btnAceptar);
        buttonPanel.add(btnCancelar);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // Cargar ComboBox de Institutos
    private void cargarInstitutos() {
        cmbInstitutos.removeAllItems();
        List<String> insts = icInstituto.listarInstitutos();
        for (String inst : insts) {
            cmbInstitutos.addItem(inst);
        }
    }

    //Cargar JList de Docentes
    private void cargarDocentes() {
        listModelDocentes.clear();
        List<String> docentes = icUsuario.listarNicknamesUsuarios();
        for (String doc : docentes) {
            listModelDocentes.addElement(doc);
        }
    }

    // Evento al cambiar de instituto -> filtra los cursos
    private void alSeleccionarInstituto() {
        cmbCursos.removeAllItems();
        String instSeleccionado = (String) cmbInstitutos.getSelectedItem();
        if (instSeleccionado != null) {
            List<String> cursos = icCurso.listarCursosPorInstituto(instSeleccionado);
            for (String c : cursos) {
                cmbCursos.addItem(c);
            }
        }
    }

    // Evento del botón Aceptar
    private void btnAceptarActionPerformed() {
        try {
            // Validaciones de UI
            String nombreCurso = (String) cmbCursos.getSelectedItem();
            if (nombreCurso == null) {
                throw new Exception("Debe seleccionar un curso.");
            }

            String nombreEdicion = txtNombreEdicion.getText().trim();
            if (nombreEdicion.isEmpty()) {
                throw new Exception("Ingrese el nombre de la Edición.");
            }

            // Conversión de Fechas
            LocalDate fechaInicio = convertirALocalDate((Date) spinnerFechaInicio.getValue());
            LocalDate fechaFin = convertirALocalDate((Date) spinnerFechaFin.getValue());

            if (fechaFin.isBefore(fechaInicio)) {
                throw new Exception("La fecha de fin no puede ser anterior a la fecha de inicio.");
            }

            // Parseo de Cupo
            int cupo = 0;

            if (chkTieneCupo.isSelected()) {
                String textoCupo = txtCupo.getText().trim();
                if (textoCupo.isEmpty()) {
                    throw new Exception("Ingrese o deshabilite el cupo.");
                }
    
                try {
                    cupo = Integer.parseInt(textoCupo);
                    if (cupo <= 0) {
                        throw new Exception("El cupo debe ser mayor a 0.");
                    }
                } catch (NumberFormatException e) {
                    throw new Exception("El cupo debe ser un número entero válido.");
                }
            }

            // Docentes seleccionados
            List<String> docentesSeleccionados = listDocentes.getSelectedValuesList();

            // Construir DataType y llamar al Controlador
            DTEdicionCurso dtEdicion = new DTEdicionCurso(
                nombreEdicion,
                fechaInicio,
                fechaFin,
                cupo,
                LocalDate.now(),
                docentesSeleccionados
            );

            icEdicion.altaEdicionCurso(nombreCurso, dtEdicion);

            // Éxito: Notificar y cerrar
            JOptionPane.showMessageDialog(this, "La edición del curso se ha registrado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (Exception e) {
            // Captura de errores con diálogos
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error al Dar de Alta Edición", JOptionPane.ERROR_MESSAGE);
        }
    }

    private LocalDate convertirALocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    /**
     * Creates new form AltaEdicionCursoFrame
     */
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 394, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 274, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
