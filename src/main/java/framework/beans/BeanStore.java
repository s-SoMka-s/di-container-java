package framework.beans;

import framework.exceptions.CircularDependencyException;

import java.util.HashMap;

public class BeanStore {
    private final HashMap<String, Bean> beans;
    private final HashMap<String, String> deferredBeans;

    public BeanStore() {
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

    public HashMap<String, Bean> getBeansMap() {
        return beans;
    }

    public Object getBeanObject(String name) {
        var bean = get(name);
        if (bean == null) {
            return null;
        }

        return bean.getBean();
    }

    public void addDeferred(String waiter, String beanName) {
        deferredBeans.put(waiter, beanName);
    }

    public void ensureHasNoCyclicDependency() {
        if (deferredBeans.isEmpty()) {
            return;
        }

        var visited = new HashMap<String, Boolean>();

        var items = deferredBeans.entrySet();

        for (var item : items) {
            var waiterName = item.getKey();
            var paramName = item.getValue();

            if (visited.containsKey(waiterName)) {
                try {
                    throw new CircularDependencyException(waiterName, paramName);
                } catch (CircularDependencyException e) {
                    e.printStackTrace();
                }
            }

            visited.put(paramName, true);
        }
    }
}
