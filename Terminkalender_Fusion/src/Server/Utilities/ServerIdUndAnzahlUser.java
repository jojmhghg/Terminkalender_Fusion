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
public class ServerIdUndAnzahlUser implements Serializable {
    
    public int anzahlUser;
    public String serverID;
    public String serverIP;
    
    public ServerIdUndAnzahlUser(int anzahlUser, String serverID, String serverIP){
        this.anzahlUser = anzahlUser;
        this.serverID = serverID;
        this.serverIP = serverIP;
    }
}
