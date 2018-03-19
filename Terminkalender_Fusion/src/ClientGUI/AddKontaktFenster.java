/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientGUI;

import Server.ClientStub;
import Utilities.BenutzerException;
import java.awt.Color;
import java.rmi.RemoteException;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Edwrard Nana
 */
public class AddKontaktFenster extends javax.swing.JFrame {
    
    private final ClientStub stub;
    private int sitzungsID;
    //private String username;
    /**
     * Creates new form AddKontakt
     * @param stub
     * @param sitzungsID
     * @throws Utilities.BenutzerException
     * @throws java.rmi.RemoteException
     */
    public AddKontaktFenster(ClientStub stub, int sitzungsID) throws BenutzerException, RemoteException {
        initComponents();
        this.stub = stub;
        this.sitzungsID = sitzungsID;
        
        setColor();
    }
    
    private void setColor() throws RemoteException, BenutzerException{
        
        Color[] color = stub.getColor(sitzungsID);
        Color color1 = color[0];
        Color color2 = color[1];
        Color color3 = color[2];
        Color color4 = color[3];
        
        //Light theme
        jPanel3.setBackground(color1);
        hinzufuegenLabel.setBackground(color1);
        
        //Middle theme
        jPanel1.setBackground(color2);
        
        //Font 
        jLabel7.setForeground(color4);
        jLabel1.setForeground(color4);
        hinzufuegenlabel1.setForeground(color4);
        userNameField.setForeground(color4);
        
    }
    
    private AddKontaktFenster() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Methode in der GUI um Kontakte zu Adden 
     * @param username username des users der geadden werden sollen
     * @throws RemoteException 
     */
    public void addKontakt(String username) throws RemoteException {
         try {
            //sitzungsID = stub.addKontakt(username, sitzungsID);
            //if(sitzungsID>0){
            stub.addKontakt(username,sitzungsID);
            JOptionPane.showMessageDialog(null, "Kontakt erfolgreich hinzugefügt", "AddKontakt", JOptionPane.INFORMATION_MESSAGE);
            
            //username.setText(null);
            
            this.setVisible(false);
            //}
        } catch (BenutzerException e) {
            //JOptionPane.showInputDialog();
            JOptionPane.showMessageDialog(null,e.getMessage(), "AddKontakt - Terminkalender", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,ex.getMessage(), "AddKontakt - Terminkalender", JOptionPane.ERROR_MESSAGE);
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
        jLabel1 = new javax.swing.JLabel();
        userNameField = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        hinzufuegenLabel = new javax.swing.JPanel();
        hinzufuegenlabel1 = new javax.swing.JLabel();

        setTitle("Add Kontakt - Termin Kalender");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);
        setType(java.awt.Window.Type.POPUP);

        jPanel1.setBackground(new java.awt.Color(29, 30, 66));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(240, 240, 240));
        jLabel1.setText("Username des Kontakts");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 220, 180, -1));

        userNameField.setBackground(new java.awt.Color(29, 30, 66));
        userNameField.setForeground(new java.awt.Color(240, 240, 240));
        userNameField.setBorder(null);
        userNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userNameFieldActionPerformed(evt);
            }
        });
        jPanel1.add(userNameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 250, 220, -1));

        jPanel3.setBackground(new java.awt.Color(46, 49, 117));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(240, 240, 240));
        jLabel7.setText("Add Kontakt");
        jPanel3.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ClientGUI/if_Account_1891016.png"))); // NOI18N
        jPanel3.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 30, -1, -1));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, 420, 190));
        jPanel1.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 280, 220, 10));

        hinzufuegenLabel.setBackground(new java.awt.Color(46, 49, 117));
        hinzufuegenLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                hinzufuegenLabelMouseClicked(evt);
            }
        });

        hinzufuegenlabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        hinzufuegenlabel1.setForeground(new java.awt.Color(240, 240, 240));
        hinzufuegenlabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        hinzufuegenlabel1.setText("Hinzufügen");
        hinzufuegenlabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                hinzufuegenlabel1MouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                hinzufuegenlabel1MouseReleased(evt);
            }
        });

        javax.swing.GroupLayout hinzufuegenLabelLayout = new javax.swing.GroupLayout(hinzufuegenLabel);
        hinzufuegenLabel.setLayout(hinzufuegenLabelLayout);
        hinzufuegenLabelLayout.setHorizontalGroup(
            hinzufuegenLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, hinzufuegenLabelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(hinzufuegenlabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                .addContainerGap())
        );
        hinzufuegenLabelLayout.setVerticalGroup(
            hinzufuegenLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(hinzufuegenlabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        jPanel1.add(hinzufuegenLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 250, 120, 30));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 461, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void userNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userNameFieldActionPerformed

         //String username, password, email;
         //username = this.userNameField.getText();
    }//GEN-LAST:event_userNameFieldActionPerformed

    private void hinzufuegenlabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hinzufuegenlabel1MouseClicked
        hinzufuegenLabel.setBackground(new Color(66, 47, 124));
        
        String username;
        username = this.userNameField.getText();
        //int sitzungsID;
               
        try {
            this.addKontakt(username);
            //sitzungsID = stub.addKontakt(username, sitzungsID);
            //stub.addKontakt(username, sitzungsID);
        } catch (RemoteException e) {
            JOptionPane.showInputDialog(e.getMessage());
        }
    }//GEN-LAST:event_hinzufuegenlabel1MouseClicked

    private void hinzufuegenLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hinzufuegenLabelMouseClicked
        
    }//GEN-LAST:event_hinzufuegenLabelMouseClicked

    private void hinzufuegenlabel1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hinzufuegenlabel1MouseReleased
        // TODO add your handling code here:
        hinzufuegenLabel.setBackground(new Color(46,49,117));
    }//GEN-LAST:event_hinzufuegenlabel1MouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel hinzufuegenLabel;
    private javax.swing.JLabel hinzufuegenlabel1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField userNameField;
    // End of variables declaration//GEN-END:variables

}
