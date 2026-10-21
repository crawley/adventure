package adventure.actions;

import adventure.Actor;

public interface Action {
	
	public void perform(Actor target, String[] args);
	
	public String describe();

}
