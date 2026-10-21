package adventure.actions;

import adventure.Actor;

public class HelpAction implements Action {

	@Override
	public void perform(Actor actor, String[] args) {
	    if (args.length == 0) {
	        System.out.println("What action do you want help for?");
	    } else if (args.length > 1) {
            System.out.println("Only one action");
        } else {
	        Action action = Actions.getInstance().get(args[0]); 
	        if (action == null) {
	            System.out.println("There is no known action '" + action + "'");
	        } else {
	            System.out.println(args[0] + " - " + action.describe());
	        }
        }
	}

    @Override
    public String describe() {
        return "Provide help information for an action.";
    }
}
