package tests.fields.named_simple;

import framework.context.ContextBuilder;
import framework.exceptions.IncorrectFieldAnnotationsException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws IncorrectFieldAnnotationsException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        var builder = new ContextBuilder();
        var context = builder.Build();
        context.Run(Main.class);

        var controller = context.getType(Controller.class);
        var count = controller.getCount();
    }
}
