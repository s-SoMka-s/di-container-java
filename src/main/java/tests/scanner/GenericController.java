package tests.scanner;

import framework.annotations.Autowired;
import framework.annotations.Component;

@Component
public class GenericController {
    @Autowired
    public GenericController(GenericRepository<User> users, GenericRepository<Mail> mails) {

    }
}
