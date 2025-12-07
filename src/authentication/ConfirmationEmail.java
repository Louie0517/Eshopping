package authentication;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;



public class ConfirmationEmail {
   
    private static final String host = "smtp.gmail.com";
    private static final  String username = "brentcasas0517@gmail.com";
    private static final String password = "wzyo nvhi lung uikw";
    
    public static void sendEmail(String recepient) {
        
        Properties props = System.getProperties();

        props.put("mail.smtp.auth", "true"); 
        props.put("mail.smtp.starttls.enable", "true"); 
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(username, password);
            }
        });

        try{
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(username));

            msg.addRecipient(Message.RecipientType.TO, 
                new InternetAddress(recepient));

            msg.setSubject("Welcome to Eshoping");

            String htmlBody = "<html><body>" +
                  "<h2>Hello " + recepient + "!</h2>" +
                  "<p>Congratulations! Your account has been successfully created at <strong>Eshopping</strong>.</p>" +
                  "<p>You can now log in using your registered email: <strong>" + recepient + "</strong></p>" +
                  "<p>Best regards,<br>Eshopping Team</p>" +
                  "</body></html>";

            msg.setContent(htmlBody, "text/html");

            Transport.send(msg);
            System.out.println("Email successfully sent!");
        } catch (MessagingException mex){
            mex.printStackTrace();
        }
        
        
    }
}

