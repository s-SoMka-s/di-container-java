package implementation.bean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext implements BeanFactory {

    /**
     * Container itself
     * Group by bean name
     */
    private final Map<String, Bean> beanByNameMap = new ConcurrentHashMap<>(256);

    /**
     * Container itself
     * Group by bean class
     */
    private final Map<Class<?>, Bean> beanByClassMap = new ConcurrentHashMap<>(256);

    @Override
    public Object getBean(String name) {
        return beanByNameMap.get(name).getBean();
    }

    @Override
    public <T> T getBean(Class<T> tClass) {
        return tClass.cast(beanByClassMap.get(tClass).getBean());
    }
}
