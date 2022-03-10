package implementation.factory;

import implementation.context.Context;
import implementation.configuration.Configuration;
import implementation.configuration.JavaConfiguration;
import implementation.locator.BeanLocator;
import implementation.locator.JavaBeanLocator;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

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
    /*public <T> T getBean(Class<T> clazz) {

        // если мы получили интерфейс, то тогда переходим в BeanConfiguration для поиска класса, реал. это интерфейс
        //Class<? extends T> implementationClass;
        ArrayList<Class<? extends T>> implementationClasses;

        if (clazz.isInterface()) {
            implementationClasses = beanLocator.getImplementationClass(clazz);
            //implementationClass = implementationClasses.get(0);
        } else {
            //implementationClass = clazz;
            implementationClasses = new ArrayList<>();
            implementationClasses.add(clazz);
        }

        // Перед получением конструктора сервиса, который нужно создать, ищем все аттрибуты (fields), которые
        // нужно внедрить и для них точно так же создавать экземпляр класса и внедрять.
        try {
            if (implementationClasses.size() > 1) {
                var f = 3;
            }

            // Пройдёся по каждой реализации интерфейса, чтобы среди нескольких реализаций интерфейсов при помощи
            // аннотации Named определить, какую именно реализацию привязывать.
            if (implementationClasses.size() == 0) {
                throw new RuntimeException("No interface implementation found for interface: " + clazz);
            } else if (implementationClasses.size() == 1) {
                T bean = implementationClasses.get(0).getDeclaredConstructor().newInstance();

                for (int j = 0; j < implementationClasses.get(0).getDeclaredFields().length; j++) {
                    Field field = implementationClasses.get(0).getDeclaredFields()[j];
                    if (field.isAnnotationPresent((Inject.class))) {
                        field.setAccessible(true);
                        field.set(bean, context.getBean(field.getType()));
                    }
                }
            } else {
                for (int i = 0; i < implementationClasses.size(); i++) {
                    T bean = implementationClasses.get(i).getDeclaredConstructor().newInstance();

                    // Пройдёмся по всем полям Бина и определим, какие зависимости необходимо внедрить
                    // Пример: класс MusicPlayer. Зависимость - класс ClassicalMusic (интерфейс Music).
                    // Получим только те поля, которые аннотированы через Inject.
                    // Пройдёся через все такие поля, которые надо инжектить.
                    // Мы понимаем, какой это тип. Теперь его нужно создать и внедрить в Бин.
                    // TODO Реализация внедрения через поля. Надо сделать реализацию через констуркторы и сеттеры
                    for (int j = 0; j < implementationClasses.get(i).getDeclaredFields().length; j++) {
                        Field field = implementationClasses.get(i).getDeclaredFields()[j];
                        if (field.isAnnotationPresent((Inject.class)) && field.isAnnotationPresent((Named.class))
                                && field.getAnnotation(Named.class) == bean.getClass().getAnnotation(Named.class)) {
                            field.setAccessible(true);
                            field.set(bean, context.getBean(field.getType(), field.getAnnotation(Inject.class)));
                        }
                    }
                }
            }

            // Получаем конструктор (по умолчанию) сервиса, который нужно создать.
            return null;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e.getCause());
        }
    }*/

    // по интерфейсу возвращаем бин - объект класса, реализующий интерфейс.
    public <T> Class<? extends T> getBean(Class<T> clazz, Class<T> parentClazz, Annotation[] annotations) {

        // если мы получили интерфейс, то тогда переходим в BeanConfiguration для поиска класса, реал. это интерфейс
        ArrayList<Class<? extends T>> implementationClasses;
        Class<? extends T> implementationClass = clazz;

        if (clazz.isInterface()) {
            implementationClasses = beanLocator.getImplementationClass(clazz);
            // Пройдёся по каждой реализации интерфейса, чтобы среди нескольких реализаций интерфейсов при помощи
            // аннотации Named определить, какую именно реализацию привязывать.
            if (implementationClasses.size() == 0) {
                throw new RuntimeException("No interface implementation found for interface: " + clazz);
            } else {
                int isNamedAnnotation = -1;
                for (int i = 0; i < annotations.length; i++) {
                    if (annotations[i].annotationType().equals(Named.class)) {
                        isNamedAnnotation = i;
                    }
                }
                boolean isMatchFound = false;
                // Пройдёмся по всем полям Бина и определим, какие зависимости необходимо внедрить
                if (isNamedAnnotation >= 0) {
                    for (int i = 0; i < implementationClasses.size(); i++) {
                        if (!implementationClasses.get(i).isAnnotationPresent(Named.class)) {
                            continue;
                        }
                        if (annotations[isNamedAnnotation].equals(implementationClasses.get(i).
                                getAnnotation(Named.class))) {
                            if (isMatchFound) {
                                throw new RuntimeException("Cannot perform injection" + "\nThe field of class (" +
                                        parentClazz + ") with Named annotation (" + annotations[isNamedAnnotation] +
                                        ") has several appropriate implementations with these id in annotation");
                            }
                            isMatchFound = true;
                            implementationClass = implementationClasses.get(i);
                        }
                    }
                } else if (implementationClasses.size() > 1) {
                    throw new RuntimeException("Cannot perform injection" + "\nThe field of class (" +
                            parentClazz + ") has several appropriate implementations with these id in annotation");
                } else {
                    isMatchFound = true;
                    implementationClass = implementationClasses.get(0);
                }
                if (!isMatchFound) {
                    throw new RuntimeException("Cannot perform injection" + "\nThe field of class (" + parentClazz +
                            ") has specified such an id that no one of interface (" + clazz + ") implementation has");
                }
            }
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
            // TODO Реализация внедрения через поля. Надо сделать реализацию через констуркторы и сеттеры
            for (int i = 0; i < implementationClass.getDeclaredFields().length; i++) {
                Field field = implementationClass.getDeclaredFields()[i];
                if (field.isAnnotationPresent((Inject.class))) {
                    field.setAccessible(true);
                    var a = context.getBean(field.getType(), (Class) implementationClass, field.getAnnotations());
                    field.set(bean, a);
                }
            }

            // Получаем конструктор (по умолчанию) сервиса, который нужно создать.
            return implementationClass;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                NoSuchMethodException e) {
            throw new RuntimeException(e.getCause());
        }
    }

}
