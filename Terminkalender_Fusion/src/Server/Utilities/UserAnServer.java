/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Utilities;

import java.io.Serializable;

/**
 *
 * @author timtim
 */
public class UserAnServer implements Serializable{
    
    public String serverID;
    public String username;
    public String serverIP;
    
    public UserAnServer(String serverID, String username, String serverIP){
        this.serverID = serverID;
        this.username = username;
        this.serverIP = serverIP;
    }
   
    
}
