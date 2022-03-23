package framework;

import framework.context.Context;
import framework.locator.ContextBean;
import org.springframework.beans.factory.support.ManagedMap;

import java.util.HashMap;

public class Bean extends ContextBean {
    public static Context context;

    private Class clazz;
    private String id;
    private Scope scope;
    private Object bean;
    private HashMap<Long, Object> threadBeans;
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
        threadBeans = new ManagedMap<>();
    }

    public Object getBean() {
        // если singleton, то возвращаем уже сохранённый инстанс
        // если prototype, то возвращаем новый инстанс
        // если thread, то каждому уникальному потоку возвращаем свой инстанс
        if (scope.equals(Scope.SINGLETON)) {
            return bean;
        } else if (scope.equals(Scope.PROTOTYPE)) {
            return context.createInstance(clazz);
        } else if (scope.equals(Scope.THREAD)) {
            if (threadBeans.containsKey(Thread.currentThread().getId())) {
                return threadBeans.get(Thread.currentThread().getId());
            } else {
                var obj = context.createInstance(clazz);
                threadBeans.put(Thread.currentThread().getId(), obj);
                return obj;
            }
        } else {
            throw new RuntimeException("Unknown thread!");
        }
    }

    public Scope getScope() {
        return scope;
    }

    public String getId() {
        return id;
    }
}
