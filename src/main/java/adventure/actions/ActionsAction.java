package adventure.actions;

import adventure.Actor;

public class ActionsAction implements Action {

	@Override
	public void perform(Actor target, String[] args) {
	    Actions actions = Actions.getInstance();
	    System.out.print("Actions: ");
	    for (String name: actions.actionNames()) {
	        System.out.print(name + " ");
	    }
		System.out.println();
		System.out.print("Aliases: ");
		for (String name: actions.aliasNames()) {
		    System.out.print(name + " ");
		}
		System.out.println();

	}

    @Override
    public String describe() {
        return "List all available actions and aliases.";
    }

}
