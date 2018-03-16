/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

/**
 *
 * @author timtim
 */
public class PrimitiveServerDaten {
    
    public final String ownIP;
    public int requestCounter;
    public int sitzungscounter;
    //Hierarchisch:
    public String serverID;  
    public int childCounter;
    
    public PrimitiveServerDaten(String ownIP){
        this.requestCounter = 0;  
        this.sitzungscounter = 1;
        this.ownIP = ownIP;
        this.serverID = null;
        this.childCounter = 0;
    }
    
    public void setServerID(String serverID){
        this.serverID = serverID;
    }
    
    public void setRequestCounter(int requestCounter){
        this.requestCounter = requestCounter;  
    }
    
    public String getNewChildId(){
        String id = serverID + childCounter;
        childCounter++;
        return id;
    }
    
}
