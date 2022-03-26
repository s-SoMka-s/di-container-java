package framework.injector;

import framework.annotations.Autowired;
import framework.annotations.Inject;
import framework.annotations.Value;
import framework.context.NewContext;
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

    public Object injectIntoConstructor(Class<?> beanClass, Constructor constructor) throws InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        var parameters = constructor.getParameters();
        // Конструктор без параметров
        if (parameters.length == 0) {
            return constructor.newInstance();
        }

        var mapper = new ObjectMapper();

        // Все параметры Value-annotated
        if (ParameterExtensions.isOnlyValueAnnotated(parameters)) {
            var args = new ArrayList();
            for (var parameter : parameters) {
                var rawValue = parameter.getAnnotation(Value.class).value();
                var type = parameter.getType();
                var casted = mapper.readValue(rawValue, type);
                args.add(casted);
            }

            return constructor.newInstance(args.toArray());
        }

        var beans = this.context.getBeanStore();
        var scanner = this.context.getScanner();

        // Конструктор помечен Autowired
        if (constructor.isAnnotationPresent(Autowired.class)) {
            var args = new Object[parameters.length];
            for (var parameter : parameters) {
                var index = Integer.parseInt(parameter.getName().replace("arg", ""));

                if (parameter.isAnnotationPresent(Value.class)) {
                    var rawValue = parameter.getAnnotation(Value.class).value();
                    var type = parameter.getType();
                    var casted = mapper.readValue(rawValue, type);

                    args[index] =  casted;
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

                // Инъекция неименованного параметра
                var name = NameExtensions.getInjectableParameterName(parameter);

                var type = parameter.getType();
                var typeName = NameExtensions.getDefaultName(type);

                // интерфейс без явной связи
                if (type.isInterface() && typeName.equals(name)) {
                    getInterfaceImplementation(type);
                }

                var bean = beans.getBeanObject(name);
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

        return constructor.newInstance();
    }

    private Object injectNameable(Parameter parameter) {
        var beans = this.context.getBeanStore();
        var scanner = this.context.getScanner();

        var name = NameExtensions.getInjectableParameterName(parameter);

        var type = parameter.getType();
        var typeName = NameExtensions.getDefaultName(type);

        // интерфейс без явной связи
        if (type.isInterface() && typeName.equals(name)) {

            getInterfaceImplementation(type);
        }

        return beans.getBeanObject(name);
    }

    private void getInterfaceImplementation(Class<?> type) {
        // TODO найти кого-то, кто реализует этот интерфейс и вфигачить его в параметр

        var beans = this.context.getBeanStore();
        var scanner = this.context.getScanner();

        var implementations = new ArrayList<Class<?>>(scanner.getSubTypesOf(type));
        if (implementations.isEmpty()) {
            throw new RuntimeException("No implementations for type" + type);
        }

        for (var impl : implementations) {
            var implName = NameExtensions.getComponentName(impl);
            var bean = beans.get(implName);
            if (bean != null) {

            }
        }
    }
}
