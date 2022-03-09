package implementation.factory;

import implementation.annotation.Inject;
import implementation.context.Context;
import implementation.configuration.Configuration;
import implementation.configuration.JavaConfiguration;
import implementation.locator.BeanLocator;
import implementation.locator.JavaBeanLocator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class BeanFactory {

    private final BeanLocator beanLocator;
    private final Configuration configuration;

    // Нужен при работе с полями Бина, так как необходимые зависимости уже могут быть, что можно определить через
    // кэш в ApplicationContext
    private final Context context;

    public BeanFactory(Context context) {
        this.configuration = new JavaConfiguration();
        this.beanLocator = new JavaBeanLocator(configuration.getPackageToScan(),
                configuration.getInterfaceToImplementation());
        this.context = context;
    }

    // по интерфейсу возвращаем бин - объект класса, реализующий интерфейс.
    public <T> T getBean(Class<T> clazz) {

        // если мы получили интерфейс, то тогда переходим в BeanConfiguration для поиска класса, реал. это интерфейс
        Class<? extends T> implementationClass = clazz;

        if (implementationClass.isInterface()) {
            implementationClass = beanLocator.getImplementationClass(implementationClass);
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
            for (Field field : Arrays.stream(implementationClass.getDeclaredFields()).filter(field ->
                    field.isAnnotationPresent(Inject.class)).collect(Collectors.toList())) {
                field.setAccessible(true);
                field.set(bean, context.getBean(field.getType()));
            }

            // Получаем конструктор (по умолчанию) сервиса, который нужно создать.
            return bean;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e.getCause());
        }
    }

}
