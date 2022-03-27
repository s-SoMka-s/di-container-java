package framework.beans;

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

    public Bean createBeanFromComponent(ComponentClass component) throws IncorrectFieldAnnotationsException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        var instance = createInstance(component.getType());
        return new Bean(component);
    }

    public Bean createBean(Class<?> item) throws IncorrectFieldAnnotationsException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        var name = NameExtensions.getComponentName(item);
        var scope = ScopeExtensions.getScope(item);

        var instance = createInstance(item);
        if (instance == null) {
            return null;
        }

        return new Bean(item, name, scope, instance);
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
