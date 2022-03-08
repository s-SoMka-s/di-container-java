package implementation.bean;

public interface BeanFactory {

    /**
     * Get bean by bean name
     *
     * @param name bean Name
     * @return bean
     */
    Object getBean(String name);

    /**
     * Get bean by bean type
     *
     * @param tClass bean type
     * @param <T>    Generic T
     * @return bean
     */
    <T> T getBean(Class<T> tClass);
}
