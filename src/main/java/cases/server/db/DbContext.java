package cases.server.db;

import cases.server.entitiies.Mail;
import cases.server.entitiies.User;
import framework.annotations.Component;
import framework.annotations.Scope;

import java.util.ArrayList;
import java.util.List;

@Component()
@Scope(framework.enums.Scope.SINGLETON)
public class DbContext {
    private List<User> users = new ArrayList();
    private List<Mail> mails = new ArrayList();

    public List<User> getUsers(){
        return users;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public List<Mail> getMails(){
        return mails;
    }

    public void addMail(Mail mail) {
        mails.add(mail);
    }
}
