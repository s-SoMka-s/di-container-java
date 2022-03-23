package cases.server.controllers;

import cases.server.db.interfaces.Repository;
import cases.server.entitiies.User;
import framework.annotations.Component;
import framework.annotations.Inject;

@Component
public class UsersController {
    @Inject("usersRepository")
    private Repository<User> users;

    public void addUser(String name) {
        users.add(new User(name));
    }

    public void allSayHi(){
        users.getAll().forEach(User::sayHi);
    }
}
