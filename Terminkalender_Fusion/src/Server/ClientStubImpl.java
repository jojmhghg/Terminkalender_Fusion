package Server;

import Server.Utilities.Verbindung;
import Server.Utilities.DatenbankException;
import Server.Utilities.EMailService;
import Server.Utilities.Sitzung;
import Server.Threads.FindIdForUserFloodingThread;
import Server.Threads.FindUserDataFlooding;
import Server.Threads.FindUserProfilFloodingThread;
import Utilities.Anfrage;
import Utilities.Benutzer;
import Utilities.BenutzerException;
import Utilities.Datum;
import Utilities.Meldung;
import Utilities.Teilnehmer;
import Utilities.Termin;
import Utilities.TerminException;
import Utilities.Zeit;
import java.rmi.RemoteException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nader
 */
public class ClientStubImpl implements ClientStub{

    //private final BenutzerListe benutzerliste;
    // Liste mit Benutzer + SitzungID
    private final ServerDaten serverDaten;
    
    public ClientStubImpl(ServerDaten serverDaten) throws SQLException, DatenbankException{
        this.serverDaten = serverDaten;
       // benutzerliste = new BenutzerListe(serverDaten.datenbank.getUserCounter());
    }

    /**
     * Methode zum Ausloggen eines Users
     * 
     * @param sitzungsID wird benötigt um die Sitzung zu identifizieren
     */
    @Override
    public void ausloggen(int sitzungsID){
        for(Sitzung sitzung : serverDaten.aktiveSitzungen){
            if(sitzung.compareWithSitzungsID(sitzungsID)){
                serverDaten.aktiveSitzungen.remove(sitzung);
            }
        }
    }
    
    /**
     * Methode um einen neuen User anzulegen
     * 
     * @param username username des neuen users
     * @param passwort passwort des neuen users
     * @param email email des neuen users
     * @throws BenutzerException 
     * @throws java.sql.SQLException 
     */
    @Override
    public void createUser(String username, String passwort, String email) throws BenutzerException, SQLException{
        //existiert User auf diesem Server oder auf anderem Server?      
        if(!findServerForUser(username).equals("false")){
            throw new BenutzerException("Benutzer existiert bereits!");
        }
        
        //lege User in DB an
        serverDaten.datenbank.addUser(username, passwort, email);
    }
    
    /**
     * suche server an dem die db des users liegt
     * 
     * @param username des einzuloggenden users
     * @return true, falls client am richtigen server, false falls server nicht
     * vorhanden, sonst die ip des servers an dem die db liegt
     * @throws SQLException 
     */
    @Override
    public String findServerForUser(String username) throws SQLException{
        if(serverDaten.datenbank.userExists(username)){
            return "true";          
        }
        //wenn user nicht vorhanden--> Flooding weiterleitung
        else{
            LinkedList<String> resultList = new LinkedList<>();
            int anzahlThreads = 0;
            for(Verbindung connection : serverDaten.connectionList){             
                new FindUserDataFlooding(resultList, this.serverDaten.primitiveDaten.ownIP, this.serverDaten.primitiveDaten.requestCounter, username, connection).start();
                anzahlThreads++;
            }
            this.serverDaten.incRequestCounter();
            while(resultList.size() < anzahlThreads){
                for(String result : resultList){
                    if(result != null && !result.equals("false")){
                        return result;
                    }
                }
                try {
                    Thread.sleep(30);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerStubImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            for(String result : resultList){
                if(result != null && !result.equals("false")){
                    return result;                   
                }              
            }            
        }
        return "false";
    }
    
    /**
     * Methode um einen User einzuloggen
     * Testet ob username & passwort stimmen
     * Client muss mit dem richtigen Server verbunden sein
     * 
     * @param username username des einzuloggenden users
     * @param passwort passwort des einzuloggenden users
     * @return gibt die SitzungsID oder -1 im Fehlerfall zurück
     * @throws Utilities.BenutzerException  
     * @throws java.sql.SQLException   
     * @throws Server.Utilities.DatenbankException   
     */
    @Override
    public int einloggen(String username, String passwort) throws BenutzerException, SQLException, DatenbankException{
        int sitzungsID = 10000000 * serverDaten.primitiveDaten.sitzungscounter + (int)(Math.random() * 1000000 + 1);
        serverDaten.primitiveDaten.sitzungscounter++;
        Benutzer user;
        
        try{
            //falls User bereits eingeloggt, dann wird das vorhandene user objekt benutzt
            user = istEingeloggt(username);
        }
        catch(BenutzerException ex){
            //falls user noch nicht eingeloggt, wird er aus der db geladen
            user = this.serverDaten.datenbank.getBenutzer(username);
        }       
        if(user.istPasswort(passwort)){
            serverDaten.aktiveSitzungen.add(new Sitzung(user, sitzungsID));
            return sitzungsID;
        }      
        //werfe fehler
        return -1;
    }
    
    /**
     * Methode um das Passwort einen Users zurückzusetzen
     * User bekommt das neue Passwort via Email gesendet
     * FindServerForUser() vorher aufrufen & mit Ergebnis verbinden!
     * 
     * @param username username des Users desssen Passwort zurückgesetzt werden soll
     * @throws BenutzerException
     * @throws SQLException 
     */
    @Override
    public void resetPassword(String username) throws BenutzerException, SQLException{         
        if(this.serverDaten.datenbank.userExists(username)){       
            String message;
            EMailService emailService = new EMailService();
            String allowedChars = "0123456789abcdefghijklmnopqrstuvwABCDEFGHIJKLMNOP!?";
            SecureRandom random = new SecureRandom();
            StringBuilder pass = new StringBuilder(10);

            //zufälliges Passwort generieren (10 Zeichen)
            for (int i = 0; i < 10; i++) {
                pass.append(allowedChars.charAt(random.nextInt(allowedChars.length())));
            }

            String passwort = pass.toString();

            //Sende email
            message = "Ihr neues Passwort lautet: " + passwort ;
            emailService.sendMail(serverDaten.datenbank.getEmail(username), "Terminkalender: Passwort zurückgesetzt", message);

            //aktuallisiere DB
            serverDaten.datenbank.changePasswort(passwort, username);
            try{
                Benutzer user = istEingeloggt(username);
                user.setPasswort(passwort);
            }
            catch(BenutzerException ex){ }
        }
        else{
            throw new BenutzerException("User existiert nicht!");
        }
    }
    
    /**
     * Methode gibt einen bestimmten Termin eines Users zurück
     * 
     * @param terminID bestimmt den Termin eindeutig
     * @param sitzungsID authentifiziert den client
     * @return
     * @throws BenutzerException 
     * @throws Utilities.TerminException 
     */
    @Override
    public Termin getTermin(int terminID, int sitzungsID) throws BenutzerException, TerminException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID); 
        return eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID);
    }
    
    
    
    /**
     * Legt einen neuen Termin an
     * 
     * @param termin termin der angelegt werden soll
     * @param sitzungsID authentifiziert den benutzer
     * @throws BenutzerException
     * @throws TerminException 
     * @throws java.sql.SQLException 
     */
    @Override
    public void addTermin(Termin termin, int sitzungsID) throws BenutzerException, TerminException, SQLException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        eingeloggterBenutzer.getTerminkalender().addTermin(termin);
        serverDaten.datenbank.addTerminToUser(termin.getID(), 0, eingeloggterBenutzer.getUsername());
    }
   
    /**
     * fügt dem eingeloggten Benutzer den Termin mit den übergebenen Parametern hinzu
     * 
     * @param datum datum des termins
     * @param beginn startzeit des termins
     * @param ende endzeit des termins
     * @param titel titel des termins
     * @param sitzungsID authentifiziert den benutzer
     * @throws BenutzerException
     * @throws TerminException 
     * @throws java.sql.SQLException 
     */
    @Override
    public void addTermin(Datum datum, Zeit beginn, Zeit ende, String titel, int sitzungsID) throws BenutzerException, TerminException, SQLException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);        
        int terminID = this.serverDaten.datenbank.getTerminIdCounter();
        eingeloggterBenutzer.addTermin(new Termin(datum, beginn, ende, titel, terminID, eingeloggterBenutzer.getUsername()));
        serverDaten.datenbank.addNewTermin(datum, beginn, ende, titel, eingeloggterBenutzer.getUserID(), terminID);
    }
    
    /**
     * entfernt den termin mit angegebener id
     * 
     * @param terminID zu entfernender termin
     * @param sitzungsID authentifiziert den benutzer
     * @throws BenutzerException 
     * @throws Utilities.TerminException 
     * @throws java.sql.SQLException 
     */
    @Override
    public void removeTermin(int terminID, int sitzungsID) throws BenutzerException, TerminException, SQLException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
           
        if (eingeloggterBenutzer.getUsername().equals(eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID).getOwner())) {

            String text = eingeloggterBenutzer.getUsername() 
                            + " hat den Termin '" 
                            + eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID).getTitel()
                            + "' am "
                            + eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID).getDatum().toString()
                            + " gelöscht";
            
            //Flooding weiterleitung
            int tmpRC1 = serverDaten.primitiveDaten.requestCounter;
            for (Verbindung connection : serverDaten.connectionList) {
                new Thread(() -> {
                    try {
                        connection.getServerStub().deleteTermin(serverDaten.primitiveDaten.ownIP, tmpRC1, eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID),text);
                    } catch (TerminException | RemoteException | SQLException ex) {
                        Logger.getLogger(ClientStubImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }).start();
            }
            serverDaten.primitiveDaten.requestCounter ++;
            
            //entferne Termin aus DB + alle Anfragen werden auf DB entfernt
            serverDaten.datenbank.deleteTermin(terminID);

            //für alle Teilnehmer des Termin...
            for (Teilnehmer teilnehmer : eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID).getTeilnehmerliste()){
                //... prüfe ob diese zu diesem Server gehören
                if(serverDaten.datenbank.userExists(teilnehmer.getUsername())){
                    //jeder bekommt eine Meldung, dass der Termin gelöscht wurde
                    int meldungsID = serverDaten.datenbank.addMeldung(teilnehmer.getUsername(), text, false);
                    //für alle eingeloggten User...
                    for(Sitzung sitzung : serverDaten.aktiveSitzungen){
                        //... teste ob diese Teilnehmer des Termins sind...
                        if(teilnehmer.getUsername().equals(sitzung.getEingeloggterBenutzer().getUsername())){
                            try {
                                //... wenn ja entferne den Termin aus dem Kaldender auf dem Server
                                sitzung.getEingeloggterBenutzer().getTerminkalender().removeTerminByID(terminID);
                                //und füge ihm eine Meldung auf dem Server hinzu
                                sitzung.getEingeloggterBenutzer().addMeldung(new Meldung(text, meldungsID));
                                //und entferne falls vorhanden die Anfrage zu diesem Termin
                                sitzung.getEingeloggterBenutzer().deleteAnfrage(terminID);
                            }catch (TerminException ex) {}

                        }
                    }
                }
            }
            
        }
        else{          
            String text = eingeloggterBenutzer.getUsername() 
                            + " nimmt nicht mehr an dem  Termin '" 
                            + eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID).getTitel()
                            + "' am "
                            + eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID).getDatum().toString()
                            + " teil";
            
            //Flooding weiterleitung
            int tmpRC1 = serverDaten.primitiveDaten.requestCounter;
            for (Verbindung connection : serverDaten.connectionList) {
                new Thread(() -> {
                    try {
                        connection.getServerStub().removeTeilnehmerFromTermin(serverDaten.primitiveDaten.ownIP, tmpRC1,
                                eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID),eingeloggterBenutzer.getUsername(), eingeloggterBenutzer.getUserID());
                    } catch (TerminException | RemoteException | SQLException ex) {
                        Logger.getLogger(ClientStubImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }).start();
            }
            serverDaten.primitiveDaten.requestCounter ++;
               
            serverDaten.datenbank.removeTeilnehmer(eingeloggterBenutzer.getUsername(), terminID);
            for (Teilnehmer teilnehmer : eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID).getTeilnehmerliste()){
                if(serverDaten.datenbank.userExists(teilnehmer.getUsername())){
                    int meldungsID = serverDaten.datenbank.addMeldung(teilnehmer.getUsername(), text, false);
                    for(Sitzung sitzung : serverDaten.aktiveSitzungen){
                        if(teilnehmer.getUsername().equals(sitzung.getEingeloggterBenutzer().getUsername())){
                            try {
                                sitzung.getEingeloggterBenutzer().addMeldung(new Meldung(text, meldungsID));
                                sitzung.getEingeloggterBenutzer().getTerminkalender().getTerminByID(terminID).removeTeilnehmer(eingeloggterBenutzer.getUsername());                              
                            }catch (TerminException ex) {}
                        }
                    }
                }
            }
            eingeloggterBenutzer.getTerminkalender().removeTerminByID(terminID);
            //und entferne falls vorhanden die Anfrage zu diesem Termin
            try{
                eingeloggterBenutzer.deleteAnfrage(terminID);
            } catch (IndexOutOfBoundsException e){}
        }  
    }
    
    /**
     * Ändert die Editierrechte eines Termins
     * 
     * @param termin
     * @param sitzungsID authentifiziert den benutzer
     * @throws TerminException 
     * @throws Utilities.BenutzerException 
     * @throws java.sql.SQLException 
     */
    @Override
    public void changeEditierrechte(Termin termin, int sitzungsID) throws TerminException, BenutzerException, SQLException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
 
        //ändere Termin bei user (testet ob user editierrechte hat)
        eingeloggterBenutzer.getTerminkalender().getTerminByID(termin.getID()).setEditierbar(termin.getEditierbar(), eingeloggterBenutzer.getUsername());
        eingeloggterBenutzer.getTerminkalender().getTerminByID(termin.getID()).incTimestemp();
        eingeloggterBenutzer.getTerminkalender().getTerminByID(termin.getID()).setEditorID(eingeloggterBenutzer.getUserID());
        
        //trage aktuallisierte Daten ein
        serverDaten.datenbank.changeEditierrechte(termin.getEditierbar(), termin.getID());
        //erneure zeitstempel und editorID
        serverDaten.datenbank.incTimestemp(termin.getID());
        serverDaten.datenbank.updateEditorID(termin.getID(), eingeloggterBenutzer.getUserID());
        
        //teste ob weitere Benutzer am Termin teilnehmen
        if(termin.getTeilnehmerliste().size() > 1){
            int tmpRC1 = this.serverDaten.primitiveDaten.requestCounter;
            //Flooding weiterleitung
            for(Verbindung connection : serverDaten.connectionList){             
                new Thread(() ->{
                    try {
                        connection.getServerStub().changeEditierrechte(serverDaten.primitiveDaten.ownIP, tmpRC1, eingeloggterBenutzer.getTerminkalender().getTerminByID(termin.getID()));
                    } catch (RemoteException | SQLException | TerminException ex) { }
                }).start();
            }     
            this.serverDaten.incRequestCounter();
        }  
    }
    
    /**
     * ändert das termindatum
     * 
     * @param terminID bestimmt den termin
     * @param neuesDatum bestimmt das neue datum
     * @param sitzungsID authentifiziert den benutzer
     * @throws BenutzerException 
     * @throws TerminException 
     * @throws java.sql.SQLException 
     */
    
    public void changeTermindatum(int terminID, Datum neuesDatum, int sitzungsID) throws BenutzerException, TerminException, SQLException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID).setDatum(neuesDatum, eingeloggterBenutzer.getUsername());
        serverDaten.datenbank.changeTermindatum(terminID, neuesDatum);
    }
    
    /**
     * ändert den beginn (uhrzeit) eines termins
     * 
     * @param terminID bestimmt den termin
     * @param neuerBeginn neue startzeit
     * @param sitzungsID authentifiziert den benutzer
     * @throws BenutzerException
     * @throws TerminException 
     * @throws java.sql.SQLException 
     */
    
    public void changeTerminbeginn(int terminID, Zeit neuerBeginn, int sitzungsID) throws BenutzerException, TerminException, SQLException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID).setBeginn(neuerBeginn, eingeloggterBenutzer.getUsername());
        serverDaten.datenbank.changeTerminbeginn(terminID, neuerBeginn);
    }
    
    /**
     * Ändert das Ende (Uhrzeit) eines Termins
     * 
     * @param terminID bestimmt termin eindeutig
     * @param neuesEnde neue endzeit
     * @param sitzungsID authentifiziert den benutzer
     * @throws BenutzerException 
     * @throws TerminException 
     * @throws java.sql.SQLException 
     */
    
    public void changeTerminende(int terminID, Zeit neuesEnde, int sitzungsID) throws BenutzerException, TerminException, SQLException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID).setEnde(neuesEnde, eingeloggterBenutzer.getUsername());
        serverDaten.datenbank.changeTerminende(terminID, neuesEnde);
    }
    
    /**
     * Ändert die Terminnotiz eines Termins
     * 
     * @param terminID bestimmt Termin eindeutig
     * @param neueNotiz neue Notiz
     * @param sitzungsID authentifiziert den benutzer
     * @throws BenutzerException 
     * @throws TerminException 
     * @throws java.sql.SQLException 
     */
    
    public void changeTerminnotiz(int terminID, String neueNotiz, int sitzungsID) throws BenutzerException, TerminException, SQLException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID).setNotiz(neueNotiz, eingeloggterBenutzer.getUsername());
        serverDaten.datenbank.changeTerminnotiz(terminID, neueNotiz);
    }
    
    /**
     * Ändert den Titel eines Termins
     * 
     * @param terminID bestimmt Termin eindeutig
     * @param neuerTitel neuer Titel
     * @param sitzungsID authentifiziert den benutzer
     * @throws BenutzerException 
     * @throws TerminException 
     * @throws java.sql.SQLException 
     */
    
    public void changeTermintitel(int terminID, String neuerTitel, int sitzungsID) throws BenutzerException, TerminException, SQLException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID).setTitel(neuerTitel, eingeloggterBenutzer.getUsername());
        serverDaten.datenbank.changeTermintitel(terminID, neuerTitel);
    }
    
    /**
     * Ändert den Ort eines Termins
     * 
     * @param terminID bestimmt Termin eindeutig
     * @param neuerOrt neuer Ort des Termins
     * @param sitzungsID authentifiziert den benutzer
     * @throws BenutzerException 
     * @throws TerminException 
     * @throws java.sql.SQLException 
     */
    
    public void changeTerminort(int terminID, String neuerOrt, int sitzungsID) throws BenutzerException, TerminException, SQLException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID).setOrt(neuerOrt, eingeloggterBenutzer.getUsername());
        serverDaten.datenbank.changeTerminort(terminID, neuerOrt);
    }
    
    /**
     * aktuallisiert einen Termin
     * 
     * @param termin id des termins
     * @param sitzungsID authentifiziert den benutzer
     * @throws BenutzerException
     * @throws TerminException
     * @throws SQLException 
     */
    @Override
    public void changeTermin(Termin termin, int sitzungsID) throws BenutzerException, TerminException, SQLException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
 
        //ändere Termin bei user (testet ob user editierrechte hat)
        eingeloggterBenutzer.getTerminkalender().updateTermin(termin, eingeloggterBenutzer.getUsername());
        eingeloggterBenutzer.getTerminkalender().getTerminByID(termin.getID()).incTimestemp();
        eingeloggterBenutzer.getTerminkalender().getTerminByID(termin.getID()).setEditorID(eingeloggterBenutzer.getUserID());
        
        //trage aktuallisierte Daten ein
        serverDaten.datenbank.changeTerminbeginn(termin.getID(), termin.getBeginn());
        serverDaten.datenbank.changeTerminende(termin.getID(), termin.getEnde());
        serverDaten.datenbank.changeTerminnotiz(termin.getID(), termin.getNotiz());
        serverDaten.datenbank.changeTerminort(termin.getID(), termin.getOrt());
        serverDaten.datenbank.changeTermintitel(termin.getID(), termin.getTitel());
        serverDaten.datenbank.changeTermindatum(termin.getID(), termin.getDatum());
        //erneure zeitstempel und editorID
        serverDaten.datenbank.incTimestemp(termin.getID());
        serverDaten.datenbank.updateEditorID(termin.getID(), eingeloggterBenutzer.getUserID());
        
        //teste ob weitere Benutzer am Termin teilnehmen
        if(termin.getTeilnehmerliste().size() > 1){
            //Flooding weiterleitung
            int tmpRC1 = this.serverDaten.primitiveDaten.requestCounter;
            for(Verbindung connection : serverDaten.connectionList){             
                new Thread(() ->{
                    try {
                        connection.getServerStub().updateTermin(serverDaten.primitiveDaten.ownIP, tmpRC1, eingeloggterBenutzer.getTerminkalender().getTerminByID(termin.getID()));
                    } catch (RemoteException | SQLException | TerminException ex) { }
                }).start();
            }     
            this.serverDaten.incRequestCounter();
        }              
    }
    
    /**
     * fügt einem Termin einen neuen Teilnehmer hinzu
     * 
     * @param terminID bestimmt Termin eindeutig
     * @param username username des neuen Teilnehmers
     * @param sitzungsID authentifiziert den benutzer
     * @throws BenutzerException 
     * @throws Utilities.TerminException 
     * @throws java.sql.SQLException 
     */
    @Override
    public void addTerminteilnehmer(int terminID, String username, int sitzungsID) throws BenutzerException, TerminException, SQLException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);    
        String text = eingeloggterBenutzer.getUsername() + " lädt sie zu einem Termin am ";
        
        //Schritt 1: Teilnehmer zu Termin hinzufügen
        //teste ob user existiert & speichere seine id für später
        int kontaktID = findUserID(username);

        //suche in db nach termin           
        if(serverDaten.datenbank.terminExists(terminID)){
            serverDaten.datenbank.addTeilnehmer(terminID, username);
            eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID).addTeilnehmer(username);
        }
        //Flooding weiterleitung
        int tmpRQ1 = this.serverDaten.primitiveDaten.requestCounter;
        for(Verbindung connection : serverDaten.connectionList){             
            new Thread(() ->{
                try { 
                    connection.getServerStub().addTeilnehmer(serverDaten.primitiveDaten.ownIP, tmpRQ1, terminID, kontaktID, username);
                } catch (RemoteException | SQLException ex) { }
            }).start();
        }  
        this.serverDaten.incRequestCounter();
        //Schritt 2: Dem Teilnehmer den Termin & eine Anfrage hinzufügen
        Anfrage anfrage = new Anfrage(text, eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID), eingeloggterBenutzer.getUsername(), this.serverDaten.datenbank.getMeldungsCounter());

        //ist kontakt auf eigener db?
        if(serverDaten.datenbank.userExists(kontaktID)){ 
            //füge die anfrage der db hinzu
            serverDaten.datenbank.addAnfrage(username, anfrage.getTermin().getID(), eingeloggterBenutzer.getUsername(), anfrage.getText());

            //suche auf server nach dem user
            for(Sitzung sitzung : serverDaten.aktiveSitzungen){                   
                if(sitzung.getEingeloggterBenutzer().getUserID() == kontaktID){
                    //füge dem user den termin hinzu
                    sitzung.getEingeloggterBenutzer().getTerminkalender().addTermin(anfrage.getTermin());
                    //füge dem user die anfrage hinzu
                    sitzung.getEingeloggterBenutzer().addAnfrage(anfrage);
                }     
            }
        }    
        //fall nicht:
        else{
            int tmpRQ2 = this.serverDaten.primitiveDaten.requestCounter;
            //Flooding weiterleitung
            for(Verbindung connection : serverDaten.connectionList){  
                new Thread(() ->{
                    try {
                        connection.getServerStub().addTermin(this.serverDaten.primitiveDaten.ownIP, tmpRQ2, kontaktID, anfrage, eingeloggterBenutzer.getUsername());
                    } catch (RemoteException | SQLException ex) { }
                }).start();
            } 
            this.serverDaten.incRequestCounter();  
        }
    }
    
    /**
     * Nimmt eine Terminanfrage an
     * 
     * @param terminID bestimmt den termin der angefragt wurde
     * @param sitzungsID authentifiziert den benutzer
     * @throws TerminException
     * @throws BenutzerException 
     * @throws java.sql.SQLException 
     */
    @Override
    public void terminAnnehmen(int terminID, int sitzungsID) throws TerminException, BenutzerException, SQLException{
        //Ist Sitzungsid gültig? Wenn ja, lade user dazu
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        
        //Setze 'User nimmt Teil' auf Server
        eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID).changeTeilnehmerNimmtTeil(eingeloggterBenutzer.getUsername());
        //Setze 'User nimmt Teil' auf DB
        serverDaten.datenbank.nimmtTeil(terminID, eingeloggterBenutzer.getUsername());
        
        String meldungstext= eingeloggterBenutzer.getUsername() 
                            + " nimmt an dem Termin '" 
                            + eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID).getTitel()
                            + "' am "
                            + eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID).getDatum().toString()
                            + " teil";
        
        Termin termin = eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID);
        //für jeden teilnehmer des termins
        for (Teilnehmer teilnehmer : termin.getTeilnehmerliste()){
            //ist teilnehmer auf db?
            if(serverDaten.datenbank.userExists(teilnehmer.getUsername()) && !teilnehmer.getUsername().equals(eingeloggterBenutzer.getUsername())){
                //generiere dem Teilnehmer eine meldung
                int meldungsID = serverDaten.datenbank.addMeldung(teilnehmer.getUsername(), meldungstext, false);
                //suche auf server nach dem termin
                for(Sitzung sitzung : serverDaten.aktiveSitzungen){    
                    if(sitzung.getEingeloggterBenutzer().getUsername().equals(teilnehmer.getUsername())){
                        try {
                            //test ob eingeloggter user zu termin eingeladen ist oder daran teilnimmt
                            sitzung.getEingeloggterBenutzer().getTerminkalender().getTerminByID(termin.getID());
                            //füge den anderen teilnehmern die eingeloggt sind die meldung hinzu
                            sitzung.getEingeloggterBenutzer().addMeldung(new Meldung(meldungstext, meldungsID));
                        } catch (TerminException ex) { }  
                    }                   
                }
            }
        }
        
        //Flooding weiterleitung
        int tmpRC1 = serverDaten.primitiveDaten.requestCounter;
        for(Verbindung connection : serverDaten.connectionList){             
            new Thread(() ->{
                try {
                    connection.getServerStub().teilnehmerChangeStatus(serverDaten.primitiveDaten.ownIP, tmpRC1, termin, eingeloggterBenutzer.getUsername(), true, meldungstext);
                } catch (RemoteException | SQLException ex) { }
            }).start();
        } 
        serverDaten.incRequestCounter();   
        eingeloggterBenutzer.deleteAnfrage(terminID);
    }
     
    /**
     * Lehnt einen Termin ab
     * 
     * @param terminID bestimmt Termin der abzulehnen ist 
     * @param sitzungsID authentifiziert den benutzer
     * @throws TerminException
     * @throws BenutzerException 
     * @throws java.sql.SQLException 
     */
    @Override
    public void terminAblehnen(int terminID, int sitzungsID) throws TerminException, BenutzerException, SQLException{
        //Ist Sitzungsid gültig? Wenn ja, lade user dazu
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        
        //Lösche Eintrag des Users in Tabelle Terminkalener aus DB
        serverDaten.datenbank.removeTeilnehmer(eingeloggterBenutzer.getUsername(), terminID);
           
        String meldungstext = eingeloggterBenutzer.getUsername() 
                        + " hat den Termin '" 
                        + eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID).getTitel()
                        + "' am "
                        + eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID).getDatum().toString()
                        + " abgelehnt";
        
        Termin termin = eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID);
        //für jeden teilnehmer des termins
        for (Teilnehmer teilnehmer : termin.getTeilnehmerliste()){
            //ist teilnehmer auf db?
            if(serverDaten.datenbank.userExists(teilnehmer.getUsername()) && !teilnehmer.getUsername().equals(eingeloggterBenutzer.getUsername())){
                //generiere dem Teilnehmer eine meldung
                int meldungsID = serverDaten.datenbank.addMeldung(teilnehmer.getUsername(), meldungstext, false);
                //suche auf server nach dem termin
                for(Sitzung sitzung : serverDaten.aktiveSitzungen){    
                    if(sitzung.getEingeloggterBenutzer().getUsername().equals(teilnehmer.getUsername())){
                        try {
                        //test ob eingeloggter user zu termin eingeladen ist oder daran teilnimmt
                        sitzung.getEingeloggterBenutzer().getTerminkalender().getTerminByID(termin.getID());
                        //füge den anderen teilnehmern die eingeloggt sind die meldung hinzu
                        sitzung.getEingeloggterBenutzer().addMeldung(new Meldung(meldungstext, meldungsID));
                    } catch (TerminException ex) { } 
                    }                    
                }
            }
        }
        
        int tmpRC1 = serverDaten.primitiveDaten.requestCounter;
        //Flooding weiterleitung
        for(Verbindung connection : serverDaten.connectionList){             
            new Thread(() ->{
                try {
                    connection.getServerStub().teilnehmerChangeStatus(serverDaten.primitiveDaten.ownIP, tmpRC1, termin, eingeloggterBenutzer.getUsername(), false, meldungstext);
                } catch (RemoteException | SQLException ex) { }
            }).start();
        } 
        serverDaten.incRequestCounter();
        
        //Lösche Termin des Users auf Server
        eingeloggterBenutzer.deleteAnfrage(terminID);
        eingeloggterBenutzer.getTerminkalender().removeTerminByID(terminID);
    }
    
    /**
     * Ändert das Passwort eines Users
     * 
     * @param altesPW altes Passwort zur Sicherheit
     * @param neuesPW neues Passwort
     * @param sitzungsID authentifiziert den benutzer
     * @throws BenutzerException 
     * @throws java.sql.SQLException 
     */
    @Override
    public void changePasswort(String altesPW, String neuesPW, int sitzungsID) throws BenutzerException, SQLException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        if(!eingeloggterBenutzer.istPasswort(altesPW)){
            throw new BenutzerException("altes Passwort war falsch!");
        }
        eingeloggterBenutzer.setPasswort(neuesPW);
        serverDaten.datenbank.changePasswort(neuesPW, eingeloggterBenutzer.getUsername());
    }
    
    /**
     * Ändert den Vornamen eines Benutzers
     * 
     * @param neuerVorname neuer Vorname des Users
     * @param sitzungsID authentifiziert den benutzer
     * @throws BenutzerException 
     * @throws java.sql.SQLException 
     */
    @Override
    public void changeVorname(String neuerVorname, int sitzungsID) throws BenutzerException, SQLException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        eingeloggterBenutzer.setVorname(neuerVorname);
        serverDaten.datenbank.changeVorname(neuerVorname, eingeloggterBenutzer.getUserID());
    }
    
    /**
     * Ändert den Nachnamen eines Users
     * 
     * @param neuerNachname neuer Nachname
     * @param sitzungsID authentifiziert den benutzer
     * @throws BenutzerException 
     * @throws java.sql.SQLException 
     */
    @Override
    public void changeNachname(String neuerNachname, int sitzungsID) throws BenutzerException, SQLException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        eingeloggterBenutzer.setNachname(neuerNachname);
        serverDaten.datenbank.changeNachname(neuerNachname, eingeloggterBenutzer.getUserID());
    }
    
    /**
     * Ändert die Email Adresse eines Benutzers
     * 
     * @param neueEmail neue Email Adresse eines benutzers
     * @param sitzungsID authentifiziert den benutzer
     * @throws BenutzerException 
     * @throws java.sql.SQLException 
     */
    @Override
    public void changeEmail(String neueEmail, int sitzungsID) throws BenutzerException, SQLException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        eingeloggterBenutzer.setEmail(neueEmail);
        serverDaten.datenbank.changeEmail(neueEmail, eingeloggterBenutzer.getUserID());
    }
    
    /**
     * Fügt dem User einen neuen Kontakt hinzu
     * Es wird getestet ob dieser vorhanden ist
     * 
     * @param username username des neuen Kontakts
     * @param sitzungsID authentifiziert den benutzer
     * @throws Utilities.BenutzerException 
     * @throws java.sql.SQLException 
     */
    @Override
    public void addKontakt(String username, int sitzungsID) throws BenutzerException, SQLException{       
        int kontaktID = findUserID(username);
        
        if(kontaktID == -1){
            throw new BenutzerException("Username: " + username + " nicht vorhanden!");
        }
        
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);    
        eingeloggterBenutzer.addKontakt(username);
        serverDaten.datenbank.addKontakt(eingeloggterBenutzer.getUserID(), username);
    }

    /**
     * Entfernt einen Kontakt des Users
     * 
     * @param username username des zu entfernenden Kontaktes
     * @param sitzungsID authentifiziert den benutzer
     * @throws BenutzerException 
     * @throws java.sql.SQLException 
     */
    @Override
    public void removeKontakt(String username, int sitzungsID) throws BenutzerException, SQLException{
        int kontaktID = findUserID(username); 
        if(kontaktID == -1){
            throw new BenutzerException(username + " nicht in deiner Liste vorhanden!");
        }
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        eingeloggterBenutzer.removeKontakt(username);
        serverDaten.datenbank.removeKontakt(eingeloggterBenutzer.getUserID(), username);
    }
    
    /**
     * Gibt die Kontaktliste eines Users zurück
     * 
     * @param sitzungsID authentifiziert den benutzer
     * @return Kontaktliste des eingeloggten Benutzer
     * @throws BenutzerException
     */
    @Override
    public LinkedList<String> getKontakte(int sitzungsID) throws BenutzerException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        return eingeloggterBenutzer.getKontaktliste();
    }
   
    /**
     * Gibt den Username eines Users zurück
     * 
     * @param sitzungsID authentifiziert den benutzer
     * @return den eigenen Usernamen 
     * @throws BenutzerException 
     */
    @Override
    public String getUsername(int sitzungsID) throws BenutzerException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        return eingeloggterBenutzer.getUsername();
    }
    
    /**
     * Gibt den Vornamen eines Users zurück
     * 
     * @param sitzungsID authentifiziert den benutzer
     * @return vorname des Users
     * @throws BenutzerException 
     */
    @Override
    public String getVorname(int sitzungsID) throws BenutzerException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        return eingeloggterBenutzer.getVorname();
    }
    
    /**
     * Gibt den Nachnamen eines Users zurück
     * 
     * @param sitzungsID authentifiziert den benutzer
     * @return Nachname des Benutzers
     * @throws BenutzerException 
     */
    @Override
    public String getNachname(int sitzungsID) throws BenutzerException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        return eingeloggterBenutzer.getNachname();
    }
    
    /**
     * Gibt die Email Adresse eines Users zurück
     * 
     * @param sitzungsID authentifiziert den benutzer
     * @return
     * @throws BenutzerException 
     */
    @Override
    public String getEmail(int sitzungsID) throws BenutzerException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        return eingeloggterBenutzer.getEmail();
    }
    
    /**
     * Gibt die Termine eines Users in einer bestimmten Kalenderwoche zurück
     * 
     * @param kalenderwoche bestimmt Kalenderwoche 
     * @param jahr bestimmt das Jahr
     * @param sitzungsID authentifiziert den benutzer
     * @return
     * @throws BenutzerException 
     */
    @Override
    public LinkedList<Termin> getTermineInKalenderwoche(int kalenderwoche, int jahr, int sitzungsID) throws BenutzerException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        return eingeloggterBenutzer.getTerminkalender().getTermineInWoche(kalenderwoche, jahr);
    }
    
    /**
     * Gibt Termine an einem bestimmten Tag zurück
     * 
     * @param datum bestimmt den Tag
     * @param sitzungsID authentifiziert den benutzer
     * @return
     * @throws TerminException
     * @throws BenutzerException 
     */
    @Override
    public LinkedList<Termin> getTermineAmTag(Datum datum, int sitzungsID) throws TerminException, BenutzerException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        return eingeloggterBenutzer.getTerminkalender().getTermineAmTag(datum);
    }
    
    /**
     * Gibt alle Termine in einem bestimmten Monat zurück
     * 
     * @param monat bestimmt den Monat
     * @param jahr bestimmt das Jahr
     * @param sitzungsID authentifiziert den benutzer
     * @return
     * @throws BenutzerException
     * @throws TerminException 
     */
    @Override
    public LinkedList<Termin> getTermineInMonat(int monat, int jahr, int sitzungsID) throws BenutzerException, TerminException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        return eingeloggterBenutzer.getTerminkalender().getTermineImMonat(monat, jahr);
    }

    /**
     * gibt alle Meldung eines Users zurück
     * 
     * @param sitzungsID authentifiziert den benutzer
     * @return
     * @throws BenutzerException 
     */
    @Override
    public LinkedList<Meldung> getMeldungen(int sitzungsID) throws BenutzerException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        return eingeloggterBenutzer.getMeldungen();
    }
    
    /**
     * Löscht eine bestimmte Meldung eines Users
     * 
     * @param meldungsID bestimmt welche Meldung gelöscht werden soll
     * @param sitzungsID authentifiziert den benutzer
     * @throws BenutzerException 
     * @throws java.sql.SQLException 
     */
    @Override
    public void deleteMeldung(int meldungsID, int sitzungsID) throws BenutzerException, SQLException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);       
        serverDaten.datenbank.deleteMeldung(meldungsID);
        eingeloggterBenutzer.deleteMeldung(meldungsID);
    }
    
    /**
     * Setzt eine Meldung als gelesen
     * 
     * @param meldungsID liest die richtige Meldung aus
     * @param sitzungsID authentifiziert den benutzer
     * @throws BenutzerException 
     * @throws java.sql.SQLException 
     */
    @Override
    public void setMeldungenGelesen(int meldungsID, int sitzungsID) throws BenutzerException, SQLException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        serverDaten.datenbank.setMeldungenGelesen(meldungsID);
        LinkedList<Meldung> meldungen = eingeloggterBenutzer.getMeldungen();
        for(Meldung meldung : meldungen){
            if(meldungsID == meldung.meldungsID){
                meldung.meldungGelesen();
                break;
            }     
        }
    }
    
    /**
     * gibt das Profil eines Users zurück
     * 
     * @param username bestimmt den User
     * @return
     * @throws BenutzerException 
     * @throws java.sql.SQLException 
     */
    @Override
    public LinkedList<String> getProfil(String username) throws BenutzerException, SQLException{
        LinkedList<String> profil = new LinkedList<>();

        int userID = findUserID(username);
        
        //suche in db nach username
        if(serverDaten.datenbank.userExists(username)){
            profil = serverDaten.datenbank.getProfil(userID);
        }
        //wenn user nicht vorhanden--> Flooding weiterleitung
        else{
            LinkedList<LinkedList<String>> resultList = new LinkedList<>();
            int anzahlThreads = 0;
            for(Verbindung connection : serverDaten.connectionList){             
                new FindUserProfilFloodingThread(resultList, serverDaten.primitiveDaten.ownIP, serverDaten.primitiveDaten.requestCounter, userID, connection).start();
                anzahlThreads++;
            }
            this.serverDaten.incRequestCounter();
            while(resultList.size() < anzahlThreads){
                for(LinkedList<String> result : resultList){
                    if(result.size() > 0){
                        profil = result;
                    }
                }
                try {
                    Thread.sleep(30);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerStubImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            for(LinkedList<String> result : resultList){
                if(result.size() > 0){
                    profil = result;                   
                }              
            } 
        }
        
        if(profil.size() > 0){
            return profil;
        }
        else{
            throw new BenutzerException("Username: " + username + " existiert nicht!");
        }
    }
    
    
    // ----------------------------------- Hilfsmethoden ----------------------------------- //

    /**
     * Testet ob ein User eingeloggt ist
     * 
     * @param sitzungsID authentifiziert den benutzer
     * @return UserID
     * @throws BenutzerException 
     */
    private Benutzer istEingeloggt(int sitzungsID) throws BenutzerException {
        for(Sitzung sitzung : serverDaten.aktiveSitzungen){
            if(sitzung.compareWithSitzungsID(sitzungsID)){
                return sitzung.getEingeloggterBenutzer();
            }
        }
        throw new BenutzerException("ungültige Sitzungs-ID");
    }
    
    /**
     * Testet ob ein User eingeloggt ist
     * 
     * @param sitzungsID authentifiziert den benutzer
     * @return
     * @throws BenutzerException 
     */
    private Benutzer istEingeloggt(String username) throws BenutzerException {
        for(Sitzung sitzung : serverDaten.aktiveSitzungen){
            if(sitzung.getEingeloggterBenutzer().getUsername().equals(username)){
                return sitzung.getEingeloggterBenutzer();
            }
        }
        throw new BenutzerException("ungültige Sitzungs-ID");
    }
    
    /**
     * Methode die die UserID eines Users durch bekannten username
     * 
     * @param username
     * @return userID 
     * @throws SQLException
     * @throws BenutzerException 
     */
    private int findUserID(String username) throws SQLException, BenutzerException{
        int userID = -1;
        //ist kontakt auf db des selben servers hinterlegt?
        if(serverDaten.datenbank.userExists(username)){
            userID = serverDaten.datenbank.getUserID(username);
        }
        //wenn user nicht vorhanden--> Flooding weiterleitung
        else{
            LinkedList<Integer> resultList = new LinkedList<>();
            int anzahlThreads = 0;
            for(Verbindung connection : serverDaten.connectionList){  
                new FindIdForUserFloodingThread(resultList, serverDaten.primitiveDaten.ownIP, serverDaten.primitiveDaten.requestCounter, username, connection).start();
                anzahlThreads++;
            }
            this.serverDaten.incRequestCounter();  
            while(resultList.size() < anzahlThreads){
                for(int result : resultList){
                    if(result >= 0){
                        userID = result;
                    }
                }
                try {
                    Thread.sleep(30);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerStubImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            for(int result : resultList){
                if(result != -1){
                    userID = result;                   
                }              
            }   
        }
        if(userID == -1){
           throw new BenutzerException("Username: " + username + " existiert nicht"); 
        }
        return userID;
    }
     
}
