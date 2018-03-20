package Server;

import Server.Threads.FindIdForUserFloodingThread;
import Server.Threads.FindUserDataFlooding;
import Server.Threads.FindUserProfilFloodingThread;
import Server.Threads.VerbindungstestsChildsThread;
import Server.Threads.VerbindungstestsThread;
import Server.Utilities.DatenbankException;
import Server.Utilities.ServerIdUndAnzahlUser;
import Server.Utilities.Sitzung;
import Server.Utilities.UserAnServer;
import Server.Utilities.Verbindung;
import Utilities.Anfrage;
import Utilities.Benutzer;
import Utilities.BenutzerException;
import Utilities.Datum;
import Utilities.Meldung;
import Utilities.Teilnehmer;
import Utilities.Termin;
import Utilities.TerminException;
import Utilities.Zeit;
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
        this.serverDaten.primitiveDaten.serverID = newID;
        int counter = 0;
        for(Verbindung child : this.serverDaten.childConnection){
            child.getServerStub().setID(this.serverDaten.primitiveDaten.serverID + counter);
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
    public String initConnectionToChild(String childIP) throws RemoteException {
        try {
            String childID = this.serverDaten.primitiveDaten.getNewChildId();
            
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
     * gibt Server die IP-Adresse und den Port eines Servers mit dem er sich verbinden soll
     * dient der Erzeugung einer beidseitigen Verbindung / ungerichteten Verbindung
     * 
     * @param ip
     * @return 
     * @throws RemoteException
     * @throws AccessException 
     */
    @Override
    public boolean initConnectionP2P(String ip) throws RemoteException{           
        try {    
            //baut Verbindung zu Server auf
            Registry registry = LocateRegistry.getRegistry(ip, 1100);
            ServerStub stub = (ServerStub) registry.lookup("ServerStub");
            
            //fügt Verbindung zur Liste der Verbindungen hinzu
            Verbindung verbindung = new Verbindung(stub, ip, "0");
            ((RootServerDaten)this.serverDaten).connectionList.add(verbindung);
            
            //Starte Threads, die die Verbindung zu anderen Servern testen
            new VerbindungstestsThread(this.serverDaten, verbindung).start();
            
            //Ausgabe im Terminal            
            System.out.println("Dauerhafte Verbindung zu Root-Server " + ip + " hergestellt!");       
            
            return true;
        } catch (NotBoundException | IOException e) {
            return false;
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
        
        if(this.serverDaten instanceof RootServerDaten){
            for(Verbindung verbindung : ((RootServerDaten)this.serverDaten).connectionList){
                if(verbindung.getIP().equals(senderIP)){
                    result = true;
                }
            }
        }
        else{
            result = ((ChildServerDaten)this.serverDaten).parent.getIP().equals(senderIP);

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
        return this.serverDaten.primitiveDaten.serverID;         
    }
     
     /**
      * gibt die anzahl eingeloggter Benutzer des Server zurück
      * 
      * @return size
      * @throws RemoteException 
      */
    @Override
    public int getAnzahlUser() throws RemoteException{
        if(this.serverDaten instanceof ChildServerDaten){
            return ((ChildServerDaten) this.serverDaten).aktiveSitzungen.size();
        }
        else{
            return -1;
        }
    }      
    
    /**
     * suche server mit wenigsten usern und gib ip dessen zurück
     * 
     * @return minServerIP
     * @throws RemoteException 
     */
    @Override
    public ServerIdUndAnzahlUser findServerWithLeastUsers() throws RemoteException{
        int tmp;
        int min = ((ChildServerDaten) this.serverDaten).aktiveSitzungen.size();
        String minServerIP = this.serverDaten.primitiveDaten.ownIP ;
        String serverID = this.serverDaten.primitiveDaten.serverID;
        
        //suche server mit wenigstern usern und gib ip dessen zurück
        for(Verbindung child : this.serverDaten.childConnection){
            tmp = child.getServerStub().getAnzahlUser();
            if(tmp < min && tmp > 0){
                min = tmp;
                minServerIP = child.getIP();
                serverID = child.getID();
            }
        }
        
        return new ServerIdUndAnzahlUser(min, serverID, minServerIP);
    }
    
    /**
     * sucht den server mit der db eines bestimmten users und gibt ip des servers zurueck
     * 
     * @param username Username der gesucht wird
     * @param originIP Ip-Adresse des Initialen senders
     * @param requestCounter counter der die Anfrage eindeitig identifitiert
     * @return 
     * @throws RemoteException 
     * @throws java.sql.SQLException 
     */
    @Override
    public String findServerWithDbForUser(String originIP, int requestCounter, String username) throws RemoteException, SQLException{
        // war die Anfrage schonmal hier
        if(!checkRequest(originIP, requestCounter) && !originIP.equals(this.serverDaten.primitiveDaten.ownIP)){
            //suche in db nach username, falls vorhanden, suche dort nach server mit wenigsten usern
            if(((RootServerDaten)this.serverDaten).datenbank.userExists(username)){
                ServerIdUndAnzahlUser tmp;
                ServerIdUndAnzahlUser min = this.serverDaten.childConnection.getFirst().getServerStub().findServerWithLeastUsers();  

                //teste ob user schon irgendwo eingeloggt
                for(UserAnServer uas : ((RootServerDaten)this.serverDaten).userAnServerListe){
                    if(uas.username.equals(username)){
                        //wenn ja, gibt ip dieses servers zurück
                        return uas.serverIP;
                    }
                }

                //suche server mit wenigstern usern und gib ip dessen zurück
                for(Verbindung child : this.serverDaten.childConnection){
                    if(!this.serverDaten.childConnection.getFirst().equals(child)){
                        tmp = child.getServerStub().findServerWithLeastUsers();
                        if(tmp.anzahlUser < min.anzahlUser){
                            min = tmp;
                        }
                    }           
                }

                ((RootServerDaten)this.serverDaten).userAnServerListe.add(new UserAnServer(min.serverID, username, min.serverIP));
                return min.serverIP;
            }
            //wenn user nicht vorhanden--> Flooding weiterleitung
            else{
                LinkedList<String> resultList = new LinkedList<>();
                int anzahlThreads = 0;
                for(Verbindung connection : ((RootServerDaten)this.serverDaten).connectionList){    
                    new FindUserDataFlooding(resultList, originIP, requestCounter,username,connection).start();
                    anzahlThreads++;
                }
                while(resultList.size() < anzahlThreads){
                    for(String result : resultList){
                        if(!result.isEmpty()){
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
        }
        return "false";
    }
    
    /**
     *
     * @param username
     * @throws Utilities.BenutzerException
     * @throws java.rmi.RemoteException
     */
    @Override
    public void removeAusUserAnServerListe(String username) throws BenutzerException, RemoteException{
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
            ((ChildServerDaten)this.serverDaten).parent.getServerStub().removeUserFromRootList(username);
        }
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
            if(((RootServerDaten) this.serverDaten).datenbank.userExists(username)){
                return ((RootServerDaten) this.serverDaten).datenbank.getUserID(username);
            }
            else{
                int tmpRC1 = this.serverDaten.primitiveDaten.requestCounter;
                ((RootServerDaten)this.serverDaten).incRequestCounter();
                LinkedList<Integer> resultList = new LinkedList<>();
                int anzahlThreads = 0;
                for(Verbindung connection : ((RootServerDaten)this.serverDaten).connectionList){             
                    new FindIdForUserFloodingThread(resultList, this.serverDaten.primitiveDaten.ownIP, tmpRC1, username, connection).start();
                    anzahlThreads++;
                }
                while(resultList.size() < anzahlThreads){
                    for(int result : resultList){
                        if(result >= 0){
                            return result;
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
                        return result;                   
                    }              
                } 
                return -1;
            }
        }
        else{
            return ((ChildServerDaten)this.serverDaten).parent.getServerStub().findIdForUser(username);
        }
    }
    
    /**
     * sucht den server mit der db eines bestimmten users und gibt die id des users zurück
     * 
     * @param username Username der gesucht wird
     * @param originIP Ip-Adresse des Initialen senders
     * @param requestCounter counter der die Anfrage eindeitig identifitiert
     * @return 
     * @throws RemoteException 
     * @throws java.sql.SQLException 
     */
    @Override
    public int findIdForUserRoots(String originIP,int requestCounter, String username) throws RemoteException, SQLException{       
        if(!checkRequest(originIP, requestCounter) && !originIP.equals(this.serverDaten.primitiveDaten.ownIP)){
            //suche in db nach username
            if(((RootServerDaten)this.serverDaten).datenbank.userExists(username)){
                return ((RootServerDaten)this.serverDaten).datenbank.getUserID(username);
            }
            //wenn user nicht vorhanden--> Flooding weiterleitung
            else{
                LinkedList<Integer> resultList = new LinkedList<>();
                int anzahlThreads = 0;
                for(Verbindung connection : ((RootServerDaten)this.serverDaten).connectionList){             
                    new FindIdForUserFloodingThread(resultList, originIP, requestCounter,username,connection).start();
                    anzahlThreads++;
                }
                while(resultList.size() < anzahlThreads){
                    for(int result : resultList){
                        if(result >= 0){
                            return result;
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
                        return result;                   
                    }              
                }  
            }
        }
        return -1;
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
            if(((RootServerDaten)this.serverDaten).datenbank.userExists(userID)){
                return ((RootServerDaten)this.serverDaten).datenbank.getProfil(userID);
            }
            else{
                int tmpRC1 = this.serverDaten.primitiveDaten.requestCounter;
                ((RootServerDaten)this.serverDaten).incRequestCounter();
                LinkedList<LinkedList<String>> resultList = new LinkedList<>();
                int anzahlThreads = 0;
                for(Verbindung connection : ((RootServerDaten)this.serverDaten).connectionList){             
                    new FindUserProfilFloodingThread(resultList, this.serverDaten.primitiveDaten.ownIP, tmpRC1, userID, connection).start();
                    anzahlThreads++;
                }
                while(resultList.size() < anzahlThreads){
                    for(LinkedList<String> result : resultList){
                        if(result.size() > 0){
                            return result;
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
                        return result;                   
                    }              
                } 
                return new LinkedList<>();
            }
        }
        else{
            return ((ChildServerDaten)this.serverDaten).parent.getServerStub().findUserProfil(userID);
        }
    }
    
    /**
     * sucht den server mit der db eines bestimmten users und gibt dessen Profil zurück
     * 
     * @param userID id des gesuchten users
     * @param originIP Ip-Adresse des Initialen senders
     * @param requestCounter counter der die Anfrage eindeitig identifitiert
     * @return userattribute als liste zurück
     * @throws RemoteException 
     * @throws java.sql.SQLException 
     */
    @Override
    public LinkedList<String> findUserProfilRoots(String originIP, int requestCounter, int userID) throws RemoteException, SQLException{
        // war die Anfrage schonmal hier
        if(!checkRequest(originIP, requestCounter) && !originIP.equals(this.serverDaten.primitiveDaten.ownIP)){
            //suche in db nach username
            if(((RootServerDaten)this.serverDaten).datenbank.userExists(userID)){
                return ((RootServerDaten)this.serverDaten).datenbank.getProfil(userID);
            }
            //wenn user nicht vorhanden--> Flooding weiterleitung
            else{
                LinkedList<LinkedList<String>> resultList = new LinkedList<>();
                int anzahlThreads = 0;
                for(Verbindung connection : ((RootServerDaten)this.serverDaten).connectionList){             
                    new FindUserProfilFloodingThread(resultList, originIP, requestCounter,userID,connection).start();
                    anzahlThreads++;
                }
                while(resultList.size() < anzahlThreads){
                    for(LinkedList<String> result : resultList){
                        if(result.size() > 0){
                            return result;
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
                        return result;                   
                    }              
                } 
            }
        }
        return new LinkedList<>();
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
            return ((ChildServerDaten)this.serverDaten).parent.getServerStub().getUser(username);
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
            ((ChildServerDaten)this.serverDaten).parent.getServerStub().removeUserFromRootList(username);
        }
    }
    
    /**
     * Methode um das Passwort einen Users in der DB des Root zu ändern
     * 
     * @param passwort
     * @param username
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void changePasswort(String passwort, String username) throws RemoteException, SQLException{
        //wird methode an root aufgerufen?
        if(this.serverDaten instanceof RootServerDaten){
            ((RootServerDaten)this.serverDaten).datenbank.changePasswort(username, passwort);
        }
        //falls nicht, wird aufruf an root weitergeleitet
        else{
            ((ChildServerDaten)this.serverDaten).parent.getServerStub().changePasswort(passwort, username);
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
            ((ChildServerDaten)this.serverDaten).parent.getServerStub().changeVorname(username, vorname);
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
            ((ChildServerDaten)this.serverDaten).parent.getServerStub().changeNachname(nachname, username);
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
            ((ChildServerDaten)this.serverDaten).parent.getServerStub().changeEmail(email, username);
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
            ((ChildServerDaten)this.serverDaten).parent.getServerStub().addKontakt(kontaktname, userID);
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
            ((ChildServerDaten)this.serverDaten).parent.getServerStub().removeKontakt(kontaktname, userID);
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
            ((ChildServerDaten)this.serverDaten).parent.getServerStub().deleteMeldung(meldungsID);
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
            ((ChildServerDaten)this.serverDaten).parent.getServerStub().setMeldungenGelesen(meldungsID);
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
            return ((ChildServerDaten)this.serverDaten).parent.getServerStub().addNewTermin(datum, beginn, ende, titel, userID);
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
    public void changeEditierrechte(Termin termin, int userID) throws RemoteException, SQLException, BenutzerException{
        //ist server ein root server?
        if(this.serverDaten instanceof RootServerDaten){
      
        /* --- falls mehr als ein teilnehmer am termin teilnimmt, dann wird die änderung an alle root-server-nachbarn weitergesendet --- */
        
            if(termin.getTeilnehmerliste().size() > 1){
                int tmpRC1 = this.serverDaten.primitiveDaten.requestCounter;
                //Flooding weiterleitung
                for(Verbindung connection : ((RootServerDaten)this.serverDaten).connectionList){             
                    new Thread(() ->{
                        try {
                            connection.getServerStub().changeEditierrechteRoots(this.serverDaten.primitiveDaten.ownIP, tmpRC1, termin, userID);
                        } catch (RemoteException | SQLException ex) { }
                    }).start();
                }     
                ((RootServerDaten)this.serverDaten).incRequestCounter();
            }
            
        /* --- ändere Daten auf DB des Servers --- */
                       
            //trage aktuallisierte Daten ein
            ((RootServerDaten)serverDaten).datenbank.changeEditierrechte(termin.getEditierbar(), termin.getID());
            //erneure zeitstempel und editorID
            ((RootServerDaten)serverDaten).datenbank.incTimestemp(termin.getID());
            ((RootServerDaten)serverDaten).datenbank.updateEditorID(termin.getID(), userID);
          
        /* --- ändere Daten auf den child-servern --- */
        
            //mit dieser Liste merkt man sich serverIDs die bereits einen änderungsaufruf bekommen
            //so werden nicht dem selben server mehrere änderungen geschickt
            LinkedList<String> bereitsAngesteuerteServer = new LinkedList<>();
            //für jeden Teilnehmer wird die Änderung an dessen Server geschickt
            for(Teilnehmer teilnehmer : termin.getTeilnehmerliste()){
                for(Verbindung child : this.serverDaten.childConnection){
                    try{ 
                        String serverID = ((RootServerDaten)this.serverDaten).getServerIdByUsername(teilnehmer.getUsername());
                        if(!bereitsAngesteuerteServer.contains(serverID)){
                            child.getServerStub().changeEditierrechteChilds(termin, serverID, teilnehmer.getUsername());
                            bereitsAngesteuerteServer.add(serverID);
                        }
                        
                    } catch (BenutzerException ex){}
                }
            }   
                          
        }
        //wenn nicht, leite an dessen parten weiter (bis man an root ankommt)
        else{
            ((ChildServerDaten)this.serverDaten).parent.getServerStub().changeEditierrechte(termin, userID);
        }
    }
    
    /**
     * 
     * @param originIP
     * @param requestCounter
     * @param termin
     * @param userID
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void changeEditierrechteRoots(String originIP, int requestCounter, Termin termin, int userID) throws RemoteException, SQLException{
        // war die Anfrage schonmal hier
        if(!checkRequest(originIP, requestCounter) && !originIP.equals(this.serverDaten.primitiveDaten.ownIP)){
        
        /* --- änderung wird an alle root-server-nachbarn weitergesendet --- */
            //Flooding weiterleitung
            for(Verbindung connection : ((RootServerDaten)this.serverDaten).connectionList){             
                new Thread(() ->{
                    try {
                        connection.getServerStub().changeEditierrechteRoots(originIP, requestCounter, termin, userID);
                    } catch (RemoteException | SQLException ex) { }
                }).start();
            }     
 
            //existiert termin überhaupt auf db?
            if(((RootServerDaten)this.serverDaten).datenbank.terminExists(termin.getID())){
                           
        /* --- ändere Daten auf DB des Servers --- */
                       
                //trage aktuallisierte Daten ein
                ((RootServerDaten)serverDaten).datenbank.changeEditierrechte(termin.getEditierbar(), termin.getID());
                //erneure zeitstempel und editorID
                ((RootServerDaten)serverDaten).datenbank.incTimestemp(termin.getID());
                ((RootServerDaten)serverDaten).datenbank.updateEditorID(termin.getID(), userID);
          
        /* --- ändere Daten auf den child-servern --- */
        
                //mit dieser Liste merkt man sich serverIDs die bereits einen änderungsaufruf bekommen
                //so werden nicht dem selben server mehrere änderungen geschickt
                LinkedList<String> bereitsAngesteuerteServer = new LinkedList<>();
                //für jeden Teilnehmer wird die Änderung an dessen Server geschickt
                for(Teilnehmer teilnehmer : termin.getTeilnehmerliste()){
                    for(Verbindung child : this.serverDaten.childConnection){
                        try{ 
                            String serverID = ((RootServerDaten)this.serverDaten).getServerIdByUsername(teilnehmer.getUsername());
                            if(!bereitsAngesteuerteServer.contains(serverID)){
                                child.getServerStub().changeEditierrechteChilds(termin, serverID, teilnehmer.getUsername());
                                bereitsAngesteuerteServer.add(serverID);
                            }

                        } catch (BenutzerException ex){}
                    }
                } 
            }
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
    public void changeEditierrechteChilds(Termin termin, String serverID, String username) throws RemoteException, SQLException{
        //ist man schon am richtigen server? (serverID gleich)
        if(serverID.equals(serverDaten.primitiveDaten.serverID)){
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
        else if(serverID.startsWith(serverDaten.primitiveDaten.serverID)){          
            for(Verbindung child : this.serverDaten.childConnection){
                child.getServerStub().changeEditierrechteChilds(termin, serverID, username);
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
    public void changeTermin(Termin termin, int userID) throws RemoteException, SQLException, BenutzerException{
        //ist server ein root server?
        if(this.serverDaten instanceof RootServerDaten){
      
        /* --- falls mehr als ein teilnehmer am termin teilnimmt, dann wird die änderung an alle root-server-nachbarn weitergesendet --- */
        
            if(termin.getTeilnehmerliste().size() > 1){
                //Flooding weiterleitung
                int tmpRC1 = serverDaten.primitiveDaten.requestCounter;
                for(Verbindung connection : ((RootServerDaten)serverDaten).connectionList){             
                    new Thread(() ->{
                        try {
                            connection.getServerStub().changeTerminRoots(serverDaten.primitiveDaten.ownIP, tmpRC1, termin, userID);
                        } catch (RemoteException | SQLException ex) { }
                    }).start();
                }     
                ((RootServerDaten)serverDaten).incRequestCounter();
            }  
            
        /* --- ändere Daten auf DB des Servers --- */
                       
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
          
        /* --- ändere Daten auf den child-servern --- */
        
            //mit dieser Liste merkt man sich serverIDs die bereits einen änderungsaufruf bekommen
            //so werden nicht dem selben server mehrere änderungen geschickt
            LinkedList<String> bereitsAngesteuerteServer = new LinkedList<>();
            //für jeden Teilnehmer wird die Änderung an dessen Server geschickt
            for(Teilnehmer teilnehmer : termin.getTeilnehmerliste()){
                for(Verbindung child : this.serverDaten.childConnection){
                    try{ 
                        String serverID = ((RootServerDaten)this.serverDaten).getServerIdByUsername(teilnehmer.getUsername());
                        if(!bereitsAngesteuerteServer.contains(serverID)){
                            child.getServerStub().changeTerminChilds(termin, ((RootServerDaten)this.serverDaten).getServerIdByUsername(teilnehmer.getUsername()), teilnehmer.getUsername());
                            bereitsAngesteuerteServer.add(serverID);
                        }
                        
                    } catch (BenutzerException ex){}
                }
            }   
                          
        }
        //wenn nicht, leite an dessen parten weiter (bis man an root ankommt)
        else{
            ((ChildServerDaten)this.serverDaten).parent.getServerStub().changeTermin(termin, userID);
        }
    }
    
    /**
     *  Methode, die einen gebenen Termin aktualisiert
     *  
     * @param originIP Ip-Adresse des Initialen senders
     * @param requestCounter counter der die Anfrage eindeitig identifitiert.
     * @param termin Termin der zu updaten ist.
     * @param userID
     * @throws RemoteException
     * @throws SQLException
     */
    @Override
    public void changeTerminRoots(String originIP, int requestCounter, Termin termin, int userID) throws RemoteException, SQLException{
        // war die Anfrage schonmal hier
        if(!checkRequest(originIP, requestCounter) && !originIP.equals(this.serverDaten.primitiveDaten.ownIP)){
        
        /* --- änderung wird an alle root-server-nachbarn weitergesendet --- */
            //Flooding weiterleitung
            for(Verbindung connection : ((RootServerDaten)this.serverDaten).connectionList){             
                new Thread(() ->{
                    try {
                        connection.getServerStub().changeTerminRoots(originIP, requestCounter, termin, userID);
                    } catch (RemoteException | SQLException ex) { }
                }).start();
            }     
 
            //existiert termin überhaupt auf db?
            if(((RootServerDaten)this.serverDaten).datenbank.terminExists(termin.getID())){
        
        /* --- ändere Daten auf DB des Servers --- */
                       
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
          
        /* --- ändere Daten auf den child-servern --- */
        
                //mit dieser Liste merkt man sich serverIDs die bereits einen änderungsaufruf bekommen
                //so werden nicht dem selben server mehrere änderungen geschickt
                LinkedList<String> bereitsAngesteuerteServer = new LinkedList<>();
                //für jeden Teilnehmer wird die Änderung an dessen Server geschickt
                for(Teilnehmer teilnehmer : termin.getTeilnehmerliste()){
                    for(Verbindung child : this.serverDaten.childConnection){
                        try{ 
                            String serverID = ((RootServerDaten)this.serverDaten).getServerIdByUsername(teilnehmer.getUsername());
                            if(!bereitsAngesteuerteServer.contains(serverID)){
                                child.getServerStub().changeTerminChilds(termin, ((RootServerDaten)this.serverDaten).getServerIdByUsername(teilnehmer.getUsername()), teilnehmer.getUsername());
                                bereitsAngesteuerteServer.add(serverID);
                            }

                        } catch (BenutzerException ex){}
                    }
                } 
            }
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
    public void changeTerminChilds(Termin termin, String serverID, String username) throws RemoteException, SQLException{
        //ist man schon am richtigen server? (serverID gleich)
        if(serverID.equals(serverDaten.primitiveDaten.serverID)){
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
        else if(serverID.startsWith(serverDaten.primitiveDaten.serverID)){          
            for(Verbindung child : this.serverDaten.childConnection){
                child.getServerStub().changeTerminChilds(termin, serverID, username);
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
    public void deleteTerminAlsNichtOwner(Termin termin, String username, String text) throws RemoteException, SQLException, BenutzerException{
        //ist server ein root server?
        if(this.serverDaten instanceof RootServerDaten){
      
        /* --- falls mehr als ein teilnehmer am termin teilnimmt, dann wird die änderung an alle root-server-nachbarn weitergesendet --- */
                   
            int tmpRC1 = this.serverDaten.primitiveDaten.requestCounter;
            //Flooding weiterleitung
            for(Verbindung connection : ((RootServerDaten)this.serverDaten).connectionList){             
                new Thread(() ->{
                    try {
                        connection.getServerStub().deleteTerminAlsNichtOwnerRoots(this.serverDaten.primitiveDaten.ownIP, tmpRC1, termin, username, text);
                    } catch (RemoteException | SQLException ex) { }
                }).start();
            }     
            ((RootServerDaten)this.serverDaten).incRequestCounter();
            
            
        /* --- ändere Daten auf DB des Servers --- */
                       
            //Entferne Teilnehmer von dem Termin aus DB
            ((RootServerDaten)serverDaten).datenbank.removeTeilnehmer(username, termin.getID());
          
        /* --- ändere Daten auf den child-servern --- */
        
            Meldung meldung;
            int meldungsID;       
            //für jeden Teilnehmer wird die Änderung an dessen Server geschickt
            for(Teilnehmer teilnehmer : termin.getTeilnehmerliste()){
                for(Verbindung child : this.serverDaten.childConnection){
                    meldungsID = ((RootServerDaten)this.serverDaten).datenbank.addMeldung(teilnehmer.getUsername(), text, false);
                    meldung = new Meldung(text, meldungsID);
                    try{ 
                        String serverID = ((RootServerDaten)this.serverDaten).getServerIdByUsername(teilnehmer.getUsername());
                        child.getServerStub().removeTeilnehmerChilds(termin.getID(), teilnehmer.getUsername(), username, serverID, meldung);                                                    
                    } catch (BenutzerException ex){}
                }
            }   
                          
        }
        //wenn nicht, leite an dessen parten weiter (bis man an root ankommt)
        else{
            ((ChildServerDaten)this.serverDaten).parent.getServerStub().deleteTerminAlsNichtOwner(termin, username, text);
        }       
    }
    
    /**
     * 
     * @param originIP
     * @param requestCounter
     * @param termin
     * @param username
     * @param text
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void deleteTerminAlsNichtOwnerRoots(String originIP, int requestCounter, Termin termin, String username, String text) throws RemoteException, SQLException{
        // war die Anfrage schonmal hier
        if(!checkRequest(originIP, requestCounter) && !originIP.equals(this.serverDaten.primitiveDaten.ownIP)){
        
        /* --- änderung wird an alle root-server-nachbarn weitergesendet --- */
            //Flooding weiterleitung
            for(Verbindung connection : ((RootServerDaten)this.serverDaten).connectionList){             
                new Thread(() ->{
                    try {
                        connection.getServerStub().deleteTerminAlsNichtOwnerRoots(originIP, requestCounter, termin, username, text);
                    } catch (RemoteException | SQLException ex) { }
                }).start();
            }      
 
            //existiert termin überhaupt auf db?
            if(((RootServerDaten)this.serverDaten).datenbank.terminExists(termin.getID())){
                           
        /* --- ändere Daten auf DB des Servers --- */
                       
                //Entferne Teilnehmer von dem Termin aus DB
                ((RootServerDaten)serverDaten).datenbank.removeTeilnehmer(username, termin.getID());
          
        /* --- ändere Daten auf den child-servern --- */
        
                Meldung meldung;
                int meldungsID;       
                //für jeden Teilnehmer wird die Änderung an dessen Server geschickt
                for(Teilnehmer teilnehmer : termin.getTeilnehmerliste()){
                    if(((RootServerDaten)serverDaten).datenbank.userExists(teilnehmer.getUsername())){    
                        meldungsID = ((RootServerDaten)this.serverDaten).datenbank.addMeldung(teilnehmer.getUsername(), text, false);
                        meldung = new Meldung(text, meldungsID);
                        for(Verbindung child : this.serverDaten.childConnection){                          
                            try{ 
                                String serverID = ((RootServerDaten)this.serverDaten).getServerIdByUsername(teilnehmer.getUsername());
                                child.getServerStub().removeTeilnehmerChilds(termin.getID(), teilnehmer.getUsername(), username, serverID, meldung);                                                    
                            } catch (BenutzerException ex){}
                        }
                    }
                } 
            }
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
    public void removeTeilnehmerChilds(int terminID, String username, String teilnehmer, String serverID, Meldung meldung) throws RemoteException, SQLException{
        //ist man schon am richtigen server? (serverID gleich)
        if(serverID.equals(serverDaten.primitiveDaten.serverID)){            
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
        else if(serverID.startsWith(serverDaten.primitiveDaten.serverID)){          
            for(Verbindung child : this.serverDaten.childConnection){
                child.getServerStub().removeTeilnehmerChilds(terminID, username, teilnehmer, serverID, meldung);
            }           
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
        //ist server ein root server?
        if(this.serverDaten instanceof RootServerDaten){
      
        /* --- falls mehr als ein teilnehmer am termin teilnimmt, dann wird die änderung an alle root-server-nachbarn weitergesendet --- */
                   
            int tmpRC1 = this.serverDaten.primitiveDaten.requestCounter;
            //Flooding weiterleitung
            for(Verbindung connection : ((RootServerDaten)this.serverDaten).connectionList){             
                new Thread(() ->{
                    try {
                        connection.getServerStub().deleteTerminAlsOwnerRoots(this.serverDaten.primitiveDaten.ownIP, tmpRC1, termin, username, text);
                    } catch (RemoteException | SQLException ex) { }
                }).start();
            }     
            ((RootServerDaten)this.serverDaten).incRequestCounter();
            
            
        /* --- ändere Daten auf DB des Servers --- */
                       
            //Entferne Teilnehmer von dem Termin aus DB
            ((RootServerDaten)serverDaten).datenbank.deleteTermin(termin.getID());
            //Entferne alle Anfragen zu dem Termin
            ((RootServerDaten)serverDaten).datenbank.deleteAnfrageByTerminID(termin.getID());
          
        /* --- ändere Daten auf den child-servern --- */
        
            Meldung meldung;
            int meldungsID;       
            //für jeden Teilnehmer wird die Änderung an dessen Server geschickt
            for(Teilnehmer teilnehmer : termin.getTeilnehmerliste()){
                for(Verbindung child : this.serverDaten.childConnection){
                    meldungsID = ((RootServerDaten)this.serverDaten).datenbank.addMeldung(teilnehmer.getUsername(), text, false);
                    meldung = new Meldung(text, meldungsID);
                    try{ 
                        String serverID = ((RootServerDaten)this.serverDaten).getServerIdByUsername(teilnehmer.getUsername());
                        child.getServerStub().removeTermin(termin.getID(), teilnehmer.getUsername(), serverID, meldung);                                                   
                    } catch (BenutzerException ex){}
                }
            }   
                          
        }
        //wenn nicht, leite an dessen parten weiter (bis man an root ankommt)
        else{
            ((ChildServerDaten)this.serverDaten).parent.getServerStub().deleteTerminAlsOwner(termin, username, text);
        }       
    }
     
    /**
     * 
     * @param originIP
     * @param requestCounter
     * @param termin
     * @param username
     * @param text
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void deleteTerminAlsOwnerRoots(String originIP, int requestCounter, Termin termin, String username, String text) throws RemoteException, SQLException{
        // war die Anfrage schonmal hier
        if(!checkRequest(originIP, requestCounter) && !originIP.equals(this.serverDaten.primitiveDaten.ownIP)){
        
        /* --- änderung wird an alle root-server-nachbarn weitergesendet --- */
            //Flooding weiterleitung
            for(Verbindung connection : ((RootServerDaten)this.serverDaten).connectionList){             
                new Thread(() ->{
                    try {
                        connection.getServerStub().deleteTerminAlsOwnerRoots(originIP, requestCounter, termin, username, text);
                    } catch (RemoteException | SQLException ex) { }
                }).start();
            }    
 
            //existiert termin überhaupt auf db?
            if(((RootServerDaten)this.serverDaten).datenbank.terminExists(termin.getID())){
                           
        /* --- ändere Daten auf DB des Servers --- */
                       
                //Entferne Teilnehmer von dem Termin aus DB
                ((RootServerDaten)serverDaten).datenbank.deleteTermin(termin.getID());
                //Entferne alle Anfragen zu dem Termin
                ((RootServerDaten)serverDaten).datenbank.deleteAnfrageByTerminID(termin.getID());
          
        /* --- ändere Daten auf den child-servern --- */
        
                Meldung meldung;
                int meldungsID;       
                //für jeden Teilnehmer wird die Änderung an dessen Server geschickt
                for(Teilnehmer teilnehmer : termin.getTeilnehmerliste()){
                    for(Verbindung child : this.serverDaten.childConnection){
                        meldungsID = ((RootServerDaten)this.serverDaten).datenbank.addMeldung(teilnehmer.getUsername(), text, false);
                        meldung = new Meldung(text, meldungsID);
                        try{ 
                            String serverID = ((RootServerDaten)this.serverDaten).getServerIdByUsername(teilnehmer.getUsername());
                            child.getServerStub().removeTermin(termin.getID(), teilnehmer.getUsername(), serverID, meldung);                                                   
                        } catch (BenutzerException ex){}
                    }
                }   
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
        if(serverID.equals(serverDaten.primitiveDaten.serverID)){
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
        else if(serverID.startsWith(serverDaten.primitiveDaten.serverID)){          
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
    public void addTeilnehmer(Termin termin, String username, String einlader) throws RemoteException, SQLException, BenutzerException{
        //ist server ein root server?
        if(this.serverDaten instanceof RootServerDaten){
      
        /* --- änderung wird an alle root-server-nachbarn weitergesendet --- */
                   
            int tmpRC1 = this.serverDaten.primitiveDaten.requestCounter;
            //Flooding weiterleitung
            for(Verbindung connection : ((RootServerDaten)this.serverDaten).connectionList){             
                new Thread(() ->{
                    try {
                        connection.getServerStub().addTeilnehmerRoots(this.serverDaten.primitiveDaten.ownIP, tmpRC1, termin, username, einlader);
                    } catch (RemoteException | SQLException ex) { }
                }).start();
            }     
            ((RootServerDaten)this.serverDaten).incRequestCounter();
            
            
        /* --- ändere Daten auf DB des Servers --- */
                       
            //Füge dem Termin den neuen Teilnehmer in der DB hinzu
            ((RootServerDaten)serverDaten).datenbank.addTeilnehmer(termin.getID(), username);
            
            
        /* --- Falls der eingeladene User zu dieser DB gehört, füge der DB & dem Server, 
                    an dem er eingeloggt ist, eine Anfrage und den Termin dem User hinzu --- */
        
            if(((RootServerDaten)serverDaten).datenbank.userExists(username)){ 
                String text = einlader + " lädt sie zu einem Termin am ";
                Anfrage anfrage = new Anfrage(text, termin, einlader, ((RootServerDaten)this.serverDaten).datenbank.getMeldungsCounter());
                
                //Füge der DB die Anfrage hinzu
                ((RootServerDaten)serverDaten).datenbank.addAnfrage(username, termin.getID(), einlader, text); 
                
                //Füge dem neuen Teilnehmer den Termin hinzu (auf dem Server)
                for(Verbindung child : this.serverDaten.childConnection){
                    try{
                        child.getServerStub().addTermin(anfrage, ((RootServerDaten)this.serverDaten).getServerIdByUsername(username), username);
                    } catch (BenutzerException ex){}
                } 
            }
          
        /* --- ändere Daten auf den child-servern --- */
        
            //mit dieser Liste merkt man sich serverIDs die bereits einen änderungsaufruf bekommen
            //so werden nicht dem selben server mehrere änderungen geschickt
            LinkedList<String> bereitsAngesteuerteServer = new LinkedList<>();
            //für jeden Teilnehmer wird die Änderung an dessen Server geschickt
            for(Teilnehmer teilnehmer : termin.getTeilnehmerliste()){
                for(Verbindung child : this.serverDaten.childConnection){
                    try{ 
                        String serverID = ((RootServerDaten)this.serverDaten).getServerIdByUsername(teilnehmer.getUsername());
                        if(!bereitsAngesteuerteServer.contains(serverID)){
                            child.getServerStub().addTeilnehmerChilds(termin.getID(), teilnehmer.getUsername(), username, serverID); 
                            bereitsAngesteuerteServer.add(serverID);
                        }              
                    } catch (BenutzerException ex){}
                }
            }                            
        }
        //wenn nicht, leite an dessen parten weiter (bis man an root ankommt)
        else{
            ((ChildServerDaten)this.serverDaten).parent.getServerStub().addTeilnehmer(termin, username, einlader);
        }
    }
    
    /**
     * Methode fügt allen Teilnehmern des Termins den neuen Teilnehmer hinzu
     * 
     * @param originIP Ip-Adresse des Initialen senders
     * @param requestCounter counter der die Anfrage eindeitig identifitiert
     * @param termin
     * @param username username des hinzuzufügenden teilnehmers
     * @param einlader
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void addTeilnehmerRoots(String originIP, int requestCounter, Termin termin, String username, String einlader) throws RemoteException, SQLException{
        // war die Anfrage schonmal hier
        if(!checkRequest(originIP, requestCounter) && !originIP.equals(this.serverDaten.primitiveDaten.ownIP)){

        /* --- änderung wird an alle root-server-nachbarn weitergesendet --- */
        
            //Flooding weiterleitung
            for(Verbindung connection : ((RootServerDaten)this.serverDaten).connectionList){             
                new Thread(() ->{
                    try {
                        connection.getServerStub().addTeilnehmerRoots(originIP, requestCounter, termin, username, einlader);                        
                    } catch (RemoteException | SQLException ex) { }
                }).start();
            }     
 
            //existiert termin überhaupt auf db?
            if(((RootServerDaten)this.serverDaten).datenbank.terminExists(termin.getID())){
           
        /* --- ändere Daten auf DB des Servers --- */
                       
                //Füge dem Termin den neuen Teilnehmer in der DB hinzu
                ((RootServerDaten)serverDaten).datenbank.addTeilnehmer(termin.getID(), username);
  
        /* --- ändere Daten auf den child-servern --- */
        
                //mit dieser Liste merkt man sich serverIDs die bereits einen änderungsaufruf bekommen
                //so werden nicht dem selben server mehrere änderungen geschickt
                LinkedList<String> bereitsAngesteuerteServer = new LinkedList<>();
                //für jeden Teilnehmer wird die Änderung an dessen Server geschickt
                for(Teilnehmer teilnehmer : termin.getTeilnehmerliste()){
                    for(Verbindung child : this.serverDaten.childConnection){
                        try{ 
                            String serverID = ((RootServerDaten)this.serverDaten).getServerIdByUsername(teilnehmer.getUsername());
                            if(!bereitsAngesteuerteServer.contains(serverID)){
                                child.getServerStub().addTeilnehmerChilds(termin.getID(), teilnehmer.getUsername(), username, serverID); 
                                bereitsAngesteuerteServer.add(serverID);
                            }              
                        } catch (BenutzerException ex){}
                    }
                }  
            }
        }
        
        /* --- Falls der eingeladene User zu dieser DB gehört, füge der DB & dem Server, 
        an dem er eingeloggt ist, eine Anfrage und den Termin dem User hinzu --- */

        if(((RootServerDaten)serverDaten).datenbank.userExists(username)){ 

            String text = einlader + " lädt sie zu einem Termin am ";
            Anfrage anfrage = new Anfrage(text, termin, einlader, ((RootServerDaten)this.serverDaten).datenbank.getMeldungsCounter());

            //Füge der DB die Anfrage hinzu
            ((RootServerDaten)serverDaten).datenbank.addExistingTermin(termin);
            ((RootServerDaten)serverDaten).datenbank.addTeilnehmer(termin.getID(), username);
            ((RootServerDaten)serverDaten).datenbank.addAnfrage(username, termin.getID(), einlader, text); 

            //Füge dem neuen Teilnehmer den Termin hinzu (auf dem Server)
            for(Verbindung child : this.serverDaten.childConnection){
                try{
                    child.getServerStub().addTermin(anfrage, ((RootServerDaten)this.serverDaten).getServerIdByUsername(username), username);
                } catch (BenutzerException ex){}
            } 
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
    public void addTeilnehmerChilds(int terminID, String username, String kontakt, String serverID) throws RemoteException, SQLException{
        //ist man schon am richtigen server? (serverID gleich)
        if(serverID.equals(serverDaten.primitiveDaten.serverID)){
            for(Sitzung sitzung : ((ChildServerDaten)serverDaten).aktiveSitzungen){
                if(sitzung.getEingeloggterBenutzer().getUsername().equals(username)){
                    try {
                        sitzung.getEingeloggterBenutzer().getTerminkalender().getTerminByID(terminID).addTeilnehmer(kontakt);                        
                    } catch (TerminException ex) {
                        Logger.getLogger(ServerStubImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }     
                }
            }            
        }
        //ist man auf dem richtigen weg? (serverID ersten x ziffern gleich)
        else if(serverID.startsWith(serverDaten.primitiveDaten.serverID)){          
            for(Verbindung child : this.serverDaten.childConnection){
                child.getServerStub().addTeilnehmerChilds(terminID, username, kontakt, serverID);
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
        if(serverID.equals(serverDaten.primitiveDaten.serverID)){
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
        else if(serverID.startsWith(serverDaten.primitiveDaten.serverID)){          
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
        //ist server ein root server?
        if(this.serverDaten instanceof RootServerDaten){
      
        /* --- falls mehr als ein teilnehmer am termin teilnimmt, dann wird die änderung an alle root-server-nachbarn weitergesendet --- */
        
            if(termin.getTeilnehmerliste().size() > 1){
                int tmpRC1 = this.serverDaten.primitiveDaten.requestCounter;
                //Flooding weiterleitung
                for(Verbindung connection : ((RootServerDaten)this.serverDaten).connectionList){             
                    new Thread(() ->{
                        try {
                            connection.getServerStub().teilnehmerNimmtTeilRoots(this.serverDaten.primitiveDaten.ownIP, tmpRC1, termin, username, true, text);
                        } catch (RemoteException | SQLException ex) { }
                    }).start();
                }     
                ((RootServerDaten)this.serverDaten).incRequestCounter();
            }
            
        /* --- ändere Daten auf DB des Servers --- */
                       
            //Setze den teilnehmer für den termin auf nimmt teil
            ((RootServerDaten)serverDaten).datenbank.nimmtTeil(termin.getID(), username);
            //Anfrage aus DB löschen
            ((RootServerDaten)serverDaten).datenbank.removeAnfrageForUserByTerminID(termin.getID(), username);
          
        /* --- ändere Daten auf den child-servern --- */
        
            Meldung meldung;
            int meldungsID;
            //BEACHTE: hier wird der änderung auch mehrfach an einen server geschickt, da jeder teilnehmer eine meldung bekommen muss
            
            //für jeden Teilnehmer wird die Änderung an dessen Server geschickt
            for(Teilnehmer teilnehmer : termin.getTeilnehmerliste()){
                //gehört teilnehmer zu diesem server?
                if(((RootServerDaten)this.serverDaten).datenbank.userExists(teilnehmer.getUsername())){
                    meldungsID = ((RootServerDaten)this.serverDaten).datenbank.addMeldung(teilnehmer.getUsername(), text, false);
                    meldung = new Meldung(text, meldungsID);
                
                    for(Verbindung child : this.serverDaten.childConnection){
                        try{ 
                            String serverID = ((RootServerDaten)this.serverDaten).getServerIdByUsername(teilnehmer.getUsername());
                            child.getServerStub().teilnehmerNimmtTeilChilds(termin.getID(), teilnehmer.getUsername(), username, serverID, meldung);
                        } catch (BenutzerException ex){}
                    }
                }                   
            }                           
        }
        //wenn nicht, leite an dessen parten weiter (bis man an root ankommt)
        else{
            ((ChildServerDaten)this.serverDaten).parent.getServerStub().teilnehmerNimmtTeil(termin, username, text);
        }
    }
    
    /**
     * Methode um den Status, ob ein User teilnimmt oder nicht, zu setzen
     * 
     * @param originIP Die IP-Adresse des Initiators der Flooding Anfrage
     * @param requestCounter identifizert die request eindeutig (eines servers)
     * @param termin termin
     * @param username identifiziert den user
     * @param status bestimmt ob user teilnimmt (true) oder nicht (false)
     * @param text text der meldung
     * @throws RemoteException
     * @throws SQLException 
     */
    @Override
    public void teilnehmerNimmtTeilRoots(String originIP, int requestCounter, Termin termin, String username, boolean status, String text) throws RemoteException, SQLException{
        // war die Anfrage schonmal hier
        if(!checkRequest(originIP, requestCounter) && !originIP.equals(this.serverDaten.primitiveDaten.ownIP)){
            
            /* --- falls mehr als ein teilnehmer am termin teilnimmt, dann wird die änderung an alle root-server-nachbarn weitergesendet --- */
            
            //Flooding weiterleitung
            for(Verbindung connection : ((RootServerDaten)this.serverDaten).connectionList){             
                new Thread(() ->{
                    try {
                        connection.getServerStub().teilnehmerNimmtTeilRoots(originIP, requestCounter, termin, username, status, text);
                    } catch (RemoteException | SQLException ex) { }
                }).start();
            }     
            
            //existiert termin überhaupt auf db?
            if(((RootServerDaten)this.serverDaten).datenbank.terminExists(termin.getID())){
            
            /* --- ändere Daten auf DB des Servers --- */
                       
                //Setze den teilnehmer für den termin auf nimmt teil
                ((RootServerDaten)serverDaten).datenbank.nimmtTeil(termin.getID(), username);
                //Anfrage aus DB löschen
                ((RootServerDaten)serverDaten).datenbank.removeAnfrageForUserByTerminID(termin.getID(), username);
          
            /* --- ändere Daten auf den child-servern --- */
        
                Meldung meldung;
                int meldungsID;
                //BEACHTE: hier wird der änderung auch mehrfach an einen server geschickt, da jeder teilnehmer eine meldung bekommen muss
                
                //für jeden Teilnehmer wird die Änderung an dessen Server geschickt
                for(Teilnehmer teilnehmer : termin.getTeilnehmerliste()){
                    //gehört teilnehmer zu diesem server?
                    if(((RootServerDaten)this.serverDaten).datenbank.userExists(teilnehmer.getUsername())){
                        meldungsID = ((RootServerDaten)this.serverDaten).datenbank.addMeldung(teilnehmer.getUsername(), text, false);
                        meldung = new Meldung(text, meldungsID);

                        for(Verbindung child : this.serverDaten.childConnection){
                            try{ 
                                String serverID = ((RootServerDaten)this.serverDaten).getServerIdByUsername(teilnehmer.getUsername());
                                child.getServerStub().teilnehmerNimmtTeilChilds(termin.getID(), teilnehmer.getUsername(), username, serverID, meldung);
                            } catch (BenutzerException ex){}
                        }
                    }                   
                } 
            }
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
    public void teilnehmerNimmtTeilChilds(int terminID, String username, String teilnehmer, String serverID, Meldung meldung) throws RemoteException, SQLException{
        //ist man schon am richtigen server? (serverID gleich)
        if(serverID.equals(this.serverDaten.primitiveDaten.serverID)){
            for(Sitzung sitzung : ((ChildServerDaten)this.serverDaten).aktiveSitzungen){
                if(sitzung.getEingeloggterBenutzer().getUsername().equals(username)){
                    try {                        
                        //fügt meldung hinzu
                        sitzung.getEingeloggterBenutzer().addMeldung(meldung);
                        //setzt teilnehmer nimmt teil
                        sitzung.getEingeloggterBenutzer().getTerminkalender().getTerminByID(terminID).changeTeilnehmerNimmtTeil(teilnehmer);
                    } catch (TerminException ex) {
                        Logger.getLogger(ServerStubImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }     
                }
            }            
        }
        //ist man auf dem richtigen weg? (serverID ersten x ziffern gleich)
        else if(serverID.startsWith(serverDaten.primitiveDaten.serverID)){          
            for(Verbindung child : this.serverDaten.childConnection){
                child.getServerStub().teilnehmerNimmtTeilChilds(terminID, username, teilnehmer, serverID, meldung);
            }           
        }
    }    
    
    /**
     * testet ob Anfrage bereits behandelt wurde, wenn ja: gibt true zurück,
     * wenn nein: gibt false zurück und speichert den request in der Liste ab
     * 
     * @param originIP ip des ursprungs der anfrage
     * @param requestCounter identifizert die request eindeutig (eines servers)
     * @return 
     */
    private boolean checkRequest(String originIP, int requestCounter){
        //gibt es von der IP bereits Anfragen
        if (((RootServerDaten)this.serverDaten).requestTable.containsKey(originIP)){
            //Wenn ja welche Anfragen 
            if(((RootServerDaten)this.serverDaten).requestTable.get(originIP).contains(requestCounter)){
                return true;
            }
            else{
                ((RootServerDaten)this.serverDaten).requestTable.get(originIP).add(requestCounter);
                return false;
            }
        }
        else{
            ((RootServerDaten)this.serverDaten).requestTable.put(originIP, new LinkedList<>());
            ((RootServerDaten)this.serverDaten).requestTable.get(originIP).add(requestCounter);
            return false;
        }
    }

    @Override
    public void changeColor(int color, int userID) throws RemoteException, SQLException {       
        if(this.serverDaten instanceof RootServerDaten){
            ((RootServerDaten)serverDaten).datenbank.changeColor(color, userID);
        }
        else{
            ((ChildServerDaten)this.serverDaten).parent.getServerStub().changeColor(color, userID);
        }          
    }
    
}
