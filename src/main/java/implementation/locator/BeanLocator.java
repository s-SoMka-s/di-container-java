package implementation.locator;

public interface BeanLocator {

    <T> Class<? extends T> getImplementationClass(Class<T> interfaceClass);
}
