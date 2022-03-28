package tests.fields.named_simple;

import framework.annotations.Component;
import framework.annotations.Inject;

@Component("users")
public class UsersRepository {
    public int getUsersCount(){
        return 5;
    }
}
