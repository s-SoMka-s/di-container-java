package framework.injector;

import framework.annotations.Autowired;
import framework.annotations.Component;
import framework.annotations.Inject;
import framework.annotations.Value;
import framework.context.NewContext;
import framework.exceptions.IncorrectFieldAnnotationsException;
import framework.extensions.NameExtensions;
import framework.extensions.ParameterExtensions;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Injector {
    private final NewContext context;

    public Injector(NewContext context) {
        this.context = context;
    }

    /**
     * Производим непосредственное внедрение значения в поле
     *
     * @param field     поле, аннотированное Value-м
     * @param object    будущий инстанс бина
     */
    public void injectValue(Field field, Object object) throws IOException, IllegalAccessException {
        var rawValue = field.getAnnotation(Value.class).value();
        var mapper = new ObjectMapper();

        field.setAccessible(true);
        if (!rawValue.startsWith("$")) {
            field.set(object, mapper.readValue(rawValue, field.getType()));

            return;
        }

        var configuration = this.context.getCurrentConfiguration();
        if (configuration == null) {
            throw new RuntimeException("No declared configuration!");
        }

        var key = rawValue.substring(1);
        var value = configuration.getValue(key);
        if (value == null) {
            throw new RuntimeException("No such variable id in the config file!" + "\nVariable id: " + rawValue);
        }

        field.set(object, value);
    }

    public Object injectIntoConstructor(Constructor constructor) throws InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
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
                if (parameter.isAnnotationPresent(Value.class)) {
                    var rawValue = parameter.getAnnotation(Value.class).value();
                    var type = parameter.getType();
                    var casted = mapper.readValue(rawValue, type);
                    var index = Integer.parseInt(parameter.getName().replace("arg", ""));

                    args[index] =  casted;
                    continue;
                }

                if (parameter.isAnnotationPresent(Inject.class)) {
                    var name = NameExtensions.getInjectableParameterName(parameter);

                    var type = parameter.getType();
                    var typeName = NameExtensions.getDefaultName(type);

                    // интерфейс без явной связи
                    if (type.isInterface() && typeName.equals(name)) {
                        // TODO найти кого-то, кто реализует этот интерфейс и вфигачить его в параметр
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

                    var index = Integer.parseInt(parameter.getName().replace("arg", ""));

                    var bean = beans.get(name);
                    if (bean == null) {
                        // add deferred;
                        args[index] = null;
                        continue;
                    }

                    args[index] = bean.getBean();
                    continue;
                }
            }

            return constructor.newInstance(args);
        }

        return constructor.newInstance();
    }

    /**
     * Производим внедрение зависимости в поле
     *
     * @param beanClass класс бина, которой необходимо создать
     * @param field     поле, аннотированное Inject-ом
     * @param object    будущий инстанс бина
     */
    public void injectField(Class<?> beanClass, Field field, Object object) throws IncorrectFieldAnnotationsException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        var beans = this.context.getBeanStore();
        var beanFactory = this.context.getBeanFactory();

        var name = NameExtensions.getInjectableFieldName(field);

        var bean = beans.get(name);
        if (bean == null) {
            if (!field.getType().isAnnotationPresent(Component.class)) {
                var waiterName = NameExtensions.getComponentName(beanClass);
                beans.addDeferred(waiterName, name);
                return;
            }

            bean = beanFactory.createBean(field.getDeclaringClass());
            beans.add(bean);
        }

        field.setAccessible(true);
        field.set(object, bean.getBean());
    }
}
