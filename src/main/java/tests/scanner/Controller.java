package tests.scanner;

import framework.annotations.Autowired;
import framework.annotations.Component;

@Component
public class Controller {
    @Autowired
    public Controller(Repository repository) {

    }
}
