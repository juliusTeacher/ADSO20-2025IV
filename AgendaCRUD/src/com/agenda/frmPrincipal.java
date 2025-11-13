package com.agenda;

import java.awt.HeadlessException;
import java.sql.*;
import java.util.logging.*;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.table.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

public class frmPrincipal extends javax.swing.JFrame {

    Conexion con = new Conexion();

    Connection cn;
    Statement st;
    ResultSet rs;
    DefaultTableModel modelo;
    
    int idEmp; // => Manejo de las operaciones restantes (Editar, eliminar);

    public frmPrincipal() {
        initComponents();
        id_autoincrementable();
        inhabilitar_txt();
        inhabilitar_btn();
        listar();
    }

    /* Método para listar los datos de la tabla tblEmpeado a través del componente: tablaEmpleado */
    public final void listar() {
        String sql = "SELECT * FROM tblEmpleado";
        try {
            cn = con.establerConexion();
            st = cn.createStatement();
            rs = st.executeQuery(sql);
            Object[] emp = new Object[5];
            modelo = (DefaultTableModel) tablaEmpleado.getModel();
            while (rs.next()) {
                emp[0] = rs.getString("id_empleado");
                emp[1] = rs.getString("nom_empleado");
                emp[2] = rs.getString("direccion");
                emp[3] = rs.getString("telefono");
                emp[4] = rs.getString("correo");
                modelo.addRow(emp);
            }
            tablaEmpleado.setModel(modelo);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error " + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            con.cierreConexion();
        }
    }

    /* Método para registrar un empleado y se confirme en la tabla: tblEmpleado */
    public void registrar() {
        try {

            int codigo = Integer.parseInt(txtCodigo.getText());
            String nom = txtNombre.getText();
            String dir = txtDireccion.getText();
            String tel = txtTelefono.getText();
            String email = txtCorreo.getText();

            if (Integer.toString(codigo).equals("") || nom.equals("") || dir.equals("") || tel.equals("") || email.equals("")) {
                JOptionPane.showMessageDialog(null, "Ingrese el registro del empleado", "Advertencia", JOptionPane.WARNING_MESSAGE);
                limpiarTabla(modelo);
                txtCodigo.requestFocus();
            } else {

                String sql = "INSERT INTO tblEmpleado(id_empleado,nom_empleado,direccion,telefono,correo) "
                        + "VALUES('" + codigo + "','" + nom + "','" + dir + "','" + tel + "','" + email + "')";
                cn = con.establerConexion();
                st = cn.createStatement();
                st.executeUpdate(sql);
                JOptionPane.showMessageDialog(null, "Empleado registrado exitosamente", "Mensaje", JOptionPane.PLAIN_MESSAGE);
                limpiarTabla(modelo);
                inhabilitar_txt();
                btnRegistrar.setEnabled(false);
            }

        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Error " + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            limpiarTabla(modelo);
        } finally{
            con.cierreConexion();
        }
    }
    
    /* Método para modificar y/o actualizar un empleado y se confirme en la tabla: tblEmpleado */
    public void modificar(){
        String id = txtCodigo.getText();
        String nom = txtNombre.getText();
        String dir = txtDireccion.getText();
        String tel = txtTelefono.getText();
        String email = txtCorreo.getText();
        
        if (nom.equals("") || dir.equals("") || tel.equals("") || email.equals("")) {
            JOptionPane.showMessageDialog(null, "Seleccione el registro del empleado", "Advertencia", JOptionPane.WARNING_MESSAGE);
            limpiarTabla(modelo);
        }
        else{
            String sql = "UPDATE tblEmpleado SET nom_empleado='"+nom+"',direccion='"+dir+"',telefono='"+tel+"',correo='"+email+"' WHERE id_empleado="+id;
            try {
                cn = con.establerConexion();
                st = cn.createStatement();
                st.executeUpdate(sql);
                JOptionPane.showMessageDialog(null, "Empleado actualizado", "Mensaje", JOptionPane.PLAIN_MESSAGE);
                limpiarTabla(modelo);
                btnEditar.setEnabled(false);
            } catch (HeadlessException | SQLException e) {
                JOptionPane.showMessageDialog(null, "Error " + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                limpiarTabla(modelo);
            } finally {
                con.cierreConexion();
            }
        } 
    }
    
    /* Método para eliminar un empleado y se confirme en la tabla: tblEmpleado */
    public void eliminar(){
        String id = txtCodigo.getText();
        String sql = "DELETE FROM tblEMpleado WHERE id_empleado ="+id;
        int seleccionado = tablaEmpleado.getSelectedRow();
        if(seleccionado < 0){
            JOptionPane.showMessageDialog(null, "Seleccione el registro del empleado", "Advertencia", JOptionPane.WARNING_MESSAGE);
            limpiarTabla(modelo);
        }
        else{
            try {
                cn = con.establerConexion();
                st = cn.createStatement();
                st.executeUpdate(sql);
                JOptionPane.showMessageDialog(null, "¡Empleado eliminado!","Mensaje",JOptionPane.PLAIN_MESSAGE);
                limpiarTabla(modelo);
                btnEliminar.setEnabled(false);
            } catch (HeadlessException | SQLException e) {
                JOptionPane.showMessageDialog(null, "Error " + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                limpiarTabla(modelo);
            } finally {
                con.cierreConexion();
            }
        }
    }
    
    /* Método para realizar el reporte */
    public void RptEmpleados(){
        var reportPath = "C:\\Users\\DELL\\Documents\\ADSO-20\\AgendaCRUD\\REmpleado.jrxml";
        JasperReport jr;
        try {
            cn = con.establerConexion();
            jr = JasperCompileManager.compileReport(reportPath);
            JasperPrint jp = JasperFillManager.fillReport(jr,null,cn);
            JasperViewer jv = new JasperViewer(jp, false);
            jv.setVisible(true);
        } catch (JRException ex) {
            Logger.getLogger(frmPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            con.cierreConexion();
        }
    }
    
    /* Método para realizar la búsqueda por cualquier tipo en la tabla: tblEmpleado */
    public void busqueda_por(){
        modelo = (DefaultTableModel) tablaEmpleado.getModel();
        TableRowSorter<DefaultTableModel> tabla = new TableRowSorter<>(modelo);
        tablaEmpleado.setRowSorter(tabla);
        tabla.setRowFilter(RowFilter.regexFilter(txtBuscar.getText()));
    }
    
    /* Método para mostrar el autoincrementable: txtCodigo */
    public final void id_autoincrementable(){
        try {
            String sql = "SELECT id_empleado FROM tblEmpleado ORDER BY id_empleado DESC LIMIT 1";
            cn = con.establerConexion();
            st = cn.createStatement();
            rs = st.executeQuery(sql);
            if(rs.next()){
                int id = rs.getInt(1);
                int n = id+1;
                txtCodigo.setText(Integer.toString(n));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error " + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            con.cierreConexion();
        }
    }

    public void limpiarTabla(DefaultTableModel model) {
        for (int i = 0; tablaEmpleado.getRowCount() > i; i++) {
            modelo.removeRow(i);
            i = i - 1;
        }
    }

    public void limpiar_txt() {
        txtCodigo.setText(null);
        txtNombre.setText(null);
        txtDireccion.setText(null);
        txtTelefono.setText(null);
        txtCorreo.setText(null);
    }
    
    /* Método para inhabilitar las cajas de texto */
    public final void inhabilitar_txt(){
        txtCodigo.setEditable(false);
        txtNombre.setEditable(false);
        txtDireccion.setEditable(false);
        txtTelefono.setEditable(false);
        txtCorreo.setEditable(false);
    }
    
    /* Método para habilitar las cajas de texto */
    public void habilitar_txt(){
        txtCodigo.setEditable(true);
        txtNombre.setEditable(true);
        txtDireccion.setEditable(true);
        txtTelefono.setEditable(true);
        txtCorreo.setEditable(true);
        txtNombre.requestFocus();
    }
    
    /* Método para ihnabilitar los botones */
    public final void inhabilitar_btn(){
        btnRegistrar.setEnabled(false);
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
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
        lblCodigo = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblDireccion = new javax.swing.JLabel();
        txtDireccion = new javax.swing.JTextField();
        lblTelefono = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JTextField();
        lblCorreo = new javax.swing.JLabel();
        txtCorreo = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaEmpleado = new javax.swing.JTable();
        btnRegistrar = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnReporte = new javax.swing.JButton();
        lblBuscar = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        btnNuevo = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblCodigo.setText("Empleado No.");

        lblNombre.setText("Nombre");

        lblDireccion.setText("Dirección");

        lblTelefono.setText("Teléfono");

        lblCorreo.setText("Correo");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCodigo)
                    .addComponent(lblNombre)
                    .addComponent(lblDireccion)
                    .addComponent(lblTelefono)
                    .addComponent(lblCorreo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCodigo)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNombre)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDireccion)
                    .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblTelefono)
                        .addGap(18, 18, 18)
                        .addComponent(lblCorreo))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tablaEmpleado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Nombre", "Dirección", "Teléfono", "Correo"
            }
        ));
        tablaEmpleado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaEmpleadoMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablaEmpleado);
        if (tablaEmpleado.getColumnModel().getColumnCount() > 0) {
            tablaEmpleado.getColumnModel().getColumn(0).setResizable(false);
            tablaEmpleado.getColumnModel().getColumn(0).setPreferredWidth(5);
            tablaEmpleado.getColumnModel().getColumn(1).setResizable(false);
            tablaEmpleado.getColumnModel().getColumn(1).setPreferredWidth(50);
            tablaEmpleado.getColumnModel().getColumn(2).setResizable(false);
            tablaEmpleado.getColumnModel().getColumn(2).setPreferredWidth(50);
            tablaEmpleado.getColumnModel().getColumn(3).setResizable(false);
            tablaEmpleado.getColumnModel().getColumn(3).setPreferredWidth(20);
            tablaEmpleado.getColumnModel().getColumn(4).setResizable(false);
            tablaEmpleado.getColumnModel().getColumn(4).setPreferredWidth(70);
        }

        btnRegistrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/save.png"))); // NOI18N
        btnRegistrar.setToolTipText("Registrar empleado");
        btnRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarActionPerformed(evt);
            }
        });

        btnEditar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/edit.png"))); // NOI18N
        btnEditar.setToolTipText("Editar empleado");
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        btnEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/remove.png"))); // NOI18N
        btnEliminar.setToolTipText("Eliminar empleado");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        btnReporte.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/print.png"))); // NOI18N
        btnReporte.setToolTipText("Imprimir Reporte");
        btnReporte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReporteActionPerformed(evt);
            }
        });

        lblBuscar.setText("Búsqueda");

        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarKeyReleased(evt);
            }
        });

        btnNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/add.png"))); // NOI18N
        btnNuevo.setToolTipText("Activar Registro");
        btnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnNuevo)
                        .addGap(6, 6, 6)
                        .addComponent(btnRegistrar)
                        .addGap(6, 6, 6)
                        .addComponent(btnEditar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEliminar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnReporte))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblBuscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 672, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnNuevo)
                            .addComponent(btnRegistrar)
                            .addComponent(btnEditar)
                            .addComponent(btnEliminar)
                            .addComponent(btnReporte))
                        .addGap(40, 40, 40)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblBuscar)
                            .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarActionPerformed
        // TODO add your handling code here:
        registrar();
        listar();
        limpiar_txt();
        id_autoincrementable();
    }//GEN-LAST:event_btnRegistrarActionPerformed

    private void tablaEmpleadoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaEmpleadoMouseClicked
        // TODO add your handling code here:
        int fila = tablaEmpleado.getSelectedRow();
        if(fila == -1){
            JOptionPane.showMessageDialog(null, "Debe seleccionar el registro", "Advertencia",JOptionPane.WARNING_MESSAGE);
        }
        else{
            String codigo = tablaEmpleado.getValueAt(fila, 0).toString();
            String nombre = tablaEmpleado.getValueAt(fila, 1).toString();
            String direccion = tablaEmpleado.getValueAt(fila, 2).toString();
            String telefono = tablaEmpleado.getValueAt(fila, 3).toString();
            String correo = tablaEmpleado.getValueAt(fila, 4).toString();
            
            txtCodigo.setText(codigo);
            txtNombre.setText(nombre);
            txtDireccion.setText(direccion);
            txtTelefono.setText(telefono);
            txtCorreo.setText(correo);
            habilitar_txt();
            btnEditar.setEnabled(true);
            btnEliminar.setEnabled(true);
        }
        
    }//GEN-LAST:event_tablaEmpleadoMouseClicked

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        // TODO add your handling code here:
        modificar();
        listar();
        limpiar_txt();
        inhabilitar_txt();
        inhabilitar_btn();
        id_autoincrementable();
    }//GEN-LAST:event_btnEditarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        // TODO add your handling code here:
        eliminar();
        listar();
        limpiar_txt();
        inhabilitar_txt();
        inhabilitar_btn();
        id_autoincrementable();
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        // TODO add your handling code here:
        habilitar_txt();
        btnRegistrar.setEnabled(true);
        id_autoincrementable();
    }//GEN-LAST:event_btnNuevoActionPerformed

    private void txtBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyReleased
        // TODO add your handling code here:
        busqueda_por();
    }//GEN-LAST:event_txtBuscarKeyReleased

    private void btnReporteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReporteActionPerformed
        // TODO add your handling code here:
        RptEmpleados();
    }//GEN-LAST:event_btnReporteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JButton btnReporte;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblCodigo;
    private javax.swing.JLabel lblCorreo;
    private javax.swing.JLabel lblDireccion;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblTelefono;
    private javax.swing.JTable tablaEmpleado;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtCorreo;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
