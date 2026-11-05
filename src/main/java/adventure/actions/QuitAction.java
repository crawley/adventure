package adventure.actions;

import adventure.Person;
import adventure.QuitException;

public class QuitAction implements Action {

	@Override
	public int perform(Person actor, String[] args) {
		throw new QuitException();
	}

	@Override
    public String describe() {
        return "Quit the game.  (Game state is not saved.)";
    }
}
