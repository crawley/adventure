package adventure;

public class TriggerException extends RuntimeException {

    private static final long serialVersionUID = -8184769254759574903L;

    public TriggerException(String message) {
        super(message);
    }

    public TriggerException(String message, Throwable cause) {
        super(message, cause);
    }

}
