package framework.extensions;

import framework.enums.Scope;

public class ScopeExtensions {
    /**
     * Получение скопа класса.
     * При отсутствии явного задания скопа выставляется Singletone
     *
     * @param item Сам класс
     * @return скоп класса
     */
    public static Scope getScope(Class<?> item) {
        if (!item.isAnnotationPresent(framework.annotations.Scope.class)) {
            return Scope.SINGLETON;
        }

        var scope = item.getAnnotation(framework.annotations.Scope.class).value();
        return scope;
    }
}
