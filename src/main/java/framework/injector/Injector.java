package framework.injector;

import framework.annotations.Inject;
import framework.annotations.Value;
import framework.context.NewContext;
import framework.exceptions.IncorrectFieldAnnotationsException;
import framework.extensions.NameExtensions;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

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

        var name = field.getDeclaredAnnotation(Inject.class).value();
        if (name.isBlank() || name.isEmpty()){
            name = NameExtensions.getDefaultName(field.getType());
        }

        var bean = beans.get(name);
        if (bean == null) {
            bean = beanFactory.createBean(field.getType());
            beans.add(bean);
        }

        field.setAccessible(true);
        field.set(object, bean.getBean());
    }
}
