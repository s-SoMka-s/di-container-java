package implementation.context;

import implementation.Bean;
import implementation.Scope;
import implementation.annotation.Value;
import implementation.configuration.Configuration;
import implementation.configuration.JavaConfiguration;
import org.codehaus.jackson.map.ObjectMapper;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
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

    private final Reflections scanner;

    // Список всех компонентов.
    private final Map<String, Class<?>> classMapByName = new ConcurrentHashMap<>();

    private final Map<Class, Bean> beanMapByClass = new ConcurrentHashMap<>();
    private final Map<String, Bean> beanMapByName = new ConcurrentHashMap<>();
    Set<String> defaultNames = new HashSet<>();

    //private final Map<String, Scope> scopeMapByName = new ConcurrentHashMap<>();
    //private final Map<Class, Class<?>> classMapByClass = new ConcurrentHashMap<>();

    // Map: Интерфейс -> конкретный объект класса, реализующего интерфейс.
    // При запросе на получение бина, мы посмотрим в Map, если там уже инициализированный бин для данного интерфейса.
    // Если есть, то мы его вернём. Если нет, то создадим и положим в Map.s

    public Context(String packageToScan) {
        scanner = new Reflections(packageToScan);
        new Bean(this);
        refresh();
    }

    // возвращаем бин по его классу в случае, если инъектируемый компонент не имеет конкретного id,
    // т.е. указан

    /**
     * Возвращаем бин по его классу в случае, если инъектируемый компонент не имеет конкретного id,
     * т.е. задан следюущим образом: @Named.
     *
     * @param clazz Сам класс
     * @param <T>   -
     * @return Бин
     */
    public <T> T getBean(Class<T> clazz) {
        if (beanMapByClass.containsKey(clazz)) {
            if (defaultNames.contains(getDefaultName(clazz))) {
                if (beanMapByClass.get(clazz).getScope().equals(Scope.SINGLETON)) {
                    return (T) beanMapByClass.get(clazz).getBean();
                } else {
                    return (T) createBean(clazz);
                }
            } else {
                throw new RuntimeException("Cannot get bean for class: " + clazz +
                        "\nThe possible component has its unique ids! Try specify id.");
            }
        } else {
            throw new RuntimeException("No such bean: " + clazz);
        }
    }

    /**
     * Возвращаем бин по его классу в случае, если инъектируемый компонент имеет конкретный id,
     * т.е. задан следюущим образом: @Named("имя").
     *
     * @param clazz Сам класс
     * @param <T>   -
     * @return Бин
     */
    public <T> T getBean(String name, Class<T> clazz) {
        if (beanMapByClass.containsKey(clazz)) {
            if (beanMapByName.containsKey(name)) {
                if (beanMapByName.get(name).getScope().equals(Scope.SINGLETON) ||
                        beanMapByName.get(name).getScope().equals(Scope.THREAD)) {
                    return (T) beanMapByName.get(name).getBean();
                } else {
                    return (T) createBean(clazz);
                }
            } else {
                throw new RuntimeException("No such bean with id: " + name);
            }
        } else {
            throw new RuntimeException("No such bean: " + clazz);
        }
    }

    public void refresh() {
        /*final Reflections scanner = new Reflections(configuration.getPackageToScan(), new SubTypesScanner(),
                new TypeAnnotationsScanner(),
                new FieldAnnotationsScanner());*/

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
                    classMapByName.put(getDefaultName(beanClass), beanClass);
                    defaultNames.add(getDefaultName(beanClass));
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

    /**
     * Метод создаёт бин класса beanClass и возвращает инстанс бина со всеми внедрёнными зависимостями.
     * Стоит учесть, что возвращаемый инстанс имеет смысл, лишь если scope класса singleton.
     *
     * @param beanClass класс, бин которого необходимо создать
     * @return инстанс бина
     */
    public Object createBean(Class<?> beanClass) {
        // Если уже есть бин, то просто его возвращаем (речь идёт только про синглтон тип!)
        if (beanMapByClass.get(beanClass) != null) {
            if (beanMapByClass.get(beanClass).getScope().equals(Scope.SINGLETON)) {
                return beanMapByClass.get(beanClass).getBean();
            }
        }

        // Создаём новый инстанс бина
        Object object = createInstance(beanClass);

        // Берём имя нашего компонента
        Named beanClassAnnotation = beanClass.getAnnotation(Named.class);

        // Получаем его scope
        Scope scope = getScope(beanClass);

        // Если имя компонента не указано, то кладём бин по имени и классу, используя дефолтное имя, иначе его истинное
        if (beanClassAnnotation.value().isEmpty()) {
            beanMapByName.put(getDefaultName(beanClass), new Bean(beanClass, getDefaultName(beanClass), scope, object));
            beanMapByClass.put(beanClass, new Bean(beanClass, getDefaultName(beanClass), scope, object));
        } else {
            beanMapByName.put(beanClassAnnotation.value(), new Bean(beanClass, beanClassAnnotation.value(), scope, object));
            beanMapByClass.put(beanClass, new Bean(beanClass, beanClassAnnotation.value(), scope, object));
        }

        return object;
    }

    /**
     * Метод создаёт инстанс бина (внедряет все зависимости) и возвращает объект класса beanClass.
     * Метод не владеет(!) никакой информации о существовании бина этого класса, т.е. может такое быть, что
     * бин уже создан и мы несколько раз хотим получить новый инстанс этого бина (в случае prototype и thread).
     * Опять же, метод ПРОСТО создаёт инстанс бина. Расширением метода служит createBean.
     *
     * @param beanClass класс, инстанс бина которого нужно получить
     * @return объект класса beanClass
     */
    public Object createInstance(Class<?> beanClass) {
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

            // Если поле имеет аннотации Value и Inject, то это ошибка, так как смысла такая конструкция не имеет
            if (field.isAnnotationPresent(Value.class) && field.isAnnotationPresent(Inject.class)) {
                throw new RuntimeException("Value and Inject annotations cannot be together!" +
                        "\nIn the field: " + field.getName() +
                        "\nIn the class/interface: " + beanClass);
            }

            // Если поле имеет аннотацию Value, то производим внедрение значений
            if (field.isAnnotationPresent(Value.class)) {
                valueAnnotation(field, object);
            }

            // Если поле имеет аннотацию Inject, то производим внедрение зависимостей
            if (field.isAnnotationPresent(Inject.class)) {
                injectAnnotation(beanClass, field, object);
            }
        }

        return object;
    }

    /**
     * Производим непосредственное внедрение зависимостей (заточена пока под поле)
     *
     * @param beanClass класс бина, которой необходимо создать
     * @param field     поле, аннотированное Inject-ом
     * @param object    будущий инстанс бина
     */
    private void injectAnnotation(Class<?> beanClass, Field field, Object object) {
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

                // Сама инъекция: непосредственное внедрение бина в ПОЛЕ.
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
                if (classMapByName.get(getDefaultName(field.getType())) != null) {
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

    private void valueAnnotation(Field field, Object object) {
        field.setAccessible(true);

        ObjectMapper mapper = new ObjectMapper();
        try {
            field.set(object, mapper.readValue(field.getAnnotation(Value.class).value(), field.getType()));
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }

        /*var fieldTypeName = field.getType().getTypeName();

        try {
            switch (fieldTypeName) {
                case "int" ->
                    field.set(object, Integer.parseInt(field.getAnnotation(Value.class).value()));
                case "String" ->
                    field.set(object, field.getAnnotation(Value.class).value());
                default ->
                    throw new RuntimeException("the Value annotation is assigned to inappropriate type" +
                            "\nIn the field: " + field +
                            "\nIn the class: " + beanClass);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * Получение дефолтного имени путём получения имени класса и замены первой буквы на строчную
     *
     * @param clazz Сам класс
     * @return дефолтное имя объекта класса clazz
     */
    private String getDefaultName(Class clazz) {
        var a = clazz.getTypeName();
        return a.substring(0, 1).toLowerCase() +
                a.substring(1);
    }

    /**
     * Получение скопа класса.
     * При отсутствии явного задания скопа выставляется Singletone
     *
     * @param clazz Сам класс
     * @return скоп класса
     */
    private Scope getScope(Class clazz) {
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
