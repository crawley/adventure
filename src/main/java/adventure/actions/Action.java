package adventure.actions;

import adventure.Person;

public interface Action {
	
	public int perform(Person target, String[] args);
	
	public String describe();

}
