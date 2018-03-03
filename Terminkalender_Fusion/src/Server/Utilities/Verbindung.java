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
    private final String id;
    
    /**
     *
     * @param stub
     * @param ip
     * @param id
     */
    public Verbindung(ServerStub stub, String ip, String id){
        this.stub = stub;
        this.ip = ip;
        this.id = id;
    }
    
    public ServerStub getServerStub(){
        return this.stub;
    }
    
    public String getIP(){
        return this.ip;
    }
    
    public String getID(){
        return this.id;
    }
    
    public boolean equals(String ip){
        return this.ip.equals(ip);
    }
    
    
}
