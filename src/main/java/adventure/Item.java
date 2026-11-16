package adventure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adventure.actions.ActionResult;
import adventure.actions.Grammar;
import adventure.actions.Trigger;
import adventure.actions.Triggered;
import adventure.actions.Triggered.Verb;
import adventure.actions.VerbResult;
import adventure.actions.VerbResult.Outcome;

public class Item extends Entity {
    
    public static final long TAKEABLE = 0x1L;
    public static final long MOVEABLE = 0x2L;
    public static final long EDIBLE = 0x4L;
    public static final long WEARABLE = 0x8L;
    public static final long WIELDABLE = 0x10L;
    public static final long OPENABLE = 0x20L;
    public static final long LOCKABLE = 0x40L;
    public static final long SWITCHABLE = 0x80L;
    public static final long LIGHT = 0x100L;

    public static final long HAS_STATE_FLAGS = LOCKABLE | OPENABLE | SWITCHABLE;
    
    
    @SuppressWarnings("serial")
    public static final Map<String, FlagDescriptor> ITEM_FLAGS = new HashMap<>(){
        {
            put("TAKEABLE", new FlagDescriptor (TAKEABLE));
            put("MOVEABLE", new FlagDescriptor (MOVEABLE));
            put("WEARABLE", new FlagDescriptor (WEARABLE));
            put("WIELDABLE", new FlagDescriptor (WIELDABLE));
            put("EDIBLE", new FlagDescriptor (EDIBLE));
            put("OPENABLE", new FlagDescriptor (OPENABLE, "open", false));
            put("LOCKABLE", new FlagDescriptor (LOCKABLE, "locked", false));
            put("SWITCHABLE", new FlagDescriptor (SWITCHABLE, "on", false));
            put("LIGHT", new FlagDescriptor (LIGHT));
        }
    };
    
    public Item(String label, String name, String abbrev, 
            String description, long flags, Template base,
            List<Trigger> triggers, PropertySet properties) {
        super(label, name, abbrev, description, flags, base, triggers, properties);
        
        addFlagProperties(ITEM_FLAGS);

        if (this.properties.lookup("hidden") == null) {
            this.properties.add(new BooleanProperty("hidden", false));
        }
    }

    public boolean isTakeable() {
        return (getFlags() & TAKEABLE) != 0;
    }

    public boolean isMoveable() {
        return (getFlags() & MOVEABLE) != 0;
    }
    
    public boolean isEdible() {
        return (getFlags() & EDIBLE) != 0;
    }

    public boolean isWearable() {
        return (getFlags() & WEARABLE) != 0;
    }
    
    public boolean isaLight() {
        return (getFlags() & LIGHT) != 0;
    }
    
    public boolean isHidden() {
        Property prop = this.properties.lookup("hidden");
        return prop != null && prop.isTrue();
    }
    
    public void setHidden(boolean hidden) {
        Property prop = this.properties.lookup("hidden");
        if (prop != null) {
            ((BooleanProperty) prop).setValue(hidden);
        }
    }
    
    public boolean isBlocked() {
        Property prop = this.properties.lookup("blocked");
        return prop != null && prop.isTrue();
    }
    
    public void setBlocked(boolean blocked) {
        Property prop = this.properties.lookup("blocked");
        if (prop != null) {
            ((BooleanProperty) prop).setValue(blocked);
        }
    }
    
    public boolean isLockable() {
        return (getFlags() & LOCKABLE) != 0;
    }
    
    public boolean isLocked() {
        Property prop = this.properties.lookup("locked");
        return prop != null && prop.isTrue();
    }
    
    public void setLocked(boolean locked) {
        Property prop = this.properties.lookup("locked");
        if (prop != null) {
            ((BooleanProperty) prop).setValue(locked);
        }
    }
    
    public boolean isOpenable() {
        return (getFlags() & OPENABLE) != 0;
    }

    public boolean isOpen() {
        Property prop = this.properties.lookup("open");
        return prop != null && prop.isTrue();
    }
    
    public void setOpen(boolean open) {
        Property prop = this.properties.lookup("open");
        if (prop != null) {
            ((BooleanProperty) prop).setValue(open);
        }
    }
    
    public boolean isSwitchable() {
        return (getFlags() & SWITCHABLE) != 0;
    }

    public boolean isOn() {
        return this.properties.lookup("on").isTrue();
    }
    
    public void setOn(boolean on) {
        ((BooleanProperty) this.properties.lookup("on")).setValue(on);
    }
    
    public boolean hasStateFlags() {
        return (getFlags() & HAS_STATE_FLAGS) != 0;
    }
    
    public VerbResult moveItem() {
        if (this.isMoveable()) {
            return new VerbResult(1, this);
        } else {
            return new VerbResult(Outcome.NOT_APPLICABLE, this);
        }
    }
    
    public ActionResult openItemWith(Item opener) {
        Arena arena = Arena.getInstance();
        if (!this.isOpenable()) {
            return new ActionResult(ActionResult.Outcome.NOT_APPLICABLE);
        } else if (this.isOpen()) {
            return new ActionResult(ActionResult.Outcome.ALREADY_DONE);
        } else if (this.isLockable() && this.isLocked()) {
            return new ActionResult(ActionResult.Outcome.NOT_POSSIBLE, 
                    "it is locked");
        } else if (opener == null
                && arena.getAssociations().isRelated(
                        "HAS_OPENER", this, null)) {
            return new ActionResult(ActionResult.Outcome.NOT_POSSIBLE,
                    "you need a tool to open it");
        } else if (opener != null 
                && !arena.getAssociations().isRelated(
                        "HAS_OPENER", this, opener)) {
            return new ActionResult(ActionResult.Outcome.NOT_POSSIBLE,
                    "you cannot open it with " + Grammar.indefiniteName(opener));
        } else {
            this.setOpen(true);
            return new ActionResult(ActionResult.Outcome.SUCCESS, 1);
        }
    }
    
    public ActionResult closeItem() {
        if (!this.isOpenable()) {
            return new ActionResult(ActionResult.Outcome.NOT_APPLICABLE);
        } else if (!this.isOpen()) {
            return new ActionResult(ActionResult.Outcome.ALREADY_DONE);
        } else {
            this.setOpen(false);
            return new ActionResult(ActionResult.Outcome.SUCCESS, 1);
        }
    }

    public ActionResult lockItem(Entity key) {
        Arena arena = Arena.getInstance();
        if (!this.isLockable()) {
            return new ActionResult(ActionResult.Outcome.NOT_APPLICABLE);
        } else if (this.isLocked()) {
            return new ActionResult(ActionResult.Outcome.ALREADY_DONE);
        } else if (!arena.getAssociations().isRelated("HAS_KEY", this, key)) {
            return new ActionResult(ActionResult.Outcome.NOT_POSSIBLE,
                    "the key does not fit its lock");
        } else if (this.isOpenable() && this.isOpen()) {
            return new ActionResult(ActionResult.Outcome.NOT_POSSIBLE, "it is open");
        } else {
            this.setLocked(true);
            return new ActionResult(ActionResult.Outcome.SUCCESS, 1);
        }
    }

    public ActionResult unlockItem(Entity key) {
        Arena arena = Arena.getInstance();
        if (!this.isLockable()) {
            return new ActionResult(ActionResult.Outcome.NOT_APPLICABLE);
        } else if (!this.isLocked()) {
            return new ActionResult(ActionResult.Outcome.ALREADY_DONE);
        } else if (!arena.getAssociations().isRelated("HAS_KEY", this, key)) {
            return new ActionResult(ActionResult.Outcome.NOT_POSSIBLE, 
                    "the key does not fit its lock");
        } else {
            this.setLocked(false);
            return new ActionResult(ActionResult.Outcome.SUCCESS, 1);
        }
    }
    
    public ActionResult switchItem(boolean on) {
        if (!this.isSwitchable()) {
            return new ActionResult(ActionResult.Outcome.NOT_APPLICABLE);
        } 
        if (this.isOn() == on) {
            return new ActionResult(ActionResult.Outcome.ALREADY_DONE,
                    on ? "switched on" : "switched off");
        } else {
            this.setOn(on);
            return new ActionResult(ActionResult.Outcome.SUCCESS, 1);
        }
    }
    
    protected boolean doTrigger(Triggered action, Entity other) {
        Verb verb = action.getVerb();
        Item item = lookupItem(action.getTarget());
        if (item == null) {
            item = this;
        }
        switch (verb) {
        case HIDE:
            item.setHidden(true);
            break;
        case REVEAL:
            item.setHidden(false);
            break;
        case LOCK:
            if (item.isLockable()) {
                item.setLocked(true);
            }
            break;
        case UNLOCK:
            if (item.isLockable()) {
                item.setLocked(false);
            }
            break;
        case OPEN:
            if (item.isOpenable()) {
                item.setOpen(true);
            }
            break;
        case CLOSE:
            if (item.isOpenable()) {
                item.setOpen(false);
            }
            break;
        case DROP:
            for (Actor actor: Arena.getInstance().getAllActors()) {
                if (actor instanceof Person) {
                    Person human = (Person) actor;
                    if (human.getItemsInHand().contains(item)) {
                        human.dropItem(item);
                        break;
                    }
                }
            }
            break;
        case DESTROY:
            item.destroy();
            break;
        case SET_FLAG:
            Long flag = ITEM_FLAGS.get(action.getArg()).flag();
            if (flag != null) {
                item.setFlag(flag.longValue(), true);
            }
            break;
        case CLEAR_FLAG:
            flag = ITEM_FLAGS.get(action.getArg()).flag();
            if (flag != null) {
                item.setFlag(flag.longValue(), false);
            }
            break;
        default:
            return super.doTrigger(action, other);
        }
        return true;
    }

    public static Item lookupItem(String label) {
        if (label == null) {
            return null;
        }
        Entity entity = Entity.lookupEntity(label);
        if (entity instanceof Item) {
            return (Item) entity;
        } else {
            throw new EntityException("Entity with label '" + label + "' is not an item");
        }
    }

    public ActionResult clearWith(Item tool) {
        if (properties.lookup("blocked") == null) {
            return new ActionResult(ActionResult.Outcome.NOT_APPLICABLE);
        } else if (!isBlocked()) {
            return new ActionResult(ActionResult.Outcome.ALREADY_DONE);
        } else {
            setBlocked(false);
            return new ActionResult(ActionResult.Outcome.SUCCESS);
        }
    }
}
