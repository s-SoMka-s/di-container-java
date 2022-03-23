package framework.locator;

import java.util.ArrayList;

public interface BeanLocator {

    <T> ArrayList<Class<? extends T>> getImplementationClass(Class<T> interfaceClass);
}
