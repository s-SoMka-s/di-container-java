package framework.injector;

import framework.annotations.Inject;
import framework.annotations.Value;
import framework.components.ComponentClass;
import framework.context.NewContext;
import framework.exceptions.IncorrectFieldAnnotationsException;
import framework.extensions.FieldExtensions;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class Injector {
    private final NewContext context;
    private final ConstructorInjector constructorInjector;
    private final FieldInjector fieldInjector;

    public Injector(NewContext context) {
        this.context = context;
        constructorInjector = new ConstructorInjector(context);
        fieldInjector = new FieldInjector(context);
    }

    public void tryInjectIntoField(Class<?> beanClass, Field field, Object instance) throws IncorrectFieldAnnotationsException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        FieldExtensions.ensureAnnotationsValid(field);

        // Если поле имеет аннотацию Value, то производим внедрение значений
        if (field.isAnnotationPresent(Value.class)) {
            this.fieldInjector.injectValue(field, instance);
        }

        // Если поле имеет аннотацию Inject, то производим внедрение зависимостей
        if (field.isAnnotationPresent(Inject.class)) {
            this.fieldInjector.injectField(beanClass, field, instance);
        }
    }

    public Object trtInjectIntoConstructor(Class<?> beanClass, Constructor constructor) throws IOException, InvocationTargetException, InstantiationException, IllegalAccessException, IncorrectFieldAnnotationsException, NoSuchMethodException {
        return this.constructorInjector.injectIntoConstructor(beanClass, constructor);
    }

    public Object trtInjectIntoConstructor(ComponentClass component) throws InvocationTargetException, InstantiationException, IllegalAccessException, IOException, IncorrectFieldAnnotationsException, NoSuchMethodException {
        return this.constructorInjector.injectIntoConstructor(component);
    }
}
