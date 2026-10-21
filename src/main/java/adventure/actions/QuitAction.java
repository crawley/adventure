package adventure.actions;

import adventure.Actor;
import adventure.QuitException;

public class QuitAction implements Action {

	@Override
	public void perform(Actor actor, String[] args) {
		throw new QuitException();
	}

	@Override
    public String describe() {
        return "Quit the game.  (Game state is not saved.)";
    }
}
