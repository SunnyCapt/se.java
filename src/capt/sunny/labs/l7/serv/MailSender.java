package capt.sunny.labs.l7.serv;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailSender {

    private String username;
    private String password;
    private Properties props;

    public MailSender(String _username, String _password) {
        username = _username;
        password = _password;

        props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
    }


    public void send(String text, String toEmail) throws  RuntimeException{

        try {
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("BFLI: sign in");
            message.setText(text);

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Cannt send message to " + toEmail);
        }
    }
}
