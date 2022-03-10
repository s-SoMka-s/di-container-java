package implementation.context;

import implementation.Bean;
import implementation.Scope;
import implementation.configuration.Configuration;
import implementation.configuration.JavaConfiguration;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Context {

    private final Configuration configuration;
    private final Map<Class, Bean> beanMapByClass = new ConcurrentHashMap<>();
    private final Map<Class, Class<?>> classMapByClass = new ConcurrentHashMap<>();
    private final Map<String, Class<?>> classMapByName = new ConcurrentHashMap<>();
    //private final Map<String, Scope> scopeMapByName = new ConcurrentHashMap<>();
    Set<String> defaultNames = new HashSet<>();
    private final Map<String, Bean> beanMapByName = new ConcurrentHashMap<>();

    // Map: Интерфейс -> конкретный объект класса, реализующего интерфейс.
    // При запросе на получение бина, мы посмотрим в Map, если там уже инициализированный бин для данного интерфейса.
    // Если есть, то мы его вернём. Если нет, то создадим и положим в Map.s

    public Context() {
        configuration = new JavaConfiguration();
        refresh();
    }

    // возвращаем бин по его классу - всё аналогично BeanFactory.

    public <T> T getBean(Class<T> clazz) {
        if (beanMapByClass.containsKey(clazz)) {
            return (T) beanMapByClass.get(clazz).getBean();
        }
        throw new RuntimeException("No such bean: " + clazz);
    }

    public void refresh() {
        final Reflections scanner = new Reflections(configuration.getPackageToScan(), new SubTypesScanner(),
                new TypeAnnotationsScanner(),
                new FieldAnnotationsScanner());

        Set<Class<?>> namedClasses = scanner.getTypesAnnotatedWith(Named.class);
        //Set<Field> injectedFields = scanner.getFieldsAnnotatedWith(Inject.class);

        /**
         * Для начала ищу все классы, которые по сути являются компонентами, т.е. имеется аннотация Named и
         * нет(!) аннотации Inject.
         * В аннотациях Named могут быть указаны id компонента или не могут. Если не могут, то задаём базовое
         * имя, равное имени класса, т.е. будет уникальным, и отмечаем, что это имя дефолтное.
         */
        for (Class beanClass : namedClasses) {
            if (!beanClass.isAnnotationPresent(Inject.class)) {
                if (!((Named) beanClass.getAnnotation(Named.class)).value().equals("")) {
                    if (classMapByName.get(((Named) beanClass.getAnnotation(Named.class)).value()) != null) {
                        throw new RuntimeException("Cannot couple class/interface" +
                                "\nMultiple use of the same ID: " +
                                ((Named) beanClass.getAnnotation(Named.class)).value());
                    }
                    classMapByName.put(((Named) beanClass.getAnnotation(Named.class)).value(), beanClass);
                } else {
                    classMapByName.put(defaultName(beanClass), beanClass);
                    defaultNames.add(defaultName(beanClass));
                }
            }
        }


        /**
         * Согласно спецификации, начнём с создания бинов указанных выше компонентов. Будем пытаться внедрять
         * зависимости: анализируем (пока что) поля на наличие Injected и пытаемся произвести байндинг
         * (возможен уход внутрь по рекурсии, чтобы атомарные компоненты были сперва определены)
         */
        // TODO Ниже всё идет реализация под поля! Нужно интгрировать аккуратно для сеттеров и конструкторов
        for (int i = 0; i < namedClasses.size(); i++) {
            createBean((Class<?>) namedClasses.toArray()[i]);
        }
    }

    public Object createBean(Class<?> beanClass) {
        JavaConfiguration javaConfiguration = new JavaConfiguration();
        Reflections scanner = new Reflections(javaConfiguration.getPackageToScan());

        // Если уже есть бин, то просто его возвращаем
        if (beanMapByClass.get(beanClass) != null) {
            return beanMapByClass.get(beanClass).getBean();
        }

        // Создаём сам бин
        Constructor constructor = null;
        Object object = null;
        try {
            constructor = beanClass.getDeclaredConstructor();
            object = constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        // Работа с полями!!!
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {

            // Если есть аннотации Inject
            Annotation inject = field.getAnnotation(Inject.class);
            if (inject != null) {

                // Если есть уточнение по имени
                if (field.isAnnotationPresent(Named.class)) {

                    // Получаем имя, смотрим, пустое ли оно.
                    // Если пустое, то это ошибка, так как эта аннотация Named не имеет смысла.
                    // Иначе идём далее
                    String name = field.getAnnotation(Named.class).value();
                    Object diObj;
                    if (!name.isEmpty()) {

                        // Если бин для класса/интерф поля уже существует, то просто его достаём
                        if (beanMapByName.get(name) != null) {
                            diObj = beanMapByName.get(name).getBean();
                        } else {

                            // проверяем, если вообще подходящий класс/интерф поля
                            // Да -> создаем его бин (шаг вглубь)
                            // Нет -> ошибка
                            if (classMapByName.get(name) != null) {
                                diObj = createBean(classMapByName.get(name));
                            } else {
                                throw new RuntimeException("Cannot couple class/interface: " + beanClass +
                                        "\nNo such component with specified id exists!: " + name);
                            }
                        }

                        // Магия: внедрение бина в ПОЛЕ.
                        field.setAccessible(true);
                        try {
                            field.set(object, diObj);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else {

                        // Тот самый ненужный пустой Named при Inject
                        throw new RuntimeException("Misuse of Named annotation in the field: " + field.getName() +
                                "\nIn the class/interface: " + beanClass);
                    }
                } else {

                    if (field.getType().isInterface()) {
                        // Если класс у поля интерфейс, то ищем все классы, реализующие этот интерфейс
                        var implementationClasses = new ArrayList<>(scanner.getSubTypesOf(field.getType()));
                        var engagedImplementationClasses = new ArrayList<>(implementationClasses);
                        boolean appropriateImplementationFound = false;

                        // Проверяем для каждой реализации, что она вообще используется (т.е. помечена Named)
                        // Если не используется, то ёё отбрасываем.
                        for (int i = 0; i < implementationClasses.size(); i++) {
                            if (!implementationClasses.get(i).isAnnotationPresent(Named.class)) {
                                engagedImplementationClasses.remove(implementationClasses.get(i));
                            }
                        }

                        // Если больше 1 реализации -> неопределённость, ибо нельзя однозначно выбрать реализацию
                        if (engagedImplementationClasses.size() > 1) {
                            throw new RuntimeException("Cannot couple interface: " + beanClass +
                                    "\nThere are several appropriate implementations!");
                        }

                        // Для каждой оставшейся реализации смотрим, что у нее ТАК ЖЕ нет конретного имени в Named,
                        // Ведь если есть, то мы не можем использовать эту реализацию, так как у нашего искомого поля
                        // нет конкртеной конкретизации.
                        // Пытаеся создать бин для подходяший реализации
                        for (int i = 0; i < engagedImplementationClasses.size(); i++) {
                            if (engagedImplementationClasses.get(i).getAnnotation(Named.class).value().equals("")) {
                                Object diObj = createBean(engagedImplementationClasses.get(i));
                                field.setAccessible(true);
                                try {
                                    field.set(object, diObj);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                                appropriateImplementationFound = true;
                            }
                        }

                        // Если не удалось найти (=> создать) реалзиацию, то это ошибка.
                        if (!appropriateImplementationFound) {
                            throw new RuntimeException("Cannot couple interface: " + beanClass +
                                    "\nThe possible component has its unique ids! Try specify id.");
                        }
                    } else {
                        // Если не интерфейс, то просто создаём бин для самого класса, если такой класс вообще
                        // был указан как компонент
                        if (classMapByName.get(defaultName(field.getType())) != null) {
                            Object diObj = createBean(field.getType());
                            field.setAccessible(true);
                            try {
                                field.set(object, diObj);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        } else {
                            // Если не был класс указан как компонент => не с чем внедрять (либо в принципе
                            // нет компонента такого класса, либо компоненты имеют конкретные имена (а наше поле
                            // нет)) => ошибка.
                            throw new RuntimeException("Cannot couple class/interface: " + beanClass +
                                    "\nAll possible components have their unique ids! Try specify id." +
                                    "\nOr there is no such component at all!");
                        }
                    }
                }
            }
        }

        // Берём имя нашего компонента
        Named beanClassAnnotation = beanClass.getAnnotation(Named.class);

        // Получаем его scope
        Scope scope = defineScope(beanClass);

        // Если имя компонента не указано, то кладём бин по имени и классу, используя дефолтное имя, иначе его истинное
        if (beanClassAnnotation.value().isEmpty()) {
            beanMapByName.put(defaultName(beanClass), new Bean(beanClass, defaultName(beanClass), scope, object));
            //scopeMapByName.put(defaultName(beanClass), scope);
            beanMapByClass.put(beanClass, new Bean(beanClass, defaultName(beanClass), scope, object));
        } else {
            beanMapByName.put(beanClassAnnotation.value(), new Bean(beanClass, beanClassAnnotation.value(), scope, object));
            beanMapByClass.put(beanClass, new Bean(beanClass, beanClassAnnotation.value(), scope, object));
        }

        return object;
    }

    private String defaultName(Class clazz) {
        var a = clazz.getSimpleName();
        return a.substring(0, 1).toLowerCase() +
                a.substring(1);
    }

    private Scope defineScope(Class clazz) {
        if (clazz.isAnnotationPresent(implementation.annotation.Scope.class)) {
            if (((implementation.annotation.Scope) clazz.getAnnotation(implementation.annotation.Scope.class)).value().equals("singleton")) {
                return Scope.SINGLETON;
            }
            if (((implementation.annotation.Scope) clazz.getAnnotation(implementation.annotation.Scope.class)).value().equals("thread")) {
                return Scope.THREAD;
            }
            if (((implementation.annotation.Scope) clazz.getAnnotation(implementation.annotation.Scope.class)).value().equals("prototype")) {
                return Scope.PROTOTYPE;
            }
            throw new RuntimeException("Specified scope does not exist in class: " + clazz);
        }
        return Scope.SINGLETON;
    }
}
