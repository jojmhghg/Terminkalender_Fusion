/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Threads;
import Server.Utilities.Verbindung;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nader
 */
public class FindUserProfilFloodingThread extends Thread{
    
    String originIP;
    int requestCounter;
    int userID;
    Verbindung connection;            
    LinkedList<LinkedList<String>> resultList;
    
    public FindUserProfilFloodingThread(LinkedList<LinkedList<String>> resultList, String originIP, int requestCounter, int userID, Verbindung connection){
        this.resultList = resultList;
        this.originIP = originIP;
        this.requestCounter = requestCounter;
        this.userID = userID;
        this.connection = connection;
    }
   
    @Override
    public void run() {
        try {
            this.resultList.add(connection.getServerStub().findUserProfil(originIP, requestCounter, userID));           
        } catch (RemoteException | SQLException ex) {
            Logger.getLogger(FindUserProfilFloodingThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
