package tests.fields.named_simple;

import framework.annotations.Component;

@Component("mails")
public class MailsRepository {
    public int getMailsCount(){
        return 10;
    }
}
