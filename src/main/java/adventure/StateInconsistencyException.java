package adventure;

public class StateInconsistencyException extends RuntimeException {
    
    private static final long serialVersionUID = 6382025206838658957L;

    public StateInconsistencyException(String message) {
        super(message);
    }

}
