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
public class FindUserDataFlooding extends Thread{
    
    String originIP;
    int requestCounter;
    String username;
    Verbindung connection;            
    LinkedList<String> resultList;
    
    public FindUserDataFlooding(LinkedList<String> resultList, String originIP, int requestCounter, String username, Verbindung connection){
        this.resultList = resultList;
        this.originIP = originIP;
        this.requestCounter = requestCounter;
        this.username = username;
        this.connection = connection;
    }
   
    @Override
    public void run() {
        try {
            this.resultList.add(connection.getServerStub().findServerForUser(originIP, requestCounter, username));           
        } catch (RemoteException | SQLException ex) {
            Logger.getLogger(FindUserDataFlooding.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
