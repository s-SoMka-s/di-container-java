package framework.injector;

import framework.annotations.Autowired;
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
        var factory = this.context.getBeanFactory();

        // Конструктор помечен Autowired
        var args = new Object[parameters.length];
        for (var parameter : parameters) {
            var index = Integer.parseInt(parameter.getName().replace("arg", ""));

            if (parameter.isAnnotationPresent(Value.class)) {
                var arg = injectValueIntoParameter(parameter, mapper);
                args[index] = arg;

                continue;
            }

            var component = scanner.getComponent(parameter);
            if (component == null) {
                throw new RuntimeException("There is no suitable component for this parameter");
            }

            var componentName = NameExtensions.getComponentName(component);
            var existed = beans.get(componentName);
            if (existed == null) {
                existed = factory.createBean(parameter.getType());
                beans.add(existed);
            }

            args[index] = existed.getBean();
            continue;
        }

        var hasDeferred = Arrays.stream(args).anyMatch(Objects::isNull);
        return hasDeferred ? null : constructor.newInstance(args);
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
