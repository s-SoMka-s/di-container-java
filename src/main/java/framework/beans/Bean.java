package framework.beans;

import framework.context.Context;
import framework.enums.Scope;
//import framework.context.Context;
import framework.exceptions.IncorrectFieldAnnotationsException;
import framework.locator.ContextBean;
import org.springframework.beans.factory.support.ManagedMap;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class Bean extends ContextBean {
    public static BeanFactory beanFactory;

    private Class clazz;
    private String name;
    private Scope scope;
    private Object bean;
    private HashMap<Long, Object> threadBeans;
    //private initParams;
    //private constParams;

    public Bean(BeanFactory bf) {
        beanFactory = bf;
    }

    public Bean(ComponentClass component, Object instance) {
        this.clazz = component.getType();
        this.name = component.getName();
        this.scope = component.getScope();

        this.bean = instance;
    }

    public Bean(Class clazz, String name, Scope scope, Object bean) {
        this.clazz = clazz;
        this.name = name;
        this.bean = bean;
        this.scope = scope;
        threadBeans = new ManagedMap<>();
    }

    public Object getBean() {
        // если singleton, то возвращаем уже сохранённый инстанс
        // если prototype, то возвращаем новый инстанс
        // если thread, то каждому уникальному потоку возвращаем свой инстанс
        try {
            return switch (scope) {
                case PROTOTYPE -> beanFactory.createBean(clazz).bean;
                case SINGLETON -> bean;
                case THREAD -> null;
            };
        } catch (IncorrectFieldAnnotationsException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException | IOException e) {
            e.printStackTrace();
        }
//        if (scope.equals(Scope.SINGLETON)) {
//            return bean;
//        } else if (scope.equals(Scope.PROTOTYPE)) {
//            return context.createInstance(clazz);
//        } else if (scope.equals(Scope.THREAD)) {
//            if (threadBeans.containsKey(Thread.currentThread().getId())) {
//                return threadBeans.get(Thread.currentThread().getId());
//            } else {
//                //var obj = context.createInstance(clazz);
//                threadBeans.put(Thread.currentThread().getId(), obj);
//                return obj;
//            }
//        } else {
//            throw new RuntimeException("Unknown thread!");
//        }
        return null;
    }

    public Scope getScope() {
        return scope;
    }

    public String getName() {
        return name;
    }
}
