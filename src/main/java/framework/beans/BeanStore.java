package framework.beans;

import java.util.HashMap;

public class BeanStore {
    private final HashMap<String, Bean> beans;
    private final HashMap<String, String> deferredBeans;
    public Object get;

    public BeanStore(){
        this.beans = new HashMap<>();
        deferredBeans = new HashMap<>();
    }

    public void add(Bean bean) {
        var name = bean.getName();
        beans.put(name, bean);
    }

    public Bean get(String name) {
        if (!beans.containsKey(name)) {
            return null;
        }

        return beans.get(name);
    }

    public void addDeferred(String waiter, String beanName) {
        deferredBeans.put(waiter, beanName);
    }
}
