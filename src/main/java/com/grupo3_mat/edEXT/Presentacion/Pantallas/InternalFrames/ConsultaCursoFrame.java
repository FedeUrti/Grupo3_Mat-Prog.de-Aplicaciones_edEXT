/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package com.grupo3_mat.edEXT.Presentacion.Pantallas.InternalFrames;

import com.grupo3_mat.edEXT.Logica.DataTypes.DtCurso;
import com.grupo3_mat.edEXT.Logica.Fabrica;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorInstituto;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 *
 * @author fede1
 */
public class ConsultaCursoFrame extends javax.swing.JInternalFrame {

    /**
     * Creates new form ConsultaCursoFrame
     */
    public ConsultaCursoFrame() {
        initComponents();
        limpiarTodo();
        cargarInstitutos();
    }

    private void limpiarTodo() {
        lstCursos.setModel(new DefaultListModel<>());
        limpiarDatosDerecha();
    }

    private void cargarInstitutos() {
        cbInstitutos.removeAllItems();
        cbInstitutos.addItem("Seleccionar Instituto");

        try {
            IControladorInstituto ici = Fabrica.getInstance().getIControladorInstituto();
            List<String> institutos = ici.listarInstitutos();
            for (String inst : institutos) {
                cbInstitutos.addItem(inst);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar institutos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarDatosDerecha() {
        valorNombre.setText("");
        txtDescripcion.setText("");
        valorDuracion.setText("");
        if (valorHoras != null) {
            valorHoras.setText("");
        }
        if (valorCreditos != null) {
            valorCreditos.setText("");
        }
        if (valorURL != null) {
            valorURL.setText("");
        }
        lstEdiciones.setModel(new DefaultListModel<>());
        lstProgramas.setModel(new DefaultListModel<>());
    }

    private void mostrarDatosCurso(String nombreCurso) {
        try {
            IControladorCurso icc = Fabrica.getInstance().getIControladorCurso();
            DtCurso dt = icc.consultarCurso(nombreCurso);

            if (dt == null) {
                return;
            }

            // 1. Cargar Datos Básicos en las etiquetas correspondientes
            valorNombre.setText(dt.getNombre());
            txtDescripcion.setText(dt.getDescripcion());
            valorDuracion.setText(String.valueOf(dt.getDuracion()));
            valorHoras.setText(String.valueOf(dt.getCantHoras()) + " hs");
            valorCreditos.setText(String.valueOf(dt.getCreditos()));
            valorURL.setText(dt.getUrl());

            // 2. Cargar Lista de Ediciones
            DefaultListModel<String> modEdiciones = new DefaultListModel<>();
            if (dt.getEdiciones() != null) {
                for (String ed : dt.getEdiciones()) {
                    modEdiciones.addElement(ed);
                }
            }
            lstEdiciones.setModel(modEdiciones);

            // 3. Cargar Lista de Programas de Formación
            DefaultListModel<String> modProgramas = new DefaultListModel<>();
            if (dt.getProgramas() != null) {
                for (String prog : dt.getProgramas()) {
                    modProgramas.addElement(prog);
                }
            }
            lstProgramas.setModel(modProgramas);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al consultar curso: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cargarDatosCurso(String nombreCurso) {
        if (nombreCurso == null || nombreCurso.isEmpty()) {
            return;
        }

        try {
            IControladorCurso icc = Fabrica.getInstance().getIControladorCurso();
            DtCurso dt = icc.consultarCurso(nombreCurso);

            if (dt != null) {
                // 1. Obtener el instituto al que pertenece el curso
                String nomInstituto = dt.getNomInstituto();

                if (nomInstituto != null && !nomInstituto.isEmpty()) {
                    // 2. Seleccionar el instituto en el ComboBox (esto dispara cbInstitutosActionPerformed y llena lstCursos)
                    cbInstitutos.setSelectedItem(nomInstituto);

                    // 3. Seleccionar el curso en el JList (esto dispara lstCursosValueChanged y ejecuta mostrarDatosCurso)
                    lstCursos.setSelectedValue(nombreCurso, true);
                } else {
                    // Si no tiene instituto, cargar los datos manualmente a la derecha
                    mostrarDatosCurso(nombreCurso);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos del curso: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstCursos = new javax.swing.JList<>();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        lblNombre = new javax.swing.JLabel();
        lblDescripcion = new javax.swing.JLabel();
        lblDuracion = new javax.swing.JLabel();
        valorDuracion = new javax.swing.JLabel();
        valorNombre = new javax.swing.JLabel();
        lblNombre1 = new javax.swing.JLabel();
        lblInstituto1 = new javax.swing.JLabel();
        valorCreditos = new javax.swing.JLabel();
        valorHoras = new javax.swing.JLabel();
        lblInstituto2 = new javax.swing.JLabel();
        valorURL = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDescripcion = new javax.swing.JTextArea();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        lstProgramas = new javax.swing.JList<>();
        btnVerPF = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        lstEdiciones = new javax.swing.JList<>();
        btnVerEC = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        cbInstitutos = new javax.swing.JComboBox<>();

        setMaximizable(true);
        setResizable(true);
        setTitle("Consultar Curso");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Seleccione Curso", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Inter", 0, 18))); // NOI18N

        lstCursos.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        lstCursos.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        lstCursos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstCursos.addListSelectionListener(this::lstCursosValueChanged);
        jScrollPane1.setViewportView(lstCursos);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 651, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(108, 108, 108))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Informacion sobre el curso", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Inter", 0, 18))); // NOI18N

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos Basicos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Inter", 0, 18))); // NOI18N

        lblNombre.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        lblNombre.setText("Nombre");

        lblDescripcion.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        lblDescripcion.setText("Descripcion");

        lblDuracion.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        lblDuracion.setText("Duracion");

        valorDuracion.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        valorDuracion.setText("\"\"");

        valorNombre.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        valorNombre.setText("\"\"");

        lblNombre1.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        lblNombre1.setText("Horas");

        lblInstituto1.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        lblInstituto1.setText("Creditos");

        valorCreditos.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        valorCreditos.setText("\"\"");

        valorHoras.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        valorHoras.setText("\"\"");

        lblInstituto2.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        lblInstituto2.setText("URL");

        valorURL.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        valorURL.setText("\"\"");

        txtDescripcion.setEditable(false);
        txtDescripcion.setColumns(20);
        txtDescripcion.setRows(5);
        jScrollPane2.setViewportView(txtDescripcion);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblDuracion)
                            .addComponent(lblDescripcion)
                            .addComponent(lblNombre1)
                            .addComponent(lblInstituto1)
                            .addComponent(lblInstituto2))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(valorCreditos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addComponent(jScrollPane2)
                                .addGap(18, 18, 18))
                            .addComponent(valorHoras, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(valorURL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(valorDuracion))
                        .addContainerGap())
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(lblNombre)
                        .addGap(18, 18, 18)
                        .addComponent(valorNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNombre)
                    .addComponent(valorNombre))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(lblDescripcion)
                        .addGap(0, 64, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(lblDuracion))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(valorDuracion)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNombre1)
                    .addComponent(valorHoras))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblInstituto1)
                    .addComponent(valorCreditos))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblInstituto2)
                    .addComponent(valorURL))
                .addGap(15, 15, 15))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Programas de Formacion", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Inter", 0, 18))); // NOI18N

        lstProgramas.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        lstProgramas.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane5.setViewportView(lstProgramas);

        btnVerPF.setText("Ver Programa");
        btnVerPF.addActionListener(this::btnVerPFActionPerformed);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnVerPF)))
                .addGap(15, 15, 15))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnVerPF)
                .addContainerGap(158, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab2", jPanel6);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ediciones", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Inter", 0, 18))); // NOI18N

        lstEdiciones.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        lstEdiciones.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane3.setViewportView(lstEdiciones);

        btnVerEC.setText("Ver Edicion");
        btnVerEC.addActionListener(this::btnVerECActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnVerEC, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 408, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnVerEC)
                .addContainerGap(166, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab1", jPanel2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 32, Short.MAX_VALUE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jLabel2.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        jLabel2.setText("Insitituto");

        cbInstitutos.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        cbInstitutos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbInstitutos.addActionListener(this::cbInstitutosActionPerformed);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbInstitutos, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbInstitutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 695, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cbInstitutosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbInstitutosActionPerformed
        // TODO add your handling code here:
        String institutoSeleccionado = (String) cbInstitutos.getSelectedItem();
        if (institutoSeleccionado == null || institutoSeleccionado.equals("Seleccionar Instituto")) {
            lstCursos.setModel(new DefaultListModel<>());
            limpiarDatosDerecha();
            return;
        }

        try {
            IControladorCurso icc = Fabrica.getInstance().getIControladorCurso();
            List<String> cursos = icc.listarCursosPorInstituto(institutoSeleccionado);

            DefaultListModel<String> modeloCursos = new DefaultListModel<>();
            for (String curso : cursos) {
                modeloCursos.addElement(curso);
            }
            lstCursos.setModel(modeloCursos);

            limpiarDatosDerecha();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al obtener cursos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_cbInstitutosActionPerformed

    private void lstCursosValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstCursosValueChanged
        if (!evt.getValueIsAdjusting()) {
            String cursoSeleccionado = lstCursos.getSelectedValue();
            if (cursoSeleccionado != null) {
                mostrarDatosCurso(cursoSeleccionado);
            } else {
                limpiarDatosDerecha();
            }
        }
    }//GEN-LAST:event_lstCursosValueChanged

    private void btnVerECActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerECActionPerformed
        String edicionSeleccionada = lstEdiciones.getSelectedValue();

        if (edicionSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccioná una edición de la lista.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String instituto = (String) cbInstitutos.getSelectedItem();
        String curso = lstCursos.getSelectedValue();

        ConsultaEdicionCursoFrame frameEdicion = new ConsultaEdicionCursoFrame(instituto, curso, edicionSeleccionada);

        this.getDesktopPane().add(frameEdicion);
        frameEdicion.setVisible(true);
        frameEdicion.toFront();
    }//GEN-LAST:event_btnVerECActionPerformed

    private void btnVerPFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerPFActionPerformed
        String programaSeleccionado = lstProgramas.getSelectedValue();

        if (programaSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccioná un programa de la lista.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ConsultaDeProgramaFrame framePrograma = new ConsultaDeProgramaFrame();

        this.getDesktopPane().add(framePrograma);
        framePrograma.setVisible(true);
        framePrograma.toFront();

        framePrograma.cargarDatosPrograma(programaSeleccionado);
    }//GEN-LAST:event_btnVerPFActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnVerEC;
    private javax.swing.JButton btnVerPF;
    private javax.swing.JComboBox<String> cbInstitutos;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblDescripcion;
    private javax.swing.JLabel lblDuracion;
    private javax.swing.JLabel lblInstituto1;
    private javax.swing.JLabel lblInstituto2;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblNombre1;
    private javax.swing.JList<String> lstCursos;
    private javax.swing.JList<String> lstEdiciones;
    private javax.swing.JList<String> lstProgramas;
    private javax.swing.JTextArea txtDescripcion;
    private javax.swing.JLabel valorCreditos;
    private javax.swing.JLabel valorDuracion;
    private javax.swing.JLabel valorHoras;
    private javax.swing.JLabel valorNombre;
    private javax.swing.JLabel valorURL;
    // End of variables declaration//GEN-END:variables
}
