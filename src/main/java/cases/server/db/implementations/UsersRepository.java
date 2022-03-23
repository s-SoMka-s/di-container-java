package cases.server.db.implementations;

import cases.server.db.DbContext;
import cases.server.db.interfaces.Repository;
import cases.server.entitiies.User;

import javax.inject.Inject;
import java.util.List;

public class UsersRepository implements Repository<User> {
    @Inject
    private DbContext dbContext;

    @Override
    public List<User> getAll() {
        return dbContext.getUsers();
    }

    @Override
    public void add(User entity) {
        dbContext.addUser(entity);
    }
}
