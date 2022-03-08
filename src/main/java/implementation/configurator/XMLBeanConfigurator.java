package implementation.configurator;

/**
 * Реализация BeanConfigurator на XML.
 */
public class XMLBeanConfigurator implements BeanConfigurator {
    @Override
    public <T> Class<? extends T> getImplementationClass(Class<T> interfaceClass) {
        return null;
    }
}
