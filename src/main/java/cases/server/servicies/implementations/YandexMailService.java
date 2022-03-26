package cases.server.servicies.implementations;

import cases.server.servicies.interfaces.MailService;
import framework.annotations.Component;

@Component("yandexMailer")
public class YandexMailService implements MailService {
    @Override
    public void sendMail() {
        System.out.println("Send mail by yandex mailer");
    }
}
