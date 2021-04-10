package com.timeline;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Properties;

/**
 * Static class handling account recovery.
 * Methods for sending recovery emails and generating temporary passwords.
 * @author Joel Salo js225fg
 */
public class AccountRecovery {
    private static final String MAIL_USERNAME = "timewavesapplication@gmail.com";
    private static final String MAIL_PASSWORD = "TimeWaves123!";

    /**
     * Sends a recovery email to the user containing the randomly generated password, to be changed on the next login.
     * @author Joel Salo js225fg
     */
    public static void sendEmail(User user, String temporaryPassword) {

        String username = user.getUsername();

        String to = user.getEmail();
        String from = "timewavesapplication@gmail.com";

        String host = "smtp.gmail.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        // get the Session object.
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(MAIL_USERNAME, MAIL_PASSWORD);
                    }
                });

        try {
            // create a default MimeMessage object
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(from, "TimeWaves"));

            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));

            // set Subject
            message.setSubject("Account Recovery");

            // this mail has 2 part, the BODY and the embedded image
            MimeMultipart multipart = new MimeMultipart("related");

            // first part (the html)
            BodyPart messageBodyPart = new MimeBodyPart();
            String htmlText = "<img src=\"cid:image\"><h1>Password reset</h1><br>" +
                    "<p>We recently received a request to reset the password for account: <strong>" + username +
                    "</strong>.</p>" + "<p>Not your account? Someone else likely entered your email by mistake, " +
                    "feel free to " + "disregard this!</p><br>" + "<ul><li><strong>Temporary password:</strong> " +
                    temporaryPassword + "</li></ul><br>" + "<p>You will be asked to change your password the next time you login.</p>" +
                    "<p>Best regards, <br>The TimeWaves team</p>";

            messageBodyPart.setContent(htmlText, "text/html");

            // add it
            multipart.addBodyPart(messageBodyPart);

            // second part (the image)
            messageBodyPart = new MimeBodyPart();
            DataSource fds = new FileDataSource(
                    "src/main/resources/images/TWLogo.png");

            messageBodyPart.setDataHandler(new DataHandler(fds));
            messageBodyPart.setHeader("Content-ID", "<image>");

            // add image to the multipart
            multipart.addBodyPart(messageBodyPart);

            // put everything together
            message.setContent(multipart);

            // send message
            Transport.send(message);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a random password, meant for temporary use in case of forgotten-/reset password event.
     * @return - the new password (String)
     * @author Joel Salo js225fg
     */
    public static String generatePassword() {
        return randomGenerator();
    }

    // helper method in order to abstract the RNG-algorithm for security reasons
    private static String randomGenerator() {
        SecureRandom random = new SecureRandom();

        // variables for restricting the RNG bounds (indices are referring to ASCII)
        int length = 14;
        int startIndex = 48;
        int endIndex = 122;

        return random.ints(startIndex, endIndex + 1)
                .filter(i -> Character.isAlphabetic(i) || Character.isDigit(i))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
    }
}
