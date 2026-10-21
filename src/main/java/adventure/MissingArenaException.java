package adventure;

public class MissingArenaException extends RuntimeException {

	private static final long serialVersionUID = -8046374708791957733L;

	public MissingArenaException(String msg, Exception ex) {
		super(msg, ex);
	}

}
