package framework.exceptions;

public class IncorrectFieldAnnotationsException extends FrameWorkException {
    public IncorrectFieldAnnotationsException(String inType, String inField) {
        super("Value and Inject annotations cannot be together!\n", "In type: " + inType + "\nIn field: " + inField);
    }

    public IncorrectFieldAnnotationsException() {
        super("Value and Inject annotations cannot be together!");
    }
}
