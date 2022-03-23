package cases.server.servicies.implementations;

import cases.server.servicies.interfaces.MailService;

public class GoogleMailService implements MailService {
    @Override
    public void sendMail() {
        System.out.println("Send mail by google mailer");
    }
}
