package tests.constructors.value;

import framework.context.ContextBuilder;
import framework.exceptions.IncorrectFieldAnnotationsException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws IncorrectFieldAnnotationsException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        var context = new ContextBuilder().Build();
        context.Run(Main.class);

        var src = context.getType(RemoteSource.class);
        var url = src.getUrl();
    }
}
