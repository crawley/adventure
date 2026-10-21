package adventure;

public class AlreadyContainsException extends RuntimeException {

    private static final long serialVersionUID = -4632555408309180334L;

    public AlreadyContainsException(String message) {
        super(message);
    }
}
