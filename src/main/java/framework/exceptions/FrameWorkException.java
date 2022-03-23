package framework.exceptions;

public class FrameWorkException extends Exception{
    public FrameWorkException(String reason, String location) {
        super(reason + location);
    }

    public FrameWorkException(String reason) {
        super(reason);
    }
}
