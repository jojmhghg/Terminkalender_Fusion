/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Threads;

import Server.ChildServerDaten;
import Server.RootServerDaten;
import Server.ServerStub;
import java.rmi.RemoteException;
import Server.ServerDaten;

/**
 *
 * @author timtim
 */
public class PingThread extends Thread{
    
    private final ServerStub serverStub;
    private final Counter counter;
    private final ServerDaten serverDaten;
    
    public PingThread(ServerStub serverStub, Counter counter, ServerDaten serverDaten){
        this.serverStub = serverStub;
        this.counter = counter;
        this.serverDaten = serverDaten;
    }    
    
    @Override 
    public void run(){
        try {
            String ownIP;
            if(serverDaten instanceof ChildServerDaten){
                ownIP = ((ChildServerDaten)serverDaten).primitiveDaten.ownIP;
            }
            else{
                ownIP = ((RootServerDaten)serverDaten).primitiveDaten.ownIP;
            }
            
            if(this.serverStub.ping(ownIP)){
                //pingtest kam an, alles gut, resete counter
                counter.resetCounter();
            }
            else{
                //pingtest kam an, aber der andere Server 
                //hat keine Verbindung mehr zu diesem
                counter.setZero();
            }
        } catch (RemoteException ex) {
        }
    }
}
