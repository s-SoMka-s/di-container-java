package cases.server.db.implementations;

import cases.server.db.DbContext;
import cases.server.db.interfaces.Repository;
import cases.server.entitiies.Mail;

import javax.inject.Inject;
import java.util.List;

public class MailsRepository implements Repository<Mail> {
    @Inject
    private DbContext dbContext;

    @Override
    public List<Mail> getAll() {
        return dbContext.getMails();
    }

    @Override
    public void add(Mail entity) {
        dbContext.addMail(entity);
    }
}
