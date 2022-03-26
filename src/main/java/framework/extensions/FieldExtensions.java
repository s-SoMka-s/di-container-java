package framework.extensions;

import framework.annotations.Value;
import framework.exceptions.IncorrectFieldAnnotationsException;

import javax.inject.Inject;
import java.lang.reflect.Field;

public class FieldExtensions {
    public static void ensureAnnotationsValid(Field field) throws IncorrectFieldAnnotationsException {
        // Если поле имеет аннотации Value и Inject, то это ошибка, так как смысла такая конструкция не имеет
        if (field.isAnnotationPresent(Value.class) && field.isAnnotationPresent(Inject.class)) {
            throw new IncorrectFieldAnnotationsException();
        }
    }
}
