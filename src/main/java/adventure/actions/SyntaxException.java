package adventure.actions;

public class SyntaxException extends Exception {
    
    private static final long serialVersionUID = -2658191415282235014L;
    
    private final ActionResult result;

    public SyntaxException(String message) {
        super(message);
        this.result = null;
    }

    public SyntaxException(ActionResult result) {
        super();
        this.result = result;
    }

    public ActionResult getActionResult() {
        if (result == null) {
            return new ActionResult(ActionResult.Outcome.SYNTAX, this.getMessage());
        } else {
            return result;
        }
    }

}
