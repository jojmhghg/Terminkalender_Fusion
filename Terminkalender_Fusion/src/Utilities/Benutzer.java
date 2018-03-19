/*
 * TODO: alles mit todo markiert + Metoden für getTermineImMonat & getTermineInWoche
 * implementieren
 * 
 */
package Utilities;

import Server.Utilities.EMailService;
import java.awt.Color;
import java.util.LinkedList;
import java.io.Serializable;
import java.security.SecureRandom;

/**
 *
 * @author Tim Meyer
 */
public class Benutzer implements Serializable{
    
    private final int userID;
    private final String username;
    private String vorname;
    private String nachname;
    private String email;
    private String passwort;
    
    private int color;
    
    private final Terminkalender terminkalender;
    private LinkedList<String> kontaktliste; 
    private LinkedList<Meldung> meldungen;   
    
    /**
     * Konstruktor, genutzt von der DB
     * 
     * @param username
     * @param passwort
     * @param email
     * @param userID
     * @param vorname
     * @param nachname
     * @param color
     */
    public Benutzer(String username, String passwort, String email, int userID, String vorname, String nachname, int color){        
        this.userID = userID;
        this.email = email;
        this.username = username;
        this.passwort = passwort;
        this.nachname = nachname;
        this.vorname = vorname;
        
        this.terminkalender = new Terminkalender();
        this.kontaktliste = new LinkedList<>();
        this.meldungen = new LinkedList<>(); 
        this.color = color;
    }
    
    //Getter:
    public String getUsername(){
        return username;
    }
    public String getPasswort(){
        return passwort;
    }
    public String getNachname(){
        return nachname;
    }
    public String getVorname(){
        return vorname;
    }
    public String getEmail(){
        return email;
    } 
    public Terminkalender getTerminkalender(){
        return terminkalender;
    }
    public final LinkedList<String> getKontaktliste(){
        return kontaktliste;
    }
    public LinkedList<Meldung> getMeldungen(){
        return meldungen;
    }    
    public int getUserID(){
        return this.userID;
    }
    public int getColor(){
        return color;
    }
    
    //Setter:
    public void setNachname(String nachname){
        this.nachname = nachname;
    }
    public void setVorname(String vorname){
        this.vorname = vorname;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setColor(Integer color){
        this.color = color;
    }
    public void setPasswort(String neuesPasswort) throws BenutzerException{
        if(neuesPasswort.length() < 4 || neuesPasswort.length() > 12){
            throw new BenutzerException("Das Passwort sollte zwischen 4 und 12 Zeichen lang sein");
        }
        this.passwort = neuesPasswort;
    }
    public void setKontaktliste(LinkedList<String> kontaktliste){
        this.kontaktliste = kontaktliste;
    }
    public void setMeldungen(LinkedList<Meldung> meldungen){
        this.meldungen = meldungen;
    }
    
    
    /**
     * Sendet eine Email an den User mit einem neuen Passwort
     * 
     * @return passwort das neu erstellt worden ist.
     */
    public String resetPasswort(){
        String message;
        EMailService emailService = new EMailService();
        String allowedChars = "0123456789abcdefghijklmnopqrstuvwABCDEFGHIJKLMNOP!?";
        SecureRandom random = new SecureRandom();
        StringBuilder pass = new StringBuilder(10);
    
        //zufälliges Passwort generieren (10 Zeichen)
        for (int i = 0; i < 10; i++) {
            pass.append(allowedChars.charAt(random.nextInt(allowedChars.length())));
        }
               
        passwort = pass.toString();
        message = "Ihr neues Passwort lautet: " + passwort ;
        emailService.sendMail(email, "Terminkalender: Passwort zurückgesetzt", message);
 
        return passwort;
    }
       
    /**
     * 
     * @param passwort
     * @return 
     */
    public boolean istPasswort(String passwort){
        return (this.passwort.equals(passwort));
    }
    
    /**
     * 
     * @param username 
     * @throws Utilities.BenutzerException 
     */
    public void addKontakt(String username) throws BenutzerException{
        if(username.equals(this.username)){
            throw new BenutzerException("Du kannst dich nicht selbst hinzufügen!");
        }
        for(String kontakt : kontaktliste){
            System.out.println(kontakt + " + user: " + username);
            if(kontakt.equals(username)){
                throw new BenutzerException(username + " bereits in der Kontaktliste vorhanden!");
            }
        }
        kontaktliste.add(username);
    }
    
    /**
     * 
     * @param username 
     * @throws Utilities.BenutzerException 
     */
    public void removeKontakt(String username) throws BenutzerException{
        boolean inListe = false;
        for(String kontakt : kontaktliste){
            if(kontakt.equals(username)){
                inListe = true;
            }
        }
        if(!inListe){
            throw new BenutzerException(username + " nicht in der Kontaktliste vorhanden!");
        }
        kontaktliste.remove(username);
    }
    
    /**
     * 
     * @param termin
     */
    public void addTermin(Termin termin){
        terminkalender.addTermin(termin);
    }
    
    /**
     * 
     * @param anfrage  
     */
    public void addAnfrage(Anfrage anfrage){
        meldungen.add(anfrage);
    }
    
    /**
     *
     * @param meldung
     */
    public void addMeldung(Meldung meldung){
        meldungen.add(meldung);
    }
    
    /**
     * 
     * @param meldungsID 
     * @throws Utilities.BenutzerException 
     */
    public void deleteMeldung(int meldungsID) throws BenutzerException{
        int counter = 0;
        int index = -1;
        for(Meldung meldung : meldungen){
            if(meldung.meldungsID == meldungsID){
                index = counter;
            }
            counter++;
        } 
        if(index == -1){
            throw new BenutzerException("Meldung mit ID: " + meldungsID + " nicht auf Server vorhanden");
        }
        meldungen.remove(index);
    }
    
    /**
     * entfernt eine Anfrage mit Hilfe der TerminID
     * 
     * @param terminID 
     * @throws Utilities.BenutzerException 
     */
    public void deleteAnfrage(int terminID) throws BenutzerException{  
        int counter = 0;
        int index = -1;
        for(Meldung meldung : meldungen){
            if(meldung instanceof Anfrage){
                if(((Anfrage) meldung).getTermin().getID() == terminID){
                    index = counter;
                }
            }
            counter++;
        } 
        if(index == -1){
            throw new BenutzerException("Anfrage zu Termin mit ID: " + terminID + " nicht auf Server vorhanden");
        }
        meldungen.remove(index);
    }
    
    /**
     * 
     * @return 
     */
    public LinkedList<String> getProfil(){
        LinkedList<String> profil = new LinkedList<>();
        profil.add(this.username);
        profil.add(this.email);
        profil.add(this.vorname);
        profil.add(this.nachname);
        return profil;
    }
}
    

