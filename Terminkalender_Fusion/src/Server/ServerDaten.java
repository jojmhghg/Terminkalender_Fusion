/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Server.Utilities.Verbindung;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 *
 * @author timtim
 */
public class ServerDaten {
    
    //Verbindungen zu childs
    public LinkedList<Verbindung> childConnection;
    public PrimitiveServerDaten primitiveDaten;
             
    public ServerDaten(String[] args) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException{
        this.childConnection = new LinkedList<>();
        primitiveDaten = new PrimitiveServerDaten(args[0]);        
    }

}