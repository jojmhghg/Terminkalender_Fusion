/*
 * ~~~ erstmal fertig ~~~
 * 
 * 
 */
package Utilities;

/**
 *
 * @author Tim Meyer
 */
public enum Wochentag {
    Montag(1),
    Dienstag(2), 
    Mittwoch(3),
    Donnerstag(4),
    Freitag(5),
    Samstag(6),
    Sonntag(7);
    
    private final int wert;
    
    private Wochentag(int wert){
        this.wert = wert;
    }    
    
    public int getWert(){
        return wert;
    }      
}
