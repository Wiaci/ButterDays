package MailThings;

import javax.mail.*;

public class ServerAuthenticator extends Authenticator {

    private String login;
    private String password;

    public ServerAuthenticator(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public PasswordAuthentication getPasswordAuthentication() {
        String login = this.login;
        String password = this.password;
        return new PasswordAuthentication(login, password);
    }
}
