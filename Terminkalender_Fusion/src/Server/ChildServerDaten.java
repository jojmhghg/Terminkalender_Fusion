/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Server.Threads.VerbindungstestsParentThread;
import Server.Utilities.Sitzung;
import Server.Utilities.Verbindung;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class ChildServerDaten extends ServerDaten {
    
    public Verbindung parent;
    
    public final LinkedList<Sitzung> aktiveSitzungen; 
    public PrimitiveServerDaten primitiveDaten;
    
    
    public ChildServerDaten(String[] args) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException {
        super(args);
        this.parent = null;
        this.aktiveSitzungen = new LinkedList<>();
        primitiveDaten = new PrimitiveServerDaten(args[0], null);
    }
    
      /**
     * baut Verbindungen zu einem anderen Server auf
     *
     * @param parentIP
     * @throws IOException
     */
    public void connectToParent(String parentIP) throws IOException {
        ServerStub serverStub;
        Registry registry;

        try {
            //baut Verbindung zu Parent auf
            registry = LocateRegistry.getRegistry(parentIP, 1100);
            serverStub = (ServerStub) registry.lookup("ServerStub");

            //lässt anderen Server Verbindung zu diesem aufbauen
            this.primitiveDaten.serverID = serverStub.initConnection(this.primitiveDaten.ownIP);
            
            //fügt Verbindung zur Liste der Verbindungen hinzu
            this.parent = new Verbindung(serverStub, parentIP, serverStub.getServerID());

            //Ausgabe im Terminal
            System.out.println("LOG * ---> Verbindung zu Parent " + parent.getIP() + " hergestellt!");
            System.out.println("LOG * ---> Server wurde die ServerID:" + this.primitiveDaten.serverID + " zugewiesen!");
            
            //Starte Threads, die die Verbindung zu anderen Servern testen
            new VerbindungstestsParentThread(this, this.parent).start();
                 
        } catch (NotBoundException ex) {
            Logger.getLogger(ServerDaten.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
