package cases.server.entitiies;

public class User {
    private String name;

    public User(String name) {
        this.name = name;
    }

    public void sayHi() {
        System.out.println(name + "says: Hi!\n");
    }
}
