package framework.beans;

import framework.components.ComponentClass;
import framework.components.ContextBean;
import framework.enums.Scope;

import java.util.HashMap;

public class Bean extends ContextBean {
    public static BeanFactory beanFactory;

    private Class clazz;
    private String name;
    private Scope scope;
    private Object bean;
    private HashMap<Long, Object> threadBeans;

    public Bean(BeanFactory bf) {
        beanFactory = bf;
    }

    public Bean(ComponentClass component, Object instance) {
        this.clazz = component.getType();
        this.name = component.getName();
        this.scope = component.getScope();
        this.bean = instance;

        threadBeans = new HashMap<>();
    }

    public Bean(Class clazz, String name, Scope scope, Object bean) {
        this.clazz = clazz;
        this.name = name;
        this.bean = bean;
        this.scope = scope;

        threadBeans = new HashMap<>();
    }

    public Object getBean() {
        // если singleton, то возвращаем уже сохранённый инстанс
        // если prototype, то возвращаем новый инстанс
        // если thread, то каждому уникальному потоку возвращаем свой инстанс
        return switch (scope) {
            case PROTOTYPE -> beanFactory.createBean(clazz).bean;
            case SINGLETON -> bean;
            case THREAD -> manageThreadScope();
        };
    }

    private Object manageThreadScope() {
        if (threadBeans.containsKey(Thread.currentThread().getId())) {
            return threadBeans.get(Thread.currentThread().getId());
        } else {
            Object obj = beanFactory.createBean(clazz).bean;
            threadBeans.put(Thread.currentThread().getId(), obj);
            return obj;
        }
    }

    public Scope getScope() {
        return scope;
    }

    public Class getClazz() {
        return clazz;
    }

    public String getName() {
        return name;
    }
}
