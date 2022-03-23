package framework.beans;

import framework.annotations.Value;
import framework.annotations.Inject;
import framework.context.NewContext;
import framework.exceptions.IncorrectFieldAnnotationsException;
import framework.extensions.FieldExtensions;
import framework.extensions.NameExtensions;
import framework.extensions.ScopeExtensions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class BeanFactory {
    private final NewContext context;

    public BeanFactory(NewContext context) {
        this.context = context;
    }

    public Bean createBean(Class<?> item) throws IncorrectFieldAnnotationsException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        var name = NameExtensions.getName(item);
        var scope = ScopeExtensions.getScope(item);

        var instance = createInstance(item);
        var bean = new Bean(item, name, scope, instance);

        return bean;
    }

    private Object createInstance(Class<?> beanClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IncorrectFieldAnnotationsException, IOException {
        var injector = this.context.getInjector();

        // Создаём сам бин
        var constructor = beanClass.getDeclaredConstructor();
        var instance = constructor.newInstance();

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

        var constructors = Arrays.stream(beanClass.getConstructors()).collect(Collectors.toList());

        return instance;
    }
}
