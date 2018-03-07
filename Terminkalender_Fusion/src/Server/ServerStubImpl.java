package Server;

import Server.Threads.VerbindungstestsChildsThread;
import Server.Utilities.DatenbankException;
import Server.Utilities.ServerIdUndAnzahlUser;
import Server.Utilities.Sitzung;
import Server.Utilities.UserAnServer;
import Server.Utilities.Verbindung;
import Utilities.Benutzer;
import Utilities.BenutzerException;
import Utilities.Datum;
import Utilities.Teilnehmer;
import Utilities.Termin;
import Utilities.TerminException;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * Implementierung von Methoden aus der Interface ServerStub
 */
public class ServerStubImpl implements ServerStub {

    private final ServerDaten serverDaten;

    ServerStubImpl(ServerDaten serverDaten) {
        this.serverDaten = serverDaten;
    }

    /**
     * ändert die ID des Servers bei dem die Methode aufgerufen wird
     * wird verwendet um teilbaum neu zu strukturieren
     * 
     * @param newID
     * @throws RemoteException 
     */
    @Override
    public void setID(String newID) throws RemoteException{
        System.out.println("Neue ServerID zugewiesen: " + newID);
        ((RootServerDaten) this.serverDaten).primitiveDaten.serverID = newID;
        int counter = 0;
        for(Verbindung child : this.serverDaten.childConnection){
            child.getServerStub().setID(((RootServerDaten) this.serverDaten).primitiveDaten.serverID + counter);
            counter++;
        }
    }
    
    /**
     * gibt Server die IP-Adresse und den Port eines Servers mit dem er sich
     * verbinden soll dient der Erzeugung einer beidseitigen Verbindung /
     * ungerichteten Verbindung
     *
     * @param childIP
     * @return childID 
     * Die neue ID wird zurückgegeben
     * @throws RemoteException
     * @throws AccessException
     */
    @Override
    public String initConnection(String childIP) throws RemoteException {
        try {
            String childID = ((RootServerDaten) this.serverDaten).primitiveDaten.getNewChildId();
            
            //baut Verbindung zu Server auf
            Registry registry = LocateRegistry.getRegistry(childIP, 1100);
            ServerStub stub = (ServerStub) registry.lookup("ServerStub");
            Verbindung verbindung = new Verbindung(stub, childIP, childID);

            this.serverDaten.childConnection.add(verbindung);
            
            // Starte Thread, der die Verbindung zu anderen Servern testet
            new VerbindungstestsChildsThread(this.serverDaten, verbindung).start();

            //Ausgabe im Terminal
            System.out.println("LOG * ---> Verbindung zu KindServer: ID  " + childID + " hergestellt!");
              
            return childID;
        } catch (NotBoundException | IOException e) {
            System.out.println("LOG * ---> Verbindung zu KindServer Fehler!");
            return null;
        }
        
       
    }

    /**
     * Methode um zu testen, ob noch eine Verbindung zum Server besteht
     *
     * @param senderIP
     * @return result
     * @throws RemoteException
     */
    @Override
    public boolean ping(String senderIP) throws RemoteException {
        boolean result = false;

        for (Verbindung childConnection : this.serverDaten.childConnection) {
            if (childConnection != null && childConnection.getIP().equals(senderIP)) {
                result = true;
            }
        }
        if (this.serverDaten.parent != null
                && this.serverDaten.parent.getIP().equals(senderIP)) {
            result = true;
        }

        return result;
    }
    
    /**
     * Gibt die ID des Servers zurueck
     * 
     * @return serverID
     * @throws java.rmi.RemoteException
     */
    @Override
     public String getServerID() throws RemoteException{
        return ((RootServerDaten) this.serverDaten).primitiveDaten.serverID;         
    }
     
     /**
      * gibt die anzahl eingeloggter Benutzer des Server zurück
      * 
      * @return size
      * @throws RemoteException 
      */
    @Override
    public int getAnzahlUser() throws RemoteException{
        return ((ChildServerDaten) this.serverDaten).aktiveSitzungen.size();
    }      
    
    /**
     * suche server mit wenigstern usern und gib ip dessen zurück
     * 
     * @return minServerIP
     * @throws RemoteException 
     */
    @Override
    public ServerIdUndAnzahlUser findServerForUser() throws RemoteException{
        int tmp;
        int min = ((ChildServerDaten) this.serverDaten).aktiveSitzungen.size();
        String minServerIP = ((RootServerDaten) this.serverDaten).primitiveDaten.ownIP ;
        String serverID = ((RootServerDaten) this.serverDaten).primitiveDaten.serverID;
        
        //suche server mit wenigstern usern und gib ip dessen zurück
        for(Verbindung child : this.serverDaten.childConnection){
            tmp = child.getServerStub().getAnzahlUser();
            if(tmp < min){
                min = tmp;
                minServerIP = child.getIP();
                serverID = child.getID();
            }
        }
        
        return new ServerIdUndAnzahlUser(min, serverID, minServerIP);
    }
    
    /**
     * sucht den server mit der db eines bestimmten users und gibt die id des users zurück
     * 
     * @param username Username der gesucht wird
     * @return 
     * @throws RemoteException 
     * @throws java.sql.SQLException 
     */
    @Override
    public int findIdForUser(String username) throws RemoteException, SQLException{
        if(this.serverDaten instanceof RootServerDaten){
            return ((RootServerDaten) this.serverDaten).datenbank.getUserID(username);
        }
        else{
            return this.serverDaten.parent.getServerStub().findIdForUser(username);
        }
    }
    
    /**
     * sucht den server mit der db eines bestimmten users und gibt dessen Profil zurück
     * 
     * @param userID
     * @return userattribute als liste zurück
     * @throws RemoteException 
     * @throws java.sql.SQLException 
     */
    @Override
    public LinkedList<String> findUserProfil(int userID) throws RemoteException, SQLException{
        if(this.serverDaten instanceof RootServerDaten){
        //if(this.serverDaten.primitiveDaten.serverID.equals("0")){
            return ((RootServerDaten)this.serverDaten).datenbank.getProfil(userID);
        }
        else{
            return this.serverDaten.parent.getServerStub().findUserProfil(userID);
        }
    }
    
    /**
     * falls root: gibt userdaten zurück
     * sonst: frage parent nach userdaten
     * 
     * @param username
     * @return
     * @throws RemoteException
     * @throws SQLException
     * @throws DatenbankException 
     */
    @Override
    public Benutzer getUser(String username) throws RemoteException, SQLException, DatenbankException{
        if(this.serverDaten instanceof RootServerDaten){
            return ((RootServerDaten)this.serverDaten).datenbank.getBenutzer(username);
        }
        else{
            return this.serverDaten.parent.getServerStub().getUser(username);
        }
    }
      
    /**
     * falls root: entferne user aus UserAnServerListe
     * sonst: gebe an parent weiter
     * 
     * @param username
     * @throws RemoteException 
     * @throws Utilities.BenutzerException 
     */
    @Override
    public void removeUserFromRootList(String username) throws RemoteException, BenutzerException{
        if(this.serverDaten instanceof RootServerDaten){
            int index = -1; 
            int counter = 0;
            for(UserAnServer uas : ((RootServerDaten)this.serverDaten).userAnServerListe){
                if(uas.username.equals(username)){
                    //wenn ja, gibt ip dieses servers zurück
                    index = counter;
                }
                counter++;
            }
            if(index == -1){
                throw new BenutzerException("ClientStubImpl Line 179 index == -1 // username nicht in UserAnServerListe");
            }
            else{
                ((RootServerDaten)this.serverDaten).userAnServerListe.remove(index);
            }
        }
        else{
            this.serverDaten.parent.getServerStub().removeUserFromRootList(username);
        }
    }
    
    /**
     * Methode um das Passwort einen Users zurückzusetzen
     * 
     * @param passwort
     * @param username
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void changePasswort(String passwort, String username) throws RemoteException, SQLException{
        if(this.serverDaten instanceof RootServerDaten){
            ((RootServerDaten)this.serverDaten).datenbank.changePasswort(username, passwort);
        }
        else{
            this.serverDaten.parent.getServerStub().changePasswort(passwort, username);
        }           
    }
    
    /**
     *  Ändert den Vornamen eines Benutzers
     * 
     * @param vorname
     * @param username
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void changeVorname(String vorname, String username) throws RemoteException, SQLException{
        if(this.serverDaten instanceof RootServerDaten){
            ((RootServerDaten)this.serverDaten).datenbank.changeVorname(vorname, username);
        }
        else{
            this.serverDaten.parent.getServerStub().changeVorname(username, vorname);
        }           
    }
    
    /**
     * Ändert den Nachnamen eines Users
     * 
     * @param nachname
     * @param username
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void changeNachname(String nachname, String username) throws RemoteException, SQLException{
        if(this.serverDaten instanceof RootServerDaten){
            ((RootServerDaten)this.serverDaten).datenbank.changeNachname(nachname, username);
        }
        else{
            this.serverDaten.parent.getServerStub().changeNachname(nachname, username);
        }           
    }
    
    /**
     * Ändert die Email Adresse eines Benutzers
     * 
     * @param email
     * @param username
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void changeEmail(String email, String username) throws RemoteException, SQLException{
        if(this.serverDaten instanceof RootServerDaten){
            ((RootServerDaten)this.serverDaten).datenbank.changeEmail(email, username);
        }
        else{
            this.serverDaten.parent.getServerStub().changeEmail(email, username);
        }           
    }
    
    /**
     * Fügt dem User einen neuen Kontakt hinzu
     * Es wird getestet ob dieser vorhanden ist
     * 
     * @param kontaktname
     * @param userID
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void addKontakt(String kontaktname, int userID) throws RemoteException, SQLException{
        if(this.serverDaten instanceof RootServerDaten){
            ((RootServerDaten)this.serverDaten).datenbank.addKontakt(userID, kontaktname);
        }
        else{
            this.serverDaten.parent.getServerStub().addKontakt(kontaktname, userID);
        }    
    }
    
    /**
     * Entfernt einen Kontakt des Users
     * 
     * @param kontaktname
     * @param userID
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void removeKontakt(String kontaktname, int userID) throws RemoteException, SQLException{
        if(this.serverDaten instanceof RootServerDaten){
            ((RootServerDaten)this.serverDaten).datenbank.removeKontakt(userID, kontaktname);
        }
        else{
            this.serverDaten.parent.getServerStub().removeKontakt(kontaktname, userID);
        }    
    }
    
    /**
     * Löscht eine bestimmte Meldung eines Users
     * 
     * @param meldungsID
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void deleteMeldung(int meldungsID) throws RemoteException, SQLException{
        if(this.serverDaten instanceof RootServerDaten){
            ((RootServerDaten)this.serverDaten).datenbank.deleteMeldung(meldungsID);
        }
        else{
            this.serverDaten.parent.getServerStub().deleteMeldung(meldungsID);
        }    
    }
    
    /**
     * Setzt eine Meldung als gelesen
     * 
     * @param meldungsID
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void setMeldungenGelesen(int meldungsID) throws RemoteException, SQLException{
        if(this.serverDaten instanceof RootServerDaten){
            ((RootServerDaten)this.serverDaten).datenbank.setMeldungenGelesen(meldungsID);
        }
        else{
            this.serverDaten.parent.getServerStub().setMeldungenGelesen(meldungsID);
        }    
    }
    
    /**
     * fügt dem eingeloggten Benutzer den Termin mit den übergebenen Parametern hinzu
     * 
     * @param datum
     * @param beginn
     * @param ende
     * @param titel
     * @param userID
     * @return
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public int addNewTermin(Datum datum, Zeit beginn, Zeit ende, String titel, int userID) throws RemoteException, SQLException{
        if(this.serverDaten instanceof RootServerDaten){
            int terminID = ((RootServerDaten)this.serverDaten).datenbank.getTerminIdCounter();
            ((RootServerDaten)this.serverDaten).datenbank.addNewTermin(datum, beginn, ende, titel, userID, terminID);
            return terminID;           
        }
        else{
            return this.serverDaten.parent.getServerStub().addNewTermin(datum, beginn, ende, titel, userID);
        }
    }
    
   /**
    * Ändert die Editierrechte eines Termins in der Datenbank
    * 
    * @param termin
    * @param userID
    * @throws RemoteException
    * @throws SQLException
    * @throws BenutzerException 
    */
    @Override
    public void changeEditierrechteDB(Termin termin, int userID) throws RemoteException, SQLException, BenutzerException{
        if(this.serverDaten instanceof RootServerDaten){
            //trage aktuallisierte Daten ein
            ((RootServerDaten)serverDaten).datenbank.changeEditierrechte(termin.getEditierbar(), termin.getID());
            //erneure zeitstempel und editorID
            ((RootServerDaten)serverDaten).datenbank.incTimestemp(termin.getID());
            ((RootServerDaten)serverDaten).datenbank.updateEditorID(termin.getID(), userID);

            //für jeden Teilnehmer wird die Änderung an dessen Server geschickt
            for(Teilnehmer teilnehmer : termin.getTeilnehmerliste()){
                for(Verbindung child : this.serverDaten.childConnection){
                    try{ 
                        child.getServerStub().changeEditierrechte(termin, serverDaten.getServerIdByUsername(teilnehmer.getUsername()), teilnehmer.getUsername());
                    } catch (BenutzerException ex){}
                }
            }    
                      
        }
        else{
            this.serverDaten.parent.getServerStub().changeEditierrechteDB(termin, userID);
        }
    }
    
    /**
     * Ändert die Editierrechte eines Termins
     * 
     * @param termin
     * @param serverID
     * @param username
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void changeEditierrechte(Termin termin, String serverID, String username) throws RemoteException, SQLException{
        //ist man schon am richtigen server? (serverID gleich)
        if(serverID.equals(((RootServerDaten)serverDaten).primitiveDaten.serverID)){
            for(Sitzung sitzung : ((ChildServerDaten)serverDaten).aktiveSitzungen){
                if(sitzung.getEingeloggterBenutzer().getUsername().equals(username)){
                    try {
                        sitzung.getEingeloggterBenutzer().getTerminkalender().getTerminByID(termin.getID()).setEditierbar(termin.getEditierbar(), termin.getOwner());                   
                        sitzung.getEingeloggterBenutzer().getTerminkalender().getTerminByID(termin.getID()).incTimestemp();
                        sitzung.getEingeloggterBenutzer().getTerminkalender().getTerminByID(termin.getID()).setEditorID(sitzung.getEingeloggterBenutzer().getUserID());               
                    } catch (TerminException ex) {
                        Logger.getLogger(ServerStubImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }     
                }
            }            
        }
        //ist man auf dem richtigen weg? (serverID ersten x ziffern gleich)
        else if(serverID.startsWith(((RootServerDaten)serverDaten).primitiveDaten.serverID)){          
            for(Verbindung child : this.serverDaten.childConnection){
                child.getServerStub().changeEditierrechte(termin, serverID, username);
            }           
        }
    }
    
    /**
     * aktuallisiert einen Termin in der Datenbank
     * 
     * @param termin
     * @param userID
     * @throws RemoteException
     * @throws SQLException
     * @throws BenutzerException 
     */
    @Override
    public void changeTerminDB(Termin termin, int userID) throws RemoteException, SQLException, BenutzerException{
        if(this.serverDaten instanceof RootServerDaten){
            //trage aktuallisierte Daten ein
            ((RootServerDaten)serverDaten).datenbank.changeTerminbeginn(termin.getID(), termin.getBeginn());
            ((RootServerDaten)serverDaten).datenbank.changeTerminende(termin.getID(), termin.getEnde());
            ((RootServerDaten)serverDaten).datenbank.changeTerminnotiz(termin.getID(), termin.getNotiz());
            ((RootServerDaten)serverDaten).datenbank.changeTerminort(termin.getID(), termin.getOrt());
            ((RootServerDaten)serverDaten).datenbank.changeTermintitel(termin.getID(), termin.getTitel());
            ((RootServerDaten)serverDaten).datenbank.changeTermindatum(termin.getID(), termin.getDatum());
            //erneure zeitstempel und editorID
            ((RootServerDaten)serverDaten).datenbank.incTimestemp(termin.getID());
            ((RootServerDaten)serverDaten).datenbank.updateEditorID(termin.getID(), userID);

            //für jeden Teilnehmer wird die Änderung an dessen Server geschickt
            for(Teilnehmer teilnehmer : termin.getTeilnehmerliste()){
                for(Verbindung child : this.serverDaten.childConnection){
                    try {
                        child.getServerStub().updateTermin(termin, serverDaten.getServerIdByUsername(teilnehmer.getUsername()), teilnehmer.getUsername());
                    } catch (BenutzerException ex){}
                }
            }    
                      
        }
        else{
            this.serverDaten.parent.getServerStub().changeTerminDB(termin, userID);
        }
    }
    
    /**
     *  Methode, die einen gebenen Termin aktualisiert
     *  
     * @param termin Termin der zu updaten ist.
     * @param serverID
     * @param username
     * @throws RemoteException
     * @throws SQLException
     */
    @Override
    public void updateTermin(Termin termin, String serverID, String username) throws RemoteException, SQLException{
        //ist man schon am richtigen server? (serverID gleich)
        if(serverID.equals(((RootServerDaten)serverDaten).primitiveDaten.serverID)){
            for(Sitzung sitzung : ((ChildServerDaten)serverDaten).aktiveSitzungen){
                if(sitzung.getEingeloggterBenutzer().getUsername().equals(username)){
                    try {
                        //ändere Termin bei user (testet ob user editierrechte hat)
                        sitzung.getEingeloggterBenutzer().getTerminkalender().updateTermin(termin, termin.getOwner());
                        sitzung.getEingeloggterBenutzer().getTerminkalender().getTerminByID(termin.getID()).incTimestemp();
                        sitzung.getEingeloggterBenutzer().getTerminkalender().getTerminByID(termin.getID()).setEditorID(sitzung.getEingeloggterBenutzer().getUserID());          
                    } catch (TerminException ex) {
                        Logger.getLogger(ServerStubImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }     
                }
            }            
        }
        //ist man auf dem richtigen weg? (serverID ersten x ziffern gleich)
        else if(serverID.startsWith(((RootServerDaten)serverDaten).primitiveDaten.serverID)){          
            for(Verbindung child : this.serverDaten.childConnection){
                child.getServerStub().updateTermin(termin, serverID, username);
            }           
        }
    }
    
    /**
     * entfernt den termin als nicht Owner aus der Datenbank
     * 
     * @param termin
     * @param username
     * @param text
     * @throws RemoteException
     * @throws SQLException
     * @throws BenutzerException 
     */
    @Override
    public void deleteTerminNichtOwner(Termin termin, String username, String text) throws RemoteException, SQLException, BenutzerException{
        Meldung meldung;
        int meldungsID;
        
        if(this.serverDaten instanceof RootServerDaten){                      
            //suche in db nach termin           
            if(((RootServerDaten)serverDaten).datenbank.terminExists(termin.getID())){               
                //Entferne Teilnehmer von dem Termin aus DB
                ((RootServerDaten)serverDaten).datenbank.removeTeilnehmer(username, termin.getID());
                
                //jedem Teilnehmer des Termins wird der Teilnehmer aus dem Termin entfernt
                //und jeder bekommt eine Meldung dazu
                //die Meldung wird auch in der DB gespeichert
                for(Teilnehmer teilnehmer : termin.getTeilnehmerliste()){
                    meldungsID = serverDaten.datenbank.addMeldung(teilnehmer.getUsername(), text, false);
                    meldung = new Meldung(text, meldungsID);
                    
                    for(Verbindung child : this.serverDaten.childConnection){
                        try{
                            child.getServerStub().removeTeilnehmer(termin.getID(), teilnehmer.getUsername(), username, serverDaten.getServerIdByUsername(teilnehmer.getUsername()), meldung);
                        } catch (BenutzerException ex){}
                    }
                }                    
            }                     
        }
        else{
            this.serverDaten.parent.getServerStub().deleteTerminNichtOwner(termin, username, text);
        }
    }
    
    /**
     * entfernt den termin als Owner aus der Datenbank
     * 
     * @param termin
     * @param username
     * @param text
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void deleteTerminAlsOwner(Termin termin, String username, String text) throws RemoteException, SQLException{
        Meldung meldung;
        int meldungsID;
        
        if(this.serverDaten instanceof RootServerDaten){ 
            //suche in db nach termin           
            if(((RootServerDaten)serverDaten).datenbank.terminExists(termin.getID())){  
                //Entferne Teilnehmer von dem Termin aus DB
                ((RootServerDaten)serverDaten).datenbank.deleteTermin(termin.getID());
                //Entferne alle Anfragen zu dem Termin
                ((RootServerDaten)serverDaten).datenbank.deleteAnfrageByTerminID(termin.getID());
                
                //jedem Teilnehmer des Termins wird der Termin entfernt
                //und jeder bekommt eine Meldung dazu
                //die Meldung wird auch in der DB gespeichert
                for(Teilnehmer teilnehmer : termin.getTeilnehmerliste()){
                    meldungsID = ((RootServerDaten)serverDaten).datenbank.addMeldung(teilnehmer.getUsername(), text, false);
                    meldung = new Meldung(text, meldungsID);
                    
                    for(Verbindung child : this.serverDaten.childConnection){
                        try{
                            child.getServerStub().removeTermin(termin.getID(), teilnehmer.getUsername(), serverDaten.getServerIdByUsername(teilnehmer.getUsername()), meldung);
                        } catch (BenutzerException ex){}
                    }
                }                    
            }                     
        }
        else{
            this.serverDaten.parent.getServerStub().deleteTerminAlsOwner(termin, username, text);
        }
    }
    
    /**
     * Entfernt einen Teilnehmer aus der Datenbank
     * 
     * @param terminID
     * @param username
     * @param teilnehmer
     * @param serverID
     * @param meldung
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void removeTeilnehmer(int terminID, String username, String teilnehmer, String serverID, Meldung meldung) throws RemoteException, SQLException{
        //ist man schon am richtigen server? (serverID gleich)
        if(serverID.equals(((RootServerDaten)serverDaten).primitiveDaten.serverID)){            
            for(Sitzung sitzung : ((ChildServerDaten)serverDaten).aktiveSitzungen){
                if(sitzung.getEingeloggterBenutzer().getUsername().equals(username)){
                    try {                       
                        //fügt meldung hinzu
                        sitzung.getEingeloggterBenutzer().addMeldung(meldung);
                        //entfernt den Teilnehmer
                        sitzung.getEingeloggterBenutzer().getTerminkalender().getTerminByID(terminID).removeTeilnehmer(teilnehmer);
                    } catch (TerminException ex) {}     
                }
            }            
        }
        //ist man auf dem richtigen weg? (serverID ersten x ziffern gleich)
        else if(serverID.startsWith(((RootServerDaten)serverDaten).primitiveDaten.serverID)){          
            for(Verbindung child : this.serverDaten.childConnection){
                child.getServerStub().removeTeilnehmer(terminID, username, teilnehmer, serverID, meldung);
            }           
        }
    }
    
    /**
     * entfernt den termin mit angegebener id 
     * 
     * @param terminID
     * @param username
     * @param serverID
     * @param meldung
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void removeTermin(int terminID, String username, String serverID, Meldung meldung) throws RemoteException, SQLException{
        //ist man schon am richtigen server? (serverID gleich)
        if(serverID.equals(((RootServerDaten)serverDaten).primitiveDaten.serverID)){
            for(Sitzung sitzung : ((ChildServerDaten)serverDaten).aktiveSitzungen){
                if(sitzung.getEingeloggterBenutzer().getUsername().equals(username)){
                    try {
                        //entfernt den Teilnehmer
                        sitzung.getEingeloggterBenutzer().getTerminkalender().removeTerminByID(terminID);                       
                        //fügt meldung hinzu
                        sitzung.getEingeloggterBenutzer().addMeldung(meldung);
                        //entfernt die Anfrage zu dem Termin (evtl)
                        sitzung.getEingeloggterBenutzer().deleteAnfrage(terminID);
                    } catch (TerminException | BenutzerException ex) {}     
                }
            }            
        }
        //ist man auf dem richtigen weg? (serverID ersten x ziffern gleich)
        else if(serverID.startsWith(((RootServerDaten)serverDaten).primitiveDaten.serverID)){          
            for(Verbindung child : this.serverDaten.childConnection){
                child.getServerStub().removeTermin(terminID, username, serverID, meldung);
            }           
        }
    }
    
    /**
     * fügt einem Termin einen neuen Teilnehmer hinzu in der Datenbank
     * 
     * @param termin
     * @param username
     * @param einlader
     * @throws RemoteException
     * @throws SQLException
     * @throws BenutzerException 
     */
    @Override
    public void addTerminTeilnehmerDB(Termin termin, String username, String einlader) throws RemoteException, SQLException, BenutzerException{
        if(this.serverDaten instanceof RootServerDaten){
            if(((RootServerDaten)serverDaten).datenbank.userExists(username)){ 
                //suche in db nach termin           
                if(((RootServerDaten)serverDaten).datenbank.terminExists(termin.getID())){
                    //Füge dem Termin den neuen Teilnehmer in der DB hinzu
                    ((RootServerDaten)serverDaten).datenbank.addTeilnehmer(termin.getID(), username);
                    //jedem Teilnehmer des Termins wird der neue Teilnehmer dem Termin hinzugefügt
                    for(Teilnehmer teilnehmer : termin.getTeilnehmerliste()){
                        for(Verbindung child : this.serverDaten.childConnection){
                            try{   
                                child.getServerStub().addTeilnehmer(termin.getID(), teilnehmer.getUsername(), username, serverDaten.getServerIdByUsername(teilnehmer.getUsername()));
                            } catch (BenutzerException ex){}
                        }
                    }  
                    
                    String text = einlader + " lädt sie zu einem Termin am ";
                    Anfrage anfrage = new Anfrage(text, termin, einlader, ((RootServerDaten)this.serverDaten).datenbank.getMeldungsCounter());
                    //Füge der DB die Anfrage hinzu
                    ((RootServerDaten)serverDaten).datenbank.addAnfrage(username, termin.getID(), einlader, text);                   

                    //Füge dem neuen Teilnehmer
                    for(Verbindung child : this.serverDaten.childConnection){
                        try{
                            child.getServerStub().addTermin(anfrage, serverDaten.getServerIdByUsername(username), username);
                        } catch (BenutzerException ex){}
                    }                    
                }
            }              
        }
        else{
            this.serverDaten.parent.getServerStub().addTerminTeilnehmerDB(termin, username, einlader);
        }
    }
    
    
    /**
     * Methode fügt allen Teilnehmern des Termins den neuen Teilnehmer hinzu
     * 
     * @param terminID id des termins
     * @param username username des hinzuzufügenden teilnehmers
     * @param kontakt
     * @param serverID
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void addTeilnehmer(int terminID, String username, String kontakt, String serverID) throws RemoteException, SQLException{
        //ist man schon am richtigen server? (serverID gleich)
        if(serverID.equals(((RootServerDaten)serverDaten).primitiveDaten.serverID)){
            for(Sitzung sitzung : ((ChildServerDaten)serverDaten).aktiveSitzungen){
                if(sitzung.getEingeloggterBenutzer().getUsername().equals(username)){
                    try {
                        //ändere Termin bei user (testet ob user editierrechte hat)
                        sitzung.getEingeloggterBenutzer().getTerminkalender().getTerminByID(terminID).addTeilnehmer(kontakt);                        
                    } catch (TerminException ex) {
                        Logger.getLogger(ServerStubImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }     
                }
            }            
        }
        //ist man auf dem richtigen weg? (serverID ersten x ziffern gleich)
        else if(serverID.startsWith(((RootServerDaten)serverDaten).primitiveDaten.serverID)){          
            for(Verbindung child : this.serverDaten.childConnection){
                child.getServerStub().addTeilnehmer(terminID, username, kontakt, serverID);
            }           
        }
    }
    
    /**
     * fügt dem eingeloggten Benutzer den Termin 
     * 
     * @param serverID
     * @param username
     * @param anfrage Anfrage in Form einer Meldung
     * @throws RemoteException
     * @throws SQLException
     */
    @Override
    public void addTermin(Anfrage anfrage, String serverID, String username) throws RemoteException, SQLException{
        //ist man schon am richtigen server? (serverID gleich)
        if(serverID.equals(((RootServerDaten)serverDaten).primitiveDaten.serverID)){
            for(Sitzung sitzung : ((ChildServerDaten)serverDaten).aktiveSitzungen){
                if(sitzung.getEingeloggterBenutzer().getUsername().equals(username)){
                    //füge dem user den termin hinzu
                    sitzung.getEingeloggterBenutzer().getTerminkalender().addTermin(anfrage.getTermin());
                    try {
                        sitzung.getEingeloggterBenutzer().getTerminkalender().getTerminByID(anfrage.getTermin().getID()).addTeilnehmer(username);
                    } catch (TerminException ex) {
                        Logger.getLogger(ServerStubImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    //füge dem user die anfrage hinzu
                    sitzung.getEingeloggterBenutzer().addAnfrage(anfrage);     
                }
            }            
        }
        //ist man auf dem richtigen weg? (serverID ersten x ziffern gleich)
        else if(serverID.startsWith(((RootServerDaten)serverDaten).primitiveDaten.serverID)){          
            for(Verbindung child : this.serverDaten.childConnection){
                child.getServerStub().addTermin(anfrage, serverID, username);
            }           
        }
    }
      
    /**
     * Methode um den Status, ob ein User teilnimmt oder nicht, zu setzen
     * 
     * @param termin termin
     * @param username identifiziert den user
     * @param text
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void teilnehmerNimmtTeil(Termin termin, String username, String text) throws RemoteException, SQLException{
        Meldung meldung;
        int meldungsID;
        
        if(this.serverDaten instanceof RootServerDaten){                      
            //suche in db nach termin           
            if(((RootServerDaten)serverDaten).datenbank.terminExists(termin.getID())){               
                //Entferne Teilnehmer von dem Termin aus DB
                ((RootServerDaten)serverDaten).datenbank.nimmtTeil(termin.getID(), username);
                //Anfrage aus DB löschen
                ((RootServerDaten)serverDaten).datenbank.removeAnfrageForUserByTerminID(termin.getID(), username);
                
                //jedem Teilnehmer des Termins wird der Teilnehmer auf nimmt Teil gesetzt
                //und jeder bekommt eine Meldung dazu
                //die Meldung wird auch in der DB gespeichert
                for(Teilnehmer teilnehmer : termin.getTeilnehmerliste()){
                    meldungsID = serverDaten.datenbank.addMeldung(teilnehmer.getUsername(), text, false);
                    meldung = new Meldung(text, meldungsID);

                    for(Verbindung child : this.serverDaten.childConnection){
                        try{
                            child.getServerStub().setNimmtTeil(termin.getID(), teilnehmer.getUsername(), username, serverDaten.getServerIdByUsername(teilnehmer.getUsername()), meldung);
                        } catch (BenutzerException ex){}
                    }
                }                    
            }                     
        }
        else{
            this.serverDaten.parent.getServerStub().teilnehmerNimmtTeil(termin, username, text);
        }
    }
   
    /**
     * Setzt 1 fuer den User als Teilnehmer
     * 
     * @param terminID
     * @param username
     * @param teilnehmer
     * @param serverID
     * @param meldung
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void setNimmtTeil(int terminID, String username, String teilnehmer, String serverID, Meldung meldung) throws RemoteException, SQLException{
        //ist man schon am richtigen server? (serverID gleich)
        if(serverID.equals(serverDaten.primitiveDaten.serverID)){
            for(Sitzung sitzung : serverDaten.aktiveSitzungen){
                if(sitzung.getEingeloggterBenutzer().getUsername().equals(username)){
                    try {
                        //setzt teilnehmer nimmt teil
                        sitzung.getEingeloggterBenutzer().getTerminkalender().getTerminByID(terminID).changeTeilnehmerNimmtTeil(teilnehmer);
                        //fügt meldung hinzu
                        sitzung.getEingeloggterBenutzer().addMeldung(meldung);
                    } catch (TerminException ex) {
                        Logger.getLogger(ServerStubImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }     
                }
            }            
        }
        //ist man auf dem richtigen weg? (serverID ersten x ziffern gleich)
        else if(serverID.startsWith(((RootServerDaten)serverDaten).primitiveDaten.serverID)){          
            for(Verbindung child : this.serverDaten.childConnection){
                child.getServerStub().setNimmtTeil(terminID, username, teilnehmer, serverID, meldung);
            }           
        }
    }
}
