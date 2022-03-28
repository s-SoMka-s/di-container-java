package tests.constructors.simple;

import framework.context.ContextBuilder;
import framework.exceptions.IncorrectFieldAnnotationsException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws IncorrectFieldAnnotationsException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        var context = new ContextBuilder().Build();
        context.run(Main.class);

        var controller = context.getType(UsersController.class);
        var count = controller.getUsersCount();
    }
}
