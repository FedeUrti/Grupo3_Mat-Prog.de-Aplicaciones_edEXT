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
            DtUsuario dt = icu.obtenerInfoUsuario(nombreUsuario);

            if (dt == null) {
                return;
            }

            // 1. Cargar Datos Básicos de Texto
            lblValorNickname.setText(dt.getNickname());
            lblValorNombre.setText(dt.getNombre());
            lblValorApellido.setText(dt.getApellido());
            lblValorCorreo.setText(dt.getCorreo());
            lblValorFechaNac.setText(dt.getFechaNacimiento() != null ? dt.getFechaNacimiento().toString() : "");

            // 2. Determinar tipo de usuario
            boolean esDocente = (dt instanceof DtDocente);

            // 3. Cargar la imagen utilizando getImagePath()
            com.grupo3_mat.edEXT.Presentacion.Utils.GestorImagenes.cargarImagenEnLabel(
                    dt.getImagenPath(),
                    lblImg,
                    esDocente
            );

            DefaultListModel<String> modCursos = new DefaultListModel<>();
            DefaultListModel<String> modEdiciones = new DefaultListModel<>();
            DefaultListModel<String> modProgramas = new DefaultListModel<>();

            if (esDocente) {
                DtDocente docente = (DtDocente) dt;
                lblValorTipoUsuario.setText("Docente (" + docente.getInstituto() + ")");
                jPanel4.setVisible(true);

                // Activar el tab de Cursos (asumiendo que es el índice 0)
                jTabbedPane1.setEnabledAt(0, true);

                IControladorEdicion ice = Fabrica.getInstance().getIControladorEdicion();
                IControladorCurso icc = Fabrica.getInstance().getIControladorCurso();

                List<String> edicionesDocente = ice.listarEdicionesDeDocente(docente.getNickname());

                if (edicionesDocente != null) {
                    for (String edicion : edicionesDocente) {
                        modEdiciones.addElement(edicion);

                        String nombreCurso = ice.obtenerCursoDeEdicion(edicion);
                        if (nombreCurso != null && !modCursos.contains(nombreCurso)) {
                            modCursos.addElement(nombreCurso);

                            // CARGAR PROGRAMAS DIRECTAMENTE SIN HACER CLIC
                            DtCurso dtCurso = icc.consultarCurso(nombreCurso);
                            if (dtCurso != null && dtCurso.getProgramas() != null) {
                                for (String prog : dtCurso.getProgramas()) {
                                    if (!modProgramas.contains(prog)) {
                                        modProgramas.addElement(prog);
                                    }
                                }
                            }
                        }
                    }
                }

                lstCursos.setModel(modCursos);
                lstEdiciones1.setModel(modEdiciones);
                lstEdiciones.setModel(modProgramas);

            } else if (dt instanceof DtEstudiante) {
                DtEstudiante estudiante = (DtEstudiante) dt;
                lblValorTipoUsuario.setText("Estudiante");
                jPanel4.setVisible(false);

                // Desactivar el tab de Cursos y cambiar la vista si estaba allí
                jTabbedPane1.setEnabledAt(0, false);
                if (jTabbedPane1.getSelectedIndex() == 0) {
                    jTabbedPane1.setSelectedIndex(1); // Mover a la pestaña Ediciones
                }

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
        lblImg = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        lblValorApellido = new javax.swing.JLabel();
        lblValorNickname = new javax.swing.JLabel();
        lblValorNombre = new javax.swing.JLabel();
        lblValorFechaNac = new javax.swing.JLabel();
        lblValorTipoUsuario = new javax.swing.JLabel();
        lblValorCorreo = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        lblNickname = new javax.swing.JLabel();
        lblApellido = new javax.swing.JLabel();
        lblCorreo = new javax.swing.JLabel();
        lblFechaNac = new javax.swing.JLabel();
        lblNombre = new javax.swing.JLabel();
        lblTipoUsuario = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
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
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 504, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Informacion sobre el usuario", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 18))); // NOI18N

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos Basicos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 18))); // NOI18N

        lblImg.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        lblImg.setBorder(javax.swing.BorderFactory.createTitledBorder("Imagen de Perfil"));

        jPanel10.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblValorApellido.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        lblValorApellido.setText(".");
        lblValorApellido.setPreferredSize(new java.awt.Dimension(128, 20));

        lblValorNickname.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        lblValorNickname.setText(".");
        lblValorNickname.setPreferredSize(new java.awt.Dimension(128, 20));

        lblValorNombre.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        lblValorNombre.setText(".");
        lblValorNombre.setPreferredSize(new java.awt.Dimension(128, 20));

        lblValorFechaNac.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        lblValorFechaNac.setText(".");
        lblValorFechaNac.setPreferredSize(new java.awt.Dimension(128, 20));

        lblValorTipoUsuario.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        lblValorTipoUsuario.setText(".");
        lblValorTipoUsuario.setMaximumSize(new java.awt.Dimension(128, 20));
        lblValorTipoUsuario.setMinimumSize(new java.awt.Dimension(128, 20));
        lblValorTipoUsuario.setPreferredSize(new java.awt.Dimension(128, 20));

        lblValorCorreo.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        lblValorCorreo.setText(".");
        lblValorCorreo.setPreferredSize(new java.awt.Dimension(128, 20));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblValorNickname, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblValorFechaNac, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblValorTipoUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblValorCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblValorApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblValorNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(45, 45, 45))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblValorNickname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblValorNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(lblValorApellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblValorCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblValorFechaNac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(lblValorTipoUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblNickname.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        lblNickname.setText("Nickname:");

        lblApellido.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        lblApellido.setText("Apellido:");

        lblCorreo.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        lblCorreo.setText("Correo:");

        lblFechaNac.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        lblFechaNac.setText("Fecha Nac:");

        lblNombre.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        lblNombre.setText("Nombre:");

        lblTipoUsuario.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        lblTipoUsuario.setText("Tipo Usuario:");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCorreo)
                    .addComponent(lblApellido)
                    .addComponent(lblNickname)
                    .addComponent(lblNombre)
                    .addComponent(lblFechaNac)
                    .addComponent(lblTipoUsuario))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblNickname)
                .addGap(18, 18, 18)
                .addComponent(lblNombre)
                .addGap(18, 18, 18)
                .addComponent(lblApellido)
                .addGap(18, 18, 18)
                .addComponent(lblCorreo)
                .addGap(18, 18, 18)
                .addComponent(lblFechaNac)
                .addGap(18, 18, 18)
                .addComponent(lblTipoUsuario)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblImg, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblImg, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
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

        jTabbedPane1.addTab("Cursos", jPanel4);

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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnVerEdiciones))
        );

        jTabbedPane1.addTab("Ediciones", jPanel5);

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnVerProgramas)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnVerProgramas)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Programas¨", jPanel6);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 566, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1)
                .addGap(41, 41, 41))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnVerCursosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerCursosActionPerformed
        String cursoSeleccionado = lstCursos.getSelectedValue();
        if (cursoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un curso de la lista.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ConsultaCursoFrame frameCurso = new ConsultaCursoFrame();
        this.getDesktopPane().add(frameCurso);
        frameCurso.setVisible(true);
        frameCurso.toFront();
        // Pasa el dato a la ventana
        frameCurso.cargarDatosCurso(cursoSeleccionado);
    }//GEN-LAST:event_btnVerCursosActionPerformed

    private void btnVerProgramasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerProgramasActionPerformed
        String programaSeleccionado = lstEdiciones.getSelectedValue(); // lstEdiciones almacena programas en este frame
        if (programaSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un programa de formación de la lista.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Llamar a ConsultaDeProgramaFrame
        ConsultaDeProgramaFrame framePrograma = new ConsultaDeProgramaFrame();
        this.getDesktopPane().add(framePrograma);
        framePrograma.setVisible(true);
        framePrograma.toFront();

        // Intentar pasarle el dato, usando tu método actual (ignorar la posible Excepción de 'Not supported yet')
        try {
            framePrograma.cargarDatosPrograma(programaSeleccionado);
        } catch (UnsupportedOperationException e) {
            // Silenciado temporalmente hasta que implementes la lógica en el destino
        }
    }//GEN-LAST:event_btnVerProgramasActionPerformed

    private void btnVerEdicionesActionPerformed(java.awt.event.ActionEvent evt) {                                                
        String edicionSeleccionada = lstEdiciones1.getSelectedValue(); // lstEdiciones1 almacena ediciones en este frame
        if (edicionSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una edición de la lista.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Llamar a ConsultaEdicionCursoFrame
        ConsultaEdicionCursoFrame frameEdicion = new ConsultaEdicionCursoFrame();
        this.getDesktopPane().add(frameEdicion);
        frameEdicion.setVisible(true);
        frameEdicion.toFront();
//GEN-FIRST:event_btnVerEdicionesActionPerformed
    }//GEN-LAST:event_btnVerEdicionesActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnVerCursos;
    private javax.swing.JButton btnVerEdiciones;
    private javax.swing.JButton btnVerProgramas;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblApellido;
    private javax.swing.JLabel lblCorreo;
    private javax.swing.JLabel lblFechaNac;
    private javax.swing.JLabel lblImg;
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
