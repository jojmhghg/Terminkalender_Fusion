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
import javax.swing.JOptionPane;

/**
 *
 * @author niroshan
 */
public class TerminBearbeitenFenster extends javax.swing.JFrame {

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
    public TerminBearbeitenFenster(int terminID, ClientStub stub, int sitzungsID, Hauptfenster hauptfenster) throws RemoteException, BenutzerException, TerminException {
        initComponents();

        this.terminID = terminID;
        this.stub = stub;
        this.sitzungsID = sitzungsID;
        this.termin = stub.getTermin(terminID, sitzungsID);
          
        setColor();
        
        tag1.setSelectedItem(Integer.toString(termin.getDatum().getTag()));
        monat1.setSelectedItem(Integer.toString(termin.getDatum().getMonat()));
        jahr1.setSelectedItem(Integer.toString(termin.getDatum().getJahr()));

        
        Zeit uhrzeit = termin.getBeginn();

        stunde1.setSelectedItem(uhrzeit.getStundeAsString());
        minute1.setSelectedItem(uhrzeit.getMinuteAsString());
 
        uhrzeit = termin.getEnde();
        stunde2.setSelectedItem(uhrzeit.getStundeAsString());
        minute2.setSelectedItem(uhrzeit.getMinuteAsString());
        
        titel.setText(termin.getTitel());
        ortTextField.setText(termin.getOrt());
        ownerLabel.setText(termin.getOwner());      
        notiz.setText(termin.getNotiz());
        
        fillTeilnehmerlist();
        teilnehmerliste.setModel(teilnehmerListeModel);
        //Wenn jeder editieren möchte, wird bei dem Owner des Termins angezeigt ob er es wieder rückgängig machen will
        if(termin.getEditierbar()){
            everyoneCanEditLabel.setText("Darf keiner editieren");
        }
        //Wenn nicht Owner des Termins ist wird ihm die Möglichkeit genommen Termin editierbar zu machen
        if(!stub.getUsername(sitzungsID).equals(termin.getOwner())){
            everyoneCanEditLabel.setVisible(false);
            jPanel7.setVisible(false);
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
        dateLabel.setForeground(color4);
        startLabel.setForeground(color4);
        endLabel.setForeground(color4);
        ortLabel.setForeground(color4);
        ownerLabel.setForeground(color4);
        jLabel6.setForeground(color4);
        notiz.setForeground(color4);
        everyoneCanEditLabel.setForeground(color4);
        bearbeitenLabel.setForeground(color4);
        jLabel8.setForeground(color4);
        tag1.setForeground(color4);
        monat1.setForeground(color4);
        jahr1.setForeground(color4);
        stunde1.setForeground(color4);
        stunde2.setForeground(color4);
        minute1.setForeground(color4);
        minute2.setForeground(color4);
        jLabel4.setForeground(color4);
        teilnehmerliste.setForeground(color4);
        ortTextField.setForeground(color4);
        ownerValueLabel.setForeground(color4);
           
        
    }

    /**
     * Hilfsmethode um Teilnehmerliste zu füllen
     * 
     * @throws RemoteException
     * @throws BenutzerException
     * @throws TerminException 
     */
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

    private TerminBearbeitenFenster() {
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
        dateLabel = new javax.swing.JLabel();
        startLabel = new javax.swing.JLabel();
        endLabel = new javax.swing.JLabel();
        ortLabel = new javax.swing.JLabel();
        ownerLabel = new javax.swing.JLabel();
        ownerValueLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        titel = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        teilnehmerliste = new javax.swing.JList<>();
        jLabel4 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        notiz = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        bearbeitenLabel = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        tag1 = new javax.swing.JComboBox<>();
        monat1 = new javax.swing.JComboBox<>();
        jahr1 = new javax.swing.JComboBox<>();
        stunde1 = new javax.swing.JComboBox<>();
        minute1 = new javax.swing.JComboBox<>();
        stunde2 = new javax.swing.JComboBox<>();
        minute2 = new javax.swing.JComboBox<>();
        ortTextField = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel7 = new javax.swing.JPanel();
        everyoneCanEditLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(29, 30, 66));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dateLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        dateLabel.setForeground(new java.awt.Color(240, 240, 240));
        dateLabel.setText("Datum");
        jPanel1.add(dateLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 83, 28));

        startLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        startLabel.setForeground(new java.awt.Color(240, 240, 240));
        startLabel.setText("Start Zeit");
        jPanel1.add(startLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, 83, 35));

        endLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        endLabel.setForeground(new java.awt.Color(240, 240, 240));
        endLabel.setText("End Zeit");
        jPanel1.add(endLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 81, 27));

        ortLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        ortLabel.setForeground(new java.awt.Color(240, 240, 240));
        ortLabel.setText("Ort");
        jPanel1.add(ortLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, 90, 26));

        ownerLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        ownerLabel.setForeground(new java.awt.Color(240, 240, 240));
        ownerLabel.setText("Terminersteller");
        jPanel1.add(ownerLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 240, -1, 30));

        ownerValueLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        ownerValueLabel.setForeground(new java.awt.Color(240, 240, 240));
        ownerValueLabel.setText("Ersteller");
        jPanel1.add(ownerValueLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 240, 100, 27));

        jPanel2.setBackground(new java.awt.Color(46, 49, 117));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        titel.setBackground(new java.awt.Color(46, 49, 117));
        titel.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        titel.setForeground(new java.awt.Color(240, 240, 240));
        titel.setText("titel");
        titel.setBorder(null);
        jPanel2.add(titel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 420, 44));

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

        jPanel3.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 200, 180));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(240, 240, 240));
        jLabel4.setText("Teilnehmer");
        jPanel3.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 70, 220, 230));

        jPanel4.setBackground(new java.awt.Color(46, 49, 117));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(240, 240, 240));
        jLabel6.setText("Notizen");
        jPanel4.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 122, 37));

        notiz.setBackground(new java.awt.Color(29, 30, 66));
        notiz.setColumns(20);
        notiz.setForeground(new java.awt.Color(240, 240, 240));
        notiz.setRows(5);
        jScrollPane2.setViewportView(notiz);

        jPanel4.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 450, 90));

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
        bearbeitenLabel.setText("speichern");
        bearbeitenLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bearbeitenLabelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bearbeitenLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                .addContainerGap())
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
        jLabel8.setText("abbrechen");
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
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 470, -1, 30));

        tag1.setBackground(new java.awt.Color(46, 49, 117));
        tag1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tag1.setForeground(new java.awt.Color(240, 240, 240));
        tag1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));
        tag1.setSelectedItem(tag1);
        tag1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tag1ActionPerformed(evt);
            }
        });
        jPanel1.add(tag1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 90, -1, -1));

        monat1.setBackground(new java.awt.Color(46, 49, 117));
        monat1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        monat1.setForeground(new java.awt.Color(240, 240, 240));
        monat1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", " " }));
        jPanel1.add(monat1, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 90, -1, -1));

        jahr1.setBackground(new java.awt.Color(46, 49, 117));
        jahr1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jahr1.setForeground(new java.awt.Color(240, 240, 240));
        jahr1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1900", "1901", "1902", "1903", "1904", "1905", "1906", "1907", "1908", "1909", "1910", "1911", "1912", "1913", "1914", "1915", "1916", "1917", "1918", "1919", "1920", "1921", "1922", "1923", "1924", "1925", "1926", "1927", "1928", "1929", "1930", "1931", "1932", "1933", "1934", "1935", "1936", "1937", "1938", "1939", "1940", "1941", "1942", "1943", "1944", "1945", "1946", "1947", "1948", "1949", "1950", "1951", "1952", "1953", "1954", "1955", "1956", "1957", "1958", "1959", "1960", "1961", "1962", "1963", "1964", "1965", "1966", "1967", "1968", "1969", "1970", "1971", "1972", "1973", "1974", "1975", "1976", "1977", "1978", "1979", "1980", "1981", "1982", "1983", "1984", "1985", "1986", "1987", "1988", "1989", "1990", "1991", "1992", "1993", "1994", "1995", "1996", "1997", "1998", "1999", "2000", "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009", "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030", "2031", "2032", "2033", "2034", "2035", "2036", "2037", "2038", "2039", "2040", "2041", "2042", "2043", "2044", "2045", "2046", "2047", "2048", "2049", "2050", "2051", "2052", "2053", "2054", "2055", "2056", "2057", "2058", "2059", "2060", "2061", "2062", "2063", "2064", "2065", "2066", "2067", "2068", "2069", "2070", "2071", "2072", "2073", "2074", "2075", "2076", "2077", "2078", "2079", "2080", "2081", "2082", "2083", "2084", "2085", "2086", "2087", "2088", "2089", "2090", "2091", "2092", "2093", "2094", "2095", "2096", "2097", "2098", "2099", "2100" }));
        jPanel1.add(jahr1, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 90, -1, -1));

        stunde1.setBackground(new java.awt.Color(46, 49, 117));
        stunde1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        stunde1.setForeground(new java.awt.Color(240, 240, 240));
        stunde1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", " " }));
        jPanel1.add(stunde1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 130, -1, -1));

        minute1.setBackground(new java.awt.Color(46, 49, 117));
        minute1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        minute1.setForeground(new java.awt.Color(240, 240, 240));
        minute1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60" }));
        jPanel1.add(minute1, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 130, -1, -1));

        stunde2.setBackground(new java.awt.Color(46, 49, 117));
        stunde2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        stunde2.setForeground(new java.awt.Color(240, 240, 240));
        stunde2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", " " }));
        jPanel1.add(stunde2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 170, -1, -1));

        minute2.setBackground(new java.awt.Color(46, 49, 117));
        minute2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        minute2.setForeground(new java.awt.Color(240, 240, 240));
        minute2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60" }));
        minute2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minute2ActionPerformed(evt);
            }
        });
        jPanel1.add(minute2, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 170, -1, -1));

        ortTextField.setBackground(new java.awt.Color(29, 30, 66));
        ortTextField.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        ortTextField.setForeground(new java.awt.Color(240, 240, 240));
        ortTextField.setText("jTextField1");
        ortTextField.setBorder(null);
        ortTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ortTextFieldActionPerformed(evt);
            }
        });
        jPanel1.add(ortTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 210, 140, -1));
        jPanel1.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 230, 140, 10));

        jPanel7.setBackground(new java.awt.Color(46, 49, 117));
        jPanel7.setPreferredSize(new java.awt.Dimension(142, 28));
        jPanel7.setVerifyInputWhenFocusTarget(false);

        everyoneCanEditLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        everyoneCanEditLabel.setForeground(new java.awt.Color(240, 240, 240));
        everyoneCanEditLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        everyoneCanEditLabel.setText("Darf jeder editieren");
        everyoneCanEditLabel.setMaximumSize(new java.awt.Dimension(58, 15));
        everyoneCanEditLabel.setMinimumSize(new java.awt.Dimension(58, 15));
        everyoneCanEditLabel.setPreferredSize(new java.awt.Dimension(58, 15));
        everyoneCanEditLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                everyoneCanEditLabelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(everyoneCanEditLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(everyoneCanEditLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 2, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 470, 130, 30));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -4, 490, 520));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Methode speichert die Änderungen und geht zum vorherigen Fenster zurück
     * 
     * @param evt 
     */
    private void bearbeitenLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bearbeitenLabelMouseClicked
        try {
            int tag = Integer.parseInt((String) tag1.getSelectedItem());
            int monat = Integer.parseInt((String) monat1.getSelectedItem());
            int jahr = Integer.parseInt((String) jahr1.getSelectedItem());
            
            int startminute = Integer.parseInt((String) minute1.getSelectedItem()) ;
            int endminute = Integer.parseInt((String) minute2.getSelectedItem()) ;
            int startstunde = Integer.parseInt((String) stunde1.getSelectedItem()) ;
            int endstunde = Integer.parseInt((String) stunde2.getSelectedItem()) ;
            
            String username = stub.getUsername(sitzungsID);
            
            try{
                termin.setBeginn(new Zeit(startstunde, startminute), username); 
                termin.setEnde(new Zeit(endstunde, endminute), username);
            }
            catch(TerminException ex){
                termin.setEnde(new Zeit(endstunde, endminute), username);
                termin.setBeginn(new Zeit(startstunde, startminute), username);
            }
            
            termin.setDatum(new Datum(tag, monat, jahr), username);
            termin.setTitel(titel.getText(), username);
            termin.setOrt(ortTextField.getText(), username);
            termin.setNotiz(notiz.getText(), username);
            
            stub.changeTermin(termin, sitzungsID);

            this.dispose();
            TerminAnzeigenFenster startagain = new TerminAnzeigenFenster(terminID, stub, sitzungsID, hauptfenster);
            startagain.setVisible(true);
        } catch (BenutzerException | RemoteException | TerminException | SQLException | Datum.DatumException | Zeit.ZeitException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Termin bearbeiten", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_bearbeitenLabelMouseClicked

    private void jPanel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel5MouseClicked

    }//GEN-LAST:event_jPanel5MouseClicked

    private void jPanel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel6MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel6MouseClicked

    /**
     * Methode lässt bearbeitung abbrechen. d.h. man kehrt zum vorherigen Fenster
     * zurück ohne änderungen zu speichern
     * 
     * @param evt 
     */
    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
        try {
            this.dispose();
            TerminAnzeigenFenster startagain;
            startagain = new TerminAnzeigenFenster(terminID, stub, sitzungsID, hauptfenster);
            startagain.setVisible(true);
        } catch (RemoteException | BenutzerException | TerminException ex) {
            Logger.getLogger(TerminBearbeitenFenster.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_jLabel8MouseClicked

    private void tag1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tag1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tag1ActionPerformed

    private void minute2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minute2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_minute2ActionPerformed

    private void ortTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ortTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ortTextFieldActionPerformed

    private void everyoneCanEditLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_everyoneCanEditLabelMouseClicked
        try {
            // TODO add your handling code here:
            String username = stub.getUsername(sitzungsID);
            //Wenn editierbar ist setze auf nicht editierbar
            if(termin.getEditierbar()){
                termin.setEditierbar(false, username);
            }
            //Wenn nicht editierbar ist setze auf editierbar
            else{
                termin.setEditierbar(true, username);
            }
            stub.changeEditierrechte(termin, sitzungsID);
            this.dispose();
        } catch (BenutzerException | RemoteException | TerminException | SQLException ex) {
            Logger.getLogger(TerminBearbeitenFenster.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_everyoneCanEditLabelMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bearbeitenLabel;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JLabel endLabel;
    private javax.swing.JLabel everyoneCanEditLabel;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JComboBox<String> jahr1;
    private javax.swing.JComboBox<String> minute1;
    private javax.swing.JComboBox<String> minute2;
    private javax.swing.JComboBox<String> monat1;
    private javax.swing.JTextArea notiz;
    private javax.swing.JLabel ortLabel;
    private javax.swing.JTextField ortTextField;
    private javax.swing.JLabel ownerLabel;
    private javax.swing.JLabel ownerValueLabel;
    private javax.swing.JLabel startLabel;
    private javax.swing.JComboBox<String> stunde1;
    private javax.swing.JComboBox<String> stunde2;
    private javax.swing.JComboBox<String> tag1;
    private javax.swing.JList<String> teilnehmerliste;
    private javax.swing.JTextField titel;
    // End of variables declaration//GEN-END:variables
}
