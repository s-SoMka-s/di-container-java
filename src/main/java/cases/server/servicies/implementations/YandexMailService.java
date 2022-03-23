package cases.server.servicies.implementations;

import cases.server.servicies.interfaces.MailService;

public class YandexMailService implements MailService {
    @Override
    public void sendMail() {
        System.out.println("Send mail by yandex mailer");
    }
}
