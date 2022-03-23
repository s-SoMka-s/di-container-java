package cases.app.servicies;

public class GoogleMailService implements MailService {
    @Override
    public void sendMail() {
        System.out.println("Send mail to google");
    }
}
