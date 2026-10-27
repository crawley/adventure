package adventure.actions;

import adventure.Actor;

public interface Action {
	
	public int perform(Actor target, String[] args);
	
	public String describe();

}
