/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Server.Utilities.DatenbankException;
import Server.Utilities.ServerIdUndAnzahlUser;
import Utilities.Anfrage;
import Utilities.Benutzer;
import Utilities.BenutzerException;
import Utilities.Datum;
import Utilities.Meldung;
import Utilities.Termin;
import Utilities.Zeit;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * Interface ServerStub enthält die notwendigen Methoden für den Server
 */
public interface ServerStub extends Remote{
    
public void setID(String newID) throws RemoteException;
public String initConnection(String ip) throws RemoteException;
public boolean ping(String senderIP) throws RemoteException;
public String getServerID() throws RemoteException; 
public int getAnzahlUser() throws RemoteException;
public boolean initConnectionP2P(String ip) throws RemoteException;
public boolean pingP2P(String senderIP) throws RemoteException; 

    //Datensuche
public ServerIdUndAnzahlUser findServerForUser() throws RemoteException;
public int findIdForUser(String username) throws RemoteException, SQLException;
public LinkedList<String> findUserProfil(int userID) throws RemoteException, SQLException;
public Benutzer getUser(String username) throws RemoteException, SQLException, DatenbankException;
public void removeUserFromRootList(String username) throws RemoteException, BenutzerException;

    //Datenmanipulation - Nur ein User betroffen
    public void changePasswort(String passwort, String username) throws RemoteException, SQLException;
    public void changeVorname(String vorname, String username) throws RemoteException, SQLException;
    public void changeNachname(String nachname, String username) throws RemoteException, SQLException;
    public void changeEmail(String email, String username) throws RemoteException, SQLException;

    public void addKontakt(String kontaktname, int userID) throws RemoteException, SQLException;
    public void removeKontakt(String kontaktname, int userID) throws RemoteException, SQLException;

    public void deleteMeldung(int meldungsID) throws RemoteException, SQLException;
    public void setMeldungenGelesen(int meldungsID) throws RemoteException, SQLException;

    public int addNewTermin(Datum datum, Zeit beginn, Zeit ende, String titel, int userID) throws RemoteException, SQLException;

    //Datenmanipulation - Evtl. Mehrere User betroffen
    public void changeEditierrechte(Termin termin, int userID) throws RemoteException, SQLException, BenutzerException;
    public void changeEditierrechteRoots(String ownIP, int requestCounter, Termin termin, int userID) throws RemoteException, SQLException; 
    public void changeEditierrechteChilds(Termin termin, String serverID, String username) throws RemoteException, SQLException;

    public void changeTermin(Termin termin, int userID) throws RemoteException, SQLException, BenutzerException;
    public void changeTerminRoots(String originIP, int requestCounter, Termin termin, int userID) throws RemoteException, SQLException;
    public void changeTerminChilds(Termin termin, String serverID, String username) throws RemoteException, SQLException;

public void addTerminTeilnehmerDB(Termin termin, String username, String einlader) throws RemoteException, SQLException, BenutzerException;
public void addTeilnehmerChilds(int terminID, String username, String kontakt, String serverID) throws RemoteException, SQLException;
public void addTeilnehmerRoots(String originIP, int requestCounter, int terminID, String username, Anfrage anfrage) throws RemoteException, SQLException;    
public void addTermin(Anfrage anfrage, String serverID, String username) throws RemoteException, SQLException;

public void deleteTerminNichtOwner(Termin termin, String username, String text) throws RemoteException, SQLException, BenutzerException;  
public void deleteTerminAlsOwner(Termin termin, String username, String text) throws RemoteException, SQLException;
public void removeTeilnehmer(int terminID, String username, String teilnehmer, String serverID, Meldung meldung) throws RemoteException, SQLException;
public void removeTermin(int terminID, String username, String serverID, Meldung meldung) throws RemoteException, SQLException;

public void teilnehmerNimmtTeil(Termin termin, String username, String text) throws RemoteException, SQLException; 
public void setNimmtTeil(int terminID, String username, String teilnehmer, String serverID, Meldung meldung) throws RemoteException, SQLException;



    //Datensuche
public String findServerForUserP2P(String originIP, int requestCounter, String username) throws RemoteException, SQLException;
public int findIdForUserP2P(String originIP, int requestCounter, String username) throws RemoteException, SQLException;
public LinkedList<String> findUserProfilP2P(String originIP, int requestCounter, int userID) throws RemoteException, SQLException;

    //Datenmanipulation
public void addTerminP2P(String originIP, int requestCounter, int userID, Anfrage anfrage, String sendername) throws RemoteException, SQLException;
public void deleteTerminP2P(String originIP, int requestCounter, Termin termin, String meldungsText) throws RemoteException, SQLException;
public void removeTeilnehmerFromTerminP2P(String originIP, int requestCounter, Termin termin, String username, int userID) throws RemoteException, SQLException;
public void teilnehmerChangeStatusP2P(String originIP, int requestCounter, Termin termin, String username, boolean status, String meldungstext) throws RemoteException, SQLException; 

}
