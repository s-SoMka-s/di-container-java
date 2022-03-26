package cases.server.db;

import cases.server.entitiies.Mail;
import cases.server.entitiies.User;
import framework.annotations.Component;
import framework.annotations.Scope;
import framework.annotations.Value;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope(framework.enums.Scope.SINGLETON)
public class DbContext {
    private List<User> users;
    private List<Mail> mails;

    public DbContext(@Value("10") int usersCount, @Value("5") int mailsCount) {
        this.users = new ArrayList(usersCount);
        this.mails = new ArrayList(mailsCount);
    }

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
