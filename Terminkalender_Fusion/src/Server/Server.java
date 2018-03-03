/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Server.Utilities.DatenbankException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/**
 *
 * @author timtim
 */
public class Server {

    private final ServerDaten serverDaten;
    private final String[] args;
    
    public Server(String[] args) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException{        
        this.serverDaten = new ServerDaten(args);
        this.args = args;
        System.setProperty("java.rmi.server.hostname", args[0]);
    }

    /**
     * Startet den Server und ruft alle Methoden auf, die dazu notwendig sind
     * 
     * @throws RemoteException
     * @throws AlreadyBoundException
     * @throws NotBoundException
     * @throws UnknownHostException
     * @throws SQLException
     * @throws DatenbankException
     * @throws IOException 
     */
    public void start() throws RemoteException, AlreadyBoundException, NotBoundException, UnknownHostException, SQLException, DatenbankException, IOException{
        
        if (!args[1].equals("root")) {          
            System.out.println("LOG * Starte Leaf-Server");
        }
        else{
            System.out.println("LOG * Starte Root-Server");
        }     
        System.out.println("LOG * Server-IP: " + serverDaten.primitiveDaten.ownIP);
        System.out.println("LOG * ");
              
        //initialisiere Stubs für Server & Clients     
        initServerStub();
        initClientStub();
        System.out.println("LOG * ");
        
        //baut Verbindung zu Parent auf
        if (!args[1].equals("root")) {          
            this.serverDaten.connectToParent(args[1]);
        }
        else{
            //baue bis zu 2 dauerhafte Verbindungen zu anderen Servern auf
            System.out.println("LOG * Erste Verbindung wird aufgebaut");
            if(this.serverDaten.connectToServer()){
                System.out.println("LOG * Zweite Verbindung wird aufgebaut");
                this.serverDaten.connectToServer();
            }
        }

        System.out.println("LOG * ");
        System.out.println("LOG * Server laeuft!");
        System.out.println("---------------------------------------------");
    }

    /**
     * initialisiert den Stub für die Server
     * 
     * @throws RemoteException
     * @throws AlreadyBoundException 
     */
    private void initServerStub() throws RemoteException, AlreadyBoundException{
        ServerStubImpl serverLauncher = new ServerStubImpl(serverDaten);
        ServerStub serverStub = (ServerStub)UnicastRemoteObject.exportObject(serverLauncher, 0);
        Registry serverRegistry = LocateRegistry.createRegistry(1100);
        serverRegistry.bind("ServerStub", serverStub);
        System.out.println("LOG * ServerStub initialisiert!");
    }

    /**
     * initialisiert den Stub für die Clients
     * 
     * @throws RemoteException
     * @throws AlreadyBoundException
     * @throws SQLException
     * @throws DatenbankException 
     */
    private void initClientStub() throws RemoteException, AlreadyBoundException, SQLException, DatenbankException{
        ClientStubImpl clientLauncher = new ClientStubImpl(serverDaten);   
        ClientStub clientStub = (ClientStub)UnicastRemoteObject.exportObject(clientLauncher, 0);
        Registry clientRegistry = LocateRegistry.createRegistry(1099);
        clientRegistry.bind("ClientStub", clientStub);
        System.out.println("LOG * ClientStub initialisiert!");
    }
   
    
}
