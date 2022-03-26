package framework.locator;

import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class JavaBeanLocator2 {
    private final Reflections scanner;

    // Необязательно мы должны искать реализации в коде: у нас может быть Map, в котором может быть соответ свзязь
    private final Map<Class, Class> interfaceToImplementation;

    public JavaBeanLocator2(String packageToScan, Map<Class, Class> interfaceToImplementation) {
        this.scanner = new Reflections(packageToScan);
        this.interfaceToImplementation = new ConcurrentHashMap<>(interfaceToImplementation);
    }

    // Будем пополнять наш Map по ходу поиска реализации интерфейсов в коде, чтобы потом можно было быстро получить
    // доступ к реализации интерфейса (для ко-то уже находили реализацию).
    public <T> ArrayList<Class<? extends T>> getImplementationClass(Class<T> interfaceClass) {
        if (interfaceToImplementation.get(interfaceClass) != null) {
            var arrayList = new ArrayList<Class<? extends T>>();
            arrayList.add(interfaceToImplementation.get(interfaceClass));
            return arrayList;
        } else {
            Set<Class<? extends T>> implementationClasses = scanner.getSubTypesOf(interfaceClass);

            // если реализация интерфейса не одна, то ВРЕМЕННО кидаем исключение.
            // если реализаций интерфейса ноль, то кидаем соответ. исключение.
            /*if (implementationClasses.size() > 1) {
                throw new RuntimeException("Ambiguity: TEMP!");
            } else if (implementationClasses.size() == 0) {
                throw new RuntimeException("No interface implementation found for interface: " + interfaceClass);
            }*/

            // возвращаем единственный элемент
            return new ArrayList<>(implementationClasses);
        }
        /*return interfaceToImplementation.computeIfAbsent(interfaceClass, clazz -> {
            // передаём интерфейс. Получаем множество классов, которые реализуют этот интерфейс.
            Set<Class<? extends T>> implementationClasses = scanner.getSubTypesOf(interfaceClass);

            // если реализация интерфейса не одна, то ВРЕМЕННО кидаем исключение.
            // если реализаций интерфейса ноль, то кидаем соответ. исключение.
            if (implementationClasses.size() > 1) {
                throw new RuntimeException("Ambiguity: TEMP!");
            } else if (implementationClasses.size() == 0) {
                throw new RuntimeException("No interface implementation found for interface: " + interfaceClass);
            }

            // возвращаем единственный элемент
            return implementationClasses.stream().findFirst().get();
        });*/
    }
}
