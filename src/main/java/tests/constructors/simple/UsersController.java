package tests.constructors.simple;

import framework.annotations.Autowired;
import framework.annotations.Component;

@Component
public class UsersController {
    private final UsersRepository repository;

    @Autowired
    public UsersController(UsersRepository repository) {
        this.repository = repository;
    }

    public int getUsersCount() {
        return this.repository.getUsersCount();
    }
}
