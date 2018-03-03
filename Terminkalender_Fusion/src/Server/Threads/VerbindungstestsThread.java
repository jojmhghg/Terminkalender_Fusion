/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Threads;

import Server.ServerDaten;
import Server.Utilities.Verbindung;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author timtim
 */
public class VerbindungstestsThread extends Thread{
    
    private final ServerDaten serverDaten;
    private final Verbindung verbindung;
    
    public VerbindungstestsThread(ServerDaten serverDaten, Verbindung verbindung){
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
                    this.serverDaten.connectionList.remove(this.verbindung);
                    System.out.println("--->> Verbindung zu " + this.verbindung.getIP() + " wurde beendet");
                    
                    //teste ob noch genug Verbindungen vorhanden sind
                    if(this.serverDaten.connectionList.size() < 2){
                        //lass Server eine weitere Verbindung aufbauen
                        this.serverDaten.connectToServer();
                    }                   
                    
                    //beende Schleife
                    serverUp = false;
                }                
            } catch (InterruptedException | IOException ex) {
                Logger.getLogger(VerbindungstestsThread.class.getName()).log(Level.SEVERE, null, ex);
            }   
        }
    }
}
