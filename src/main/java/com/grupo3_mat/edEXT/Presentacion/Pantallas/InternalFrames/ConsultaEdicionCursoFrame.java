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
    
    // Para invocar desde consulta Usuario
    // Para invocar únicamente con el nombre de la edición
    public ConsultaEdicionCursoFrame(String nombreEdicion) {
        this(); // Llama al constructor por defecto: inicializa componentes, controladores y carga institutos

        if (nombreEdicion == null || nombreEdicion.trim().isEmpty()) {
            return;
        }

        String instEncontrado = null;
        String cursoEncontrado = null;

        // 1. Buscar a qué Instituto y Curso pertenece la edición navegando la jerarquía
        List<String> institutos = icInstituto.listarInstitutos();
        if (institutos != null) {
            for (String inst : institutos) {
                List<String> cursos = icCurso.listarCursosPorInstituto(inst);
                if (cursos != null) {
                    for (String curso : cursos) {
                        List<String> ediciones = icEdicion.listarEdicionesPorCurso(curso);
                        if (ediciones != null && ediciones.contains(nombreEdicion)) {
                            instEncontrado = inst;
                            cursoEncontrado = curso;
                            break;
                        }
                    }
                }
                if (instEncontrado != null) {
                    break; // Cortar el bucle exterior si ya lo encontramos
                }
            }
        }

        // 2. Si encontramos la ruta, sincronizamos la UI deshabilitando temporalmente los listeners
        if (instEncontrado != null && cursoEncontrado != null) {
            cmbInstitutos.removeActionListener(this::cmbInstitutosActionPerformed);
            cmbCursos.removeActionListener(this::cmbCursosActionPerformed);
            cmbEdiciones.removeActionListener(this::cmbEdicionesActionPerformed);

            // Cargar y seleccionar el Instituto
            cmbInstitutos.setSelectedItem(instEncontrado);
            alSeleccionarInstituto();

            // Cargar y seleccionar el Curso
            cmbCursos.setSelectedItem(cursoEncontrado);
            alSeleccionarCurso();

            // Seleccionar la Edición y cargar los datos
            cmbEdiciones.setSelectedItem(nombreEdicion);
            alSeleccionarEdicion();

            // Reactivar listeners para la interacción del usuario
            cmbInstitutos.addActionListener(this::cmbInstitutosActionPerformed);
            cmbCursos.addActionListener(this::cmbCursosActionPerformed);
            cmbEdiciones.addActionListener(this::cmbEdicionesActionPerformed);
        }
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

    // Al cambiar de Curso cargar Ediciones
    private void alSeleccionarEdicion() {
    limpiarCampos();

    String nombreEdicion = (String) cmbEdiciones.getSelectedItem();

    if (nombreEdicion != null) {
        DTEdicionCurso dt = icEdicion.mostrarDetalleEdicion(nombreEdicion);

        if (dt != null) {
            lblValNombre.setText(dt.getNombre() != null ? dt.getNombre() : "");
            lblValFechaInicio.setText(dt.getFechaInicio() != null ? dt.getFechaInicio().toString() : "");
            lblValFechaFin.setText(dt.getFechaFin() != null ? dt.getFechaFin().toString() : "");
            lblValFechaPublicacion.setText(dt.getFechaPublicacion() != null ? dt.getFechaPublicacion().toString() : "");

            // Lógica de Cupos
            int cupoTotal = dt.getCupo();

            if (cupoTotal > 0) {
                int cupoDisponible = dt.getCupoDisponible();
                lblValCupoTotal.setText(String.valueOf(cupoTotal));
                lblValNombre3.setText(String.valueOf(cupoDisponible));
            } else {
                lblValCupoTotal.setText("Sin límite");
                lblValNombre3.setText("Sin límite");
            }

            // Cargar docentes
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
    lblValNombre.setText("");
    lblValFechaInicio.setText("");
    lblValFechaFin.setText("");
    lblValFechaPublicacion.setText("");
    lblValCupoTotal.setText("");
    lblValNombre3.setText("");
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
        labelDocentes = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listDocentes = new javax.swing.JList<>();
        jPanel2 = new javax.swing.JPanel();
        labelFechaInicio = new javax.swing.JLabel();
        labelFechaFin = new javax.swing.JLabel();
        labelNombre = new javax.swing.JLabel();
        labelCupo = new javax.swing.JLabel();
        labelFechaPublicacion = new javax.swing.JLabel();
        labelCupo1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        lblValCupoTotal = new javax.swing.JLabel();
        lblValFechaPublicacion = new javax.swing.JLabel();
        lblValNombre3 = new javax.swing.JLabel();
        lblValNombre = new javax.swing.JLabel();
        lblValFechaFin = new javax.swing.JLabel();
        lblValFechaInicio = new javax.swing.JLabel();

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

        labelDocentes.setText("Docentes:");

        jScrollPane1.setViewportView(listDocentes);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        labelFechaInicio.setText("Fecha de inicio:");

        labelFechaFin.setText("Fecha de finalización:");

        labelNombre.setText("Nombre:");

        labelCupo.setText("Cupo total:");

        labelFechaPublicacion.setText("Fecha de publicación:");

        labelCupo1.setText("Cupo disponible:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelFechaInicio)
                    .addComponent(labelFechaPublicacion)
                    .addComponent(labelNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelCupo)
                    .addComponent(labelCupo1)
                    .addComponent(labelFechaFin, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelNombre)
                .addGap(28, 28, 28)
                .addComponent(labelFechaInicio)
                .addGap(28, 28, 28)
                .addComponent(labelFechaFin)
                .addGap(29, 29, 29)
                .addComponent(labelFechaPublicacion)
                .addGap(30, 30, 30)
                .addComponent(labelCupo)
                .addGap(31, 31, 31)
                .addComponent(labelCupo1)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblValCupoTotal.setText("\"\"");

        lblValFechaPublicacion.setText("\"\"");

        lblValNombre3.setText("\"\"");

        lblValNombre.setText("\"\"");

        lblValFechaFin.setText("\"\"");

        lblValFechaInicio.setText("\"\"");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblValNombre3, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblValFechaPublicacion, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblValCupoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblValFechaInicio, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblValFechaFin, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblValNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblValNombre)
                .addGap(30, 30, 30)
                .addComponent(lblValFechaInicio)
                .addGap(30, 30, 30)
                .addComponent(lblValFechaFin)
                .addGap(27, 27, 27)
                .addComponent(lblValFechaPublicacion)
                .addGap(29, 29, 29)
                .addComponent(lblValCupoTotal)
                .addGap(30, 30, 30)
                .addComponent(lblValNombre3)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(labelDocentes)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1))
                .addContainerGap(8, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addComponent(labelDocentes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(386, 386, 386)
                .addComponent(btnCerrar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(464, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelSeleccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(125, 125, 125)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(69, 69, 69))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelSeleccion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
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
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelCupo;
    private javax.swing.JLabel labelCupo1;
    private javax.swing.JLabel labelCurso;
    private javax.swing.JLabel labelDocentes;
    private javax.swing.JLabel labelEdicion;
    private javax.swing.JLabel labelFechaFin;
    private javax.swing.JLabel labelFechaInicio;
    private javax.swing.JLabel labelFechaPublicacion;
    private javax.swing.JLabel labelInstituto;
    private javax.swing.JLabel labelNombre;
    private javax.swing.JLabel lblValCupoTotal;
    private javax.swing.JLabel lblValFechaFin;
    private javax.swing.JLabel lblValFechaInicio;
    private javax.swing.JLabel lblValFechaPublicacion;
    private javax.swing.JLabel lblValNombre;
    private javax.swing.JLabel lblValNombre3;
    private javax.swing.JList<String> listDocentes;
    private javax.swing.JPanel panelSeleccion;
    // End of variables declaration//GEN-END:variables
}
