/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientGUI;

import Server.ClientStub;
import Utilities.BenutzerException;
import Utilities.Datum;
import Utilities.Termin;
import Utilities.TerminException;
import Utilities.Zeit;
import java.awt.Color;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JOptionPane;

/**
 *
 * @author niroshan
 */
public class TerminAnlegenFenster extends javax.swing.JFrame {

    private final ClientStub stub;
    private int sitzungsID;
    private Hauptfenster hf;
    Datum datum;
    Zeit start;
    Zeit ende;

    String titelS;
    int tag1S = 0, tag2S = 0, minute1S = 0, minute2S = 0, monat1S = 0, monat2S = 0, stunde1S = 0, stunde2S = 0, jahr1S = 0;

    /**
     * Creates new form TerminAnlegen
     *
     * @param stub
     * @param sitzungsID
     * @param hf
     */
    public TerminAnlegenFenster(ClientStub stub, int sitzungsID, Hauptfenster hf) {
        initComponents();
        this.stub = stub;
        this.sitzungsID = sitzungsID;
        this.hf = hf;
        
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        tag1.setSelectedItem(timeStamp.substring(6, 8));
        monat1.setSelectedItem(timeStamp.substring(4, 6));
        jahr1.setSelectedItem(timeStamp.substring(0, 4));

        stunde1.setSelectedItem(timeStamp.substring(9, 11));
        minute1.setSelectedItem(timeStamp.substring(11, 13));
        
        stunde2.setSelectedItem(timeStamp.substring(9, 11));
        minute2.setSelectedItem(timeStamp.substring(11, 13));
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
        minute2 = new javax.swing.JComboBox<>();
        tag1 = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        beschreibung = new javax.swing.JTextArea();
        jahr1 = new javax.swing.JComboBox<>();
        monat1 = new javax.swing.JComboBox<>();
        stunde1 = new javax.swing.JComboBox<>();
        stunde2 = new javax.swing.JComboBox<>();
        minute1 = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        titel = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        speichernLabel = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Termin anlegen - Termin Kalender");
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(29, 30, 66));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        minute2.setBackground(new java.awt.Color(46, 49, 117));
        minute2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        minute2.setForeground(new java.awt.Color(240, 240, 240));
        minute2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60" }));
        minute2.setBorder(null);
        minute2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minute2ActionPerformed(evt);
            }
        });
        jPanel1.add(minute2, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 170, -1, -1));

        tag1.setBackground(new java.awt.Color(46, 49, 117));
        tag1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        tag1.setForeground(new java.awt.Color(240, 240, 240));
        tag1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));
        tag1.setBorder(null);
        tag1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tag1ActionPerformed(evt);
            }
        });
        jPanel1.add(tag1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 90, -1, -1));

        beschreibung.setBackground(new java.awt.Color(29, 30, 66));
        beschreibung.setColumns(20);
        beschreibung.setForeground(new java.awt.Color(240, 240, 240));
        beschreibung.setRows(5);
        jScrollPane1.setViewportView(beschreibung);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 240, 370, -1));

        jahr1.setBackground(new java.awt.Color(46, 49, 117));
        jahr1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jahr1.setForeground(new java.awt.Color(240, 240, 240));
        jahr1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2017", "2018", "2019", "2020", "2021", "2022", " " }));
        jahr1.setBorder(null);
        jPanel1.add(jahr1, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 90, -1, -1));

        monat1.setBackground(new java.awt.Color(46, 49, 117));
        monat1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        monat1.setForeground(new java.awt.Color(240, 240, 240));
        monat1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", " " }));
        monat1.setBorder(null);
        jPanel1.add(monat1, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 90, -1, -1));

        stunde1.setBackground(new java.awt.Color(46, 49, 117));
        stunde1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        stunde1.setForeground(new java.awt.Color(240, 240, 240));
        stunde1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", " " }));
        stunde1.setBorder(null);
        jPanel1.add(stunde1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 130, -1, -1));

        stunde2.setBackground(new java.awt.Color(46, 49, 117));
        stunde2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        stunde2.setForeground(new java.awt.Color(240, 240, 240));
        stunde2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", " " }));
        stunde2.setBorder(null);
        jPanel1.add(stunde2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 170, -1, -1));

        minute1.setBackground(new java.awt.Color(46, 49, 117));
        minute1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        minute1.setForeground(new java.awt.Color(240, 240, 240));
        minute1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60" }));
        minute1.setBorder(null);
        jPanel1.add(minute1, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 130, -1, -1));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(240, 240, 240));
        jLabel2.setText("Datum");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, -1, -1));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(240, 240, 240));
        jLabel3.setText("Beginn");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 130, -1, -1));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(240, 240, 240));
        jLabel4.setText("Ende");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 170, -1, -1));

        jPanel2.setBackground(new java.awt.Color(46, 49, 117));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        titel.setBackground(new java.awt.Color(46, 49, 117));
        titel.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        titel.setForeground(new java.awt.Color(240, 240, 240));
        titel.setText("Titel");
        titel.setBorder(null);
        titel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                titelActionPerformed(evt);
            }
        });
        jPanel2.add(titel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 370, 40));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, 390, 60));

        jPanel3.setBackground(new java.awt.Color(46, 49, 117));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(240, 240, 240));
        jLabel5.setText("Notiz");
        jPanel3.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, 390, 140));

        jPanel5.setBackground(new java.awt.Color(46, 49, 117));
        jPanel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel5MouseClicked(evt);
            }
        });

        speichernLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        speichernLabel.setForeground(new java.awt.Color(240, 240, 240));
        speichernLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        speichernLabel.setText("Speichern");
        speichernLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                speichernLabelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(speichernLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 2, Short.MAX_VALUE)
                .addComponent(speichernLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 370, 130, 30));

        jPanel6.setBackground(new java.awt.Color(46, 49, 117));
        jPanel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel6MouseClicked(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(240, 240, 240));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Abbrechen");
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 2, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 370, 120, 30));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 4, 430, 420));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void tag1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tag1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tag1ActionPerformed

    private void minute2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minute2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_minute2ActionPerformed

    private void titelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_titelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_titelActionPerformed

    private void speichernLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_speichernLabelMouseClicked
        // TODO add your handling code here:
        speichernLabel.setForeground(Color.gray);
        tag1S = Integer.valueOf((String) tag1.getSelectedItem());
        minute1S = Integer.valueOf((String) minute1.getSelectedItem());
        minute2S = Integer.valueOf((String) minute2.getSelectedItem());
        stunde1S = Integer.valueOf((String) stunde1.getSelectedItem());
        stunde2S = Integer.valueOf((String) stunde2.getSelectedItem());
        monat1S = Integer.valueOf((String) monat1.getSelectedItem());
        jahr1S = Integer.valueOf((String) jahr1.getSelectedItem());
        titelS = (String) titel.getText();

        try {
            datum = new Datum(tag1S, monat1S, jahr1S);
            start = new Zeit(stunde1S, minute1S);
            ende = new Zeit(stunde2S, minute2S);
            
            stub.addTermin(datum, start, ende, titelS, sitzungsID);
            if(beschreibung.getText() != null){
                Termin mitNotiz = stub.getTermineAmTag(datum, sitzungsID).getLast();
                mitNotiz.setNotiz(beschreibung.getText(), stub.getUsername(sitzungsID));
                stub.changeTermin(mitNotiz, sitzungsID);
            }   
            
            dispose();
            JOptionPane.showMessageDialog(null, "Termin erfolgreich angelegt ! ", "Termin anlegen", JOptionPane.INFORMATION_MESSAGE);
            hf.displayDate();
        } catch (Datum.DatumException | Zeit.ZeitException | RemoteException | TerminException | BenutzerException | SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Termin anlegen", JOptionPane.ERROR_MESSAGE);
        }           
    }//GEN-LAST:event_speichernLabelMouseClicked

    private void jPanel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel5MouseClicked

    }//GEN-LAST:event_jPanel5MouseClicked

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jLabel8MouseClicked

    private void jPanel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel6MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel6MouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea beschreibung;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> jahr1;
    private javax.swing.JComboBox<String> minute1;
    private javax.swing.JComboBox<String> minute2;
    private javax.swing.JComboBox<String> monat1;
    private javax.swing.JLabel speichernLabel;
    private javax.swing.JComboBox<String> stunde1;
    private javax.swing.JComboBox<String> stunde2;
    private javax.swing.JComboBox<String> tag1;
    private javax.swing.JTextField titel;
    // End of variables declaration//GEN-END:variables
}
