package framework.context;

import framework.annotations.Inject;
import framework.beans.*;
import framework.components.ComponentClass;
import framework.components.ComponentsFactory;
import framework.config.Configuration;
import framework.extensions.NameExtensions;
import framework.injector.Injector;
import framework.scanner.Scanner;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;

public class Context {
    private final BeanFactory beanFactory;
    private final BeanStore beanStore;
    private final Injector injector;

    public static ArrayList<String> queue = new ArrayList<>();
    public static HashMap<String, String> cycles = new HashMap<>();
    public static ArrayList<Bean> fromCycle = new ArrayList<>();
    public static ArrayList<Bean> toCycle = new ArrayList<>();

    private Configuration configuration;
    private Scanner scanner;

    public Context() {
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

    public Context getContext() {
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
        checkSimilarIds(components);

        for (var component : components) {
            if (component.needLazyInitialization()) {
                continue;
            }

            queue.add(component.getName());
            Bean bean = this.beanFactory.createBeanFromComponent(component);
            if (bean != null) {
                beanStore.add(bean);
                if (cycles.containsKey(bean.getName())) {
                    fromCycle.add(bean);
                }
                if (cycles.containsValue(bean.getName())) {
                    toCycle.add(bean);
                }
                queue.remove(component.getName());
            }
        }

        cycleResolver();

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

    private void checkSimilarIds(Set<ComponentClass> componentSet) {
        var components = new ArrayList<>(componentSet);
        for (int i = 0; i < components.size(); i++) {
            for (int j = i + 1; j < components.size(); j++) {
                if (components.get(i).getName().equals(components.get(j).getName())) {
                    throw new RuntimeException("Several components have the same id!");
                }
            }
        }
    }

    private void cycleResolver() {
        if (toCycle.size() > 0 && fromCycle.size() > 0) {
            Field[] fields2 = toCycle.get(0).getBean().getClass().getDeclaredFields();
            for (Field field2 : fields2) {
                if (field2.isAnnotationPresent(Inject.class) && fromCycle.get(0).getName().equals(
                        field2.getName())) {
                    field2.setAccessible(true);
                    try {
                        field2.set(toCycle.get(0).getBean(), fromCycle.get(0).getBean());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

            Field[] fields3 = fromCycle.get(0).getBean().getClass().getDeclaredFields();
            for (Field field3 : fields3) {
                if (field3.isAnnotationPresent(Inject.class)) {
                    field3.setAccessible(true);
                    try {
                        field3.set(fromCycle.get(0).getBean(), this.getBeanStore().get(field3.getName()).getBean());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                    Field[] fields4 = field3.getType().getDeclaredFields();
                    for (Field field4 : fields4) {
                        if (field4.isAnnotationPresent(Inject.class) && toCycle.get(0).getName().equals(
                                field4.getName())) {
                            field4.setAccessible(true);
                            try {
                                field4.set(this.getBeanStore().get(field3.getName()).getBean(), this.getBeanStore().get(field4.getName()).getBean());
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }
}
