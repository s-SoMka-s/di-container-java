package framework.extensions;

import framework.annotations.Component;
import framework.annotations.Inject;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

public class NameExtensions {
    public static String getInjectableFieldName(Field field) {
        var name = field.getAnnotation(Inject.class).value();
        if (name.isEmpty() || name.isBlank()) {
            return getDefaultName(field.getType());
        }

        return name;
    }

    public static String getInjectableParameterName(Parameter parameter) {
        var name = "";
        if (parameter.isAnnotationPresent(Inject.class)) {
            name = parameter.getAnnotation(Inject.class).value();
        }

        if (name.isEmpty() || name.isBlank()) {
            return getDefaultName(parameter.getType());
        }

        return name;
    }

    public static String getComponentName(Class<?> item) {
        var name = item.getAnnotation(Component.class).value();
        if (name.isEmpty() || name.isBlank()) {
            return getDefaultName(item);
        }

        return name;
    }

    public static String getDefaultName(Class<?> item) {
        var a = item.getTypeName();
        return a.substring(0, 1).toLowerCase() +
                a.substring(1);
    }
}
