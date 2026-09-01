/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.grupo3_mat.edEXT.Presentacion.Pantallas.InternalFrames;
import com.grupo3_mat.edEXT.Logica.DataTypes.DTEdicionCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorInstituto;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorEdicion;
import com.grupo3_mat.edEXT.Logica.Fabrica;
import javax.swing.*;
import java.util.List;
import java.time.*;
import java.util.Date;
/**
 *
 * @author benja
 */
public class ConsultaEdicionCursoFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ConsultaEdicionCursoFrame.class.getName());

    private IControladorInstituto icInstituto;
    private IControladorCurso icCurso;
    private IControladorEdicion icEdicion;

    private DefaultListModel<String> listModelDocentes;

    /**
     * Creates new form ConsultaEdicionCursoFrame
     */
    //Para invocar normalmente
    //Para instanciar normalmente
    public ConsultaEdicionCursoFrame() {
        initComponents();
        Fabrica fabrica = Fabrica.getInstance();
        this.icInstituto = fabrica.getIControladorInstituto();
        this.icCurso = fabrica.getIControladorCurso();
        this.icEdicion = fabrica.getIControladorEdicion();

        listModelDocentes = new DefaultListModel<>();
        listDocentes.setModel(listModelDocentes);

        cargarInstitutos();
    }
    
    //Para invocar desde consulta curso
    public ConsultaEdicionCursoFrame(String nombreInstituto, String nombreCurso, String nombreEdicion) {
        this(); // Reutiliza la inicialización completa y carga inicial

        // Aplica la selección bloqueando eventos para que no se llamen en bucle
        cmbInstitutos.removeActionListener(this::cmbInstitutosActionPerformed);
        cmbCursos.removeActionListener(this::cmbCursosActionPerformed);
        cmbEdiciones.removeActionListener(this::cmbEdicionesActionPerformed);

        cmbInstitutos.setSelectedItem(nombreInstituto);
        alSeleccionarInstituto(); // Poblar cursos del instituto
        
        cmbCursos.setSelectedItem(nombreCurso);
        alSeleccionarCurso(); // Poblar ediciones del curso
        
        cmbEdiciones.setSelectedItem(nombreEdicion);
        alSeleccionarEdicion(); // Muestra el detalle

        // Volver a poner los listeners
        cmbInstitutos.addActionListener(this::cmbInstitutosActionPerformed);
        cmbCursos.addActionListener(this::cmbCursosActionPerformed);
        cmbEdiciones.addActionListener(this::cmbEdicionesActionPerformed);
    }

    // Cargar Institutos al iniciar
    private void cargarInstitutos() {
        cmbInstitutos.removeActionListener(this::cmbInstitutosActionPerformed);
        cmbInstitutos.removeAllItems();
        
        List<String> insts = icInstituto.listarInstitutos();
        if (insts != null) {
            for (String inst : insts) {
                cmbInstitutos.addItem(inst);
            }
        }
        
        cmbInstitutos.setSelectedIndex(-1); // Inicia sin selección si se prefiere
        cmbInstitutos.addActionListener(this::cmbInstitutosActionPerformed);
        limpiarCampos();
    }

    // Al cambiar de Instituto cargar Cursos
    private void alSeleccionarInstituto() {
        cmbCursos.removeActionListener(this::cmbCursosActionPerformed);
        cmbCursos.removeAllItems();
        limpiarCampos();
    
        String instSeleccionado = (String) cmbInstitutos.getSelectedItem();
        if (instSeleccionado != null) {
            List<String> cursos = icCurso.listarCursosPorInstituto(instSeleccionado);
            if (cursos != null) {
                for (String c : cursos) {
                    cmbCursos.addItem(c);
                }
            }
        }
        cmbCursos.setSelectedIndex(-1);
        cmbCursos.addActionListener(this::cmbCursosActionPerformed);
    }

    // Al cambiar de Curso cargar Ediciones
    private void alSeleccionarCurso() {
        cmbEdiciones.removeActionListener(this::cmbEdicionesActionPerformed);
        cmbEdiciones.removeAllItems();
        limpiarCampos();

        String cursoSeleccionado = (String) cmbCursos.getSelectedItem();
        if (cursoSeleccionado != null) {
            List<String> ediciones = icEdicion.listarEdicionesPorCurso(cursoSeleccionado);
            if (ediciones != null) {
                for (String ed : ediciones) {
                    cmbEdiciones.addItem(ed);
                }
            }
        }
        cmbEdiciones.setSelectedIndex(-1);
        cmbEdiciones.addActionListener(this::cmbEdicionesActionPerformed);
    }

    // Al cambiar de Edición consultar DataType y llenar los campos
    private void alSeleccionarEdicion() {
        limpiarCampos();

        String nombreEdicion = (String) cmbEdiciones.getSelectedItem();
        String nombreCurso = (String) cmbCursos.getSelectedItem();

        if (nombreEdicion != null && nombreCurso != null) {
            DTEdicionCurso dt = icEdicion.mostrarDetalleEdicion(nombreEdicion);

            if (dt != null) {
                txtNombreEdicion.setText(dt.getNombre() != null ? dt.getNombre() : "");
                txtFechaInicio.setText(dt.getFechaInicio() != null ? dt.getFechaInicio().toString() : "");
                txtFechaFin.setText(dt.getFechaFin() != null ? dt.getFechaFin().toString() : "");
                txtFechaPub.setText(dt.getFechaPublicacion() != null ? dt.getFechaPublicacion().toString() : "");
            
                if (dt.getCupo() > 0) {
                    txtCupo.setText(String.valueOf(dt.getCupo()));
                } else {
                    txtCupo.setText("Sin cupo");
                }

                listModelDocentes.clear();
                if (dt.getDocentes() != null) {
                    for (String doc : dt.getDocentes()) {
                        listModelDocentes.addElement(doc);
                    }
                }
            }
        }
    }


    private void limpiarCampos() {
        txtNombreEdicion.setText("");
        txtFechaInicio.setText("");
        txtFechaFin.setText("");
        txtFechaPub.setText("");
        txtCupo.setText("");
        listModelDocentes.clear();
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
        labelEdicion = new javax.swing.JLabel();
        cmbInstitutos = new javax.swing.JComboBox<>();
        cmbCursos = new javax.swing.JComboBox<>();
        cmbEdiciones = new javax.swing.JComboBox<>();
        labelNombre = new javax.swing.JLabel();
        labelFechaInicio = new javax.swing.JLabel();
        labelFechaFin = new javax.swing.JLabel();
        labelFechaPublicacion = new javax.swing.JLabel();
        labelCupo = new javax.swing.JLabel();
        labelDocentes = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listDocentes = new javax.swing.JList<>();
        txtNombreEdicion = new javax.swing.JTextField();
        txtFechaInicio = new javax.swing.JTextField();
        txtFechaPub = new javax.swing.JTextField();
        txtFechaFin = new javax.swing.JTextField();
        txtCupo = new javax.swing.JTextField();
        btnCerrar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Consulta de Edición de Curso");
        setResizable(false);

        labelInstituto.setText("Instituto:");

        labelCurso.setText("Curso:");

        labelEdicion.setText("Edición:");

        cmbInstitutos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbInstitutos.addActionListener(this::cmbInstitutosActionPerformed);

        cmbCursos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbCursos.addActionListener(this::cmbCursosActionPerformed);

        cmbEdiciones.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbEdiciones.addActionListener(this::cmbEdicionesActionPerformed);

        labelNombre.setText("Nombre:");

        labelFechaInicio.setText("Fecha de inicio:");

        labelFechaFin.setText("Fecha de finalización:");

        labelFechaPublicacion.setText("Fecha de publicación:");

        labelCupo.setText("Cupo:");

        labelDocentes.setText("Docentes:");

        listDocentes.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(listDocentes);

        txtNombreEdicion.setEditable(false);

        txtFechaInicio.setEditable(false);

        txtFechaPub.setEditable(false);

        txtFechaFin.setEditable(false);

        txtCupo.setEditable(false);

        btnCerrar.setText("Cerrar");
        btnCerrar.addActionListener(this::btnCerrarActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(81, 81, 81)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelInstituto)
                                    .addComponent(labelCurso, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(22, 22, 22)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cmbCursos, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cmbInstitutos, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(labelEdicion, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(cmbEdiciones, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(146, 146, 146)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(labelDocentes)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(labelFechaFin)
                                .addGap(24, 24, 24)
                                .addComponent(txtFechaFin))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(labelNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(83, 83, 83)
                                .addComponent(txtNombreEdicion))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(labelFechaInicio)
                                .addGap(56, 56, 56)
                                .addComponent(txtFechaInicio))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(labelCupo)
                                .addGap(108, 108, 108)
                                .addComponent(txtCupo))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(labelFechaPublicacion)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtFechaPub, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(405, 405, 405)
                        .addComponent(btnCerrar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(102, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(116, 116, 116)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelInstituto)
                            .addComponent(cmbInstitutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(45, 45, 45)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelCurso)
                            .addComponent(cmbCursos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelNombre)
                            .addComponent(txtNombreEdicion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(31, 31, 31)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelFechaInicio)
                            .addComponent(txtFechaInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(24, 24, 24)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelFechaFin)
                            .addComponent(txtFechaFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelFechaPublicacion)
                            .addComponent(txtFechaPub, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(25, 25, 25)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCupo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelCupo))))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelEdicion)
                            .addComponent(cmbEdiciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(labelDocentes)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                .addComponent(btnCerrar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void cmbInstitutosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbInstitutosActionPerformed
        alSeleccionarInstituto();
    }//GEN-LAST:event_cmbInstitutosActionPerformed

    private void cmbEdicionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbEdicionesActionPerformed
        alSeleccionarEdicion();
    }//GEN-LAST:event_cmbEdicionesActionPerformed

    private void cmbCursosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCursosActionPerformed
        alSeleccionarCurso();
    }//GEN-LAST:event_cmbCursosActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new ConsultaEdicionCursoFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrar;
    private javax.swing.JComboBox<String> cmbCursos;
    private javax.swing.JComboBox<String> cmbEdiciones;
    private javax.swing.JComboBox<String> cmbInstitutos;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelCupo;
    private javax.swing.JLabel labelCurso;
    private javax.swing.JLabel labelDocentes;
    private javax.swing.JLabel labelEdicion;
    private javax.swing.JLabel labelFechaFin;
    private javax.swing.JLabel labelFechaInicio;
    private javax.swing.JLabel labelFechaPublicacion;
    private javax.swing.JLabel labelInstituto;
    private javax.swing.JLabel labelNombre;
    private javax.swing.JList<String> listDocentes;
    private javax.swing.JTextField txtCupo;
    private javax.swing.JTextField txtFechaFin;
    private javax.swing.JTextField txtFechaInicio;
    private javax.swing.JTextField txtFechaPub;
    private javax.swing.JTextField txtNombreEdicion;
    // End of variables declaration//GEN-END:variables
}
