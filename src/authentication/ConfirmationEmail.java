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

            String confirmationEmailTxt =
                "<html>" +
                "<body style='font-family:Segoe UI, Arial, sans-serif; background-color:#f9fafb; padding:20px;'>" +
                    "<div style='max-width:600px; margin:auto; background-color:#ffffff; " +
                    "border:1px solid #e5e7eb; border-radius:8px; padding:24px;'>" +
                        "<h2 style='color:#111827; margin-bottom:16px;'>Hello " + recepient + " 👋</h2>" +
                        "<p style='color:#374151; font-size:14px; line-height:1.6;'>" +
                            "Welcome to <strong>Eshopping</strong>! We’re excited to have you on board." +
                        "</p>" +
                        "<p style='color:#374151; font-size:14px; line-height:1.6;'>" +
                            "Your account has been <strong>successfully created</strong>. " +
                            "You can now log in and start browsing products, adding items to your cart, " +
                            "and enjoying a smooth and secure shopping experience." +
                        "</p>" +
                        "<div style='background-color:#f1f5f9; padding:12px 16px; border-radius:6px; margin:16px 0;'>" +
                            "<p style='margin:0; color:#1f2937; font-size:14px;'>" +
                                "<strong>Registered Email:</strong><br>" +
                                recepient +
                            "</p>" +
                        "</div>" +
                        "<p style='color:#374151; font-size:14px; line-height:1.6;'>" +
                            "If you did not create this account, please ignore this email or contact our support team immediately." +
                        "</p>" +
                        "<p style='color:#374151; font-size:14px; line-height:1.6;'>" +
                            "Thank you for choosing <strong>Eshopping</strong>. We’re glad to have you with us!" +
                        "</p>" +
                        "<hr style='border:none; border-top:1px solid #e5e7eb; margin:24px 0;' />" +
                        "<p style='color:#6b7280; font-size:13px;'>" +
                            "Best regards,<br>" +
                            "<strong>Eshopping Team</strong>" +
                        "</p>" +
                    "</div>" +
                "</body>" +
                "</html>";

            msg.setContent(confirmationEmailTxt, "text/html");

            Transport.send(msg);
            System.out.println("Email successfully sent!");
        } catch (MessagingException mex){
            mex.printStackTrace();
        }
        
        
    }
}

