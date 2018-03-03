/*
 * 
 *
 */
package Client;

import Server.ClientStub;
import Utilities.Anfrage;
import Utilities.BenutzerException;
import Server.Utilities.DatenbankException;
import Utilities.Datum;
import Utilities.Datum.DatumException;
import Utilities.Meldung;
import Utilities.Teilnehmer;
import Utilities.Termin;
import Utilities.TerminException;
import Utilities.Zeit;
import Utilities.Zeit.ZeitException;
import java.io.InputStream;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tim Meyer
 */
public class TUI {
    
    private ClientStub stub;
    private final InputStream inputStream;
    private int sitzungsID;
    
    TUI(ClientStub stub){
        this.stub = stub;
        this.inputStream = System.in;
    }
    
    /**
     * Methode die TUI startet
     * 
     * @throws Server.Utilities.DatenbankException
     */
    public void start() throws DatenbankException{
    	try{
            startbildschirm();
	} 
	catch (RemoteException e){
            System.err.println(e.getMessage());
	} 
        catch (BenutzerException e) {
            System.err.println(e.getMessage());
        } catch (TerminException e) {
            System.err.println(e.getMessage());
        } catch (DatumException e) {
            System.err.println(e.getMessage());
        } catch (Zeit.ZeitException e) {
            System.err.println(e.getMessage());
        } catch (SQLException ex) {
            Logger.getLogger(TUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * TUI zum Startbildschirm
     * 
     * @throws RemoteException
     * @throws BenutzerException
     * @throws TerminException 
     */
    private void startbildschirm() throws RemoteException, BenutzerException, TerminException, DatumException, Zeit.ZeitException, SQLException, DatenbankException{
        Scanner scanner = new Scanner(inputStream);
        int eingabe;
        boolean wiederholen = true;
            
        do{
	    System.out.println("\n************ Startbildschirm ************\n");
            System.out.println("01 - Anmelden");
            System.out.println("02 - Registieren");
            System.out.println("03 - Passwort vergessen");
            System.out.println("04 - Beenden");	
	    System.out.print("Eingabe: ");
            
	    if(scanner.hasNextInt()){
                eingabe = scanner.nextInt();     
                switch(eingabe){
                    case 1:
                        anmelden();
                        break;
                    case 2:
                        registrieren();
                        break;
                    case 3:
                        resetPW();
                        break;
                    case 4:
                        System.out.println("\n-----> Anwendung beendet!");
			wiederholen = false;
                        break;
                    case 43:
                        entwicklerTools();
                        break;
                    default:    
                        System.out.println("\n-----> Ungueltige Eingabe!");
                        break;
                }
            } 
            else{
                System.out.println("\n-----> Ungueltige Eingabe!");
                scanner.next();
            }     
        } while(wiederholen);
    }
    
    private void resetPW() throws RemoteException, SQLException{
        Scanner scanner = new Scanner(inputStream);
        String username;
        String sicher;
        
        System.out.println("\n************ Passwort zurücksetzen ************\n");
        System.out.println("Benutzername eingeben:");
        username = scanner.nextLine();        
        System.out.println("Sind Sie sicher, dass Sie ihr Passwort zurücksetzen möchten? (j/n) ");
        sicher = scanner.nextLine();
        
        if(sicher.equals("j")){
            try {
                stub.resetPassword(username);
                System.out.println("Passwort erfolgreich zurückgesetzt!");
            } 
            catch (BenutzerException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    
    /**
     * TUI zum Anmelden
     * 
     * @throws RemoteException
     * @throws BenutzerException
     * @throws TerminException 
     */
    private void anmelden() throws RemoteException, TerminException, DatumException, Zeit.ZeitException, SQLException, DatenbankException{
        Scanner scanner = new Scanner(inputStream);
        String username, password, result;
                
        System.out.println("\n************ Anmelden ************\n");
        System.out.println("Benutzername eingeben:");
        username = scanner.nextLine();        
        System.out.println("Passwort eingeben:");
        password = scanner.nextLine();
            
        try{
            result = stub.findServerForUser(username);
            switch(result){
                case "true":                       
                    break;
                case "false":
                    System.out.println("\n-----> User existiert nicht!");
                    hauptbildschirm();
                    return;                  
                default:
                    Registry registry = LocateRegistry.getRegistry(result, 1099);                   
                    try {
                        stub = (ClientStub) registry.lookup("ClientStub");
                    } catch (NotBoundException | AccessException ex) {
                        Logger.getLogger(TUI.class.getName()).log(Level.SEVERE, null, ex);
                    }                   
            }      
            sitzungsID = stub.einloggen(username, password);
            if(sitzungsID < 0){
                System.out.println("\n-----> Falsches Passwort. Anmelden gescheitert!");
            }
            else{
                System.out.println("\n-----> Anmelden erfolgreich!");
                hauptbildschirm();
            }  
        }
        catch(BenutzerException e){
            System.out.println("\n-----> " + e.getMessage());
        }        
    }
    
    /**
     * TUI zum Registrieren
     * 
     * @throws RemoteException 
     */
    private void registrieren() throws RemoteException, SQLException{
        Scanner scanner = new Scanner(inputStream);
        String username, password, email, again;
        boolean wiederholen;
        
        System.out.println("\n************ Registieren ************\n");
	do{
        	System.out.println("Benutzername eingeben:");
        	username = scanner.nextLine(); 
        	System.out.println("Passwort eingeben:");
        	password = scanner.nextLine();
		System.out.println("E-Mail eingeben:");
        	email = scanner.nextLine();
                wiederholen = false;
		
        	try{
			stub.createUser(username, password, email);
			System.out.println("\n-----> Registrierung erfolgreich");
		}
		catch(BenutzerException e){
			System.err.println("\n" + e.getMessage());
			System.out.print("Eingabe wiederholen? (j/n) ");
			again = scanner.nextLine();
			if(again.equals("j") || again.equals("ja") || again.equals("y") || again.equals("yes")){
				wiederholen = true;
			}
			else{
				System.out.print("\n-----> Registrierung abgebrochen");
			}
        	}
	} while(wiederholen);            
    }

    /**
     * TUI zum Hauptbildschirm eines Benutzers
     * 
     * @throws RemoteException
     * @throws BenutzerException
     * @throws TerminException 
     */
    private void hauptbildschirm() throws RemoteException, BenutzerException, TerminException, DatumException, Zeit.ZeitException, SQLException{
        Scanner scanner = new Scanner(inputStream);
        int eingabe;
        boolean wiederholen = true;
                      
        do{
	    System.out.println("\n************ Hauptbildschirm ************\n");
            System.out.println("01 - Terminkalender");
            System.out.println("02 - Kontakte");
            System.out.println("03 - Benachrichtigungen");
            System.out.println("04 - Profil");	
            System.out.println("05 - Ausloggen");
	    System.out.print("Eingabe: ");
            
	    if(scanner.hasNextInt()){
                eingabe = scanner.nextInt();     
                switch(eingabe){
                    case 1:
                        terminkalender();
                        break;
                    case 2:
                        kontakte();
                        break;
                    case 3:
                        meldungen();
                        break;
                    case 4:
                        profil();
                        break;
                    case 5:
                        stub.ausloggen(sitzungsID);
                        System.out.println("\n-----> Ausloggen erfolgreich!");
                        wiederholen = false;
                        break;
                    default:    
                        System.out.println("\n-----> Ungueltige Eingabe!");
                        break;
                }
            } 
            else{
                System.out.println("\n-----> Ungueltige Eingabe!");
                scanner.next();
            }     
        } while(wiederholen);
    }

    /**
     * TUI zur Profilübersicht
     * 
     * @throws RemoteException
     * @throws BenutzerException 
     */
    private void profil() throws RemoteException, BenutzerException, SQLException{
        Scanner scanner = new Scanner(inputStream);
        int eingabe;
        boolean wiederholen = true;
                      
        do{
	    System.out.println("\n************ Profil ************\n");
            System.out.println("Benutzername: " + stub.getUsername(sitzungsID));
            System.out.println("Vorname: " + stub.getVorname(sitzungsID) + "(1)");
            System.out.println("Nachname: " + stub.getNachname(sitzungsID) + "(2)");
            System.out.println("E-Mail-Adresse: " + stub.getEmail(sitzungsID));
            System.out.println("Passwort ändern (3)");
            System.out.println("Zurück (0)");
	    System.out.print("Eingabe: ");
            
	    if(scanner.hasNextInt()){
                eingabe = scanner.nextInt();
                switch(eingabe){
                    case 1:
                        changeVorname();
                        break;
                    case 2:
                        changeNachname();
                        break;
                    case 3:
                        changePW();
                        break;
                    case 0:
                        wiederholen = false;
                        break;
                    default:    
                        System.out.println("\n-----> Ungueltige Eingabe!");
                        break;
                }
            } 
            else{
                System.out.println("\n-----> Ungueltige Eingabe!");
                scanner.next();
            }     
        } while(wiederholen);
    }

    /**
     * TUI um den Vornamen zu ändern
     * 
     * @throws RemoteException
     * @throws BenutzerException 
     */
    private void changeVorname() throws RemoteException, BenutzerException, SQLException {
        Scanner scanner = new Scanner(inputStream);
        String newName;

        System.out.print("Neuer Vorname: ");
        newName = scanner.nextLine();
        stub.changeVorname(newName, sitzungsID);
        System.out.println("\n-----> Vorname erfolgreich geändert!");
    }

    /**
     * TUI um den Namen zu ändern
     * 
     * @throws RemoteException
     * @throws BenutzerException 
     */
    private void changeNachname() throws RemoteException, BenutzerException, SQLException{
        Scanner scanner = new Scanner(inputStream);
        String newName;

        System.out.print("Neuer Nachname: ");
        newName = scanner.nextLine();
        stub.changeNachname(newName, sitzungsID);
        System.out.println("\n-----> Nachname erfolgreich geändert!");
    }

    /**
     * TUI um das Passwort zu ändern
     * 
     * @throws RemoteException 
     */
    private void changePW() throws RemoteException, SQLException {
        Scanner scanner = new Scanner(inputStream);
        String altesPW, neuesPW;

        System.out.print("Altes Passwort: ");
        altesPW = scanner.nextLine();
        System.out.print("Neues Passwort: ");
        neuesPW = scanner.nextLine();
        try {
            stub.changePasswort(altesPW, neuesPW, sitzungsID);
            System.out.println("\n-----> Passwort ändern erfolgreich!");
        } catch (BenutzerException e) {
            System.out.println("\n-----> " + e.getMessage());
        }
    }

    /**
     * TUI zum Kontaktmenü
     * 
     * @throws RemoteException
     * @throws BenutzerException 
     */
    private void kontakte() throws RemoteException, BenutzerException, SQLException {
        Scanner scanner = new Scanner(inputStream);
        int eingabe;
        boolean wiederholen = true;
                      
        do{
	    System.out.println("\n************ Kontakte ************\n");
            System.out.println("1 - Kontakte anzeigen");
            System.out.println("2 - Kontakt hinzufügen");
            System.out.println("3 - Kontakt löschen");
            System.out.println("0 - Zurück");	
	    System.out.print("Eingabe: ");
            
	    if(scanner.hasNextInt()){
                eingabe = scanner.nextInt();     
                switch(eingabe){
                    case 1:
                        showKontakte();
                        break;
                    case 2:
                        addKontakt();
                        break;
                    case 3:
                        removeKontakt();
                        break;
                    case 0:
                        wiederholen = false;
                        break;
                    default:    
                        System.out.println("\n-----> Ungueltige Eingabe!");
                        break;
                }
            } 
            else{
                System.out.println("\n-----> Ungueltige Eingabe!");
                scanner.next();
            }     
        } while(wiederholen);
    }

    /**
     * TUI um neuen Kontakt hinzuzufügen
     * 
     * @throws RemoteException 
     */
    private void addKontakt() throws RemoteException, SQLException {
        Scanner scanner = new Scanner(inputStream);
        String username;

        System.out.print("Username des Kontakts: ");
        username = scanner.nextLine();
        try {
            stub.addKontakt(username, sitzungsID);
            System.out.println("\n-----> Kontakt erfolgreich hinzugefügt!");
        } catch (BenutzerException e) {
            System.out.println("\n-----> " + e.getMessage());
        }
    }

    /**
     * TUI um Kontakt zu entfernen
     * 
     * @throws RemoteException 
     */
    private void removeKontakt() throws RemoteException, SQLException {
        Scanner scanner = new Scanner(inputStream);
        String username;

        System.out.print("Username des Kontakts: ");
        username = scanner.nextLine();
        try {
            stub.removeKontakt(username, sitzungsID);
            System.out.println("\n-----> Kontakt erfolgreich gelöscht!");
        } catch (BenutzerException e) {
            System.out.println("\n-----> " + e.getMessage());
        }
    }

    /**
     * TUI um Kontaktliste anzuzeigen
     * 
     * @throws RemoteException
     * @throws BenutzerException 
     */
    private void showKontakte() throws RemoteException, BenutzerException, SQLException {
        LinkedList<String> profil;
        int counter = 0, eingabe;
        Scanner scanner = new Scanner(inputStream);
        
        System.out.println("\n-----> Deine Kontakte(" + stub.getKontakte(sitzungsID).size() + "):");
        for(String kontakt : stub.getKontakte(sitzungsID)) {
            counter++;
            System.out.println(counter + ": " + kontakt);           
        }       
        if(counter > 0){
            System.out.print("\nWenn Sie ein Profil eines Kontaktes ansehen wollen, geben Sie die entsprechende Nummer ein: ");
            if(scanner.hasNextInt()){
                eingabe = scanner.nextInt();
                if(eingabe > 0 && eingabe <= counter){
                    profil = stub.getProfil(stub.getKontakte(sitzungsID).get(eingabe - 1));
                    System.out.println("Username: " + profil.get(0));
                    System.out.println("E-Mail: " + profil.get(1));
                    System.out.println("Vorname: " + profil.get(2));
                    System.out.println("Nachname: " + profil.get(3));
                }             
            }               
        }
        else{
            System.out.println("\nKeine Kontakte vorhanden!");
        }
        
    }

    /**
     * TUI zur Terminkalenderübersicht 
     * 
     * @throws RemoteException
     * @throws BenutzerException
     * @throws TerminException 
     */
    private void terminkalender() throws RemoteException, BenutzerException, TerminException, DatumException, Zeit.ZeitException, SQLException {
        Scanner scanner = new Scanner(inputStream);
        int eingabe;
        boolean wiederholen = true;
        LocalDate ld = LocalDate.now();

        int kw = (new Datum(ld.getDayOfMonth(), ld.getMonthValue(), ld.getYear())).getKalenderwoche();
                        
        do{
	    System.out.println("\n************ Terminkalender ************\n");
            System.out.println("1 - Wochenansicht");
            System.out.println("2 - Monatsansicht");
            System.out.println("3 - neuer Termin anlegen");
            System.out.println("0 - Zurück");	
	    System.out.print("Eingabe: ");
            
	    if(scanner.hasNextInt()){
                eingabe = scanner.nextInt();     
                switch(eingabe){
                    case 1:
                        wochenansicht(kw, ld.getYear());
                        break;
                    case 2:
                        monatsansicht(ld.getMonthValue(), ld.getYear());
                        break;
                    case 3:
                        terminAnlegen();
                        break;
                    case 0:
                        wiederholen = false;
                        break;
                    default:    
                        System.out.println("\n-----> Ungueltige Eingabe!");
                        break;
                }
            } 
            else{
                System.out.println("\n-----> Ungueltige Eingabe!");
                scanner.next();
            }     
        } while(wiederholen);
    }

    /**
     * TUI zum Anlegen eines Termins
     * 
     * @throws RemoteException
     * @throws BenutzerException 
     */
    private void terminAnlegen() throws RemoteException, BenutzerException, SQLException{
        Scanner scanner = new Scanner(inputStream);
        Datum datum;
        Zeit start, ende;
        String titel;
        int tag = 0, monat = 0, jahr = 0, stunde = 0, minute = 0;
        boolean nochmal = true;
        
        System.out.print("Titel: ");
        titel = scanner.nextLine();
        
        try{
            do{
                System.out.print("Tag(1-31):");
                if(scanner.hasNextInt()){
                    tag = scanner.nextInt();
                    nochmal = false;
                }
                else{
                    scanner.next();
                }  
            } while(nochmal);
            nochmal = true;
            do{
                System.out.print("Monat(1-12):");
                if(scanner.hasNextInt()){
                    monat = scanner.nextInt();
                    nochmal = false;
                }
                else{
                    scanner.next();
                }  
            } while(nochmal);
            nochmal = true;
            do{
                System.out.print("Jahr:");
                if(scanner.hasNextInt()){
                    jahr = scanner.nextInt();
                    nochmal = false;
                }
                else{
                    scanner.next();
                }  
            } while(nochmal);      
            datum = new Datum(tag, monat, jahr);
            
            nochmal = true;
            do{
                System.out.print("Start-Stunde:");
                if(scanner.hasNextInt()){
                    stunde = scanner.nextInt();
                    nochmal = false;
                }
                else{
                    scanner.next();
                }  
            } while(nochmal); 
            nochmal = true;
            do{
                System.out.print("Start-Minute:");
                if(scanner.hasNextInt()){
                    minute = scanner.nextInt();
                    nochmal = false;
                }
                else{
                    scanner.next();
                }  
            } while(nochmal); 
            start = new Zeit(stunde, minute);
            
            nochmal = true;
            do{
                System.out.print("End-Stunde:");
                if(scanner.hasNextInt()){
                    stunde = scanner.nextInt();
                    nochmal = false;
                }
                else{
                    scanner.next();
                }  
            } while(nochmal);
            nochmal = true;
            do{
                System.out.print("End-Minute:");
                if(scanner.hasNextInt()){
                    minute = scanner.nextInt();
                    nochmal = false;
                }
                else{
                    scanner.next();
                }  
            } while(nochmal); 
            ende = new Zeit(stunde, minute);

            stub.addTermin(datum, start, ende, titel, sitzungsID);
        }
        catch(DatumException | Zeit.ZeitException | TerminException e){
            System.out.println("\n-----> " + e.getMessage());
        }
            
        
        
    }

    /**
     * TUI zum Anzeigen der Kalenderwochenansicht der Termine
     * 
     * @throws RemoteException
     * @throws BenutzerException 
     */
    private void wochenansicht(int kw, int jahr) throws RemoteException, BenutzerException, TerminException, Zeit.ZeitException, SQLException {
        Scanner scanner = new Scanner(inputStream);
        LinkedList<Termin> dieseWoche;
        boolean nochmal = true;
        int eingabe, i;
        
        try {
            do{
                i = 1;
                dieseWoche = stub.getTermineInKalenderwoche(kw, jahr, sitzungsID);
                System.out.println("\n-----> " + dieseWoche.size() + " Termine im Jahr " + jahr + " in KW " + kw + ":");

                for(Termin termin : dieseWoche){
                    System.out.println(i + " - " + termin.getTitel() + " " + termin.getDatum().toString() + " " + termin.getBeginn().toString());
                    i++;
                }
            
                System.out.println("\n1 - Termin Bearbeiten/Löschen");
                System.out.println("2 - vorherige Woche");
                System.out.println("3 - nächste Woche");
                System.out.println("0 - zurück");
                System.out.print("Eingabe: ");
                
                if(scanner.hasNextInt()){
                    eingabe = scanner.nextInt();
                    switch (eingabe) {
                        case 2:
                            if(kw == 1){
                                kw = 53;
                                jahr--;
                            }
                            else{
                                kw--;
                            }
                            break;
                        case 3:
                            if(kw == 53){
                                kw = 1;
                                jahr++;
                            }
                            else{
                                kw++;
                            }
                            break;
                        case 0:
                            nochmal = false;
                            break;
                        case 1:
                            System.out.println("\nBitte Nummer des Termins zum Anzeigen/Bearbeiten/Löschen eingeben");
                            System.out.println("'0' um die Eingabe abzubrechen");
                            if(scanner.hasNextInt()){
                                eingabe = scanner.nextInt();
                                if(eingabe > 0 && eingabe <= dieseWoche.size()){
                                    terminAnzeigenBearbeiten(dieseWoche.get(eingabe - 1).getID());
                                }
                            }
                            else{
                                System.out.println("\n----> ungültige Eingabe!");
                            }
                            break;
                        default:
                            System.out.println("\n----> ungültige Eingabe!");
                            break;
                    }
                }
                else{
                    scanner.next();
                    System.out.println("\n----> ungültige Eingabe!");
                }        
            } while(nochmal); 
        } catch (DatumException e) {
            System.out.println(e.getMessage());
        }
        
    }

    /**
     * TUI zum Anzeigen der Monatsansicht der Termine
     * 
     * @throws BenutzerException
     * @throws RemoteException
     * @throws TerminException 
     */
    private void monatsansicht(int monat, int jahr) throws BenutzerException, RemoteException, TerminException, DatumException, Zeit.ZeitException, SQLException {
        Scanner scanner = new Scanner(inputStream);
        LinkedList<Termin> dieserMonat;
        boolean nochmal = true;
        int eingabe, i, terminID;
                
        do{
            i = 1;
            dieserMonat = stub.getTermineInMonat(monat, jahr, sitzungsID);
            System.out.println("\n-----> " + dieserMonat.size() + " Termine im Jahr " + jahr + " im Monat " + getMonthname(monat) + ":");
            
            for(Termin termin : dieserMonat){
                System.out.println(i + " - " + termin.getTitel() + " " + termin.getDatum().toString() + " " + termin.getBeginn().toString());
                i++;
            }
            
            System.out.println("\n1 - Termin Bearbeiten/Löschen");
            System.out.println("2 - vorheriger Monat");
            System.out.println("3 - nächster Monat");
            System.out.println("0 - zurück");
            System.out.print("Eingabe: ");
            
            if(scanner.hasNextInt()){
                eingabe = scanner.nextInt();
                switch (eingabe) {
                    case 2:
                        if(monat == 1){
                            monat = 12;
                            jahr --;
                        }
                        else{
                            monat--;
                        }
                        break;
                    case 3:
                        if(monat == 12){
                            monat = 1;
                            jahr++;
                        }
                        else{
                            monat++;
                        }
                        break;
                    case 0:
                        nochmal = false;
                        break;
                    case 1:
                        System.out.println("\nBitte Nummer des Termins zum Anzeigen/Bearbeiten/Löschen eingeben");
                        System.out.println("'0' um die Eingabe abzubrechen");
                        if(scanner.hasNextInt()){
                            terminID = scanner.nextInt();
                            if(terminID > 0 && terminID <= dieserMonat.size()){
                                terminAnzeigenBearbeiten(dieserMonat.get(terminID - 1).getID());
                            }
                        }
                        else{
                            System.out.println("\n----> ungültige Eingabe!");
                        }
                        break;
                default:
                    System.out.println("\n----> ungültige Eingabe!");
                    break;
                }   
            }
            else{
                scanner.next();
                System.out.println("\n----> ungültige Eingabe!");
            }
        } while(nochmal);
    }

    /**
     * TUI zum Anzeigen, Bearbeiten oder Löschen eines Termins
     * 
     * @param terminID
     * @throws RemoteException
     * @throws BenutzerException 
     */
    private void terminAnzeigenBearbeiten(int terminID) throws RemoteException, BenutzerException, DatumException, TerminException, SQLException {
        Scanner scanner = new Scanner(inputStream);
        int eingabe;
        boolean wiederholen = true, teilnehmer = false;
         
        for(Teilnehmer tn : stub.getTermin(terminID,sitzungsID).getTeilnehmerliste()){
            if(tn.getUsername().equals(stub.getUsername(sitzungsID))){
                teilnehmer = tn.checkIstTeilnehmer();
            }
        }
        do{
            if(!teilnehmer){
                System.out.println("\n************ Terminansicht ************\n");
                System.out.println("Titel: " + stub.getTermin(terminID, sitzungsID).getTitel());
                System.out.println("Datum: " + stub.getTermin(terminID, sitzungsID).getDatum().toString());
                System.out.println("Start: " + stub.getTermin(terminID, sitzungsID).getBeginn().toString());
                System.out.println("Ende:: " + stub.getTermin(terminID, sitzungsID).getEnde().toString());
                if(stub.getTermin(terminID, sitzungsID).getNotiz().length() > 20){
                    System.out.println("Notiz: " + stub.getTermin(terminID, sitzungsID).getNotiz().substring(0, 20) + "...(5)");
                }
                else{
                    System.out.println("Notiz: " + stub.getTermin(terminID, sitzungsID).getNotiz() + "(5)");
                }
                System.out.println("Ort: " + stub.getTermin(terminID, sitzungsID).getOrt());
                System.out.println("Teilnehmer anzeigen(7)");
                if(stub.getTermin(terminID, sitzungsID).getEditierbar()){
                    System.out.println("Bearbeitungsrecht: Jeder");
                }
                else{
                    System.out.println("Bearbeitungsrecht: Terminersteller");
                }      
                System.out.println("Terminersteller: " + stub.getTermin(terminID, sitzungsID).getOwner());
                System.out.println("Termin löschen");
                System.out.println("zurück(0)");
                System.out.print("Eingabe: ");
                
                if(scanner.hasNextInt()){
                    eingabe = scanner.nextInt();
                    switch(eingabe){
                        case 0:
                            wiederholen = false;
                            break;
                        case 5:
                            terminNotizAnzeigen(terminID);
                            break;
                        case 7:
                            terminTeilnehmerAnzeigen(terminID);
                            break;
                        default:    
                            System.out.println("\n-----> Ungueltige Eingabe!");
                            break;
                    }
                } 
                else{
                    System.out.println("\n-----> Ungueltige Eingabe!");
                    scanner.next();
                }         
            }          
            else{
                System.out.println("\n************ Terminansicht ************\n");
                System.out.println("Titel: " + stub.getTermin(terminID, sitzungsID).getTitel() + "(1)");
                System.out.println("Datum: " + stub.getTermin(terminID, sitzungsID).getDatum().toString() + "(2)");
                System.out.println("Start: " + stub.getTermin(terminID, sitzungsID).getBeginn().toString() + "(3)");
                System.out.println("Ende: " + stub.getTermin(terminID, sitzungsID).getEnde().toString() + "(4)");
                if(stub.getTermin(terminID, sitzungsID).getNotiz().length() > 20){
                    System.out.println("Notiz: " + stub.getTermin(terminID, sitzungsID).getNotiz().substring(0, 20) + "...(5)");
                }
                else{
                    System.out.println("Notiz: " + stub.getTermin(terminID, sitzungsID).getNotiz() + "(5)");
                }
                System.out.println("Ort: " + stub.getTermin(terminID, sitzungsID).getOrt() + "(6)");
                System.out.println("Teilnehmer anzeigen(7)");
                if(stub.getTermin(terminID, sitzungsID).getEditierbar()){
                    System.out.println("Bearbeitungsrecht: Jeder(8)");
                }
                else{
                    System.out.println("Bearbeitungsrecht: Terminersteller(8)");
                }      
                System.out.println("Terminersteller: " + stub.getTermin(terminID, sitzungsID).getOwner());
                System.out.println("Termin löschen(9)");
                System.out.println("zurück(0)");
                System.out.print("Eingabe: ");

                if(scanner.hasNextInt()){
                    eingabe = scanner.nextInt();
                    switch(eingabe){
                        case 0:
                            wiederholen = false;
                            break;
                        case 1:
                            terminTitelBearbeiten(terminID);
                            break;
                        case 2:
                            terminDatumBearbeiten(terminID);
                            break;
                        case 3:
                            terminStartBearbeiten(terminID);
                            break;
                        case 4:
                            terminEndeBearbeiten(terminID);
                            break;
                        case 5:
                            terminNotizBearbeiten(terminID);
                            break;
                        case 6:
                            terminOrtBearbeiten(terminID);
                            break;    
                        case 7:
                            terminTeilnehmerlisteBearbeiten(terminID);
                            break;
                        case 8:/*
                            try {
                                stub.changeEditierrechte(!stub.getTermin(terminID, sitzungsID).getEditierbar(), terminID, sitzungsID);
                            } catch (TerminException e) {
                                System.out.println(e.getMessage());
                            }*/
                            break;
                        case 9:
                            terminLoeschen(terminID);
                            wiederholen = false;
                            break;
                        default:    
                            System.out.println("\n-----> Ungueltige Eingabe!");
                            break;
                    }
                } 
                else{
                    System.out.println("\n-----> Ungueltige Eingabe!");
                    scanner.next();
                }     
            } 
        } while(wiederholen);
    }
    
    /**
     * TUI zum Bearbeiten des Titels eines Termins
     * 
     * @param terminID
     * @throws BenutzerException
     * @throws RemoteException 
     */
    private void terminTitelBearbeiten(int terminID) throws BenutzerException, RemoteException, SQLException {
        Scanner scanner = new Scanner(inputStream);
        String newTitel;
        
        System.out.print("\nNeuer Titel: ");
        newTitel = scanner.nextLine();
        /*try {           
            stub.changeTermintitel(terminID, newTitel, sitzungsID);
            System.out.println("-----> Titel erfolgreich geändert!");
        } catch (TerminException e) {
            System.out.println("----->" + e.getMessage());
        }   */
    }

    /**
     * TUI zum Bearbeiten des Ortes eines Termins
     * 
     * @param terminID
     * @throws BenutzerException
     * @throws RemoteException 
     */
    private void terminOrtBearbeiten(int terminID) throws BenutzerException, RemoteException, SQLException {
        Scanner scanner = new Scanner(inputStream);
        String newOrt;
        
        System.out.print("\nNeuer Ort: ");
        newOrt = scanner.nextLine();/*
        try {
            stub.changeTerminort(terminID, newOrt, sitzungsID);
            System.out.println("-----> Ort erfolgreich geändert!");
        } catch (TerminException e) {
            System.out.println("----->" + e.getMessage());
        }  */
    }

    /**
     * TUI zum Bearbeiten des Datums eines Termins
     * 
     * @param terminID
     * @throws Terminkalender.Datum.DatumException
     * @throws BenutzerException
     * @throws RemoteException
     * @throws TerminException 
     */
    private void terminDatumBearbeiten(int terminID) throws DatumException, BenutzerException, RemoteException, TerminException, SQLException{
        Scanner scanner = new Scanner(inputStream);
        int tag = 1, monat = 1, jahr = 1900;
        boolean nochmal = true;
        
        System.out.println("\nNeues Datum eingeben");
        do{
            System.out.print("Tag(1-31):");
            if(scanner.hasNextInt()){
                tag = scanner.nextInt();
                nochmal = false;
            }
            else{
                scanner.next();
            }  
        } while(nochmal);
        nochmal = true;
        do{
            System.out.print("Monat(1-12):");
            if(scanner.hasNextInt()){
                monat = scanner.nextInt();
                nochmal = false;
            }
            else{
                scanner.next();
            }  
        } while(nochmal);
        nochmal = true;
        do{
            System.out.print("Jahr:");
            if(scanner.hasNextInt()){
                jahr = scanner.nextInt();
                nochmal = false;
            }
            else{
                scanner.next();
            }  
        } while(nochmal); 
        //stub.changeTermindatum(terminID, new Datum(tag, monat, jahr), sitzungsID);
    }

    /**
     * TUI zum Bearbeiten der Notiz eines Termins
     * 
     * @param terminID
     * @throws BenutzerException
     * @throws RemoteException 
     */
    private void terminNotizBearbeiten(int terminID) throws BenutzerException, RemoteException, TerminException, SQLException {
        Scanner scanner = new Scanner(inputStream);
        String neueNotiz, eingabe;
        
        System.out.println("\n" + stub.getTermin(terminID, sitzungsID).getNotiz());
        System.out.print("Neue Notiz anlegen? (j/n)");
        eingabe = scanner.nextLine();     
        if(eingabe.equals("j")){
            System.out.println("\nNeue Notiz eingeben: (max. 200 Zeichen) ");
            neueNotiz = scanner.nextLine();/*
            try {
                stub.changeTerminnotiz(terminID, neueNotiz, sitzungsID);
                System.out.println("-----> Notiz erfolgreich geändert!");
            } catch (TerminException e) {
                System.out.println("----->" + e.getMessage());
            }  */
        }
    }
    
    /**
     * TUI um Notiz eines Termins anzuzeigen
     * 
     * @param terminID
     * @throws BenutzerException
     * @throws RemoteException
     * @throws TerminException 
     */
    private void terminNotizAnzeigen(int terminID) throws BenutzerException, RemoteException, TerminException {
        Scanner scanner = new Scanner(inputStream);
        
        System.out.println("\n" + stub.getTermin(terminID, sitzungsID).getNotiz()); 
        System.out.print("\nEingabe betätigen um zurück zu gelangen: ");
        scanner.next();
    }

    /**
     * TUI zum Bearbeiten der Startzeit eines Termins
     * 
     * @param terminID
     * @throws BenutzerException
     * @throws RemoteException 
     */
    private void terminStartBearbeiten(int terminID) throws BenutzerException, RemoteException, SQLException {
        Scanner scanner = new Scanner(inputStream);
        int stunde = 1, minute = 1;
        boolean nochmal = true;
        
        System.out.println("\nNeue Startzeit festlegen");
        do{
            System.out.print("Stunde:");
            if(scanner.hasNextInt()){
                stunde = scanner.nextInt();
                nochmal = false;
            }
            else{
                scanner.next();
            }  
        } while(nochmal); 
        nochmal = true;
        do{
            System.out.print("Minute:");
            if(scanner.hasNextInt()){
                minute = scanner.nextInt();
                nochmal = false;
            }
            else{
                scanner.next();
            }  
        } while(nochmal); 
        /*try{
            stub.changeTerminbeginn(terminID, new Zeit(stunde, minute), sitzungsID);
        } catch(ZeitException e){
            System.out.println("\n---->" + e.getMessage());
        } catch(TerminException e){
            System.out.println("\n---->" + e.getMessage());
        } */
    }

    /**
     * TUI zum Bearbeiten der Endzeit eines Termins
     * 
     * @param terminID
     * @throws BenutzerException
     * @throws RemoteException 
     */
    private void terminEndeBearbeiten(int terminID) throws BenutzerException, RemoteException, SQLException {
        Scanner scanner = new Scanner(inputStream);
        int stunde = 1, minute = 1;
        boolean nochmal = true;
        
        System.out.println("\nNeue Endzeit festlegen");
        do{
            System.out.print("Stunde:");
            if(scanner.hasNextInt()){
                stunde = scanner.nextInt();
                nochmal = false;
            }
            else{
                scanner.next();
            }  
        } while(nochmal); 
        nochmal = true;
        do{
            System.out.print("Minute:");
            if(scanner.hasNextInt()){
                minute = scanner.nextInt();
                nochmal = false;
            }
            else{
                scanner.next();
            }  
        } while(nochmal); /*
        try{
            stub.changeTerminende(terminID, new Zeit(stunde, minute), sitzungsID);
        } catch(TerminException e){
            System.out.println("\n---->" + e.getMessage());
        } catch (ZeitException e) {
            System.out.println("\n---->" + e.getMessage());
        }*/
    }

    /**
     * TUI zum Löschen eines Termins
     * 
     * @param terminID
     * @throws RemoteException
     * @throws BenutzerException 
     */
    private void terminLoeschen(int terminID) throws RemoteException, BenutzerException, TerminException, SQLException {
        Scanner scanner = new Scanner(inputStream);
        String eingabe;
        
        System.out.println("\nSind Sie sicher, dass sie den Termin löschen wollen?");
        System.out.println("Falls sie der Ersteller des Termins sind, wird er bei allen Teilnehmern gelöscht");
        System.out.print("Termin löschen? (j/n)");
        eingabe = scanner.nextLine();     
        if(eingabe.equals("j")){
            stub.removeTermin(terminID, sitzungsID);
            System.out.println("\nTermin wurde erfolgreich gelöscht!");
        }
    }

    /**
     * TUI zum Bearbeiten der Teilnehmerliste eines Termins
     * 
     * @param terminID
     * @throws RemoteException
     * @throws BenutzerException 
     */
    private void terminTeilnehmerlisteBearbeiten(int terminID) throws RemoteException, BenutzerException, TerminException, SQLException {
        Scanner scanner = new Scanner(inputStream);
        String username, eingabe;
        
        System.out.println("\nTeilnehmerliste:");
        for(Teilnehmer teilnehmer : stub.getTermin(terminID, sitzungsID).getTeilnehmerliste()){
            System.out.print(teilnehmer.getUsername());
            if(teilnehmer.checkIstTeilnehmer()){
                System.out.println(" (nimmt Teil)");
            }
            else{
                System.out.println(" (noch offen)");
            }
        }
        System.out.print("\nNeuen Teilnehmer hinzufügen? (j/n)");
        eingabe = scanner.nextLine();     
        if(eingabe.equals("j")){
            System.out.println("\nUsername: ");
            username = scanner.nextLine();
            try {
                stub.addTerminteilnehmer(terminID, username, sitzungsID);
                System.out.println("-----> Teilnehmer erfolgreich hizugefügt!");  
            } catch (TerminException e) {
                System.out.println("-----> " + e.getMessage());
            }    
        }
    }
    
    /**
     * TUI um Teilnehmer eines Termins anzuzeigen
     * 
     * @param terminID
     * @throws RemoteException
     */
    private void terminTeilnehmerAnzeigen(int terminID) throws RemoteException {
        Scanner scanner = new Scanner(inputStream);
        
        try{
            System.out.println("\nTeilnehmerliste:");
            for(Teilnehmer teilnehmer : stub.getTermin(terminID, sitzungsID).getTeilnehmerliste()){
                System.out.print(teilnehmer.getUsername());
                if(teilnehmer.checkIstTeilnehmer()){
                    System.out.println(" (nimmt Teil)");
                }
                else{
                    System.out.println(" (noch offen)");
                }
            }
            System.out.print("\nEingabe betätigen um zurück zu gelangen: ");
            scanner.nextLine();    
        }
        catch(TerminException | BenutzerException e){
            System.out.println("\n-----> " + e.getMessage());
        }
    }

    /**
     * 
     * @throws RemoteException
     * @throws BenutzerException
     * @throws TerminException 
     */
    private void meldungen() throws RemoteException, BenutzerException, TerminException, SQLException {
        Scanner scanner = new Scanner(inputStream);
        int eingabe, i;
        boolean wiederholen = true;
            
        do{
	    System.out.println("\n************ Meldungen ************\n");
            
            i = 0;
            for(Meldung meldung : stub.getMeldungen(sitzungsID)){
                i++;
                if(meldung.getText().length() > 20){
                    System.out.print(i  + "  " + meldung.getText().substring(0, 20) + "...");
                }
                else{
                    System.out.print(i  + "  " + meldung);
                }   
                if(meldung.getStatus()){
                    System.out.println("(gelesen)");
                }
                else{
                    System.out.println("(ungelesen)");
                }
            }
            System.out.println("\nNummer der Meldungen zum Lesen/Löschen eingeben oder '0' für zurück");
	    System.out.print("Eingabe: ");
            
	    if(scanner.hasNextInt()){
                eingabe = scanner.nextInt();     
                if(eingabe > 0 && eingabe <= stub.getMeldungen(sitzungsID).size()){
                    if(!stub.getMeldungen(sitzungsID).get(eingabe - 1).getStatus()){ 
                        stub.setMeldungenGelesen(eingabe - 1, sitzungsID);
                    }      
                    if(stub.getMeldungen(sitzungsID).get(eingabe - 1) instanceof Anfrage){
                        while(wiederholen){
                            System.out.println("\n" + ((Anfrage)stub.getMeldungen(sitzungsID).get(eingabe - 1)).getText());
                            System.out.println("1 - Termin annehmen");
                            System.out.println("2 - Termin ablehnen");
                            System.out.println("3 - alle Termin dieses Tages anzeigen");
                            System.out.println("0 - zurück");
                            System.out.print("Eingabe: ");
                            if(scanner.hasNextInt()){
                                i = scanner.nextInt();   
                                switch(i){
                                    case 1:
                                        try{
                                            stub.terminAnnehmen(((Anfrage)stub.getMeldungen(sitzungsID).get(eingabe - 1)).getTermin().getID(), sitzungsID);
                                            System.out.println("\n----> Termin zugesagt!");
                                        }
                                        catch(TerminException e){
                                            System.out.println("Termin existiert nicht mehr!");
                                        }
                                        stub.deleteMeldung(eingabe - 1, sitzungsID); 
                                        wiederholen = false;
                                        break;
                                    case 2:
                                        stub.terminAblehnen(((Anfrage)stub.getMeldungen(sitzungsID).get(eingabe - 1)).getTermin().getID(), sitzungsID);
                                        stub.deleteMeldung(eingabe - 1, sitzungsID);
                                        System.out.println("\n----> Termin abgelehnt!");
                                        wiederholen = false;
                                        break;
                                    case 3:
                                        termineDesTagesAnzeigen(((Anfrage)stub.getMeldungen(sitzungsID).get(eingabe - 1)).getTermin().getDatum());
                                        break;
                                    case 0 :
                                        wiederholen = false;
                                        break;
                                    default:
                                        System.out.println("\n----> ungültige Eingabe!");
                                        break;          
                                }
                            }
                            else{
                                System.out.println("\n----> ungültige Eingabe!");
                                scanner.next();
                            }
                        }
                        wiederholen = true;
                    }
                    else{
                        System.out.println("\n" + stub.getMeldungen(sitzungsID).get(eingabe - 1).text);
                        System.out.print("Meldung löschen? (ja=1): ");
                        if(scanner.hasNextInt()){
                            eingabe = scanner.nextInt();   
                            if(eingabe == 1){
                                stub.deleteMeldung(eingabe - 1, sitzungsID);
                            }
                        }
                        else{
                            System.out.println("\n----> ungültige Eingabe!");
                            scanner.next();
                        }
                    }     
                }
                else if(eingabe == 0){
                    wiederholen = false;
                }
            } 
            else{
                System.out.println("\n-----> Ungueltige Eingabe!");
                scanner.next();
            }     
        } while(wiederholen);
    }

    /**
     * TUI zum Anzeigen aller Termine eines Tages (für Meldung)
     * 
     * @param heute
     * @throws RemoteException
     */
    private void termineDesTagesAnzeigen(Datum heute) throws RemoteException, SQLException {
        Scanner scanner = new Scanner(inputStream);
        LinkedList<Termin> dieserTag;
        boolean nochmal = true;
        int eingabe, i;  
        
        try {
            do{
                i = 1;
                dieserTag = stub.getTermineAmTag(heute, sitzungsID);
                System.out.println("\n-----> " + dieserTag.size() + " Termine am " + heute.toString() + ":");

                for(Termin termin : dieserTag){
                    System.out.println(i + " - " + termin.getTitel() + " " + termin.getDatum().toString() + " " + termin.getBeginn().toString());
                    i++;
                }
            
                System.out.println("\nx - Termin 'x' anzeigen");
                System.out.println("0 - zurück");
                System.out.print("Eingabe: ");
                
                if(scanner.hasNextInt()){
                    eingabe = scanner.nextInt();
                    if(eingabe == 0){
                        nochmal = false;
                    }
                    else if(eingabe > 0 && eingabe <= dieserTag.size()){
                        terminAnzeigenBearbeiten(dieserTag.get(eingabe - 1).getID());
                    }
                    else{
                        System.out.println("\n----> ungültige Eingabe!");
                    }
                }
                else{
                    scanner.next();
                    System.out.println("\n----> ungültige Eingabe!");
                }        
            } while(nochmal); 
        } catch (TerminException | BenutzerException | DatumException e) {
            System.out.println("\n-----> " + e.getMessage());
        }    
    }

    /**
     * TUI zum Debuggen
     */
    private void entwicklerTools() {
        
    }
    
    private String getMonthname(int monat){
        String result = null;
        switch(monat){
            case 1:
                result = "Januar";
                break;
            case 2:
                result = "Februar";
                break;
            case 3:
                result = "März";
                break;
            case 4:
                result = "April";
                break; 
            case 5:
                result = "Mai";
                break;
            case 6:
                result = "Juni";
                break;
            case 7:
                result = "Juli";
                break;
            case 8:
                result = "August";
                break; 
            case 9:
                result = "September";
                break;
            case 10:
                result = "Oktober";
                break;
            case 11:
                result = "November";
                break;
            case 12:
                result = "Dezember";
                break;        
        }
        return result;       
    }
}
