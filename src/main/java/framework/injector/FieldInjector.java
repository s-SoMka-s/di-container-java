package framework.injector;

import framework.annotations.Component;
import framework.annotations.Inject;
import framework.annotations.Value;
import framework.beans.Bean;
import framework.context.NewContext;
import framework.exceptions.IncorrectFieldAnnotationsException;
import framework.extensions.NameExtensions;
import framework.scanner.Scanner;
import org.codehaus.jackson.map.ObjectMapper;

import javax.inject.Named;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class FieldInjector {
    private final NewContext context;

    public FieldInjector(NewContext context) {
        this.context = context;
    }

    /**
     * Производим внедрение зависимости в поле
     *
     * @param beanClass класс бина, которой необходимо создать
     * @param field     поле, аннотированное Inject-ом
     * @param object    будущий инстанс бина
     */
    public void injectField(Class<?> beanClass, Field field, Object object) throws IncorrectFieldAnnotationsException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        var beans = this.context.getBeanStore();
        var beanFactory = this.context.getBeanFactory();

        // Получаем имя, смотрим, пустое ли оно.
        // Если пустое, то это ошибка, так как эта аннотация Named не имеет смысла.
        // Иначе идём далее
        String InjectName = field.getAnnotation(Inject.class).value();

        Object diObj;

        if (!InjectName.isEmpty()) {

            // Если бин для класса/интерф поля уже существует, то просто его достаём
            if (beans.get(InjectName) != null) {
                diObj = beans.get(InjectName).getBean();
            } else {

                // проверяем, если вообще подходящий класс/интерф поля
                // Да -> создаем его бин (шаг вглубь)
                // Нет -> ошибка
                if (context.getScanner().getNameableComponent(InjectName) != null) {
                    diObj = beanFactory.createBean(context.getScanner().getNameableComponent(InjectName)).getBean();
                    ;
                } else {
                    throw new RuntimeException("Cannot couple class/interface: " + beanClass +
                            "\nNo such component with specified id exists!: " + InjectName);
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
            // Если бин для класса/интерф поля уже существует, то просто его достаём
            if (beans.get(NameExtensions.getDefaultName(field.getType())) != null) {
                diObj = beans.get(NameExtensions.getDefaultName(field.getType())).getBean();

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
                    var implementationClasses = new ArrayList<Class>(context.getScanner().getInterfaceImplementations(field.getType()));
                    var engagedImplementationClasses = new ArrayList<>(implementationClasses);
                    boolean appropriateImplementationFound = false;

                    // Проверяем для каждой реализации, что она вообще используется (т.е. помечена Named)
                    // Если не используется, то ёё отбрасываем.
                    for (int i = 0; i < implementationClasses.size(); i++) {
                        if (!implementationClasses.get(i).isAnnotationPresent(Component.class)) {
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
                        if (((Component) engagedImplementationClasses.get(i).getAnnotation(Component.class)).value().equals("")) {
                            diObj = beanFactory.createBean(engagedImplementationClasses.get(i)).getBean();
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
                    if (field.getType().getAnnotation(Component.class).value().equals("")) {
                        diObj = beanFactory.createBean(field.getType()).getBean();
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
    /*public void injectField(Class<?> beanClass, Field field, Object object) throws IncorrectFieldAnnotationsException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        var beans = this.context.getBeanStore();
        var beanFactory = this.context.getBeanFactory();

        var name = NameExtensions.getInjectableFieldName(field);

        var bean = beans.get(name);
        if (bean == null) {
            if (!field.isAnnotationPresent(Component.class)) {
                var waiterName = NameExtensions.getComponentName(beanClass);
                beans.addDeferred(waiterName, name);
                return;
            }

            bean = beanFactory.createBean(field.getDeclaringClass());
            beans.add(bean);
        }

        field.setAccessible(true);
        field.set(object, bean.getBean());
    }*/

    /**
     * Производим непосредственное внедрение значения в поле
     *
     * @param field  поле, аннотированное Value-м
     * @param object будущий инстанс бина
     */
    public void injectValue(Field field, Object object) throws IOException, IllegalAccessException {
        var rawValue = field.getAnnotation(Value.class).value();
        var mapper = new ObjectMapper();

        field.setAccessible(true);
        if (!rawValue.startsWith("$")) {
            field.set(object, mapper.readValue(rawValue, field.getType()));

            return;
        }

        var configuration = this.context.getCurrentConfiguration();
        if (configuration == null) {
            throw new RuntimeException("No declared configuration!");
        }

        var key = rawValue.substring(1);
        var value = configuration.getValue(key);
        if (value == null) {
            throw new RuntimeException("No such variable id in the config file!" + "\nVariable id: " + rawValue);
        }

        field.set(object, value);
    }
}
