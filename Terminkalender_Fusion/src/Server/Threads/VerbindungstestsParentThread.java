/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Threads;

import Server.ServerDaten;
import Server.Utilities.Verbindung;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author timtim
 */
public class VerbindungstestsParentThread extends Thread{
    
    private final ServerDaten serverDaten;
    private final Verbindung verbindung;
    
    public VerbindungstestsParentThread(ServerDaten serverDaten, Verbindung verbindung){
        this.serverDaten = serverDaten;
        this.verbindung = verbindung;
    }    
    
    @Override 
    public void run(){
        Counter counter = new Counter();
        boolean serverUp = true;
        
        while(serverUp){
            try {
                Thread.sleep(3000);
                       
                //System.out.println("Teste " + this.verbindung.getIP() + " | Counter = " + counter.getValue());
                counter.decrement();               
                
                //starte Thread der Server anpingt               
                new PingThread(this.verbindung.getServerStub(), counter, serverDaten).start();
                
                //test ob keine verbindung mehr zu anderem server
                if(counter.getValue() == 0){
                    //Verbindung löschen
                    this.serverDaten.childConnection.remove(this.verbindung);
                    System.out.println("--->> Verbindung zu " + this.verbindung.getIP() + " wurde beendet");                                   
                                        
                    connectToRoot();
                    
                    //beende Schleife
                    serverUp = false;
                }                
            } catch (InterruptedException ex) {
                Logger.getLogger(VerbindungstestsParentThread.class.getName()).log(Level.SEVERE, null, ex);
            }   
        }
    }
    
    private void connectToRoot(){       
        String rootIP;
        String line;            
        BufferedReader bufferedReader = null;

        //liest IP-Adressen aller Server aus File und speichert sie in LinkedList
        File file = new File(".\\src\\data\\serverlist.txt"); 
        //für mac-pcs
        if (!file.canRead() || !file.isFile()){
            file = new File("./src/data/severlist.txt"); 
        }
        try { 
            bufferedReader = new BufferedReader(new FileReader(file));  
            if((line = bufferedReader.readLine()) != null) { 
                rootIP = line;

                this.serverDaten.connectToParent(rootIP);
                int counter = 0;
                for(Verbindung child : this.serverDaten.childConnection){
                    child.getServerStub().setID(this.serverDaten.primitiveDaten.serverID + counter);
                    counter++;
                }
                
            }      
            else{
                System.out.println("LOG * ---> Verbindung zu Root-Server konnte nicht hergestellt werden!");
                System.exit(0);
            }
        } catch (IOException e) { 
            e.printStackTrace(); 
            System.exit(0);
        } 
        // zum schließen des readers
        finally { 
            if (bufferedReader != null) 
                try { 
                    bufferedReader.close(); 
                } catch (IOException e) { 
            } 
        }    
    }
}
