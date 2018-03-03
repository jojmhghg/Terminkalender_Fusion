/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Utilities;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.internet.*;
/**
 *
 * @author TimMeyer
 */
public class EMailService {
    
    Session session;
    
    public EMailService(){
        session = getGMailSession("terminkalenderserviceteam@gmail.com", "hallowelt123");        
    }
    
    public void sendMail(String receiver, String subject, String message){
        try {
            postMail(session, receiver, subject, message);
        } catch (MessagingException ex) {
            Logger.getLogger(EMailService.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    private static void postMail(Session session, String recipient, String subject, String message) throws MessagingException{
        Message msg = new MimeMessage(session);

        InternetAddress addressTo = new InternetAddress(recipient);
        msg.setRecipient(Message.RecipientType.TO, addressTo);

        msg.setSubject(subject);
        msg.setContent(message, "text/plain");
        Transport.send(msg);
    }
    
    private static Session getGMailSession(String user, String pass){
        final Properties props = new Properties();
        
        props.setProperty( "mail.pop3.host", "pop.gmail.com" );
        props.setProperty( "mail.pop3.user", user );
        props.setProperty( "mail.pop3.password", pass );
        props.setProperty( "mail.pop3.port", "995" );
        props.setProperty( "mail.pop3.auth", "true" );
        props.setProperty( "mail.pop3.socketFactory.class",
                           "javax.net.ssl.SSLSocketFactory" );
        
        props.setProperty( "mail.smtp.host", "smtp.gmail.com" );
        props.setProperty( "mail.smtp.auth", "true" );
        props.setProperty( "mail.smtp.port", "465" );
        props.setProperty( "mail.smtp.socketFactory.port", "465" );
        props.setProperty( "mail.smtp.socketFactory.class",
                           "javax.net.ssl.SSLSocketFactory" );
        props.setProperty( "mail.smtp.socketFactory.fallback", "false" );
    
        return Session.getInstance(props, new Authenticator(){
            @Override 
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(props.getProperty("mail.pop3.user"), props.getProperty("mail.pop3.password"));
            }
        });
    }
}
