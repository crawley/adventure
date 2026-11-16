package adventure.actions;

public class ActionException extends Exception {
    
    private static final long serialVersionUID = -2658191415282235014L;
    
    private final ActionResult result;

    public ActionException(String message) {
        super(message);
        this.result = null;
    }

    public ActionException(ActionResult result) {
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
