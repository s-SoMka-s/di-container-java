package framework.beans;

import framework.components.ComponentClass;
import framework.context.NewContext;
import framework.exceptions.IncorrectFieldAnnotationsException;
import framework.extensions.NameExtensions;
import framework.extensions.ScopeExtensions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class BeanFactory {
    private final NewContext context;

    public BeanFactory(NewContext context) {
        this.context = context;
    }

    public Bean createBeanFromComponent(ComponentClass component) {
        Object instance = null;
        try {
            instance = createInstance(component);
        } catch (IncorrectFieldAnnotationsException | IOException | InvocationTargetException | NoSuchMethodException |
                InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return new Bean(component, instance);
    }

    public Bean createBean(Class<?> item) {
        var name = NameExtensions.getComponentName(item);
        var scope = ScopeExtensions.getScope(item);

        Object instance = null;
        try {
            instance = createInstance(item);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException |
                IncorrectFieldAnnotationsException | IOException e) {
            e.printStackTrace();
        }
        if (instance == null) {
            return null;
        }

        return new Bean(item, name, scope, instance);
    }

    private Object createInstance(ComponentClass component) throws IncorrectFieldAnnotationsException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        var beanClass = component.getType();
        var injector = context.getInjector();
        var instance = injector.trtInjectIntoConstructor(component);
        return createInstance(beanClass);
    }

    private Object createInstance(Class<?> beanClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IncorrectFieldAnnotationsException, IOException {
        var injector = this.context.getInjector();

        var constructors = beanClass.getConstructors();
        if (constructors.length != 1) {
            throw new RuntimeException("Only one constructor should be presented");
        }

        var constructor = constructors[0];

        var instance = injector.trtInjectIntoConstructor(beanClass, constructor);

        if (instance == null) {
            return null;
        }

        var fields = beanClass.getDeclaredFields();
        for (var field : fields) {
            injector.tryInjectIntoField(beanClass, field, instance);
        }

        return instance;
    }
}
