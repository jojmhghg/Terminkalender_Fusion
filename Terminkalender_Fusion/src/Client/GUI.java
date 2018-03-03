/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import ClientGUI.LoginFenster;
import Server.ClientStub;
import Utilities.BenutzerException;
import Utilities.Datum;
import Utilities.TerminException;
import Utilities.Zeit;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author niroshan
 */
public class GUI {

    private final ClientStub stub;
        
    public GUI(ClientStub stub){
        this.stub = stub;
    }
    
    /**
     * Methode die GUI startet
     * 
     */
    public void startGUI(){
        try{
            anmeldenGUI();
	}
	catch (RemoteException e){
            System.err.println(e.getMessage());
	}  
        catch (BenutzerException e) {
            System.err.println(e.getMessage());
        }    
        catch (TerminException e) {
            System.err.println(e.getMessage());
        } 
        catch (Datum.DatumException e) {
            System.err.println(e.getMessage());
        }    
        catch (Zeit.ZeitException e) {
            System.err.println(e.getMessage());
        }
        catch (SQLException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * GUI zum Startbildschirm
     * 
     * @throws RemoteException
     * @throws BenutzerException
     * @throws TerminException
     * @throws Terminkalender.Datum.DatumException
     * @throws Terminkalender.Zeit.ZeitException 
     */
    private void anmeldenGUI() throws RemoteException, BenutzerException, TerminException, Datum.DatumException, Zeit.ZeitException, SQLException{ 
        LoginFenster start = new LoginFenster(stub);
        start.setVisible(true);
        
    }
    
}
