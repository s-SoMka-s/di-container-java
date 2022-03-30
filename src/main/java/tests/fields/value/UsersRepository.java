package tests.fields.value;

import framework.annotations.Component;
import framework.annotations.Value;

@Component("users")
public class UsersRepository {
    @Value("5")
    private int count;

    public int getUsersCount(){
        return this.count;
    }
}
