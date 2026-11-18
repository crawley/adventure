package adventure.actions;

import adventure.Person;

public class ActionsAction implements Action {

	@Override
	public int perform(Person target, String[] args) {
	    Actions actions = Actions.getInstance();
	    System.out.print("Actions: ");
	    System.out.println(lineWrapList(actions.actionNames()));
		System.out.print("Aliases: ");
		System.out.println(lineWrapList(actions.aliasNames()));
        return 0;
	}
	
	private String lineWrapList(Iterable<String> names) {
	    StringBuilder sb = new StringBuilder();
	    int lineLength = 0;
	    for (String name: names) {
	        if (lineLength + name.length() > 70) {
	            lineLength = name.length();
	            sb.append("\n          ");
	        } else {
	            lineLength += name.length() + 1;
	            sb.append(" ");
	        }
	        sb.append(name);
	    }
	    return sb.toString();
	}

    @Override
    public String describe() {
        return "List all available actions and aliases.";
    }

}
