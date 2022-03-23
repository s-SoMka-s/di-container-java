package cases.server.servicies.implementations;

import cases.server.servicies.interfaces.MailService;
import framework.annotations.Component;
import framework.annotations.Inject;
import framework.annotations.Value;

@Component
public class GoogleMailService implements MailService {
    @Value("5")
    private int Delay;

    public GoogleMailService(@Value("5") int delay) {
        this.Delay = delay;
    }


    @Override
    public void sendMail() {
        System.out.println("Send mail by google mailer");
    }
}
