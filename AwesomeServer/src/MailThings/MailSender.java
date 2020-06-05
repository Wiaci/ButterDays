package MailThings;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailSender {

    public static void send(String user, String password) throws MessagingException {
        Authenticator auth = new ServerAuthenticator("apistoletov72@gmail.com", "a1exandrP1s");
        Properties props = System.getProperties();
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.mime.charset", "UTF-8");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        Session session = Session.getDefaultInstance(props, auth);

        if (!user.matches(".+@gmail\\.com")) throw new AddressException();

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("apistoletov72@gmail.com"));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(user)); //3lordrairely@gmail.com
        msg.setSubject("Вам повiстка");
        msg.setText("Ваш логин: " + user + "\nВаш пароль: " + password);
        Transport.send(msg);
    }
}
