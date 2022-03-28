package framework.injector;

import framework.annotations.Autowired;
import framework.annotations.Component;
import framework.annotations.Inject;
import framework.annotations.Value;
import framework.beans.ComponentClass;
import framework.context.NewContext;
import framework.exceptions.IncorrectFieldAnnotationsException;
import framework.extensions.NameExtensions;
import framework.extensions.ParameterExtensions;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class ConstructorInjector {
    private final NewContext context;

    public ConstructorInjector(NewContext context) {
        this.context = context;
    }

    public Object injectIntoConstructor(Class<?> beanClass, Constructor constructor) throws InvocationTargetException, InstantiationException, IllegalAccessException, IOException, IncorrectFieldAnnotationsException, NoSuchMethodException {
        var parameters = constructor.getParameters();
        // Конструктор без параметров
        if (parameters.length == 0) {
            return constructor.newInstance();
        }

        var mapper = new ObjectMapper();

        // Все параметры Value-annotated
        if (ParameterExtensions.isOnlyValueAnnotated(parameters)) {
            var args = injectOnlyValueAnnotated(parameters, mapper);
            return constructor.newInstance(args);
        }

        if (!constructor.isAnnotationPresent(Autowired.class)) {
            return null;
        }

        var beans = this.context.getBeanStore();
        var scanner = this.context.getScanner();

        // Конструктор помечен Autowired
        var args = new Object[parameters.length];
        for (var parameter : parameters) {
            var index = Integer.parseInt(parameter.getName().replace("arg", ""));

            if (parameter.isAnnotationPresent(Value.class)) {
                var arg = injectValueIntoParameter(parameter, mapper);
                args[index] = arg;

                continue;
            }

            if (parameter.isAnnotationPresent(Inject.class)) {
                var res = injectNameable(parameter);
                if (res == null) {
                    // TODO Add deferred
                    var name = NameExtensions.getInjectableParameterName(parameter);
                    var waiterName = NameExtensions.getComponentName(beanClass);

                    beans.addDeferred(waiterName, name);
                }
                args[index] = res;
                continue;
            }

            // Unnamed parameter injection
            var name = NameExtensions.getInjectableParameterName(parameter);

            var type = parameter.getType();
            var typeName = NameExtensions.getDefaultName(type);

            // интерфейс без явной связи
            if (type.isInterface() && typeName.equals(name)) {
                var impl = scanner.getImplementation(parameter);
            }

            var bean = tryInstantiateParameter(parameter);
            if (bean == null) {
                // TODO Add deferred
                var waiterName = NameExtensions.getComponentName(beanClass);
                beans.addDeferred(waiterName, name);
            }

            args[index] = bean;
        }

        var hasDeferred = Arrays.stream(args).anyMatch(Objects::isNull);
        return hasDeferred ? null : constructor.newInstance(args);
    }

    private Object tryInstantiateParameter(Parameter parameter) throws IncorrectFieldAnnotationsException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        var name = NameExtensions.getInjectableParameterName(parameter);

        var factory = this.context.getBeanFactory();
        var beans = this.context.getBeanStore();

        var existed = beans.getBeanObject(name);
        if (existed != null) {
            return existed;
        }

        if (!parameter.getType().isAnnotationPresent(Component.class)) {
            return null;
        }

        var bean = factory.createBean(parameter.getType());
        beans.add(bean);

        return bean.getBean();
    }

    private Object injectNameable(Parameter parameter) throws IncorrectFieldAnnotationsException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        var scanner = this.context.getScanner();

        var name = NameExtensions.getInjectableParameterName(parameter);

        var type = parameter.getType();
        var typeName = NameExtensions.getDefaultName(type);

        // интерфейс без явной связи
        if (type.isInterface() && typeName.equals(name)) {
            var impl = scanner.getImplementation(parameter);
        }

        return tryInstantiateParameter(parameter);
    }

    public Object injectIntoConstructor(ComponentClass component) throws InvocationTargetException, InstantiationException, IllegalAccessException, IOException, IncorrectFieldAnnotationsException, NoSuchMethodException {
        var type = component.getType();
        var constructor = component.getType().getConstructors()[0];

        return injectIntoConstructor(type, constructor);
    }

    private Object[] injectOnlyValueAnnotated(Parameter[] parameters, ObjectMapper mapper) throws IOException {
        var args = new ArrayList();
        for (var parameter : parameters) {
            var arg = injectValueIntoParameter(parameter, mapper);
            args.add(arg);
        }

        return args.toArray();
    }

    private Object injectValueIntoParameter(Parameter parameter, ObjectMapper mapper) throws IOException {
        var rawValue = parameter.getAnnotation(Value.class).value();
        if (rawValue.startsWith("$")) {
                return getValueFromConfiguration(rawValue.substring(1));
        }

        var type = parameter.getType();
        var casted = mapper.readValue(rawValue, type);

        return casted;
    }

    private Object getValueFromConfiguration(String key) {
        var configuration = this.context.getCurrentConfiguration();
        if (configuration == null) {
            throw new RuntimeException("No declared configuration!");
        }

        var value = configuration.getValue(key);
        if (value == null) {
            throw new RuntimeException("No such variable id in the config file!" + "\nVariable id: " + key);
        }

        return value;
    }
}
