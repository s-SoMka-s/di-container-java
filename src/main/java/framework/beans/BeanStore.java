package framework.beans;

import java.util.HashMap;

public class BeanStore {
    private final HashMap<String, Bean> beans;
    public Object get;

    public BeanStore(){
        this.beans = new HashMap<>();
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
}
