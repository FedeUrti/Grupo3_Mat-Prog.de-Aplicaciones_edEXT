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
/**
 *
 * @author benja
 */
public class ConsultaEdicionCursoFrame extends javax.swing.JInternalFrame {
    
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
    
    // Para invocar desde consulta curso
    public ConsultaEdicionCursoFrame(String nombreInstituto, String nombreCurso, String nombreEdicion) {
        this(); // Inicializa componentes y carga la lista de institutos

        // Desactivar listeners para que no se disparen eventos en cadena durante la carga
        cmbInstitutos.removeActionListener(this::cmbInstitutosActionPerformed);
        cmbCursos.removeActionListener(this::cmbCursosActionPerformed);
        cmbEdiciones.removeActionListener(this::cmbEdicionesActionPerformed);

        // 1. Seleccionar instituto y cargar sus cursos
        cmbInstitutos.setSelectedItem(nombreInstituto);
        alSeleccionarInstituto();

        // 2. Seleccionar curso y cargar sus ediciones
        cmbCursos.setSelectedItem(nombreCurso);
        alSeleccionarCurso();

        // 3. Seleccionar edición y cargar detalle
        cmbEdiciones.setSelectedItem(nombreEdicion);
        alSeleccionarEdicion();

        // Reactivar listeners para la interacción del usuario
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
        cmbCursos.setEnabled(false);
        cmbEdiciones.setEnabled(false);
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
            cmbCursos.setSelectedIndex(-1);
            cmbCursos.setEnabled(true); // <--- Habilitamos si hay selección
        } else {
            cmbCursos.setEnabled(false); // <--- Si no hay instituto, deshabilitamos
        }

        // Como cambió el instituto, el combo de ediciones debe quedar deshabilitado y limpio
        cmbEdiciones.removeAllItems();
        cmbEdiciones.setEnabled(false);

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
            cmbEdiciones.setSelectedIndex(-1);
            cmbEdiciones.setEnabled(true); // <--- Habilitamos si hay selección
        } else {
            cmbEdiciones.setEnabled(false); // <--- Si no hay curso, deshabilitamos
        }

        cmbEdiciones.addActionListener(this::cmbEdicionesActionPerformed);
    }

    // Al cambiar de Edición consultar DataType y llenar los campos
    private void alSeleccionarEdicion() {
        limpiarCampos();

        String nombreEdicion = (String) cmbEdiciones.getSelectedItem();

        if (nombreEdicion != null) {
            DTEdicionCurso dt = icEdicion.mostrarDetalleEdicion(nombreEdicion);

            if (dt != null) {
                txtNombreEdicion.setText(dt.getNombre() != null ? dt.getNombre() : "");
                txtFechaInicio.setText(dt.getFechaInicio() != null ? dt.getFechaInicio().toString() : "");
                txtFechaFin.setText(dt.getFechaFin() != null ? dt.getFechaFin().toString() : "");
                txtFechaPub.setText(dt.getFechaPublicacion() != null ? dt.getFechaPublicacion().toString() : "");
            
                if (dt.getCupo() > 0) {
                    txtCupo.setText(String.valueOf(dt.getCupo()));
                } else {
                    txtCupo.setText("Sin límite de cupo.");
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

    public void cargarDatosEdicion(String nombreEdicion) {
        try {
            IControladorEdicion ice = Fabrica.getInstance().getIControladorEdicion();
            DTEdicionCurso dt = ice.mostrarDetalleEdicion(nombreEdicion); // O la llamada correspondiente en tu controlador

            if (dt != null) {
                txtNombreEdicion.setText(dt.getNombre());
                txtFechaInicio.setText(dt.getFechaInicio() != null ? dt.getFechaInicio().toString() : "");
                txtFechaFin.setText(dt.getFechaFin() != null ? dt.getFechaFin().toString() : "");
                txtCupo.setText(String.valueOf(dt.getCupo()));

                // Cargar docentes inscritos si aplica
                DefaultListModel<String> modDocentes = new DefaultListModel<>();
                if (dt.getDocentes() != null) {
                    for (String doc : dt.getDocentes()) {
                        modDocentes.addElement(doc);
                    }
                }
                listDocentes.setModel(modDocentes);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar edición: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

        btnCerrar = new javax.swing.JButton();
        panelSeleccion = new javax.swing.JPanel();
        cmbEdiciones = new javax.swing.JComboBox<>();
        labelInstituto = new javax.swing.JLabel();
        labelCurso = new javax.swing.JLabel();
        labelEdicion = new javax.swing.JLabel();
        cmbInstitutos = new javax.swing.JComboBox<>();
        cmbCursos = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        txtFechaPub = new javax.swing.JTextField();
        labelNombre = new javax.swing.JLabel();
        txtFechaFin = new javax.swing.JTextField();
        labelFechaInicio = new javax.swing.JLabel();
        txtCupo = new javax.swing.JTextField();
        labelFechaFin = new javax.swing.JLabel();
        labelFechaPublicacion = new javax.swing.JLabel();
        labelCupo = new javax.swing.JLabel();
        labelDocentes = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listDocentes = new javax.swing.JList<>();
        txtNombreEdicion = new javax.swing.JTextField();
        txtFechaInicio = new javax.swing.JTextField();

        setTitle("Consultar Edición de Curso");

        btnCerrar.setText("Cerrar");
        btnCerrar.addActionListener(this::btnCerrarActionPerformed);

        panelSeleccion.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Seleccione", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 18))); // NOI18N

        cmbEdiciones.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbEdiciones.addActionListener(this::cmbEdicionesActionPerformed);

        labelInstituto.setText("Instituto:");

        labelCurso.setText("Curso:");

        labelEdicion.setText("Edición:");

        cmbInstitutos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbInstitutos.addActionListener(this::cmbInstitutosActionPerformed);

        cmbCursos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbCursos.addActionListener(this::cmbCursosActionPerformed);

        javax.swing.GroupLayout panelSeleccionLayout = new javax.swing.GroupLayout(panelSeleccion);
        panelSeleccion.setLayout(panelSeleccionLayout);
        panelSeleccionLayout.setHorizontalGroup(
            panelSeleccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSeleccionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSeleccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelSeleccionLayout.createSequentialGroup()
                        .addGroup(panelSeleccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelInstituto)
                            .addComponent(labelCurso, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(22, 22, 22)
                        .addGroup(panelSeleccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbCursos, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbInstitutos, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelSeleccionLayout.createSequentialGroup()
                        .addComponent(labelEdicion, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cmbEdiciones, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelSeleccionLayout.setVerticalGroup(
            panelSeleccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSeleccionLayout.createSequentialGroup()
                .addGap(123, 123, 123)
                .addGroup(panelSeleccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelInstituto)
                    .addComponent(cmbInstitutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addGroup(panelSeleccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelCurso)
                    .addComponent(cmbCursos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39)
                .addGroup(panelSeleccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelEdicion)
                    .addComponent(cmbEdiciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Información sobre la edición", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 18))); // NOI18N

        txtFechaPub.setEditable(false);

        labelNombre.setText("Nombre:");

        txtFechaFin.setEditable(false);

        labelFechaInicio.setText("Fecha de inicio:");

        txtCupo.setEditable(false);

        labelFechaFin.setText("Fecha de finalización:");

        labelFechaPublicacion.setText("Fecha de publicación:");

        labelCupo.setText("Cupo:");

        labelDocentes.setText("Docentes:");

        jScrollPane1.setViewportView(listDocentes);

        txtNombreEdicion.setEditable(false);

        txtFechaInicio.setEditable(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(labelDocentes)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(labelFechaFin)
                        .addGap(24, 24, 24)
                        .addComponent(txtFechaFin))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(labelNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(49, 49, 49)
                        .addComponent(txtNombreEdicion))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(labelFechaInicio)
                        .addGap(56, 56, 56)
                        .addComponent(txtFechaInicio))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(labelCupo)
                        .addGap(108, 108, 108)
                        .addComponent(txtCupo))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(labelFechaPublicacion)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtFechaPub, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelNombre)
                    .addComponent(txtNombreEdicion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelFechaInicio)
                    .addComponent(txtFechaInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelFechaFin)
                    .addComponent(txtFechaFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelFechaPublicacion)
                    .addComponent(txtFechaPub, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCupo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelCupo))
                .addGap(43, 43, 43)
                .addComponent(labelDocentes)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(81, 81, 81)
                        .addComponent(panelSeleccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(125, 125, 125)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(386, 386, 386)
                        .addComponent(btnCerrar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(71, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelSeleccion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                .addComponent(btnCerrar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void cmbCursosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCursosActionPerformed
        alSeleccionarCurso();
    }//GEN-LAST:event_cmbCursosActionPerformed

    private void cmbInstitutosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbInstitutosActionPerformed
        alSeleccionarInstituto();
    }//GEN-LAST:event_cmbInstitutosActionPerformed

    private void cmbEdicionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbEdicionesActionPerformed
        alSeleccionarEdicion();
    }//GEN-LAST:event_cmbEdicionesActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrar;
    private javax.swing.JComboBox<String> cmbCursos;
    private javax.swing.JComboBox<String> cmbEdiciones;
    private javax.swing.JComboBox<String> cmbInstitutos;
    private javax.swing.JPanel jPanel1;
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
    private javax.swing.JPanel panelSeleccion;
    private javax.swing.JTextField txtCupo;
    private javax.swing.JTextField txtFechaFin;
    private javax.swing.JTextField txtFechaInicio;
    private javax.swing.JTextField txtFechaPub;
    private javax.swing.JTextField txtNombreEdicion;
    // End of variables declaration//GEN-END:variables
}
