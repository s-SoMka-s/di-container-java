package framework.context;

import framework.Bean;
import framework.Scope;
import framework.annotations.Value;
import lombok.SneakyThrows;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.reflections.Reflections;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Context {

    private final Reflections scanner;
    private final String valuesToScan;

    // Контейнер всех переменных из конфиг файла JSON
    //private static class VariablesJSON {
    private Map<String, Object> variablesJSON = new ConcurrentHashMap<>();
    //}

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
        this.scanner = new Reflections(packageToScan);
        this.valuesToScan = null;
        new Bean(this);
        refresh();
    }

    public Context(String packageToScan, String valuesToScan) {
        this.scanner = new Reflections(packageToScan);
        this.valuesToScan = valuesToScan;
        new Bean(this);
        deserialize();
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
            throw new RuntimeException("No such bean: " + clazz);
        }

        if (defaultNames.contains(getDefaultName(clazz))) {
            throw new RuntimeException("Cannot get bean for class: " + clazz +
                    "\nThe possible component has its unique ids! Try specify id.");
        }

        var bean = beanMapByClass.get(clazz);
        return getByScope(bean, clazz);
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
        if (!beanMapByClass.containsKey(clazz)) {
            throw new RuntimeException("No such bean: " + clazz);
        }

        if (beanMapByName.containsKey(name)) {
            throw new RuntimeException("No such bean with id: " + name);
        }

        var bean = beanMapByName.get(name);
        return getByScope(bean, clazz);
    }

    private <T> T getByScope(Bean bean, Class<T> clazz) {
        var scope = bean.getScope();
        return switch (scope) {
            case SINGLETON, THREAD -> (T) bean.getBean();
            case PROTOTYPE -> (T) createBean(clazz);
        };
    }

    public void deserialize() {
        File file = new File(valuesToScan);

        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<ConcurrentHashMap<String, Object>> typeRef = new TypeReference<>() {
        };

        try {
            variablesJSON = objectMapper.readValue(file, typeRef);
        } catch (IOException e) {
            e.printStackTrace();
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
            if (beanClass.isAnnotationPresent(Inject.class)) {
                continue;
            }

            var name = ((Named) beanClass.getAnnotation(Named.class)).value();
            if (name.isEmpty()) {
                name = getDefaultName(beanClass);
                defaultNames.add(name);
            }

            if (classMapByName.get(name) != null) {
                throw new RuntimeException("Cannot couple class/interface" +
                        "\nMultiple use of the same ID: " + name);
            }

            classMapByName.put(name, beanClass);
        }

        /**
         * Согласно спецификации, начнём с создания бинов указанных выше компонентов. Будем пытаться внедрять
         * зависимости: анализируем (пока что) поля на наличие Injected и пытаемся произвести байндинг
         * (возможен уход внутрь по рекурсии, чтобы атомарные компоненты были сперва определены)
         */
        // TODO Ниже всё идет реализация под поля! Нужно интгрировать аккуратно для сеттеров и конструкторов
        for (var item : namedClasses) {
            createBean(item);
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
        var existed = beanMapByClass.get(beanClass);
        var scope = getScope(beanClass);

        if (existed != null) {
            if (scope.equals(Scope.SINGLETON)) {
                return existed.getBean();
            }
        }

        var instance = createInstance(beanClass);

        // Берём имя нашего компонента
        var name = beanClass.getAnnotation(Named.class).value();

        // Если имя компонента не указано, то кладём бин по имени и классу, используя дефолтное имя, иначе его истинное
        if (name.isEmpty()) {
            name = getDefaultName(beanClass);
        }

        beanMapByName.put(name, new Bean(beanClass, name, scope, instance));
        beanMapByClass.put(beanClass, new Bean(beanClass, name, scope, instance));

        return instance;
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
    @SneakyThrows
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
                valueAnnotation(beanClass, field, object);
            }

            // Если поле имеет аннотацию Inject, то производим внедрение зависимостей
            if (field.isAnnotationPresent(Inject.class)) {
                injectAnnotation(beanClass, field, object);
            }
        }

        var constructors = Arrays.stream(beanClass.getConstructors()).filter(c -> c.isAnnotationPresent(Inject.class)).collect(Collectors.toList());

        return object;
    }

    private boolean onlyInjectAnnotatedPredicate(Constructor<?> constructor) {
        return constructor.isAnnotationPresent(Inject.class);
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

            if (name.isEmpty()) {
                throw new RuntimeException("Misuse of Named annotation in the field: " + field.getName() +
                        "\nIn the class/interface: " + beanClass);
            }

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
                if (classMapByName.get(getDefaultName(field.getType())) == null) {
                    // Если не был класс указан как компонент => не с чем внедрять (либо в принципе
                    // нет компонента такого класса, либо компоненты имеют конкретные имена (а наше поле
                    // нет)) => ошибка.
                    throw new RuntimeException("Cannot couple class/interface: " + beanClass +
                            "\nAll possible components have their unique ids! Try specify id." +
                            "\nOr there is no such component at all!");
                }

                Object diObj = createBean(field.getType());
                field.setAccessible(true);
                try {
                    field.set(object, diObj);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Производим непосредственное внедрение значений (заточена пока под поле)
     *
     * @param beanClass класс бина, которой необходимо создать
     * @param field     поле, аннотированное Value-м
     * @param object    будущий инстанс бина
     */
    private void valueAnnotation(Class<?> beanClass, Field field, Object object) {
        String rawValue = field.getAnnotation(Value.class).value();
        ObjectMapper mapper = new ObjectMapper();

        field.setAccessible(true);
        try {
            // Если первый символ $, то обращаемся к контейнере значений переменных из конфига.
            // Иначе внедряем то, что непосредственно указано в параметре Value
            if (rawValue.charAt(0) == '$') {
                if (variablesJSON != null) {
                    if (variablesJSON.containsKey(rawValue.substring(1))) {
                        field.set(object, variablesJSON.get(rawValue.substring(1)));
                    } else {
                        throw new RuntimeException("No such variable id in the config file!" +
                                "\nVariable id: " + rawValue +
                                "\nClass: " + beanClass);
                    }
                } else {
                    throw new RuntimeException("No config file specified!");
                }
            } else {
                field.set(object, mapper.readValue(rawValue, field.getType()));
            }
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
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
        if (clazz.isAnnotationPresent(framework.annotations.Scope.class)) {
            if (((framework.annotations.Scope) clazz.getAnnotation(framework.annotations.Scope.class)).value().equals("singleton")) {
                return Scope.SINGLETON;
            }
            if (((framework.annotations.Scope) clazz.getAnnotation(framework.annotations.Scope.class)).value().equals("thread")) {
                return Scope.THREAD;
            }
            if (((framework.annotations.Scope) clazz.getAnnotation(framework.annotations.Scope.class)).value().equals("prototype")) {
                return Scope.PROTOTYPE;
            }
            throw new RuntimeException("Specified scope does not exist in class: " + clazz);
        }
        return Scope.SINGLETON;
    }
}
