package implementation.locator;

import java.util.ArrayList;
import java.util.Set;

public interface BeanLocator {

    <T> ArrayList<Class<? extends T>> getImplementationClass(Class<T> interfaceClass);
}
