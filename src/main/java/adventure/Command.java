package adventure;

import java.util.List;

import adventure.actions.Action;

public class Command {
	
	private Action action;
	private String[] args;

	public Command(Action action, List<String> args) {
		this.action = action;
		this.args = args.toArray(new String[0]);
	}

	public void perform(Actor target) {
		action.perform(target, args);
	}

}
