/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package com.grupo3_mat.edEXT.Presentacion.Pantallas.InternalFrames;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorInstituto;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorEdicion;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorUsuario;
import com.grupo3_mat.edEXT.Logica.Fabrica;
import javax.swing.*;
import java.util.List;
import java.time.*;
import java.util.Date;
/**
 *
 * @author fede1
 */
public class InscripcionEdicionCursoFrame extends javax.swing.JInternalFrame {

    private final DefaultListModel<String> listModelEstudiantes = new DefaultListModel<>();
    private String edicionVigenteActual = "";

    public InscripcionEdicionCursoFrame() {
        initComponents();
    
        // Configuración visual y selección
        txtEdicionVigente.setEditable(false);
        btnVerInformacion.setEnabled(false);
        cmbCursos.setEnabled(false);
    
        // Fuerza selección individual en la lista de estudiantes
        listEstudiante.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listEstudiante.setModel(listModelEstudiantes);
    
        // Inicializar fecha actual
        JFecha.setDate(new Date());
        JFecha.setDateFormatString("dd/MM/yyyy");

        // Carga de datos iniciales
        cargarInstitutos();
        cargarEstudiantes();
        
        cmbInstitutos.addItemListener(this::cmbInstitutosItemStateChanged);
        cmbCursos.addItemListener(this::cmbCursosItemStateChanged);
    }
    
    //Auxiliares
    private void cargarInstitutos() {
        cmbInstitutos.removeAllItems();
        cmbInstitutos.addItem("Seleccione un Instituto...");
    
        IControladorInstituto ici = Fabrica.getInstance().getIControladorInstituto();
        List<String> institutos = ici.listarInstitutos();
    
        for (String inst : institutos) {
        cmbInstitutos.addItem(inst);
        }
    }

    private void cargarEstudiantes() {
        listModelEstudiantes.clear();
        IControladorUsuario icu = Fabrica.getInstance().getIControladorUsuario();
        List<String> estudiantes = icu.listarEstudiantes();
    
        for (String est : estudiantes) {
            listModelEstudiantes.addElement(est);
        }
    }
    
    private void cmbInstitutosItemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            String instSeleccionado = (String) cmbInstitutos.getSelectedItem();
        
            cmbCursos.removeAllItems();
            txtEdicionVigente.setText("");
            edicionVigenteActual = "";
            btnVerInformacion.setEnabled(false);
        
            if (cmbInstitutos.getSelectedIndex() > 0) {
                cmbCursos.setEnabled(true);
                cmbCursos.addItem("Seleccione un Curso...");
            
                IControladorCurso icc = Fabrica.getInstance().getIControladorCurso();
                List<String> cursos = icc.listarCursosPorInstituto(instSeleccionado);
                for (String c : cursos) {
                    cmbCursos.addItem(c);
                }
            } else {
                cmbCursos.setEnabled(false);
            }
        }
    }

    private void cmbCursosItemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            if (cmbCursos.getSelectedIndex() > 0) {
                String cursoSeleccionado = (String) cmbCursos.getSelectedItem();
                obtenerEdicionVigente(cursoSeleccionado);
            } else {
                txtEdicionVigente.setText("");
                edicionVigenteActual = "";
                btnVerInformacion.setEnabled(false);
            }
        }
    }

    private void obtenerEdicionVigente(String nombreCurso) {
        try {
            IControladorCurso icc = Fabrica.getInstance().getIControladorCurso();
            // Se busca la edición cuya vigencia se solape o esté más próxima al día de hoy
            String edicion = icc.obtenerEdicionVigente(nombreCurso, LocalDate.now());
        
            if (edicion != null && !edicion.trim().isEmpty()) {
                this.edicionVigenteActual = edicion;
                txtEdicionVigente.setText(edicion);
                btnVerInformacion.setEnabled(true);
            } else {
                this.edicionVigenteActual = "";
                txtEdicionVigente.setText("Sin edición vigente");
                btnVerInformacion.setEnabled(false);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al consultar edición: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelInstituto = new javax.swing.JLabel();
        labelCurso = new javax.swing.JLabel();
        labelEdicionVigente = new javax.swing.JLabel();
        cmbInstitutos = new javax.swing.JComboBox<>();
        cmbCursos = new javax.swing.JComboBox<>();
        txtEdicionVigente = new javax.swing.JTextField();
        btnVerInformacion = new javax.swing.JButton();
        labelEstudiante = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listEstudiante = new javax.swing.JList<>();
        labelFechaInscripcion = new javax.swing.JLabel();
        JFecha = new com.toedter.calendar.JDateChooser();
        btnCancelar = new javax.swing.JButton();
        btnAceptar = new javax.swing.JButton();

        setTitle("Inscripción a Edición de Curso");

        labelInstituto.setText("Instituto:");

        labelCurso.setText("Curso:");

        labelEdicionVigente.setText("Edición vigente:");

        cmbInstitutos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cmbCursos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        txtEdicionVigente.setEditable(false);

        btnVerInformacion.setText("Ver información");
        btnVerInformacion.addActionListener(this::btnVerInformacionActionPerformed);

        labelEstudiante.setText("Estudiante:");

        listEstudiante.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(listEstudiante);

        labelFechaInscripcion.setText("Fecha de inscripción:");

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(this::btnCancelarActionPerformed);

        btnAceptar.setText("Aceptar");
        btnAceptar.addActionListener(this::btnAceptarActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(98, 98, 98)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnVerInformacion)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelInstituto)
                            .addComponent(labelCurso)
                            .addComponent(labelEdicionVigente))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbCursos, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtEdicionVigente, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbInstitutos, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 104, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelFechaInscripcion)
                    .addComponent(labelEstudiante)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btnAceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(JFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(117, 117, 117))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelInstituto)
                    .addComponent(cmbInstitutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelEstudiante))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelCurso)
                            .addComponent(cmbCursos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(39, 39, 39)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelEdicionVigente)
                    .addComponent(txtEdicionVigente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelFechaInscripcion))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnVerInformacion)
                    .addComponent(JFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 91, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(44, 44, 44))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnVerInformacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerInformacionActionPerformed
        if (!edicionVigenteActual.isEmpty()) {
            ConsultaEdicionCursoFrame frameConsulta = new ConsultaEdicionCursoFrame((String) cmbInstitutos.getSelectedItem(), (String) cmbCursos.getSelectedItem(), edicionVigenteActual);
            getDesktopPane().add(frameConsulta);
            frameConsulta.setVisible(true);
            frameConsulta.toFront();
        }
    }//GEN-LAST:event_btnVerInformacionActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarActionPerformed
        // 1. Validaciones de Selección
        if (cmbInstitutos.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un Instituto.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (cmbCursos.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un Curso.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (edicionVigenteActual.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El curso no tiene una edición vigente activa para inscripciones.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        String estudianteSeleccionado = listEstudiante.getSelectedValue();
        if (estudianteSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un Estudiante.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (JFecha.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Debe indicar una fecha válida.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Conversión de fecha JDateChooser -> LocalDate
        LocalDate fechaInscripcion = JFecha.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // 3. Confirmación y Registro
        try {
            IControladorEdicion ice = Fabrica.getInstance().getIControladorEdicion();
            ice.inscribirEstudianteAEdicion(estudianteSeleccionado, edicionVigenteActual, fechaInscripcion);
        
            JOptionPane.showMessageDialog(this, "Inscripción registrada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo realizar la inscripción: " + e.getMessage(), "Error de Registro", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnAceptarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser JFecha;
    private javax.swing.JButton btnAceptar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnVerInformacion;
    private javax.swing.JComboBox<String> cmbCursos;
    private javax.swing.JComboBox<String> cmbInstitutos;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelCurso;
    private javax.swing.JLabel labelEdicionVigente;
    private javax.swing.JLabel labelEstudiante;
    private javax.swing.JLabel labelFechaInscripcion;
    private javax.swing.JLabel labelInstituto;
    private javax.swing.JList<String> listEstudiante;
    private javax.swing.JTextField txtEdicionVigente;
    // End of variables declaration//GEN-END:variables
}