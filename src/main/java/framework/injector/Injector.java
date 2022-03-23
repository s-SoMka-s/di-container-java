package framework.injector;

import framework.annotations.Value;
import framework.config.Configuration;
import lombok.SneakyThrows;
import org.codehaus.jackson.map.ObjectMapper;

import java.lang.reflect.Field;

public class Injector {

    /**
     * Производим непосредственное внедрение значения в поле
     *
     * @param field     поле, аннотированное Value-м
     * @param object    будущий инстанс бина
     */
    @SneakyThrows
    public static void injectValue(Field field, Object object, Configuration configuration) {
        var rawValue = field.getAnnotation(Value.class).value();
        var mapper = new ObjectMapper();

        field.setAccessible(true);
        if (!rawValue.startsWith("$")) {
            field.set(object, mapper.readValue(rawValue, field.getType()));

            return;
        }

        var key = rawValue.substring(1);
        var value = configuration.getValue(key);
        if (value == null) {
            throw new RuntimeException("No such variable id in the config file!" + "\nVariable id: " + rawValue);
        }
    }
}
