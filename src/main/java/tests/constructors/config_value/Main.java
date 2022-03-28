package tests.constructors.config_value;

import framework.context.ContextBuilder;
import framework.exceptions.IncorrectFieldAnnotationsException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws IncorrectFieldAnnotationsException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        var context = new ContextBuilder().setConfiguration("src/main/java/tests/constructors/config_value/config.json").Build();
        context.run(Main.class);

        var src = context.getType(RemoteSource.class);
        var url = src.getUrl();
    }
}
