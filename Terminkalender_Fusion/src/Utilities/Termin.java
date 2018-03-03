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
    
    //Getter 
    public boolean getEditierbar(){
        return editierbar;
    }
    public String getOwner(){
        return owner;
    }
    public int getID(){
        return terminID;
    }
    public Datum getDatum(){
        return datum;
    }
    public Zeit getBeginn(){
        return beginn;
    }
    public Zeit getEnde(){
        return ende;
    }
    public String getTitel(){
        return titel;
    }
    public String getNotiz(){
        return notiz;
    }
    public String getOrt(){
        return ort;
    }
    public int getTimestemp(){
        return timestemp;
    }
    public int getEditorID(){
        return editorID;
    }
    public final LinkedList<Teilnehmer> getTeilnehmerliste(){
        return teilnehmer;
    }
    
    // Setter
    public void setEditierbar(boolean editierbar, String username) throws TerminException{
        if(!username.equals(this.owner)){
            throw new TerminException("Nur der Ersteller des Termins kann die Rechte aendern!");
        }
        this.editierbar = editierbar;
    }
    public void setNotiz(String notiz, String username) throws TerminException{
        if(!username.equals(this.owner) && !editierbar){
            throw new TerminException("Nur der Ersteller des Termins kann die Notiz ändern!");
        }
        this.notiz = notiz;
    }   
    public void setOrt(String ort, String username) throws TerminException{
        if(!username.equals(this.owner) && !editierbar){
            throw new TerminException("Nur der Ersteller des Termins kann den Ort ändern!");
        }
        this.ort = ort;
    }
    public void setTitel(String neuerTitel, String username) throws TerminException{
        if(!username.equals(this.owner) && !editierbar){
            throw new TerminException("Nur der Ersteller des Termins kann den Titel ändern!");
        }
        titel = neuerTitel;
    }
    public void setBeginn(Zeit neuerBeginn, String username) throws TerminException{
        if(!username.equals(this.owner) && !editierbar){
            throw new TerminException("Nur der Ersteller des Termins kann den Beginn ändern!");
        }
        if(!anfangVorEnde(neuerBeginn, ende)){
            throw new TerminException("Startzeitpunkt darf nicht nach dem Endzeitpunkt liegen!");
        }
        beginn = neuerBeginn;
    }
    public void setEnde(Zeit neuesEnde, String username) throws TerminException{
        if(!username.equals(this.owner) && !editierbar){
            throw new TerminException("Nur der Ersteller des Termins kann das Ende ändern!");
        }
        if(!anfangVorEnde(beginn, neuesEnde)){
            throw new TerminException("Startzeitpunkt darf nicht nach dem Endzeitpunkt liegen!");
        }
        ende = neuesEnde;
    }
    public void setDatum(Datum neuesDatum, String username) throws TerminException{
        if(!username.equals(this.owner) && !editierbar){
            throw new TerminException("Nur der Ersteller des Termins kann das Datum ändern!");
        }
        datum = neuesDatum;
    }
    public void setEditorID(int editorID){
        this.editorID = editorID;
    }
    public void setTimestemp(int timestemp){
        this.timestemp = timestemp;
    }
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
