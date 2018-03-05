/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * 
 * @author Tim Meyer
 */
public class Termin implements Serializable{   
   //Variablen Deklaration und Initialisierung
    private final int terminID;
    private Datum datum;
    private Zeit beginn;
    private Zeit ende;
    private String titel;
    private String notiz;
    private String ort;
    private LinkedList<Teilnehmer>teilnehmer;
    /* Rechte für Verwaltung: */
    private final String owner;
    private boolean editierbar;
    /* */
    private int timestemp;
    private int editorID;
      
    /**
     * Konstruktor
     * @param datum
     * @param beginn
     * @param ende
     * @param titel
     * @param terminID
     * @param username
     * @throws TerminException 
     */
    public Termin(Datum datum, Zeit beginn, Zeit ende, String titel, int terminID, String username) throws TerminException{
        if(!anfangVorEnde(beginn, ende)){
            throw new TerminException("Startzeitpunkt darf nicht nach dem Endzeitpunkt liegen!");
        }   
        if(titel.length() == 0){
            throw new TerminException("Titel darf nicht leer sein!");
        } 
        
        this.terminID = terminID;
        this.datum = datum;
        this.beginn = beginn;
        this.ende = ende;
        this.titel = titel;
        this.notiz = "";
        this.ort = "";
        this.teilnehmer = new LinkedList<>();
        this.teilnehmer.add(new Teilnehmer(username));
        teilnehmer.getFirst().setIstTeilnemer();
        this.owner = username;
        this.editierbar = false;
        this.timestemp = 0;
        this.editorID = 0;
    }
    
    /**
     * Konstruktor
     * @param datum
     * @param beginn
     * @param ende
     * @param titel
     * @param id
     * @param owner
     * @param ort
     * @param notiz
     * @param editierbar
     * @throws TerminException 
     */
    public Termin(Datum datum, Zeit beginn, Zeit ende, String titel, int id, String owner, String ort, String notiz, Boolean editierbar) throws TerminException{
        if(!anfangVorEnde(beginn, ende)){
            throw new TerminException("Startzeitpunkt darf nicht nach dem Endzeitpunkt liegen!");
        }
        
        this.terminID = id;
        this.datum = datum;
        this.beginn = beginn;
        this.ende = ende;
        this.titel = titel;
        this.notiz = notiz;
        this.ort = ort;
        this.teilnehmer = new LinkedList<>();
        this.teilnehmer.add(new Teilnehmer(owner));
        teilnehmer.getFirst().setIstTeilnemer();
        this.owner = owner;
        this.editierbar = editierbar;
        this.timestemp = 0;
        this.editorID = 0;
    }

    /**
     * fügt der Teilnehmerliste 'teilnehmer' den Teilnehmer 'usename' hinzu
     * 
     * @param username 
     * @throws Utilities.TerminException 
     */
    public void addTeilnehmer(String username) throws TerminException{
        for(Teilnehmer user : teilnehmer){
            if(user.getUsername().equals(username)){
                throw new TerminException(username + " bereits auf der Teilnehmerliste");
            }
        }
        teilnehmer.add(new Teilnehmer(username));
    }
    
    /**
     * -------------Getter-------------
     */
    
    
    /**
     * Methode, um Rechte zum editieren zu bekommen
     * @return 
     */
    public boolean getEditierbar(){
        return editierbar;
    }
    
    /**
     * Methode, um Besitzer zu bekommen
     * @return 
     */
    public String getOwner(){
        return owner;
    }
    
    /**
     * Methode, um ID zu bekommen
     * @return 
     */
    public int getID(){
        return terminID;
    }
    
    /**
     * Methode, um Datum zu bekommen
     * @return 
     */
    public Datum getDatum(){
        return datum;
    }
    
    /**
     * Methode, um Beginn des Termins zu bekommen
     * @return 
     */
    public Zeit getBeginn(){
        return beginn;
    }
    
    /**
     * Methode, um Ende des Termins zu bekommen
     * @return 
     */
    public Zeit getEnde(){
        return ende;
    }
    
    /**
     * Methode, um Titel des Termins zu bekommen
     * @return 
     */
    public String getTitel(){
        return titel;
    }
    
    /**
     * Methode, um Notiz des Termins zu bekommen
     * @return 
     */
    public String getNotiz(){
        return notiz;
    }
    
    /**
     * Methode, um Ort des Termins zu bekommen
     * @return 
     */
    public String getOrt(){
        return ort;
    }
    
    /**
     * Methode, um Zeitpunkt des Termins zu bekommen
     * @return 
     */
    public int getTimestemp(){
        return timestemp;
    }
    
    /**
     * Methode, um Editor-ID des Termins zu bekommen
     * @return 
     */
    public int getEditorID(){
        return editorID;
    }
    
    /**
     * Methode, um Teilnehmerliste des Termins zu bekommen
     * @return 
     */
    public final LinkedList<Teilnehmer> getTeilnehmerliste(){
        return teilnehmer;
    }
    
    /**
     * -------------Setter-------------
     */
    
    /**
     * Methode, um Termin editierbar zu machen
     * @param editierbar
     * @param username
     * @throws TerminException 
     */
    public void setEditierbar(boolean editierbar, String username) throws TerminException{
        if(!username.equals(this.owner)){
            throw new TerminException("Nur der Ersteller des Termins kann die Rechte aendern!");
        }
        this.editierbar = editierbar;
    }
    
    /**
     * Methode, um in Termin eine Notiz hinzuzufügen
     * @param notiz
     * @param username
     * @throws TerminException 
     */
    public void setNotiz(String notiz, String username) throws TerminException{
        if(!username.equals(this.owner) && !editierbar){
            throw new TerminException("Nur der Ersteller des Termins kann die Notiz ändern!");
        }
        this.notiz = notiz;
    } 
    
    /**
     * Methode, um in Termin einen Ort zu setzen
     * @param ort
     * @param username
     * @throws TerminException 
     */
    public void setOrt(String ort, String username) throws TerminException{
        if(!username.equals(this.owner) && !editierbar){
            throw new TerminException("Nur der Ersteller des Termins kann den Ort ändern!");
        }
        this.ort = ort;
    }
    
    /**
     * Methode, um in Termin einen neuen Titel zu setzen
     * @param neuerTitel
     * @param username
     * @throws TerminException 
     */
    public void setTitel(String neuerTitel, String username) throws TerminException{
        if(!username.equals(this.owner) && !editierbar){
            throw new TerminException("Nur der Ersteller des Termins kann den Titel ändern!");
        }
        titel = neuerTitel;
    }
    
    /**
     * Methode, um in Termin ein Beginn zu setzen
     * @param neuerBeginn
     * @param username
     * @throws TerminException 
     */
    public void setBeginn(Zeit neuerBeginn, String username) throws TerminException{
        if(!username.equals(this.owner) && !editierbar){
            throw new TerminException("Nur der Ersteller des Termins kann den Beginn ändern!");
        }
        if(!anfangVorEnde(neuerBeginn, ende)){
            throw new TerminException("Startzeitpunkt darf nicht nach dem Endzeitpunkt liegen!");
        }
        beginn = neuerBeginn;
    }
    
    /**
     * Methode, um in Termin ein Ende zu setzen
     * @param neuesEnde
     * @param username
     * @throws TerminException 
     */
    public void setEnde(Zeit neuesEnde, String username) throws TerminException{
        if(!username.equals(this.owner) && !editierbar){
            throw new TerminException("Nur der Ersteller des Termins kann das Ende ändern!");
        }
        if(!anfangVorEnde(beginn, neuesEnde)){
            throw new TerminException("Startzeitpunkt darf nicht nach dem Endzeitpunkt liegen!");
        }
        ende = neuesEnde;
    }
    
    /**
     * Methode, um in Termin ein Datum zu setzen
     * @param neuesDatum
     * @param username
     * @throws TerminException 
     */
    public void setDatum(Datum neuesDatum, String username) throws TerminException{
        if(!username.equals(this.owner) && !editierbar){
            throw new TerminException("Nur der Ersteller des Termins kann das Datum ändern!");
        }
        datum = neuesDatum;
    }
    
    /**
     * Methode, um in Termin eine Editor-ID zu setzen
     * @param editorID 
     */
    public void setEditorID(int editorID){
        this.editorID = editorID;
    }
    
    /**
     * Methode, um in Termin einen Zeitpunkt zu setzen
     * @param timestemp 
     */
    public void setTimestemp(int timestemp){
        this.timestemp = timestemp;
    }
    
    /**
     * Methode, um in Termin die Dauer zu erhöhen
     */
    public void incTimestemp(){
        this.timestemp++;
    }
        
    /**
     * Methode die, die eingeladenen Teilnehmer als teilnehmend kennzeichnet
     * @param username User dessen Teilnahme bestätigt werden soll 
     * @throws Utilities.TerminException 
     */
    public void changeTeilnehmerNimmtTeil(String username) throws TerminException{
        boolean error = true;
        for(Teilnehmer tl : teilnehmer){
            if(tl.getUsername().equals(username)){
                tl.setIstTeilnemer();
                error = false;
            }
        }
        if(error){
            throw new TerminException(username + " nicht in der Teilnehmerliste vorhanden");
        }
    }
    
    /**
     * Löscht Teilnehmer in einem Bestimmten Termin
     * @param username User der vob der Teilnehmerliste gestrichen werden soll.
     * @throws TerminException 
     */
    public void removeTeilnehmer(String username) throws TerminException{
        boolean error = true;
        for(Teilnehmer tl : teilnehmer){
            if(tl.getUsername().equals(username)){
                teilnehmer.remove(tl);
            }
        }
        if(error){
            throw new TerminException(username + " nicht in der Teilnehmerliste vorhanden");
        }
    }
    
    /**
     * 
     * @param beginn
     * @param ende
     * @return 
     */
    private boolean anfangVorEnde(Zeit beginn, Zeit ende){
        boolean result = true;
        if(ende.getStunde() < beginn.getStunde()){
            result = false;
        }
        else if(ende.getStunde() == beginn.getStunde()){
            if(ende.getMinute() < beginn.getMinute()){
                result = false;
            }
        }
        return result;
    }
    
    /**
     * toSting-Methode
     * @return 
     */
    @Override
    public String toString(){
        return "ID: " + terminID
                + " Datum: " + datum
                + " Start: " + beginn
                + " Ende: " + ende
                + " Titel: " + titel
                + " Notiz: " + notiz
                + " Ort: " + ort
                + " Timestemp: " + timestemp;
    }
}
