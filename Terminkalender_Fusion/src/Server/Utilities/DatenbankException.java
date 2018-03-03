/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Utilities;

/**
 *
 * @author timtim
 */
public class DatenbankException extends Exception {

    private final String message;
    
    public DatenbankException(String message) {
        this.message = message;
    }
    
    @Override
    public String getMessage(){
        return this.message;
    }
}
