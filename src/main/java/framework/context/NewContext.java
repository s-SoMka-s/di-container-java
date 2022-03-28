package framework.context;

import framework.beans.*;
import framework.config.Configuration;
import framework.exceptions.IncorrectFieldAnnotationsException;
import framework.extensions.NameExtensions;
import framework.injector.Injector;
import framework.scanner.Scanner;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class NewContext {
    private final BeanFactory beanFactory;
    private final BeanStore beanStore;
    private final Injector injector;

    private Configuration configuration;
    private Scanner scanner;


    public NewContext() {
        this.beanStore = new BeanStore();
        this.beanFactory = new BeanFactory(this);
        this.injector = new Injector(this);
        this.configuration = null;
    }

    public void setConfiguration(@Nullable String path) {
        if (path != null) {
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

    public Scanner getScanner() {
        return this.scanner;
    }

    public <T> T getType(Class<T> type) {
        var name = NameExtensions.getComponentName(type);
        var bean = this.beanStore.get(name);

        return (T) bean.getBean();
    }

    public void run(Class<?> mainClass) {
        run(mainClass.getPackageName());
    }

    public void run(String packageToScan) {
        var scanner = new Scanner(packageToScan);
        this.scanner = scanner;

        new Bean(beanFactory);
        var candidates = scanner.getAllComponents();
        var factory = new ComponentsFactory(new Scanner(packageToScan));
        factory.createComponents(candidates);
        var scheme = factory.createComponentsScheme();
        scheme.ensureHasNoCircularDependency();
        var components = scheme.getRootComponents();
        for (var component : components) {
            if (component.needLazyInitialization()) {
                continue;
            }

            Bean bean = this.beanFactory.createBeanFromComponent(component);
            if (bean == null) {
                continue;
            }

            beanStore.add(bean);
        }


//        var components = scanner.getAllComponents();
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
