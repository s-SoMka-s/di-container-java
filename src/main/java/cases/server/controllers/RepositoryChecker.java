package cases.server.controllers;

import cases.server.db.interfaces.Repository;
import cases.server.entitiies.Mail;
import cases.server.entitiies.User;
import framework.annotations.Autowired;
import framework.annotations.Component;

@Component
public class RepositoryChecker {
    private final Repository<User> users;
    private final Repository<Mail> mails;

    @Autowired
    public RepositoryChecker(Repository<User> users, Repository<Mail> mails) {
        this.users = users;
        this.mails = mails;
    }
}
