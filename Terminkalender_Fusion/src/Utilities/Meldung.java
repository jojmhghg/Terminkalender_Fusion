/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

import java.io.Serializable;

/**
 *
 * @author timtim
 */
public class Meldung implements Serializable{
    
    public final String text;
    public boolean gelesen;
    public int meldungsID;
    
    public Meldung(String text, int meldungsID){
        this.text = text;
        this.gelesen = false;
        this.meldungsID = meldungsID;
    }
    
    public void meldungGelesen(){
        this.gelesen = true;
    }
    
    public String getText(){
        return this.text;
    }
    
    public boolean getStatus(){
        return this.gelesen;
    }
}
