package implementation.context;

import implementation.Bean;
import implementation.Scope;
import implementation.configuration.Configuration;
import implementation.configuration.JavaConfiguration;
import implementation.factory.BeanFactory;
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

    private BeanFactory beanFactory;
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
        BeanFactory beanFactory = new implementation.factory.BeanFactory(this);
        configuration = new JavaConfiguration();
        this.setBeanFactory(beanFactory);
        refresh();
    }

    // возвращаем бин по его классу - всё аналогично BeanFactory.

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public <T> T getBean(Class<T> clazz) {
        if (beanMapByClass.containsKey(clazz)) {
            return (T) beanMapByClass.get(clazz).getBean();
        }

        T bean;
        var beanClass = beanFactory.getBean(clazz, null, null);
        try {
            bean = beanClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                NoSuchMethodException e) {
            throw new RuntimeException();
        }

        //beanMapByClass.put(clazz, bean);
        classMapByClass.put(clazz, beanClass);

        return bean;
    }

    public <T> T getBean(Class<T> clazz, Class<T> parentsClass, Annotation[] annotations) {
        // Если уже есть, то его и возвращаем
        if (classMapByClass.containsKey(clazz)) {
            if (classMapByClass.get(clazz).isAnnotationPresent(Named.class)) {
                if (classMapByClass.get(clazz).getAnnotation(Named.class).equals(annotations[1])) {
                    return (T) beanMapByClass.get(clazz);
                }
            }
        }

        //Если нет, то создаём, кладём и возвращаем
        T bean;
        var beanClass = beanFactory.getBean(clazz, parentsClass, annotations);
        try {
            bean = beanClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException();
        }

        if (beanClass.isAnnotationPresent(Named.class)) {
            classMapByClass.put(clazz, beanClass);
            //beanMapByName.put(beanClass.getAnnotation(Named.class).value(), clazz);
            //beanMapByClass.put(clazz, bean);
        }

        return bean;
    }

    public void refresh() {
        final Reflections scanner = new Reflections(configuration.getPackageToScan(), new SubTypesScanner(),
                new TypeAnnotationsScanner(),
                new FieldAnnotationsScanner());

        Set<Class<?>> namedClasses = scanner.getTypesAnnotatedWith(Named.class);
        //Set<Field> injectedFields = scanner.getFieldsAnnotatedWith(Inject.class);
        //System.out.println("scan classes with Bean annotation : " + namedClasses.toString());

        for (Class beanClass : namedClasses) {
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


        for (int i = namedClasses.size() - 1; i >= 0; i--) {
            //System.out.println("PRE" + namedClasses.toArray()[i]);
            createBean((Class<?>) namedClasses.toArray()[i]);
        }
    }

    public Object createBean(Class<?> beanClass) {
        JavaConfiguration javaConfiguration = new JavaConfiguration();
        Reflections scanner = new Reflections(javaConfiguration.getPackageToScan());

        //System.out.println("Enter" + beanClass);
        if (beanMapByClass.get(beanClass) != null) {
            return beanMapByClass.get(beanClass).getBean();
        }
        Constructor constructor = null;
        Object object = null;
        try {
            constructor = beanClass.getDeclaredConstructor();
            object = constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            Annotation inject = field.getAnnotation(Inject.class);
            if (inject != null) {
                if (field.isAnnotationPresent(Named.class)) {
                    String name = field.getAnnotation(Named.class).value();
                    Object diObj;
                    if (!name.isEmpty()) {
                        if (beanMapByName.get(name) != null) {
                            diObj = beanMapByName.get(name).getBean();
                        } else {
                            if (classMapByName.get(name) != null) {
                                diObj = createBean(classMapByName.get(name));
                            } else {
                                throw new RuntimeException("Cannot couple class/interface: " + beanClass +
                                        "\nNo such component with specified id exists!: " + name);
                            }
                        }
                        field.setAccessible(true);
                        try {
                            field.set(object, diObj);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (field.getType().isInterface()) {
                        var implementationClasses = new ArrayList<>(scanner.getSubTypesOf(field.getType()));
                        var engagedImplementationClasses = new ArrayList<>(implementationClasses);
                        boolean appropriateImplementationFound = false;
                        for (int i = 0; i < implementationClasses.size(); i++) {
                            if (!implementationClasses.get(i).isAnnotationPresent(Named.class)) {
                                engagedImplementationClasses.remove(implementationClasses.get(i));
                            }
                        }
                        if (engagedImplementationClasses.size() > 1) {
                            throw new RuntimeException("Cannot couple interface: " + beanClass +
                                    "\nThere are several appropriate implementations!");
                        }
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
                        if (!appropriateImplementationFound) {
                            throw new RuntimeException("Cannot couple interface: " + beanClass +
                                    "\nThe possible component has its unique ids! Try specify id.");
                        }
                    } else {
                        if (classMapByName.get(defaultName(field.getType())) != null) {
                            Object diObj = createBean(field.getType());
                            field.setAccessible(true);
                            try {
                                field.set(object, diObj);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        } else {
                            throw new RuntimeException("Cannot couple class/interface: " + beanClass +
                                    "\nAll possible components have their unique ids! Try specify id.");
                        }
                    }
                }
            }
        }

        Named beanClassAnnotation = beanClass.getAnnotation(Named.class);

        if (beanClassAnnotation.value().equals("PIPA")) {
            var a = 4;
        }

        Scope scope = defineScope(beanClass);

        if (beanClassAnnotation.value().isEmpty()) {
            beanMapByName.put(defaultName(beanClass), new Bean(beanClass, defaultName(beanClass), scope, object));
            //scopeMapByName.put(defaultName(beanClass), scope);
            beanMapByClass.put(beanClass, new Bean(beanClass, defaultName(beanClass), scope, object));
        } else {
            beanMapByName.put(beanClassAnnotation.value(), new Bean(beanClass, beanClassAnnotation.value(), scope, object));
            beanMapByClass.put(beanClass, new Bean(beanClass, beanClassAnnotation.value(), scope, object));
        }

        //System.out.println("Leave" + beanClass);

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
