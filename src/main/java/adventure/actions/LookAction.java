package adventure.actions;

import java.util.List;
import java.util.stream.Collectors;

import adventure.Actor;
import adventure.Item;
import adventure.Location;

public class LookAction implements Action {

	@Override
	public void perform(Actor actor, String[] args) {
	    Location here = actor.getCurrentLocation();
		if (here.getDescription() == null) {
		    System.out.println("You are in " + here.getName());
		} else {
		    System.out.println(here.getDescription());
		}
		List<Item> items = here.getItems();
		boolean firstItem = true;
		for (Item item: items) {
		    if (!item.isHidden()) {
		        if (firstItem) {
		            System.out.println("You can see the following items: ");
		            firstItem = false;
		        }
		        System.out.println(
		                "  " + item.getAbbrev() + ": " + item.getName());
		    }
		}
		Long exitCount = here.getExits().stream()
		        .collect(Collectors.counting());
		System.out.println("There are " + exitCount + " ways to leave here");
	}

    @Override
    public String describe() {
        return "Describe the current location.";
    }
}
