package cases.server;

import cases.server.controllers.UsersController;
import cases.server.servicies.implementations.GoogleMailService;
import framework.context.ContextBuilder;
import framework.exceptions.IncorrectFieldAnnotationsException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Server {
    public static void main (String[] args) throws IncorrectFieldAnnotationsException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        var builder = new ContextBuilder();

        var context = builder.Build();

        context.run(Server.class);

        var mailer = context.getType(GoogleMailService.class);
        mailer.sendMail();

        var userController = context.getType(UsersController.class);
        userController.addUser("Nick");
        userController.addUser("Tom");
        userController.allSayHi();
    }
}
