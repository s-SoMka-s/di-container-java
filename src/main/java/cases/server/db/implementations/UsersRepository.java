package cases.server.db.implementations;

import cases.server.db.DbContext;
import cases.server.db.interfaces.Repository;
import cases.server.entitiies.User;
import framework.annotations.Autowired;
import framework.annotations.Component;

import java.util.List;

@Component("usersRepository")
public class UsersRepository implements Repository<User> {
    private final DbContext dbContext;

    @Autowired
    public UsersRepository(DbContext dbContext) {
        this.dbContext = dbContext;
    }

    @Override
    public List<User> getAll() {
        return dbContext.getUsers();
    }

    @Override
    public void add(User entity) {
        dbContext.addUser(entity);
    }
}
