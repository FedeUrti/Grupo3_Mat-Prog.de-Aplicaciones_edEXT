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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;

/**
 *
 * @author fede1
 */
public class ConsultaUsuarioFrame extends javax.swing.JInternalFrame {
    private boolean filtrosVisibles = false;
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
        
        filtroBtn.setText(filtrosVisibles ? "▲ Ocultar filtros" : "▼ Filtros avanzados");
        if (panelFiltros != null) {
            panelFiltros.setVisible(false);
        }

        configurarListeners();
    }

    /**
     * Carga la lista inicial de todos los nicknames de usuarios registrados.
     */
    private void cargarUsuarios() {
        aplicarFiltros();
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

            // 3. Cargar la imagen utilizando GestorImagenes
            com.grupo3_mat.edEXT.Presentacion.Utils.GestorImagenes.cargarImagenEnLabel(
                    dt.getImagenPath(),
                    lblImg,
                    esDocente
            );

            DefaultListModel<String> modCursos = new DefaultListModel<>();
            DefaultListModel<String> modEdiciones = new DefaultListModel<>();
            DefaultListModel<String> modProgramas = new DefaultListModel<>();

            IControladorEdicion ice = Fabrica.getInstance().getIControladorEdicion();
            IControladorCurso icc = Fabrica.getInstance().getIControladorCurso();

            if (esDocente) {
                DtDocente docente = (DtDocente) dt;
                lblValorTipoUsuario.setText("Docente (" + docente.getInstituto() + ")");

                // Habilitar la pestaña de Cursos y seleccionarla por defecto
                jTabbedPane1.setEnabledAt(0, true);
                jTabbedPane1.setSelectedIndex(0);

                List<String> edicionesDocente = ice.listarEdicionesDeDocente(docente.getNickname());

                if (edicionesDocente != null) {
                    for (String edicion : edicionesDocente) {
                        modEdiciones.addElement(edicion);

                        String nombreCurso = ice.obtenerCursoDeEdicion(edicion);
                        if (nombreCurso != null && !modCursos.contains(nombreCurso)) {
                            modCursos.addElement(nombreCurso);

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

                // Desactivar pestaña Cursos y forzar ir a la pestaña Ediciones (índice 1)
                jTabbedPane1.setEnabledAt(0, false);
                jTabbedPane1.setSelectedIndex(1);

                // 1. Obtener las ediciones del estudiante desde el DTO
                List<String> edicionesEstudiante = estudiante.getEdicionesInscripto();

                // Si el DTO viene vacío/null, intentar consultar al controlador directamente
                if (edicionesEstudiante == null || edicionesEstudiante.isEmpty()) {
                    try {
                        edicionesEstudiante = ice.listarEdicionesDeEstudiante(estudiante.getNickname());
                    } catch (Exception ignored) {
                        // Si el método no existe en la interfaz IControladorEdicion, ignora el error
                    }
                }

                // 2. Cargar ediciones y obtener sus programas asociados a través del curso
                if (edicionesEstudiante != null) {
                    for (String edicion : edicionesEstudiante) {
                        modEdiciones.addElement(edicion);

                        String nombreCurso = ice.obtenerCursoDeEdicion(edicion);
                        if (nombreCurso != null) {
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

                // 3. Cargar programas en los que el estudiante esté inscripto directamente
                if (estudiante.getProgramasInscripto() != null) {
                    for (String prog : estudiante.getProgramasInscripto()) {
                        if (!modProgramas.contains(prog)) {
                            modProgramas.addElement(prog);
                        }
                    }
                }

                // Asignación a los componentes Swing
                lstCursos.setModel(modCursos);
                lstEdiciones1.setModel(modEdiciones); // lstEdiciones1 es la lista de la pestaña Ediciones
                lstEdiciones.setModel(modProgramas);  // lstEdiciones es la lista de la pestaña Programas
            }

            // Refrescar el componente para evitar artefactos visuales
            jTabbedPane1.revalidate();
            jTabbedPane1.repaint();

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
    private void configurarListeners() {
        // Listener de texto para filtrado en tiempo real
        if (buscarTxt != null) {
            buscarTxt.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    aplicarFiltros();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    aplicarFiltros();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    aplicarFiltros();
                }
            });
        }

        // Listener de la lista de usuarios
        lstSeleccioneUsuario.addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                String usuarioSeleccionado = lstSeleccioneUsuario.getSelectedValue();
                if (usuarioSeleccionado != null) {
                    mostrarDatosUsuario(usuarioSeleccionado);
                }
            }
        });

        // Listener de la lista de cursos
        lstCursos.addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                String cursoSeleccionado = lstCursos.getSelectedValue();
                if (cursoSeleccionado != null) {
                    cargarDetallesCursoDocente(cursoSeleccionado);
                }
            }
        });
    }

    private void aplicarFiltros() {
        String texto = (buscarTxt != null) ? buscarTxt.getText().trim().toLowerCase() : "";

        // Verificación de estado de Radio Buttons
        boolean soloEstudiantes = (rbEstudiantes != null) && rbEstudiantes.isSelected();
        boolean soloDocentes = (rbDocentes != null) && rbDocentes.isSelected();

        DefaultListModel<String> model = new DefaultListModel<>();

        try {
            IControladorUsuario icu = Fabrica.getInstance().getIControladorUsuario();
            List<String> nicknames = icu.listarNicknamesUsuarios();

            if (nicknames != null) {
                for (String nick : nicknames) {
                    DtUsuario dt = icu.obtenerInfoUsuario(nick);
                    if (dt == null) {
                        continue;
                    }

                    boolean esDocente = (dt instanceof DtDocente);
                    boolean esEstudiante = (dt instanceof DtEstudiante);

                    // 1. Filtrado por Tipo (RadioButtons)
                    boolean coincideTipo = true;
                    if (soloEstudiantes) {
                        coincideTipo = esEstudiante;
                    } else if (soloDocentes) {
                        coincideTipo = esDocente;
                    }

                    // 2. Filtrado por Texto (Nickname, Nombre o Apellido)
                    String nickStr = (dt.getNickname() != null) ? dt.getNickname().toLowerCase() : "";
                    
                    boolean coincideTexto = texto.isEmpty()
                            || nickStr.contains(texto);
                        

                    if (coincideTipo && coincideTexto) {
                        model.addElement(dt.getNickname());
                    }
                }
            }

            lstSeleccioneUsuario.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
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

        filtrosBtnGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstSeleccioneUsuario = new javax.swing.JList<>();
        jPanel11 = new javax.swing.JPanel();
        buscarTxt = new javax.swing.JTextField();
        filtroBtn = new javax.swing.JToggleButton();
        panelFiltros = new javax.swing.JPanel();
        rbTodos = new javax.swing.JRadioButton();
        rbDocentes = new javax.swing.JRadioButton();
        rbEstudiantes = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
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
        jPanel7 = new javax.swing.JPanel();
        lblImg = new javax.swing.JLabel();
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

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Buscador"));

        filtroBtn.setText("Filtros");
        filtroBtn.addActionListener(this::filtroBtnActionPerformed);

        panelFiltros.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtros"));

        filtrosBtnGroup.add(rbTodos);
        rbTodos.setSelected(true);
        rbTodos.setText("Todos");
        rbTodos.addActionListener(this::rbTodosActionPerformed);

        filtrosBtnGroup.add(rbDocentes);
        rbDocentes.setText("Docentes");
        rbDocentes.addActionListener(this::rbDocentesActionPerformed);

        filtrosBtnGroup.add(rbEstudiantes);
        rbEstudiantes.setText("Estudiantes");
        rbEstudiantes.addActionListener(this::rbEstudiantesActionPerformed);

        javax.swing.GroupLayout panelFiltrosLayout = new javax.swing.GroupLayout(panelFiltros);
        panelFiltros.setLayout(panelFiltrosLayout);
        panelFiltrosLayout.setHorizontalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbTodos)
                    .addComponent(rbDocentes)
                    .addComponent(rbEstudiantes))
                .addContainerGap(149, Short.MAX_VALUE))
        );
        panelFiltrosLayout.setVerticalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbTodos)
                .addGap(18, 18, 18)
                .addComponent(rbDocentes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rbEstudiantes)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(buscarTxt)
                        .addContainerGap())
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(filtroBtn)
                        .addGap(18, 18, 18)
                        .addComponent(panelFiltros, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(14, 14, 14))))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(buscarTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filtroBtn)
                    .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Informacion sobre el usuario", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 18))); // NOI18N

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos Basicos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 18))); // NOI18N

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
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblValorTipoUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(lblValorFechaNac, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                        .addComponent(lblValorNickname, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblValorNombre, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblValorApellido, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblValorCorreo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
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

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Imagen de Perfil"));

        lblImg.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        lblImg.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblImg, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblImg, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cursos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 18))); // NOI18N

        lstCursos.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(lstCursos);

        btnVerCursos.setText("Ver Curso");
        btnVerCursos.addActionListener(this::btnVerCursosActionPerformed);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(94, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnVerCursos)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addContainerGap(94, Short.MAX_VALUE))
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
                .addComponent(btnVerProgramas)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Programas¨", jPanel6);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
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
        String edicionSeleccionada = lstEdiciones1.getSelectedValue();
        if (edicionSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una edición de la lista.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            IControladorEdicion ice = Fabrica.getInstance().getIControladorEdicion();
            IControladorCurso icc = Fabrica.getInstance().getIControladorCurso();

            // 1. Obtener el curso al que pertenece la edición
            String nombreCurso = ice.obtenerCursoDeEdicion(edicionSeleccionada);
            String nombreInstituto = null;

            if (nombreCurso != null) {
                DtCurso dtCurso = icc.consultarCurso(nombreCurso);
                if (dtCurso != null) {
                    nombreInstituto = dtCurso.getNomInstituto();
                }
            }

            // 2. Instanciar según la información obtenida
            ConsultaEdicionCursoFrame frameEdicion;
            if (nombreInstituto != null && nombreCurso != null) {
                frameEdicion = new ConsultaEdicionCursoFrame(nombreInstituto, nombreCurso, edicionSeleccionada);
            } else {
                frameEdicion = new ConsultaEdicionCursoFrame();
                frameEdicion.cargarDatosEdicion(edicionSeleccionada);
            }

            this.getDesktopPane().add(frameEdicion);
            frameEdicion.setVisible(true);
            frameEdicion.toFront();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al abrir la edición: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
//GEN-FIRST:event_btnVerEdicionesActionPerformed
    }//GEN-LAST:event_btnVerEdicionesActionPerformed

    private void rbTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbTodosActionPerformed
        // TODO add your handling code here:
        aplicarFiltros();
    }//GEN-LAST:event_rbTodosActionPerformed

    private void rbDocentesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbDocentesActionPerformed
        // TODO add your handling code here:
        aplicarFiltros();
    }//GEN-LAST:event_rbDocentesActionPerformed

    private void rbEstudiantesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbEstudiantesActionPerformed
        // TODO add your handling code here:
        aplicarFiltros();
    }//GEN-LAST:event_rbEstudiantesActionPerformed

    private void filtroBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtroBtnActionPerformed
        // TODO add your handling code here:
        filtrosVisibles = !filtrosVisibles;
        if (panelFiltros != null) {
            panelFiltros.setVisible(filtrosVisibles);
        }
        filtroBtn.setText(filtrosVisibles ? "▲ Ocultar filtros" : "▼ Filtros avanzados");

    }//GEN-LAST:event_filtroBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnVerCursos;
    private javax.swing.JButton btnVerEdiciones;
    private javax.swing.JButton btnVerProgramas;
    private javax.swing.JTextField buscarTxt;
    private javax.swing.JToggleButton filtroBtn;
    private javax.swing.ButtonGroup filtrosBtnGroup;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
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
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JRadioButton rbDocentes;
    private javax.swing.JRadioButton rbEstudiantes;
    private javax.swing.JRadioButton rbTodos;
    // End of variables declaration//GEN-END:variables
}
