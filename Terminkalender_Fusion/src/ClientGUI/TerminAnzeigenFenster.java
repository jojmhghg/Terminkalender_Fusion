package ClientGUI;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import Server.ClientStub;
import Utilities.BenutzerException;
import Utilities.Datum;
import Utilities.Teilnehmer;
import Utilities.Termin;
import Utilities.TerminException;
import Utilities.Zeit;
import java.awt.Color;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

/**
 *
 * @author niroshan
 */
public class TerminAnzeigenFenster extends javax.swing.JFrame {

    private final ClientStub stub;
    private final int sitzungsID;
    private int terminID;
    private Termin termin;
    DefaultListModel teilnehmerListeModel = new DefaultListModel();
    private Hauptfenster hauptfenster;

    /**
     * Creates new form CalenderInhalt
     *
     * @param terminID
     * @param stub
     * @param sitzungsID
     * @throws java.rmi.RemoteException
     * @throws Utilities.BenutzerException
     * @throws Utilities.TerminException
     */
    public TerminAnzeigenFenster(int terminID, ClientStub stub, int sitzungsID, Hauptfenster hauptfenster) throws RemoteException, BenutzerException, TerminException {
        initComponents();
        
        //Farben des ComboButtons ändern
        kontakte.getEditor().getEditorComponent().setBackground(new java.awt.Color(29,30,66));
        kontakte.getEditor().getEditorComponent().setForeground(Color.white);
        //erstes Item "null"
        this.kontakte.addItem("");
        //alle Kontakte einfuegen
        for(String kontakte : stub.getKontakte(sitzungsID)){
                this.kontakte.addItem(kontakte);
        }
        
        this.hauptfenster = hauptfenster;
        this.terminID = terminID;
        this.stub = stub;
        this.sitzungsID = sitzungsID;
        this.termin = stub.getTermin(terminID, sitzungsID);
        
        setColor();
        
        datum.setText(termin.getDatum().toString());
        titel.setText(termin.getTitel());
        startZeit.setText(termin.getBeginn().toString() + " Uhr");
        endZeit.setText(termin.getEnde().toString() + " Uhr");
        ort.setText(termin.getOrt());
        terminersteller.setText(termin.getOwner());      
        notiz.setText(termin.getNotiz());
        fillTeilnehmerlist();
        teilnehmerliste.setModel(teilnehmerListeModel);
        
        //Wenn nicht editierbar nimm die Möglichkeit zu editieren bzw. einzuladen(außer Owner)
        if(!termin.getEditierbar() && !stub.getUsername(sitzungsID).equals(termin.getOwner())){
            bearbeitenLabel.setVisible(false);
            jPanel5.setVisible(false);
            kontakte.setVisible(false);
            addTeilnahmeLabel.setVisible(false);
            jPanel8.setVisible(false);
            jPanel3.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 200, 180));
        }      
    }
    
    private void setColor() throws RemoteException, BenutzerException{
        
        Color[] color = stub.getColor(sitzungsID);
        Color color1 = color[0];
        Color color2 = color[1];
        Color color3 = color[2];
        Color color4 = color[3];

        
        //Light
        jPanel2.setBackground(color1);
        jPanel3.setBackground(color1);
        jPanel4.setBackground(color1);
        jPanel6.setBackground(color1);
        jPanel5.setBackground(color1);
        jPanel7.setBackground(color1);
        
        
        //Middle
        jPanel1.setBackground(color2);
        notiz.setBackground(color2);
        teilnehmerliste.setBackground(color2);
        
        //Font 
        titel.setForeground(color4);
        jLabel2.setForeground(color4);
        jLabel3.setForeground(color4);
        jLabel4.setForeground(color4);
        jLabel5.setForeground(color4);
        jLabel1.setForeground(color4);
        start.setForeground(color4);
        jLabel6.setForeground(color4);
        notiz.setForeground(color4);
        jLabel8.setForeground(color4);
        bearbeitenLabel.setForeground(color4);
        jLabel9.setForeground(color4);
        datum.setForeground(color4);
        startZeit.setForeground(color4);
        endZeit.setForeground(color4);
        ort.setForeground(color4);
        terminersteller.setForeground(color4);
        datum.setForeground(color4);
        kontakte.setForeground(color4);
        addTeilnahmeLabel.setForeground(color4);
        teilnehmerliste.setForeground(color4);
        
           
        
    }

    private void fillTeilnehmerlist() throws RemoteException, BenutzerException, TerminException {
        teilnehmerListeModel.clear();
        for (Teilnehmer teilnehmer : termin.getTeilnehmerliste()) {
            if (teilnehmer.checkIstTeilnehmer()) {
                teilnehmerListeModel.addElement(teilnehmer.getUsername() + " (nimmt Teil)");
            } else {
                teilnehmerListeModel.addElement(teilnehmer.getUsername() + " (offen)");
            }         
        }
    }

    private TerminAnzeigenFenster() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        datum = new javax.swing.JLabel();
        start = new javax.swing.JLabel();
        startZeit = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        endZeit = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        ort = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        terminersteller = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        titel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        teilnehmerliste = new javax.swing.JList<>();
        jLabel4 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        addTeilnahmeLabel = new javax.swing.JLabel();
        kontakte = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        notiz = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        bearbeitenLabel = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(29, 30, 66));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(240, 240, 240));
        jLabel1.setText("Datum");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 83, 28));

        datum.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        datum.setForeground(new java.awt.Color(240, 240, 240));
        datum.setText("datum");
        jPanel1.add(datum, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 80, 171, 28));

        start.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        start.setForeground(new java.awt.Color(240, 240, 240));
        start.setText("Start Zeit");
        jPanel1.add(start, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, 83, 35));

        startZeit.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        startZeit.setForeground(new java.awt.Color(240, 240, 240));
        startZeit.setText("startZeit");
        jPanel1.add(startZeit, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 120, 90, 28));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(240, 240, 240));
        jLabel2.setText("End Zeit");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 81, 27));

        endZeit.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        endZeit.setForeground(new java.awt.Color(240, 240, 240));
        endZeit.setText("endZeit");
        jPanel1.add(endZeit, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 160, 144, 28));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(240, 240, 240));
        jLabel3.setText("Ort");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, 90, 26));

        ort.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        ort.setForeground(new java.awt.Color(240, 240, 240));
        ort.setText("Ort");
        jPanel1.add(ort, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 200, 124, 26));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(240, 240, 240));
        jLabel5.setText("Terminersteller");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 240, -1, 30));

        terminersteller.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        terminersteller.setForeground(new java.awt.Color(240, 240, 240));
        terminersteller.setText("ersteller");
        jPanel1.add(terminersteller, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 240, 132, 27));

        jPanel2.setBackground(new java.awt.Color(46, 49, 117));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        titel.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        titel.setForeground(new java.awt.Color(240, 240, 240));
        titel.setText("Titel");
        jPanel2.add(titel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 360, 43));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 470, 60));

        jPanel3.setBackground(new java.awt.Color(46, 49, 117));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        teilnehmerliste.setBackground(new java.awt.Color(29, 30, 66));
        teilnehmerliste.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        teilnehmerliste.setForeground(new java.awt.Color(240, 240, 240));
        teilnehmerliste.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(teilnehmerliste);

        jPanel3.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 230, 130));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(240, 240, 240));
        jLabel4.setText("Teilnehmer");
        jPanel3.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jPanel8.setBackground(new java.awt.Color(29, 30, 66));

        addTeilnahmeLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        addTeilnahmeLabel.setForeground(new java.awt.Color(240, 240, 240));
        addTeilnahmeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        addTeilnahmeLabel.setText("+");
        addTeilnahmeLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addTeilnahmeLabelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(addTeilnahmeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(addTeilnahmeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel3.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 40, 40, 30));

        kontakte.setBackground(new java.awt.Color(29, 30, 66));
        kontakte.setEditable(true);
        kontakte.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        kontakte.setForeground(new java.awt.Color(255, 255, 255));
        kontakte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kontakteActionPerformed(evt);
            }
        });
        jPanel3.add(kontakte, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 190, 30));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 70, 250, 230));

        jPanel4.setBackground(new java.awt.Color(46, 49, 117));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(240, 240, 240));
        jLabel6.setText("Notizen");
        jPanel4.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 122, 37));

        notiz.setEditable(false);
        notiz.setBackground(new java.awt.Color(29, 30, 66));
        notiz.setColumns(20);
        notiz.setForeground(new java.awt.Color(240, 240, 240));
        notiz.setRows(5);
        jScrollPane2.setViewportView(notiz);

        jPanel4.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 450, 100));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, 470, 140));

        jPanel5.setBackground(new java.awt.Color(46, 49, 117));
        jPanel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel5MouseClicked(evt);
            }
        });

        bearbeitenLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        bearbeitenLabel.setForeground(new java.awt.Color(240, 240, 240));
        bearbeitenLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        bearbeitenLabel.setText("bearbeiten");
        bearbeitenLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bearbeitenLabelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(bearbeitenLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(bearbeitenLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 470, 130, 30));

        jPanel6.setBackground(new java.awt.Color(46, 49, 117));
        jPanel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel6MouseClicked(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(240, 240, 240));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("loeschen");
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelLoeschenMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 2, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 470, -1, 30));

        jPanel7.setBackground(new java.awt.Color(46, 49, 117));
        jPanel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel7MouseClicked(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(240, 240, 240));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("abbrechen");
        jLabel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel9MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 470, -1, 30));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -4, 490, 520));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void bearbeitenLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bearbeitenLabelMouseClicked
        // TODO add your handling code here:
        bearbeitenLabel.setForeground(Color.gray);
        try {
            // TODO add your handling code here:
            TerminBearbeitenFenster tIB = new TerminBearbeitenFenster(terminID, stub, sitzungsID, hauptfenster);
            this.dispose();
            tIB.setVisible(true);
        } catch (RemoteException | BenutzerException | TerminException ex) {
            Logger.getLogger(TerminAnzeigenFenster.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }//GEN-LAST:event_bearbeitenLabelMouseClicked

    private void jPanel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel5MouseClicked

    }//GEN-LAST:event_jPanel5MouseClicked

    private void jPanel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel6MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel6MouseClicked

    private void jLabelLoeschenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelLoeschenMouseClicked
        try {
            // TODO add your handling code here:
            stub.removeTermin(terminID, sitzungsID);
        } catch (RemoteException | BenutzerException | TerminException | SQLException ex) {
            Logger.getLogger(TerminAnzeigenFenster.class.getName()).log(Level.SEVERE, null, ex);
        }
        hauptfenster.refresh();
        this.dispose();
    }//GEN-LAST:event_jLabelLoeschenMouseClicked

    private void jLabel9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel9MouseClicked
        // TODO add your handling code here:
        this.dispose();          
    }//GEN-LAST:event_jLabel9MouseClicked

    private void jPanel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel7MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel7MouseClicked

    private void addTeilnahmeLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addTeilnahmeLabelMouseClicked
        // TODO add your handling code here:
        addTeilnahmeLabel.setForeground(Color.gray);

        String username = kontakte.getEditor().getItem().toString();
        if (username.length() > 0) {
            try {
                stub.addTerminteilnehmer(terminID, username, sitzungsID);
                kontakte.getEditor().setItem(null);
                //damit man nichtmehr aktualisieren muss addElement
                teilnehmerListeModel.addElement(username + " (offen)");
            } catch (RemoteException | BenutzerException | SQLException | TerminException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Teilnehmer hinzufügen - Terminansicht", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_addTeilnahmeLabelMouseClicked

    private void kontakteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kontakteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_kontakteActionPerformed

   
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
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TerminAnzeigenFenster.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new TerminAnzeigenFenster().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel addTeilnahmeLabel;
    private javax.swing.JLabel bearbeitenLabel;
    private javax.swing.JLabel datum;
    private javax.swing.JLabel endZeit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JComboBox<String> kontakte;
    private javax.swing.JTextArea notiz;
    private javax.swing.JLabel ort;
    private javax.swing.JLabel start;
    private javax.swing.JLabel startZeit;
    private javax.swing.JList<String> teilnehmerliste;
    private javax.swing.JLabel terminersteller;
    private javax.swing.JLabel titel;
    // End of variables declaration//GEN-END:variables
}
