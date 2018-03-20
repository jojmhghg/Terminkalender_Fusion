/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Utilities;

import Server.PrimitiveServerDaten;
import Utilities.Anfrage;
import Utilities.Benutzer;
import Utilities.Datum;
import Utilities.Meldung;
import Utilities.Teilnehmer;
import Utilities.Termin;
import Utilities.TerminException;
import Utilities.Zeit;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBHandler {
    boolean abfrage;
    private static Connection con;
    private static boolean hasData;
    ResultSet rs;
    private final LinkedList<Sitzung> aktiveSitzungen;
    private final LinkedList<Verbindung> connectionList;
    public PrimitiveServerDaten primitiveDaten;
    
    /**
     * Konstruktur
     * @param aktiveSitzungen
     * @param connectionlist
     * @param primitiveDaten 
     */
    public DBHandler(LinkedList<Sitzung> aktiveSitzungen, LinkedList<Verbindung> connectionlist, PrimitiveServerDaten primitiveDaten){
        abfrage = true;
        hasData = false;
        con = null;
        this.aktiveSitzungen = aktiveSitzungen;
        this.connectionList = connectionlist;
        this.primitiveDaten = primitiveDaten;
    }   
    
    /**
     * Stellt Verbindung zur Datenbank her
     * @param serverID
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws NoSuchAlgorithmException 
     */
    public void getConnection(int serverID) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException {       
        if(con == null){
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:Kalender.db");
            initialise(serverID);  
        }       
    }
    
    /**
     * erstellt die Datenbank, bzw einzelne Tabellen falls noch nicht vorhanden
     * 
     * @throws SQLException
     * @throws NoSuchAlgorithmException 
     */
    private void initialise(int serverID) throws SQLException, NoSuchAlgorithmException{
        if(!hasData){
            hasData = true;
            Statement state1 = con.createStatement();
            Statement state2 = con.createStatement();
            Statement state3 = con.createStatement();
            Statement state4 = con.createStatement();
            Statement state5 = con.createStatement();
            Statement state6 = con.createStatement();
            Statement state7 = con.createStatement();
            
            ResultSet res1 = state1.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='benutzer'");
            ResultSet res2 = state2.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='termine'");
            ResultSet res3 = state3.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='terminkalender'");
            ResultSet res4 = state4.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='kontaktliste'");
            ResultSet res5 = state5.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='meldungen'");
            ResultSet res6 = state6.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='anfragen'");
            ResultSet res7 = state7.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='counters'");
            
            if(!res1.next()){
                System.out.println("Building the User table with prepopulated values.");
                Statement stateBenutzer = con.createStatement();
                stateBenutzer.execute("CREATE TABLE benutzer(userID integer,"
                        + "username varchar(60),"
                        + "email varchar(100),"
                        + "name varchar(60),"
                        + "lastname varchar(60),"
                        + "password varchar(60),"
                        + "color integer,"
                        + "PRIMARY KEY (username))");
            }
            
            if(!res2.next()){
                System.out.println("Building the Termin table with prepopulated values.");
                Statement stateTermine = con.createStatement();
                stateTermine.execute("CREATE TABLE termine(terminID integer,"
                        + "titel varchar(60),"
                        + "day intger,"
                        + "month integer,"
                        + "year integer,"
                        + "from_hours integer,"
                        + "from_minutes integer,"
                        + "to_hours integer,"
                        + "to_minutes integer,"
                        + "note varchar(100),"
                        + "location varchar(60),"
                        + "ownername varchar(60),"
                        + "editEveryone integer,"
                        + "timestemp integer, "
                        + "editorID integer, "
                        + "primary key(terminID))");
            }
            
            if(!res3.next()){
                System.out.println("Building the UserTermin table with prepopulated values.");
                Statement stateKalender = con.createStatement();
                stateKalender.execute("CREATE TABLE terminkalender(terminID integer,"
                        + "username varchar(60),"
                        + "nimmtTeil integer,"
                        + "primary key(username, terminID))");
            }
            
            if(!res4.next()){
                System.out.println("Building the Kontakliste table with prepopulated values.");
                Statement stateKontaktliste = con.createStatement();
                stateKontaktliste.execute("CREATE TABLE kontaktliste(userID integer,"
                        + "kontaktname varchar(60),"
                        + "primary key(userID, kontaktname))");
            }
            
            if(!res5.next()){
                System.out.println("Building the Meldung table with prepopulated values.");
                Statement stateMeldungen = con.createStatement();
                stateMeldungen.execute("CREATE TABLE meldungen(meldungsID integer,"
                        + "username varchar(60),"
                        + "text varchar(60),"
                        + "gelesen integer,"
                        + "anfrage integer,"
                        + "primary key(meldungsID))");
            }
            
            if(!res6.next()){
                System.out.println("Building the MeldungAnfrage table with prepopulated values.");
                Statement stateAnfragen = con.createStatement();
                stateAnfragen.execute("CREATE TABLE anfragen(meldungsID integer,"
                        + "terminID integer,"
                        + "absender varchar(60),"
                        + "primary key(meldungsID))");
            }
            
            if(!res7.next()){
                System.out.println("Building the Benutzerliste table with prepopulated values.");
                Statement stateAnfragen = con.createStatement();
                stateAnfragen.execute("CREATE TABLE counters(reihe integer,"
                        + "userCounter integer,"
                        + "terminCounter integer,"
                        + "requestCounter integer,"
                        + "meldungsCounter integer,"
                        + "serverID integer,"
                        + "primary key(reihe))");
                
                PreparedStatement prepuser = con.prepareStatement("INSERT INTO counters values(?,?,?,?,?,?);");        
                prepuser.setInt(1, 1);
                prepuser.setInt(2, serverID * 1000 + 1);
                prepuser.setInt(3, serverID * 1000 + 1);
                prepuser.setInt(4, serverID * 1000 + 1);
                prepuser.setInt(5, serverID * 1000 + 1);
                prepuser.setInt(6, serverID);
                prepuser.execute(); 
            }
        }
    }
    
    /**
     * fügt der DB einen neuen User hinzu und erhöht den UserIdCounter in der DB um 1
     * 
     * 
     * @param username username des neuen Users
     * @param passwort passwort des neuen Users
     * @param email email des neuen Users
     * @return userID
     * @throws SQLException 
     */
    public int addUser(String username, String passwort, String email) throws SQLException{
        Statement state = con.createStatement();   
        ResultSet res = state.executeQuery("SELECT * FROM counters " +
                "WHERE reihe = " + 1); 
        
        res.next();           
        int userID = res.getInt("userCounter");

        PreparedStatement prepuser = con.prepareStatement("INSERT INTO benutzer values(?,?,?,?,?,?,?);");        
        prepuser.setInt(1, userID);
        prepuser.setString(2, username);
        prepuser.setString(3, email);
        prepuser.setString(4, "");
        prepuser.setString(5, "");
        prepuser.setString(6, passwort);
        prepuser.setInt(7, 0);
        prepuser.execute(); 

        PreparedStatement prepIncUserCounter = con.prepareStatement("UPDATE counters SET userCounter = userCounter + 1 WHERE reihe = 1");
        prepIncUserCounter.execute(); 

        return userID;       
    }
    
    /**
     * gibt den meldungsCounter zurück
     * 
     * @return
     * @throws SQLException 
     */
    public int getRequestCounter() throws SQLException{
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("Select * FROM counters " +
                "Where reihe = 1");
        res.next();
        
        return res.getInt("requestCounter");      
    }
    
    /**
     * inkrementiert den request counter in der db
     * 
     * @throws SQLException 
     */
    public void incRequestCounter() throws SQLException{
        PreparedStatement prepIncCounter = con.prepareStatement("UPDATE counters SET requestCounter = requestCounter + 1 WHERE reihe = 1");
        prepIncCounter.execute();
    }
    
    /**
     * aktuallisiert das PW eines Users
     * 
     * @param username bestimmt den user
     * @param passwort neues passwort
     * @throws SQLException 
     */
    public void changePasswort(String username, String passwort) throws SQLException{
        PreparedStatement prepResetPW = con.prepareStatement("UPDATE benutzer SET password = ? WHERE username = ?");
        prepResetPW.setString(1, passwort);
        prepResetPW.setString(2, username);
        prepResetPW.execute();      
    }
    
    /**
     * fügt dem user einen neuen kontakt hinzu
     * 
     * @param userID id des users
     * @param kontaktname username des neuen kontakts
     * @throws SQLException 
     */
    public void addKontakt(int userID, String kontaktname) throws SQLException{
        PreparedStatement prepAddKontakt = con.prepareStatement("INSERT INTO kontaktliste values(?,?);");
        prepAddKontakt.setInt(1, userID);
        prepAddKontakt.setString(2, kontaktname);
        prepAddKontakt.execute();   
    }
    
    /**
     * entfernt einen kontakt eines users
     * 
     * @param userID id des users
     * @param kontaktname username des zu entfernenden kontaktes
     * @throws SQLException 
     */
    public void removeKontakt(int userID, String kontaktname) throws SQLException{
        PreparedStatement prepRemoveKontakt = con.prepareStatement("DELETE FROM kontaktliste WHERE userID = ? AND kontaktname = ?;");        
        prepRemoveKontakt.setInt(1, userID);
        prepRemoveKontakt.setString(2, kontaktname);
        prepRemoveKontakt.execute(); 
    }
    
    /**
     * ändert den vornamen eines users
     * 
     * @param neuerVorname neuer vorname
     * @param username name des users
     * @throws SQLException 
     */
    public void changeVorname(String neuerVorname, String username) throws SQLException{
        PreparedStatement prepChangeVorname = con.prepareStatement("UPDATE benutzer SET name = ? WHERE username = ?");
        prepChangeVorname.setString(1, neuerVorname);
        prepChangeVorname.setString(2, username);
        prepChangeVorname.execute(); 
    }
    
    /**
     * ändert den nachnamen eines users
     * 
     * @param neuerNachname neuer Nachname
     * @param username username des Users
     * @throws SQLException 
     */
    public void changeNachname(String neuerNachname, String username) throws SQLException{
        PreparedStatement prepChangeNachname = con.prepareStatement("UPDATE benutzer SET lastname = ? WHERE username = ?");
        prepChangeNachname.setString(1, neuerNachname);
        prepChangeNachname.setString(2, username);
        prepChangeNachname.execute(); 
    }
    
    /**
     * Ändert die Email Adresse eines Users
     * 
     * @param neueEmail neue Email Adresse
     * @param username username des Users
     * @throws SQLException 
     */
    public void changeEmail(String neueEmail, String username) throws SQLException{
        PreparedStatement prepChangeEmail = con.prepareStatement("UPDATE benutzer SET email = ? WHERE username = ?");
        prepChangeEmail.setString(1, neueEmail);
        prepChangeEmail.setString(2, username);
        prepChangeEmail.execute(); 
    }
    
    /**
     * erstellt einen neuen Termin und gibt TerminID zurück
     * der termin sollte vorher auf dem server schonmal angelegt werden
     * 
     * @param datum datum des termins
     * @param beginn beginn des termins
     * @param ende ende des termins
     * @param titel titel des termins
     * @param userID id des erstellers des termins
     * @param terminID id des termins der erstellt werden soll
     * @throws SQLException 
     */
    public void addNewTermin(Datum datum, Zeit beginn, Zeit ende, String titel, int userID, int terminID) throws SQLException{
        String username = getUsernameByUserID(userID);
        
        //insert Termin
        PreparedStatement prepAddNewTermin = con.prepareStatement("INSERT INTO termine values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
        prepAddNewTermin.setInt(1, terminID);
        prepAddNewTermin.setString(2, titel);
        prepAddNewTermin.setInt(3, datum.getTag());
        prepAddNewTermin.setInt(4, datum.getMonat());
        prepAddNewTermin.setInt(5, datum.getJahr());
        prepAddNewTermin.setInt(6, beginn.getStunde());
        prepAddNewTermin.setInt(7, beginn.getMinute());
        prepAddNewTermin.setInt(8, ende.getStunde());
        prepAddNewTermin.setInt(9, ende.getMinute());
        prepAddNewTermin.setString(10, "");
        prepAddNewTermin.setString(11, "");
        prepAddNewTermin.setString(12, username);
        prepAddNewTermin.setInt(13, 0);
        prepAddNewTermin.setInt(14, 0);
        prepAddNewTermin.setInt(15, 0);
        prepAddNewTermin.execute();       
        
        //füge Termin dem user hinzu
        addTerminToUser(terminID, 1, username);       
       
        //increment terminIdCounter
        PreparedStatement prepIncUserCounter = con.prepareStatement("UPDATE counters SET terminCounter = terminCounter + 1 WHERE reihe = 1");
        prepIncUserCounter.execute();  
    }
    
    /**
     * Fügt einen bereits bestehenden Termin der DB hinzu
     * (Termin existiert auf anderem Server)
     * 
     * @param termin
     * @throws SQLException 
     */
    public void addExistingTermin(Termin termin) throws SQLException{       
        //insert Termin
        PreparedStatement prepAddTermin = con.prepareStatement("INSERT INTO termine values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
        prepAddTermin.setInt(1, termin.getID());
        prepAddTermin.setString(2, termin.getTitel());
        prepAddTermin.setInt(3, termin.getDatum().getTag());
        prepAddTermin.setInt(4, termin.getDatum().getMonat());
        prepAddTermin.setInt(5, termin.getDatum().getJahr());
        prepAddTermin.setInt(6, termin.getBeginn().getStunde());
        prepAddTermin.setInt(7, termin.getBeginn().getMinute());
        prepAddTermin.setInt(8, termin.getEnde().getStunde());
        prepAddTermin.setInt(9, termin.getEnde().getMinute());
        prepAddTermin.setString(10, termin.getNotiz());
        prepAddTermin.setString(11, termin.getOrt());
        prepAddTermin.setString(12, termin.getOwner());
        if(termin.getEditierbar()){
            prepAddTermin.setInt(13, 1);
        }
        else{
            prepAddTermin.setInt(13, 0);
        }       
        prepAddTermin.setInt(14, termin.getTimestemp());
        prepAddTermin.setInt(15, termin.getEditorID());
        prepAddTermin.execute();    
               

        for(Teilnehmer teilnehmer : termin.getTeilnehmerliste()){ 
            PreparedStatement prepState = con.prepareStatement("INSERT INTO terminkalender values(?,?,?);");
            prepState.setInt(1, termin.getID());
            prepState.setString(2, teilnehmer.getUsername());
            if(teilnehmer.checkIstTeilnehmer()){
                prepState.setInt(3, 1);
            }
            else{
                prepState.setInt(3, 0);
            }        
            prepState.execute();
        }
    }
    
    /**
     * fügt einen bestehenden Termin einem neuen User hinzu 
     * (wenn User zu Termin eingeladen wird bzw beitritt)
     * 
     * @param terminID id des termins
     * @param nimmtTeil true falls er teilnimmt, sonst false
     * @param username username des users
     * @throws SQLException 
     */
    public void addTerminToUser(int terminID, int nimmtTeil, String username) throws SQLException{
        PreparedStatement prepAddTermin = con.prepareStatement("INSERT INTO terminkalender values(?,?,?);");
        prepAddTermin.setInt(1, terminID);
        prepAddTermin.setString(2, username);
        prepAddTermin.setInt(3, nimmtTeil);
        prepAddTermin.execute();
    }
      
    /**
     * löscht einen termin komplett (für alle)
     * 
     * @param terminID id des termins
     * @throws SQLException 
     */
    public void deleteTermin(int terminID) throws SQLException{
        PreparedStatement prepDeleteTermin = con.prepareStatement("DELETE FROM terminkalender WHERE terminID = ?;");      
        prepDeleteTermin.setInt(1, terminID);
        prepDeleteTermin.execute(); 
        
        prepDeleteTermin = con.prepareStatement("DELETE FROM termine WHERE terminID = ?;");      
        prepDeleteTermin.setInt(1, terminID);
        prepDeleteTermin.execute(); 
        
        Statement state = con.createStatement();
        ResultSet resSet = state.executeQuery("Select * FROM anfragen " +
                "Where terminID = " + terminID);
        
        while(resSet.next()){
            prepDeleteTermin = con.prepareStatement("DELETE FROM meldungen WHERE meldungsID = ?;");      
            prepDeleteTermin.setInt(1, resSet.getInt("meldungsID"));
            prepDeleteTermin.execute(); 
        } 
        
        prepDeleteTermin = con.prepareStatement("DELETE FROM anfragen WHERE terminID = ?;");      
        prepDeleteTermin.setInt(1, terminID);
        prepDeleteTermin.execute(); 
    }
    
    /**
     * ändert die editierrechte eines termins (wer hat rechte)
     * 
     * @param editierbar true = alle, falls = nur owner
     * @param terminID id des termins
     * @throws SQLException 
     */
    public void changeEditierrechte(boolean editierbar, int terminID) throws SQLException{
        PreparedStatement prepChangeEditierrechte = con.prepareStatement("UPDATE termine SET editEveryone = ? WHERE terminID = ?");
        if(editierbar){
            prepChangeEditierrechte.setInt(1, 1);
        }
        else{
            prepChangeEditierrechte.setInt(1, 0);
        }
        prepChangeEditierrechte.setInt(2, terminID);
        prepChangeEditierrechte.execute();
    }
            
    /**
     * ändert den ort des termins
     * 
     * @param terminID id des termins
     * @param neuerOrt neuer ort
     * @throws SQLException 
     */
    public void changeTerminort(int terminID, String neuerOrt) throws SQLException{
        PreparedStatement prepChangeTerminort = con.prepareStatement("UPDATE termine SET location = ? WHERE terminID = ?");
        prepChangeTerminort.setString(1, neuerOrt);
        prepChangeTerminort.setInt(2, terminID);
        prepChangeTerminort.execute();
    }
    
    /**
     * ändert den titel des termins
     * 
     * @param terminID id des termins
     * @param neuerTitel neuer titel 
     * @throws SQLException 
     */
    public void changeTermintitel(int terminID, String neuerTitel) throws SQLException{
        PreparedStatement prepChangeTermintitel = con.prepareStatement("UPDATE termine SET titel = ? WHERE terminID = ?");
        prepChangeTermintitel.setString(1, neuerTitel);
        prepChangeTermintitel.setInt(2, terminID);
        prepChangeTermintitel.execute();
    }
    
    /**
     * ändert die notiz des termins
     * 
     * @param terminID id des temins
     * @param neueNotiz neue notiz
     * @throws SQLException 
     */
    public void changeTerminnotiz(int terminID, String neueNotiz) throws  SQLException{
        PreparedStatement prepChangeTerminnotiz = con.prepareStatement("UPDATE termine SET note = ? WHERE terminID = ?");
        prepChangeTerminnotiz.setString(1, neueNotiz);
        prepChangeTerminnotiz.setInt(2, terminID);
        prepChangeTerminnotiz.execute();
    }
    
    /**
     * änder das ende eines termins (stunden und minuten)
     * 
     * @param terminID id des termins
     * @param neuesEnde neue endzeit
     * @throws SQLException 
     */
    public void changeTerminende(int terminID, Zeit neuesEnde) throws  SQLException{
        PreparedStatement prepChangeTerminende = con.prepareStatement("UPDATE termine SET to_hours = ?, to_minutes = ? WHERE terminID = ?");
        prepChangeTerminende.setInt(1, neuesEnde.getStunde());
        prepChangeTerminende.setInt(2, neuesEnde.getMinute());
        prepChangeTerminende.setInt(3, terminID);
        prepChangeTerminende.execute(); 
    }
    
    /**
     * änder den anfang eines termins (stunden und minuten)
     * 
     * @param terminID id des termins
     * @param neuerBeginn neue startzeit 
     * @throws SQLException 
     */
    public void changeTerminbeginn(int terminID, Zeit neuerBeginn) throws SQLException{
        PreparedStatement prepChangeTerminbeginn = con.prepareStatement("UPDATE termine SET from_hours = ?, from_minutes = ? WHERE terminID = ?");
        prepChangeTerminbeginn.setInt(1, neuerBeginn.getStunde());
        prepChangeTerminbeginn.setInt(2, neuerBeginn.getMinute());
        prepChangeTerminbeginn.setInt(3, terminID);
        prepChangeTerminbeginn.execute(); 
    }
    
    /**
     * ändert das datum eines termins (tag monat jahr)
     * 
     * @param terminID id des termins
     * @param neuesDatum neues datum 
     * @throws SQLException 
     */
    public void changeTermindatum(int terminID, Datum neuesDatum) throws SQLException{
        PreparedStatement prepChangeTermin = con.prepareStatement("UPDATE termine SET day = ?, month = ?, year = ? WHERE terminID = ?");
        prepChangeTermin.setInt(1, neuesDatum.getTag());
        prepChangeTermin.setInt(2, neuesDatum.getMonat());
        prepChangeTermin.setInt(3, neuesDatum.getJahr());
        prepChangeTermin.setInt(4, terminID);
        prepChangeTermin.execute(); 
    }
    
    /**
     * erhöht den zeitstempel um 1
     * 
     * @param terminID id des termins
     * @throws SQLException 
     */
    public void incTimestemp(int terminID) throws SQLException{
        PreparedStatement prepChangeTermin = con.prepareStatement("UPDATE termine SET timestemp = timestemp + 1 WHERE terminID = ?");
        prepChangeTermin.setInt(1, terminID);
        prepChangeTermin.execute(); 
    }
    
    /**
     * aktuallisiert die EditorID
     * 
     * @param terminID id des Termins
     * @param editorID neue editor id
     * @throws SQLException 
     */
    public void updateEditorID(int terminID, int editorID) throws SQLException{
        PreparedStatement prepChangeTermin = con.prepareStatement("UPDATE termine SET editorID = ? WHERE terminID = ?");
        prepChangeTermin.setInt(1, editorID);
        prepChangeTermin.setInt(2, terminID);
        prepChangeTermin.execute(); 
    }
    
    /**
     * lässt einen user an einem termin teilnehmen
     * 
     * @param terminID id des termins
     * @param username name des users
     * @throws SQLException 
     */
    public void nimmtTeil(int terminID, String username) throws SQLException{
        PreparedStatement prepNimmtTeil = con.prepareStatement("UPDATE terminkalender SET nimmtTeil = ? WHERE terminID = ? AND username = ?");
        prepNimmtTeil.setInt(1, 1);
        prepNimmtTeil.setInt(2, terminID);
        prepNimmtTeil.setString(3, username);
        prepNimmtTeil.execute();
    }
       
    /**
     * fügt der db eine neue meldung hinzu
     * für anfragen gibt es eine eigene methode!!!
     * 
     * @param username name des users der die meldung bekommt
     * @param text text der meldung
     * @param istAnfrage true wenn anfrage, false falls nur meldung
     * @return
     * @throws SQLException 
     */
    public int addMeldung(String username, String text, Boolean istAnfrage) throws SQLException{
        //get meldungsID
        Statement state = con.createStatement();   
        ResultSet res = state.executeQuery("SELECT * FROM counters " +
                "WHERE reihe = " + 1);        
        res.next();           
        int meldungsID = res.getInt("meldungsCounter");
  
        //insert meldung
        PreparedStatement prepAddMeldung = con.prepareStatement("INSERT INTO meldungen values(?,?,?,?,?);");
        prepAddMeldung.setInt(1, meldungsID);
        prepAddMeldung.setString(2, username);
        prepAddMeldung.setString(3, text);
        prepAddMeldung.setInt(4, 0);
        if(istAnfrage){
            prepAddMeldung.setInt(5, 1);
        }
        else{
            prepAddMeldung.setInt(5, 0);
        }
        prepAddMeldung.execute();
        
        //increment meldungsCounter
        PreparedStatement prepIncUserCounter = con.prepareStatement("UPDATE counters SET meldungsCounter = meldungsCounter + 1 WHERE reihe = 1");
        prepIncUserCounter.execute(); 

        return meldungsID; 
    }  
    
    /**
     * fügt einem user eine anfrage hinzu
     * 
     * @param username name des angefragten users
     * @param terminID id des termins der anfrage
     * @param absender username des absenders der anfrage
     * @param text text der anfrage
     * @return meldungsid
     * @throws SQLException 
     */
    public int addAnfrage(String username, int terminID, String absender, String text) throws SQLException{
        //get meldungsID
        Statement state = con.createStatement();   
        ResultSet res = state.executeQuery("SELECT * FROM counters " +
                "WHERE reihe = " + 1);        
        res.next();      
        
        int meldungsID = res.getInt("meldungsCounter");
        
        //fügt teil der meldung hinzu
        addMeldung(username, text, true);
        
        //fügt teil der anfrage hinzu
        PreparedStatement prepAddAnfrage = con.prepareStatement("INSERT INTO anfragen values(?,?,?);");
        prepAddAnfrage.setInt(1, meldungsID);
        prepAddAnfrage.setInt(2, terminID);
        prepAddAnfrage.setString(3, absender);
        prepAddAnfrage.execute();
        
        return meldungsID;
    }
    
    /**
     * löscht eine meldung
     * 
     * @param meldungsID id der meldung
     * @throws SQLException 
     */
    public void deleteMeldung(int meldungsID) throws SQLException{
        PreparedStatement deleteAnfrage, deleteMeldung;
        Statement state = con.createStatement();
        ResultSet resSet = state.executeQuery("Select * From meldungen " +
                    "Where meldungsID = " + meldungsID);
        
        if(resSet.getInt("anfrage") == 1){
            deleteAnfrage = con.prepareStatement("DELETE FROM anfragen WHERE meldungsID = ?");
            deleteAnfrage.setInt(1, meldungsID);
            deleteAnfrage.execute();
        }
        
        deleteMeldung = con.prepareStatement("DELETE FROM meldungen WHERE meldungsID = ?");
        deleteMeldung.setInt(1, meldungsID);
        deleteMeldung.execute();
    }
    
    /**
     * löscht eine meldung
     * 
     * @param terminID id des termins zu dem die anfrage gehört
     * @throws SQLException 
     */
    public void deleteAnfrageByTerminID(int terminID) throws SQLException{
        PreparedStatement deleteAnfrage, deleteMeldung;
        Statement state = con.createStatement();
        ResultSet resSet = state.executeQuery("Select * From anfragen " +
                    "Where terminID = " + terminID);
        
        while(resSet.next()){
            int meldungsID = resSet.getInt("meldungsID");
        
            deleteAnfrage = con.prepareStatement("DELETE FROM anfragen WHERE meldungsID = ?");
            deleteAnfrage.setInt(1, meldungsID);
            deleteAnfrage.execute();


            deleteMeldung = con.prepareStatement("DELETE FROM meldungen WHERE meldungsID = ?");
            deleteMeldung.setInt(1, meldungsID);
            deleteMeldung.execute();
        }     
    }
    
    /**
     * setzt eine meldung als gelesen
     * 
     * @param meldungsID id der meldung
     * @throws SQLException 
     */
    public void setMeldungenGelesen(int meldungsID) throws SQLException{
        PreparedStatement prepNimmtTeil = con.prepareStatement("UPDATE meldungen SET gelesen = ? WHERE meldungsID = ?");
        prepNimmtTeil.setInt(1, 1);
        prepNimmtTeil.setInt(2, meldungsID);
        prepNimmtTeil.execute();
    }
    
    /**
     * fügt einem termin einen teilnehmer (user) hinzu
     * 
     * @param terminID id des termins
     * @param username username des teilnehmers
     * @throws SQLException 
     */
    public void addTeilnehmer(int terminID, String username) throws SQLException{
        PreparedStatement statement = con.prepareStatement("INSERT INTO terminkalender values(?,?,?);");        
        statement.setInt(1, terminID);
        statement.setString(2, username);
        statement.setInt(3, 0);      
        statement.execute(); 
    }
    
    /**
     * Methode, die einen Termin für einen User auf seinem Client nicht mehr sichtbar macht
     * @param terminID
     * @param username
     * @throws SQLException 
     */
    public void removeAnfrageForUserByTerminID(int terminID, String username) throws SQLException{
        Statement state = con.createStatement();
        ResultSet resSet = state.executeQuery("Select * FROM meldungen JOIN anfragen ON " +
                "meldungen.meldungsID = anfragen.meldungsID " +
                "Where meldungen.username = \"" + username + "\" AND anfragen.terminID = " + terminID);
        
        if(resSet.next()){
            PreparedStatement removeStatement = con.prepareStatement("DELETE FROM meldungen WHERE meldungsID = ?;");      
            removeStatement.setInt(1, resSet.getInt("meldungsID"));
            removeStatement.execute(); 

            removeStatement = con.prepareStatement("DELETE FROM anfragen WHERE meldungsID = ?;");      
            removeStatement.setInt(1, resSet.getInt("meldungsID"));
            removeStatement.execute(); 
        }  
    }
    
    /**
     * Methode, die Teilnehmerliste eines Termins aktualisiert.
     * @param username name des Users bei dem die Teilnehmerliste aktualisiert
     * @param terminID Die Id des Termins der aktualisiert werden soll
     * @throws SQLException 
     */
    public void removeTeilnehmer(String username, int terminID) throws SQLException{
        PreparedStatement removeStatement = con.prepareStatement("DELETE FROM terminkalender WHERE username = ? AND terminID = ?");
        removeStatement.setString(1, username);
        removeStatement.setInt(2, terminID);
        removeStatement.execute();
        
        Statement state = con.createStatement();
        ResultSet resSet = state.executeQuery("Select * FROM meldungen JOIN  anfragen ON " +
                "meldungen.meldungsID = anfragen.meldungsID " +
                "Where meldungen.username = \"" + username + "\" AND anfragen.terminID = " + terminID);
        
        while(resSet.next()){
            removeStatement = con.prepareStatement("DELETE FROM meldungen WHERE meldungsID = ?;");      
            removeStatement.setInt(1, resSet.getInt("meldungsID"));
            removeStatement.execute(); 
            
            removeStatement = con.prepareStatement("DELETE FROM anfragen WHERE meldungsID = ?;");      
            removeStatement.setInt(1, resSet.getInt("meldungsID"));
            removeStatement.execute(); 
        }    
    }
    
    /**
     * Ändert die Farbe eines Users
     * 
     * @param color ist eine einzige Zahl, welche später zu einer Gruppe von Farben zugeordnet wird
     * @param userID ID des Users
     * @throws SQLException 
     */
    public void changeColor(int color, int userID) throws SQLException{
        PreparedStatement prepChangeColor = con.prepareStatement("UPDATE benutzer SET color = ? WHERE userID = ?");
        prepChangeColor.setInt(1, color);
        prepChangeColor.setInt(2, userID);
        prepChangeColor.execute();
    }
    
    /**
     * updatet einen Termin der durch flooding geändert wird
     * testet ob timestemp aktueller bzw. ob editorID kleiner als alte (bei gleichem timestemp)
     * 
     * @param termin
     * @throws SQLException 
     */
    public void updateTermin(Termin termin) throws SQLException{
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT * FROM termine " +
                "WHERE terminID = " + termin.getID());       

        res.next();
        //teste ob neuer timestemp größer als alter
        if(res.getInt("timestemp") < termin.getTimestemp() 
                || (res.getInt("timestemp") == termin.getTimestemp()
                && (res.getInt("editorID") >= termin.getEditorID()))){
            
            PreparedStatement statement = con.prepareStatement("UPDATE termine SET "
                    + "day = ?, "
                    + "month = ?, "
                    + "year = ?, "
                    + "from_hours = ?, "
                    + "from_minutes = ?, "
                    + "to_hours = ?, "
                    + "to_minutes = ?, "
                    + "note = ?, "
                    + "location = ?, "
                    + "editEveryone = ?, "
                    + "timestemp = ?, "
                    + "editorID = ?, " 
                    + "titel = ? "
                    + "WHERE terminID = ?");
            statement.setInt(1, termin.getDatum().getTag());
            statement.setInt(2, termin.getDatum().getMonat());
            statement.setInt(3, termin.getDatum().getJahr());
            statement.setInt(4, termin.getBeginn().getStunde());
            statement.setInt(5, termin.getBeginn().getMinute());
            statement.setInt(6, termin.getEnde().getStunde());
            statement.setInt(7, termin.getEnde().getMinute());
            statement.setString(8, termin.getNotiz());
            statement.setString(9, termin.getOrt());
            if(termin.getEditierbar()){
                statement.setInt(10, 1);
            }
            else{
                statement.setInt(10, 0);
            }          
            statement.setInt(11, termin.getTimestemp());
            statement.setInt(12, termin.getEditorID());
            statement.setString(13, termin.getTitel());
            statement.setInt(14, termin.getID());
            statement.execute(); 
            
        }
    }
    
    // ****************************** GETTER ****************************** //
    
    /**
     * gibt den aktuellen Wert des TerminIdCounter zurück
     * Dieser sollte als ID für einen neuen Termin verwendet werden. 
     * Er wird später beim hinzufügen des Termins in die DB inkrementiert.
     * 
     * @return 
     * @throws java.sql.SQLException 
     */
    public int getTerminIdCounter() throws SQLException{
        //get TerminID
        Statement state = con.createStatement();   
        ResultSet res = state.executeQuery("SELECT * FROM counters " +
                "WHERE reihe = " + 1); 
        
        res.next();           
        return res.getInt("terminCounter");
    }
    
    /**
     * gibt den meldungsCounter zurück und inkrementiert ihn danach um 1
     * 
     * @return
     * @throws SQLException 
     */
    public int getMeldungsCounter() throws SQLException{
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("Select * FROM counters " +
                "Where reihe = 1");
        res.next();
        
        PreparedStatement prepIncCounter = con.prepareStatement("UPDATE counters SET meldungsCounter = meldungsCounter + 1 WHERE reihe = 1");
        prepIncCounter.execute();
        
        return res.getInt("meldungsCounter");      
    }
    
    /**
     * gibt userIdCounter zurück
     * 
     * @return
     * @throws SQLException
     * @throws DatenbankException 
     */
    public int getUserCounter() throws SQLException, DatenbankException{
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("Select * FROM counters " +
                "Where reihe = 1");
        if(res.next()){
            return res.getInt("userCounter");
        }
        throw new DatenbankException("kein user Counter!!");
    }
    
    /**
     * Hilfsmethode um UserDaten zu bekommmen
     * 
     * @param userID id des users
     * @return
     * @throws SQLException 
     */
    private ResultSet getUserDetails(int userID) throws SQLException{
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("Select * FROM benutzer " +
                "Where userID = " + userID);
        return res;
    }   
    
    /**
     * Hilfsmethode um Termine eines Users zu bekommen
     * 
     * @param username name des Users
     * @return
     * @throws SQLException 
     */
    private LinkedList<Termin> getTermine(String username) throws SQLException {
        LinkedList<Termin> terminkalender = new LinkedList<>();   
        Statement state = con.createStatement();
        String teilnehmerUsername;
        boolean edit, stopLoop;
        ResultSet teilnehmerliste, termine = null;
                
        try {
            //Grunddaten jedes Termins des Users holen
            termine = state.executeQuery("SELECT * FROM termine " +
                    "JOIN terminkalender ON termine.terminID = terminkalender.terminID " +
                    "WHERE terminkalender.username = \"" + username + "\"");
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //hier werden die Terminliste erstellt
        while(termine != null && termine.next()){
            
            //gibt es einen User auf dem Servern, der an dem Termin teilnimmt? 
            //wenn ja: füge referenz hinzu
            teilnehmerliste = getTeilnehmerSet(termine.getInt("terminID"));
            stopLoop = false;
            /*
            while(teilnehmerliste.next() && !stopLoop){
                teilnehmerUsername = teilnehmerliste.getString("username");
                for(Sitzung sitzung : aktiveSitzungen){
                    if(sitzung.getEingeloggterBenutzer().getUsername().equals(teilnehmerUsername)){
                        try {
                            terminkalender.add(sitzung.getEingeloggterBenutzer().getTerminkalender().getTerminByID(termine.getInt("terminID")));
                            stopLoop = true;
                        } catch (TerminException ex) {
                            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }   */ 
            //wenn nein: erstelle neuen Termin
            if(!stopLoop){
                edit = termine.getInt("editEveryone") == 1;
                try {
                    //Erst die einfachen Datentypen und Klassen hinzufügen
                    terminkalender.add(new Termin(
                            new Datum(termine.getInt("day"), termine.getInt("month"), termine.getInt("year")),
                            new Zeit(termine.getInt("from_hours"), termine.getInt("from_minutes")),
                            new Zeit(termine.getInt("to_hours"), termine.getInt("to_minutes")),
                            termine.getString("titel"),
                            termine.getInt("terminID"),
                            termine.getString("ownername"),
                            termine.getString("location"),
                            termine.getString("note"),
                            edit));
                    //und nun die Teilnehmerliste
                    teilnehmerliste = getTeilnehmerSet(termine.getInt("terminID"));
                    while(teilnehmerliste.next()){
                        if(!termine.getString("ownername").equals(teilnehmerliste.getString("username"))){
                            teilnehmerUsername = teilnehmerliste.getString("username");
                            terminkalender.getLast().addTeilnehmer(teilnehmerUsername);
                            //falls der Teilnehmer zugesagt hat, muss das noch gesetzt werden
                            if(testUserNimmtTeil(teilnehmerliste.getString("username"), termine.getInt("terminID"))){
                                terminkalender.getLast().changeTeilnehmerNimmtTeil(teilnehmerUsername);
                            }
                        }
                    }
                } catch (TerminException | Datum.DatumException | Zeit.ZeitException ex) {
                    Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return terminkalender;
    }
      
    /**
     * Hilfsmethode um Kontaktliste eines users zu bekommen
     * 
     * @param userID id es users
     * @return
     * @throws SQLException
     * @throws DatenbankException 
     */
    private LinkedList<String> getKontaktliste(int userID) throws SQLException, DatenbankException{
        LinkedList<String> kontaktliste = new LinkedList<>();
        Statement state = con.createStatement();
        ResultSet resSet = state.executeQuery("Select * FROM kontaktliste " +
                "Where userID = " + userID);
        
        while(resSet.next()){
            kontaktliste.add(resSet.getString("kontaktname"));
        } 
        
        return kontaktliste;
    }
    
    /**
     * Hilfsmethode um Meldung eines users zu bekommen und in userobjekt zu schreiben
     * 
     * @param username name des users
     * @param benutzer user objekt in welches die meldungen eingefügt werden sollen
     * @return
     * @throws SQLException 
     */
    private LinkedList<Meldung> getMeldungen(String username, Benutzer benutzer) throws SQLException{
        LinkedList<Meldung> meldungen = new LinkedList<>();
        
        Statement state = con.createStatement();
        ResultSet resSet = state.executeQuery("Select * FROM meldungen " +
                "Where username = \"" + username + "\"");
        
        while(resSet.next()){
            if(resSet.getInt("anfrage") == 1){
                meldungen.add(getAnfrage(resSet.getString("text"), resSet.getInt("meldungsID"), benutzer));
            }
            else{
                meldungen.add(new Meldung(resSet.getString("text"), resSet.getInt("meldungsID")));                    
            } 
            if(resSet.getInt("gelesen") == 1){
                    meldungen.getLast().meldungGelesen();
            }  
        }
        return meldungen;
    }
    
    /**
     * Hilfsmethode um Anfrage eines users zu bekommen und in userobjekt zu schreiben
     * 
     * @param text text der anfrage
     * @param meldungsID id der meldung (welche eine anfrage ist)
     * @param benutzer benutzer objekt in welches geschrieben werden soll
     * @return
     * @throws SQLException 
     */
    private Anfrage getAnfrage(String text, int meldungsID, Benutzer benutzer) throws SQLException{
        Anfrage anfrage; 
        Termin termin;
        
        Statement state = con.createStatement();
        ResultSet resSet = state.executeQuery("Select * FROM anfragen " +
                "Where meldungsID = " + meldungsID);
        
        resSet.next();
        try {
            termin = benutzer.getTerminkalender().getTerminByID(resSet.getInt("terminID"));
            anfrage = new Anfrage(text, termin, resSet.getString("absender"), meldungsID);
            return anfrage;
        } catch (TerminException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * Methode um den Benutzer aus der DB zu lesen (komplett mit Terminen etc)
     * Ruft einige Hilfsmethoden auf!
     * 
     * @param userID id des users
     * @return
     * @throws SQLException
     * @throws DatenbankException 
     */
    public Benutzer getBenutzer(int userID) throws SQLException, DatenbankException{
        Benutzer benutzer;
        LinkedList<Termin> termine;
        LinkedList<String> kontakte;
        LinkedList<Meldung> meldungen;
        
        ResultSet user = getUserDetails(userID);
        
        if(user.next()){
            //erstmal alle einfachen Datentypen 
            benutzer = new Benutzer(
                    user.getString("username"), 
                    user.getString("password"), 
                    user.getString("email"), 
                    user.getInt("userID"), 
                    user.getString("name"), 
                    user.getString("lastname"), 
                    user.getInt("color")
            );
            //jetzt die Listen
            //1. Termine
            termine = getTermine(user.getString("username"));
            for(Termin termin : termine){
                benutzer.addTermin(termin);
            }
            //2. Meldung
            meldungen = getMeldungen(user.getString("username"), benutzer);
            benutzer.setMeldungen(meldungen);
            //3. Kontakte
            kontakte = getKontaktliste(userID);
            benutzer.setKontaktliste(kontakte);
           
            return benutzer;
        }
        
        throw new DatenbankException("user " + userID + " nicht in Datenbank vorhanden!");
    }

    /**
     * Methode um den Benutzer aus der DB zu lesen (komplett)
     * ermittelt die userID des users und ruft damit dann die andere getBenutzer-Methode auf
     * 
     * @param username username des zu ladenden users
     * @return
     * @throws SQLException
     * @throws DatenbankException 
     */
    public Benutzer getBenutzer(String username) throws SQLException, DatenbankException{
        int userID;
        Statement state = con.createStatement();
        
        ResultSet res = state.executeQuery("SELECT * FROM benutzer " +
                "WHERE username = \"" + username + "\"");       
        
        if(res.next()){
            userID = res.getInt("userID");
            return getBenutzer(userID);
        }
        
        throw new DatenbankException(username + " nicht in Datenbank vorhanden!");
    }
    
    /**
     * Methode zur Überprüfung der existenz eines Users
     * @param username name des Benutzers
     * @return true oder false
     * @throws SQLException 
     */
    public boolean userExists(String username) throws SQLException{
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT * FROM benutzer " +
                "WHERE username = \"" + username + "\"");       

        return res.next();
    }
    
    /**
     * Methode zur Überprüfung der existenz eines Users
     * @param userID id des users
     * @return true oder false
     * @throws SQLException 
     */
    public boolean userExists(int userID) throws SQLException{
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT * FROM benutzer " +
                "WHERE userID = \"" + userID + "\"");       

        return res.next();
    }
    
    /**
     * Methode zur Überprüfung der existenz eines termins
     * 
     * @param terminID id des termins
     * @return
     * @throws SQLException 
     */
    public boolean terminExists(int terminID) throws SQLException{
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT * FROM termine " +
                "WHERE terminID = \"" + terminID + "\"");       

        return res.next();
    }
    
    /**
     * gibt id eines users zurück
     * 
     * @param username username zu gesuchter userid
     * @return
     * @throws SQLException 
     */
    public int getUserID(String username) throws SQLException{
        Statement state = con.createStatement();

        ResultSet res = state.executeQuery("SELECT * FROM benutzer " +
                "WHERE username = \"" + username + "\"");       

        res.next();
        return res.getInt("userID");
    }
    
    /**
     * gibt username zu userID aus DB zurück
     * 
     * @param userID userId des users dessen username gesucht wird
     * @return 
     * @throws java.sql.SQLException 
     */
    public String getUsernameById(int userID) throws SQLException{
        Statement state = con.createStatement();

        ResultSet res = state.executeQuery("SELECT * FROM benutzer " +
                "WHERE userID = " + userID);       

        res.next();
        return res.getString("username");
    }
    
    /**
     * Methode um die Email Adresse eines zu Users zu bekommen
     * 
     * @param username username des users
     * @return
     * @throws SQLException 
     */
    public String getEmail(String username) throws SQLException{
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT * FROM benutzer " +
                "WHERE username = \"" + username + "\"");       
              
        res.next();
        return res.getString("email"); 
    }
    
    /** gibt das profil (username, email, vorname, nachname) in einer linkedlist zurück
     * 
     * @param userID id des gesuchten users
     * @return linkedlist mit benutzerinfos
     * @throws SQLException 
     */
    public LinkedList<String> getProfil(int userID) throws SQLException{
        LinkedList<String> profil = new LinkedList<>();
        
        Statement state = con.createStatement();
        ResultSet user = state.executeQuery("Select * FROM benutzer " +
                "Where userID = " + userID);
                
        user.next();
        //erstmal alle einfachen Datentypen 
        profil.add(user.getString("username"));
        profil.add(user.getString("email"));
        profil.add(user.getString("name"));
        profil.add(user.getString("lastname"));
            
        return profil;   
    }
    
    // ****************************** Hilfsmethoden ****************************** //
    
    /**
     * Hilfsmethode um Username anhand der UserID zu bekommen
     * @param userID
     * @return
     * @throws SQLException 
     */
    private String getUsernameByUserID(int userID) throws SQLException{
        String username;
        Statement state = con.createStatement();
        
        ResultSet res = state.executeQuery("SELECT * FROM benutzer " +
                "WHERE userID = " + userID); 
        
        if(res.next()){
            username = res.getString("username");
            return username;
        }
        return null; 
    }
    
    /**
     * Hilfsmethode um Teilnemer dem Termin hinzuzufügen 
     * @param terminID
     * @return
     * @throws SQLException 
     */
    private ResultSet getTeilnehmerSet(int terminID) throws SQLException{
        Statement state = con.createStatement();
        ResultSet resSet = state.executeQuery("Select * From terminkalender " +
                    "Where terminID = " + terminID);
        
        return resSet;
    }

    /**
     * Hilfsmethode um User am Termin teilnehmen zu lassen
     * @param username
     * @param terminID
     * @return
     * @throws SQLException 
     */
    private boolean testUserNimmtTeil(String username, int terminID) throws SQLException {
        Statement state = con.createStatement();
        ResultSet resSet = state.executeQuery("Select * From terminkalender " +
                    "Where terminID = " + terminID + " AND username = \"" + username + "\"");
        
        resSet.next();        
        return resSet.getInt("nimmtTeil") == 1;
    }
}
