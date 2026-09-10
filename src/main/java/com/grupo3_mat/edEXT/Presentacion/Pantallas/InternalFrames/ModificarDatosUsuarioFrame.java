/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package com.grupo3_mat.edEXT.Presentacion.Pantallas.InternalFrames;

import com.grupo3_mat.edEXT.Logica.DataTypes.DtDocente;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtEstudiante;
import com.grupo3_mat.edEXT.Logica.DataTypes.DtUsuario;
import com.grupo3_mat.edEXT.Logica.Fabrica;
import com.grupo3_mat.edEXT.Logica.Interfaces.IControladorUsuario;
import com.grupo3_mat.edEXT.Presentacion.Utils.GestorImagenes;
import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author fede1
 */

public class ModificarDatosUsuarioFrame extends javax.swing.JInternalFrame {
    private boolean filtrosVisibles = false;
    private String rutaImagenNuevaSeleccionada = null;
    private String rutaImagenActualUsuario = null;
    private boolean esDocenteSeleccionado = false;

    public ModificarDatosUsuarioFrame() {
        initComponents();
        filtroBtn.setText(filtrosVisibles ? "▲ Ocultar filtros" : "▼ Filtros avanzados");
        if (panelFiltros != null) {
            panelFiltros.setVisible(false);
        }

        // Estado inicial: Formulario totalmente limpio y bloqueado
        limpiarYBloquearFormulario();

        // Configuración de listeners
        configurarListeners();

        // Cargar lista de usuarios desde la lógica
        cargarUsuarios();
    }

    /**
     * Limpia todos los campos y deshabilita la interacción con el formulario.
     */
    private void limpiarYBloquearFormulario() {
        buscarTxt.setText("");
        txtNick.setText("");
        txtCorreo.setText("");
        txtNombre.setText("");
        txtApellido.setText("");

        txtNick.setEnabled(false);
        txtCorreo.setEnabled(false);
        txtNombre.setEnabled(false);
        txtApellido.setEnabled(false);

        if (jFecha != null) {
            jFecha.setDate(null);
            jFecha.setEnabled(false);
        }

        if (btnSeleccionarImagen != null) {
            btnSeleccionarImagen.setEnabled(false);
        }

        if (btnGuardar != null) {
            btnGuardar.setEnabled(false);
        }

        if (lblImagenPerfil != null) {
            lblImagenPerfil.setIcon(null);
            lblImagenPerfil.setText("");
        }

        this.rutaImagenNuevaSeleccionada = null;
        this.rutaImagenActualUsuario = null;
        this.esDocenteSeleccionado = false;
    }

    /**
     * Habilita los campos modificables al seleccionar un usuario. Nickname y
     * Correo permanecen deshabilitados de forma fija.
     */
    private void habilitarCamposModificables() {
        txtNombre.setEnabled(true);
        txtApellido.setEnabled(true);

        if (jFecha != null) {
            jFecha.setEnabled(true);
        }

        if (btnSeleccionarImagen != null) {
            btnSeleccionarImagen.setEnabled(true);
        }

        if (btnGuardar != null) {
            btnGuardar.setEnabled(true);
        }

        // Nickname y Correo siempre inhabilitados
        txtNick.setEnabled(false);
        txtCorreo.setEnabled(false);
    }

    private void configurarListeners() {
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

        lstSeleccioneUsuario.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String nickname = lstSeleccioneUsuario.getSelectedValue();
                if (nickname != null) {
                    cargarDatosUsuarioEnFormulario(nickname);
                } else {
                    limpiarYBloquearFormulario();
                }
            }
        });
    }

    private void cargarUsuarios() {
        aplicarFiltros();
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

                    // 2. Filtrado por Texto (con protección anti-null)
                    String nickStr = (dt.getNickname() != null) ? dt.getNickname().toLowerCase() : "";
                    String nomStr = (dt.getNombre() != null) ? dt.getNombre().toLowerCase() : "";
                    String apeStr = (dt.getApellido() != null) ? dt.getApellido().toLowerCase() : "";

                    boolean coincideTexto = texto.isEmpty()
                            || nickStr.contains(texto)
                            || nomStr.contains(texto)
                            || apeStr.contains(texto);

                    if (coincideTipo && coincideTexto) {
                        model.addElement(dt.getNickname());
                    }
                }
            }

            lstSeleccioneUsuario.setModel(model);

            if (lstSeleccioneUsuario.getSelectedValue() == null) {
                limpiarYBloquearFormulario();
            }
        } catch (Exception e) {
            // Imprime en consola para depurar si surge algún detalle interno sin bloquear la pantalla con ventanas
            e.printStackTrace();
        }
    }

    private void cargarDatosUsuarioEnFormulario(String nickname) {
        try {
            IControladorUsuario icu = Fabrica.getInstance().getIControladorUsuario();
            DtUsuario dt = icu.obtenerInfoUsuario(nickname);
            if (dt == null) {
                limpiarYBloquearFormulario();
                return;
            }

            txtNick.setText(dt.getNickname());
            txtCorreo.setText(dt.getCorreo());
            txtNombre.setText(dt.getNombre());
            txtApellido.setText(dt.getApellido());

            if (dt.getFechaNacimiento() != null) {
                jFecha.setDate(java.sql.Date.valueOf(dt.getFechaNacimiento()));
            } else {
                jFecha.setDate(null);
            }

            this.esDocenteSeleccionado = (dt instanceof DtDocente);
            this.rutaImagenActualUsuario = dt.getImagenPath();
            this.rutaImagenNuevaSeleccionada = null;

            GestorImagenes.cargarImagenEnLabel(this.rutaImagenActualUsuario, lblImagenPerfil, this.esDocenteSeleccionado);

            // Se habilitan únicamente los campos modificables
            habilitarCamposModificables();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al obtener datos del usuario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

        btnGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstSeleccioneUsuario = new javax.swing.JList<>();
        panelBuscador = new javax.swing.JPanel();
        buscarBtn = new javax.swing.JButton();
        buscarTxt = new javax.swing.JTextField();
        panelFiltros = new javax.swing.JPanel();
        rbTodos = new javax.swing.JRadioButton();
        rbDocentes = new javax.swing.JRadioButton();
        rbEstudiantes = new javax.swing.JRadioButton();
        filtroBtn = new javax.swing.JToggleButton();
        jPanel2 = new javax.swing.JPanel();
        btnSeleccionarImagen = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        lblImagenPerfil = new javax.swing.JLabel();
        btnGuardar = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jFecha = new com.toedter.calendar.JDateChooser();
        lblFechaNac = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblNombre = new javax.swing.JLabel();
        txtCorreo = new javax.swing.JTextField();
        lblNickname = new javax.swing.JLabel();
        lblCorreo = new javax.swing.JLabel();
        txtNick = new javax.swing.JTextField();
        lblApellido = new javax.swing.JLabel();
        txtApellido = new javax.swing.JTextField();

        setTitle("Modificar usuario");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Seleccione Usuario", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 18))); // NOI18N

        lstSeleccioneUsuario.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(lstSeleccioneUsuario);

        panelBuscador.setBorder(javax.swing.BorderFactory.createTitledBorder("Buscador"));
        panelBuscador.setMinimumSize(new java.awt.Dimension(410, 142));

        buscarBtn.setText("Buscar");

        buscarTxt.setText("jTextField1");

        panelFiltros.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtros"));

        btnGroup.add(rbTodos);
        rbTodos.setText("Todos");
        rbTodos.addActionListener(this::rbTodosActionPerformed);

        btnGroup.add(rbDocentes);
        rbDocentes.setText("Docentes");
        rbDocentes.addActionListener(this::rbDocentesActionPerformed);

        btnGroup.add(rbEstudiantes);
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        filtroBtn.setText("Filtros");
        filtroBtn.addActionListener(this::filtroBtnActionPerformed);

        javax.swing.GroupLayout panelBuscadorLayout = new javax.swing.GroupLayout(panelBuscador);
        panelBuscador.setLayout(panelBuscadorLayout);
        panelBuscadorLayout.setHorizontalGroup(
            panelBuscadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBuscadorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBuscadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBuscadorLayout.createSequentialGroup()
                        .addComponent(buscarTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buscarBtn))
                    .addGroup(panelBuscadorLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(filtroBtn)
                        .addGap(18, 18, 18)
                        .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        panelBuscadorLayout.setVerticalGroup(
            panelBuscadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBuscadorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBuscadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buscarTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buscarBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelBuscadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filtroBtn))
                .addGap(29, 29, 29))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(panelBuscador, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelBuscador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos del Usuario", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 18))); // NOI18N

        btnSeleccionarImagen.setText("Seleccionar Imagen");
        btnSeleccionarImagen.addActionListener(this::btnSeleccionarImagenActionPerformed);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Imagen de Perfil"));

        lblImagenPerfil.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblImagenPerfil.setText("-");
        lblImagenPerfil.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblImagenPerfil, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblImagenPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        btnGuardar.setText("Guardar Cambios");
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);

        lblFechaNac.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblFechaNac.setText("Fecha Nac::");

        txtNombre.setText("jTextField1");

        lblNombre.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblNombre.setText("Nombre:");

        txtCorreo.setText("jTextField1");

        lblNickname.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblNickname.setText("Nickname:");

        lblCorreo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblCorreo.setText("Correo:");

        txtNick.setText("jTextField1");

        lblApellido.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblApellido.setText("Apellido:");

        txtApellido.setText("jTextField1");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFechaNac)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(lblApellido)
                        .addGap(18, 18, 18)
                        .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblNickname)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                            .addComponent(lblCorreo)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                            .addComponent(lblNombre)
                            .addGap(18, 18, 18)
                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtNick, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGap(76, 76, 76)
                            .addComponent(jFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNickname)
                    .addComponent(txtNick, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCorreo)
                    .addComponent(txtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNombre)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblApellido))
                .addGap(28, 28, 28)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFechaNac)
                    .addComponent(jFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(btnSeleccionarImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(75, 75, 75)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addComponent(btnGuardar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSeleccionarImagen)
                .addGap(58, 58, 58))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(97, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(16, 16, 16))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSeleccionarImagenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeleccionarImagenActionPerformed
        // TODO add your handling code here:
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar foto de perfil");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Imágenes (JPG, PNG)", "jpg", "jpeg", "png"));

        int resultado = fileChooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = fileChooser.getSelectedFile();
            this.rutaImagenNuevaSeleccionada = archivoSeleccionado.getAbsolutePath();
            GestorImagenes.desplegarImagen(new ImageIcon(this.rutaImagenNuevaSeleccionada), lblImagenPerfil);
        }
        
    }//GEN-LAST:event_btnSeleccionarImagenActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        // TODO add your handling code here:
        String nickname = txtNick.getText().trim();
        if (nickname.isEmpty() || lstSeleccioneUsuario.getSelectedValue() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un usuario para modificar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        Date fechaDate = jFecha.getDate();

        if (nombre.isEmpty() || apellido.isEmpty() || fechaDate == null) {
            JOptionPane.showMessageDialog(this, "Los campos Nombre, Apellido y Fecha de Nacimiento no pueden estar vacíos.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate fechaNac = fechaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        try {
            String rutaFinalImagen = this.rutaImagenActualUsuario;

            if (this.rutaImagenNuevaSeleccionada != null && !this.rutaImagenNuevaSeleccionada.isEmpty()) {
                File origen = new File(this.rutaImagenNuevaSeleccionada);
                rutaFinalImagen = GestorImagenes.guardarImagenLocal(origen, nickname);
            }

            IControladorUsuario icu = Fabrica.getInstance().getIControladorUsuario();
            icu.modificarDatosUsuario(nickname, nombre, apellido, fechaNac, rutaFinalImagen);

            JOptionPane.showMessageDialog(this, "Usuario actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            cargarUsuarios();
            lstSeleccioneUsuario.setSelectedValue(nickname, true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al modificar el usuario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void filtroBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtroBtnActionPerformed
        // TODO add your handling code here:
        filtrosVisibles = !filtrosVisibles;
        if (panelFiltros != null) {
            panelFiltros.setVisible(filtrosVisibles);
        }
        filtroBtn.setText(filtrosVisibles ? "▲ Ocultar filtros" : "▼ Filtros avanzados");
    
    }//GEN-LAST:event_filtroBtnActionPerformed

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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup btnGroup;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnSeleccionarImagen;
    private javax.swing.JButton buscarBtn;
    private javax.swing.JTextField buscarTxt;
    private javax.swing.JToggleButton filtroBtn;
    private com.toedter.calendar.JDateChooser jFecha;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblApellido;
    private javax.swing.JLabel lblCorreo;
    private javax.swing.JLabel lblFechaNac;
    private javax.swing.JLabel lblImagenPerfil;
    private javax.swing.JLabel lblNickname;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JList<String> lstSeleccioneUsuario;
    private javax.swing.JPanel panelBuscador;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JRadioButton rbDocentes;
    private javax.swing.JRadioButton rbEstudiantes;
    private javax.swing.JRadioButton rbTodos;
    private javax.swing.JTextField txtApellido;
    private javax.swing.JTextField txtCorreo;
    private javax.swing.JTextField txtNick;
    private javax.swing.JTextField txtNombre;
    // End of variables declaration//GEN-END:variables
}
