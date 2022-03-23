package cases.server.controllers;

import cases.server.db.interfaces.Repository;
import cases.server.entitiies.User;

import javax.inject.Inject;

public class UsersController {
    @Inject
    private Repository<User> users;

    public void addUser(String name) {
        users.add(new User(name));
    }

    public void allSayHi(){
        users.getAll().forEach(u -> u.sayHi());
    }
}
