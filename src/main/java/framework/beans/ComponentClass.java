package framework.beans;

import framework.annotations.Autowired;
import framework.annotations.Inject;
import framework.annotations.Value;
import framework.enums.Scope;
import framework.extensions.NameExtensions;
import framework.extensions.ScopeExtensions;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ComponentClass {
    private final String name;
    private final Class<?> clazz;
    private final Scope scope;

    private ArrayList<ComponentNode> childNodes = new ArrayList<>();
    private Object instance = null;

    public ComponentClass(Class<?> clazz) {
        this.clazz = clazz;
        this.name = NameExtensions.getComponentName(clazz);
        this.scope = ScopeExtensions.getScope(clazz);

        // constructor
        var constructors = clazz.getConstructors();
        if (constructors.length != 1) {
            throw new RuntimeException("Only one constructor should be presented");
        }

        var constructor = constructors[0];
    }

    public List<Field> getInjectableFields() {
        var fields = clazz.getDeclaredFields();
        var injectableFields = Arrays.stream(fields).filter(f -> f.isAnnotationPresent(Inject.class)).collect(Collectors.toList());

        return injectableFields;
    }

    public Class<?> getType() {
        return this.clazz;
    }

    public String getName() {
        return this.name;
    }

    public List<Parameter> getInjectableConstructorParameters() {
        var constructors = this.clazz.getConstructors();
        if (constructors.length != 1) {
            throw new RuntimeException("Only one constructor should be presented");
        }

        var constructor = constructors[0];
        if (!constructor.isAnnotationPresent(Autowired.class)) {
            return new ArrayList<>();
        }

        var parameters = constructor.getParameters();
        var injectableParameters = Arrays.stream(parameters).filter(f -> !f.isAnnotationPresent(Value.class)).collect(Collectors.toList());

        return injectableParameters;

    }

    public void setChildren(List<ComponentNode> children) {
        this.childNodes.addAll(children);
    }

    public ArrayList<ComponentNode> getChildNodes() {
        return this.childNodes;
    }

    public Scope getScope() {
        return this.scope;
    }

    public boolean needLazyInitialization() {
        return this.childNodes.stream().anyMatch(ComponentNode::needLazyInitialization);
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }
}
