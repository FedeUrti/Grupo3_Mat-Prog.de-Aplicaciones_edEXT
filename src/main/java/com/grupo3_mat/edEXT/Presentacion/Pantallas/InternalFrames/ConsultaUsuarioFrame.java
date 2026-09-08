/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package com.grupo3_mat.edEXT.Presentacion.Pantallas.InternalFrames;

import com.grupo3_mat.edEXT.Logica.DataTypes.DtCurso;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtDocente;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtEstudiante;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtUsuario;
import com.grupo3_mat.edEXT.Logica.Fabrica;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorCurso;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorEdicion;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorUsuario;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;

/**
 *
 * @author fede1
 */
public class ConsultaUsuarioFrame extends javax.swing.JInternalFrame {

    /**
     * Creates new form ConsultaUsuarioFrame
     */
    public ConsultaUsuarioFrame() {
        initComponents();
        cargarUsuarios();
        lstSeleccioneUsuario.addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                String usuarioSeleccionado = lstSeleccioneUsuario.getSelectedValue();
                if (usuarioSeleccionado != null) {
                    mostrarDatosUsuario(usuarioSeleccionado);
                }
            }
        });
        lstCursos.addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                String cursoSeleccionado = lstCursos.getSelectedValue();
                if (cursoSeleccionado != null) {
                    cargarDetallesCursoDocente(cursoSeleccionado);
                }
            }
        });
    }

    /**
     * Carga la lista inicial de todos los nicknames de usuarios registrados.
     */
    private void cargarUsuarios() {
        DefaultListModel<String> model = new DefaultListModel<>();
        try {
            IControladorUsuario icu = Fabrica.getInstance().getIControladorUsuario();
            List<String> usuarios = icu.listarNicknamesUsuarios();
            if (usuarios != null) {
                for (String nick : usuarios) {
                    model.addElement(nick);
                }
            }
            lstSeleccioneUsuario.setModel(model);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar la lista de usuarios: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Obtiene los datos del usuario seleccionado y actualiza los componentes de
     * la interfaz.
     */
    private void mostrarDatosUsuario(String nombreUsuario) {
        try {
            IControladorUsuario icu = Fabrica.getInstance().getIControladorUsuario();
            IControladorCurso icc = Fabrica.getInstance().getIControladorCurso();
            DtUsuario dt = icu.obtenerInfoUsuario(nombreUsuario);

            if (dt == null) {
                return;
            }

            // Cargar Datos Básicos
            lblValorNickname.setText(dt.getNickname());
            lblValorNombre.setText(dt.getNombre());
            lblValorApellido.setText(dt.getApellido());
            lblValorCorreo.setText(dt.getCorreo());
            lblValorFechaNac.setText(dt.getFechaNacimiento() != null ? dt.getFechaNacimiento().toString() : "");

            DefaultListModel<String> modCursos = new DefaultListModel<>();
            DefaultListModel<String> modEdiciones = new DefaultListModel<>();
            DefaultListModel<String> modProgramas = new DefaultListModel<>();

            if (dt instanceof DtDocente) {
                DtDocente docente = (DtDocente) dt;
                lblValorTipoUsuario.setText("Docente (" + docente.getInstituto() + ")");
                jPanel4.setVisible(true);

                // 1. Obtener de la BD las ediciones en las que participa el docente
                IControladorEdicion ice = Fabrica.getInstance().getIControladorEdicion();
                List<String> edicionesDocente = ice.listarEdicionesDeDocente(docente.getNickname());

                if (edicionesDocente != null) {
                    for (String edicion : edicionesDocente) {
                        modEdiciones.addElement(edicion);

                        // 2. Obtener el nombre del Curso al que pertenece la edición (evitando duplicados)
                        String nombreCurso = ice.obtenerCursoDeEdicion(edicion);
                        if (nombreCurso != null && !modCursos.contains(nombreCurso)) {
                            modCursos.addElement(nombreCurso);
                        }
                    }
                }

                lstCursos.setModel(modCursos);
                lstEdiciones1.setModel(modEdiciones);
                lstEdiciones.setModel(modProgramas);
            } else if (dt instanceof DtEstudiante) {
                DtEstudiante estudiante = (DtEstudiante) dt;
                lblValorTipoUsuario.setText("Estudiante");

                jPanel4.setVisible(false); // Ocultar panel de Cursos para Estudiante

                if (estudiante.getEdicionesInscripto() != null) {
                    for (String ed : estudiante.getEdicionesInscripto()) {
                        modEdiciones.addElement(ed);
                    }
                }

                if (estudiante.getProgramasInscripto() != null) {
                    for (String prog : estudiante.getProgramasInscripto()) {
                        modProgramas.addElement(prog);
                    }
                }

                lstCursos.setModel(modCursos);
                lstEdiciones1.setModel(modEdiciones);
                lstEdiciones.setModel(modProgramas);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al consultar usuario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    /* Consulta el curso seleccionado por el Docente y carga sus ediciones y programas asociado
     */
    private void cargarDetallesCursoDocente(String nombreCurso) {
        try {
            IControladorCurso icc = Fabrica.getInstance().getIControladorCurso();
            DtCurso dtCurso = icc.consultarCurso(nombreCurso);

            if (dtCurso != null) {
                DefaultListModel<String> modEdiciones = new DefaultListModel<>();
                if (dtCurso.getEdiciones() != null) {
                    for (String ed : dtCurso.getEdiciones()) {
                        modEdiciones.addElement(ed);
                    }
                }
                lstEdiciones1.setModel(modEdiciones);

                DefaultListModel<String> modProgramas = new DefaultListModel<>();
                if (dtCurso.getProgramas() != null) {
                    for (String prog : dtCurso.getProgramas()) {
                        modProgramas.addElement(prog);
                    }
                }
                lstEdiciones.setModel(modProgramas);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al consultar detalles del curso: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        lstSeleccioneUsuario = new javax.swing.JList<>();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        lblNickname = new javax.swing.JLabel();
        lblNombre = new javax.swing.JLabel();
        lblApellido = new javax.swing.JLabel();
        lblCorreo = new javax.swing.JLabel();
        lblFechaNac = new javax.swing.JLabel();
        lblTipoUsuario = new javax.swing.JLabel();
        lblValorNickname = new javax.swing.JLabel();
        lblValorApellido = new javax.swing.JLabel();
        lblValorNombre = new javax.swing.JLabel();
        lblValorCorreo = new javax.swing.JLabel();
        lblValorFechaNac = new javax.swing.JLabel();
        lblValorTipoUsuario = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstCursos = new javax.swing.JList<>();
        btnVerCursos = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        lstEdiciones1 = new javax.swing.JList<>();
        btnVerEdiciones = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        lstEdiciones = new javax.swing.JList<>();
        btnVerProgramas = new javax.swing.JButton();

        setTitle("Consultar Usuario");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Seleccione Usuario", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 18))); // NOI18N

        lstSeleccioneUsuario.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lstSeleccioneUsuario.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(lstSeleccioneUsuario);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 819, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Informacion sobre el usuario", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 18))); // NOI18N

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos Basicos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 18))); // NOI18N

        lblNickname.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblNickname.setText("Nickname:");

        lblNombre.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblNombre.setText("Nombre:");

        lblApellido.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblApellido.setText("Apellido:");

        lblCorreo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblCorreo.setText("Correo:");

        lblFechaNac.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblFechaNac.setText("Fecha Nac:");

        lblTipoUsuario.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblTipoUsuario.setText("Tipo Usuario:");

        lblValorNickname.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblValorNickname.setText("\"\"");

        lblValorApellido.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblValorApellido.setText("\"\"");

        lblValorNombre.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblValorNombre.setText("\"\"");

        lblValorCorreo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblValorCorreo.setText("\"\"");

        lblValorFechaNac.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblValorFechaNac.setText("\"\"");

        lblValorTipoUsuario.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblValorTipoUsuario.setText("\"\"");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lblTipoUsuario)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblValorTipoUsuario))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(lblFechaNac)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblValorFechaNac))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(lblCorreo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblValorCorreo))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(lblApellido)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblValorApellido))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(lblNombre)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblValorNombre))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(lblNickname)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblValorNickname)))
                .addGap(128, 128, 128))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNickname)
                    .addComponent(lblValorNickname))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNombre)
                    .addComponent(lblValorNombre))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblApellido)
                    .addComponent(lblValorApellido))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCorreo)
                    .addComponent(lblValorCorreo))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFechaNac)
                    .addComponent(lblValorFechaNac))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTipoUsuario)
                    .addComponent(lblValorTipoUsuario))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cursos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 18))); // NOI18N

        lstCursos.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(lstCursos);

        btnVerCursos.setText("Ver Cursos");
        btnVerCursos.addActionListener(this::btnVerCursosActionPerformed);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnVerCursos)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnVerCursos))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ediciones", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 18))); // NOI18N

        lstEdiciones1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane4.setViewportView(lstEdiciones1);

        btnVerEdiciones.setText("Ver Ediciones");
        btnVerEdiciones.addActionListener(this::btnVerEdicionesActionPerformed);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnVerEdiciones)
                .addContainerGap())
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(52, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addComponent(btnVerEdiciones))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Programas de Formacion", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 18))); // NOI18N

        lstEdiciones.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(lstEdiciones);

        btnVerProgramas.setText("Ver Programas");
        btnVerProgramas.addActionListener(this::btnVerProgramasActionPerformed);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnVerProgramas)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnVerProgramas))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnVerCursosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerCursosActionPerformed
        // TODO add your handling code here:
        String cursoSeleccionado = lstCursos.getSelectedValue();
        if (cursoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un curso de la lista.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Consultando detalles del curso: " + cursoSeleccionado, "Consulta de Curso", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnVerCursosActionPerformed

    private void btnVerProgramasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerProgramasActionPerformed
        // TODO add your handling code here:
        String programaSeleccionado = lstEdiciones.getSelectedValue();
        if (programaSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un programa de formación de la lista.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Consultando detalles del programa: " + programaSeleccionado, "Consulta de Programa de Formación", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnVerProgramasActionPerformed

    private void btnVerEdicionesActionPerformed(java.awt.event.ActionEvent evt) {                                                
        // TODO add your handling code here:
        String edicionSeleccionada = lstEdiciones1.getSelectedValue();
        if (edicionSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una edición de la lista.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Consultando detalles de la edición: " + edicionSeleccionada, "Consulta de Edición", JOptionPane.INFORMATION_MESSAGE);
//GEN-FIRST:event_btnVerEdicionesActionPerformed
    }//GEN-LAST:event_btnVerEdicionesActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnVerCursos;
    private javax.swing.JButton btnVerEdiciones;
    private javax.swing.JButton btnVerProgramas;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblApellido;
    private javax.swing.JLabel lblCorreo;
    private javax.swing.JLabel lblFechaNac;
    private javax.swing.JLabel lblNickname;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblTipoUsuario;
    private javax.swing.JLabel lblValorApellido;
    private javax.swing.JLabel lblValorCorreo;
    private javax.swing.JLabel lblValorFechaNac;
    private javax.swing.JLabel lblValorNickname;
    private javax.swing.JLabel lblValorNombre;
    private javax.swing.JLabel lblValorTipoUsuario;
    private javax.swing.JList<String> lstCursos;
    private javax.swing.JList<String> lstEdiciones;
    private javax.swing.JList<String> lstEdiciones1;
    private javax.swing.JList<String> lstSeleccioneUsuario;
    // End of variables declaration//GEN-END:variables
}
