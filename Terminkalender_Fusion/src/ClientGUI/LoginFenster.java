/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientGUI;

import Server.ClientStub;
import Utilities.BenutzerException;
import Server.Utilities.DatenbankException;
import Utilities.Datum;
import Utilities.TerminException;
import Utilities.Zeit;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author niroshan
 */
public class LoginFenster extends javax.swing.JFrame {

    private ClientStub stub;
    private int sitzungsID;

    /**
     * Creates new form Fenster
     *
     * @param stub
     * @throws java.rmi.RemoteException
     * @throws Utilities.BenutzerException
     */
    public LoginFenster(ClientStub stub) throws RemoteException, BenutzerException {
        initComponents();
        this.stub = stub;
        infoBoxPanel.setVisible(false);
        //setColor();
    }
    /*
    private void setColor() throws RemoteException, BenutzerException{
        
        Color[] color = stub.getColor(sitzungsID);
        Color color1 = color[0];
        Color color2 = color[1];
        Color color3 = color[2];
        Color color4 = color[3];
        
        //Light
        jPanel1.setBackground(color1);
        anmeldenPanel.setBackground(color1);
        beendenPanel.setBackground(color1);
        jPanel7.setBackground(color1);
        
        
        //Middle
        jPanel2.setBackground(color2);
        jPanel6.setBackground(color2);
        
        
        //Font 
        jLabel9.setForeground(color4);
        jLabel5.setForeground(color4);
        jBenutzernameField.setForeground(color4);
        jPasswortField.setForeground(color4);
        jLabel8.setForeground(color4);
        anmeldenLabel.setForeground(color4);
        beendenLabel.setForeground(color4);

        
    }
*/
    private LoginFenster() {
        //initComponents();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void setStub(ClientStub stub){
        this.stub = stub;
    }

    public void zuweisen(String username, String password) throws RemoteException, TerminException, Datum.DatumException, Zeit.ZeitException, SQLException, DatenbankException {

        try{
            String result = stub.findServerForUser(username);
            switch(result){                  
                case "true":                     
                    break;
                case "false":
                    infoBoxText.setText("User existiert nicht!");
                    infoBoxPanel.setVisible(true);
                    return;                  
                default:
                    Registry registry = LocateRegistry.getRegistry(result, 1099);                   
                    try {
                        stub = (ClientStub) registry.lookup("ClientStub");
                        System.out.println("-> Neue Verbindung zu " + result + " hergestellt");
                    } catch (NotBoundException | AccessException ex) {

                    }                   
            }  
            sitzungsID = stub.einloggen(username, password);
            if(sitzungsID < 0){
                //JOptionPane.showMessageDialog(null, "Falsches Passwort. Anmelden gescheitert!", "Anmelden", JOptionPane.ERROR_MESSAGE);
                infoBoxText.setText("Falsches Passwort. Anmelden gescheitert!");
                
                //infoBoxPanel.setBackground(new Color(153,0,51));
                infoBoxPanel.setVisible(true);
                verbindeMitRoot();
            
            }
            else{
                //JOptionPane.showMessageDialog(null, "Anmeldung erfolgreich!", "anmelden", JOptionPane.INFORMATION_MESSAGE);
                infoBoxText.setText("Anmeldung erfolgreich!");
                
                infoBoxPanel.setBackground(new Color(77,168,99));
                infoBoxPanel.setVisible(true);
                
                this.setVisible(false);
                
                Hauptfenster start = new Hauptfenster(stub, sitzungsID, this);
                start.fillContactList();
                start.fillMeldList();
                start.setVisible(true);                          
            }  
        }
        catch(BenutzerException | DatenbankException e){
            //JOptionPane.showMessageDialog(null, e.getMessage(), "Anmelden", JOptionPane.ERROR_MESSAGE);
            infoBoxText.setText(e.getMessage());
            infoBoxPanel.setVisible(true);
            JOptionPane.showMessageDialog(null,e.getMessage(), "Anmelden", JOptionPane.ERROR_MESSAGE);
            verbindeMitRoot();
        }  
    }

    private void verbindeMitRoot(){
        Registry registry;     
                
        String rootIP;
        String line;            
        BufferedReader bufferedReader = null;

        //liest IP-Adressen aller Server aus File und speichert sie in LinkedList
        File file = new File(".\\src\\data\\serverlist.txt"); 
        //für mac-pcs
        if (!file.canRead() || !file.isFile()){
            file = new File("./src/data/severlist.txt"); 
        }
        try { 
            bufferedReader = new BufferedReader(new FileReader(file));  
            if((line = bufferedReader.readLine()) != null) { 
                rootIP = line;
                
                try {
                    //baut Verbindung zu Server auf
                    registry = LocateRegistry.getRegistry(rootIP, 1099);
                    this.stub = (ClientStub) registry.lookup("ClientStub");
                    System.out.println("LOG * ---> Verbindung zu Root-Server mit IP " + rootIP + " hergestellt!");

                } catch (RemoteException | NotBoundException ex) {
                    System.out.println("LOG * ---> Verbindung zu Root-Server mit IP " + rootIP + " konnte nicht hergestellt werden!");  
                }
            }      
            else{
                System.out.println("LOG * ---> Verbindung zu Root-Server konnte nicht hergestellt werden!");
            }
        } catch (IOException e) { 
            e.printStackTrace(); 
        } 
        // zum schließen des readers
        finally { 
            if (bufferedReader != null) 
                try { 
                    bufferedReader.close(); 
                } catch (IOException e) { 
            } 
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

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jBenutzernameField = new javax.swing.JTextField();
        jPasswortField = new javax.swing.JPasswordField();
        anmeldenPanel = new javax.swing.JPanel();
        anmeldenLabel = new javax.swing.JLabel();
        beendenPanel = new javax.swing.JPanel();
        beendenLabel = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        infoBoxPanel = new javax.swing.JPanel();
        infoBoxText = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Termin Kalender");
        setResizable(false);

        jPanel2.setBackground(new java.awt.Color(29, 30, 66));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(46, 49, 117));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 58)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(29, 30, 66));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Kalender");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 370, 200));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 48)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(240, 240, 240));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Termin ");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(-100, -30, 370, 200));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setForeground(java.awt.SystemColor.activeCaption);
        jLabel2.setText("Noch kein Account ?");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 306, -1, 40));

        jPanel6.setBackground(new java.awt.Color(29, 30, 66));
        jPanel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel6MouseClicked(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(240, 240, 240));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Registrieren");
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 310, 150, 30));

        jPanel2.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, 310, 370));

        jBenutzernameField.setBackground(new java.awt.Color(29, 30, 66));
        jBenutzernameField.setForeground(new java.awt.Color(240, 240, 240));
        jBenutzernameField.setBorder(null);
        jBenutzernameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBenutzernameFieldActionPerformed(evt);
            }
        });
        jPanel2.add(jBenutzernameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 80, 190, 30));

        jPasswortField.setBackground(new java.awt.Color(29, 30, 66));
        jPasswortField.setForeground(new java.awt.Color(240, 240, 240));
        jPasswortField.setBorder(null);
        jPanel2.add(jPasswortField, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 160, 190, 30));

        anmeldenPanel.setBackground(new java.awt.Color(46, 49, 117));
        anmeldenPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                anmeldenPanelMouseClicked(evt);
            }
        });

        anmeldenLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        anmeldenLabel.setForeground(new java.awt.Color(240, 240, 240));
        anmeldenLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        anmeldenLabel.setText("Anmelden");
        anmeldenLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                anmeldenLabelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                anmeldenLabelMouseEntered(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                anmeldenLabelMouseReleased(evt);
            }
        });
        anmeldenLabel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                anmeldenLabelKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout anmeldenPanelLayout = new javax.swing.GroupLayout(anmeldenPanel);
        anmeldenPanel.setLayout(anmeldenPanelLayout);
        anmeldenPanelLayout.setHorizontalGroup(
            anmeldenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(anmeldenPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(anmeldenLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                .addContainerGap())
        );
        anmeldenPanelLayout.setVerticalGroup(
            anmeldenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(anmeldenLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
        );

        jPanel2.add(anmeldenPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 330, 160, 40));

        beendenPanel.setBackground(new java.awt.Color(46, 49, 117));
        beendenPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                beendenPanelMouseClicked(evt);
            }
        });

        beendenLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        beendenLabel.setForeground(new java.awt.Color(240, 240, 240));
        beendenLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        beendenLabel.setText("Beenden");
        beendenLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                beendenLabelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout beendenPanelLayout = new javax.swing.GroupLayout(beendenPanel);
        beendenPanel.setLayout(beendenPanelLayout);
        beendenPanelLayout.setHorizontalGroup(
            beendenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(beendenPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(beendenLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                .addContainerGap())
        );
        beendenPanelLayout.setVerticalGroup(
            beendenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(beendenLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
        );

        jPanel2.add(beendenPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 330, 150, -1));

        jPanel7.setBackground(new java.awt.Color(46, 49, 117));
        jPanel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel7MouseClicked(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(240, 240, 240));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Passwort vergessen?");
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addContainerGap(11, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 220, 150, 20));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(240, 240, 240));
        jLabel5.setText("Passwort");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 170, -1, 18));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(240, 240, 240));
        jLabel9.setText("Benutzername");
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 90, -1, 18));
        jPanel2.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 190, 190, 10));
        jPanel2.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 110, 190, 10));

        infoBoxPanel.setBackground(new java.awt.Color(182, 30, 81));
        infoBoxPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        infoBoxText.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        infoBoxText.setForeground(new java.awt.Color(240, 240, 240));
        infoBoxText.setText("jLabel3");
        infoBoxPanel.add(infoBoxText, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 200, -1));

        jPanel2.add(infoBoxPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 270, 230, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jBenutzernameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBenutzernameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jBenutzernameFieldActionPerformed

    private void anmeldenPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_anmeldenPanelMouseClicked
        // TODO add your handling code here:
        
    }//GEN-LAST:event_anmeldenPanelMouseClicked

    private void beendenPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_beendenPanelMouseClicked
        // TODO add your handling code here:
        
    }//GEN-LAST:event_beendenPanelMouseClicked

    private void jPanel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel7MouseClicked
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jPanel7MouseClicked

    private void jPanel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel6MouseClicked
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jPanel6MouseClicked

    private void jLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseClicked
        // TODO add your handling code here:
        jLabel7.setForeground(Color.WHITE);
        RegistrierenFenster start;
        try {
            start = new RegistrierenFenster(stub);
            start.setVisible(true);
        } catch (RemoteException | BenutzerException ex) {
            Logger.getLogger(LoginFenster.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_jLabel7MouseClicked

    private void anmeldenLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_anmeldenLabelMouseClicked
        // TODO add your handling code here:
        
        anmeldenPanel.setBackground(new Color(46,49,117));
        
        String username, password;

        username = this.jBenutzernameField.getText();

        password = this.jPasswortField.getText();

        try {
            this.zuweisen(username, password);
         } catch (RemoteException e) {
            JOptionPane.showInputDialog(e.getMessage());
        } catch (TerminException e) {
            JOptionPane.showInputDialog(e.getMessage());
        } catch (Datum.DatumException e) {
            JOptionPane.showInputDialog(e.getMessage());
        } catch (Zeit.ZeitException e) {
            JOptionPane.showInputDialog(e.getMessage());
        } catch (SQLException | DatenbankException e) {
            JOptionPane.showInputDialog(e.getMessage());
        }
    }//GEN-LAST:event_anmeldenLabelMouseClicked

    private void beendenLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_beendenLabelMouseClicked
        // TODO add your handling code here:
        beendenPanel.setBackground(new Color(86, 16, 16));
        System.exit(0);
    }//GEN-LAST:event_beendenLabelMouseClicked

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
        // TODO add your handling code here:
        jLabel8.setForeground(Color.WHITE);
        ForgotPasswordFenster resetPW;
        try {
            resetPW = new ForgotPasswordFenster(stub,sitzungsID);
            resetPW.setVisible(true);
        } catch (RemoteException | BenutzerException ex) {
            Logger.getLogger(LoginFenster.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_jLabel8MouseClicked

    private void anmeldenLabelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_anmeldenLabelKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_anmeldenLabelKeyPressed

    private void anmeldenLabelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_anmeldenLabelMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_anmeldenLabelMouseReleased

    private void anmeldenLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_anmeldenLabelMouseEntered
        // TODO add your handling code here:
        
    }//GEN-LAST:event_anmeldenLabelMouseEntered

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
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginFenster.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new LoginFenster().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel anmeldenLabel;
    private javax.swing.JPanel anmeldenPanel;
    private javax.swing.JLabel beendenLabel;
    private javax.swing.JPanel beendenPanel;
    private javax.swing.JPanel infoBoxPanel;
    private javax.swing.JLabel infoBoxText;
    private javax.swing.JTextField jBenutzernameField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPasswordField jPasswortField;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    // End of variables declaration//GEN-END:variables
}
