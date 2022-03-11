package implementation;

import implementation.context.Context;
import implementation.locator.ContextBean;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Bean extends ContextBean {
    private Class clazz;
    private String id;
    private Scope scope;
    private Object bean;
    private Constructor constructor;
    public static Context context;
    //private initParams;
    //private constParams;

    public Bean(Context ctx) {
        context = ctx;
    }

    public Bean(Class clazz, String id, Scope scope, Object bean) {
        this.clazz = clazz;
        this.id = id;
        this.bean = bean;
        this.scope = scope;
    }

    /*public Bean(Class clazz, String id, Scope scope, Constructor constructor) {
        this.clazz = clazz;
        this.id = id;
        this.constructor = constructor;
        this.scope = scope;
        if (scope.equals(Scope.SINGLETON)) {
            try {
                this.bean = constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }*/

    // по дефолту тип жизненного цикла - Singleton.
    public Bean(Class clazz, String id, Scope scope) {
        this.clazz = clazz;
        this.id = id;
        if (scope != null) {
            this.scope = scope;
        } else {
            this.scope = Scope.SINGLETON;
        }
    }

    public Object getBean() {
        if (scope.equals(Scope.SINGLETON)) {
            return bean;
        } else {
            return context.createBean(clazz);
        }
    }

    public Scope getScope() {
        return scope;
    }

    public String getId() {
        return id;
    }

    /*public Object getBean() {
        if (scope.equals(Scope.SINGLETON)) {
            return bean;
        } else {
            try {
                return constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException();
            }
        }
    }*/
}
