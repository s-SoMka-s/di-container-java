package framework.locator;

import java.util.ArrayList;

/**
 * Реализация BeanConfigurator на XML.
 */
public class XMLBeanLocator implements BeanLocator {
    @Override
    public <T> ArrayList<Class<? extends T>> getImplementationClass(Class<T> interfaceClass) {
        return null;
    }
}
