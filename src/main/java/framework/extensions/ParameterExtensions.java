package framework.extensions;

import framework.annotations.Value;

import java.lang.reflect.Parameter;
import java.util.Arrays;

public class ParameterExtensions {
    public static boolean isOnlyValueAnnotated(Parameter[] parameters) {
        return Arrays.stream(parameters).allMatch(p -> p.isAnnotationPresent(Value.class));
    }
}
