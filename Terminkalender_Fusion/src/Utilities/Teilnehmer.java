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
public class Teilnehmer implements Serializable{
    
    private final String username;
    private boolean nimmtTeil;
    
    Teilnehmer(String username){
        this.username = username;
        this.nimmtTeil = false; 
    }
    
    /**
     * 
     * @return 
     */
    public String getUsername(){
        return username;
    }
    
    /**
     * 
     * @return 
     */
    public boolean checkIstTeilnehmer(){
        return nimmtTeil;
    }
    
    public void setIstTeilnemer(){
        nimmtTeil = true;
    }
}