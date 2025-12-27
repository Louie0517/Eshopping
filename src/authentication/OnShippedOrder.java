package authentication;

import java.util.Properties;

import util.AmountUtil;
import util.DateUtil;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class OnShippedOrder {
    private static final String host = "smtp.gmail.com";
    private static final  String username = "brentcasas0517@gmail.com";
    private static final String password = "wzyo nvhi lung uikw";

    public static void sendOnShippedOrderEmail(String recipient, String prodName, 
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

            String onShippedEmailTxt =
                "<html>" +
                    "<body style='font-family:Segoe UI, Arial, sans-serif; background-color:#f9fafb; padding:20px;'>" +

                        "<div style='max-width:600px; margin:auto; background-color:#ffffff; " +
                        "border:1px solid #e5e7eb; border-radius:8px; padding:24px;'>" +

                            "<h2 style='color:#111827; margin-bottom:16px;'>Good news, " + recipient + "!</h2>" +

                            "<p>" +
                                "Your order has been <strong>shipped</strong> and is on its way to you." +
                            "</p>" +

                            "<hr style='border:none; border-top:1px solid #E5E7EB; margin:20px 0;'>" +

                            "<h3>Order Details</h3>" +
                            "<p>" +
                                 "<strong>Product: </strong>" + prodName + " <br>" +
                                "<strong>Total Amount: </strong>" + AmountUtil.formatTotalAmount(totalAmount) + " Pesos<br>" +
                                "<strong>Shipping Date: </strong> " + DateUtil.timeFormat() + "<br>"+
                                "<strong>Status: </strong> " + status +
                            "</p>" +

                            "<p>" +
                                "You can track your package using the tracking number above. " +
                                "Delivery usually takes 2-5 business days depending on your location." +
                            "</p>" +

                            "<p>" +
                                "If you have any questions or need assistance, feel free to contact our support team anytime." +
                            "</p>" +

                            "<p style='margin-top:30px;'>" +
                                "Thank you for choosing <strong>Eshopping</strong>. " +
                                "We hope you enjoy your purchase!" +
                            "</p>" +

                            "<p style='margin-top:20px;'>" +
                                "Best regards,<br>" +
                                "<strong>Eshopping Team</strong>" +
                            "</p>" +

                            "<hr style='border:none; border-top:1px solid #E5E7EB; margin:30px 0;'>" +

                            "<p style='font-size:12px; color:#6B7280;'>" +
                                "This is an automated email. Please do not reply directly to this message." +
                            "</p>" +

                        "</div>" +
                    "</body>" +
                "</html>";
   
            msg.setContent(onShippedEmailTxt, "text/html");

        Transport.send(msg);
        System.out.println("Email successfully sent!");
        } catch (MessagingException mex){
            mex.printStackTrace();
    }

    }
} 