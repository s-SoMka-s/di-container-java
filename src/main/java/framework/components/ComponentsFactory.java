package framework.components;

import framework.scanner.Scanner;
import framework.scheme.ComponentsScheme;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ComponentsFactory {
    private final ArrayList<ComponentClass> components;
    private final Scanner scanner;

    public ComponentsFactory(Scanner scanner) {
        this.components = new ArrayList<>();
        this.scanner = scanner;
    }

    public void createComponents(Set<Class<?>> candidates) {
        for (var candidate : candidates) {
            var component = new ComponentClass(candidate);
            var name = component.getName();
            this.components.add(component);
        }
    }

    public ComponentsScheme createComponentsScheme() {
        var nodes = new ArrayList<ComponentNode>(components.size());

        for (var component : components) {
            var fieldsChildren = getFieldsChildren(component);
            var constructorParametersChildren = getConstructorParametersChildren(component);
            var children = Stream.of(fieldsChildren, constructorParametersChildren)
                    .flatMap(x -> x.stream())
                    .collect(Collectors.toList());

            component.setChildren(children);
            nodes.add(new ComponentNode(component));
        }

        return new ComponentsScheme(nodes);
    }

    private ArrayList<ComponentNode> getFieldsChildren(ComponentClass component) {
        var nodes = new ArrayList<ComponentNode>();
        var fields = component.getInjectableFields();
        for (var field : fields) {
            var type = scanner.getImplementation(field);
            var rootComponent = this.components.stream().filter(c -> c.getType() == type).findFirst().orElse(null);
            var annotations = field.getAnnotations();
            var node = new ComponentNode(rootComponent, ComponentType.Filed, annotations);
            nodes.add(node);
        }

        return nodes;
    }

    private ArrayList<ComponentNode> getConstructorParametersChildren(ComponentClass component) {
        var nodes = new ArrayList<ComponentNode>();
        var parameters = component.getInjectableConstructorParameters();

        for (var parameter : parameters) {
            var type = scanner.getImplementation(parameter);
            var rootComponent = this.components.stream().filter(c -> c.getType() == type).findFirst().orElse(null);
            var annotations = parameter.getAnnotations();
            var node = new ComponentNode(rootComponent, ComponentType.ConstructorParameter, annotations);
            nodes.add(node);
        }

        return nodes;
    }
}
