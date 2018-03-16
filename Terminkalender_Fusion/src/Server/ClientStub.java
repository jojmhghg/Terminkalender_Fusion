/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Server.Utilities.DatenbankException;
import Utilities.BenutzerException;
import Utilities.Datum;
import Utilities.Meldung;
import Utilities.Termin;
import Utilities.TerminException;
import Utilities.Zeit;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 *  Interface Klasse mit allen Methoden, die in dem Client benutzt werden.
 *
 */
public interface ClientStub extends Remote{
    
    /* initiale Methoden */
    public void createUser(String username, String passwort, String email) throws RemoteException, BenutzerException, SQLException;
    public String findServerForUser(String username) throws RemoteException, SQLException, BenutzerException;
    public int einloggen(String username, String passwort) throws RemoteException, BenutzerException, SQLException, DatenbankException;
    public void ausloggen(int sitzungsID) throws RemoteException, BenutzerException;
    public void resetPassword(String username) throws RemoteException, BenutzerException, SQLException;

    /* alles zu der Kontaktliste */
    public void addKontakt(String username, int sitzungsID) throws RemoteException, BenutzerException, SQLException;
    public void removeKontakt(String username, int sitzungsID) throws BenutzerException, RemoteException, SQLException;
    public LinkedList<String> getKontakte(int sitzungsID) throws BenutzerException, RemoteException;

    /* alles zu den Benutzerdaten */
    public void changePasswort(String altesPW, String neuesPW, int sitzungsID) throws RemoteException, BenutzerException, SQLException;
    public void changeVorname(String neuerVorname, int sitzungsID) throws RemoteException, BenutzerException, SQLException;
    public void changeNachname(String neuerNachname, int sitzungsID) throws RemoteException, BenutzerException, SQLException;
    public void changeEmail(String neueEmail, int sitzungsID) throws RemoteException, BenutzerException, SQLException;
    public String getUsername(int sitzungsID) throws RemoteException, BenutzerException;
    public String getVorname(int sitzungsID) throws RemoteException, BenutzerException;
    public String getNachname(int sitzungsID) throws RemoteException, BenutzerException;
    public String getEmail(int sitzungsID) throws RemoteException, BenutzerException;

    /* alles zu Terminen */
    public Termin getTermin(int TerminID, int sitzungsID) throws RemoteException, BenutzerException, TerminException;
    public void addTermin(Datum datum, Zeit beginn, Zeit ende, String titel, int sitzungsID) throws RemoteException, BenutzerException, TerminException, SQLException;
    public void removeTermin(int terminID, int sitzungsID) throws RemoteException, BenutzerException, TerminException, SQLException;

    public void changeEditierrechte(Termin termin, int sitzungsID) throws TerminException, BenutzerException, RemoteException, SQLException;        
    public void changeTermin(Termin termin, int sitzungsID) throws BenutzerException, RemoteException, TerminException, SQLException;

    public void addTerminteilnehmer(int terminID, String username, int sitzungsID) throws RemoteException, BenutzerException, TerminException, SQLException;
    public LinkedList<Termin> getTermineInKalenderwoche(int kalenderwoche, int jahr, int sitzungsID) throws RemoteException, BenutzerException;
    public LinkedList<Termin> getTermineInMonat(int monat, int jahr, int sitzungsID) throws RemoteException, TerminException, BenutzerException;
    public LinkedList<Termin> getTermineAmTag(Datum datum, int sitzungsID) throws RemoteException, TerminException, BenutzerException;
    public void terminAnnehmen(int terminID, int sitzungsID) throws RemoteException, TerminException, BenutzerException, SQLException;
    public void terminAblehnen(int terminID, int sitzungsID) throws RemoteException, TerminException, BenutzerException, SQLException;

    /* alles zu ausstehenden Meldung */ 
    public LinkedList<Meldung> getMeldungen(int sitzungsID) throws RemoteException, BenutzerException;
    public void deleteMeldung(int index, int sitzungsID) throws RemoteException, BenutzerException, SQLException;
    public void setMeldungenGelesen(int meldungsID, int sitzungsID) throws BenutzerException, RemoteException, SQLException;

    /* Profil */
    // Liste = { username, email, vorname, nachname }
    public LinkedList<String> getProfil(String username) throws RemoteException, BenutzerException, SQLException;
}
