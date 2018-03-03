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
public class Terminkalender implements Serializable{

    private final LinkedList<Termin> terminkalender;
    
    Terminkalender(){
        this.terminkalender = new LinkedList<>();
    }
       
    /**
     * 
     * @param id
     * @return 
     * @throws Utilities.TerminException 
     */
    public Termin getTerminByID(int id) throws TerminException{
        for(Termin termin : terminkalender){
            if(termin.getID() == id){
                return termin;
            }
        }
        throw new TerminException("kein Termin mit dieser ID vorhannden");
    }
    
    /**
     * Hilfsmethode für getTerminImMonat und getTerminInWoche
     * 
     * @param termin der hinzugefügt werden soll 
     */
    public void addTermin(Termin termin){
        terminkalender.add(termin);
    }
    
    /**
     * gibt alle Termine im Monat 'monat' in LinkedList zurück
     * 
     * @param monat Monat der gesucht wird
     * @param jahr Jeweiliges Jahr
     * @return Auszug der Termine in einem Monat
     * @throws Utilities.TerminException 
     */
    public LinkedList<Termin> getTermineImMonat(int monat, int jahr) throws TerminException{
        LinkedList<Termin> monatsauszug = new LinkedList<>();
        
        for(Termin termin : terminkalender){
            if(termin.getDatum().getMonat() == monat && termin.getDatum().getJahr() == jahr){
                monatsauszug.add(termin);
            }
        }    
        return monatsauszug;
    }
    
    /**
     * Gibt die Termine an einem Tag zurück 
     * @param datum Das datum mit dem Tag dessen Termine gesucht werden
     * @return Auszug der Termine an einem Tag 
     * @throws TerminException 
     */
    public LinkedList<Termin> getTermineAmTag(Datum datum) throws TerminException{
        LinkedList<Termin> monatsauszug = new LinkedList<>();
        
        for(Termin termin : terminkalender){
            if(termin.getDatum().equal(datum)){
                monatsauszug.add(termin);         
            }
        }    
        return monatsauszug;
    }
    
    /**
     * gibt alle Termine der übergebenen Kalenderwoche in LinkedList zurück
     * 
     * @param kalenderwoche Kalenderwoche aus der die Termine Angezeigt werden
     * @param jahr jeweiliges Jahr
     * @return Auszug der Termine an einer Woche
     */
    public LinkedList<Termin> getTermineInWoche(int kalenderwoche, int jahr) {
        LinkedList<Termin> wochenauszug = new LinkedList<>();
        
        for(Termin termin : terminkalender){
            if(termin.getDatum().getKalenderwoche() == kalenderwoche && termin.getDatum().getJahr() == jahr){
                wochenauszug.add(termin);
            }
        }       
        return wochenauszug;
    }

    public boolean updateTermin(Termin termin, String username, int userID) throws TerminException{
        for(Termin alterTermin : terminkalender){
            if(alterTermin.getID() == termin.getID()){
                try{
                    alterTermin.setBeginn(termin.getBeginn(), username);
                    alterTermin.setEnde(termin.getEnde(), username);
                }
                catch(TerminException ex){                    
                    alterTermin.setEnde(termin.getEnde(), username);
                    alterTermin.setBeginn(termin.getBeginn(), username);
                }
                alterTermin.setDatum(termin.getDatum(), username);
                alterTermin.setNotiz(termin.getNotiz(), username);
                alterTermin.setOrt(termin.getOrt(), username);
                alterTermin.setTitel(termin.getTitel(), username);              
                return true;
            }
        }
        return false;
    }
    
    /**
     * Entfernt einen Termin
     * @param id ID des Termins der netfernt werden soll
     * @throws Utilities.TerminException 
     */
    public void removeTerminByID(int id) throws TerminException{
        if(terminkalender.contains(getTerminByID(id))){
            terminkalender.remove(getTerminByID(id));
        }
    }
    
}