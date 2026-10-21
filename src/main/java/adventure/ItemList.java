package adventure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An ItemList is a custom set-like list of items with "fail fast" semantics.
 * It throws an exception if you attempt to add an item that is already present 
 * or remove an item that isn't present.  Some List operations are not 
 * supported (yet).
 * 
 * Each ItemList has a name for diagnostic purposes.
 */
public class ItemList extends ArrayList<Item> implements List<Item> {
    
    private static final long serialVersionUID = 1691058264794808856L;
    private final String name;
    
    public ItemList(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    @Override
    public boolean add(Item item) {
        if (super.contains(item)) {
            throw new StateInconsistencyException(
                    "Item list '" + name + "' already contains item " + item.getLabel());
        }
        return super.add(item);
    }

    @Override
    public void add(int pos, Item item) {
        if (super.contains(item)) {
            throw new StateInconsistencyException(
                    "Item list '" + name + "' already contains item " + item.getLabel());
        }
        super.add(pos, item);
    }

    @Override
    public boolean addAll(Collection<? extends Item> items) {
        for (Item item: items) {
            add(item);
        }
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends Item> items) {
        for (Item item: items) {
            add(index++, item);
        }
        return true;
    }

        @Override
    public boolean remove(Object obj) {
        Item item = (Item) obj;
        if (!super.remove(item)) {
            throw new StateInconsistencyException(
                    "Item list '" + name + "' didn't contains item " + item.getLabel());
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> items) {
        for (Object obj: items) {
            remove(obj);
        }
        return true;

    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("ItemList::retainAll");
    }

    @Override
    public Item set(int arg0, Item arg1) {
        throw new UnsupportedOperationException("ItemList::set");
    }

    @Override
    public List<Item> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("ItemList::sublist");
    }
}
