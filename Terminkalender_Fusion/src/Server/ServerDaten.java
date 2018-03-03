/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Server.Utilities.Verbindung;
import Server.Utilities.DBHandler;
import Server.Utilities.Sitzung;
import Server.Threads.VerbindungstestsThread;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author timtim
 */
public class ServerDaten {
    
    //Liste mit Server-Server-Verbindungen
    public final LinkedList<Verbindung> connectionList; 
    //Datenbank
    public final DBHandler datenbank;
    //Liste mit bereits behandelten Anfragen
    public Map<String, LinkedList> requestTable;
    //Liste mit aktiven Sitzungen (eingeloggte User des Servers)
    public final LinkedList<Sitzung> aktiveSitzungen;    
    public PrimitiveServerDaten primitiveDaten;
          
    public ServerDaten(String ownIP) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException{
        this.connectionList = new LinkedList<>();    
        this.requestTable = new HashMap<>();
        this.aktiveSitzungen = new LinkedList<>();             
        this.datenbank = new DBHandler(aktiveSitzungen, connectionList, primitiveDaten); 
        this.primitiveDaten = new PrimitiveServerDaten(ownIP);
        startDB();    
        
        int requestCounter = this.datenbank.getRequestCounter();  
        this.primitiveDaten.setRequestCounter(requestCounter);
    }
    
    private void startDB() throws ClassNotFoundException, SQLException, NoSuchAlgorithmException{
        String line;
        int linecounter = 0;
        BufferedReader bufferedReader = null;
        
        //sucht zeile der eigenen ip aus liste und übergibt diese als serverID
        File file = new File(".\\src\\data\\serverlist.txt"); 
        if (!file.canRead() || !file.isFile()){
            file = new File("./src/data/severlist.txt"); 
        }
        try { 
            bufferedReader = new BufferedReader(new FileReader(file));  
            while ((line = bufferedReader.readLine()) != null) {              
                if(line.equals(primitiveDaten.ownIP)){
                    this.datenbank.getConnection(linecounter);
                }
                linecounter++;
            }             
        } catch (IOException e) { 
            e.printStackTrace(); 
        } finally { 
            if (bufferedReader != null) 
                try { 
                    bufferedReader.close(); 
                } catch (IOException e) { 
            } 
        }    
        
        
    }
    
    /**
     * baut Verbindungen zu einem anderen Server auf
     * 
     * @return 
     * @throws RemoteException
     * @throws IOException 
     */
    public boolean connectToServer() throws IOException{       
        Verbindung verbindung;            
        ServerStub serverStub;
        Registry registry;

        boolean noConnection = true, result = false, vorhanden;
        String line, tmpIP;            
        BufferedReader bufferedReader = null;
        LinkedList<String> serverlist = new LinkedList<>();

        //liest IP-Adressen aller Server aus File und speichert sie in LinkedList
        File file = new File(".\\src\\data\\serverlist.txt"); 
        if (!file.canRead() || !file.isFile()){
            file = new File("./src/data/severlist.txt"); 
        }
        try { 
            bufferedReader = new BufferedReader(new FileReader(file));  
            while ((line = bufferedReader.readLine()) != null) { 
                serverlist.add(line);
            }             
        } catch (IOException e) { 
            e.printStackTrace(); 
        } finally { 
            if (bufferedReader != null) 
                try { 
                    bufferedReader.close(); 
                } catch (IOException e) { 
            } 
        }          

        //Versucht Verbindung zu einem zufälligen Server aufzubauen
        while(noConnection && serverlist.size() > 0){
            tmpIP = serverlist.get((int) (Math.random() * serverlist.size()));
            System.out.println("LOG * ---> Versuche Verbindung zu " + tmpIP + " herzustellen!");  
            if(!this.primitiveDaten.ownIP.equals(tmpIP)){  
                vorhanden = false;
                for(Verbindung verb : this.connectionList){
                    if(verb.getIP().equals(tmpIP)){
                        vorhanden = true;
                    }
                }
                if(!vorhanden){
                    try {
                        //baut Verbindung zu Server auf
                        registry = LocateRegistry.getRegistry(tmpIP, 1100);
                        serverStub = (ServerStub) registry.lookup("ServerStub");

                        //lässt anderen Server Verbindung zu diesem aufbauen
                        serverStub.initConnection(this.primitiveDaten.ownIP);

                        //fügt Verbindung zur Liste der Verbindungen hinzu
                        verbindung = new Verbindung(serverStub, tmpIP);
                        this.connectionList.add(verbindung);

                        //Ausgabe im Terminal
                        System.out.println("LOG * ---> Verbindung zu Server " + tmpIP + " hergestellt!");

                        //Starte Threads, die die Verbindung zu anderen Servern testen
                        new VerbindungstestsThread(this, verbindung).start(); 
                        result = true;
                        noConnection = false;                    
                    } catch (RemoteException | NotBoundException ex) {
                        System.out.println("LOG * ---> Verbindung zu Server " + tmpIP + " konnte nicht hergestellt werden!");  
                    }    
                }
                else{
                    System.out.println("LOG * ---> Verbindung zu Server " + tmpIP + " schon vorhanden!");
                }                                    
            }
            else{
                System.out.println("LOG * ---> Verbindung zu Server " + tmpIP + " konnte nicht hergestellt werden! (eigener Server)");  
            }            
            serverlist.remove(tmpIP);
        } 
        
        return result;
    }   

    /**
     * inkrementiert den requestcounter auf der db und dem server
     * @throws java.sql.SQLException
     */
    public void incRequestCounter() throws SQLException{
        this.primitiveDaten.requestCounter++;
        this.datenbank.incRequestCounter();
    }
 
}
