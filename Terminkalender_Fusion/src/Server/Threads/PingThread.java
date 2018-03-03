/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Threads;

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
            if(this.serverStub.ping(serverDaten.primitiveDaten.ownIP)){
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
