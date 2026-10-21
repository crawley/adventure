package adventure;

public class AlreadyContainedException extends RuntimeException {

    private static final long serialVersionUID = -7429580873961514105L;

    public AlreadyContainedException(String message) {
        super(message);
    }

}
