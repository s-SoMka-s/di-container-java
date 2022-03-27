package framework.context;

import framework.annotations.Component;
import framework.beans.*;
import framework.config.Configuration;
import framework.exceptions.IncorrectFieldAnnotationsException;
import framework.extensions.NameExtensions;
import framework.injector.Injector;
import framework.scanner.Scanner;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NewContext {
    private final BeanFactory beanFactory;
    private final BeanStore beanStore;
    private final Injector injector;

    private Configuration configuration;
    private Reflections scanner;


    public NewContext() {
        this.beanStore = new BeanStore();
        this.beanFactory = new BeanFactory(this);
        this.injector = new Injector(this);
        this.configuration = null;
    }

    public void setConfiguration(@Nullable String path) {
        if (path != null){
            this.configuration = new Configuration(path);
        }
    }

    public NewContext getContext() {
        return this;
    }

    public Configuration getCurrentConfiguration() {
        return this.configuration;
    }

    public BeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    public BeanStore getBeanStore() {
        return this.beanStore;
    }

    public Injector getInjector() {
        return this.injector;
    }

    public Reflections getScanner() {
        return this.scanner;
    }

    public <T> T getType(Class<T> type) {
        var name = NameExtensions.getComponentName(type);

        return (T)this.beanStore.get(name).getBean();
    }

    public void Run(Class<?> mainClass) throws IncorrectFieldAnnotationsException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        var packageToScan = mainClass.getPackageName();

        var scanner = new Scanner(packageToScan);

        var candidates = scanner.getAllComponents();
        var factory = new ComponentsFactory(new Scanner(packageToScan));
        factory.createComponents(candidates);
        var scheme = factory.createDependenciesScheme();

//        for (var component : components) {
//            var bean = this.beanFactory.createBean(component);
//            if (bean == null) {
//                continue;
//            }
//
//            beanStore.add(bean);
//        }
//        beanStore.ensureHasNoCyclicDependency();

    }
}
