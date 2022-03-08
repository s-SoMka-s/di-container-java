package implementation.factory;

import implementation.annotation.Inject;
import implementation.configuration.Configuration;
import implementation.configuration.JavaConfiguration;
import implementation.configurator.BeanConfigurator;
import implementation.configurator.JavaBeanConfigurator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class BeanFactory {

    private static final BeanFactory BEAN_FACTORY = new BeanFactory();
    private final BeanConfigurator beanConfigurator;
    private final Configuration configuration;

    private BeanFactory() {
        this.configuration = new JavaConfiguration();
        this.beanConfigurator = new JavaBeanConfigurator(configuration.getPackageToScan(),
                configuration.getInterfaceToImplementation());
    }

    // Синглтон реализация
    public static BeanFactory getInstance() {
        return BEAN_FACTORY;
    }

    // по интерфейсу возвращаем бин - объект класса, реализующий интерфейс.
    public <T> T getBean(Class<T> clazz) {

        // если мы получили интерфейс, то тогда переходим в BeanConfiguration для поиска класса, реал. это интерфейс
        Class<? extends T> implementationClass = clazz;

        if (implementationClass.isInterface()) {
            implementationClass = beanConfigurator.getImplementationClass(implementationClass);
        }

        // Перед получением конструктора сервиса, который нужно создать, ищем все аттрибуты (fields), которые
        // нужно внедрить и для них точно так же создавать экземпляр класса и внедрять.
        try {
            T bean = implementationClass.getDeclaredConstructor().newInstance();

            // Пройдёмся по всем полям Бина и определим, какие зависимости необходимо внедрить
            // Пример: класс MusicPlayer. Зависимость - класс ClassicalMusic (интерфейс Music).
            // Получим только те поля, которые аннотированы через Inject.
            // Пройдёся через все такие поля, которые надо инжектить.
            // Мы понимаем, какой это тип. Теперь его нужно создать и внедрить в Бин.
            for (int i = 0; i < implementationClass.getDeclaredFields().length; i++) {
                Field field = implementationClass.getDeclaredFields()[i];
                if (field.isAnnotationPresent((Inject.class))) {
                    field.setAccessible(true);
                    field.set(bean, BEAN_FACTORY.getBean(field.getType()));
                }
            }

            // Получаем конструктор (по умолчанию) сервиса, который нужно создать.
            return bean;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e.getCause());
        }
    }

}
