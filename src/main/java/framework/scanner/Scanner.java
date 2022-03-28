package framework.scanner;

import framework.annotations.Component;
import framework.annotations.Inject;
import framework.extensions.NameExtensions;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    /**
     * Returns injectable component for constructor parameter.
     * @param parameter constructor parameter, can be represented as named type, interface, generic interface or simple class
     * @return desired component or null, if there is no suitable component
     */
    public Class<?> getComponent(Parameter parameter) {
        if (parameter.isAnnotationPresent(Inject.class)) {
            var name = parameter.getAnnotation(Inject.class).value();
            if (!name.isBlank() && !name.isEmpty()) {
                return getNameableComponent(name);
            }
        }

        var type = parameter.getType();
        if (type.isInterface()) {
            if (isGeneric(type)) {
                return getGenericImplementation(parameter);
            }
            else {
                return getImplementation(type);
            }
        }

        return type;
    }

    /**
     * Returns injectable implementation of parameter, represented by generic interface
     * @param parameter constructor or method parameter
     * @return desired implementation, or null if there is no suitable implementations
     */
    public Class<?> getGenericImplementation(Parameter parameter) {
        var type = parameter.getType();
        var parameterizedType = parameter.getParameterizedType();

        return scanner.getSubTypesOf(type)
                .stream()
                .filter(t -> t.isAnnotationPresent(Component.class))
                .filter(t -> Arrays.asList(t.getGenericInterfaces()).contains(parameterizedType))
                .findFirst()
                .orElse(null);
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
            if (isGeneric(type)) {
                var typeName = parameter.getParameterizedType().getTypeName();
            }
            else {
                return getImplementation(type);
            }
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
        return scanner.getSubTypesOf(type)
                .stream()
                .filter(t -> t.isAnnotationPresent(Component.class))
                .findFirst()
                .orElse(null);
    }

    private List<Class<?>> getImplementations(Class<?> interfaceType) {
        return scanner.getSubTypesOf(interfaceType)
                .stream()
                .filter(t -> t.isAnnotationPresent(Component.class))
                .collect(Collectors.toList());
    }

    public Set getInterfaceImplementations(Class<?> type) {
        return scanner.getSubTypesOf(type);
    }

    private boolean isGeneric(Class<?> type) {
        return type.getTypeParameters().length > 0;
    }
}
