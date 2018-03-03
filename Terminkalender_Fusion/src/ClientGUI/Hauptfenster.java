
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientGUI;

import Server.ClientStub;
import Server.ClientStubImpl;
import Utilities.Anfrage;
import Utilities.BenutzerException;
import Utilities.Datum;
import Utilities.Meldung;
import Utilities.Termin;
import Utilities.TerminException;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author niroshan
 */
public class Hauptfenster extends javax.swing.JFrame implements ListSelectionListener {

    private final ClientStub stub;
    private int sitzungsID;
    //private DefaultListModel listModel;
    DefaultListModel listModel = new DefaultListModel();
    DefaultListModel termineListeModel = new DefaultListModel();
    //DefaultListModel event = new DefaultListModel();
    LoginFenster fenster;
    DefaultListModel meldungModel = new DefaultListModel();
    LinkedList<Termin> dieserMonat;
    private int meldungssize ;

    //Niros globale Variablen
    LocalDate ld = LocalDate.now();
    int month = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH);
    int year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
    JLabel l = new JLabel("", JLabel.CENTER);
    String day = "";
    JPanel d;
    JButton[] button = new JButton[42];
    int[] tagBekommen = new int[42];
    int daySelector;
    Timer timer;

    /**
     * Creates new form HauptFenster
     *
     * @param stub
     * @param sitzungsID
     * @param fenster
     */
    public Hauptfenster(ClientStub stub, int sitzungsID, LoginFenster fenster) {
        initComponents();

        this.stub = stub;
        this.sitzungsID = sitzungsID;
        this.fenster = fenster;
        this.meldungssize = 0;
        
        jList1.setModel(listModel);
        termineListe.setModel(termineListeModel);
        daySelector = 0;
        initKalender();
        try {
            gruess();
        } catch (RemoteException | BenutzerException ex) {
            Logger.getLogger(Hauptfenster.class.getName()).log(Level.SEVERE, null, ex);
        }
              
        ActionListener taskPerformer = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                refresh();
            }
        };
        timer = new Timer(1000, taskPerformer);
        timer.start();
        
    }

    private void initKalender() {
        /*String tag = day;
        int count = 1;
        String tay2 = new String();
        for (int i = 0; i <= 41; i++){
            int count2 = count + i;
            String cast = String.valueOf(count2);
            tay2 = tag + cast;
            JButton jbatton = (JButton) tay2;
            button[i] = jbatton;
        }*/
        button[0] = day1;
        button[1] = day2;
        button[2] = day3;
        button[3] = day4;
        button[4] = day5;
        button[5] = day6;
        button[6] = day7;
        button[7] = day8;
        button[8] = day9;
        button[9] = day10;
        button[10] = day11;
        button[11] = day12;
        button[12] = day13;
        button[13] = day14;
        button[14] = day15;
        button[15] = day16;
        button[16] = day17;
        button[17] = day18;
        button[18] = day19;
        button[19] = day20;
        button[20] = day21;
        button[21] = day22;
        button[22] = day23;
        button[23] = day24;
        button[24] = day25;
        button[25] = day26;
        button[26] = day27;
        button[27] = day28;
        button[28] = day29;
        button[29] = day30;
        button[30] = day31;
        button[31] = day32;
        button[32] = day33;
        button[33] = day34;
        button[34] = day35;
        button[35] = day36;
        button[36] = day37;
        button[37] = day38;
        button[38] = day39;
        button[39] = day40;
        button[40] = day41;
        button[41] = day42;

        for (int x = 0; x < 42; x++) {
            final int selection = x;

            button[x].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    try {
                        termineListeModel.clear();
                        daySelector = selection;
                        day = button[selection].getActionCommand();

                        int monat1 = month + 1;
                        zeigeTerminInhalt(tagBekommen[selection], monat1, year);
                        //CalenderInhalt start = new CalenderInhalt();
                        //start.setVisible(true);
                    } catch (RemoteException | TerminException | BenutzerException ex) {
                        Logger.getLogger(Hauptfenster.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        }

        displayDate();

    }

    private void gruess() throws RemoteException, BenutzerException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        zumProfil.setText(stub.getUsername(sitzungsID));
        
        int zeitJetzt = Integer.parseInt(timeStamp.substring(9, 11));
        //JOptionPane.showMessageDialog(null, "Guten Morgen!, " + stub.getVorname(sitzungsID), "InfoBox: day", JOptionPane.INFORMATION_MESSAGE);
        if (zeitJetzt > 6 || zeitJetzt < 12) {
            eventMessage.setText("Guten Morgen, " + stub.getUsername(sitzungsID));
        }
        
        if (zeitJetzt > 12 || zeitJetzt < 18) {
            eventMessage.setText("Guten Tag, " + stub.getUsername(sitzungsID));
        }
        
        if (zeitJetzt > 18 || zeitJetzt < 6) {
            eventMessage.setText("Guten Abend, " + stub.getUsername(sitzungsID));
        }
        
    }
    
    public void zeigeTerminInhalt(int day, int monat, int jahr) throws RemoteException, TerminException, BenutzerException {

        //int tag = Integer.parseInt(day);
        //JOptionPane.showMessageDialog(null, day, "InfoBox: day", JOptionPane.INFORMATION_MESSAGE);
        //JOptionPane.showMessageDialog(null, month, "InfoBox: day", JOptionPane.INFORMATION_MESSAGE);
        //JOptionPane.showMessageDialog(null, year, "InfoBox: day", JOptionPane.INFORMATION_MESSAGE);
        dieserMonat = stub.getTermineInMonat(monat, jahr, sitzungsID);
        //titelNachricht.setText(dieserMonat.size() + " Termine im Jahr " + jahr + " im Monat " + monat);

        //for (int i = 0; i < 10; i++) {
        //    legen.addElement(i);
        //}
        termineListe.setModel(termineListeModel);

        StringBuilder sb = new StringBuilder();
        StringBuilder cl = new StringBuilder();
        int i = 0;

        gruess();

        for (Termin termin : dieserMonat) {
            //String tag = termin.getDatum().toString().substring(0, 1);

            cl.append(day);
            cl.append(".");
            cl.append(monat);
            cl.append(".");
            cl.append(jahr);

            String calenderDate = cl.toString();
            //JOptionPane.showMessageDialog(null, calenderDate, "InfoBox: stub1", JOptionPane.INFORMATION_MESSAGE);

            String tuiDate = termin.getDatum().toString();
            //JOptionPane.showMessageDialog(null, tuiDate, "InfoBox: 2 Datum", JOptionPane.INFORMATION_MESSAGE);

            if (calenderDate.equals(tuiDate)) {
                termineListeModel.addElement(termin.getTitel() + " um " + termin.getBeginn().toString());
                i++;

                if (i == 1) {
                    eventMessage.setText("Sie haben " + i + " Ereignis am " + tuiDate);
                }

                if (i > 1) {
                    eventMessage.setText("Sie haben " + i + " Ereignisse am " + tuiDate);
                }

            }
            cl.setLength(0);
        }
    }

    public void displayDate() {
        for (int x = 0; x < 42; x++)//for loop
        {
            button[x].setText("");//set text
        }
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMMM YYYY");
        java.util.Calendar cal = java.util.Calendar.getInstance();

        cal.set(year, month, 1);

        int dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
        int daysInMonth = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);

        StringBuilder sb = new StringBuilder();
        StringBuilder cl = new StringBuilder();

        LinkedList<Termin> dieserMonatlokal;

        int monat = month + 1;

        try {
            dieserMonatlokal = stub.getTermineInMonat(monat, year, sitzungsID);

            int i = 1;
            //String zusammen = " ";

            //condition
            for (int x = dayOfWeek - 1, tag = 1; tag <= daysInMonth; x++, tag++) //set text
            {

                for (Termin termin : dieserMonatlokal) {
                    cl.append(tag);
                    cl.append(".");
                    cl.append(monat);
                    cl.append(".");
                    cl.append(year);

                    String calenderDate = cl.toString();
                    String tuiDate = termin.getDatum().toString();

                    if (calenderDate.equals(tuiDate)) {
                        String titel = termin.getTitel();
                        String[] parts = titel.split(" ");
                        String part1 = parts[0]; // 004
                        String cutString = part1;

                        int length = part1.length();

                        if (length > 10) {
                            cutString = part1.substring(0, 8) + "...";
                        }

                        sb.append(cutString);
                        sb.append("\n");
                    }
                    cl.setLength(0);
                    i++;
                }

                String getSt = sb.toString();
                String twooLines = tag + "\n" + getSt;
                button[x].setForeground(Color.white);

                if (ld.getDayOfMonth() == tag && monat == ld.getMonthValue() && year == ld.getYear()) {
                    button[x].setForeground(Color.red);
                    button[x].setBackground(new Color(29, 30, 66));
                }

                tagBekommen[x] = tag;

                button[x].setText("<html>" + twooLines.replaceAll("\\n", "<br>") + "</html>");
                sb.setLength(0);
            }

            dateLabel.setText(sdf.format(cal.getTime()));

            //set title
            //d.setTitle("Date Picker");
        } catch (RemoteException | TerminException | BenutzerException ex) {
        }
    }

    public String setPickedDate() {
        //if condition
        if (day.equals("")) {
            return day;
        }
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(year, month, Integer.parseInt(day));
        return sdf.format(cal.getTime());
    }

    /*private void zeigeTerminInhalt() throws RemoteException, TerminException, BenutzerException {
        int i = 0;
        Datum datum = null;
        try {
            for (Termin terminListe : stub.getTermineAmTag(datum, sitzungsID)) {
                i++;
                //termineListe.addElement(termineListeModel);
                termineListeModel.addElement(terminListe);
                
            }
        } catch (BenutzerException | RemoteException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Hauptfenster", JOptionPane.ERROR_MESSAGE);
        }
    }*/
    /**
     * Standart Konstrucktor
     */
    public Hauptfenster() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @author Edwrard Nana
     */
    public class MyListModel extends AbstractListModel {

        private final LinkedList<String> list;

        public MyListModel(LinkedList<String> list) {
            this.list = list;
        }

        @Override
        public Object getElementAt(int index) {
            return list.get(index);
        }

        @Override
        public int getSize() {
            return list.size();
        }

    }

    DefaultListModel event = new DefaultListModel();

    public void AddEvent(String eventname) {
        benachList.setModel(event);
        event.addElement(eventname);
    }

    public void loechEvent(String eventname) {
        benachList.setModel(event);
        event.removeElement(eventname);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        contactUsernameField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jLabel8 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        showAddKontakt = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        showRemoveKontakt = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        benachList = new javax.swing.JList<>();
        jLabel11 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        calendarPanel = new javax.swing.JPanel();
        headerPanel = new javax.swing.JPanel();
        dateLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        dienstagLabel = new javax.swing.JLabel();
        donnerstagLabel = new javax.swing.JLabel();
        mittwochLabel = new javax.swing.JLabel();
        montagLabel = new javax.swing.JLabel();
        samstagLabel = new javax.swing.JLabel();
        sonntagLabel = new javax.swing.JLabel();
        freitagLabel = new javax.swing.JLabel();
        day8 = new javax.swing.JButton();
        day3 = new javax.swing.JButton();
        day4 = new javax.swing.JButton();
        day5 = new javax.swing.JButton();
        day6 = new javax.swing.JButton();
        day2 = new javax.swing.JButton();
        day7 = new javax.swing.JButton();
        day1 = new javax.swing.JButton();
        day15 = new javax.swing.JButton();
        day29 = new javax.swing.JButton();
        day39 = new javax.swing.JButton();
        day22 = new javax.swing.JButton();
        day30 = new javax.swing.JButton();
        day23 = new javax.swing.JButton();
        day16 = new javax.swing.JButton();
        day9 = new javax.swing.JButton();
        day36 = new javax.swing.JButton();
        day37 = new javax.swing.JButton();
        day10 = new javax.swing.JButton();
        day17 = new javax.swing.JButton();
        day24 = new javax.swing.JButton();
        day31 = new javax.swing.JButton();
        day38 = new javax.swing.JButton();
        day11 = new javax.swing.JButton();
        day18 = new javax.swing.JButton();
        day25 = new javax.swing.JButton();
        day32 = new javax.swing.JButton();
        day40 = new javax.swing.JButton();
        day12 = new javax.swing.JButton();
        day19 = new javax.swing.JButton();
        day13 = new javax.swing.JButton();
        day26 = new javax.swing.JButton();
        day33 = new javax.swing.JButton();
        day34 = new javax.swing.JButton();
        day20 = new javax.swing.JButton();
        day41 = new javax.swing.JButton();
        day42 = new javax.swing.JButton();
        day27 = new javax.swing.JButton();
        day14 = new javax.swing.JButton();
        day21 = new javax.swing.JButton();
        day28 = new javax.swing.JButton();
        day35 = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        termineListe = new javax.swing.JList<>();
        jLabel7 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        zumProfil = new javax.swing.JLabel();
        eventMessage = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Termin Kalender");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);
        setSize(new java.awt.Dimension(800, 600));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        mainPanel.setBackground(new java.awt.Color(21, 22, 48));
        mainPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel6.setBackground(new java.awt.Color(29, 30, 66));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(46, 49, 117));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        contactUsernameField.setBackground(new java.awt.Color(29, 30, 66));
        contactUsernameField.setForeground(new java.awt.Color(240, 240, 240));
        contactUsernameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contactUsernameFieldActionPerformed(evt);
            }
        });
        jPanel2.add(contactUsernameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 120, 20));

        jScrollPane1.setBackground(new java.awt.Color(29, 30, 66));
        jScrollPane1.setBorder(null);

        jList1.setBackground(new java.awt.Color(29, 30, 66));
        jList1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jList1.setForeground(new java.awt.Color(240, 240, 240));
        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jList1.setToolTipText("");
        jList1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList1MouseClicked(evt);
            }
        });
        jList1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jList1ComponentShown(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 210, 110));

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(240, 240, 240));
        jLabel8.setText("Kontaktliste");
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, -1));

        jPanel8.setBackground(new java.awt.Color(29, 30, 66));
        jPanel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel8MouseClicked(evt);
            }
        });

        showAddKontakt.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        showAddKontakt.setForeground(new java.awt.Color(240, 240, 240));
        showAddKontakt.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        showAddKontakt.setText("Hinzufügen");
        showAddKontakt.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showAddKontaktMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                showAddKontaktMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(showAddKontakt, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(showAddKontakt, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel2.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 30, 80, -1));

        jPanel9.setBackground(new java.awt.Color(29, 30, 66));
        jPanel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel9MouseClicked(evt);
            }
        });

        showRemoveKontakt.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        showRemoveKontakt.setForeground(new java.awt.Color(240, 240, 240));
        showRemoveKontakt.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        showRemoveKontakt.setText("Entfernen");
        showRemoveKontakt.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showRemoveKontaktMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                showRemoveKontaktMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(showRemoveKontakt, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(showRemoveKontakt, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel2.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 190, -1, -1));

        jPanel6.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 120, 230, 231));

        jPanel1.setBackground(new java.awt.Color(46, 49, 117));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        benachList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = {};
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        benachList.setBackground(new java.awt.Color(29, 30, 66));
        benachList.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        benachList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                benachListMouseClicked(evt);
            }
        });
        benachList.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                benachListComponentShown(evt);
            }
        });
        jScrollPane2.setViewportView(benachList);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 210, 140));

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(240, 240, 240));
        jLabel11.setText("Benachrichtigungen");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, -1));

        jPanel6.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 370, 230, 190));

        jPanel7.setBackground(new java.awt.Color(46, 49, 117));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(240, 240, 240));
        jLabel2.setText("Termin");
        jPanel7.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(29, 30, 66));
        jLabel1.setText("Kalender");
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });
        jPanel7.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 30, 150, -1));

        jPanel6.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, 200, 100));

        mainPanel.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 240, 580));

        calendarPanel.setBackground(new java.awt.Color(29, 30, 66));
        calendarPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        headerPanel.setBackground(new java.awt.Color(46, 49, 117));
        headerPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dateLabel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        dateLabel.setForeground(new java.awt.Color(240, 240, 240));
        dateLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dateLabel.setText("Datum:");
        headerPanel.add(dateLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 210, -1));

        calendarPanel.add(headerPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 0, 330, 40));

        jPanel3.setBackground(new java.awt.Color(21, 22, 48));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dienstagLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        dienstagLabel.setForeground(new java.awt.Color(240, 240, 240));
        dienstagLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dienstagLabel.setText("Mo");
        jPanel3.add(dienstagLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 0, 79, 30));

        donnerstagLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        donnerstagLabel.setForeground(new java.awt.Color(240, 240, 240));
        donnerstagLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        donnerstagLabel.setText("Mi");
        jPanel3.add(donnerstagLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 0, 85, 30));

        mittwochLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        mittwochLabel.setForeground(new java.awt.Color(240, 240, 240));
        mittwochLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mittwochLabel.setText("Di");
        jPanel3.add(mittwochLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 0, 85, 30));

        montagLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        montagLabel.setForeground(new java.awt.Color(240, 240, 240));
        montagLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        montagLabel.setText("So");
        montagLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel3.add(montagLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 82, 30));

        samstagLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        samstagLabel.setForeground(new java.awt.Color(240, 240, 240));
        samstagLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        samstagLabel.setText("Fr");
        jPanel3.add(samstagLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 0, 83, 30));

        sonntagLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        sonntagLabel.setForeground(new java.awt.Color(240, 240, 240));
        sonntagLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sonntagLabel.setText("Sa");
        jPanel3.add(sonntagLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 0, 97, 30));

        freitagLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        freitagLabel.setForeground(new java.awt.Color(240, 240, 240));
        freitagLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        freitagLabel.setText("Do");
        jPanel3.add(freitagLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 0, 85, 30));

        day8.setBackground(new java.awt.Color(46, 49, 117));
        day8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day8.setForeground(new java.awt.Color(240, 240, 240));
        day8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day8.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day8.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day8.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 100, 100, 70));

        day3.setBackground(new java.awt.Color(46, 49, 117));
        day3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day3.setForeground(new java.awt.Color(240, 240, 240));
        day3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day3.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day3.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day3.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day3, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 30, 100, 70));

        day4.setBackground(new java.awt.Color(46, 49, 117));
        day4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day4.setForeground(new java.awt.Color(240, 240, 240));
        day4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day4.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day4.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day4.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day4, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 30, 100, 70));

        day5.setBackground(new java.awt.Color(46, 49, 117));
        day5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day5.setForeground(new java.awt.Color(240, 240, 240));
        day5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day5.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day5.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day5.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        day5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                day5ActionPerformed(evt);
            }
        });
        jPanel3.add(day5, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 30, 100, 70));

        day6.setBackground(new java.awt.Color(46, 49, 117));
        day6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day6.setForeground(new java.awt.Color(240, 240, 240));
        day6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day6.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day6.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day6.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        day6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                day6ActionPerformed(evt);
            }
        });
        jPanel3.add(day6, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 30, 100, 70));

        day2.setBackground(new java.awt.Color(46, 49, 117));
        day2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day2.setForeground(new java.awt.Color(240, 240, 240));
        day2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day2.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day2.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 30, 100, 70));

        day7.setBackground(new java.awt.Color(46, 49, 117));
        day7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day7.setForeground(new java.awt.Color(240, 240, 240));
        day7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day7.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day7.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day7.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day7, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 30, 100, 70));

        day1.setBackground(new java.awt.Color(46, 49, 117));
        day1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day1.setForeground(new java.awt.Color(240, 240, 240));
        day1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day1.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 100, 70));

        day15.setBackground(new java.awt.Color(46, 49, 117));
        day15.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day15.setForeground(new java.awt.Color(240, 240, 240));
        day15.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day15.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day15.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day15.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day15, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 170, 100, 70));

        day29.setBackground(new java.awt.Color(46, 49, 117));
        day29.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day29.setForeground(new java.awt.Color(240, 240, 240));
        day29.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day29.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day29.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day29.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day29.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day29, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 310, 100, 70));

        day39.setBackground(new java.awt.Color(46, 49, 117));
        day39.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day39.setForeground(new java.awt.Color(240, 240, 240));
        day39.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day39.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day39.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day39.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day39.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day39, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 380, 100, 70));

        day22.setBackground(new java.awt.Color(46, 49, 117));
        day22.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day22.setForeground(new java.awt.Color(240, 240, 240));
        day22.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day22.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day22.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day22.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day22.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day22, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 240, 100, 70));

        day30.setBackground(new java.awt.Color(46, 49, 117));
        day30.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day30.setForeground(new java.awt.Color(240, 240, 240));
        day30.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day30.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day30.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day30.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day30.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day30, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 310, 100, 70));

        day23.setBackground(new java.awt.Color(46, 49, 117));
        day23.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day23.setForeground(new java.awt.Color(240, 240, 240));
        day23.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day23.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day23.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day23.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day23.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day23, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 240, 100, 70));

        day16.setBackground(new java.awt.Color(46, 49, 117));
        day16.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day16.setForeground(new java.awt.Color(240, 240, 240));
        day16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day16.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day16.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day16.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        day16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                day16ActionPerformed(evt);
            }
        });
        jPanel3.add(day16, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 170, 100, 70));

        day9.setBackground(new java.awt.Color(46, 49, 117));
        day9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day9.setForeground(new java.awt.Color(240, 240, 240));
        day9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day9.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day9.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day9.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        day9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                day9ActionPerformed(evt);
            }
        });
        jPanel3.add(day9, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 100, 100, 70));

        day36.setBackground(new java.awt.Color(46, 49, 117));
        day36.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day36.setForeground(new java.awt.Color(240, 240, 240));
        day36.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day36.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day36.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day36.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day36.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        day36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                day36ActionPerformed(evt);
            }
        });
        jPanel3.add(day36, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 380, 100, 70));

        day37.setBackground(new java.awt.Color(46, 49, 117));
        day37.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day37.setForeground(new java.awt.Color(240, 240, 240));
        day37.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day37.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day37.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day37.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day37.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day37, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 380, 100, 70));

        day10.setBackground(new java.awt.Color(46, 49, 117));
        day10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day10.setForeground(new java.awt.Color(240, 240, 240));
        day10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day10.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day10.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day10.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day10, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 100, 100, 70));

        day17.setBackground(new java.awt.Color(46, 49, 117));
        day17.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day17.setForeground(new java.awt.Color(240, 240, 240));
        day17.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day17.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day17.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day17.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day17, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 170, 100, 70));

        day24.setBackground(new java.awt.Color(46, 49, 117));
        day24.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day24.setForeground(new java.awt.Color(240, 240, 240));
        day24.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day24.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day24.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day24.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day24.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day24, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 240, 100, 70));

        day31.setBackground(new java.awt.Color(46, 49, 117));
        day31.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day31.setForeground(new java.awt.Color(240, 240, 240));
        day31.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day31.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day31.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day31.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day31.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day31, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 310, 100, 70));

        day38.setBackground(new java.awt.Color(46, 49, 117));
        day38.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day38.setForeground(new java.awt.Color(240, 240, 240));
        day38.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day38.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day38.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day38.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day38.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day38, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 380, 100, 70));

        day11.setBackground(new java.awt.Color(46, 49, 117));
        day11.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day11.setForeground(new java.awt.Color(240, 240, 240));
        day11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day11.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day11.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day11.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day11, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 100, 100, 70));

        day18.setBackground(new java.awt.Color(46, 49, 117));
        day18.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day18.setForeground(new java.awt.Color(240, 240, 240));
        day18.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day18.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day18.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day18.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day18, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 170, 100, 70));

        day25.setBackground(new java.awt.Color(46, 49, 117));
        day25.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day25.setForeground(new java.awt.Color(240, 240, 240));
        day25.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day25.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day25.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day25.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day25.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        day25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                day25ActionPerformed(evt);
            }
        });
        jPanel3.add(day25, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 240, 100, 70));

        day32.setBackground(new java.awt.Color(46, 49, 117));
        day32.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day32.setForeground(new java.awt.Color(240, 240, 240));
        day32.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day32.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day32.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day32.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day32.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day32, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 310, 100, 70));

        day40.setBackground(new java.awt.Color(46, 49, 117));
        day40.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day40.setForeground(new java.awt.Color(240, 240, 240));
        day40.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day40.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day40.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day40.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day40.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day40, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 380, 100, 70));

        day12.setBackground(new java.awt.Color(46, 49, 117));
        day12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day12.setForeground(new java.awt.Color(240, 240, 240));
        day12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day12.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day12.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day12.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day12.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day12, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 100, 100, 70));

        day19.setBackground(new java.awt.Color(46, 49, 117));
        day19.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day19.setForeground(new java.awt.Color(240, 240, 240));
        day19.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day19.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day19.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day19.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day19.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day19, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 170, 100, 70));

        day13.setBackground(new java.awt.Color(46, 49, 117));
        day13.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day13.setForeground(new java.awt.Color(240, 240, 240));
        day13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day13.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day13.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day13.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day13, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 100, 100, 70));

        day26.setBackground(new java.awt.Color(46, 49, 117));
        day26.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day26.setForeground(new java.awt.Color(240, 240, 240));
        day26.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day26.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day26.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day26.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day26.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day26, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 240, 100, 70));

        day33.setBackground(new java.awt.Color(46, 49, 117));
        day33.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day33.setForeground(new java.awt.Color(240, 240, 240));
        day33.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day33.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day33.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day33.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day33.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day33, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 310, 100, 70));

        day34.setBackground(new java.awt.Color(46, 49, 117));
        day34.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day34.setForeground(new java.awt.Color(240, 240, 240));
        day34.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day34.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day34.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day34.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day34.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day34, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 310, 100, 70));

        day20.setBackground(new java.awt.Color(46, 49, 117));
        day20.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day20.setForeground(new java.awt.Color(240, 240, 240));
        day20.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day20.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day20.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day20.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day20, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 170, 100, 70));

        day41.setBackground(new java.awt.Color(46, 49, 117));
        day41.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day41.setForeground(new java.awt.Color(240, 240, 240));
        day41.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day41.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day41.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day41.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day41.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day41, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 380, 100, 70));

        day42.setBackground(new java.awt.Color(46, 49, 117));
        day42.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day42.setForeground(new java.awt.Color(240, 240, 240));
        day42.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day42.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day42.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day42.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day42.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        day42.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                day42ActionPerformed(evt);
            }
        });
        jPanel3.add(day42, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 380, 100, 70));

        day27.setBackground(new java.awt.Color(46, 49, 117));
        day27.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day27.setForeground(new java.awt.Color(240, 240, 240));
        day27.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day27.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day27.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day27.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day27.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day27, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 240, 100, 70));

        day14.setBackground(new java.awt.Color(46, 49, 117));
        day14.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day14.setForeground(new java.awt.Color(240, 240, 240));
        day14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day14.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day14.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day14.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day14, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 100, 100, 70));

        day21.setBackground(new java.awt.Color(46, 49, 117));
        day21.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day21.setForeground(new java.awt.Color(240, 240, 240));
        day21.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day21.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day21.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day21.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day21.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day21, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 170, 100, 70));

        day28.setBackground(new java.awt.Color(46, 49, 117));
        day28.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day28.setForeground(new java.awt.Color(240, 240, 240));
        day28.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day28.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day28.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day28.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day28.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day28, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 240, 100, 70));

        day35.setBackground(new java.awt.Color(46, 49, 117));
        day35.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        day35.setForeground(new java.awt.Color(240, 240, 240));
        day35.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        day35.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        day35.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        day35.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        day35.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(day35, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 310, 100, 70));

        calendarPanel.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 710, 460));

        jPanel14.setBackground(new java.awt.Color(46, 49, 117));
        jPanel14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel14MouseClicked(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(240, 240, 240));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("<<");
        jLabel16.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel16MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel16MouseEntered(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel16MouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        calendarPanel.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, 80, -1));

        jPanel15.setBackground(new java.awt.Color(46, 49, 117));
        jPanel15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel15MouseClicked(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(240, 240, 240));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText(">>");
        jLabel17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel17MouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel17MouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        calendarPanel.add(jPanel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 10, 80, -1));

        mainPanel.add(calendarPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 70, 700, 510));

        jPanel5.setBackground(new java.awt.Color(29, 30, 66));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBackground(new java.awt.Color(46, 49, 117));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        termineListe.setBackground(new java.awt.Color(29, 30, 66));
        termineListe.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        termineListe.setForeground(new java.awt.Color(240, 240, 240));
        termineListe.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        termineListe.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                termineListeMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                termineListeMouseEntered(evt);
            }
        });
        jScrollPane3.setViewportView(termineListe);

        jPanel4.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 170, 350));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(240, 240, 240));
        jLabel7.setText("Alle Termine");
        jPanel4.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, -1));

        jPanel5.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 190, 390));

        mainPanel.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(970, 160, 200, 420));

        jPanel10.setBackground(new java.awt.Color(46, 49, 117));
        jPanel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel10MouseClicked(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(240, 240, 240));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Neuen Termin");
        jLabel12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel12MouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel12MouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        mainPanel.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 20, 120, -1));

        jPanel11.setBackground(new java.awt.Color(46, 49, 117));
        jPanel11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel11MouseClicked(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(240, 240, 240));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Aktualisieren");
        jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel13MouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel13MouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        mainPanel.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 20, -1, -1));

        jPanel12.setBackground(new java.awt.Color(46, 49, 117));
        jPanel12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel12MouseClicked(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(240, 240, 240));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("Abmelden");
        jLabel14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel14MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        mainPanel.add(jPanel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 20, -1, -1));

        jPanel13.setBackground(new java.awt.Color(46, 49, 117));
        jPanel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel13MouseClicked(evt);
            }
        });

        zumProfil.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        zumProfil.setForeground(new java.awt.Color(240, 240, 240));
        zumProfil.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        zumProfil.setText("Zum Profil");
        zumProfil.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                zumProfilMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                zumProfilMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(zumProfil, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(zumProfil, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
        );

        mainPanel.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 70, 180, 80));

        eventMessage.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        eventMessage.setForeground(new java.awt.Color(240, 240, 240));
        eventMessage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        eventMessage.setText("Hallo!");
        mainPanel.add(eventMessage, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 20, 390, 30));

        getContentPane().add(mainPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(-4, 0, -1, 580));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jList1ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jList1ComponentShown
//        int i = 0;
//         //LinkedList<String> contactListe = stub.getKontakte(sitzungsID);
//        try {
//            for(String contactListe : stub.getKontakte(sitzungsID)){
//                i++;
//            listModel.addElement(contactListe);
//            //listModel.addElement(stub.getKontakte(sitzungsID));
//            }
//        } catch (BenutzerException | RemoteException ex) {
//            JOptionPane.showMessageDialog(null, ex.getMessage(), "Hauptfenster", JOptionPane.ERROR_MESSAGE);
//        }
    }//GEN-LAST:event_jList1ComponentShown

    private void benachListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_benachListMouseClicked
        if(evt.getClickCount() == 2 && evt.getButton() == MouseEvent.BUTTON1){
            int size = meldungModel.getSize();
            if (!(size == 0)) {
                try {
                    Meldung meldung = stub.getMeldungen(sitzungsID).get(meldungssize - 1 - benachList.getSelectedIndex());
                    new AnfragenMeldungenFenster(stub, sitzungsID, meldung, this).setVisible(true);
                } catch (RemoteException | BenutzerException ex) {
                    Logger.getLogger(Hauptfenster.class.getName()).log(Level.SEVERE, null, ex);
                }           
            }
        }
    }//GEN-LAST:event_benachListMouseClicked

    private void benachListComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_benachListComponentShown

    }//GEN-LAST:event_benachListComponentShown

    private void day6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_day6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_day6ActionPerformed

    private void day5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_day5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_day5ActionPerformed

    private void day16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_day16ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_day16ActionPerformed

    private void day9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_day9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_day9ActionPerformed

    private void day36ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_day36ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_day36ActionPerformed

    private void day25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_day25ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_day25ActionPerformed

    private void jList1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseClicked
        if(evt.getClickCount() == 2 && evt.getButton() == MouseEvent.BUTTON1){
            KontaktProfilFenster profil;
            
            try {
                profil = new KontaktProfilFenster(stub, sitzungsID, jList1.getSelectedValue());
                profil.setVisible(true);
            } catch (RemoteException | BenutzerException | SQLException ex) {
                Logger.getLogger(Hauptfenster.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }//GEN-LAST:event_jList1MouseClicked

    private void showAddKontaktMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showAddKontaktMouseClicked

        showAddKontakt.setForeground(Color.white);
        //AddKontakt start= new AddKontakt(stub,sitzungsID);
        //start.setVisible(true);
        String contact = contactUsernameField.getText();
        if (contact.length() > 0) {
            try {
                //if (contact.length() >= 0) {
                //AddKontakt add = new AddKontakt(stub,sitzungsID);
                stub.addKontakt(contact, sitzungsID);
                listModel.addElement(contact);
                contactUsernameField.setText("");
                showRemoveKontakt.setEnabled(true);
            } catch (RemoteException | BenutzerException | SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Kontakt hinzufügen - Termin Kalender", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Geben Sie bitte einen gültigen Benutzername an", "Hauptfenster - Termin Kalender", JOptionPane.WARNING_MESSAGE);

        }

    }//GEN-LAST:event_showAddKontaktMouseClicked

    private void jPanel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel8MouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_jPanel8MouseClicked

    private void showRemoveKontaktMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showRemoveKontaktMouseClicked
        showRemoveKontakt.setForeground(Color.white);
        //RemoveKontakt start = new RemoveKontakt(stub,sitzungsID);
        //start.setVisible(true);
        int selectedIndex = jList1.getSelectedIndex();
        int size = listModel.getSize();
        if (selectedIndex != -1) {
            try {
                stub.removeKontakt(listModel.get(selectedIndex).toString(), sitzungsID);
                listModel.remove(selectedIndex);
                selectedIndex--;
            } catch (BenutzerException | RemoteException | SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Kontakt entfernen - Termin Kalender", JOptionPane.ERROR_MESSAGE);
            }
        } else if (size == 0) {
            showRemoveKontakt.setEnabled(false);
            JOptionPane.showMessageDialog(null, "Die Liste ist doch leer !", "Hauptfenster - Termin Kalender", JOptionPane.WARNING_MESSAGE);

        } else {
            JOptionPane.showMessageDialog(null, "Ein Problem ist aufgetretten", "Kontakt entfernen - Termin Kalender", JOptionPane.WARNING_MESSAGE);
        }

        //fillList();
    }//GEN-LAST:event_showRemoveKontaktMouseClicked

    private void jPanel9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel9MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel9MouseClicked

    private void termineListeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_termineListeMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_termineListeMouseEntered

    private void termineListeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_termineListeMouseClicked
        if(evt.getClickCount() == 2 && evt.getButton() == MouseEvent.BUTTON1){
            if (termineListeModel.getSize() > 0) {
                try {                                             
                    int terminID = stub.getTermineAmTag(new Datum(tagBekommen[daySelector], month + 1, year), sitzungsID).get(termineListe.getSelectedIndex()).getID();                   
                    new TerminAnzeigenFenster(terminID, stub, sitzungsID, this).setVisible(true);
                } catch (RemoteException | BenutzerException | TerminException | Datum.DatumException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Hauptfenster", JOptionPane.ERROR_MESSAGE);
                }
            }
        }     
    }//GEN-LAST:event_termineListeMouseClicked

    private void jPanel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel10MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel10MouseClicked

    private void jLabel12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseClicked
        // TODO add your handling code here:
        jLabel12.setForeground(Color.white);
        TerminAnlegenFenster startTA = new TerminAnlegenFenster(stub, sitzungsID, this);
        startTA.setVisible(true);
    }//GEN-LAST:event_jLabel12MouseClicked

    private void jLabel13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel13MouseClicked

        refresh();
    }//GEN-LAST:event_jLabel13MouseClicked

    private void jPanel11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel11MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel11MouseClicked

    private void jLabel14MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel14MouseClicked
        // TODO add your handling code here:
        ausloggen();
    }//GEN-LAST:event_jLabel14MouseClicked

    private void jPanel12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel12MouseClicked
        // TODO add your handling code here:
        ausloggen();
    }//GEN-LAST:event_jPanel12MouseClicked

    private void zumProfilMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_zumProfilMouseClicked
        // TODO add your handling code here:
        zumProfil.setForeground(Color.white);
        ProfilFenster profil;
        profil = new ProfilFenster(stub, sitzungsID);
        profil.fillProfil();
        profil.setVisible(true);
    }//GEN-LAST:event_zumProfilMouseClicked

    private void jPanel13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel13MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel13MouseClicked

    private void jLabel16MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel16MouseClicked
        // TODO add your handling code here:
        jLabel16.setForeground(Color.white);
        month--;
        displayDate();
    }//GEN-LAST:event_jLabel16MouseClicked

    private void jPanel14MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel14MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel14MouseClicked

    private void jLabel17MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel17MouseClicked
        // TODO add your handling code here:
        jLabel17.setForeground(Color.white);
        month++;
        displayDate();
    }//GEN-LAST:event_jLabel17MouseClicked

    private void jPanel15MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel15MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel15MouseClicked

    private void day42ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_day42ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_day42ActionPerformed

    private void jLabel16MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel16MouseReleased
        // TODO add your handling code here:
        jLabel16.setForeground(Color.WHITE);
    }//GEN-LAST:event_jLabel16MouseReleased

    private void jLabel17MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel17MouseReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_jLabel17MouseReleased

    private void jLabel12MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_jLabel12MouseReleased

    private void jLabel13MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel13MouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel13MouseReleased

    private void zumProfilMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_zumProfilMouseReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_zumProfilMouseReleased

    private void showAddKontaktMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showAddKontaktMouseReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_showAddKontaktMouseReleased

    private void showRemoveKontaktMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showRemoveKontaktMouseReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_showRemoveKontaktMouseReleased

    private void contactUsernameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contactUsernameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_contactUsernameFieldActionPerformed

    private void jLabel16MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel16MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel16MouseEntered

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        // TODO add your handling code here:
        VersionsFenster startVersion = new VersionsFenster();
        startVersion.setVisible(true);
    }//GEN-LAST:event_jLabel1MouseClicked

    public void ausloggen() {
        try {

            stub.ausloggen(sitzungsID);
            //this.setVisible(false);
            this.dispose();
            this.fenster.setVisible(true);
            
            this.timer.stop();
            /*GUI out = new GUI();
            out.startGUI();         */
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Hauptfenster", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Fuele Kontaktliste auf
     */
    public void fillContactList() {
        int i = 0;
        try {
            for (String contactListe : stub.getKontakte(sitzungsID)) {
                i++;
                listModel.addElement(contactListe);
                //listModel.addElement(stub.getKontakte(sitzungsID));
            }
        } catch (BenutzerException | RemoteException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Hauptfenster", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Fuele Meldung liste auf
     */
    public void fillMeldList() { 
        try {
            meldungModel = new DefaultListModel();
            LinkedList<Meldung> meldungen = stub.getMeldungen(sitzungsID);            
            meldungssize = meldungen.size();
            Meldung meldung;
            
            for (int i = meldungssize - 1; i >= 0; i--) {
                meldung = meldungen.get(i);
                if(meldung instanceof Anfrage){
                    meldungModel.addElement("Einladung von " + ((Anfrage) meldung).getAbsender());
                    //TODO: färbe hintergrund in anderer farbe
                }
                else{
                    meldungModel.addElement(meldung.getText().substring(0, 25) + "...");
                }
            }

        } catch (RemoteException | BenutzerException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Benachrichtigungen aktualisierung", JOptionPane.ERROR_MESSAGE);
        }
        benachList.setModel(meldungModel);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {

            if (jList1.getSelectedIndex() == -1) {
                //No selection, disable add button.
                showAddKontakt.setEnabled(false);

            } else {
                //Selection, enable the remove button.
                showRemoveKontakt.setEnabled(true);
            }
        }
    }

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
            java.util.logging.Logger.getLogger(Hauptfenster.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Hauptfenster().setVisible(true);
        });
    }
    
    public void refresh(){
        jLabel13.setForeground(Color.white);
        fillMeldList();
        displayDate();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<String> benachList;
    private javax.swing.JPanel calendarPanel;
    private javax.swing.JTextField contactUsernameField;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JButton day1;
    private javax.swing.JButton day10;
    private javax.swing.JButton day11;
    private javax.swing.JButton day12;
    private javax.swing.JButton day13;
    private javax.swing.JButton day14;
    private javax.swing.JButton day15;
    private javax.swing.JButton day16;
    private javax.swing.JButton day17;
    private javax.swing.JButton day18;
    private javax.swing.JButton day19;
    private javax.swing.JButton day2;
    private javax.swing.JButton day20;
    private javax.swing.JButton day21;
    private javax.swing.JButton day22;
    private javax.swing.JButton day23;
    private javax.swing.JButton day24;
    private javax.swing.JButton day25;
    private javax.swing.JButton day26;
    private javax.swing.JButton day27;
    private javax.swing.JButton day28;
    private javax.swing.JButton day29;
    private javax.swing.JButton day3;
    private javax.swing.JButton day30;
    private javax.swing.JButton day31;
    private javax.swing.JButton day32;
    private javax.swing.JButton day33;
    private javax.swing.JButton day34;
    private javax.swing.JButton day35;
    private javax.swing.JButton day36;
    private javax.swing.JButton day37;
    private javax.swing.JButton day38;
    private javax.swing.JButton day39;
    private javax.swing.JButton day4;
    private javax.swing.JButton day40;
    private javax.swing.JButton day41;
    private javax.swing.JButton day42;
    private javax.swing.JButton day5;
    private javax.swing.JButton day6;
    private javax.swing.JButton day7;
    private javax.swing.JButton day8;
    private javax.swing.JButton day9;
    private javax.swing.JLabel dienstagLabel;
    private javax.swing.JLabel donnerstagLabel;
    private javax.swing.JLabel eventMessage;
    private javax.swing.JLabel freitagLabel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
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
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel mittwochLabel;
    private javax.swing.JLabel montagLabel;
    private javax.swing.JLabel samstagLabel;
    private javax.swing.JLabel showAddKontakt;
    private javax.swing.JLabel showRemoveKontakt;
    private javax.swing.JLabel sonntagLabel;
    private javax.swing.JList<String> termineListe;
    private javax.swing.JLabel zumProfil;
    // End of variables declaration//GEN-END:variables

}
