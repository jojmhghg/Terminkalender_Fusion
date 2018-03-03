/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Server.ClientStub;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.*;
import java.util.LinkedList;

/**
 *
 * @author Tim Meyer
 */
public class Client {
   
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ClientStub stub;
        Registry registry;     
                
        boolean noConnection = true;
        String line, tmpIP;            
        BufferedReader bufferedReader = null;
        LinkedList<String> serverlist = new LinkedList<>();

        //liest IP-Adressen aller Server aus File und speichert sie in LinkedList
        File file = new File(".\\src\\data\\serverlist.txt"); 
        if (!file.canRead() || !file.isFile()){
            file = new File("./src/data/severlist.txt"); 
        }
        try { 
            bufferedReader = new BufferedReader(new FileReader(file));  
            while ((line = bufferedReader.readLine()) != null) { 
                serverlist.add(line);
            }             
        } catch (IOException e) { 
            e.printStackTrace(); 
        } finally { 
            if (bufferedReader != null) 
                try { 
                    bufferedReader.close(); 
                } catch (IOException e) { 
            } 
        }  
        
        //Versucht Verbindung zu einem zufälligen Server aufzubauen
        while(noConnection && serverlist.size() > 0){
            tmpIP = serverlist.get((int) (Math.random() * serverlist.size()));
            System.out.println("LOG * ---> Versuche Verbindung zu " + tmpIP + " herzustellen!");  

            try {
                //baut Verbindung zu Server auf
                registry = LocateRegistry.getRegistry(tmpIP, 1099);
                stub = (ClientStub) registry.lookup("ClientStub");
                System.out.println("LOG * ---> Verbindung zu Server " + tmpIP + " hergestellt!");

                GUI gui = new GUI(stub);
                gui.startGUI();
                
                //TUI tui = new TUI(stub);
                //tui.start();
                
                noConnection = false;                    
            } catch (RemoteException | NotBoundException ex) {
                System.out.println("LOG * ---> Verbindung zu Server " + tmpIP + " konnte nicht hergestellt werden!");  
            }    
            serverlist.remove(tmpIP);
        } 
        
    }    
}