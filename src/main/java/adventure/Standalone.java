package adventure;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import adventure.actions.Action;
import adventure.actions.Actions;
import adventure.actions.Trigger;

public class Standalone implements Runnable {
	
	private Scanner input;
	private Actor player;
	private Arena arena;

	public Standalone(String fileName) {
		try (InputStream is = ClassLoader.getSystemClassLoader()
				.getResourceAsStream(fileName))
		{
			arena = Arena.load(is);
		} catch (IOException ex) {
			throw new ArenaException("Can't find or read arena file '" +
					fileName + "'", ex);
		}
		PropertySet props = new PropertySet();
		props.add(new FloatProperty("hunger", 5, 0, 10));
		player = new Actor(Actor.Kind.PLAYER, "player", "player", 
		        "player", "the player (you)", arena.getStart(), 
		        null, props);
		arena.addActor(player);
	}

	@Override
	public void run() {
	    input = new Scanner(System.in);
	    List<Actor> allActors = Arena.getInstance().getAllActors();
	    player.showLocationAndActors();
		try {
			while (true) {
				Command command = readCommand();
				if (command != null) {
					int ticks = command.perform(player);
					for (int i = 0; i < ticks; i++) {
					    for (Actor actor: allActors) {
					        actor.trigger(Trigger.Event.TICK);
					    }
					}
				}
			}
		} catch (QuitException | NoSuchElementException e) {
			System.out.println("Bye");
		}
	}

	private Command readCommand() {
		Command command = null;
		while (command == null) {
            System.out.print("Action> ");
			@SuppressWarnings("resource")
			Scanner line = new Scanner(input.nextLine());
			if (line.hasNext()) {
				String actionName = line.next();
				Action action = Actions.getInstance().get(actionName);
				if (action == null) {
				    System.out.println("Don't grok action '" + actionName + "'");
				} else {
					List<String> args = new ArrayList<>();
					while (line.hasNext()) {
						args.add(line.next());
					}
					command = new Command(action, args);
				}
			}
		}
		return command;
	}

}
