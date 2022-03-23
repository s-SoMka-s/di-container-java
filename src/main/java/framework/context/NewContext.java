package framework.context;

import framework.annotations.Component;
import framework.beans.BeanFactory;
import framework.beans.BeanStore;
import framework.config.Configuration;
import framework.exceptions.IncorrectFieldAnnotationsException;
import framework.extensions.NameExtensions;
import framework.injector.Injector;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class NewContext {
    private final BeanFactory beanFactory;
    private final BeanStore beanStore;
    private final Injector injector;

    private Configuration configuration;


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

    public <T> T getType(Class<T> type) {
        var name = NameExtensions.getName(type);

        return (T)this.beanStore.get(name).getBean();
    }

    public void Run(Class<?> mainClass) throws IncorrectFieldAnnotationsException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        var packageToScan = mainClass.getPackageName();

        var scanner = new Reflections(packageToScan);

        var components = scanner.getTypesAnnotatedWith(Component.class);

        for (var component : components) {
            var bean = this.beanFactory.createBean(component);
            beanStore.add(bean);
        }
    }
}
