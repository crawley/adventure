package adventure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adventure.actions.Trigger;
import adventure.actions.VerbResult;
import adventure.actions.VerbResult.Outcome;

public class Item extends Entity {
    private final List<Trigger> triggers;
    private long flags;
    private Container container;
    
    public static final long TAKEABLE = 0x1L;
    public static final long MOVEABLE = 0x2L;
    public static final long EDIBLE = 0x4L;
    public static final long WEARABLE = 0x8L;
    public static final long WIELDABLE = 0x10L;
    public static final long HIDDEN = 0x20L;
    public static final long OPENABLE = 0x40L;
    public static final long OPEN = 0x80L;
    public static final long LOCKABLE = 0x100L;
    public static final long LOCKED = 0x200L;
    
    public static final long HAS_STATE_FLAGS = LOCKABLE | OPENABLE;
    
    @SuppressWarnings("serial")
    public static final Map<String, Long> ITEM_FLAGS = new HashMap<String, Long>(){
        {
            put("TAKEABLE", TAKEABLE);
            put("MOVEABLE", MOVEABLE);
            put("WEARABLE", WEARABLE);
            put("WIELDABLE", WIELDABLE);
            put("EDIBLE", EDIBLE);
            put("OPENABLE", OPENABLE);
            put("OPEN", OPEN);
            put("LOCKABLE", LOCKABLE);
            put("LOCKED", LOCKED);
            put("HIDDEN", HIDDEN);
        }
    };
    
    public Item(String label, String name, String abbrev, 
            String description, long flags, List<Trigger> triggers) {
        super(label, name, abbrev, description);
        this.flags = flags;
        this.triggers = triggers;
    }

    public List<Trigger> getTriggers() {
        return triggers;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        if (this.container != null && container != null) {
            throw new StateInconsistencyException(
                    getLabel() + " is already in a container ('"
                    + this.container.getLabel() + "')");
        }
        this.container = container;
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
        return (flags & HIDDEN) != 0;
    }
    
    public void setHidden(boolean hidden) {
        setFlag(HIDDEN, hidden);
    }
    
    public boolean isLockable() {
        return (flags & LOCKABLE) != 0;
    }
    
    public boolean isLocked() {
        return (flags & LOCKED) != 0;
    }
    
    public void setLocked(boolean locked) {
        setFlag(LOCKED, locked);
    }
    
    public boolean isOpenable() {
        return (flags & OPENABLE) != 0;
    }

    public boolean isOpen() {
        return (flags & OPEN) != 0;
    }
    
    public void setOpen(boolean open) {
        setFlag(OPEN, open);
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
            return new VerbResult(Outcome.SUCCESS, this);
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
            return new VerbResult(Outcome.SUCCESS, this);
        }
    }
    
    public VerbResult closeItem() {
        if (!this.isOpenable()) {
            return new VerbResult(Outcome.NOT_APPLICABLE, this);
        } else if (!this.isOpen()) {
            return new VerbResult(Outcome.ALREADY_DONE, this);
        } else {
            this.setOpen(false);
            return new VerbResult(Outcome.SUCCESS, this);
        }
    }

    public VerbResult lockItem(Item key) {
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
            return new VerbResult(Outcome.SUCCESS, this);
        }
    }

    public VerbResult unlockItem(Item key) {
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
            return new VerbResult(Outcome.SUCCESS, this);
        }
    }
    
    public void trigger(Trigger.Event event) {
        for (Trigger trigger: triggers) {
            if (trigger.getEvents().contains(event)) {
                if (trigger.isTriggered()) {
                    continue;
                }
                trigger.setTriggered(true);
                if (trigger.getMessage() != null) {
                    System.out.println(trigger.getMessage());
                }
                if (trigger.getVerb() != null) {
                    Item item = lookupItem(trigger.getTarget());
                    if (item != null) {
                        switch (trigger.getVerb()) {
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
                        }
                    }
                }
            }
        }
    }

    public static Item lookupItem(String label) {
        Entity entity = Entity.lookup(label);
        if (entity == null) {
            throw new EntityException("No entity with label '" + label + "'");
        } else if (entity instanceof Item) {
            return (Item) entity;
        } else {
            throw new EntityException("Entity with label '" + label + "' is not an item");
        }
    }
}
