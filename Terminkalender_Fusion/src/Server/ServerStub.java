/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Utilities.Anfrage;
import Utilities.Termin;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 *
 * @author nader
 */
public interface ServerStub extends Remote{
    
    public boolean initConnection(String ip) throws RemoteException;
    public boolean ping(String senderIP) throws RemoteException; 
    
    //Datensuche
    public String findServerForUser(String originIP, int requestCounter, String username) throws RemoteException, SQLException;
    public int findIdForUser(String originIP, int requestCounter, String username) throws RemoteException, SQLException;
    public LinkedList<String> findUserProfil(String originIP, int requestCounter, int userID) throws RemoteException, SQLException;
    
    //Datenmanipulation
    public void changeEditierrechte(String ownIP, int requestCounter, Termin termin) throws RemoteException, SQLException;
    public void updateTermin(String originIP, int requestCounter, Termin termin) throws RemoteException, SQLException;
    public void addTeilnehmer(String originIP, int requestCounter, int terminID, int userID, String username) throws RemoteException, SQLException;
    public void addTermin(String originIP, int requestCounter, int userID, Anfrage anfrage, String sendername) throws RemoteException, SQLException;
    public void deleteTermin(String originIP, int requestCounter, Termin termin, String meldungsText) throws RemoteException, SQLException;
    public void removeTeilnehmerFromTermin(String originIP, int requestCounter, Termin termin, String username, int userID) throws RemoteException, SQLException;
    public void teilnehmerChangeStatus(String originIP, int requestCounter, Termin termin, String username, boolean status, String meldungstext) throws RemoteException, SQLException; 
}