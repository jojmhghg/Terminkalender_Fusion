/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

import java.io.Serializable;

/**
 *
 * @author Tim Meyer
 */
public class Anfrage extends Meldung implements Serializable{
    
    private final Termin termin;
    private final String absender;
    
    public Anfrage(String text, Termin termin, String absender, int meldungsID){
        super(text, meldungsID);
        this.termin = termin;
        this.absender = absender;
    }
    
    public Termin getTermin(){
        return termin;
    }
    
    public String getAbsender(){
        return absender;
    }
    
    @Override
    public String getText(){
        return absender + " lädt sie zu dem Termin " + termin.getTitel() + " am " + termin.getDatum() + " ein.";
    }
}
