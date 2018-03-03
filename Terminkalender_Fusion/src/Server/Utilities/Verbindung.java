/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Utilities;

import Server.ServerStub;

/**
 *
 * @author timtim
 */
public class Verbindung {
    
    private final ServerStub stub;
    private final String ip;
    
    /**
     *
     * @param stub
     * @param ip
     */
    public Verbindung(ServerStub stub, String ip){
        this.stub = stub;
        this.ip = ip;
    }
    
    public ServerStub getServerStub(){
        return this.stub;
    }
    
    public String getIP(){
        return this.ip;
    }
    
    public boolean equals(String ip){
        return this.ip.equals(ip);
    }
    
    
}
