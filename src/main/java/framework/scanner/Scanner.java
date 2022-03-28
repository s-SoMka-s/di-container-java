package framework.scanner;

import framework.annotations.Component;
import framework.annotations.Inject;
import framework.extensions.NameExtensions;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Set;

public class Scanner {
    private final Reflections scanner;

    public Scanner(String packageToScan) {
        this.scanner = new Reflections(packageToScan);
    }

    public Set<Class<?>> getAllComponents() {
        return scanner.getTypesAnnotatedWith(Component.class);
    }

    public Class<?> getNameableComponent(String name) {
        var components = getAllComponents();
        for (var component : components) {
            var componentName = component.getAnnotation(Component.class).value();
            if (componentName.isEmpty() || componentName.isBlank()) {
                componentName = NameExtensions.getComponentName(component);
            }

            if (componentName.equals(name)) {
                return  component;
            }
        }

        return null;
    }

    public void getInjectableConstructorParameters(Class<?> clazz) {

    }

    public Class<?> getImplementation(Parameter parameter) {
        if (parameter.isAnnotationPresent(Inject.class)) {
            var name = parameter.getAnnotation(Inject.class).value();
            if (!name.isBlank() && !name.isEmpty()) {
                return getNameableComponent(name);
            }
        }

        var type = parameter.getType();
        if (type.isInterface()) {
            return getImplementation(type);
        }

        return type;
    }

    public Class<?> getImplementation(Field field) {
        var name = field.getAnnotation(Inject.class).value();
        if (!name.isBlank() && !name.isEmpty()) {
            return getNameableComponent(name);
        }

        var type = field.getType();
        if (type.isInterface()) {
            return getImplementation(type);
        }

        return type;
    }

    public Class<?> getImplementation(Class<?> type) {
        var subtypes = scanner.getSubTypesOf(type);

        return subtypes.stream().findFirst().orElse(null);
    }

    public Set getInterfaceImplementations(Class<?> type) {
        return scanner.getSubTypesOf(type);
    }
}
