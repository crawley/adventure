package adventure.actions;

import java.util.Map;
import java.util.TreeMap;

import adventure.Direction;

public class Actions {
    
    private final TreeMap<String, Action> actions;
    private final Map<String, String> aliases;
    
    private static Actions instance = new Actions();
        
    private Actions() {
        actions = new TreeMap<>();
        aliases = new TreeMap<>();
        actions.put("where", new WhereAction());
        actions.put("look", new LookAction());
        actions.put("describe", new DescribeAction());
        actions.put("take", new TakeAction());
        actions.put("drop", new DropAction());
        actions.put("wear", new WearAction());
        actions.put("remove", new RemoveAction());
        actions.put("move", new MoveAction());
        actions.put("open", new OpenAction());
        actions.put("close", new CloseAction());
        actions.put("lock", new LockAction());
        actions.put("unlock", new UnlockAction());
        actions.put("put", new PutAction());
        actions.put("get", new GetAction());
        actions.put("pet", new PetAction());
        actions.put("eat", new EatAction());
        actions.put("inventory", new InventoryAction());
        aliases.put("invent", "inventory");
        actions.put("statistics", new StatisticsAction());
        aliases.put("stats", "statistics");
        actions.put("quit", new QuitAction());
        actions.put("actions", new ActionsAction());
        aliases.put("?", "actions");
        actions.put("help", new HelpAction());
        for (Direction d : Direction.values()) {
            actions.put(d.getName(), new GoAction(d));
            aliases.put(d.toString(), d.getName());
        }
    }
    
    public static Actions getInstance() {
        return instance;
    }

    public Action get(String name) {
        Action action = actions.get(name);
        if (action == null) {
            String alias = aliases.get(name);
            if (alias != null) {
                action = actions.get(alias);
            }
        }
        return action;
    }
    
    public Iterable<String> actionNames() {
        return actions.keySet();
    }
    
    public Iterable<String> aliasNames() {
        return aliases.keySet();
    }

}
