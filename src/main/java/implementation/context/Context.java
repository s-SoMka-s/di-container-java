package implementation.context;

import implementation.factory.BeanFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Context {

    private BeanFactory beanFactory;
    private final Map<Class, Object> beanMap = new ConcurrentHashMap<>();

    // Map: Интерфейс -> конкретный объект класса, реализующего интерфейс.
    // При запросе на получение бина, мы посмотрим в Map, если там уже инициализированный бин для данного интерфейса.
    // Если есть, то мы его вернём. Если нет, то создадим и положим в Map.s

    public Context() {
        BeanFactory beanFactory = new implementation.factory.BeanFactory(this);
        this.setBeanFactory(beanFactory);
    }

    // возвращаем бин по его классу - всё аналогично BeanFactory.

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public <T> T getBean(Class<T> clazz) {
        // Если уже есть, то его и возвращаем
        if (beanMap.containsKey(clazz)) {
            return (T) beanMap.get(clazz);
        }

        //Если нет, то создаём, кладём и возвращаем
        T bean = beanFactory.getBean(clazz);

        beanMap.put(clazz, bean);

        return bean;
    }
}
