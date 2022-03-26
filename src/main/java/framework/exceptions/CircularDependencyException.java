package framework.exceptions;

public class CircularDependencyException extends FrameWorkException {
    public CircularDependencyException(String classA, String classB) {
        super("Error creating bean with name " + classA + "Requested bean is currently in creation: Is there an unresolvable circular reference?");
    }
}
