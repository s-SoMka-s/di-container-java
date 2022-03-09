package implementation.locator;

/**
 * Реализация BeanConfigurator на XML.
 */
public class XMLBeanLocator implements BeanLocator {
    @Override
    public <T> Class<? extends T> getImplementationClass(Class<T> interfaceClass) {
        return null;
    }
}
