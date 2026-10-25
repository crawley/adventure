package adventure;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.NoSuchElementException;

import adventure.actions.Action;
import adventure.actions.Actions;

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
			throw new MissingArenaException("Problem loading arena '" +
					fileName + "'", ex);
		}
		player = new Actor("player", arena.getStart(), arena);
	}

	@Override
	public void run() {
	    input = new Scanner(System.in);
	    Location here = player.getCurrentLocation();
		System.out.println("You are " + here.getType().getPreposition() 
		        + " " + here.getName());
		try {
			while (true) {
				Command command;
				if ((command = readCommand()) != null) {
					command.perform(player);
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
