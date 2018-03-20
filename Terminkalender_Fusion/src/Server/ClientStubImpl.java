package Server;


import Server.Threads.FindUserDataFlooding;
import Server.Utilities.DatenbankException;
import Server.Utilities.EMailService;
import Server.Utilities.ServerIdUndAnzahlUser;
import Server.Utilities.Sitzung;
import Server.Utilities.UserAnServer;
import Server.Utilities.Verbindung;
import Utilities.Benutzer;
import Utilities.BenutzerException;
import Utilities.Datum;
import Utilities.Meldung;
import Utilities.Termin;
import Utilities.TerminException;
import Utilities.Zeit;
import java.awt.Color;
import java.rmi.RemoteException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementierung von der Klasse ClientStub Interface
 * 
*/
public class ClientStubImpl implements ClientStub{

    private final Color[] colors;
    
    private final ServerDaten serverDaten;
    /**
     * 
     * @param serverDaten
     * @throws SQLException
     * @throws DatenbankException 
     */
    public ClientStubImpl(ServerDaten serverDaten) throws SQLException, DatenbankException{
        this.serverDaten = serverDaten;
        
        this.colors = new Color[16];
        //blau - classic
        colors[0] = new Color(153,204,255);
        colors[1] = new Color(0,153,204);
        colors[2] = new Color(0,51,102);
        colors[3] = new Color(0,0,0);
        //grau - light
        colors[4] = new Color(204,204,204);
        colors[5] = new Color(153,153,153);
        colors[6] = new Color(102,102,102);
        colors[7] = new Color(0, 0, 0);
        //schwarz - dark
        colors[8] = new Color(46,49,117);        
        colors[9] = new Color(29,30,66);
        colors[10] = new Color(21,22,48);
        colors[11] = new Color(240,240,240);
        //pink
        colors[12] = new Color(255,153,255);
        colors[13] = new Color(102,0,102);
        colors[14] = new Color(51,0,51);
        colors[15] = new Color(240,240,240);
    }
    
    /**
     * Methode um einen neuen User anzulegen
     * 
     * @param username username des neuen users
     * @param passwort passwort des neuen users
     * @param email email des neuen users
     * @throws BenutzerException 
     * @throws java.sql.SQLException 
     * @throws java.rmi.RemoteException 
     */
    @Override
    public void createUser(String username, String passwort, String email) throws BenutzerException, SQLException, RemoteException{
        if(this.serverDaten instanceof RootServerDaten){
            //existiert User auf diesem Server oder auf anderem Server?            
            if(findServerForUser(username).equals("false")){
                //lege User in DB an
                ((RootServerDaten) serverDaten).datenbank.addUser(username, passwort, email);
            }
            else{
                throw new BenutzerException("User existiert bereits!");
            }                         
        }
        else{
            throw new BenutzerException("Client nicht mit dem Root-Server verbunden!");
        }
    }
    
    /**
     * suche server mit den wenigsten usern um sich mit diesem zu verbinden
     * 
     * @param username des einzuloggenden users
     * @return true, falls client am richtigen server, false falls server nicht
     * vorhanden, sonst die ip des servers an dem die db liegt
     * @throws SQLException 
     * @throws java.rmi.RemoteException 
     * @throws Utilities.BenutzerException 
     */
    @Override
    public String findServerForUser(String username) throws SQLException, RemoteException, BenutzerException{
        //teste ob man mit einem root verbunden -> sonst fehler
        if(this.serverDaten instanceof RootServerDaten){
            
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
            //sonst suche per flooding nach server mit wenigsten usern
            else{
                int tmpRC1 = ((RootServerDaten)this.serverDaten).primitiveDaten.requestCounter;
                ((RootServerDaten)this.serverDaten).incRequestCounter();
                LinkedList<String> resultList = new LinkedList<>();
                int anzahlThreads = 0;
                for(Verbindung connection : ((RootServerDaten)this.serverDaten).connectionList){    
                    new FindUserDataFlooding(resultList, this.serverDaten.primitiveDaten.ownIP, tmpRC1, username, connection).start();
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
            return "false";
        }
        else{
            throw new BenutzerException("Client nicht mit dem Root-Server verbunden!");
        }              
    }
    
    /**
     * Methode um einen User einzuloggen
     * Testet ob username und passwort stimmen
     * Client muss mit dem richtigen Server verbunden sein
     * 
     * @param username username des einzuloggenden users
     * @param passwort passwort des einzuloggenden users
     * @return gibt die SitzungsID oder -1 im Fehlerfall zurück
     * @throws Utilities.BenutzerException  
     * @throws java.sql.SQLException      
     * @throws Server.Utilities.DatenbankException      
     * @throws java.rmi.RemoteException   
     */
    @Override
    public int einloggen(String username, String passwort) throws BenutzerException, SQLException, DatenbankException, RemoteException{
        if(this.serverDaten instanceof ChildServerDaten){
             int sitzungsID = 10000000 * ((ChildServerDaten)serverDaten).primitiveDaten.sitzungscounter + (int)(Math.random() * 1000000 + 1);
            ((ChildServerDaten)serverDaten).primitiveDaten.sitzungscounter++;
            Benutzer user;

            try{
                //falls User bereits eingeloggt, dann wird das vorhandene user objekt benutzt
                user = istEingeloggt(username);
            }
            catch(BenutzerException ex){
                //falls user noch nicht eingeloggt, wird er aus der db geladen
                user = ((ChildServerDaten)this.serverDaten).parent.getServerStub().getUser(username);
                
            }       
            if(user.istPasswort(passwort)){
                ((ChildServerDaten)serverDaten).aktiveSitzungen.add(new Sitzung(user, sitzungsID));
                return sitzungsID;
            }      
            //werfe fehler
            return -1;
        }
        else{
            throw new BenutzerException("Client nicht mit dem Root-Server verbunden!");
        }              
    }
    
    /**
     * Methode zum Ausloggen eines Users
     * 
     * @param sitzungsID wird benötigt um die Sitzung zu identifizieren
     * @throws Utilities.BenutzerException
     * @throws java.rmi.RemoteException
     */
    @Override
    public void ausloggen(int sitzungsID) throws BenutzerException, RemoteException{        
        String username = this.getUsername(sitzungsID);
        boolean remove = true;
        
        //entferne sitzung aus liste
        int index = -1, counter = 0;
        for(Sitzung sitzung : ((ChildServerDaten)serverDaten).aktiveSitzungen){
            if(sitzung.compareWithSitzungsID(sitzungsID)){
                index = counter;
            }
            counter++;
        }
        ((ChildServerDaten)serverDaten).aktiveSitzungen.remove(index);
        
        //falls user nicht mehr an dem server eingeloggt (auch nicht mit anderen sitzungen)
        //entferne ihn aus der userAnServerListe
        for(Sitzung sitzung : ((ChildServerDaten)serverDaten).aktiveSitzungen){
            if(sitzung.getEingeloggterBenutzer().getUsername().equals(username)){
                remove = false;
            }
        }
        if(remove){
            ((ChildServerDaten)serverDaten).parent.getServerStub().removeAusUserAnServerListe(username);
        }
    }
    
    /**
     * Methode um das Passwort einen Users zurückzusetzen
     * User bekommt das neue Passwort via Email gesendet
     * FindServerForUser() vorher aufrufen und mit Ergebnis verbinden!
     * 
     * @param username username des Users desssen Passwort zurückgesetzt werden soll
     * @throws BenutzerException
     * @throws SQLException 
     */
    @Override
    public void resetPassword(String username) throws BenutzerException, SQLException{ 
        if(((RootServerDaten)serverDaten).primitiveDaten.serverID.equals("0")){
            if(((RootServerDaten)this.serverDaten).datenbank.userExists(username)){       
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
                emailService.sendMail(((RootServerDaten)serverDaten).datenbank.getEmail(username), "Terminkalender: Passwort zurückgesetzt", message);

                //aktuallisiere DB
                ((RootServerDaten)serverDaten).datenbank.changePasswort(passwort, username);
                try{
//TODO: hier muss von root an child weitergeleitet werden!
                    Benutzer user = istEingeloggt(username);
                    user.setPasswort(passwort);
                    
//bis hier!                    
                }
                catch(BenutzerException ex){ }
            }
            else{
                throw new BenutzerException("User existiert nicht!");
            }
        }
        else{
            throw new BenutzerException("Client nicht mit dem Root-Server verbunden!");
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
     * @throws java.rmi.RemoteException 
     */
    @Override
    public void addTermin(Datum datum, Zeit beginn, Zeit ende, String titel, int sitzungsID) throws BenutzerException, TerminException, SQLException, RemoteException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);     
        
        //füge den neu erstellten Termin der db des roots hinzu
        int terminID = ((ChildServerDaten)this.serverDaten).parent.getServerStub().addNewTermin(datum, beginn, ende, titel, eingeloggterBenutzer.getUserID());
        //füge ihn jetzt auf dem server hinzu
        eingeloggterBenutzer.addTermin(new Termin(datum, beginn, ende, titel, terminID, eingeloggterBenutzer.getUsername()));
    }
    
    /**
     * entfernt den termin mit angegebener id
     * 
     * @param terminID zu entfernender termin
     * @param sitzungsID authentifiziert den benutzer
     * @throws BenutzerException 
     * @throws Utilities.TerminException 
     * @throws java.sql.SQLException 
     * @throws java.rmi.RemoteException 
     */
    @Override
    public void removeTermin(int terminID, int sitzungsID) throws BenutzerException, TerminException, SQLException, RemoteException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
           
        if (eingeloggterBenutzer.getUsername().equals(eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID).getOwner())) {
            String text = eingeloggterBenutzer.getUsername() 
                            + " hat den Termin '" 
                            + eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID).getTitel()
                            + "' am "
                            + eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID).getDatum().toString()
                            + " gelöscht";
            
            ((ChildServerDaten)this.serverDaten).parent.getServerStub().deleteTerminAlsOwner(eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID), eingeloggterBenutzer.getUsername(), text);    
        }
        else{  
            String text = eingeloggterBenutzer.getUsername() 
                            + " nimmt nicht mehr an dem  Termin '" 
                            + eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID).getTitel()
                            + "' am "
                            + eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID).getDatum().toString()
                            + " teil";
            
            ((ChildServerDaten)this.serverDaten).parent.getServerStub().deleteTerminAlsNichtOwner(eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID), eingeloggterBenutzer.getUsername(), text);
            eingeloggterBenutzer.deleteAnfrage(terminID);
            eingeloggterBenutzer.getTerminkalender().removeTerminByID(terminID);
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
     * @throws java.rmi.RemoteException 
     */
    @Override
    public void changeEditierrechte(Termin termin, int sitzungsID) throws TerminException, BenutzerException, SQLException, RemoteException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        
        //ändere editierrechte auf server (in termin des users)     
        
        //ändere editierrechte auf anderen servern (roots & childs)
        ((ChildServerDaten)this.serverDaten).parent.getServerStub().changeEditierrechte(termin, eingeloggterBenutzer.getUserID()); 
    }
        
    /**
     * aktuallisiert einen Termin
     * 
     * @param termin id des termins
     * @param sitzungsID authentifiziert den benutzer
     * @throws BenutzerException
     * @throws TerminException
     * @throws SQLException 
     * @throws java.rmi.RemoteException 
     */
    @Override
    public void changeTermin(Termin termin, int sitzungsID) throws BenutzerException, TerminException, SQLException, RemoteException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        System.out.println("clientstub - 1");
        ((ChildServerDaten)this.serverDaten).parent.getServerStub().changeTermin(termin, eingeloggterBenutzer.getUserID());     
        System.out.println("clientstub - 2");       
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
     * @throws java.rmi.RemoteException 
     */
    @Override
    public void addTerminteilnehmer(int terminID, String username, int sitzungsID) throws BenutzerException, TerminException, SQLException, RemoteException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);   
        
        ((ChildServerDaten)this.serverDaten).parent.getServerStub().addTeilnehmer(eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID), username, eingeloggterBenutzer.getUsername());  
    }
    
    /**
     * Nimmt eine Terminanfrage an
     * 
     * @param terminID bestimmt den termin der angefragt wurde
     * @param sitzungsID authentifiziert den benutzer
     * @throws TerminException
     * @throws BenutzerException 
     * @throws java.sql.SQLException 
     * @throws java.rmi.RemoteException 
     */
    @Override
    public void terminAnnehmen(int terminID, int sitzungsID) throws TerminException, BenutzerException, SQLException, RemoteException{
        //Ist Sitzungsid gültig? Wenn ja, lade user dazu
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);

        String text= eingeloggterBenutzer.getUsername() 
                            + " nimmt an dem Termin '" 
                            + eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID).getTitel()
                            + "' am "
                            + eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID).getDatum().toString()
                            + " teil";
        
        ((ChildServerDaten)this.serverDaten).parent.getServerStub().teilnehmerNimmtTeil(eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID), eingeloggterBenutzer.getUsername(), text);
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
     * @throws java.rmi.RemoteException 
     */
    @Override
    public void terminAblehnen(int terminID, int sitzungsID) throws TerminException, BenutzerException, SQLException, RemoteException{
        //Ist Sitzungsid gültig? Wenn ja, lade user dazu
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
           
        String text = eingeloggterBenutzer.getUsername() 
                        + " hat den Termin '" 
                        + eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID).getTitel()
                        + "' am "
                        + eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID).getDatum().toString()
                        + " abgelehnt";
        
        ((ChildServerDaten)this.serverDaten).parent.getServerStub().deleteTerminAlsNichtOwner(eingeloggterBenutzer.getTerminkalender().getTerminByID(terminID), eingeloggterBenutzer.getUsername(), text);
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
     * @throws java.rmi.RemoteException 
     */
    @Override
    public void changePasswort(String altesPW, String neuesPW, int sitzungsID) throws BenutzerException, SQLException, RemoteException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        if(!eingeloggterBenutzer.istPasswort(altesPW)){
            throw new BenutzerException("altes Passwort war falsch!");
        }
        eingeloggterBenutzer.setPasswort(neuesPW);
        
        ((ChildServerDaten)this.serverDaten).parent.getServerStub().changePasswort(neuesPW, eingeloggterBenutzer.getUsername());
    }
    
    /**
     * Ändert den Vornamen eines Benutzers
     * 
     * @param neuerVorname neuer Vorname des Users
     * @param sitzungsID authentifiziert den benutzer
     * @throws BenutzerException 
     * @throws java.sql.SQLException 
     * @throws java.rmi.RemoteException 
     */
    @Override
    public void changeVorname(String neuerVorname, int sitzungsID) throws BenutzerException, SQLException, RemoteException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        eingeloggterBenutzer.setVorname(neuerVorname);
        
        ((ChildServerDaten)this.serverDaten).parent.getServerStub().changeVorname(neuerVorname, eingeloggterBenutzer.getUsername());
    }
    
    /**
     * Ändert den Nachnamen eines Users
     * 
     * @param neuerNachname neuer Nachname
     * @param sitzungsID authentifiziert den benutzer
     * @throws BenutzerException 
     * @throws java.sql.SQLException 
     * @throws java.rmi.RemoteException 
     */
    @Override
    public void changeNachname(String neuerNachname, int sitzungsID) throws BenutzerException, SQLException, RemoteException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        eingeloggterBenutzer.setNachname(neuerNachname);
        
        ((ChildServerDaten)this.serverDaten).parent.getServerStub().changeNachname(neuerNachname, eingeloggterBenutzer.getUsername());
    }
    
    /**
     * Ändert die Email Adresse eines Benutzers
     * 
     * @param neueEmail neue Email Adresse eines benutzers
     * @param sitzungsID authentifiziert den benutzer
     * @throws BenutzerException 
     * @throws java.sql.SQLException 
     * @throws java.rmi.RemoteException 
     */
    @Override
    public void changeEmail(String neueEmail, int sitzungsID) throws BenutzerException, SQLException, RemoteException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        eingeloggterBenutzer.setEmail(neueEmail);
        
        ((ChildServerDaten)this.serverDaten).parent.getServerStub().changeEmail(neueEmail, eingeloggterBenutzer.getUsername());
    }
    
    /**
     * Fügt dem User einen neuen Kontakt hinzu
     * Es wird getestet ob dieser vorhanden ist
     * 
     * @param username username des neuen Kontakts
     * @param sitzungsID authentifiziert den benutzer
     * @throws Utilities.BenutzerException 
     * @throws java.sql.SQLException 
     * @throws java.rmi.RemoteException 
     */
    @Override
    public void addKontakt(String username, int sitzungsID) throws BenutzerException, SQLException, RemoteException{             
        //existiert user überhaupt?
        if(((ChildServerDaten)this.serverDaten).parent.getServerStub().findIdForUser(username) == -1){
            throw new BenutzerException("User existiert nicht!!!");
        }
        

        //adde kontakt auf server
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);            
        eingeloggterBenutzer.addKontakt(username);
        
        //adde konkakt in db des roots
        ((ChildServerDaten)this.serverDaten).parent.getServerStub().addKontakt(username, eingeloggterBenutzer.getUserID());
    }

    /**
     * Entfernt einen Kontakt des Users
     * 
     * @param username username des zu entfernenden Kontaktes
     * @param sitzungsID authentifiziert den benutzer
     * @throws BenutzerException 
     * @throws java.sql.SQLException 
     * @throws java.rmi.RemoteException 
     */
    @Override
    public void removeKontakt(String username, int sitzungsID) throws BenutzerException, SQLException, RemoteException{
        //existiert user überhaupt?
        ((ChildServerDaten)this.serverDaten).parent.getServerStub().findIdForUser(username);
        
        //entferne kontakt auf server
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        eingeloggterBenutzer.removeKontakt(username);
        
        //entferne kontakt aus db des roots
        ((ChildServerDaten)this.serverDaten).parent.getServerStub().removeKontakt(username, eingeloggterBenutzer.getUserID());
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
    
    
    @Override
    public void changeColor(int color, int sitzungsID) throws BenutzerException, SQLException, RemoteException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        eingeloggterBenutzer.setColor(color);
        
        ((ChildServerDaten)this.serverDaten).parent.getServerStub().changeColor(color, eingeloggterBenutzer.getUserID());
    }
    
    /**
     *
     * @param sitzungsID
     * @return
     */
    @Override
    public Color[] returnColor(int sitzungsID){
        return colors;
    }
   
    /**
     * Methode die die Color gibt
     * 
     * @param sitzungsID
     * @return colors 
     * @throws Utilities.BenutzerException
     */
    @Override
    public Color[] getColor(int sitzungsID) throws BenutzerException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        Color[] color = new Color[4];
        switch (eingeloggterBenutzer.getColor()){
            //grau
            case 1:
                color[0] = this.colors[4];
                color[1] = this.colors[5];
                color[2] = this.colors[6];
                color[3] = this.colors[7];
                break;
            //schwarz
            case 2:
                color[0] = this.colors[8];
                color[1] = this.colors[9];
                color[2] = this.colors[10];
                color[3] = this.colors[11];
                break;
            //pink    
            case 3:
                color[0] = this.colors[12];
                color[1] = this.colors[13];
                color[2] = this.colors[14];
                color[3] = this.colors[15];
                break;   
            //blau    
            default:
                color[0] = this.colors[0];
                color[1] = this.colors[1];
                color[2] = this.colors[2];
                color[3] = this.colors[3];
                break;
        }
        return color;
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
     * @throws java.rmi.RemoteException 
     */
    @Override
    public void deleteMeldung(int meldungsID, int sitzungsID) throws BenutzerException, SQLException, RemoteException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);       
        //entferne meldung auf server
        eingeloggterBenutzer.deleteMeldung(meldungsID);
        //entferne meldung aus db des roots
        ((ChildServerDaten)this.serverDaten).parent.getServerStub().deleteMeldung(meldungsID);
    }
    
    /**
     * Setzt eine Meldung als gelesen
     * 
     * @param meldungsID liest die richtige Meldung aus
     * @param sitzungsID authentifiziert den benutzer
     * @throws BenutzerException 
     * @throws java.sql.SQLException 
     * @throws java.rmi.RemoteException 
     */
    @Override
    public void setMeldungenGelesen(int meldungsID, int sitzungsID) throws BenutzerException, SQLException, RemoteException{
        Benutzer eingeloggterBenutzer = istEingeloggt(sitzungsID);
        //setze meldung auf server als gelesen
        LinkedList<Meldung> meldungen = eingeloggterBenutzer.getMeldungen();
        for(Meldung meldung : meldungen){
            if(meldungsID == meldung.meldungsID){
                meldung.meldungGelesen();
                break;
            }     
        }
        //setze meldung auf db des roots als gelesen
        ((ChildServerDaten)this.serverDaten).parent.getServerStub().setMeldungenGelesen(meldungsID);
    }
    
    /**
     * gibt das Profil eines Users zurück
     * 
     * @param username bestimmt den User
     * @return
     * @throws BenutzerException 
     * @throws java.sql.SQLException 
     * @throws java.rmi.RemoteException 
     */
    @Override
    public LinkedList<String> getProfil(String username) throws BenutzerException, SQLException, RemoteException{
        int userID = ((ChildServerDaten)this.serverDaten).parent.getServerStub().findIdForUser(username);
        return ((ChildServerDaten)this.serverDaten).parent.getServerStub().findUserProfil(userID);
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
        if(serverDaten instanceof RootServerDaten){
            throw new BenutzerException("ungültige Sitzungs-ID");
        }
        for(Sitzung sitzung : ((ChildServerDaten)serverDaten).aktiveSitzungen){
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
        if(serverDaten instanceof ChildServerDaten){
            for(Sitzung sitzung : ((ChildServerDaten)serverDaten).aktiveSitzungen){
                if(sitzung.getEingeloggterBenutzer().getUsername().equals(username)){
                    return sitzung.getEingeloggterBenutzer();
                }
            }           
        }
        throw new BenutzerException("user nicht eingeloggt");
    }
        
    
  
}
