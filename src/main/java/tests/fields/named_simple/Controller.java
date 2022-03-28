package tests.fields.named_simple;

import framework.annotations.Component;
import framework.annotations.Inject;

@Component
public class Controller {
    @Inject("users")
    private UsersRepository users;

    public int getCount () {
        return users.getUsersCount();
    }
}
