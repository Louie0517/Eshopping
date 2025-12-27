package authentication;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

import util.*;

import util.DateUtil;


public class CheckOutEmail {
    private static final String host = "smtp.gmail.com";
    private static final  String username = "brentcasas0517@gmail.com";
    private static final String password = "wzyo nvhi lung uikw";


    public static void sendEmail(String recepient, String prodName, LocalDateTime orderDate, 
        double totalAmount, String status) {
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

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String date = orderDate.format(dateFormatter);

           
            msg.setSubject("Thank you for checking out!");
            String checkoutEmailTxt =
                "<html>" +
                "<body style='font-family:Segoe UI, Arial, sans-serif; background-color:#f9fafb; padding:20px;'>" +

                    "<div style='max-width:600px; margin:auto; background-color:#ffffff; " +
                    "border:1px solid #e5e7eb; border-radius:8px; padding:24px;'>" +

                        "<h2 style='color:#111827; margin-bottom:16px;'>Thank you for your purchase, " + recepient + "!</h2>" +

                        "<p style='color:#374151; font-size:14px; line-height:1.6;'>" +
                            "We truly appreciate you shopping with <strong>Eshopping</strong>. " +
                            "Your order has been <strong>successfully placed</strong>, and we’re already preparing it for processing." +
                        "</p>" +

                        "<div style='background-color:#f1f5f9; padding:14px 16px; border-radius:6px; margin:16px 0;'>" +
                            "<p style='margin:0; color:#1f2937; font-size:14px;'>" +
                                "<strong>Order Summary</strong><br>" +
                                "Product: <strong>" + prodName + "</strong><br>" +
                                "Total Amount: <strong>" + totalAmount + " Pesos</strong><br>" +
                                "Status: <strong>" + status + "</strong><br>" +
                                "Date: <strong>" + date + "</strong><br>" +
                                "Time: <strong>" + DateUtil.timeFormat() + "</strong>" +
                            "</p>" +
                        "</div>" +

                        "<p style='color:#374151; font-size:14px; line-height:1.6;'>" +
                            "You’ll receive another email once your order is on its way. " +
                            "In the meantime, feel free to continue browsing and discover more great deals on Eshopping." +
                        "</p>" +

                        "<p style='color:#374151; font-size:14px; line-height:1.6;'>" +
                            "If you have any questions about your order, our support team is always here to help." +
                        "</p>" +

                        "<hr style='border:none; border-top:1px solid #e5e7eb; margin:24px 0;' />" +

                        "<p style='color:#6b7280; font-size:13px;'>" +
                            "Thank you for choosing <strong>Eshopping</strong>.<br>" +
                            "We look forward to serving you again!" +
                        "</p>" +

                        "<p style='color:#6b7280; font-size:13px; margin-top:12px;'>" +
                            "Warm regards,<br>" +
                            "<strong>Eshopping Team</strong>" +
                        "</p>" +

                    "</div>" +

                "</body>" +
                "</html>";

        msg.setContent(checkoutEmailTxt, "text/html");

        Transport.send(msg);
        System.out.println("Email successfully sent!");
        } catch (MessagingException mex){
            mex.printStackTrace();
        }


    }
}
