package tests.constructors.simple;

import framework.annotations.Component;

@Component
public class UsersRepository {
    public int getUsersCount() {
        return 5;
    }
}
