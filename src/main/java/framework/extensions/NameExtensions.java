package framework.extensions;

import framework.annotations.Component;

public class NameExtensions {
    public static String getName(Class<?> item) {
        var name = item.getAnnotation(Component.class).value();
        if (name.isEmpty() || name.isBlank()) {
            return getDefaultName(item);
        }

        return name;
    }

    public static String getDefaultName(Class<?> item) {
        var divided = item.getTypeName().split("\\.");
        var className = divided[divided.length - 1];

        return className;
    }
}
