package adventure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adventure.actions.Grammar;
import adventure.actions.Trigger;
import adventure.actions.Triggered;
import adventure.actions.Triggered.Verb;
import adventure.actions.VerbResult;
import adventure.actions.VerbResult.Outcome;

public class Item extends Entity {
    private long flags;
    
    public static final long TAKEABLE = 0x1L;
    public static final long EDIBLE = 0x4L;
    public static final long WEARABLE = 0x8L;
    public static final long WIELDABLE = 0x10L;
    public static final long OPENABLE = 0x20L;
    public static final long LOCKABLE = 0x40L;
    
    public static final long HAS_STATE_FLAGS = LOCKABLE | OPENABLE;
    
    public record FlagDescriptor (
            long flag, 
            String property,
            boolean initial) {
        public FlagDescriptor(long flag) {
            this(flag, null, false);
        }
    }
    
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
        }
    };
    
    public Item(String label, String name, String abbrev, 
            String description, long flags, Template base,
            List<Trigger> triggers, PropertySet properties) {
        super(label, name, abbrev, description, base, triggers, properties);
        this.flags = flags;
        
        // Add default and flag properties
        for (FlagDescriptor desc: ITEM_FLAGS.values()) {
            if (desc.property != null && ((flags & desc.flag) != 0)) {
                if (this.properties.lookup(desc.property) == null) {
                    BooleanProperty prop = new BooleanProperty(
                            desc.property, desc.initial);
                    this.properties.add(prop);
                }
            }
        }
        if (this.properties.lookup("hidden") == null) {
            this.properties.add(new BooleanProperty("hidden", false));
        }
    }

    public long getFlags() {
        return flags;
    }

    public boolean isTakeable() {
        return (flags & TAKEABLE) != 0;
    }

    public boolean isMoveable() {
        return (flags & MOVEABLE) != 0;
    }
    
    public boolean isEdible() {
        return (flags & EDIBLE) != 0;
    }

    public boolean isWearable() {
        return (flags & WEARABLE) != 0;
    }
    
    public boolean isHidden() {
        return this.properties.lookup("hidden").isTrue();
    }
    
    public void setHidden(boolean hidden) {
        ((BooleanProperty) this.properties.lookup("hidden")).setValue(hidden);
    }
    
    public boolean isLockable() {
        return (flags & LOCKABLE) != 0;
    }
    
    public boolean isLocked() {
        return this.properties.lookup("locked").isTrue();
    }
    
    public void setLocked(boolean locked) {
        ((BooleanProperty) this.properties.lookup("locked")).setValue(locked);
    }
    
    public boolean isOpenable() {
        return (flags & OPENABLE) != 0;
    }

    public boolean isOpen() {
        return this.properties.lookup("open").isTrue();
    }
    
    public void setOpen(boolean open) {
        ((BooleanProperty) this.properties.lookup("open")).setValue(open);
    }
    
    private void setFlag(long flag, boolean yes) {
        if (yes) {
            flags |= flag;
        } else {
            flags &= ~flag;
        }
    }

    public boolean hasStateFlags() {
        return (flags & HAS_STATE_FLAGS) != 0;
    }
    
    public VerbResult moveItem() {
        if (this.isMoveable()) {
            return new VerbResult(1, this);
        } else {
            return new VerbResult(Outcome.NOT_APPLICABLE, this);
        }
    }

    public VerbResult openItem() {
        if (!this.isOpenable()) {
            return new VerbResult(Outcome.NOT_APPLICABLE, this);
        } else if (this.isOpen()) {
            return new VerbResult(Outcome.ALREADY_DONE, this);
        } else if (this.isLockable() && this.isLocked()) {
            return new VerbResult(Outcome.NOT_POSSIBLE, this, "it is locked");
        } else {
            this.setOpen(true);
            return new VerbResult(1, this);
        }
    }
    
    public VerbResult openItemWith(Item opener) {
        Arena arena = Arena.getInstance();
        if (!this.isOpenable()) {
            return new VerbResult(Outcome.NOT_APPLICABLE, this);
        } else if (this.isOpen()) {
            return new VerbResult(Outcome.ALREADY_DONE, this);
        } else if (!arena.getAssociations().isRelated("HAS_OPENER", this, opener)) {
            return new VerbResult(Outcome.NOT_POSSIBLE, this,
                    Grammar.definiteName(opener) + " cannot open a "
                    + Grammar.indefiniteName(this));
        } else {
            this.setOpen(true);
            return new VerbResult(1, this);
        }
    }
    
    public VerbResult closeItem() {
        if (!this.isOpenable()) {
            return new VerbResult(Outcome.NOT_APPLICABLE, this);
        } else if (!this.isOpen()) {
            return new VerbResult(Outcome.ALREADY_DONE, this);
        } else {
            this.setOpen(false);
            return new VerbResult(1, this);
        }
    }

    public VerbResult lockItem(Entity key) {
        Arena arena = Arena.getInstance();
        if (!this.isLockable()) {
            return new VerbResult(Outcome.NOT_APPLICABLE, this);
        } else if (this.isLocked()) {
            return new VerbResult(Outcome.ALREADY_DONE, this);
        } else if (!arena.getAssociations().isRelated("HAS_KEY", this, key)) {
            return new VerbResult(Outcome.NOT_POSSIBLE, this,
                    "the key does not fit its lock");
        } else if (this.isOpenable() && this.isOpen()) {
            return new VerbResult(Outcome.NOT_POSSIBLE, this, "it is open");
        } else {
            this.setLocked(true);
            return new VerbResult(1, this);
        }
    }

    public VerbResult unlockItem(Entity key) {
        Arena arena = Arena.getInstance();
        if (!this.isLockable()) {
            return new VerbResult(Outcome.NOT_APPLICABLE, this);
        } else if (!this.isLocked()) {
            return new VerbResult(Outcome.ALREADY_DONE, this);
        } else if (!arena.getAssociations().isRelated("HAS_KEY", this, key)) {
            return new VerbResult(Outcome.NOT_POSSIBLE, this,
                    "the key does not fit its lock");
        } else {
            this.setLocked(false);
            return new VerbResult(1, this);
        }
    }
    
    protected boolean doTrigger(Triggered action, Entity target) {
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
        case CONSUME:
            this.trigger(Trigger.Event.CONSUMED);
            break;
        case DESTROY:
            item.destroy();
            break;
        case SET_FLAG:
            Long flag = ITEM_FLAGS.get(action.getArg()).flag;
            if (flag != null) {
                item.setFlag(flag.longValue(), true);
            }
            break;
        case CLEAR_FLAG:
            flag = ITEM_FLAGS.get(action.getArg()).flag;
            if (flag != null) {
                item.setFlag(flag.longValue(), false);
            }
            break;
        default:
            return super.doTrigger(action, target);
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
}
