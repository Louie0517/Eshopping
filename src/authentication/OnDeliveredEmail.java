package authentication;

import java.time.LocalDate;
import java.util.Properties;

import util.AmountUtil;
import util.DateUtil;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class OnDeliveredEmail {
    private static final String host = "smtp.gmail.com";
    private static final  String username = "brentcasas0517@gmail.com";
    private static final String password = "wzyo nvhi lung uikw";

    public static void sendOnDeliveredOrderEmail(String recipient, String prodName,
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
                new InternetAddress(recipient));

        String onDeliveredEmailTxt =
    "<html>" +
        "<body style='font-family:Segoe UI, Arial, sans-serif; background-color:#f9fafb; padding:20px;'>" +

            "<div style='max-width:600px; margin:auto; background-color:#ffffff; " +
            "border:1px solid #e5e7eb; border-radius:8px; padding:24px;'>" +

                "<h2 style='color:#111827; margin-bottom:16px;'>" +
                    "Hello, " + recipient + "," +
                "</h2>" +

                "<hr style='border:none; border-top:1px solid #E5E7EB; margin:20px 0;'>" +

                "<p style='color:#374151; line-height:1.6;'>" +
                    "We're excited to let you know that your order has been " +
                    "<strong>successfully received</strong> and is now " +
                    "<strong>being processed</strong>. Our team is preparing your items for shipment." +
                "</p>" +

                "<hr style='border:none; border-top:1px solid #E5E7EB; margin:20px 0;'>" +

                "<h3 style='color:#111827; margin-bottom:12px;'>Order Details</h3>" +
                "<p style='color:#374151; line-height:1.6;'>" +
                    "<strong>Product Name:</strong> " + prodName + "<br>" +
                    "<strong>Total Amount:</strong> " + AmountUtil.formatTotalAmount(totalAmount) + " Pesos<br>" +
                    "<strong>Order Date:</strong> " + LocalDate.now() + "<br>" +
                    "<strong>Processing Time:</strong> " + DateUtil.timeFormat() + "<br>" +
                    "<strong>Status:</strong> " + status +
                "</p>" +

            "</div>" +
        "</body>" +
    "</html>";



        msg.setContent(onDeliveredEmailTxt, "text/html");

        Transport.send(msg);
        System.out.println("Email successfully sent!");
        } catch (MessagingException mex){
            mex.printStackTrace();
    }
}
   
}
