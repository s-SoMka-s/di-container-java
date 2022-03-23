package framework.beans;

import framework.annotations.Value;
import framework.annotations.Inject;
import framework.context.NewContext;
import framework.exceptions.IncorrectFieldAnnotationsException;
import framework.extensions.FieldExtensions;
import framework.extensions.NameExtensions;
import framework.extensions.ParameterExtensions;
import framework.extensions.ScopeExtensions;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class BeanFactory {
    private final NewContext context;

    public BeanFactory(NewContext context) {
        this.context = context;
    }

    public void createBeanFromComponent(Class<?> component) {
        var name = NameExtensions.getComponentName(component);
        var scope = ScopeExtensions.getScope(component);
    }

    public Bean createBean(Class<?> item) throws IncorrectFieldAnnotationsException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        var name = NameExtensions.getComponentName(item);
        var scope = ScopeExtensions.getScope(item);

        var instance = createInstance(item);
        var bean = new Bean(item, name, scope, instance);

        return bean;
    }

    private Object createInstance(Class<?> beanClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IncorrectFieldAnnotationsException, IOException {
        var injector = this.context.getInjector();

        var constructors = beanClass.getConstructors();
        if (constructors.length != 1) {
            throw new RuntimeException("Only one constructor should be presented");
        }

        var constructor = constructors[0];

        var instance = injector.injectIntoConstructor(constructor);

        var fields = beanClass.getDeclaredFields();
        for (var field : fields) {
            FieldExtensions.ensureAnnotationsValid(field);

            // Если поле имеет аннотацию Value, то производим внедрение значений
            if (field.isAnnotationPresent(Value.class)) {
                injector.injectValue(field, instance);
            }

            // Если поле имеет аннотацию Inject, то производим внедрение зависимостей
            if (field.isAnnotationPresent(Inject.class)) {
                injector.injectField(beanClass, field, instance);
            }
        }

        return instance;
    }
}
